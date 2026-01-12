package net.springprojectbackend.springboot.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		
		if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
			
			filterChain.doFilter(request, response);
			return;
		}
		
		String idToken = authHeader.substring(7);
		
		try {
            // 2. Verify token via Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String uid = decodedToken.getUid();

			/*
			 * // "role": "PARENT" or "CHILD") Object roleClaim =
			 * decodedToken.getClaims().get("role"); String role = (roleClaim != null) ?
			 * roleClaim.toString() : "CHILD"; // if not exists, default CHILD
			 */
            List<GrantedAuthority> authorities = new ArrayList<>();
            // Spring Security uses "ROLE_" prefix internally
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

            // 3. Build Authentication object
            Authentication auth = new UsernamePasswordAuthenticationToken(uid, null, authorities);
            
            // 4. Store in security context
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            // Token invalid or verification failed → clear context, optional 401
        	 e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        // 5. Continue filter chain
        filterChain.doFilter(request, response);
		
	}

}

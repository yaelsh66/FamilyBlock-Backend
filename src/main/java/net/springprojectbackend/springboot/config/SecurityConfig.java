package net.springprojectbackend.springboot.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import net.springprojectbackend.springboot.security.FirebaseAuthFilter;
import org.springframework.http.HttpMethod;

@Component
public class SecurityConfig {

	private final FirebaseAuthFilter firebaseAuthFilter;
	private final List<String> allowedOrigins;
	
	//Constructor injection - Spring finds and passes it. Ok it is final.
	public SecurityConfig(
			FirebaseAuthFilter firebaseAuthFilter,
			@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.firebaseAuthFilter = firebaseAuthFilter;
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
        		.map(String::trim)
        		.filter(origin -> !origin.isEmpty())
        		.toList();
    }
	
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )
	        .authorizeHttpRequests(auth -> auth
	            // ✅ allow preflight
	            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
	            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
	            // public
	            .requestMatchers("/public/**").permitAll()
	            .requestMatchers("/agent/**").permitAll()

	            // protected
	            .requestMatchers("/api/**").authenticated()

	            .anyRequest().permitAll()
	        )
	        .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);
        
        

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
	
}

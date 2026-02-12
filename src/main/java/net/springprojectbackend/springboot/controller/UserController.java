package net.springprojectbackend.springboot.controller;

import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.transaction.Transactional;
import net.springprojectbackend.springboot.model.AppUser;
import net.springprojectbackend.springboot.model.Family;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.FamilyRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;
import net.springprojectbackend.springboot.repository.UserRepository;


@RestController
@RequestMapping("/api/users")
public class UserController {

	private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final TimeBalanceRepository timeBalanceRepository;
    
    public UserController(UserRepository userRepository, FamilyMemberRepository familyMemberRepository, FamilyRepository familyRepository, TimeBalanceRepository timeBalanceRepository) {
        
		this.familyRepository = familyRepository;
		this.userRepository = userRepository;
		this.familyMemberRepository = familyMemberRepository;
		this.timeBalanceRepository = timeBalanceRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String authHeader,
    		@RequestBody Map<String, Object> body) throws FirebaseAuthException {
    	
    	System.out.println("Authorization header: " + authHeader);

    	String idToken = authHeader.substring(7);
    	System.out.println("Extracted token: " + idToken);

        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

        String firebaseUid = decoded.getUid();
        String email = decoded.getEmail();
        
        
        
        String familyName = (String) body.get("familyName");
        Family family = familyRepository.findByFamily(familyName);
        if (family == null) {
            family = new Family();
            family.setFamily(familyName);
           
        }
        familyRepository.save(family);
        
        FamilyMember familyMember = new FamilyMember();
        AppUser user = new AppUser();
        
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);
        user.setNickname((String) body.get("nickname"));
        user.setFirstName((String) body.get("name"));
        userRepository.save(user);

        familyMember.setFamily(family);
        familyMember.setUser(user);
        familyMember.setFirebaseUid(firebaseUid);
        String roleStr = (String) body.get("role");
        UserRole role = UserRole.valueOf(roleStr.toUpperCase());

        familyMember.setRole(role);
        
        familyMemberRepository.save(familyMember);
        
        TimeBalance timeB = timeBalanceRepository.findByChild(familyMember);
        if (timeB == null) {
        	timeB = new TimeBalance();
        	timeB.setChild(familyMember);
        }
        
        
        timeB.setPendingTimeInMinutes(0);
        timeB.setTotalTimeInMinutes(0);
        timeBalanceRepository.save(timeB);
        
        return ResponseEntity.ok(user);
    }
}

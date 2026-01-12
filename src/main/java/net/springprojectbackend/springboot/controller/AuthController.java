package net.springprojectbackend.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.security.core.Authentication;

import net.springprojectbackend.springboot.dto.UserDto.UpdateMe;
import net.springprojectbackend.springboot.dto.UserDto.UpdateTimeRequest;
import net.springprojectbackend.springboot.dto.UserDto.UserLoginResponse;
import net.springprojectbackend.springboot.model.AppUser;
import net.springprojectbackend.springboot.model.Family;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.FamilyRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;
import net.springprojectbackend.springboot.repository.UserRepository;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final FamilyRepository familyRepository;
	private final UserRepository userRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final TimeBalanceRepository timeBalanceRepository;
	
	public AuthController(UserRepository userRepository, FamilyMemberRepository familyMemberRepository, 
			TimeBalanceRepository timeBalanceRepository, FamilyRepository familyRepository) {
		this.familyRepository = familyRepository;
		this.userRepository = userRepository;
		this.familyMemberRepository = familyMemberRepository;
		this.timeBalanceRepository = timeBalanceRepository;
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserLoginResponse> me(Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		
		AppUser user = userRepository.findByfirebaseUid(uid);
		FamilyMember familyMember = familyMemberRepository.findByUser(user);
		Family family = familyMember.getFamily();
		TimeBalance time = timeBalanceRepository.findByChild(familyMember);
		
		UserLoginResponse response = new UserLoginResponse(familyMember.getRole(), family.getId(),
				user.getBackgroundImage(), user.getBackgroundColor(), time.getTotalTimeInMinutes(),
				time.getPendingTimeInMinutes(), user.getNickname());
		
		System.out.println("responseBODY = " + response);
		return ResponseEntity.ok(response);
		
	}
	
	@PatchMapping("/update")
	public ResponseEntity<Void> updateMe(@RequestBody UpdateMe req, Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		
		AppUser user = userRepository.findByfirebaseUid(uid);
		
		System.out.println("requestBODY = " + req);
		if(req.backgroundColor() != null) {
			user.setBackgroundColor(req.backgroundColor());
		}
		if(req.backgroundImage() != null) {
			user.setBackgroundImage(req.backgroundImage());
		}
		if(req.nickname() != null) {
			user.setNickname(req.nickname());
		}
		
		userRepository.save(user);
		
		return ResponseEntity.noContent().build();

	}
	
	@PatchMapping("update_time")
	public ResponseEntity<Void> updateTime(@RequestBody UpdateTimeRequest req, Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TimeBalance time = timeBalanceRepository.findByChild(familyMember);
		if(req.totalTime() != null) {
			time.setTotalTimeInMinutes(req.totalTime());
		}
		if(req.pendingTime() != null) {
			time.setPendingTimeInMinutes(req.pendingTime());
		}
		timeBalanceRepository.save(time);
		return ResponseEntity.noContent().build();
		
	}
}

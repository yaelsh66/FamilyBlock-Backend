package net.springprojectbackend.springboot.dto;


import net.springprojectbackend.springboot.model.FamilyMember.UserRole;

public class UserDto {
	
	public record UserLoginResponse(UserRole role, Long familyId, String backgroundImage, 
			String backgroundColor, Integer totalTime, Integer pendingTime, String nickname) {}
	
	public record UpdateMe(String backgroundImage, 
			String backgroundColor, String nickname) {}
	
	public record UpdateTimeRequest(Integer totalTime, Integer pendingTime) {}
}

package net.springprojectbackend.springboot.dto;

import java.time.LocalDateTime;

import java.util.List;


import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import net.springprojectbackend.springboot.model.TaskInstance.TaskStatus;

public class TaskDto {

	public record AddNewTaskRequest(String title, String description, Integer screenTime, String famliyName) {}
	
	public record TaskRequest(Long id, String title, String description, Integer screenTime) {}
	
	public record TaskResponse(Long id, String title, String description, Integer time) {}
	
	public record DeleteTaskRequest(Long id) {}
	
	public record TaskInstanceResponse(Long id, String title, String description, Integer time, LocalDateTime createdAt, 
			LocalDateTime submittedAt, LocalDateTime approvedAt, String childComment, String parentComment, TaskStatus Status) {}
	
	public record TaskListResponse(List<TaskResponse> familyTasksList) {}
	
	public record TaskInstanceListResponse(List<TaskInstanceResponse> childTaskList) {}
	
	public record UpdateTaskAssignmentRequest(Long taskId, String newAssignedTo) {}
	
	public record UpdateTaskAssignmentResponse(Long taskId, String newAssignedTo) {}
	
	public record SubmitCompletionRequest(Long tastInstanceId, String comment) {}
	
	public record GetKidsRequest(Long familyId) {}
	
	public record GetKidResponse(Long id, String uid, String email, String nickname, UserRole role, 
			String familyId, Integer totalTime, Integer pendingTime) {}
	
	public record FamilyPendingCompletionRequest(String familyId) {}
	
	public record WithdrawTimeRequest(String childId, Integer minutes) {}
	
	public record WithdrawTimeResponse(Boolean ok, String response) {}
	
	public record TaskHistoryRequest(Long childId) {}
	
	public record TaskHistoryResponse(Long id, String title, String description, Integer minutesReward, LocalDateTime createdAt, 
			LocalDateTime submittedAt, LocalDateTime approvedAt, String childComment, String parentComment, TaskStatus Status) {}
}

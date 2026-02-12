package net.springprojectbackend.springboot.dto;



public class TaskCompletionDto {

	public record ApproveCompletionRequest(Long completionId, String childId, Integer time, String parentComment) {}
	
	public record RejectCompletionRequest(Long completionId, String childId, Integer time, String parentComment) {}
	
}

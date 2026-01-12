package net.springprojectbackend.springboot.dto;

import java.util.List;

public class TaskDto {

	public record AddNewTaskRequest(String title, String description, Integer screenTime, String famliyName) {}
	
	public record TaskResponse(Long id, String title, String description, Integer time) {}
	
	public record TaskListResponse(List<TaskResponse> familyTasksList) {}
}

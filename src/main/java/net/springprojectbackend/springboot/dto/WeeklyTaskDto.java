package net.springprojectbackend.springboot.dto;

import java.time.LocalTime;
import java.util.List;

public class WeeklyTaskDto {

	public record GetWeeklyScheduleResponse(Long id, Long taskTemplateId, Long familyMemberId, String title, 
			String description, Integer minutesReward, List<String> days, LocalTime toDoAt, 
			Boolean wasCompleteToday) {}
	
	public record AssignTaskWeeklyRequest(Long taskId, Long childId, List<String> days, LocalTime localTime) {}
	
	public record UnassignTaskWeeklySlotRequest(Long taskId, Long childId, String day, LocalTime localTime) {}
	
	public record UpdateWeeklyTaskRequest(Long id, List<String> days, LocalTime toDoAt) {}
	
	public record ComtleteWeeklyTaskRequest(Long id, String comment) {}
	
}

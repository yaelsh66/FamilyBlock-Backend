package net.springprojectbackend.springboot.dto;

import java.time.LocalTime;
import java.util.List;

public class TimeDto {
	
	public record AddTimeToChildRequest(Long childId, Integer time) {}
	
	public record AddTimeToChildResponse(Integer total) {}
	
	public record UpdateDailyTimeRequest(Integer dailyTimeMinutes, List<String> days) {}
	
	public record UpdateScheduleTimeRequest(Long childId, String name, List<String> days, LocalTime start, LocalTime end) {}

	public record GetDailyTime(Long id, List<String> days, Integer time) {}
	
	public record GetDailyTimesResponse(List<GetDailyTime> dailyTimeList) {}
	
	public record GetScheduleTime(Long id, String name, List<String> days, LocalTime start, LocalTime end) {}
	
	public record GetScheduleTimesResponse(List<GetScheduleTime> scheduleTimeList) {}
}

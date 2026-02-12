package net.springprojectbackend.springboot.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import net.springprojectbackend.springboot.model.WeeklyTask;
import net.springprojectbackend.springboot.repository.WeeklyTaskRepository;

public class WeeklyTaskCompleteTodayScheduler {

	private final WeeklyTaskRepository weeklyTaskRepository;
	
	public WeeklyTaskCompleteTodayScheduler(WeeklyTaskRepository weeklyTaskRepository) {
		
		this.weeklyTaskRepository = weeklyTaskRepository;
	}
	
	@Scheduled(cron = "0 30 03 * * *")
	public void addDailyTime() {
		
		LocalDate date = LocalDate.now();
		String today = date.getDayOfWeek().name();
		List<WeeklyTask> weeklyTaskList = weeklyTaskRepository.findAll();
		for(WeeklyTask weeklyTask : weeklyTaskList) {
			if(!weeklyTask.getToday().equals(today)) {
				weeklyTask.setWasCompleateToday(false);
				weeklyTaskRepository.save(weeklyTask);
			}
		}
		
	}
}

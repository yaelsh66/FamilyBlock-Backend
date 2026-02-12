package net.springprojectbackend.springboot.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.springprojectbackend.springboot.model.DailyTimeRule;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.repository.DailyTimeRuleRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;

@Component
public class DailyTimeScheduler {

	private final TimeBalanceRepository timeBalanceRepository;
	private final DailyTimeRuleRepository dailyTimeRuleRepository;
	
	public DailyTimeScheduler(TimeBalanceRepository timeBalanceRepository, 
			DailyTimeRuleRepository dailyTimeRuleRepository) {
		this.dailyTimeRuleRepository = dailyTimeRuleRepository;
		this.timeBalanceRepository = timeBalanceRepository;
	}
	
	@Scheduled(cron = "0 30 03 * * *")
	public void addDailyTime() {
		
		LocalDate date = LocalDate.now();
		String today = date.getDayOfWeek().name();
		List<DailyTimeRule> dailyTimeRuleList = dailyTimeRuleRepository.findAll();
		if(dailyTimeRuleList == null || dailyTimeRuleList.isEmpty()) {
			return;
		}
		for(DailyTimeRule rule: dailyTimeRuleList) {
			if(rule.getDays().contains(today)) {
				TimeBalance timeBalance = timeBalanceRepository.findByChild(rule.getFamilyMember());
				if(timeBalance.getDailyTimeLastUpdate() != null && !timeBalance.getDailyTimeLastUpdate().isBefore(LocalDate.now())) {
					return;
				}else {
					timeBalance.setDailyTimeInMinutes(rule.getTimeInMinutes());
					
					timeBalance.setDailyTimeLastUpdate(LocalDate.now());
					timeBalanceRepository.save(timeBalance);
				}
			}
		}
		
		
	}
}

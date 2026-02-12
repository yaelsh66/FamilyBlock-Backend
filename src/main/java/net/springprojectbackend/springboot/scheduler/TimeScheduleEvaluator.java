package net.springprojectbackend.springboot.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.model.TimeScheduleRule;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;
import net.springprojectbackend.springboot.repository.TimeScheduleRuleRepository;

@Component
public class TimeScheduleEvaluator {

	private final TimeScheduleRuleRepository timeScheduleRuleRepository;
	private final TimeBalanceRepository timeBalanceRepository;
	
	public TimeScheduleEvaluator(TimeScheduleRuleRepository timeScheduleRuleRepository, TimeBalanceRepository timeBalanceRepository) {
		this.timeScheduleRuleRepository = timeScheduleRuleRepository;
		this.timeBalanceRepository = timeBalanceRepository;
		
	}
	
	@Scheduled(fixedRate = 60_000)
	public void evaluate() {
		
		LocalDateTime now = LocalDateTime.now();
		LocalTime nowTime = LocalTime.now();
		DayOfWeek day = now.getDayOfWeek();
		Boolean isBlocked = false;
		List<TimeScheduleRule> ruleList = timeScheduleRuleRepository.findAllByEnabledTrue();
		if(ruleList == null || ruleList.isEmpty()) {
			return;
		}
		//TimeBalance timeBalance = timeBalanceRepository.findByChild(ruleList.getFirst().getChild());
		
		for(TimeScheduleRule rule : ruleList) {
			
			if(rule.getDay().contains(day.name())) {
				isBlocked = isBlocked(nowTime, rule.getStart(), rule.getEnd());
				
					TimeBalance timeBalance = timeBalanceRepository.findByChild(rule.getChild());
					System.out.println("TimeBalance timeBalance = timeBalanceRepository.findByChild(rule.getChild(): " + rule.getChild() + isBlocked);
					timeBalance.setScheduleBlock(isBlocked);
					timeBalanceRepository.save(timeBalance);
					continue;
				
			}
			
		}
		
		//timeBalance.setScheduleBlock(isBlocked);
		//timeBalanceRepository.save(timeBalance);
		return;
	}
	
	private Boolean isBlocked(LocalTime now, LocalTime start, LocalTime end) {
		
		if(start.isBefore(end)) {
			if(now.isAfter(start) && now.isBefore(end)) {
				return true;
			}else {
				return false;
			}
		}
		if(now.isBefore(end) || now.isAfter(start)) {
			return true;
		}
		return false;
		
	}
	
}

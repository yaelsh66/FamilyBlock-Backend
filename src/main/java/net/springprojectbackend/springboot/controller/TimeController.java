package net.springprojectbackend.springboot.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.dto.TimeDto.AddTimeToChildRequest;
import net.springprojectbackend.springboot.dto.TimeDto.AddTimeToChildResponse;
import net.springprojectbackend.springboot.dto.TimeDto.GetDailyTime;
import net.springprojectbackend.springboot.dto.TimeDto.GetDailyTimesResponse;
import net.springprojectbackend.springboot.dto.TimeDto.GetScheduleTime;
import net.springprojectbackend.springboot.dto.TimeDto.GetScheduleTimesResponse;
import net.springprojectbackend.springboot.dto.TimeDto.UpdateDailyTimeRequest;
import net.springprojectbackend.springboot.dto.TimeDto.UpdateScheduleTimeRequest;
import net.springprojectbackend.springboot.model.DailyTimeRule;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.model.TimeScheduleRule;
import net.springprojectbackend.springboot.repository.DailyTimeRuleRepository;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;
import net.springprojectbackend.springboot.repository.TimeScheduleRuleRepository;
import net.springprojectbackend.springboot.repository.TimeSessionRepository;
import net.springprojectbackend.springboot.repository.TimeTransactionRepository;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("api/time_control")
public class TimeController {

	
    private final FamilyMemberRepository familyMemberRepository;
	private final TimeBalanceRepository timeBalanceRepository;
	private final DailyTimeRuleRepository dailyTimeRuleRepository;
	private final TimeScheduleRuleRepository timeScheduleRuleRepository;
	private final TimeTransactionRepository timeTransactionRepository;
	private final TimeSessionRepository timeSessionRepository;
	
	public TimeController(TimeBalanceRepository timeBalanceRepository, DailyTimeRuleRepository dailyTimeRuleRepository, FamilyMemberRepository familyMemberRepository, TimeScheduleRuleRepository timeScheduleRuleRepository, TimeTransactionRepository timeTransactionRepository, TimeSessionRepository timeSessionRepository) {
		this.timeBalanceRepository = timeBalanceRepository;
		this.dailyTimeRuleRepository = dailyTimeRuleRepository;
		this.familyMemberRepository = familyMemberRepository;
		this.timeScheduleRuleRepository = timeScheduleRuleRepository;
		this.timeTransactionRepository = timeTransactionRepository;
		this.timeSessionRepository = timeSessionRepository;
	}
	
	
	@PatchMapping("add_time_to_child/{childId}")
	public ResponseEntity<AddTimeToChildResponse> addTimeToChild(@PathVariable Long childId, 
			@RequestBody AddTimeToChildRequest req, Authentication authentication){
		FamilyMember familyMember = familyMemberRepository.getReferenceById(childId);
		TimeBalance timeBalance = timeBalanceRepository.findByChild_Id(childId);
		if(timeBalance == null) {
			timeBalance = new TimeBalance();
			timeBalance.setChild(familyMember);
		}
		if(req.time() > 0) {
			timeBalance.setTotalTimeInMinutes(timeBalance.getTotalTimeInMinutes() + req.time());
		}else {
			if(timeBalance.getTotalTimeInMinutes() + timeBalance.getDailyTimeInMinutes() + req.time() >= 0) {
				if(timeBalance.getDailyTimeInMinutes() + req.time() >= 0) {
					timeBalance.setDailyTimeInMinutes(timeBalance.getDailyTimeInMinutes() + req.time());
				}else {
					Integer reduceTotal = timeBalance.getDailyTimeInMinutes() + req.time();
					timeBalance.setDailyTimeInMinutes(0);
					timeBalance.setTotalTimeInMinutes(timeBalance.getTotalTimeInMinutes() + reduceTotal);
				}
			}
		}
		
		if(timeBalance.getIsRunning()) {
			timeBalance.setWithdrawTime(timeBalance.getWithdrawTime() + req.time());
		}
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);	
			
		
		return ResponseEntity.ok(new AddTimeToChildResponse(timeBalance.getTotalTimeInMinutes() +
				timeBalance.getDailyTimeInMinutes()));
	}
	
	@PostMapping("update_daily_time/{childId}")
	public ResponseEntity<Void> updateDailyTime(@PathVariable Long childId, 
			@RequestBody UpdateDailyTimeRequest req, Authentication authentication){
		
		List<DailyTimeRule> dailyTimeRuleList = dailyTimeRuleRepository.findAllByChild_Id(childId);
				
		for(DailyTimeRule rule : dailyTimeRuleList) {
			
			List<String> newDays = rule.getDays();
			newDays.removeAll(req.days());
			if(newDays == null || newDays.isEmpty()) {
				dailyTimeRuleRepository.delete(rule);
			}else {
				
				rule.setDays(newDays);
				dailyTimeRuleRepository.save(rule);
			}
		}
		DailyTimeRule dailyTimeRule = new DailyTimeRule();
		dailyTimeRule.setDays(req.days());
		dailyTimeRule.setFamilyMember(familyMemberRepository.getReferenceById(childId));
		dailyTimeRule.setTimeInMinutes(req.dailyTimeMinutes());
		dailyTimeRuleRepository.save(dailyTimeRule);
		
		return ResponseEntity.ok(null);
		
	}
	
	@GetMapping("get_daily_times/{childId}")
	public ResponseEntity<GetDailyTimesResponse> getDailyTime(@PathVariable Long childId){
		List<DailyTimeRule> dailyTimeRules = dailyTimeRuleRepository.findAllByChild_Id(childId);
		List<GetDailyTime> getDailyTimeList = dailyTimeRules.stream().map(rule -> new GetDailyTime(rule.getId(), rule.getDays(), rule.getTimeInMinutes())).toList();
		return ResponseEntity.ok(new GetDailyTimesResponse(getDailyTimeList));
	}
	
	@GetMapping("get_schedule_times/{childId}")
	public ResponseEntity<GetScheduleTimesResponse> getScheduleTimes(@PathVariable Long childId, Authentication authentication){
		List<TimeScheduleRule> timeScheduleRuleList = timeScheduleRuleRepository.findAllByChild_id(childId);
		List<GetScheduleTime> getScheduleTimeList = timeScheduleRuleList.stream().map(rule -> new GetScheduleTime(rule.getId(), rule.getName(), rule.getDay(), rule.getStart(),
				 rule.getEnd())).toList();
		return ResponseEntity.ok(new GetScheduleTimesResponse(getScheduleTimeList));
		
		
	}
	
	@DeleteMapping("delete_schedule_time/{id}")
	public ResponseEntity<Void> deleteScheduleTime(@PathVariable Long id ){
		TimeScheduleRule timeScheduleRule = timeScheduleRuleRepository.getReferenceById(id);
		timeScheduleRuleRepository.delete(timeScheduleRule);
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("update_schedule_time/{childId}")
	public ResponseEntity<Void> updateScheduleTime(@PathVariable Long childId, @RequestBody UpdateScheduleTimeRequest req, Authentication authentication){
		
		TimeScheduleRule timeScheduleRule = new TimeScheduleRule();
		timeScheduleRule.setChild(familyMemberRepository.getReferenceById(childId));
		timeScheduleRule.setName(req.name());
		timeScheduleRule.setDay(req.days());
		timeScheduleRule.setStart(req.start());
		timeScheduleRule.setEnd(req.end());
		timeScheduleRuleRepository.save(timeScheduleRule);
		
		return ResponseEntity.ok(null);
		
	}
}

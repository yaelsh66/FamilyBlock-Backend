package net.springprojectbackend.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.DailyTimeRule;
import net.springprojectbackend.springboot.model.TimeScheduleRule;

public interface TimeScheduleRuleRepository extends JpaRepository<TimeScheduleRule, Long>{
	
	List<TimeScheduleRule> findAllByEnabledTrue();
	
	List<TimeScheduleRule> findAllByChild_id(Long childId);

}

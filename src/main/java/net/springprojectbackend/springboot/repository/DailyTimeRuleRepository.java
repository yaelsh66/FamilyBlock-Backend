package net.springprojectbackend.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.DailyTimeRule;

public interface DailyTimeRuleRepository extends JpaRepository<DailyTimeRule, Long>{

	List<DailyTimeRule> findAllByChild_Id(Long childId);
}

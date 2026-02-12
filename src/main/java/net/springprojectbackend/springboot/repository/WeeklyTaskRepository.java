package net.springprojectbackend.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.dto.WeeklyTaskDto.GetWeeklyScheduleResponse;
import net.springprojectbackend.springboot.model.WeeklyTask;

public interface WeeklyTaskRepository extends JpaRepository<WeeklyTask, Long> {
	
	List<GetWeeklyScheduleResponse> findAllByFamilyMember_Id(Long childId);

}

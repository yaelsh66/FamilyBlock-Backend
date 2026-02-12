package net.springprojectbackend.springboot.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.dto.WeeklyTaskDto.AssignTaskWeeklyRequest;
import net.springprojectbackend.springboot.dto.WeeklyTaskDto.ComtleteWeeklyTaskRequest;
import net.springprojectbackend.springboot.dto.WeeklyTaskDto.GetWeeklyScheduleResponse;
import net.springprojectbackend.springboot.dto.WeeklyTaskDto.UpdateWeeklyTaskRequest;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import net.springprojectbackend.springboot.model.TaskInstance.TaskStatus;
import net.springprojectbackend.springboot.model.TaskInstance;
import net.springprojectbackend.springboot.model.TaskTemplate;
import net.springprojectbackend.springboot.model.WeeklyTask;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.TaskInstanceRepository;
import net.springprojectbackend.springboot.repository.TaskTemplateRepository;
import net.springprojectbackend.springboot.repository.WeeklyTaskRepository;

@RestController
@RequestMapping("api/weekly")
public class WeeklyController {
	
	private final FamilyMemberRepository familyMemberRepository;
	private final TaskTemplateRepository taskTemplateRepository;
	private final TaskInstanceRepository taskInstanceRepository;
	private final WeeklyTaskRepository weeklyTaskRepository;
	
	public WeeklyController(FamilyMemberRepository familyMemberRepository, TaskInstanceRepository taskInstanceRepository, 
			WeeklyTaskRepository weeklyTaskRepository, TaskTemplateRepository taskTemplateRepository) {
		this.familyMemberRepository = familyMemberRepository;
		this.taskTemplateRepository = taskTemplateRepository;
		this.taskInstanceRepository = taskInstanceRepository;
		this.weeklyTaskRepository = weeklyTaskRepository;
	}
	
	@GetMapping("family_weekly_schedule")
	public ResponseEntity<List<GetWeeklyScheduleResponse>> getFamilyWeeklySchedule(
			@RequestParam(required = false) Long childId, Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		if(childId != null) {
			List<GetWeeklyScheduleResponse> res = weeklyTaskRepository.findAllByFamilyMember_Id(childId);
			return ResponseEntity.ok(res);
		}
		
		List<FamilyMember> familyKids = familyMemberRepository.findAllByFamilyIdAndRole(familyMember.getFamily().getId(), UserRole.CHILD);
		
		List<GetWeeklyScheduleResponse> res =
			    familyKids.stream()
			        .flatMap(child ->
			            weeklyTaskRepository.findAllByFamilyMember_Id(child.getId()).stream()
			        )
			        .toList();
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("child_weekly_schedule")
	public ResponseEntity<List<GetWeeklyScheduleResponse>> getChildWeeklySchedule(Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		List<GetWeeklyScheduleResponse> res = weeklyTaskRepository.findAllByFamilyMember_Id(familyMember.getId());
		return ResponseEntity.ok(res);
		
	}
	
	@PostMapping("assign_weekly")
	public ResponseEntity<Void> assignWeekly(@RequestBody AssignTaskWeeklyRequest req, Authentication authentication){
		
		TaskTemplate taskTemplate = taskTemplateRepository.getReferenceById(req.taskId());
		FamilyMember familyMember = familyMemberRepository.getReferenceById(req.childId());
		WeeklyTask task = new WeeklyTask();
		task.setFamilyMember(familyMember);
		task.setCreatedAt(LocalDateTime.now());
		task.setDays(req.days());
		task.setToDoAt(req.localTime());
		task.setDescription(taskTemplate.getDescription());
		task.setMinutesReward(taskTemplate.getMinutesReward());
		task.setTitle(taskTemplate.getTitle());
		task.setTaskTemplate(taskTemplate);
		task.setToday(LocalDateTime.now().getDayOfWeek().name());
		task.setWasCompleateToday(false);
		weeklyTaskRepository.save(task);
		return ResponseEntity.ok(null);
		
	}
	
	@PutMapping("update")
	public ResponseEntity<Void> updateWeeklyTask(@RequestParam(required = false) Long childId, @RequestBody UpdateWeeklyTaskRequest req, Authentication authentication){
		WeeklyTask weeklyTask = weeklyTaskRepository.getReferenceById(req.id());
		if(req.days() != null && !req.days().isEmpty()) {
			weeklyTask.setDays(req.days());
		}
		if(req.toDoAt() != null) {
			weeklyTask.setToDoAt(req.toDoAt());
		}
		weeklyTaskRepository.save(weeklyTask);
		return ResponseEntity.ok(null);
		
	}
	
	@DeleteMapping("clear_weekly_schedule/{taskId}")
	public ResponseEntity<Void> deleteWeeklyTask(@PathVariable Long taskId, Authentication authentication){
		weeklyTaskRepository.deleteById(taskId);
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("complete")
	public ResponseEntity<Void> completeWeeklyTask(@RequestParam(required = false) Long childId, @RequestBody ComtleteWeeklyTaskRequest req, Authentication authentication){
		FamilyMember familyMember;
		if(childId == null) {
			String uid = (String) authentication.getPrincipal();
			familyMember = familyMemberRepository.findByFirebaseUid(uid);
		}else {
			familyMember = familyMemberRepository.getReferenceById(childId);
		}
		WeeklyTask weeklyTask = weeklyTaskRepository.getReferenceById(req.id());
		TaskInstance taskInstance = new TaskInstance();
		taskInstance.setTaskTemplate(weeklyTask.getTaskTemplate());
		taskInstance.setFamilyMember(familyMember);
		taskInstance.setTitle(weeklyTask.getTitle());
		taskInstance.setDescription(weeklyTask.getDescription());
		taskInstance.setMinutesReward(weeklyTask.getMinutesReward());
		taskInstance.setStatus(TaskStatus.SUBMITTED);
		taskInstance.setChildComment(req.comment());
		taskInstance.setSubmittedAt(LocalDateTime.now());
		taskInstanceRepository.save(taskInstance);
		
		weeklyTask.setToday(LocalDate.now().getDayOfWeek().name());
		weeklyTask.setWasCompleateToday(true);
		System.out.println("^^^^^^^weeklyTask.setWasCompleateToday(true);: " + weeklyTask.getWasCompleateToday());
		weeklyTaskRepository.save(weeklyTask);
		return ResponseEntity.ok(null);
		
	}
	

}

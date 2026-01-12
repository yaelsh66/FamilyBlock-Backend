package net.springprojectbackend.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.dto.TaskDto.AddNewTaskRequest;

import net.springprojectbackend.springboot.dto.TaskDto.TaskListResponse;
import net.springprojectbackend.springboot.dto.TaskDto.TaskResponse;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TaskTemplate;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.TaskTemplateRepository;

@RestController
@RequestMapping("api/task")
public class TasksController {

	private final TaskTemplateRepository taskTemplateRepository;
	private final FamilyMemberRepository familyMemberRepository;
	
	public TasksController(TaskTemplateRepository ttr, FamilyMemberRepository familyMemberRepository) {
		this.taskTemplateRepository = ttr;
		this.familyMemberRepository = familyMemberRepository;
	}
	
	@PostMapping("/new_task")
	public ResponseEntity<Void> addNewTask(@RequestBody AddNewTaskRequest req, Authentication authentication){
		System.out.println("req: " + req);
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TaskTemplate task = new TaskTemplate();
		
		task.setOwner(familyMember);
		task.setFamily(familyMember.getFamily());
		task.setTitle(req.title());
		task.setDescription(req.description());
		task.setMinutesReward(req.screenTime());
		task.setActive(false);
		taskTemplateRepository.save(task);
		return ResponseEntity.noContent().build();
		
		
	}
	
	@GetMapping("/family_tasks")
	public ResponseEntity<TaskListResponse> getFamilyTasks(Authentication authentication){
		System.out.println("getFamilyTasks");
		String uid = (String) authentication.getPrincipal();
		System.out.println("getFamilyTasks - uid: " + uid);
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		List<TaskTemplate> familyTasksTemplate = taskTemplateRepository.findAllByFamily_id(familyMember.getFamily().getId());
		
		List<TaskResponse> familyTasksList = familyTasksTemplate.stream().map(task -> new TaskResponse(
				task.getId(),
				task.getTitle(), 
				task.getDescription(),
				task.getMinutesReward()
				)).toList();
		
		return ResponseEntity.ok(
		        new TaskListResponse(familyTasksList));
		
	}
	
	@GetMapping("/child_tasks")
	public ResponseEntity<TaskListResponse> getChildTasks(Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		List<TaskTemplate> childTasksTemplate = taskTemplateRepository.findAllByOwner_id(familyMember.getUser().getId());
		
		List<TaskResponse> childTasksList = childTasksTemplate.stream().map(task -> new TaskResponse(
				task.getId(),
				task.getTitle(), 
				task.getDescription(),
				task.getMinutesReward()
				)).toList();
		
		return ResponseEntity.ok(
		        new TaskListResponse(childTasksList));
		
	}
}

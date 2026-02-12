package net.springprojectbackend.springboot.controller;

import java.time.LocalDateTime;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.dto.TaskDto.AddNewTaskRequest;
import net.springprojectbackend.springboot.dto.TaskDto.FamilyPendingCompletionRequest;
import net.springprojectbackend.springboot.dto.FamilyPendingCompletionResponse;
import net.springprojectbackend.springboot.dto.TaskCompletionDto.ApproveCompletionRequest;
import net.springprojectbackend.springboot.dto.TaskCompletionDto.RejectCompletionRequest;
import net.springprojectbackend.springboot.dto.TaskDto.GetKidResponse;
import net.springprojectbackend.springboot.dto.TaskDto.GetKidsRequest;

import net.springprojectbackend.springboot.dto.TaskDto.SubmitCompletionRequest;
import net.springprojectbackend.springboot.dto.TaskDto.TaskHistoryRequest;
import net.springprojectbackend.springboot.dto.TaskDto.TaskHistoryResponse;
import net.springprojectbackend.springboot.dto.TaskDto.TaskListResponse;
import net.springprojectbackend.springboot.dto.TaskDto.TaskRequest;
import net.springprojectbackend.springboot.dto.TaskDto.TaskResponse;
import net.springprojectbackend.springboot.dto.TaskDto.UpdateTaskAssignmentResponse;
import net.springprojectbackend.springboot.dto.TaskDto.WithdrawTimeRequest;
import net.springprojectbackend.springboot.dto.TaskDto.WithdrawTimeResponse;
import net.springprojectbackend.springboot.dto.TaskDto.UpdateTaskAssignmentRequest;
import net.springprojectbackend.springboot.model.AppUser;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TaskInstance;
import net.springprojectbackend.springboot.model.TaskInstance.TaskStatus;
import net.springprojectbackend.springboot.model.TaskTemplate;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.model.FamilyMember.UserRole;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.TaskInstanceRepository;
import net.springprojectbackend.springboot.repository.TaskTemplateRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;
import net.springprojectbackend.springboot.repository.AppUserRepository;



@RestController
@RequestMapping("api/task")
public class TasksController {

	private final TaskTemplateRepository taskTemplateRepository;
	private final TaskInstanceRepository taskInstanceRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final TimeBalanceRepository timeBalanceRepository;
	private final AppUserRepository appUserRepository;
	
	public TasksController(TaskTemplateRepository ttr, FamilyMemberRepository familyMemberRepository, TaskInstanceRepository taskInstanceRepository,
			TimeBalanceRepository timeBalanceRepository, AppUserRepository appUserRepository) {
		this.taskTemplateRepository = ttr;
		this.taskInstanceRepository = taskInstanceRepository;
		this.familyMemberRepository = familyMemberRepository;
		this.timeBalanceRepository = timeBalanceRepository;
		this.appUserRepository = appUserRepository;
	}
	
	@PostMapping("/new_task")
	public ResponseEntity<Void> addNewTask(@RequestBody AddNewTaskRequest req, Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TaskTemplate task = new TaskTemplate();
		
		task.setUser(familyMember);
		task.setFamily(familyMember.getFamily());
		task.setTitle(req.title());
		task.setDescription(req.description());
		task.setMinutesReward(req.screenTime());
		task.setActive(true);
		taskTemplateRepository.save(task);
		return ResponseEntity.noContent().build();
		
		
	}
	
	@GetMapping("/family_tasks")
	public ResponseEntity<TaskListResponse> getFamilyTasks(Authentication authentication){
		
		String uid = (String) authentication.getPrincipal();
		
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		List<TaskTemplate> familyTasksTemplate = taskTemplateRepository.findAllByFamily_id(familyMember.getFamily().getId());
		List<TaskTemplate> familyActiveTasksTemplate = familyTasksTemplate.stream().filter(task -> task.getActive() == true).toList();
		List<TaskResponse> familyTasksList = familyActiveTasksTemplate.stream().map(task -> new TaskResponse(
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
		
		List<TaskInstance> childTasksInstance = taskInstanceRepository.findAllByFamilyMember_id(familyMember.getId());
		
		
		List<TaskResponse> childTasksList = childTasksInstance.stream()
				.filter(task -> task.getStatus().equals(TaskStatus.CREATED))
				.map(task -> new TaskResponse(
				task.getId(),
				task.getTitle(), 
				task.getDescription(),
				task.getMinutesReward()
				)).toList();
		
		return ResponseEntity.ok(new TaskListResponse(childTasksList));
		
	}
	
	@PostMapping("update_task_assignment")
	public ResponseEntity<?> updateTaskAssignment(@RequestBody UpdateTaskAssignmentRequest req, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		if(req.newAssignedTo() == null) {
			
			TaskInstance taskInstance = taskInstanceRepository.getReferenceById(req.taskId());
			if(taskInstance == null) {
				ResponseEntity.ok(null);
			}
			Long templateId = taskInstance.getTaskTemplate().getId();
			taskInstanceRepository.deleteById(req.taskId());
			
			return ResponseEntity.ok(new UpdateTaskAssignmentResponse(templateId, null));
		}
		Long templateId = req.taskId();
		TaskTemplate taskTemplate = taskTemplateRepository.getReferenceById(templateId);
		
		
		TaskInstance taskInstance = new TaskInstance();
		taskInstance.setTaskTemplate(taskTemplate);
		taskInstance.setFamilyMember(familyMember);
		taskInstance.setTitle(taskTemplate.getTitle());
		taskInstance.setDescription(taskTemplate.getDescription());
		taskInstance.setMinutesReward(taskTemplate.getMinutesReward());
		taskInstance.setStatus(TaskStatus.CREATED);
		taskInstance.setCreatedAt(LocalDateTime.now());
		taskInstanceRepository.save(taskInstance);
		
		return ResponseEntity.ok(new UpdateTaskAssignmentResponse(taskInstance.getId(), taskInstance.getFamilyMember().getFirebaseUid()));
		
	}
	
	@PostMapping("submit_completion")
	public ResponseEntity<?> submitCompletion(@RequestBody SubmitCompletionRequest req, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		TaskInstance taskInstance = taskInstanceRepository.getReferenceById(req.tastInstanceId());
		taskInstance.setStatus(TaskStatus.SUBMITTED);
		taskInstance.setChildComment(req.comment());
		taskInstance.setSubmittedAt(LocalDateTime.now());
		taskInstanceRepository.save(taskInstance);
		TimeBalance timeBalance = timeBalanceRepository.findByChild(familyMember);
		timeBalance.setPendingTimeInMinutes(timeBalance.getPendingTimeInMinutes() + 
				taskInstance.getMinutesReward());
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("family_kids")
	public ResponseEntity<List<GetKidResponse>> getFamilyKids(@RequestBody GetKidsRequest req, Authentication authentication){
		
		
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		List<FamilyMember> familyMembers = familyMemberRepository.findAllByFamily_id(familyMember.getFamily().getId());
		
		List<GetKidResponse> getKidsListResponse = familyMembers.stream()
				.filter(kid -> kid.getRole().equals(UserRole.CHILD))
				.map(kid -> {
					AppUser appUser = appUserRepository.getReferenceById(kid.getUser().getId());
					TimeBalance timeBalance = timeBalanceRepository.findByChild(kid);
					return new GetKidResponse(kid.getId(),
							kid.getFirebaseUid(), appUser.getEmail(), appUser.getNickname(), kid.getRole(), kid.getFamily().getFamily(), timeBalance.getTotalTimeInMinutes(), 
							timeBalance.getPendingTimeInMinutes());
							
				}).toList();
		
		return ResponseEntity.ok(getKidsListResponse);
	
	}
	
	@PostMapping("family_pending_completions")
	public ResponseEntity<List<FamilyPendingCompletionResponse>> familyPendingCompletion(@RequestBody FamilyPendingCompletionRequest req, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		List<FamilyPendingCompletionResponse> familyPendingCompletionResponses = taskInstanceRepository.findPendingCompletionsByFamilyId(familyMember.getFamily().getId(), TaskStatus.SUBMITTED);
		
		return ResponseEntity.ok(familyPendingCompletionResponses);
	}
	
	@PostMapping("/approve_completion")
	public ResponseEntity<Void> approveCompletionRequest(@RequestBody ApproveCompletionRequest req, Authentication authentication){
		
		FamilyMember childMember = familyMemberRepository.findByFirebaseUid(req.childId());
		TimeBalance timeBalance = timeBalanceRepository.findByChild_Id(childMember.getId());
		TaskInstance taskInstance = taskInstanceRepository.getReferenceById(req.completionId());
		
		System.out.println("req.childId() "  + req.childId() + "childMember.getId(): " + childMember.getId() + "    timeBalance.getPendingTimeInMinutes() - req.time() " + timeBalance.getPendingTimeInMinutes() + " " + req.time() +
				"Before: timeBalance.getTotalTimeInMinutes() " + timeBalance.getTotalTimeInMinutes() + "timeBalance.getChild().getId: " + timeBalance.getChild().getId());
		
		timeBalance.setPendingTimeInMinutes(timeBalance.getPendingTimeInMinutes() - taskInstance.getMinutesReward());
		timeBalance.setTotalTimeInMinutes(timeBalance.getTotalTimeInMinutes() + req.time());
		timeBalance.setLastUpdate(LocalDateTime.now());
		
		timeBalanceRepository.save(timeBalance);
		System.out.println("After save ------------- timeBalance.getPendingTimeInMinutes() " + timeBalance.getPendingTimeInMinutes() + " " + req.time() +
				"After: timeBalance.getTotalTimeInMinutes() " + timeBalance.getTotalTimeInMinutes());
		
		taskInstance.setParentComment(req.parentComment());
		taskInstance.setStatus(TaskStatus.APPROVED);
		taskInstance.setApprovedAt(LocalDateTime.now());
		taskInstanceRepository.save(taskInstance);
		
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("/reject_completion")
	public ResponseEntity<Void> rejectCompletionRequest(@RequestBody RejectCompletionRequest req, Authentication authentication){
		
		FamilyMember childMember = familyMemberRepository.findByFirebaseUid(req.childId());
		TimeBalance timeBalance = timeBalanceRepository.getReferenceById(childMember.getId());
		TaskInstance taskInstance = taskInstanceRepository.getReferenceById(req.completionId());
		
		timeBalance.setPendingTimeInMinutes(timeBalance.getPendingTimeInMinutes() - req.time());
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		
		taskInstance.setParentComment(req.parentComment());
		taskInstance.setStatus(TaskStatus.REJECTED);
		taskInstance.setApprovedAt(LocalDateTime.now());
		taskInstanceRepository.save(taskInstance);
		
		return ResponseEntity.ok(null);
			
	}
	
	@PostMapping("/withdraw_time")
	public ResponseEntity<WithdrawTimeResponse> withdrawTime(@RequestBody WithdrawTimeRequest req, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TimeBalance timeBalance = timeBalanceRepository.findByChild(familyMember);
		WithdrawTimeResponse res;
		if(timeBalance.getParentBlock()) {
			res = new WithdrawTimeResponse(false, "A parent blocked you!");
			return ResponseEntity.ok(res);
		}
		if(timeBalance.getScheduleBlock()) {
			res = new WithdrawTimeResponse(false, "It is your schedule block time");
			return ResponseEntity.ok(res);
		}
		timeBalance.setWithdrawTime(req.minutes());
		timeBalance.setWithdrawTimeUsed(0);
		timeBalance.setIsRunning(true);
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		res = new WithdrawTimeResponse(true, "Ok");
		return ResponseEntity.ok(res);

	}
	
	@PatchMapping("/withdraw_time_stop")
	public ResponseEntity<Void> withdrawTimeStop(Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TimeBalance timeBalance = timeBalanceRepository.findByChild(familyMember);
		
		timeBalance.setWithdrawTime(0);
		timeBalance.setWithdrawTimeUsed(0);
		timeBalance.setIsRunning(false);
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		return ResponseEntity.ok(null);
		
		
	
	}
	
	@PatchMapping("update_task_tamplate")
	public ResponseEntity<TaskResponse> updateTaskTamplate(@RequestBody TaskRequest req, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		
		TaskTemplate taskTemplate = taskTemplateRepository.findByIdAndFamily_Id(req.id(), familyMember.getFamily().getId());
		if(taskTemplate == null) {
			return ResponseEntity.ok(null);
		}
		if(req.title() != null && req.title() != taskTemplate.getTitle()) {
			taskTemplate.setTitle(req.title());
		}
		if(req.description() != null && req.description() != taskTemplate.getDescription()) {
			taskTemplate.setDescription(req.description());
		}
		if(req.screenTime() != null && req.screenTime() != taskTemplate.getMinutesReward()) {
			taskTemplate.setMinutesReward(req.screenTime());
		}
		taskTemplateRepository.save(taskTemplate);
		TaskResponse res = new TaskResponse(taskTemplate.getId(), taskTemplate.getTitle(), taskTemplate.getDescription(), taskTemplate.getMinutesReward());
		return ResponseEntity.ok(res);
		
	}
	
	@DeleteMapping("delete_task/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		TaskTemplate taskTemplate = taskTemplateRepository.findByIdAndFamily_Id(id, familyMember.getFamily().getId());
		if(taskTemplate != null) {
			taskTemplate.setActive(false);
			taskTemplateRepository.save(taskTemplate);
		}
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("family_task_history")
	public ResponseEntity<List<TaskHistoryResponse>> taskHistoryResponse(@RequestBody TaskHistoryRequest req, Authentication authentication){
		List<TaskHistoryResponse> res = taskInstanceRepository.findByFamilyMember_Id(req.childId());
		
		return ResponseEntity.ok(res);
		
	}
	
	@GetMapping("child_task_history")
	public ResponseEntity<List<TaskHistoryResponse>> childTaskHistoryResponse(Authentication authentication){
		String uid = (String) authentication.getPrincipal();
		FamilyMember familyMember = familyMemberRepository.findByFirebaseUid(uid);
		List<TaskHistoryResponse> res = taskInstanceRepository.findByFamilyMember_Id(familyMember.getId());
		
		return ResponseEntity.ok(res);
		
	}
}

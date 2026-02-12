package net.springprojectbackend.springboot.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="weekly_task")
public class WeeklyTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "task_template_id", nullable = false)
	private TaskTemplate taskTemplate;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "child_id", nullable = false)
	private FamilyMember familyMember;
	
	@Column(nullable = false)
	private String title;
	
	private String description;
	
	@Column(nullable = false)
	private Integer minutesReward;
	
	private List<String> days;
	
	private LocalTime toDoAt;
	
	private String today;
	
	private Boolean wasCompleteToday;
	
	private LocalDateTime createdAt;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskTemplate getTaskTemplate() {
		return taskTemplate;
	}

	public void setTaskTemplate(TaskTemplate taskTemplate) {
		this.taskTemplate = taskTemplate;
	}

	public FamilyMember getFamilyMember() {
		return familyMember;
	}

	public void setFamilyMember(FamilyMember familyMember) {
		this.familyMember = familyMember;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getMinutesReward() {
		return minutesReward;
	}

	public void setMinutesReward(Integer minutesReward) {
		this.minutesReward = minutesReward;
	}

	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	public LocalTime getToDoAt() {
		return toDoAt;
	}

	public void setToDoAt(LocalTime toDoAt) {
		this.toDoAt = toDoAt;
	}

	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

	public Boolean getWasCompleateToday() {
		return wasCompleteToday;
	}

	public void setWasCompleateToday(Boolean wasCompleateToday) {
		this.wasCompleteToday = wasCompleateToday;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
}

package net.springprojectbackend.springboot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_task_instance")
public class TaskInstance {

	enum TaskStatus {
	    CREATED,
	    SUBMITTED,
	    APPROVED,
	    REJECTED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
	private AppUser user;
	
	@Column(nullable = false)
	private String title;
	
	private String description;
	
	@Column(nullable = false)
	private Integer minutesReward;
	
	private String parentComment;	
	private String childComment;
	
	private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    
	private TaskStatus status;
	
}

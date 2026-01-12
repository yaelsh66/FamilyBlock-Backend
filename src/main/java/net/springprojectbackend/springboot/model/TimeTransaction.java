package net.springprojectbackend.springboot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_transaction")
public class TimeTransaction {

	enum TimeTransactionType {
	    TASK_REWARD,
	    SESSION_SPENT,
	    PARENT_ADJUST
	}
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private FamilyMember child;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeTransactionType type;    // TASK_REWARD, SESSION_SPENT, PARENT_ADJUST

    @Column(nullable = false)
    private Integer minutesChange;       // Positive (reward) or negative (spent)

    @Column(nullable = true, length = 2000)
    private String reason;               // e.g. "Cleaned room"

    private LocalDateTime createdAt;     // When this transaction happened

    @ManyToOne(optional = true)
    private TaskInstance taskInstance;   // Link to task if this came from a task

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FamilyMember getChild() {
		return child;
	}

	public void setChild(FamilyMember child) {
		this.child = child;
	}

	public TimeTransactionType getType() {
		return type;
	}

	public void setType(TimeTransactionType type) {
		this.type = type;
	}

	public Integer getMinutesChange() {
		return minutesChange;
	}

	public void setMinutesChange(Integer minutesChange) {
		this.minutesChange = minutesChange;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}
}


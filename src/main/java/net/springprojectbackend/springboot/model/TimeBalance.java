package net.springprojectbackend.springboot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_balance")
public class TimeBalance {

	public TimeBalance() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "child_id", unique = true)
	private FamilyMember child;
	
	@Column(nullable = false)
	private Integer totalTimeInMinutes = 0;
	
	@Column(nullable = false)
	private Integer pendingTimeInMinutes = 0;
	
	@Column(nullable = false)
	private Boolean isRunning = false;
	
	private LocalDateTime lastUpdate;

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

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

	public Integer getTotalTimeInMinutes() {
		return totalTimeInMinutes;
	}

	public void setTotalTimeInMinutes(Integer totalTimeInMinutes) {
		this.totalTimeInMinutes = totalTimeInMinutes;
	}

	public Integer getPendingTimeInMinutes() {
		return pendingTimeInMinutes;
	}

	public void setPendingTimeInMinutes(Integer pendingTimeInMinutes) {
		this.pendingTimeInMinutes = pendingTimeInMinutes;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	
}

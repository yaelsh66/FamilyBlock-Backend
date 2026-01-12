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
@Table(name = "time_session")
public class TimeSession {

	enum TimeSessionStatus {
	    RUNNING,
	    FINISHED,
	    FORCE_STOPPED

	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private FamilyMember child;

    @ManyToOne(optional = false)
    private Device device;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Integer minutesAtStart;
    private Integer minutesAtEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSessionStatus status;

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

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(LocalDateTime endedAt) {
		this.endedAt = endedAt;
	}

	public Integer getMinutesAtStart() {
		return minutesAtStart;
	}

	public void setMinutesAtStart(Integer minutesAtStart) {
		this.minutesAtStart = minutesAtStart;
	}

	public Integer getMinutesAtEnd() {
		return minutesAtEnd;
	}

	public void setMinutesAtEnd(Integer minutesAtEnd) {
		this.minutesAtEnd = minutesAtEnd;
	}

	public TimeSessionStatus getStatus() {
		return status;
	}

	public void setStatus(TimeSessionStatus status) {
		this.status = status;
	}
}



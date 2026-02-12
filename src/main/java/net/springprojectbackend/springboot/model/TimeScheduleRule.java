package net.springprojectbackend.springboot.model;



import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="time_schedule")
public class TimeScheduleRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "child_id", unique = true)
	private FamilyMember child;
	
	private String name;
	
	private List<String> day;
	
	private LocalTime start_time;
	
	private LocalTime end_time;
	
	
	
	private Boolean enabled;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDay() {
		return day;
	}

	public void setDay(List<String> day) {
		this.day = day;
	}

	

	public LocalTime getStart() {
		return start_time;
	}

	public void setStart(LocalTime start) {
		this.start_time = start;
	}

	public LocalTime getEnd() {
		return end_time;
	}

	public void setEnd(LocalTime end) {
		this.end_time = end;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}

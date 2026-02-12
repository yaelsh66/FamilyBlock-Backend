package net.springprojectbackend.springboot.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="daily_time_rule")
public class DailyTimeRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "child_id", nullable = false)
	private FamilyMember child;
	
	private List<String> days;
	
	private Integer timeInMinutes;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public FamilyMember getFamilyMember() {
		return child;
	}
	public void setFamilyMember(FamilyMember familyMember) {
		this.child = familyMember;
	}
	public List<String> getDays() {
		return days;
	}
	public void setDays(List<String> days) {
		this.days = days;
	}
	public Integer getTimeInMinutes() {
		return timeInMinutes;
	}
	public void setTimeInMinutes(Integer timeInMinutes) {
		this.timeInMinutes = timeInMinutes;
	}
	
}

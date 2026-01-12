package net.springprojectbackend.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_task_template")
public class TaskTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "family_id")
	private Family family;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
	private FamilyMember owner;
	
	@Column(nullable = false)
	private String title;
	
	private String description;
	
	@Column(nullable = false)
	private Integer minutesReward;
	
	@Column(nullable = false)
	private Boolean active = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public FamilyMember getOwner() {
		return owner;
	}

	public void setOwner(FamilyMember owner) {
		this.owner = owner;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

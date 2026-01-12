package net.springprojectbackend.springboot.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "device")
public class Device {
	
	public Device() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private FamilyMember child;
	
	@Column(nullable = false, unique = true)
	private String deviceId;
	
	@Column(nullable = false)
	private String deviceSecretHash;
	
	@Column(nullable = true)
    private String name;               

    private LocalDateTime lastHeartbeatAt;   
    
    private Boolean lastIsRunning;           
    
    private Integer minutesToReduce;

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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceSecretHash() {
		return deviceSecretHash;
	}

	public void setDeviceSecretHash(String deviceSecretHash) {
		this.deviceSecretHash = deviceSecretHash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getLastHeartbeatAt() {
		return lastHeartbeatAt;
	}

	public void setLastHeartbeatAt(LocalDateTime lastHeartbeatAt) {
		this.lastHeartbeatAt = lastHeartbeatAt;
	}

	public Boolean getLastIsRunning() {
		return lastIsRunning;
	}

	public void setLastIsRunning(Boolean lastIsRunning) {
		this.lastIsRunning = lastIsRunning;
	}

	public Integer getMinutesToReduce() {
		return minutesToReduce;
	}

	public void setMinutesToReduce(Integer minutesToReduce) {
		this.minutesToReduce = minutesToReduce;
	}
    
    
}

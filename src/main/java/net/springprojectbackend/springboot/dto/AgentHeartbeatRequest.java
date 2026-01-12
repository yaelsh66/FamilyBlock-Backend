package net.springprojectbackend.springboot.dto;

public class AgentHeartbeatRequest {

	public String deviceId;
	public String deviceSecret;
	public boolean isRunning;
	public int minutesToReduce;
}

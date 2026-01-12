package net.springprojectbackend.springboot.security;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import net.springprojectbackend.springboot.dto.AgentHeartbeatRequest;
import net.springprojectbackend.springboot.dto.AgentHeartbeatResponse;

@Service
public class AgentService {

	public AgentHeartbeatResponse handleHeartbeatRequest(AgentHeartbeatRequest req) {
		
		AgentHeartbeatResponse res = new AgentHeartbeatResponse();
		
		res.isRunning = req.isRunning;
		res.remainingMinutes = res.remainingMinutes - req.minutesToReduce;
		res.blockedApps = Arrays.asList("WINWORD.EXE", "minecraft.exe");
        res.blockedBrowserApps = Arrays.asList("chrome.exe", "msedge.exe");
        res.blockedWebsites = Arrays.asList("127.0.0.1 youtube.com", "127.0.0.1 www.youtube.com");
        
		return res;
		
	}
	
}

package net.springprojectbackend.springboot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.dto.AgentHeartbeatRequest;
import net.springprojectbackend.springboot.dto.AgentHeartbeatResponse;
import net.springprojectbackend.springboot.model.Device;
import net.springprojectbackend.springboot.repository.DeviceRepository;
import net.springprojectbackend.springboot.security.AgentService;
import net.springprojectbackend.springboot.service.AgentHeartbeatService;

@RestController
@RequestMapping("/agent")
public class AgentController {

	private final AgentHeartbeatService agentService;
	private final DeviceRepository deviceRepository;
	public AgentController(AgentHeartbeatService as, DeviceRepository dr) {
		agentService = as;
		deviceRepository = dr;
	}
	
	@PostMapping("/heartbeat")
	public ResponseEntity<AgentHeartbeatResponse> heartbeat(@RequestBody AgentHeartbeatRequest req) {
        //Test without validation!!!!
        AgentHeartbeatResponse resp = agentService.handleHeartbeat(req);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
	
	@GetMapping("/debug/devices")
	public List<Device> debug() {
	    return deviceRepository.findAll();
	}

}

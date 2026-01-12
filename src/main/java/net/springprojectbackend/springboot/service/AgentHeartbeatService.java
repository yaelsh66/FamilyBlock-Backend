package net.springprojectbackend.springboot.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.springprojectbackend.springboot.dto.AgentHeartbeatRequest;
import net.springprojectbackend.springboot.dto.AgentHeartbeatResponse;
import net.springprojectbackend.springboot.model.Device;
import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.model.BlockConfig;
import net.springprojectbackend.springboot.repository.BlockConfigRepository;
import net.springprojectbackend.springboot.repository.DeviceRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;

@Service
public class AgentHeartbeatService {

	private final DeviceRepository deviceRepository;
    private final TimeBalanceRepository timeBalanceRepository;
    private final BlockConfigRepository blockConfigRepository;
    private final DeviceSecretVerifier deviceSecretVerifier;
    private final BlockConfigParser blockConfigParser;

    AgentHeartbeatService(DeviceRepository deviceRepository,
                          TimeBalanceRepository timeBalanceRepository,
                          BlockConfigRepository blockConfigRepository,
                          DeviceSecretVerifier deviceSecretVerifier,
                          BlockConfigParser blockConfigParser) {
        this.deviceRepository = deviceRepository;
        this.timeBalanceRepository = timeBalanceRepository;
        this.blockConfigRepository = blockConfigRepository;
        this.deviceSecretVerifier = deviceSecretVerifier;
        this.blockConfigParser = blockConfigParser;
    }

    /**
     * Handles a heartbeat.
     *
     * Steps:
     * 1) Authenticate deviceId + deviceSecret
     * 2) Get child
     * 3) Load TimeBalance (authoritative minutes)
     * 4) Deduct minutes if requested
     * 5) Clamp at 0 and force isRunning=false if no time left
     * 6) Return true remaining + block lists
     */
    @Transactional
    public AgentHeartbeatResponse handleHeartbeat(AgentHeartbeatRequest req) {

        // 1) Find device
        Device device = deviceRepository.findByDeviceId(req.deviceId)
                .orElseThrow(() -> new AgentAuthException("Unknown deviceId: " + req.deviceId));

        // 2) Verify secret
        if (!deviceSecretVerifier.matches(req.deviceSecret, device.getDeviceSecretHash())) {
            throw new AgentAuthException("Invalid deviceSecret");
        }

        // 3) Resolve child
        FamilyMember child = device.getChild();

        // 4) Load (or create) TimeBalance
        TimeBalance balance = timeBalanceRepository.findByChild(child);
                if(balance == null) {
                    TimeBalance b = new TimeBalance();
                    b.setChild(child);
                    b.setTotalTimeInMinutes(0);
                    b.setLastUpdate(LocalDateTime.now());
                    timeBalanceRepository.save(b);
                }

        // Keep track of authoritative minutes
        int available = balance.getTotalTimeInMinutes() == null ? 0 : balance.getTotalTimeInMinutes();
        
        if(available > 0 && Boolean.TRUE.equals(device.getLastIsRunning()) && req.isRunning) {
        	
        	int deduct = Math.min(req.minutesToReduce, 1);
        	available = available - deduct;
        }
        
        if(available <= 0 ) {
        	available = 0;
        	device.setLastIsRunning(false);
        }
        
        device.setLastHeartbeatAt(LocalDateTime.now());
        deviceRepository.save(device);
        
        balance.setTotalTimeInMinutes(available);
        balance.setLastUpdate(LocalDateTime.now());
        timeBalanceRepository.save(balance);
       
	        	        
        // 8) Load blocking rules (if missing, treat as empty)
        BlockConfig blockCfg = blockConfigRepository.findByChild(child).orElse(null);

        List<String> blockedApps = blockConfigParser.parseJsonArray(blockCfg == null ? null : blockCfg.getBlockedAppsJson());
        List<String> blockedBrowserApps = blockConfigParser.parseJsonArray(blockCfg == null ? null : blockCfg.getBlockedBrowserAppsJson());
        List<String> blockedWebsites = blockConfigParser.parseJsonArray(blockCfg == null ? null : blockCfg.getBlockedWebsitesJson());
        
        return new AgentHeartbeatResponse(
        		device.getLastIsRunning(),
        		available,
                blockedApps,
                blockedBrowserApps,
                blockedWebsites
        );
    }
    
    //TODO - Update TimeSession, TimeTransaction
}

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

    @Transactional
    public AgentHeartbeatResponse handleHeartbeat(AgentHeartbeatRequest req) {

    	System.out.println("@@@@@@@@@@@@@@@@@@@@ req.deviceId: " + req.deviceId + 
    			"$$$$$$$$$$$$$$ req.deviceSecret: " + req.deviceSecret);
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
        TimeBalance timeBalance = timeBalanceRepository.findByChild(child);
        if (timeBalance == null) {
            TimeBalance b = new TimeBalance();
            b.setChild(child);
            b.setTotalTimeInMinutes(0);
            b.setLastUpdate(LocalDateTime.now());
            timeBalanceRepository.save(b);
            timeBalance = b;
        }

        BlockConfig blockCfg = blockConfigRepository.findByChild(child).orElse(null);

        // ----------------------------
        // NEW STRUCTURE
        // ----------------------------

        List<String> blockedApps =
                blockConfigParser.parseJsonArray(
                        blockCfg == null ? null : blockCfg.getBlockedAppsJson());

        List<String> permanentWebsites =
                blockConfigParser.parseJsonArray(
                        blockCfg == null ? null : blockCfg.getBlockedPermanentWebsitesJson());

        List<String> dynamicWebsites =
                blockConfigParser.parseJsonArray(
                        blockCfg == null ? null : blockCfg.getBlockedWebsitesJson()); // reuse old field for dynamic

        Boolean rewritePermanentWebsites =
                blockCfg == null ? Boolean.FALSE : blockCfg.getRewritePermanentWebsites();

        int available = timeBalance.getTotalTimeInMinutes() == null
                ? 0
                : timeBalance.getTotalTimeInMinutes();
        

        // Parent block
        if (timeBalance.getParentBlock()) {
            return new AgentHeartbeatResponse(
                    false,
                    available,
                    blockedApps,
                    permanentWebsites,
                    dynamicWebsites,
                    rewritePermanentWebsites
            );
        }

        // Schedule block
        if (timeBalance.getScheduleBlock()) {
            return new AgentHeartbeatResponse(
                    false,
                    available,
                    blockedApps,
                    permanentWebsites,
                    dynamicWebsites,
                    rewritePermanentWebsites
            );
        }

        int withdrawTime = timeBalance.getWithdrawTime();
        int deduct = Math.min(req.minutesToReduce, 1);

        if (withdrawTime > 0
                && Boolean.TRUE.equals(timeBalance.getIsRunning())
                && req.isRunning) {

            withdrawTime -= deduct;
            timeBalance.setWithdrawTime(withdrawTime);
            timeBalance.setWithdrawTimeUsed(
                    timeBalance.getWithdrawTimeUsed() + deduct);
        }

        if(timeBalance.getDailyTimeInMinutes() != null && timeBalance.getDailyTimeInMinutes() > 0) {
        	timeBalance.setDailyTimeInMinutes(timeBalance.getDailyTimeInMinutes() - deduct);
        }else {
        	if (available > 0
                    && Boolean.TRUE.equals(timeBalance.getIsRunning())
                    && req.isRunning) {

                available -= deduct;
            }
        	
        }
             

        if (withdrawTime <= 0) {
            timeBalance.setIsRunning(false);
        }

        if(timeBalance.getDailyTimeInMinutes() <= 0 && available <= 0) {
        	timeBalance.setDailyTimeInMinutes(0);
        	available = 0;
            timeBalance.setIsRunning(false);
        }
        
        device.setLastHeartbeatAt(LocalDateTime.now());
        deviceRepository.save(device);

        timeBalance.setTotalTimeInMinutes(available);
        timeBalance.setLastUpdate(LocalDateTime.now());
        timeBalanceRepository.save(timeBalance);

        return new AgentHeartbeatResponse(
                timeBalance.getIsRunning(),
                available,
                blockedApps,
                permanentWebsites,
                dynamicWebsites,
                rewritePermanentWebsites
        );
    }

    
    //TODO - Update TimeSession, TimeTransaction
}

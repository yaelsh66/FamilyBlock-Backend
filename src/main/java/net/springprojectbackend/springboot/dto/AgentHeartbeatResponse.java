package net.springprojectbackend.springboot.dto;

import java.util.List;

public class AgentHeartbeatResponse {

    public boolean isRunning;
    public int remainingMinutes;

    public List<String> blockedApps;

    // New structure
    public List<String> permanentWebsites;
    public List<String> dynamicWebsites;

    public Boolean rewritePermanentWebsites;

    // Default constructor required by Jackson
    public AgentHeartbeatResponse() {}

    public AgentHeartbeatResponse(
            boolean isRunning,
            int remainingMinutes,
            List<String> blockedApps,
            List<String> permanentWebsites,
            List<String> dynamicWebsites,
            Boolean rewritePermanentWebsites) {

        this.isRunning = isRunning;
        this.remainingMinutes = remainingMinutes;
        this.blockedApps = blockedApps;
        this.permanentWebsites = permanentWebsites;
        this.dynamicWebsites = dynamicWebsites;
        this.rewritePermanentWebsites = rewritePermanentWebsites;
    }
}

package net.springprojectbackend.springboot.dto;

import java.util.List;

public class AgentHeartbeatResponse {

    public boolean isRunning;
    public int remainingMinutes;
    public List<String> blockedApps;
    public List<String> blockedBrowserApps;
    public List<String> blockedWebsites;

    // Default constructor required by Jackson
    public AgentHeartbeatResponse() {}

    public AgentHeartbeatResponse(
            boolean isRunning,
            int remainingMinutes,
            List<String> blockedApps,
            List<String> blockedBrowserApps,
            List<String> blockedWebsites) {

        this.isRunning = isRunning;
        this.remainingMinutes = remainingMinutes;
        this.blockedApps = blockedApps;
        this.blockedBrowserApps = blockedBrowserApps;
        this.blockedWebsites = blockedWebsites;
    }
}

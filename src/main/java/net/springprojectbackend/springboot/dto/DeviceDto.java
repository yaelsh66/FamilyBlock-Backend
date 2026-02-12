package net.springprojectbackend.springboot.dto;

import java.util.List;

public class DeviceDto {

	public record AppListRespone(String appList) {}
	
	public record UpdateAppListRequest(Long childId, List<String> appList) {}
	
	public record SiteListRespone(String siteList) {}
	
	public record PermanentSiteListRespone(String siteList, Boolean rewritePermanentWebsites) {}
	
	public record UpdateSiteListRequest(Long childId, List<String> siteList) {}
	
	public record IsRunningResponse(Boolean isRunning) {}
	
	public record AddDeviceRequest(Long childId, String name, String deviceId, String devicePassword) {}
	
	public record GetDeviceResponse(Long id, String name, String deviceId) {}
	
	public record GetDevicesResponse(List<GetDeviceResponse> res) {} 
	
	public record UpdatePermanentSiteListRequest(Long childId, List<String> siteList, 
			Boolean rewritePermanentWebsites) {}
}

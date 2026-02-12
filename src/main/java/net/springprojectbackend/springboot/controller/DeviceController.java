package net.springprojectbackend.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.springprojectbackend.springboot.dto.DeviceDto.AddDeviceRequest;
import net.springprojectbackend.springboot.dto.DeviceDto.AppListRespone;
import net.springprojectbackend.springboot.dto.DeviceDto.GetDeviceResponse;
import net.springprojectbackend.springboot.dto.DeviceDto.GetDevicesResponse;
import net.springprojectbackend.springboot.dto.DeviceDto.IsRunningResponse;
import net.springprojectbackend.springboot.dto.DeviceDto.PermanentSiteListRespone;
import net.springprojectbackend.springboot.dto.DeviceDto.UpdateAppListRequest;
import net.springprojectbackend.springboot.dto.DeviceDto.UpdatePermanentSiteListRequest;
import net.springprojectbackend.springboot.dto.DeviceDto.SiteListRespone;
import net.springprojectbackend.springboot.dto.DeviceDto.UpdateSiteListRequest;
import net.springprojectbackend.springboot.model.BlockConfig;
import net.springprojectbackend.springboot.model.Device;
import net.springprojectbackend.springboot.model.TimeBalance;
import net.springprojectbackend.springboot.repository.BlockConfigRepository;
import net.springprojectbackend.springboot.repository.DeviceRepository;
import net.springprojectbackend.springboot.repository.FamilyMemberRepository;
import net.springprojectbackend.springboot.repository.TimeBalanceRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("api/device")
public class DeviceController {

	private final FamilyMemberRepository familyMemberRepository;
	private final BlockConfigRepository blockConfigRepository;
	private final TimeBalanceRepository timeBalanceRepository;
	private final DeviceRepository deviceRepository;
	
	public DeviceController(FamilyMemberRepository familyMemberRepository, 
			BlockConfigRepository blockConfigRepository, TimeBalanceRepository timeBalanceRepository, DeviceRepository deviceRepository) {
		this.familyMemberRepository = familyMemberRepository;
		this.blockConfigRepository = blockConfigRepository;
		this.timeBalanceRepository = timeBalanceRepository;
		this.deviceRepository = deviceRepository;
	}
	
	
	@GetMapping("get_apps/{childId}")
	public ResponseEntity<AppListRespone> getApps(@PathVariable Long childId, Authentication authentication) {
				
		BlockConfig blockConfig = blockConfigRepository.findByChild_id(childId);
		if (blockConfig == null) {
		    return ResponseEntity.ok(null);
		}
		AppListRespone appListRespone = new AppListRespone(blockConfig.getBlockedAppsJson());
		return ResponseEntity.ok(appListRespone);
	}
	
	@PostMapping("update_apps")
	public ResponseEntity<Void> updateApps(@RequestBody UpdateAppListRequest req, Authentication authentication){
		
		BlockConfig blockConfig = blockConfigRepository.findByChild_id(req.childId());
		if (blockConfig == null) {
			blockConfig = new BlockConfig();
			blockConfig.setChild(familyMemberRepository.getReferenceById(req.childId()));
			
		}
		
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(req.appList());
			blockConfig.setBlockedAppsJson(json);
			blockConfigRepository.save(blockConfig);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok(null);
		
	}
	
	@GetMapping("get_sites/{childId}")
	public ResponseEntity<SiteListRespone> getSites(@PathVariable Long childId, Authentication authentication) {
				
		BlockConfig blockConfig = blockConfigRepository.findByChild_id(childId);
		if (blockConfig == null) {
		    return ResponseEntity.ok(null);
		}
		SiteListRespone siteListRespone = new SiteListRespone(blockConfig.getBlockedWebsitesJson());
		return ResponseEntity.ok(siteListRespone);
	}
	
	
	@PostMapping("update_sites")
	public ResponseEntity<Void> updateSites(@RequestBody UpdateSiteListRequest req, Authentication authentication){
		
		BlockConfig blockConfig = blockConfigRepository.findByChild_id(req.childId());
		if (blockConfig == null) {
			blockConfig = new BlockConfig();
			blockConfig.setChild(familyMemberRepository.getReferenceById(req.childId()));
			
		}
		
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(req.siteList());
			blockConfig.setBlockedWebsitesJson(json);
			blockConfigRepository.save(blockConfig);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok(null);
	}
	
	@GetMapping("get_permanent_sites/{childId}")
	public ResponseEntity<PermanentSiteListRespone> getPermanentSites(
	        @PathVariable Long childId,
	        Authentication authentication) {

	    BlockConfig blockConfig = blockConfigRepository.findByChild_id(childId);

	    if (blockConfig == null) {
	        return ResponseEntity.ok(null);
	    }

	    
	    PermanentSiteListRespone response =
	            new PermanentSiteListRespone(blockConfig.getBlockedPermanentWebsitesJson(),
	                                blockConfig.getRewritePermanentWebsites());

	    return ResponseEntity.ok(response);
	}

	@PostMapping("update_permanent_sites")
	public ResponseEntity<Void> updatePermanentSites(
	        @RequestBody UpdatePermanentSiteListRequest req,
	        Authentication authentication) {

	    BlockConfig blockConfig =
	            blockConfigRepository.findByChild_id(req.childId());

	    if (blockConfig == null) {
	        blockConfig = new BlockConfig();
	        blockConfig.setChild(
	                familyMemberRepository.getReferenceById(req.childId()));
	    }

	    try {
	        String json = new ObjectMapper()
	                .writeValueAsString(req.siteList());

	        blockConfig.setBlockedPermanentWebsitesJson(json);
	        blockConfig.setRewritePermanentWebsites(req.rewritePermanentWebsites());

	        blockConfigRepository.save(blockConfig);

	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	    return ResponseEntity.ok(null);
	}

	@GetMapping("get_is_running/{childId}")
	public ResponseEntity<IsRunningResponse> getIsRunning(@PathVariable Long childId, Authentication authentication){
		TimeBalance timeBalance = timeBalanceRepository.findByChild_Id(childId);
		if (timeBalance == null) {
			return ResponseEntity.ok(null);
		}
		if(timeBalance.getParentBlock()) {
			return ResponseEntity.ok(new IsRunningResponse(false));
		}
		if(timeBalance.getScheduleBlock()) {
			return ResponseEntity.ok(new IsRunningResponse(false));
		}

		IsRunningResponse response = new IsRunningResponse(timeBalance.getIsRunning());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("block_device/{childId}")
	public ResponseEntity<Void> parentBlockDeviceRequest(@PathVariable Long childId, Authentication authentication){
		
		TimeBalance timeBalance = timeBalanceRepository.findByChild_Id(childId);
		if(timeBalance == null) {
			timeBalance = new TimeBalance();
		}
		timeBalance.setParentBlock(true);
		timeBalance.setIsRunning(false);
		timeBalance.setWithdrawTime(0);
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		
		return ResponseEntity.ok(null);
		
	}
	
	@PostMapping("unblock_device/{childId}")
	public ResponseEntity<Void> parentUnblockDevice(@PathVariable Long childId, Authentication authentication){
		TimeBalance timeBalance = timeBalanceRepository.findByChild_Id(childId);
		if(timeBalance == null) {
			timeBalance = new TimeBalance();
		}
		timeBalance.setParentBlock(false);
		timeBalance.setIsRunning(true);
		timeBalance.setWithdrawTime(timeBalance.getDailyTimeInMinutes() + timeBalance.getTotalTimeInMinutes());
		timeBalance.setLastUpdate(LocalDateTime.now());
		timeBalanceRepository.save(timeBalance);
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("add_device")
	public ResponseEntity<Void> addDevice(@RequestBody AddDeviceRequest req, Authentication authentication){
		Device device = new Device();
		device.setChild(familyMemberRepository.getReferenceById(req.childId()));
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secret = req.devicePassword(); 
        String hash = encoder.encode(secret);
        
		device.setName(req.name());
		device.setDeviceId(req.deviceId());
		device.setDeviceSecretHash(hash);
		device.setLastIsRunning(false);
		device.setMinutesToReduce(0);
        
        
        deviceRepository.save(device);
		return ResponseEntity.ok(null);
		
	}
	
	@GetMapping("get_devices/{childId}")
	public ResponseEntity<GetDevicesResponse> getDevices(@PathVariable Long childId, Authentication authentication){
		List<Device> deviceList = deviceRepository.findAllByChild_id(childId);
		List<GetDeviceResponse> resList = deviceList.stream().map((device) -> 
			new GetDeviceResponse(device.getId(), device.getName(), device.getDeviceId())).toList();
		return ResponseEntity.ok(new GetDevicesResponse(resList));
		
	}
	
	@DeleteMapping("delete_device/{childId}/{deviceId}")
	public ResponseEntity<Void> deleteDevice(@PathVariable Long childId, @PathVariable String deviceId){
		Device device = deviceRepository.findByChildIdAndDeviceId(childId, deviceId);
		deviceRepository.delete(device);
		return null;
		
	}
	@Value("${downloadsAgent.path}")
    private String agentDownloadsPath;
	
	@GetMapping("/download_agent")
	public ResponseEntity<Resource> downloadAgent() throws IOException {

	    Path file = Paths.get(agentDownloadsPath).resolve("FamilyBlockService.zip").normalize();
	    System.out.println("Looking for file at: " + file.toAbsolutePath());

	    Resource resource = new UrlResource(file.toUri());

	    if (!resource.exists()) {
	        throw new RuntimeException("File not found: " + file.toAbsolutePath());
	    }

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"FamilyBlockService.zip\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}

	
	@Value("${downloadsUI.path}")
    private String agentDownloadsUIPath;
	
	@GetMapping("/download_agent_ui")
    public ResponseEntity<Resource> downloadAgentUI(@RequestHeader("Authorization") String authorization) throws IOException {
		
        Path file = Paths.get(agentDownloadsUIPath, "FamilyBlockUI.zip");
        System.out.println("UI path base: " + agentDownloadsUIPath);
        Resource resource = new UrlResource(file.toUri());
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"FamilyBlockUI.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
	
	@Value("${downloadsAgentInstaller.path}")
    private String agentInstallerDownloadsPath;
	
	@GetMapping("/download_agentInstaller")
	public ResponseEntity<Resource> downloadAgentInstaller() throws IOException {

	    Path file = Paths.get(agentInstallerDownloadsPath).resolve("FamilyBlockInstaller.zip").normalize();
	    System.out.println("Looking for file at: " + file.toAbsolutePath());

	    Resource resource = new UrlResource(file.toUri());

	    if (!resource.exists()) {
	        throw new RuntimeException("File not found: " + file.toAbsolutePath());
	    }

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"FamilyBlockInstaller.zip\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}
	
}

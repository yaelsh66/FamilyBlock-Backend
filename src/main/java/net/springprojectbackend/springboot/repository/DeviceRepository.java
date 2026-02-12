package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import net.springprojectbackend.springboot.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long>{

	Optional<Device> findByDeviceId(String deviceId);
	
	List<Device> findAllByChild_id(Long childId);
	
	Device findByChildIdAndDeviceId(Long childId, String deviceId);
}

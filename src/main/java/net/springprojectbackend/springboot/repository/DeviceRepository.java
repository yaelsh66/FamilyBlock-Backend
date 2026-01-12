package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import net.springprojectbackend.springboot.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long>{

	Optional<Device> findByDeviceId(String deviceId);
}

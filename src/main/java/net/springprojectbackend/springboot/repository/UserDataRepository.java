package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.UserData;

public interface UserDataRepository extends JpaRepository<UserData, Long>{
	
}

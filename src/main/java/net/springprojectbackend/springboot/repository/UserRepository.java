package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

	AppUser findByfirebaseUid(String uid);

}

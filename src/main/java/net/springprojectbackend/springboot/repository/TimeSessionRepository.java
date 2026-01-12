package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.TimeSession;

public interface TimeSessionRepository extends JpaRepository<TimeSession, Long> {

}

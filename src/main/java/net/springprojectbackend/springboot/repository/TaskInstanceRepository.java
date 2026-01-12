package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.TaskInstance;

public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {

}

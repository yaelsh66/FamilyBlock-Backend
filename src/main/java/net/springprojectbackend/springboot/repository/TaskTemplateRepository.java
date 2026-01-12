package net.springprojectbackend.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.TaskTemplate;

public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

	public List<TaskTemplate> findAllByFamily_id(Long familyId);
	
	public List<TaskTemplate> findAllByOwner_id(Long userId);
}

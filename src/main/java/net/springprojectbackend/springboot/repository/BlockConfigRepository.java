package net.springprojectbackend.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.BlockConfig;

import net.springprojectbackend.springboot.model.FamilyMember;

public interface BlockConfigRepository extends JpaRepository<BlockConfig, Long>{

	Optional<BlockConfig> findByChild(FamilyMember child);
}

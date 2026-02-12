package net.springprojectbackend.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import net.springprojectbackend.springboot.model.FamilyMember;
import net.springprojectbackend.springboot.model.TimeBalance;

public interface TimeBalanceRepository extends JpaRepository<TimeBalance, Long> {

	TimeBalance findByChild(FamilyMember child);
	
	TimeBalance findByChild_Id(Long id);
}

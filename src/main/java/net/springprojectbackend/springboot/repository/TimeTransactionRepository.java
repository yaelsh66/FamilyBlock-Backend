package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.TimeTransaction;

public interface TimeTransactionRepository extends JpaRepository<TimeTransaction, Long> {

	TimeTransaction findByChild_Id(Long childId);
}

package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.FutureExpenses;

public interface VisaCalInputFutureRepository extends JpaRepository<FutureExpenses, Long>{

}

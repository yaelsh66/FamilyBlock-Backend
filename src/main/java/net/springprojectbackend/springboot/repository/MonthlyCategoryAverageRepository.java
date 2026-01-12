package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.MonthlyCategoryAverage;

public interface MonthlyCategoryAverageRepository extends JpaRepository<MonthlyCategoryAverage, Long> {

}

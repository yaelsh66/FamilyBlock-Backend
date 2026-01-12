package net.springprojectbackend.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.AllExpenses;

public interface AllExpensesRepository extends JpaRepository<AllExpenses, Long>{

	
	List<AllExpenses> findByCategoryIdIsNull(); 
	
	List<AllExpenses> findByCategoryIdIsNullAndGeminiCategoryIdIsNull();
}

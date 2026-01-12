package net.springprojectbackend.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.springprojectbackend.springboot.model.ExpenseCategory;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

}

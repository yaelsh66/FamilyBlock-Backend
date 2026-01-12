package net.springprojectbackend.springboot.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;
import net.springprojectbackend.springboot.model.ExpenseCategory;
import net.springprojectbackend.springboot.repository.ExpenseCategoryRepository;

@RestController
@RequestMapping("/category")
public class ExpenseCategoryController {

	private final ExpenseCategoryRepository expenseCategoryRepo;
	
	public ExpenseCategoryController(ExpenseCategoryRepository expenseCategoryRepo) {
		this.expenseCategoryRepo = expenseCategoryRepo;
	}
	
	@PostMapping
	public ExpenseCategory createCategory(@RequestBody ExpenseCategory category){
		return expenseCategoryRepo.save(category);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody ExpenseCategory category) {
	    ExpenseCategory existing;

	    try {
	        existing = expenseCategoryRepo.getReferenceById(id);
	    } catch (EntityNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Category not found with id " + id);
	    }

	    existing.setCategoryName(category.getCategoryName());
	    ExpenseCategory updated = expenseCategoryRepo.save(existing);

	    return ResponseEntity.ok(updated);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Long id){
		expenseCategoryRepo.deleteById(id);
		return ResponseEntity.ok("deleted"+id);
	}
	
	@GetMapping
	public List<ExpenseCategory> getExpenseCategory(){
		return expenseCategoryRepo.findAll();
	}
	

}

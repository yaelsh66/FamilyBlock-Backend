package net.springprojectbackend.springboot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import net.springprojectbackend.springboot.dto.GeminiDto.GeminiResult;
import net.springprojectbackend.springboot.model.AllExpenses;
import net.springprojectbackend.springboot.model.ExpenseCategory;
import net.springprojectbackend.springboot.repository.AllExpensesRepository;
import net.springprojectbackend.springboot.repository.ExpenseCategoryRepository;

@Service
public class BusinessCategorizationService {

	private final AllExpensesRepository allExpensesRepository;
	private final ExpenseCategoryRepository categoryRepository;
	private final GeminiService geminiService;
	
	public BusinessCategorizationService(AllExpensesRepository a, ExpenseCategoryRepository c, GeminiService g) {
		this.allExpensesRepository = a;
		this.categoryRepository = c;
		this.geminiService = g;
		
	}
	
	
	public void assignCategoryToBusiness() {
		
		Map<Long, String> categoriesMap = new HashMap<Long, String>();
		List<ExpenseCategory> categoriesList = categoryRepository.findAll();
		
		for(ExpenseCategory c : categoriesList) {
			categoriesMap.put(c.getCategoryId(), c.getCategoryName());
		}
		
		List<AllExpenses> expensesList = allExpensesRepository.findByCategoryIdIsNullAndGeminiCategoryIdIsNull();
		
		Map<Long, Long> categoriesToUpdate = new HashMap<Long, Long>();
		
		for(AllExpenses expense : expensesList) {
			String message = geminiMessage(expense.getBussinessName(), categoriesMap);
			assignCategoryToBusinessAsync(message, expense);
			
			
		}
		
	}
	
	@Async("geminiExecutor")
	public CompletableFuture<Void> assignCategoryToBusinessAsync(String message, AllExpenses expense){
		
		try {
			GeminiResult geminiResult = geminiService.chat(message);
			
			if(geminiResult.isSuccess()) {
				ExpenseCategory category = categoryRepository.findById(geminiResult.categoryId()).orElse(null);
				expense.setGeminiCategoryId(category);
				allExpensesRepository.save(expense);
			}
		} catch(Exception e) {
	        System.out.println("Error processing business: " + e.getMessage());
	    }
		
		
		return CompletableFuture.completedFuture(null);
	}
	
	private String geminiMessage(String businessName, Map<Long, String> categoriesMap) {
	    StringBuilder s = new StringBuilder();
	    s.append("You are a categorization model. Below is a list of category IDs and names:\n");
	    for (Entry<Long, String> entry : categoriesMap.entrySet()) {
	        s.append(entry.getKey()).append(". ").append(entry.getValue()).append("\n");
	    }

	    s.append("\nBusiness name: ").append(businessName).append("\n\n");
	    s.append("Instructions:\n");
	    s.append("- Analyze only the business name.\n");
	    s.append("- Respond **only with the matching category ID number** (for example: 3).\n");
	    s.append("- Do not include any words, explanations, or punctuation. Just output the number.\n");
	    s.append("- If you are not sure, return the ID for 'Other'.\n");

	    return s.toString();
	}

}

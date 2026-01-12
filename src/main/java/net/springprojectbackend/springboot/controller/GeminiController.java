package net.springprojectbackend.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.springprojectbackend.springboot.service.BusinessCategorizationService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

	private final BusinessCategorizationService businessCategorizationService;
	
	public GeminiController(BusinessCategorizationService b) {
		this.businessCategorizationService = b;
	}
	
	@PostMapping("/ask")
	public ResponseEntity<String> startCategorization() {
        try {
        	businessCategorizationService.assignCategoryToBusiness();
            return ResponseEntity.ok("✅ Categorization process finished successfully.");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to start categorization: " + ex.getMessage());
        }
    }
}

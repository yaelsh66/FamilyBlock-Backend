package net.springprojectbackend.springboot.dto;

import java.util.List;

public class openAIdto {

	public record Message(String role, String content) {}

	public record ChatRequest(String model, List<Message> message) {}
	
	public record Choice(Message message) {} 
	
	public record ChatResponse(List<Choice> choices) {}
	
	public record CategorizationRequest(
		    String businessName,
		    List<String> availableCategories
		) {}

	public record CategorizationResult(
		    String businessName,
		    String selectedCategory
		) {}

}

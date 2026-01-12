package net.springprojectbackend.springboot.dto;

import java.util.List;

public class GeminiDto {

	public record GeminiPart(String text) {}
	
	public record GeminiContent(List<GeminiPart> parts) {}
	
	public record GeminiRequest(List<GeminiContent> contents) {}
	
	public record GeminiCandidate(GeminiContent content, List<GeminiContent> contents) {}
	
	public record GeminiResponse(List<GeminiCandidate> candidates) {}
	
	public record GeminiResult(Long categoryId, String error) {
		
		public boolean isSuccess() {
			return error == null;
		}
	}
}

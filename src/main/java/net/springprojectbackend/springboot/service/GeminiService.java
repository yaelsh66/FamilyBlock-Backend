package net.springprojectbackend.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.annotation.PostConstruct;
import net.springprojectbackend.springboot.dto.GeminiDto;
import net.springprojectbackend.springboot.dto.GeminiDto.GeminiContent;
import net.springprojectbackend.springboot.dto.GeminiDto.GeminiPart;
import net.springprojectbackend.springboot.dto.GeminiDto.GeminiRequest;
import net.springprojectbackend.springboot.dto.GeminiDto.GeminiResponse;
import net.springprojectbackend.springboot.dto.GeminiDto.GeminiResult;
import net.springprojectbackend.springboot.repository.AllExpensesRepository;
import net.springprojectbackend.springboot.repository.ExpenseCategoryRepository;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;
	
	@Value("${gemini.base-url}")
	private String url;
	
	@Value("${gemini.model}")
	private String model;
	
	private WebClient webClient;
	
	
	@PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
	
	public GeminiResult chat(String request){
		
		GeminiDto.GeminiPart part = new GeminiPart(request);
		List<GeminiDto.GeminiPart> parts = new ArrayList<GeminiPart>();
		parts.add(part);
		
		GeminiDto.GeminiContent cont = new GeminiContent(parts);
		List<GeminiContent> conts = new ArrayList<GeminiDto.GeminiContent>();
		conts.add(cont);
		
		GeminiDto.GeminiRequest r = new GeminiRequest(conts);
		
		try {
			GeminiResponse geminiCategory = webClient.post()
					.uri(model + ":generateContent?key=" + apiKey)
					.bodyValue(r)
					.retrieve()
					.bodyToMono(GeminiResponse.class)
					.block();
			
			Long category = getCategoryFromResponse(geminiCategory);
			return new GeminiResult(category, null);
		}
		catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
	        return new GeminiResult(null, "Gemini API error: " + ex.getMessage());
	    } catch (org.springframework.web.reactive.function.client.WebClientRequestException ex) {
	        return new GeminiResult(null, "Network error calling Gemini: " + ex.getMessage());
	    } catch (Exception ex) {
	        return new GeminiResult(null, "Unexpected error: " + ex.getMessage());
	    }
				
	
	}
	
	private Long getCategoryFromResponse(GeminiResponse response) {
	    if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
	        throw new RuntimeException("Gemini returned no candidates");
	    }

	    GeminiDto.GeminiCandidate candidate = response.candidates().get(0);

	    // Newer Gemini models use "content" instead of "contents"
	    GeminiDto.GeminiContent content = null;
	    if (candidate.content() != null) {
	        content = candidate.content();
	    } else if (candidate.contents() != null && !candidate.contents().isEmpty()) {
	        content = candidate.contents().get(0);
	    } else {
	        throw new RuntimeException("Gemini returned no content or contents");
	    }

	    if (content.parts() == null || content.parts().isEmpty()) {
	        throw new RuntimeException("Gemini returned no parts in content");
	    }

	    String text = content.parts().get(0).text();
	    if (text == null || text.isBlank()) {
	        throw new RuntimeException("Gemini returned an empty response");
	    }

	    try {
	        return Long.parseLong(text.trim());
	    } catch (NumberFormatException e) {
	        // If Gemini returned text instead of a number, log it
	        System.out.println("⚠️ Gemini returned non-numeric response: " + text);
	        return null;
	    }
	}


}

package net.springprojectbackend.springboot.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
	//Inject the path from application.properties
	@Value("${firebase.admin.service-account}")
    private String serviceAccountPath;

	@Value("${firebase.admin.service-account-json:}")
	private String serviceAccountJson;
	
	@Bean
	public FirebaseApp firebaseApp() throws IOException{
		
		try (InputStream serviceAccount = openServiceAccount()) {
			//setCredentials: set the API key and other json info
			FirebaseOptions options = FirebaseOptions.builder()
	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                .build();
			//Make sure it happens ONLY once, else get the existing one.
			if (FirebaseApp.getApps().isEmpty()) {
	            return FirebaseApp.initializeApp(options);
	        } else {
	            return FirebaseApp.getInstance();
	        }
		}
		
			
	}

	private InputStream openServiceAccount() throws IOException {
		if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
			return new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
		}

		return new FileInputStream(serviceAccountPath);
	}

}

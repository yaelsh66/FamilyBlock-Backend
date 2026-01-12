package net.springprojectbackend.springboot.config;

import java.io.FileInputStream;
import java.io.IOException;

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
	
	@Bean
	public FirebaseApp firebaseApp() throws IOException{
		
		FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
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

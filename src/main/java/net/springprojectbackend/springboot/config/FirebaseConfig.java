package net.springprojectbackend.springboot.config;

/*
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
*/

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

    @Value("${firebase.admin.service-account}")
    private String serviceAccountPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {

        InputStream serviceAccount;

        String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");

        if (firebaseJson != null && !firebaseJson.isBlank()) {
            serviceAccount = new ByteArrayInputStream(
                    firebaseJson.getBytes(StandardCharsets.UTF_8));
        } else {
            serviceAccount = new FileInputStream(serviceAccountPath);
        }

        try (serviceAccount) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }

            return FirebaseApp.getInstance();
        }
    }
}

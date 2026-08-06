package com.bib.app.Confi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "firebase.credentials.path")
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {

        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String credentialsPath = System.getenv()
                .getOrDefault("FIREBASE_CREDENTIALS",
                        "/vault/secrets/firebase.json");

        try (FileInputStream serviceAccount =
                     new FileInputStream(credentialsPath)) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}
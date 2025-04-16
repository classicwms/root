package com.tekclover.wms.api.enterprise.transaction.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class FirebaseConfig {

//    @Value("classpath:serviceAccountKey.json")
//    private Resource serviceAccountKeyResource;
//
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        InputStream serviceAccountStream = serviceAccountKeyResource.getInputStream();
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
//                .build();
//
//        return FirebaseApp.initializeApp(options);
//    }
//
//    @Bean
//    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
//        return FirebaseMessaging.getInstance(firebaseApp);
//    }

    @Value("classpath:serviceAccountKey.json")
    private Resource serviceAccountKeyResource;

    private long lastModifiedTime = 0;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            return initializeFirebaseApp();
        } else {
            return FirebaseApp.getInstance(); // Return existing instance
        }
    }

    private FirebaseApp initializeFirebaseApp() throws IOException {
        try (InputStream serviceAccountStream = serviceAccountKeyResource.getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream)
                            .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging")))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }

    @Scheduled(fixedRate = 43200000) // Check every 12 hours
    public void reloadFirebaseAppIfModified() {
        try {
            Path path = serviceAccountKeyResource.getFile().toPath();
            long currentModifiedTime = Files.getLastModifiedTime(path).toMillis();

            // Check if the file has been modified
            if (currentModifiedTime > lastModifiedTime) {
                // Update the last modified time
                lastModifiedTime = currentModifiedTime;
                // Reinitialize FirebaseApp only if it exists
                if (FirebaseApp.getApps().isEmpty()) {
                    initializeFirebaseApp();
                } else {
                    // Optional: Log the reloading of the app
                    System.out.println("FirebaseApp reload attempt ignored; instance already exists.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reloading FirebaseApp: " + e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        // Ensure that FirebaseApp is initialized before accessing it
        FirebaseApp firebaseApp = firebaseApp(); // This should be safe now
        return FirebaseMessaging.getInstance(firebaseApp);
    }

}
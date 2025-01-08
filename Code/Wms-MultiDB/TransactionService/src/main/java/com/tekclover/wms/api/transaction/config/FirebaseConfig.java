package com.tekclover.wms.api.transaction.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("classpath:serviceAccountKey.json")
    private Resource serviceAccountKeyResource;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccountStream = serviceAccountKeyResource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        ClassLoader classLoader = WrapperServiceApplication.class.getClassLoader();
//
//        File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getFile());
//
//        FileInputStream serviceAccount =
//                new FileInputStream(file.getAbsolutePath());
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        return FirebaseApp.initializeApp(options, "DEFAULT");
//    }
//
//    @Bean
//    public FirebaseMessaging firebaseMessaging() {
//        return FirebaseMessaging.getInstance(FirebaseApp.getInstance("DEFAULT"));
//    }
}

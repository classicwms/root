package com.tekclover.wms.api.idmaster;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

//import com.tekclover.wms.core.config.JWTAuthorizationFilter;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

//import java.io.IOException;
import java.util.TimeZone;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableResourceServer
@EnableAuthorizationServer
@EnableSwagger2
public class IDMasterServiceApplication {

//	@Bean
//	FirebaseMessaging firebaseMessaging() throws IOException {
//		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource("firebase-service.json").getInputStream());
//		FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
//		FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions, "my-app");
//
//		return FirebaseMessaging.getInstance(firebaseApp);
//
//	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
		SpringApplication.run(IDMasterServiceApplication.class, args);
	}
}
package com.mnrclara.spark.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//import com.tekclover.wms.core.config.JWTAuthorizationFilter;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SuppressWarnings("deprecation")
@SpringBootApplication
@EnableSwagger2
@EnableScheduling
public class SparkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparkServiceApplication.class, args);
	}
}

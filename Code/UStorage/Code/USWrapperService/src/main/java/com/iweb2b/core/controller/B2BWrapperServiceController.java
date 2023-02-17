//package com.iweb2b.core.controller;
//
//import java.util.Optional;
//import java.util.Random;
//
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.iweb2b.core.config.PropertiesConfig;
//import com.iweb2b.core.model.auth.AuthToken;
//import com.iweb2b.core.model.auth.AuthTokenRequest;
//import com.iweb2b.core.model.multidb.DBContextHolder;
//import com.iweb2b.core.model.multidb.DBTypeEnum;
//import com.iweb2b.core.model.test.Post;
//import com.iweb2b.core.repository.PostRepository;
//import com.iweb2b.core.service.AuthTokenService;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.SwaggerDefinition;
//import io.swagger.annotations.Tag;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@CrossOrigin(origins = "*")
//@RestController
//@Api(tags = { "Gateway Service" }, value = "Gateway Service Operations") // label for swagger
//@SwaggerDefinition(tags = { @Tag(name = "User", description = "Operations related to User") })
//public class B2BWrapperServiceController {
//
//	@Autowired
//	AuthTokenService authTokenService;
//
//	@Autowired
//	PropertiesConfig propertiesConfig;
//
//	@Autowired
//	PostRepository postRepository;
//
//	@ApiOperation(response = Optional.class, value = "OAuth Token") // label for swagger
//	@PostMapping("/auth-token")
//	public ResponseEntity<?> authToken(@Valid @RequestBody AuthTokenRequest authTokenRequest) {
//		AuthToken authToken = authTokenService.getAuthToken(authTokenRequest);
//		return new ResponseEntity<>(authToken, HttpStatus.OK);
//	}
//	
//	// test -> main DB
//	// test?client=client-a -> Client A DB
//	// test?client=client-b -> Client B DB
//	@GetMapping("/test")
//	@ResponseBody
//	public Iterable<Post> getTest(@RequestParam(defaultValue = "main") String client) {
//		switch (client) {
//		case "client-a":
//			DBContextHolder.setCurrentDb(DBTypeEnum.CLIENT_A);
//			break;
//		case "client-b":
//			DBContextHolder.setCurrentDb(DBTypeEnum.CLIENT_B);
//			break;
//		}
//		return postRepository.findAll();
//	}
//
//	@GetMapping("/init-data")
//	@ResponseBody
//	public String initialData() {
//		String name = "main - " + (new Random()).nextInt();
//		postRepository.save(new Post("Main DB"));
//		DBContextHolder.setCurrentDb(DBTypeEnum.CLIENT_A);
//		
//		postRepository.save(new Post("Client A DB"));
//		DBContextHolder.setCurrentDb(DBTypeEnum.CLIENT_B);
//		
//		postRepository.save(new Post("Client B DB"));
//		return "Success!";
//	}
//}
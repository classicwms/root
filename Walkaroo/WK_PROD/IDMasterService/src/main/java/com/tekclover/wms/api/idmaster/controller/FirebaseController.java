//package com.tekclover.wms.api.idmaster.controller;
//
//
//import com.tekclover.wms.api.idmaster.model.notification.NotificationMessage;
//import com.tekclover.wms.api.idmaster.model.notification.Token;
//import com.tekclover.wms.api.idmaster.model.notification.notificationuser;
//import com.tekclover.wms.api.idmaster.repository.IUserRepository;
//import com.tekclover.wms.api.idmaster.service.FirebaseMessagingService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.SwaggerDefinition;
//import io.swagger.annotations.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@Api(tags = {"notification"}, value = "Operations related to FirebaseController") // label for swagger
//@SwaggerDefinition(tags = {@Tag(name = "notification",description = "Operations related to notification ")})
//@RequestMapping("/firebase")
//public class FirebaseController {
//
//
//    @Autowired
//    public FirebaseMessagingService firebaseMessagingService;
//
//    @Autowired
//    private IUserRepository userRepository;
//
//
//    @PostMapping("/sendnotification/{username}")
//    public ResponseEntity<?> sendNotificationByToken(@RequestBody NotificationMessage notificationMessage , @PathVariable String username)
//    {
//        try
//        {
//            System.out.println(notificationMessage.getReceptionToken());
//            System.out.println(notificationMessage.getTitle());
//            System.out.println(notificationMessage.getBody());
//            return new ResponseEntity(firebaseMessagingService.sendNotificationByString(notificationMessage , username ) , HttpStatus.OK);
//        }
//        catch(Exception e)
//        {
//            return new ResponseEntity<>(e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//
//    @PostMapping("/gettoken")
//    private ResponseEntity<?> getToken(@RequestBody notificationuser user ){
//        try
//        {
//            notificationuser u = userRepository.findByUsername(user.getUsername());
//
//            if(u!= null)
//            {
//                return new ResponseEntity<>("user already exists , use patch to add new token to the existing user" , HttpStatus.BAD_REQUEST);
//            }
//            System.out.println(user + "user");
//            firebaseMessagingService.setToken(user);
//            return new ResponseEntity<>("adding user success" , HttpStatus.OK);
//
//        }
//        catch(Exception e)
//        {
//            return new ResponseEntity<>(e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PatchMapping("/token/{username}")
//    private  ResponseEntity<?> updateToken(@PathVariable String username , @RequestBody Token token)
//    {
//        try
//        {
//            notificationuser u = userRepository.findByUsername(username);
//
//            u.getTokens().add(token);
//            firebaseMessagingService.updateUser(u);
//            return new ResponseEntity<>("updated user successfully" , HttpStatus.OK);
//        }
//        catch(Exception e)
//        {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//}

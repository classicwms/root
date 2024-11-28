//package com.tekclover.wms.api.idmaster.service;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.tekclover.wms.api.idmaster.model.notification.NotificationMessage;
//import com.tekclover.wms.api.idmaster.model.notification.Token;
//import com.tekclover.wms.api.idmaster.model.notification.notificationuser;
//import com.tekclover.wms.api.idmaster.repository.IUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Service
//public class FirebaseMessagingService {
//
//    @Autowired
//    private IUserRepository userRepository;
//
//    private  String  newString;
//
//    @Autowired
//    private FirebaseMessaging firebaseMessaging;
//
//
//    public void setToken(notificationuser user) {
//        try
//        {
//            System.out.println(user.getTokens() + "tokens");
//            userRepository.save(user);
//            System.out.println(newString + "new string token");
//
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//
//        }
//    }
//
//
//    public notificationuser updateUser(notificationuser user) {
//        return userRepository.save(user);
//    }
//
//
//
//    public String sendNotificationByString(NotificationMessage notificationMessage , String username) {
//        try {
//
//            List<String> tokens = new ArrayList<>();
//            notificationuser user = userRepository.findByUsername(username);
//
//            List<Token> tokenlist=  user.getTokens();
//
//            for(Token t:tokenlist)
//            {
//                tokens.add( t.getToken());
//            }
//
//            System.out.println(notificationMessage.getBody() +"requested entity");
//            Notification notification = Notification
//                    .builder()
//                    .setBody(notificationMessage.getBody())
//                    .setTitle(notificationMessage.getTitle())
//                    .setImage(notificationMessage.getImage())
//                    .build();
//
//
//            for(String tok:tokens)
//            {
//                newString =  tok;
//
//                Message message = Message.builder()
//                        .setToken(newString)
//                        .setNotification(notification)
//                        .putAllData(notificationMessage.getData())
//                        .build();
//
//                firebaseMessaging.send(message);
//            }
//
////                FirebaseMessaging.getInstance().send(message);
//
//            return "Sending notification success";
//
//        } catch (FirebaseMessagingException e) {
//            // Handle FirebaseMessagingException
//
//            e.printStackTrace();
//            return "Error in sending notification: " + e.getMessage();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error in sending notification: " + e.getMessage();
//        }
//    }
//
//
//}

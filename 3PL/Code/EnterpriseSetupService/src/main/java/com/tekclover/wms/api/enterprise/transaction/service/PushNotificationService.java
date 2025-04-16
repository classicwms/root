package com.tekclover.wms.api.enterprise.transaction.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tekclover.wms.api.enterprise.transaction.model.inbound.v2.InboundOrderCancelInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class PushNotificationService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    MastersService mastersService;

    public String sendPushNotification(List<String> tokens, String title, String message) throws FirebaseMessagingException {

        Iterator<String> iterator = tokens.iterator();
        boolean emailSent = false;
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (token == null || token.isEmpty()) {
                iterator.remove();
                continue;
            }
            try {
                Message pushMessage = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build())
                        .build();
                firebaseMessaging.send(pushMessage);
            } catch (FirebaseMessagingException e) {
                iterator.remove();
                if (e.getMessage().contains("invalid_grant") && !emailSent) {
                    sendEmail("Firebase Key Expired: " + e.getMessage());
                    emailSent = true;
                }
                log.error("FireBase Exception : " + e.toString());
            } catch (Exception e) {
                log.error("Exception while push notification : " + e.toString());
            }
        }
        return "OK";
    }

    // Send Mail
    public void sendEmail(String message) {
        InboundOrderCancelInput input = new InboundOrderCancelInput();
        input.setRemarks(message);
        mastersService.sendMailForNotification(input);
    }
}
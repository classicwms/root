package com.tekclover.wms.api.outbound.transaction.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tekclover.wms.api.outbound.transaction.model.notification.NotificationMessage;
import com.tekclover.wms.api.outbound.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.outbound.transaction.repository.NotificationMessageRepository;
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
    NotificationMessageRepository notificationMessageRepository;

    public String sendPushNotification(List<String> tokens, String title, String message) throws FirebaseMessagingException {

        Iterator<String> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if(token == null || token.isEmpty()) {
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
//                e.printStackTrace();
                log.error("FireBase Exception : " + e.toString());
            } catch (Exception e) {
//                e.printStackTrace();
                log.error("Exception while push notification : " + e.toString());
            }
        }
        return "OK";
    }

    /**
     *
     * @param tokens
     * @param notification
     * @return
     */
    public String sendPushNotification(List<String> tokens, NotificationSave notification) {

        Iterator<String> iterator = tokens.iterator();
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
                                .setTitle(notification.getTopic())
                                .setBody(notification.getMessage())
                                .build())
                        .build();
                firebaseMessaging.send(pushMessage);

                boolean existingMessages = notificationMessageRepository.existsByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndDeletionIndicator(
                        notification.getLanguageId(), notification.getCompanyCodeId(), notification.getPlantId(),
                        notification.getWarehouseId(), notification.getDocumentNumber(), 0L);
                if (!existingMessages) {
                    try {
                        saveNotificationMessage(notification);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            } catch (FirebaseMessagingException e) {
                // Token not found, remove it from the list
                iterator.remove();
                log.error("FireBase Exception : " + e.toString());
            } catch (Exception e) {
                iterator.remove();
                // Handle other unexpected exceptions
                log.error("Exception while firebase push notification : " + e.toString());
            }
        }
        return "OK";
    }

    /**
     *
     * @param tokens
     * @param notification
     * @return
     */
    public String sendPushNotificationV5(List<String> tokens, NotificationSave notification) {

        Iterator<String> iterator = tokens.iterator();
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
                                .setTitle(notification.getTopic())
                                .setBody(notification.getMessage())
                                .build())
                        .build();
                firebaseMessaging.send(pushMessage);

                boolean existingMessages = notificationMessageRepository.existsByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndDeletionIndicator(
                        notification.getLanguageId(), notification.getCompanyCodeId(), notification.getPlantId(),
                        notification.getWarehouseId(), notification.getDocumentNumber(), 0L);
                if (!existingMessages) {
                    try {
                        saveNotificationMessage(notification);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            } catch (FirebaseMessagingException e) {
                // Token not found, remove it from the list
                iterator.remove();
                log.error("FireBase Exception : " + e.toString());
            } catch (Exception e) {
                iterator.remove();
                // Handle other unexpected exceptions
                log.error("Exception while firebase push notification : " + e.toString());
            }
        }
        return "OK";
    }

    /**
     *
     * @param notification
     */
    public void saveNotificationMessage(NotificationSave notification) {
        log.info("SaveNotification started");
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setCompanyCodeId(notification.getCompanyCodeId());
        notificationMessage.setLanguageId(notification.getLanguageId());
        notificationMessage.setPlantId(notification.getPlantId());
        notificationMessage.setWarehouseId(notification.getWarehouseId());
        notificationMessage.setTitle(notification.getTopic());
        notificationMessage.setMessage(notification.getMessage());
        notificationMessage.setProcessId(notification.getReferenceNumber());
        notificationMessage.setDeletionIndicator(0L);
        notificationMessage.setCreatedOn(notification.getCreatedOn());

        notificationMessage.setUserId(notification.getUserId().toString());
        notificationMessage.setUserType(null);
        notificationMessage.setReferenceNumber(notification.getReferenceNumber());
        notificationMessage.setDocumentNumber(notification.getDocumentNumber());
        notificationMessage.setCreatedBy(notification.getCreatedBy());
        notificationMessage.setStorageBin(notification.getStorageBin());
        notificationMessage.setNewCreated(true);
        notificationMessageRepository.save(notificationMessage);
        log.info("{} Notification Message saved successfully ", notification.getDocumentNumber());
    }
}
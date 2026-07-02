package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.model.notification.SearchNotification;
import com.tekclover.wms.api.idmaster.model.websocketnotification.*;
import com.tekclover.wms.api.idmaster.repository.NotificationRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.NotificationSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public void sendMessage(String topic, String message, String userId) {
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/messages",
                new OutputMessage(topic, message, time, userId, null));
    }

    public List<Notification> getNotificationList(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrUserIdIsNull(userId);
        return notifications;
    }

    public Boolean updateNotificationAsRead(String userId) {
        try {
            log.info("userId[read all executed]: " + userId);
            notificationRepository.updateStatusV3();
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification for user " + userId);
            return false;
        }
    }

    public Boolean updateNotificationMessage(List<Notification> notification) {
        try {
            notification.stream().forEach(noti -> {
                if (noti.getStorageBin() != null && !noti.getStorageBin().isEmpty()) {
                    notificationRepository.updateStorageBin(noti.getStorageBin());
                } else {
                    notificationRepository.updateStorageBin();
                }
            });
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification");
            return false;
        }
    }

    public Boolean updateNotificationMessage() {
        try {
            notificationRepository.updateStorageBin();
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification");
            return false;
        }
    }

    public Boolean updateNotificationRead(String userId, Long id) {
        try {
            log.info("userId, noti_id: " + userId + "," + id);
            notificationRepository.updateStatusByIdV3(id);
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification for user " + userId);
            return false;
        }
    }

    @Async
    public void saveNotifications(List<String> userId, String userType, String message,
                                  String topic, Date createdOn, String createdBy) {
        List<Notification> toSave = new ArrayList<>();
        if (userId != null && !userId.isEmpty()) {
            for (String user : userId) {
                toSave.add(createNotificationModel(user, userType, message, topic, createdOn, createdBy));
            }
        } else {
            toSave.add(createNotificationModel(null, userType, message, topic, createdOn, createdBy));
        }
        try {
            List<Notification> savedNotificationList = notificationRepository.saveAll(toSave);
            sendMessage(topic, message, null);
            log.info("Notification created for " + topic + " message: " + message, savedNotificationList);
        } catch (Exception e) {
            log.error("Error in creation notification for " + topic + " message: " + message, toSave);
        }
    }

    public Notification createNotificationModel(String userId, String userType,
                                                String message, String topic,
                                                Date createdOn, String createdBy) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(userType);
        notification.setMessage(message);
        notification.setTopic(topic);
        notification.setCreatedOn(createdOn);
        notification.setCreatedBy(createdBy);
        notification.setNewCreated(true);
        return notification;
    }

    @Async
    public void saveNotifications(NotificationSave notificationInput) {
        List<Notification> toSave = new ArrayList<>();
        String topic = notificationInput.getTopic();
        String message = notificationInput.getMessage();
        if (notificationInput.getUserId() != null && !notificationInput.getUserId().isEmpty()) {
            for (String user : notificationInput.getUserId()) {
                toSave.add(createNotificationModel(user, notificationInput));
            }
        } else {
            toSave.add(createNotificationModel(null, notificationInput));
        }
        try {
            List<Notification> savedNotificationList = notificationRepository.saveAll(toSave);
            sendMessage(topic, message, null);
            log.info("Notification created for " + topic + " message: " + message, savedNotificationList);
        } catch (Exception e) {
            log.error("Error in creation notification for " + topic + " message: " + message, toSave);
        }
    }

    public Notification createNotificationModel(String userId, NotificationSave notificationInput) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(notificationInput.getUserType());
        notification.setMessage(notificationInput.getMessage());
        notification.setTopic(notificationInput.getTopic());
        notification.setCreatedOn(notificationInput.getCreatedOn());
        notification.setCreatedBy(notificationInput.getCreatedBy());
        notification.setUpdatedBy(notificationInput.getCreatedBy());
        notification.setDocumentNumber(notificationInput.getDocumentNumber());
        notification.setStorageBin(notificationInput.getStorageBin());
        notification.setCompanyCodeId(notificationInput.getCompanyCodeId());
        notification.setPlantId(notificationInput.getPlantId());
        notification.setLanguageId(notificationInput.getLanguageId());
        notification.setWarehouseId(notificationInput.getWarehouseId());
        return notification;
    }

    /**
     *
     * @param searchNotification
     * @return
     */
    public List<Notification> findNotificationList(SearchNotification searchNotification) {
        NotificationSpecification specification = new NotificationSpecification(searchNotification);
        List<Notification> notifications = notificationRepository.findAll(specification);
        return notifications;
    }
}

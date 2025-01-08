package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.notification.Notification;
import com.tekclover.wms.api.masters.model.notification.OutputMessage;
import com.tekclover.wms.api.masters.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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

    /**
     * @param simpMessagingTemplate
     */
    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * @param topic
     * @param message
     * @param userId
     */
    public void sendMessage(String topic, String message, String userId) throws ParseException {
        try {
            final String time = new SimpleDateFormat("HH:mm").format(new Date());
            simpMessagingTemplate.convertAndSend("/topic/messages",
                    new OutputMessage(topic, message, time, userId, null));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param userId
     * @return
     */
    public List<Notification> getNotificationList(String userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdOrUserIdIsNull(userId);
            return notifications;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param userId
     * @return
     */
    public Boolean updateNotificationAsRead(String userId) {
        try {
            notificationRepository.updateStatus(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in updating status of notification for user " + userId);
            return false;
        }
    }

    /**
     * @param userId
     * @param id
     * @return
     */
    public Boolean updateNotificationRead(String userId, Long id) {
        try {
            notificationRepository.updateStatusById(userId, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in updating status of notification for user " + userId);
            return false;
        }
    }

    /**
     * @param userId
     * @param userType
     * @param message
     * @param topic
     * @param createdOn
     * @param createdBy
     */
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
            e.printStackTrace();
        }
    }

    /**
     * @param userId
     * @param userType
     * @param message
     * @param topic
     * @param createdOn
     * @param createdBy
     * @return
     */
    public Notification createNotificationModel(String userId, String userType,
                                                String message, String topic,
                                                Date createdOn, String createdBy) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setUserType(userType);
            notification.setMessage(message);
            notification.setTopic(topic);
            notification.setCreatedOn(createdOn);
            notification.setCreatedBy(createdBy);
            return notification;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}
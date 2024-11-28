package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.model.websocketnotification.WSNotification;
import com.tekclover.wms.api.idmaster.model.websocketnotification.NotificationSave;
import com.tekclover.wms.api.idmaster.model.websocketnotification.OutputMessage;
import com.tekclover.wms.api.idmaster.model.websocketnotification.SearchNotification;
import com.tekclover.wms.api.idmaster.repository.Specification.WebSocketNotificationSpecification;
import com.tekclover.wms.api.idmaster.repository.WebSocketNotificationRepository;
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
public class WebSocketNotificationService {

    @Autowired
    WebSocketNotificationRepository notificationRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public void sendMessage(String topic, String message, String userId) {
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/messages",
                new OutputMessage(topic, message, time, userId, null));
    }

    public List<WSNotification> getNotificationList(String userId) {
        List<WSNotification> WSNotifications = notificationRepository.findByUserIdOrUserIdIsNull(userId);
        return WSNotifications;
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

    public Boolean updateNotificationMessage(List<WSNotification> WSNotification) {
        try {
            WSNotification.stream().forEach(noti -> {
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
        List<WSNotification> toSave = new ArrayList<>();
        if (userId != null && !userId.isEmpty()) {
            for (String user : userId) {
                toSave.add(createNotificationModel(user, userType, message, topic, createdOn, createdBy));
            }
        } else {
            toSave.add(createNotificationModel(null, userType, message, topic, createdOn, createdBy));
        }
        try {
            List<WSNotification> savedWSNotificationList = notificationRepository.saveAll(toSave);
            sendMessage(topic, message, null);
            log.info("Notification created for " + topic + " message: " + message, savedWSNotificationList);
        } catch (Exception e) {
            log.error("Error in creation notification for " + topic + " message: " + message, toSave);
        }
    }

    public WSNotification createNotificationModel(String userId, String userType,
                                                  String message, String topic,
                                                  Date createdOn, String createdBy) {
        WSNotification WSNotification = new WSNotification();
        WSNotification.setUserId(userId);
        WSNotification.setUserType(userType);
        WSNotification.setMessage(message);
        WSNotification.setTopic(topic);
        WSNotification.setCreatedOn(createdOn);
        WSNotification.setCreatedBy(createdBy);
        WSNotification.setNewCreated(true);
        return WSNotification;
    }

    @Async
    public void saveNotifications(NotificationSave notificationInput) {
        List<WSNotification> toSave = new ArrayList<>();
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
            List<WSNotification> savedWSNotificationList = notificationRepository.saveAll(toSave);
            sendMessage(topic, message, null);
            log.info("Notification created for " + topic + " message: " + message, savedWSNotificationList);
        } catch (Exception e) {
            log.error("Error in creation notification for " + topic + " message: " + message, toSave);
        }
    }

    public WSNotification createNotificationModel(String userId, NotificationSave notificationInput) {
        WSNotification WSNotification = new WSNotification();
        WSNotification.setUserId(userId);
        WSNotification.setUserType(notificationInput.getUserType());
        WSNotification.setMessage(notificationInput.getMessage());
        WSNotification.setTopic(notificationInput.getTopic());
        WSNotification.setCreatedOn(notificationInput.getCreatedOn());
        WSNotification.setCreatedBy(notificationInput.getCreatedBy());
        WSNotification.setUpdatedBy(notificationInput.getCreatedBy());
        WSNotification.setDocumentNumber(notificationInput.getDocumentNumber());
        WSNotification.setStorageBin(notificationInput.getStorageBin());
        WSNotification.setCompanyCodeId(notificationInput.getCompanyCodeId());
        WSNotification.setPlantId(notificationInput.getPlantId());
        WSNotification.setLanguageId(notificationInput.getLanguageId());
        WSNotification.setWarehouseId(notificationInput.getWarehouseId());
        return WSNotification;
    }

    /**
     *
     * @param searchNotification
     * @return
     */
    public List<WSNotification> findNotificationList(SearchNotification searchNotification) {
        WebSocketNotificationSpecification specification = new WebSocketNotificationSpecification(searchNotification);
        return notificationRepository.findAll(specification);
    }
}
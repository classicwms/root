package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.notification.NotificationMsg;
import com.tekclover.wms.api.idmaster.model.notification.SearchNotification;
import com.tekclover.wms.api.idmaster.model.notificationmessage.FindNotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import com.tekclover.wms.api.idmaster.model.websocketnotification.Notification;
import com.tekclover.wms.api.idmaster.model.websocketnotification.NotificationSave;
import com.tekclover.wms.api.idmaster.service.NotificationMessageService;
import com.tekclover.wms.api.idmaster.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@Api(tags = {"Notification-Message"}, value = "Notification-Message Controller Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Other micro service API calls for triggering notification")})
@RequestMapping("/notification-message")
public class NotificationMessageController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationMessageService notificationMessageService;

    @ApiOperation(response = Notification.class, value = "Get a notification message") // label for swagger
    @PostMapping("/create")
    public void createNotificationFromOtherMicroService(@RequestBody NotificationSave notificationSave) {
        notificationService.saveNotifications(notificationSave);
    }

    @ApiOperation(response = Notification.class, value = "Get all Notification details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(name = "userId") String userId) {
        List<Notification> notificationList = notificationService.getNotificationList(userId);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    @ApiOperation(response = Notification.class, value = "Update notification read") // label for swagger
    @GetMapping("/mark-read/{id}")
    public ResponseEntity<?> markNotificationRead(@RequestParam String loginUserID, @PathVariable Long id) {
        Boolean result = notificationService.updateNotificationRead(loginUserID, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @ApiOperation(response = Notification.class, value = "Update notification read all") // label for swagger
//    @GetMapping("/mark-read-all")
//    public ResponseEntity<?> markNotificationAsRead(@RequestParam String loginUserID) {
//        Boolean result = notificationService.updateNotificationAsRead(loginUserID);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    @ApiOperation(response = Notification.class, value = "Update Notification Message")
    @PatchMapping("/update/message")
    public ResponseEntity<?> updateNotification(@Valid @RequestBody List<Notification> notification) {
        Boolean result = notificationService.updateNotificationMessage(notification);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(response = String.class, value = "Update NewCreated Notification ")
    @GetMapping("/update")
    public ResponseEntity<?> updateNotification() {
        Boolean result = notificationMessageService.updateNotificationMessage();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(response = Notification.class, value = "Find Notification details") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findNotification(@RequestBody FindNotificationMessage searchNotification) throws ParseException {
        List<NotificationMessage> notificationList = notificationMessageService.findNotification(searchNotification);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    @ApiOperation(response = NotificationMessage.class, value = "Update notification read all") // label for swagger
    @GetMapping("/mark-read-all")
    public ResponseEntity<?> markNotificationAsRead(@RequestParam String loginUserID) {
        Boolean result = notificationMessageService.updateNotificationAsRead(loginUserID);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
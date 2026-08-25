package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.masters.model.notification.Notification;
import com.tekclover.wms.api.masters.model.notification.NotificationSave;
import com.tekclover.wms.api.masters.service.NotificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@Api(tags = {"Notification-Message"}, value = "Notification-Message Controller Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Other micro service API calls for triggering notification")})
@RequestMapping("/notification-message")
public class NotificationMessageController {

    @Autowired
    NotificationService notificationService;


    @ApiOperation(response = Notification.class, value = "Get a notification message") // label for swagger
    @PostMapping("/create")
    public void createNotificationFromOtherMicroService(@RequestBody NotificationSave notificationSave) {

        notificationService.saveNotifications(notificationSave.getUserId(),
                notificationSave.getUserType(), notificationSave.getMessage(), notificationSave.getTopic(),
                notificationSave.getCreatedOn(),notificationSave.getCreatedBy());
    }

    @ApiOperation(response = Notification.class, value = "Get all Inquiry details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(name = "userId") String userId) {
        List<Notification> notificationList = notificationService.getNotificationList(userId);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    @ApiOperation(response = Notification.class, value = "Update notification read") // label for swagger
    @GetMapping("/mark-read/{id}")
    public ResponseEntity<?> markNotificationRead(@RequestParam String loginUserID,@PathVariable Long id)
            throws IllegalAccessException, InvocationTargetException {
        Boolean result = notificationService.updateNotificationRead(loginUserID,id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @ApiOperation(response = Notification.class, value = "Update notification read all") // label for swagger
    @GetMapping("/mark-read-all")
    public ResponseEntity<?> markNotificationAsRead(@RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Boolean result = notificationService.updateNotificationAsRead(loginUserID);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
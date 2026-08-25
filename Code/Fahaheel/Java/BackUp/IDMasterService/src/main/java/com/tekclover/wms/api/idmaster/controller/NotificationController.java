package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.notification.FindNotification;
import com.tekclover.wms.api.idmaster.model.notification.Notification;
import com.tekclover.wms.api.idmaster.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Notification"}, value = "Notification Operations related to Notification") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Notification", description = "Operations related to Notification")})
@RequestMapping("/firebase/notification")
@RestController
public class NotificationController {

    @Autowired
    NotificationService notificationService;


    // Create Notification
    @ApiOperation(response = Notification.class, value = "Create Notification")
    @PostMapping("")
    public ResponseEntity<?> postNotification(@Valid @RequestBody Notification addNotification, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Notification createdNotification = notificationService.createNotification(addNotification, loginUserID);
        return new ResponseEntity<>(createdNotification, HttpStatus.OK);
    }

    // Update Notification
    @ApiOperation(response = Notification.class, value = "Update Notification")
    @PatchMapping("/update")
    public ResponseEntity<?> patchNotification(@Valid @RequestBody Notification updateNotification, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Notification updatedNotification = notificationService.updateNotification(updateNotification, loginUserID);
        return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
    }

    // Delete Notification
    @ApiOperation(response = Notification.class, value = "Delete Notification")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId, @RequestParam String languageId,
                                                @RequestParam String companyId, @RequestParam String loginUserID) {
        notificationService.deleteNotification(languageId, companyId, notificationId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get All Notification Details
    @ApiOperation(response = Notification.class, value = "Get All Notification Details")
    @GetMapping("")
    public ResponseEntity<?> getAllNotifications() {
        List<Notification> notificationList = notificationService.getAllNotification();
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    // Get Notification
    @ApiOperation(response = Notification.class, value = "Get Notification")
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> getNotification(@PathVariable String notificationId, @RequestParam String languageId,
                                             @RequestParam String companyId) {
        Notification dbNotification = notificationService.getNotification(languageId, companyId, notificationId);
        return new ResponseEntity<>(dbNotification, HttpStatus.OK);
    }

    // Find Notifications
    @ApiOperation(response = Notification.class, value = "Find Notification")
    @PostMapping("/find")
    public ResponseEntity<?> findNotification(@Valid @RequestBody FindNotification findNotification) throws Exception {
        List<Notification> notificationList = notificationService.findNotifications(findNotification);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

}
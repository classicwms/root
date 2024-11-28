package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.notificationmessage.FindNotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMsgDeleteInput;
import com.tekclover.wms.api.idmaster.service.NotificationMessageService;
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
@RestController
@Api(tags = {"NotificationMessage"}, value = "NotificationMessage Operations related to NotificationController")
@SwaggerDefinition(tags = {@Tag(name = "NotificationMessage", description = "Operations related to NotificationMessage")})
@RequestMapping("/notificationMessage")
public class NotificationMessageController {

    @Autowired
    NotificationMessageService notificationMessageService;

    // Get All Notification Messages
    @ApiOperation(response = NotificationMessage.class, value = "Get All Notification Messages")
    @GetMapping("")
    public ResponseEntity<?> getAllNotifications() {
        List<NotificationMessage> notificationMessages = notificationMessageService.getAllNotificationMessages();
        return new ResponseEntity<>(notificationMessages, HttpStatus.OK);
    }

    // Find Notification Messages
    @ApiOperation(response = NotificationMessage.class, value = "Find Notification Messages")
    @PostMapping("/find")
    public List<NotificationMessage> findNotifications(@Valid @RequestBody FindNotificationMessage findNotificationMessage) throws ParseException {
        return notificationMessageService.findNotification(findNotificationMessage);
    }

    // Update Notification Messages
    @ApiOperation(response = NotificationMessage.class, value = "Update Notification Messages")
    @PatchMapping("/update")
    public ResponseEntity<?> patchNotification(@Valid @RequestBody List<NotificationMessage> updateNotification,
                                               @RequestParam String loginUserID) {
        List<NotificationMessage> dbNotification = notificationMessageService.updateNotification(updateNotification, loginUserID);
        return new ResponseEntity<>(dbNotification, HttpStatus.OK);
    }

    // Delete Notification Messages
    @ApiOperation(response = NotificationMessage.class, value = "Delete Notification Messages")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteNotification(@Valid @RequestBody NotificationMsgDeleteInput deleteInput,
                                                @RequestParam String loginUserID) {
        notificationMessageService.deleteNotificationMessage(deleteInput, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete Notification Messages - List
    @ApiOperation(response = NotificationMessage.class, value = "Delete Notification Messages - List")
    @PostMapping("/delete/list")
    public ResponseEntity<?> deleteNotificationList(@Valid @RequestBody List<NotificationMsgDeleteInput> deleteInputList,
                                                    @RequestParam String loginUserID) {
        notificationMessageService.deleteNotificationMessageList(deleteInputList, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = String.class, value = "Update NewCreated Notification ")
    @GetMapping("/update")
    public ResponseEntity<?> updateNotification() {
        Boolean result = notificationMessageService.updateNotificationMessage();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(response = NotificationMessage.class, value = "Update notification read all") // label for swagger
    @GetMapping("/mark-read-all")
    public ResponseEntity<?> markNotificationAsRead(@RequestParam String loginUserID) {
        Boolean result = notificationMessageService.updateNotificationAsRead(loginUserID);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.notificationmessage.FindNotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMsgDeleteInput;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.model.websocketnotification.WSNotification;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.UserManagementRepository;
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
import java.lang.reflect.InvocationTargetException;
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

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    UserManagementRepository userManagementRepository;

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

        try {
            log.info("findNotification input ------> {}", findNotificationMessage);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(findNotificationMessage.getCompanyId(), findNotificationMessage.getPlantId(), findNotificationMessage.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return notificationMessageService.findNotification(findNotificationMessage);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // Update Notification Messages
    @ApiOperation(response = NotificationMessage.class, value = "Update Notification Messages")
    @PatchMapping("/update")
    public ResponseEntity<?> patchNotification(@Valid @RequestBody List<NotificationMessage> updateNotification,
                                               @RequestParam String loginUserID) {
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(updateNotification.get(0).getCompanyCodeId()
                ,updateNotification.get(0).getPlantId(), updateNotification.get(0).getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
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
    public ResponseEntity<?> updateNotification(@RequestBody FindNotificationMessage findNotificationMessage) {

        try {
            log.info("findNotification input ------> {}", findNotificationMessage);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(findNotificationMessage.getCompanyId(), findNotificationMessage.getPlantId(), findNotificationMessage.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            Boolean result = notificationMessageService.updateNotificationMessage();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = WSNotification.class, value = "Update notification read all") // label for swagger
    @GetMapping("/mark-read-all")
    public ResponseEntity<?> markNotificationAsRead(@RequestParam String loginUserID) {
        UserManagement getUser = userManagementRepository.getUserDetails(loginUserID);
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(getUser.getCompanyCode(),getUser.getPlantId(),getUser.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        Boolean result = notificationMessageService.updateNotificationAsRead(loginUserID);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // Create Notification
    @ApiOperation(response = NotificationMessage.class, value = "Create Notification")
    @PostMapping("/create")
    public ResponseEntity<?> postMessage(@Valid @RequestBody NotificationMessage addNotification, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(addNotification.getCompanyCodeId(), addNotification.getPlantId(), addNotification.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        NotificationMessage createdNotification = notificationMessageService.createNotificationMessage(addNotification, loginUserID);
        createdNotification.setStatus(false);
        return new ResponseEntity<>(createdNotification, HttpStatus.OK);
    }

}
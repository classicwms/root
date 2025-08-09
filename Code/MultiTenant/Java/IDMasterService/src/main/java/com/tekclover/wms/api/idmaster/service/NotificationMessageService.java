package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.notificationmessage.FindNotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMsgDeleteInput;
import com.tekclover.wms.api.idmaster.repository.NotificationMessageRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.NotificationMessageSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class NotificationMessageService {

    @Autowired
    NotificationMessageRepository notificationMessageRepository;

    //=================================================================================================================

    /**
     * Get All Notification Messages
     *
     * @return
     */
//    public List<NotificationMessage> getAllNotificationMessages() {
//        List<NotificationMessage> notificationMessages = notificationMessageRepository.findAll();
//        notificationMessages = notificationMessages
//                .stream()
//                .filter(i -> i.getDeletionIndicator() == 0)
//                .collect(Collectors.toList());
//        return notificationMessages;
//    }
    public List<NotificationMessage> getAllNotificationMessages() {
        return notificationMessageRepository.getAllNonDeletedNotificationMessages();
    }

    /**
     * Find Notification Messages
     *
     * @param findNotificationMessage
     * @return
     * @throws ParseException
     */
    public List<NotificationMessage> findNotification(FindNotificationMessage findNotificationMessage) throws ParseException {

        if (findNotificationMessage.getStartDate() != null && findNotificationMessage.getEndDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(findNotificationMessage.getStartDate(), findNotificationMessage.getEndDate());
            findNotificationMessage.setStartDate(dates[0]);
            findNotificationMessage.setEndDate(dates[1]);
        }
        log.info("given Values to fetch Notification Messages --> {}", findNotificationMessage);

        NotificationMessageSpecification spec = new NotificationMessageSpecification(findNotificationMessage);
        return notificationMessageRepository.findAll(spec);
    }

    /**
     * Update Notification Message
     *
     * @param updateNotificationList
     * @param loginUserID
     * @return
     */
    public List<NotificationMessage> updateNotification(List<NotificationMessage> updateNotificationList, String loginUserID) {
//        log.info("Notification to be updated: {}", updateNotificationList);
        List<NotificationMessage> updatedNotificationList = new ArrayList<>();
        for (NotificationMessage updateNotification : updateNotificationList) {
            Long notificationId = updateNotification.getNotificationId();
            if (notificationId != null) {
                NotificationMessage dbNotification =
                        notificationMessageRepository.findByNotificationId(notificationId);
                if (dbNotification != null) {
                    BeanUtils.copyProperties(updateNotification, dbNotification, CommonUtils.getNullPropertyNames(updateNotification));
                    if (dbNotification.getTab() != null) {
                        dbNotification.setTab(dbNotification.getTab());
                    }
                    dbNotification.setUpdatedOn(new Date());
                    dbNotification.setUpdatedBy(loginUserID);
//                    log.info("Updated Notification : {}", dbNotification);
                    updatedNotificationList.add(notificationMessageRepository.save(dbNotification));
                }
            }
        }
        return updatedNotificationList;
    }

    /**
     * Delete Notification Message
     *
     * @param deleteInput
     * @param loginUserID
     */
    public void deleteNotificationMessage(NotificationMsgDeleteInput deleteInput, String loginUserID) {

        List<NotificationMessage> notificationMessages = null;
//        if (deleteInput.getLanguageId() != null && deleteInput.getCompanyId() != null && deleteInput.getPlantId() != null && deleteInput.getWarehouseId() != null &&
//                !deleteInput.getLanguageId().isEmpty() && !deleteInput.getCompanyId().isEmpty() && !deleteInput.getPlantId().isEmpty() && !deleteInput.getWarehouseId().isEmpty()) {
//
//            notificationMessages = notificationMessageRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
//                    deleteInput.getLanguageId(), deleteInput.getCompanyId(), deleteInput.getPlantId(),
//                    deleteInput.getWarehouseId(), 0L);
//
//        } else
        if (deleteInput.getNotificationId() != null) {
            notificationMessages = notificationMessageRepository.findByNotificationIdAndDeletionIndicator(deleteInput.getNotificationId(), 0L);
        }
//            else {
//            throw new BadRequestException("At least one among languageId, companyId, plantId, warehouseId or notificationId must be provided");
//        }

        if (notificationMessages != null && !notificationMessages.isEmpty()) {
            for (NotificationMessage dbNotification : notificationMessages) {
                dbNotification.setDeletionIndicator(1L);
                dbNotification.setUpdatedBy(loginUserID);
                dbNotification.setUpdatedOn(new Date());
                notificationMessageRepository.save(dbNotification);
            }
        } else {
            throw new BadRequestException("Notification Messages for given values doesn't exists");
        }
    }

    /**
     * Delete Notification Messages - List
     *
     * @param deleteInputList
     * @param loginUserID
     */
    public void deleteNotificationMessageList(List<NotificationMsgDeleteInput> deleteInputList, String loginUserID) {

        if (deleteInputList != null && !deleteInputList.isEmpty()) {
            for (NotificationMsgDeleteInput deleteInput : deleteInputList) {
                deleteNotificationMessage(deleteInput, loginUserID);
            }
        }
    }

    public Boolean updateNotificationMessage() {
        try {
            notificationMessageRepository.updateStorageBin();
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification");
            return false;
        }
    }

    public Boolean updateNotificationAsRead(String userId) {
        try {
            log.info("userId[read all executed]: " + userId);
            notificationMessageRepository.updateStatusV3();
            return true;
        } catch (Exception e) {
            log.error("error in updating status of notification for user " + userId);
            return false;
        }
    }


    /**
     * Create Notification
     *
     * @param addNotification
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public NotificationMessage createNotificationMessage(NotificationMessage addNotification, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        NotificationMessage notificationMessage = new NotificationMessage();
        BeanUtils.copyProperties(addNotification,notificationMessage,CommonUtils.getNullPropertyNames(addNotification));
        return notificationMessageRepository.save(notificationMessage);

    }

}
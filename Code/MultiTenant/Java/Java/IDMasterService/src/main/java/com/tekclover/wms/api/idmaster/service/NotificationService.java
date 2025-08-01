package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.notification.FindNotification;
import com.tekclover.wms.api.idmaster.model.notification.Notification;
import com.tekclover.wms.api.idmaster.repository.NotificationRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.NotificationSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NumberRangeService numberRangeService;

    /**
     * Get Notification
     *
     * @param languageId
     * @param companyId
     * @param notificationId
     * @return
     */
    public Notification getNotification(String languageId, String companyId, String notificationId) {

        Optional<Notification> dbNotification = notificationRepository.findByLanguageIdAndCompanyIdAndNotificationIdAndDeletionIndicator(
                languageId, companyId, notificationId, 0L);
        if (dbNotification.isEmpty()) {
            throw new BadRequestException("The given values - LanguageId: " + languageId + ", CompanyId: " + companyId
                    + " and NotificationId: " + notificationId + "  doesn't exists");
        }
        return dbNotification.get();
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
    public Notification createNotification(Notification addNotification, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            boolean duplicateNotification = notificationRepository.existsByLanguageIdAndCompanyIdAndNotificationIdAndDeletionIndicator(
                    addNotification.getLanguageId(), addNotification.getCompanyId(), addNotification.getNotificationId(), 0L);
            if (duplicateNotification) {
                throw new BadRequestException("Record is getting duplicated with given values : notificationId - " + addNotification.getNotificationId());
            }
            log.info("new Notification --> {}", addNotification);

            IKeyValuePair iKeyValuePair = notificationRepository.getDescription(addNotification.getLanguageId(), addNotification.getCompanyId());
            Notification newNotification = new Notification();
            BeanUtils.copyProperties(addNotification, newNotification, CommonUtils.getNullPropertyNames(addNotification));
            if (iKeyValuePair != null) {
                newNotification.setLanguageDescription(iKeyValuePair.getLangDesc());
                newNotification.setCompanyName(iKeyValuePair.getCompanyDesc());
            }
            newNotification.setDeletionIndicator(0L);
            newNotification.setCreatedBy(loginUserID);
            newNotification.setCreatedOn(new Date());
            newNotification.setUpdatedBy(null);
            newNotification.setUpdatedOn(null);
            return notificationRepository.save(newNotification);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update Notification
     *
     * @param updateNotification
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional
    public Notification updateNotification(Notification updateNotification, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            Notification dbNotification = getNotification(updateNotification.getLanguageId(), updateNotification.getCompanyId(), updateNotification.getNotificationId());
            BeanUtils.copyProperties(updateNotification, dbNotification, CommonUtils.getNullPropertyNames(updateNotification));
            dbNotification.setUpdatedBy(loginUserID);
            dbNotification.setUpdatedOn(new Date());
            return notificationRepository.save(dbNotification);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete Notification
     *
     * @param languageId
     * @param companyId
     * @param notificationId
     * @param loginUserID
     */
    public void deleteNotification(String languageId, String companyId, String notificationId, String loginUserID) {
        Notification dbNotification = getNotification(languageId, companyId, notificationId);
        if (dbNotification != null) {
            dbNotification.setDeletionIndicator(1L);
            dbNotification.setUpdatedBy(loginUserID);
            dbNotification.setUpdatedOn(new Date());
            notificationRepository.save(dbNotification);
        } else {
            throw new BadRequestException("Error in deleting NotificationId - " + notificationId);
        }
    }

    /**
     * Get all Notification Details
     *
     * @return
     */
//    public List<Notification> getAllNotification() {
//        List<Notification> notificationList = notificationRepository.findAll()
//                .stream()
//                .filter(i -> i.getDeletionIndicator() == 0)
//                .collect(Collectors.toList());
//        return notificationList;
//    }
    public List<Notification> getAllNotification() {
        return notificationRepository.getAllNonDeletedNotifications();
    }

    /**
     * Find Notifications
     *
     * @param findNotification
     * @return
     */
    public List<Notification> findNotifications(FindNotification findNotification) {

        NotificationSpecification spec = new NotificationSpecification(findNotification);
        List<Notification> results = notificationRepository.findAll(spec);
//        log.info("found Notifications --> {}", results);
        return results;
    }

}

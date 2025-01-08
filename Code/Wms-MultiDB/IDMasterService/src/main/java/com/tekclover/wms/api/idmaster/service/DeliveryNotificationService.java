package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.deliverynotification.DeliveryNotification;
import com.tekclover.wms.api.idmaster.repository.DeliveryNotificationRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class DeliveryNotificationService {

    @Autowired
    DeliveryNotificationRepository deliveryNotificationRepository;


    /**
     * Create DeliveryNotification
     * @param newDeliveryNotification
     * @return
     */
    public DeliveryNotification createDeliveryNotification(DeliveryNotification newDeliveryNotification, String loginUserID) {

        DeliveryNotification newHht = new DeliveryNotification();
        Optional<DeliveryNotification> optionalDelNotification =
                deliveryNotificationRepository.findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
                newDeliveryNotification.getCompanyId(),
                        newDeliveryNotification.getPlantId(),
                        newDeliveryNotification.getWarehouseId(),
                        newDeliveryNotification.getLanguageId(),
                        newDeliveryNotification.getDeviceId(),
                        newDeliveryNotification.getUserId(),
                        newDeliveryNotification.getTokenId(),
                        0L);
        if (optionalDelNotification.isPresent()) {
            DeliveryNotification dbHhtNotification = optionalDelNotification.get();
            dbHhtNotification.setDeletionIndicator(1L);
            dbHhtNotification.setUpdatedOn(new Date());
            dbHhtNotification.setUpdatedBy(loginUserID);
            deliveryNotificationRepository.save(dbHhtNotification);

            if (!newDeliveryNotification.getIsLoggedIn()) {
                return dbHhtNotification;
            }
        }
        BeanUtils.copyProperties(newDeliveryNotification, newHht, CommonUtils.getNullPropertyNames(newDeliveryNotification));
        newHht.setDeletionIndicator(0L);
        newHht.setCreatedBy(loginUserID);
        newHht.setUpdatedBy(loginUserID);
        newHht.setCreatedOn(new Date());
        newHht.setUpdatedOn(new Date());
        newHht.setNotificationHeaderId(System.currentTimeMillis());
        return deliveryNotificationRepository.save(newHht);
    }


    /**
     * GetDeliveryNotification
     *
     * @param warehouseId
     * @param companyId
     * @param languageId
     * @param plantId
     * @param deviceId
     * @param userId
     * @param tokenId
     * @return
     */
    public DeliveryNotification getDeliveryNotification (String warehouseId, String companyId,String languageId,String plantId, String deviceId, String userId,String tokenId ) {
        Optional<DeliveryNotification> dbHhtNotification =
                deliveryNotificationRepository.findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
                        companyId,
                        plantId,
                        warehouseId,
                        languageId,
                        deviceId,
                        userId,
                        tokenId,
                        0L
                );
        if (dbHhtNotification.isPresent()) {
            return dbHhtNotification.get();
        }else {
            throw new BadRequestException("No User Found");
        }
    }



}

package com.tekclover.wms.api.idmaster.service;


import com.tekclover.wms.api.idmaster.model.hhtuser.OrderTypeId;
import com.tekclover.wms.api.idmaster.repository.OrderTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderTypeIdService {
    @Autowired
    OrderTypeIdRepository orderTypeIdRepository;


    public List<OrderTypeId> getOrderTypeIds () {
        List<OrderTypeId> orderTypeIdList =  orderTypeIdRepository.findAll();
        orderTypeIdList = orderTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return orderTypeIdList;
    }

    /**
     * getReferenceField3
     * @param storageMethod
     * @return
     */
    public List<OrderTypeId> getOrderTypeId (String userId) {
        List<OrderTypeId> orderTypeIdList = orderTypeIdRepository.findByUserIdAndDeletionIndicator(userId, 0L);
        if (orderTypeIdList.isEmpty()) {
            return null;
        }
        //levelReferences = levelReferences.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return orderTypeIdList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param userId
     * @return
     */
    public List<OrderTypeId> getOrderTypeId (String companyCodeId, String plantId, String languageId, String warehouseId, String userId) {
        List<OrderTypeId> orderTypeIdList = orderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndUserIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, userId, 0L);
        if (orderTypeIdList.isEmpty()) {
            return null;
        }
        return orderTypeIdList;
    }

    /**
     * deleteLevelReferences
     */
    public void deleteOrderTypeId (String userId, String loginUserID) throws ParseException {
        List<OrderTypeId> orderTypeIds= getOrderTypeId(userId);
        if (orderTypeIds != null) {
            for(OrderTypeId newOrderTypeId: orderTypeIds){
                newOrderTypeId.setDeletionIndicator(1L);
                newOrderTypeId.setUpdatedBy(loginUserID);
                newOrderTypeId.setUpdatedOn(new Date());
                orderTypeIdRepository.save(newOrderTypeId);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + userId);
        }
    }
    public void deleteOrderTypeIdV2 (String userId, String loginUserID) throws ParseException {
        List<OrderTypeId> orderTypeIds= getOrderTypeId(userId);
        if (orderTypeIds != null) {
            for(OrderTypeId newOrderTypeId: orderTypeIds){
                newOrderTypeId.setDeletionIndicator(1L);
                newOrderTypeId.setUpdatedBy(loginUserID);
                newOrderTypeId.setUpdatedOn(new Date());
                orderTypeIdRepository.save(newOrderTypeId);
            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + userId);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param userId
     * @param loginUserID
     * @throws ParseException
     */
    public void deleteOrderTypeIdV2 (String companyCodeId, String plantId, String languageId, String warehouseId, String userId, String loginUserID) throws ParseException {
        List<OrderTypeId> orderTypeIds= getOrderTypeId(companyCodeId, plantId, languageId, warehouseId, userId);
        if (orderTypeIds != null) {
//            for(OrderTypeId newOrderTypeId: orderTypeIds){
//                orderTypeIdRepository.delete(newOrderTypeId);
                orderTypeIdRepository.deleteOrderTypeId(userId, languageId, companyCodeId, plantId, warehouseId);
//            }
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + userId);
        }
    }
}

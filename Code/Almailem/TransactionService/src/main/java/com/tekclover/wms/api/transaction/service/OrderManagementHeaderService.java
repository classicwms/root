package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.AddOrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementHeader;
import com.tekclover.wms.api.transaction.repository.OrderManagementHeaderRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderManagementHeaderService {

    @Autowired
    private OrderManagementHeaderRepository orderManagementHeaderRepository;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;
    //------------------------------------------------------------------------------------------------------

    /**
     * getOrderManagementHeaders
     *
     * @return
     */
    public List<OrderManagementHeader> getOrderManagementHeaders() {
        List<OrderManagementHeader> orderManagementHeaderList = orderManagementHeaderRepository.findAll();
        orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return orderManagementHeaderList;
    }

    /**
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OrderManagementHeader getOrderManagementHeader(String warehouseId, String preOutboundNo,
                                                          String refDocNumber, String partnerCode) {
        OrderManagementHeader orderManagementHeader =
                orderManagementHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementHeader ID : " +
                "warehouseId : " + warehouseId +
                ", preOutboundNo : " + preOutboundNo +
                ", refDocNumber : " + refDocNumber +
                ", partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OrderManagementHeader getOrderManagementHeaderForUpdate(String warehouseId, String preOutboundNo,
                                                                   String refDocNumber, String partnerCode) {
        OrderManagementHeader orderManagementHeader =
                orderManagementHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        log.info("The given OrderManagementHeader ID : " +
                "warehouseId : " + warehouseId +
                ", preOutboundNo : " + preOutboundNo +
                ", refDocNumber : " + refDocNumber +
                ", partnerCode : " + partnerCode +
                " doesn't exist.");
        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    public OrderManagementHeader getOrderManagementHeader(String preOutboundNo) {
        OrderManagementHeader orderManagementHeader = orderManagementHeaderRepository.findByPreOutboundNo(preOutboundNo);
        if (orderManagementHeader != null && orderManagementHeader.getDeletionIndicator() == 0) {
            return orderManagementHeader;
        } else {
            throw new BadRequestException("The given OrderManagementHeader ID : " + preOutboundNo + " doesn't exist.");
        }
    }

    /**
     * createOrderManagementHeader
     *
     * @param newOrderManagementHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementHeader createOrderManagementHeader(AddOrderManagementHeader newOrderManagementHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Optional<OrderManagementHeader> ordermangementheader =
                orderManagementHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        newOrderManagementHeader.getLanguageId(),
                        newOrderManagementHeader.getCompanyCodeId(),
                        newOrderManagementHeader.getPlantId(),
                        newOrderManagementHeader.getWarehouseId(),
                        newOrderManagementHeader.getPreOutboundNo(),
                        newOrderManagementHeader.getRefDocNumber(),
                        newOrderManagementHeader.getPartnerCode(),
                        0L);
        if (!ordermangementheader.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        OrderManagementHeader dbOrderManagementHeader = new OrderManagementHeader();
        log.info("newOrderManagementHeader : " + newOrderManagementHeader);
        BeanUtils.copyProperties(newOrderManagementHeader, dbOrderManagementHeader);
        dbOrderManagementHeader.setDeletionIndicator(0L);
        return orderManagementHeaderRepository.save(dbOrderManagementHeader);
    }

    /**
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @param updateOrderManagementHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementHeader updateOrderManagementHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                                             String loginUserID, UpdateOrderManagementHeader updateOrderManagementHeader)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementHeader dbOrderManagementHeader = getOrderManagementHeaderForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (dbOrderManagementHeader != null) {
            BeanUtils.copyProperties(updateOrderManagementHeader, dbOrderManagementHeader, CommonUtils.getNullPropertyNames(updateOrderManagementHeader));
            return orderManagementHeaderRepository.save(dbOrderManagementHeader);
        }
        return null;
    }

    /**
     * deleteOrderManagementHeader
     *
     * @param loginUserID
     * @param refDocNumber
     */
    public void deleteOrderManagementHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID) {
        OrderManagementHeader orderManagementHeader =
                getOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (orderManagementHeader != null) {
            orderManagementHeader.setDeletionIndicator(1L);
            orderManagementHeaderRepository.save(orderManagementHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

    //=================================================================V2=======================================================================================

    /**
     * getOrderManagementHeaders
     *
     * @return
     */
    public List<OrderManagementHeaderV2> getOrderManagementHeadersV2() {
        List<OrderManagementHeaderV2> orderManagementHeaderList = orderManagementHeaderV2Repository.findAll();
        orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return orderManagementHeaderList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OrderManagementHeaderV2 getOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preOutboundNo,
                                                              String refDocNumber, String partnerCode) {
        OrderManagementHeaderV2 orderManagementHeader =
                orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ", preOutboundNo : " + preOutboundNo +
                ", refDocNumber : " + refDocNumber +
                ", partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    public OrderManagementHeaderV2 getOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preOutboundNo,
                                                              String refDocNumber) {
        OrderManagementHeaderV2 orderManagementHeader =
                orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
       return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OrderManagementHeaderV2 getOrderManagementHeaderForUpdateV2(String companyCodeId, String plantId, String languageId,
                                                                       String warehouseId, String preOutboundNo,
                                                                       String refDocNumber, String partnerCode) {
        OrderManagementHeaderV2 orderManagementHeader =
                orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        log.info("The given OrderManagementHeader ID : " +
                "warehouseId : " + warehouseId +
                ", preOutboundNo : " + preOutboundNo +
                ", refDocNumber : " + refDocNumber +
                ", partnerCode : " + partnerCode +
                " doesn't exist.");
        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    public OrderManagementHeaderV2 getOrderManagementHeaderV2(String preOutboundNo) {
        OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderV2Repository.findByPreOutboundNo(preOutboundNo);
        if (orderManagementHeader != null && orderManagementHeader.getDeletionIndicator() == 0) {
            return orderManagementHeader;
        } else {
            throw new BadRequestException("The given OrderManagementHeader ID : " + preOutboundNo + " doesn't exist.");
        }
    }

    /**
     * createOrderManagementHeader
     *
     * @param newOrderManagementHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementHeaderV2 createOrderManagementHeaderV2(OrderManagementHeaderV2 newOrderManagementHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<OrderManagementHeaderV2> ordermangementheader =
                orderManagementHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        newOrderManagementHeader.getLanguageId(),
                        newOrderManagementHeader.getCompanyCodeId(),
                        newOrderManagementHeader.getPlantId(),
                        newOrderManagementHeader.getWarehouseId(),
                        newOrderManagementHeader.getPreOutboundNo(),
                        newOrderManagementHeader.getRefDocNumber(),
                        newOrderManagementHeader.getPartnerCode(),
                        0L);
        if (!ordermangementheader.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        OrderManagementHeaderV2 dbOrderManagementHeader = new OrderManagementHeaderV2();
        log.info("newOrderManagementHeader : " + newOrderManagementHeader);

        IKeyValuePair description = stagingLineV2Repository.getDescription(newOrderManagementHeader.getCompanyCodeId(),
                newOrderManagementHeader.getLanguageId(),
                newOrderManagementHeader.getPlantId(),
                newOrderManagementHeader.getWarehouseId());

        BeanUtils.copyProperties(newOrderManagementHeader, dbOrderManagementHeader, CommonUtils.getNullPropertyNames(newOrderManagementHeader));

        if (newOrderManagementHeader.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(newOrderManagementHeader.getStatusId(), newOrderManagementHeader.getLanguageId());
            dbOrderManagementHeader.setStatusDescription(statusDescription);
        }

        dbOrderManagementHeader.setCompanyDescription(description.getCompanyDesc());
        dbOrderManagementHeader.setPlantDescription(description.getPlantDesc());
        dbOrderManagementHeader.setWarehouseDescription(description.getWarehouseDesc());

        dbOrderManagementHeader.setDeletionIndicator(0L);
        dbOrderManagementHeader.setPickupCreatedBy(loginUserID);
        dbOrderManagementHeader.setPickupCreatedOn(new Date());
        dbOrderManagementHeader.setPickupUpdatedBy(loginUserID);
        dbOrderManagementHeader.setPickupupdatedOn(new Date());
        return orderManagementHeaderV2Repository.save(dbOrderManagementHeader);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @param updateOrderManagementHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OrderManagementHeaderV2 updateOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String preOutboundNo, String refDocNumber, String partnerCode,
                                                                 String loginUserID, OrderManagementHeaderV2 updateOrderManagementHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OrderManagementHeaderV2 dbOrderManagementHeader = getOrderManagementHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (dbOrderManagementHeader != null) {
            BeanUtils.copyProperties(updateOrderManagementHeader, dbOrderManagementHeader, CommonUtils.getNullPropertyNames(updateOrderManagementHeader));
            dbOrderManagementHeader.setPickupUpdatedBy(loginUserID);
            dbOrderManagementHeader.setPickupupdatedOn(new Date());

            if (dbOrderManagementHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbOrderManagementHeader.getStatusId(), dbOrderManagementHeader.getLanguageId());
                dbOrderManagementHeader.setStatusDescription(statusDescription);
            }

            return orderManagementHeaderV2Repository.save(dbOrderManagementHeader);
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     */
    public void deleteOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID) throws ParseException {
        OrderManagementHeaderV2 orderManagementHeader =
                getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (orderManagementHeader != null) {
            orderManagementHeader.setDeletionIndicator(1L);
            orderManagementHeader.setPickupUpdatedBy(loginUserID);
            orderManagementHeader.setPickupupdatedOn(new Date());
            orderManagementHeaderV2Repository.save(orderManagementHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    // DeleteOrderManagementHeader
    public OrderManagementHeaderV2 deleteOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId,
                                                                 String warehouseId, String refDocNumber,String loginUserID)throws Exception{

        OrderManagementHeaderV2 orderManagementHeaderV2 =
                orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("PickList Cancellation - OrderManagementHeader : " + orderManagementHeaderV2);
        if(orderManagementHeaderV2 != null){
            orderManagementHeaderV2.setDeletionIndicator(1L);
            orderManagementHeaderV2.setPickupUpdatedBy(loginUserID);
            orderManagementHeaderV2.setPickupupdatedOn(new Date());
//            orderManagementHeaderV2.setPickupupdatedOn(new Date());
            orderManagementHeaderV2Repository.save(orderManagementHeaderV2);
        }
        return orderManagementHeaderV2;
    }
}
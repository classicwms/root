package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.SearchPreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.tng.*;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundLineV2Specification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundLine;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
public class PreOutboundLineService extends BaseService {

    @Autowired
    private PreOutboundLineRepository preOutboundLineRepository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    //----------------------------------------------------------------------------------------
    @Autowired
    private PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;
    //----------------------------------------------------------------------------------------

    /**
     * getPreOutboundLines
     *
     * @return
     */
    public List<PreOutboundLine> getPreOutboundLines() {
        List<PreOutboundLine> preOutboundLineList = preOutboundLineRepository.findAll();
        preOutboundLineList = preOutboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preOutboundLineList;
    }

    /**
     * getPreOutboundLine
     *
     * @param lineNumber
     * @return
     */
    public PreOutboundLine getPreOutboundLine(String languageId, String companyCodeId, String plantId, String warehouseId,
                                              String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode) {
        Optional<PreOutboundLine> preOutboundLine =
                preOutboundLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode,
                        lineNumber, itemCode, 0L);
        if (!preOutboundLine.isEmpty()) {
            return preOutboundLine.get();
        }

        return null;
    }

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @return
     */
    public List<PreOutboundLine> getPreOutboundLine(String languageId, String companyCodeId, String plantId, String warehouseId,
                                                    String refDocNumber, String preOutboundNo, String partnerCode) {
        List<PreOutboundLine> preOutboundLine =
                preOutboundLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, 0L);
        if (!preOutboundLine.isEmpty()) {
            return preOutboundLine;
        }

        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    private List<PreOutboundLine> getPreOutboundLine(String preOutboundNo) {
        List<PreOutboundLine> preOutboundLine = preOutboundLineRepository.findByPreOutboundNo(preOutboundNo);
        if (preOutboundLine != null) {
            return preOutboundLine;
        } else {
            throw new BadRequestException("The given PreOutboundLine ID : " + preOutboundNo + " doesn't exist.");
        }
    }


    /**
     * @param searchPreOutboundLine
     * @return
     * @throws ParseException
     */
    public List<PreOutboundLine> findPreOutboundLine(SearchPreOutboundLine searchPreOutboundLine)
            throws ParseException {
        PreOutboundLineSpecification spec = new PreOutboundLineSpecification(searchPreOutboundLine);
        List<PreOutboundLine> results = preOutboundLineRepository.findAll(spec);
        return results;
    }

    /**
     * createPreOutboundLine
     *
     * @param newPreOutboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundLine createPreOutboundLine(AddPreOutboundLine newPreOutboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreOutboundLine dbPreOutboundLine = new PreOutboundLine();
        log.info("newPreOutboundLine : " + newPreOutboundLine);
        BeanUtils.copyProperties(newPreOutboundLine, dbPreOutboundLine);
        dbPreOutboundLine.setDeletionIndicator(0L);
        dbPreOutboundLine.setCreatedBy(loginUserID);
        dbPreOutboundLine.setUpdatedBy(loginUserID);
        dbPreOutboundLine.setCreatedOn(new Date());
        dbPreOutboundLine.setUpdatedOn(new Date());
        return preOutboundLineRepository.save(dbPreOutboundLine);
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updatePreOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundLine updatePreOutboundLine(String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode,
                                                 String loginUserID, UpdatePreOutboundLine updatePreOutboundLine)
            throws IllegalAccessException, InvocationTargetException {
        PreOutboundLine dbPreOutboundLine = getPreOutboundLine(getLanguageId(), getCompanyCode(), getPlantId(), warehouseId,
                refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode);
        if (dbPreOutboundLine != null) {
            BeanUtils.copyProperties(updatePreOutboundLine, dbPreOutboundLine, CommonUtils.getNullPropertyNames(updatePreOutboundLine));
            dbPreOutboundLine.setUpdatedBy(loginUserID);
            dbPreOutboundLine.setUpdatedOn(new Date());
            return preOutboundLineRepository.save(dbPreOutboundLine);
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param loginUserID
     * @param updatePreOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundLine updatePreOutboundLine(String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, String loginUserID,
                                                 UpdatePreOutboundLine updatePreOutboundLine)
            throws IllegalAccessException, InvocationTargetException {
        List<PreOutboundLine> dbPreOutboundLines = getPreOutboundLine(getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, refDocNumber, preOutboundNo, partnerCode);
        for (PreOutboundLine dbPreOutboundLine : dbPreOutboundLines) {
            BeanUtils.copyProperties(updatePreOutboundLine, dbPreOutboundLine, CommonUtils.getNullPropertyNames(updatePreOutboundLine));
            dbPreOutboundLine.setUpdatedBy(loginUserID);
            dbPreOutboundLine.setUpdatedOn(new Date());
            dbPreOutboundLine = preOutboundLineRepository.save(dbPreOutboundLine);
        }
        return null;
    }


    /**
     * deletePreOutboundLine
     * @param loginUserID
     * @param lineNumber
     */
//	public void deletePreOutboundLine (String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode, String loginUserID) {
//		PreOutboundLine preOutboundLine = getPreOutboundLine(lineNumber);
//		if ( preOutboundLine != null) {
//			preOutboundLine.setDeletionIndicator(1L);
//			preOutboundLine.setUpdatedBy(loginUserID);
//			preOutboundLineRepository.save(preOutboundLine);
//		} else {
//			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
//		}
//	}

//================================================================V2=================================================================================

    /**
     * getPreOutboundLines
     *
     * @return
     */
    public List<PreOutboundLineV2> getPreOutboundLinesV2() {
        List<PreOutboundLineV2> preOutboundLineList = preOutboundLineV2Repository.findAll();
        preOutboundLineList = preOutboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preOutboundLineList;
    }

    /**
     * getPreOutboundLine
     *
     * @param lineNumber
     * @return
     */
    public PreOutboundLineV2 getPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode) {
        Optional<PreOutboundLineV2> preOutboundLine =
                preOutboundLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode,
                        lineNumber, itemCode, 0L);
        if (!preOutboundLine.isEmpty()) {
            return preOutboundLine.get();
        }

        return null;
    }

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @return
     */
    public List<PreOutboundLineV2> getPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String refDocNumber, String preOutboundNo, String partnerCode) {
        List<PreOutboundLineV2> preOutboundLine =
                preOutboundLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, 0L);
        if (!preOutboundLine.isEmpty()) {
            return preOutboundLine;
        }

        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<PreOutboundLineV2> getPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String refDocNumber, String preOutboundNo) {
        List<PreOutboundLineV2> preOutboundLine =
                preOutboundLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, 0L);
        if (!preOutboundLine.isEmpty()) {
            return preOutboundLine;
        }

        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    private List<PreOutboundLineV2> getPreOutboundLineV2(String preOutboundNo) {
        List<PreOutboundLineV2> preOutboundLine = preOutboundLineV2Repository.findByPreOutboundNo(preOutboundNo);
        if (preOutboundLine != null) {
            return preOutboundLine;
        } else {
            throw new BadRequestException("The given PreOutboundLine ID : " + preOutboundNo + " doesn't exist.");
        }
    }


    /**
     * @param searchPreOutboundLine
     * @return
     * @throws ParseException
     */
    public Stream<PreOutboundLineV2> findPreOutboundLineV2(SearchPreOutboundLineV2 searchPreOutboundLine)
            throws ParseException {
        PreOutboundLineV2Specification spec = new PreOutboundLineV2Specification(searchPreOutboundLine);
        Stream<PreOutboundLineV2> results = preOutboundLineV2Repository.stream(spec, PreOutboundLineV2.class);
        return results;
    }

    /**
     * createPreOutboundLine
     *
     * @param newPreOutboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundLineV2 createPreOutboundLineV2(PreOutboundLineV2 newPreOutboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PreOutboundLineV2 dbPreOutboundLine = new PreOutboundLineV2();
        log.info("newPreOutboundLine : " + newPreOutboundLine);
        BeanUtils.copyProperties(newPreOutboundLine, dbPreOutboundLine);
        dbPreOutboundLine.setDeletionIndicator(0L);
        dbPreOutboundLine.setCreatedBy(loginUserID);
        dbPreOutboundLine.setUpdatedBy(loginUserID);
        dbPreOutboundLine.setCreatedOn(new Date());
        dbPreOutboundLine.setUpdatedOn(new Date());
        return preOutboundLineV2Repository.save(dbPreOutboundLine);
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updatePreOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundLineV2 updatePreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode,
                                                     String loginUserID, UpdatePreOutboundLine updatePreOutboundLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PreOutboundLineV2 dbPreOutboundLine = getPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode);
        if (dbPreOutboundLine != null) {
            BeanUtils.copyProperties(updatePreOutboundLine, dbPreOutboundLine, CommonUtils.getNullPropertyNames(updatePreOutboundLine));
            dbPreOutboundLine.setUpdatedBy(loginUserID);
            dbPreOutboundLine.setUpdatedOn(new Date());
            //TNG
            String businessPartnerCode = imBasicData1V2Repository.getPartnerCode(companyCodeId,plantId,languageId,warehouseId,dbPreOutboundLine.getPartnerCode());
            if (businessPartnerCode != null && businessPartnerCode.equalsIgnoreCase("True")) {
                UpdateShipmentOrder updateShipmentOrder = new UpdateShipmentOrder();
                List<Sku> skus = new ArrayList<>();

                Sku sku = new Sku();
                sku.setSku(dbPreOutboundLine.getItemCode());
                sku.setQty(dbPreOutboundLine.getOrderQty());
                skus.add(sku);

                updateShipmentOrder.setOrderReference(dbPreOutboundLine.getRefDocNumber());
                updateShipmentOrder.setStorerKey("TAAGER");
                updateShipmentOrder.setSku(skus);

                try {
                    OrderResponse response = idmasterService.updateShipmentOrder(updateShipmentOrder);

                    if (response.getSuccess()) {
                        Long WEBHOOK_STATUS = 200L;
                        preOutboundLineV2Repository.updateWebhookStatus(dbPreOutboundLine.getRefDocNumber(), WEBHOOK_STATUS);
                    } else {
                        Long WEBHOOK_STATUS = 500L;
                        preOutboundLineV2Repository.updateWebhookStatus(dbPreOutboundLine.getRefDocNumber(), WEBHOOK_STATUS);
                    }

                } catch (RestClientException e) {
                    log.error("WebHook Error while pushing purchaseOrder ----> " + e);
                }
            }
            return preOutboundLineV2Repository.save(dbPreOutboundLine);
        }
        return dbPreOutboundLine;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param loginUserID
     * @param updatePreOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PreOutboundLineV2> updatePreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String refDocNumber, String preOutboundNo, String partnerCode, String loginUserID,
                                                           PreOutboundLineV2 updatePreOutboundLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<PreOutboundLineV2> dbPreOutboundLines = getPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, partnerCode);
        List<PreOutboundLineV2> updatedPreOutboundLine = new ArrayList<>();
        for (PreOutboundLineV2 dbPreOutboundLine : dbPreOutboundLines) {
            BeanUtils.copyProperties(updatePreOutboundLine, dbPreOutboundLine, CommonUtils.getNullPropertyNames(updatePreOutboundLine));
            dbPreOutboundLine.setUpdatedBy(loginUserID);
            dbPreOutboundLine.setUpdatedOn(new Date());
            dbPreOutboundLine = preOutboundLineV2Repository.save(dbPreOutboundLine);
            updatedPreOutboundLine.add(dbPreOutboundLine);
        }
        return updatedPreOutboundLine;
    }


    /**
     * deletePreOutboundLine
     *
     * @param loginUserID
     * @param lineNumber
     */
    public void deletePreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode, String loginUserID) throws java.text.ParseException {
        PreOutboundLineV2 preOutboundLine = getPreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode);
        if (preOutboundLine != null) {
            preOutboundLine.setDeletionIndicator(1L);
            preOutboundLine.setUpdatedBy(loginUserID);
            preOutboundLine.setUpdatedOn(new Date());
            String customerCode = imBasicData1V2Repository.getPartnerCode(companyCodeId,plantId,languageId,warehouseId,partnerCode);
            if (customerCode != null && customerCode.equalsIgnoreCase("True")) {
                CancelShipmentOrder cancelShipmentOrder = new CancelShipmentOrder();
                cancelShipmentOrder.setOrderReference(preOutboundLine.getRefDocNumber());
                cancelShipmentOrder.setWarehouseKey("INFOR_SCPRD_wmwhse1");
                cancelShipmentOrder.setOrderKey("0000036565");

                try {
                    CancelShipmentOrderResponse response = idmasterService.cancelShipmentOrder(cancelShipmentOrder);

                    if (response.getSuccess()) {
                        Long WEBHOOK_STATUS = 20L;
                        preOutboundLineV2Repository.updateWebhookStatus(preOutboundLine.getRefDocNumber(), WEBHOOK_STATUS);
                    } else {
                        Long WEBHOOK_STATUS = 50L;
                        preOutboundLineV2Repository.updateWebhookStatus(preOutboundLine.getRefDocNumber(), WEBHOOK_STATUS);
                    }
                } catch (RestClientException e) {
                    log.error("WebHook Error while pushing purchaseOrder ----> " + e);
                }
            }
            preOutboundLineV2Repository.save(preOutboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
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
    //Cancel PreOutboundLine
    public List<PreOutboundLineV2> cancelPreOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String refDocNumber, String  preOutboundNo, String loginUserID)throws Exception{

        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - PreOutboundLine : " + preOutboundLineV2List);
        List<PreOutboundLineV2> preOutboundLineV2s = new ArrayList<>();
        if(preOutboundLineV2List != null && !preOutboundLineV2List.isEmpty()) {
            for(PreOutboundLineV2 preOutboundLineV2 : preOutboundLineV2List){
                preOutboundLineV2.setStatusId(96L);
                statusDescription = stagingLineV2Repository.getStatusDescription(96L, languageId);
                preOutboundLineV2.setStatusDescription(statusDescription);
                preOutboundLineV2.setUpdatedBy(loginUserID);
                preOutboundLineV2.setUpdatedOn(new Date());
                PreOutboundLineV2 dbPreOutBoundLine = preOutboundLineV2Repository.save(preOutboundLineV2);
                preOutboundLineV2s.add(dbPreOutBoundLine);
            }
        }
        return preOutboundLineV2s;
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
    //Delete PreOutboundLine
    public List<PreOutboundLineV2> deletePreOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID)throws Exception{

        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - PreOutboundLine : " + preOutboundLineV2List);
        List<PreOutboundLineV2> preOutboundLineV2s = new ArrayList<>();
        if(preOutboundLineV2List != null && !preOutboundLineV2List.isEmpty()) {
            for(PreOutboundLineV2 preOutboundLineV2 : preOutboundLineV2List){
                preOutboundLineV2.setDeletionIndicator(1L);
                preOutboundLineV2.setUpdatedBy(loginUserID);
                preOutboundLineV2.setUpdatedOn(new Date());
                PreOutboundLineV2 dbPreOutBoundLine = preOutboundLineV2Repository.save(preOutboundLineV2);
                preOutboundLineV2s.add(dbPreOutBoundLine);
            }
        }
        return preOutboundLineV2s;
    }

    /**
     * Pick List cancel
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<PreOutboundLineV2> getPLCPreOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String refDocNumber, String preOutboundNo) {
        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - PreOutboundLine : " + preOutboundLineV2List);
        return preOutboundLineV2List;
}
}
package com.tekclover.wms.api.inbound.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.InventoryDetail;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineOutputV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.SearchPreInboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PreInboundLineV2Specification;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.SearchInventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.BomHeader;
import com.tekclover.wms.api.inbound.transaction.model.dto.BomLine;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.AddPreInboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundLineEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.UpdatePreInboundLine;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreInboundLineService extends BaseService {

    @Autowired
    private PreInboundLineRepository preInboundLineRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private AuthTokenService authTokenService;

    //--------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    
    @Autowired
    private InventoryService inventoryService;

    String statusDescription = null;
    //--------------------------------------------------------------------------------------

    /**
     * getPreInboundLines
     *
     * @return
     */
    public List<PreInboundLineEntity> getPreInboundLines() {
        List<PreInboundLineEntity> preInboundLineList = preInboundLineRepository.findAll();
        preInboundLineList = preInboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preInboundLineList;
    }

    /**
     *
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public PreInboundLineEntity getPreInboundLine(String preInboundNo, String warehouseId,
                                                  String refDocNumber, Long lineNo, String itemCode) {
        Optional<PreInboundLineEntity> preInboundLine =
                preInboundLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (preInboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",temCode: " + itemCode + " doesn't exist.");
        }
        return preInboundLine.get();
    }

    /**
     *
     * @param preInboundNo
     * @return
     */
    public List<PreInboundLineEntity> getPreInboundLine(String preInboundNo) {
        List<PreInboundLineEntity> preInboundLines =
                preInboundLineRepository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
        if (preInboundLines.isEmpty()) {
            throw new BadRequestException("The given values: preInboundNo:" + preInboundNo +
                    " doesn't exist.");
        }
        preInboundLines.forEach(preInboundLine -> {
            var quantity = inboundLineRepository.getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(
                    preInboundLine.getItemCode(), preInboundLine.getRefDocNumber(), preInboundLine.getPreInboundNo(),
                    preInboundLine.getLineNo(), preInboundLine.getWarehouseId());
            preInboundLine.setReferenceField5(quantity != null ? quantity.toString() : null);
        });
        return preInboundLines;
    }

    /**
     * createPreInboundLine
     *
     * @param newPreInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntity createPreInboundLine(AddPreInboundLine newPreInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        PreInboundLineEntity dbPreInboundLine = new PreInboundLineEntity();
        log.info("newPreInboundLine : " + newPreInboundLine);
        BeanUtils.copyProperties(newPreInboundLine, dbPreInboundLine, CommonUtils.getNullPropertyNames(newPreInboundLine));
        dbPreInboundLine.setDeletionIndicator(0L);
        dbPreInboundLine.setCreatedBy(loginUserID);
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setCreatedOn(new Date());
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineRepository.save(dbPreInboundLine);
    }

    /**
     * createPreInboundLine
     *
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param lineNo
     * @param loginUserID
     * @return
     */
    public List<PreInboundLineEntity> createPreInboundLine(String preInboundNo, String warehouseId, String refDocNumber,
                                                           String itemCode, Long lineNo, String loginUserID) {
        try {
            // Query PreInboundLine
            PreInboundLineEntity preInboundLineEntity = getPreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);

            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            BomHeader bomHeader = mastersService.getBomHeader(itemCode, warehouseId, authTokenForMastersService.getAccess_token());
            if (bomHeader != null) {
                BomLine[] bomLine =
                        mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(), authTokenForMastersService.getAccess_token());
                List<PreInboundLineEntity> toBeCreatedPreInboundLineList = new ArrayList<>();
                for (BomLine dbBomLine : bomLine) {
                    toBeCreatedPreInboundLineList.add(createPreInboundLineBOMBased(getCompanyCode(),
                            getPlantId(), preInboundNo, refDocNumber, warehouseId, lineNo, itemCode, dbBomLine,
                            preInboundLineEntity, loginUserID));
                }

                // Batch Insert - PreInboundLines
                if (!toBeCreatedPreInboundLineList.isEmpty()) {
                    List<PreInboundLineEntity> createdPreInboundLineList = preInboundLineRepository.saveAll(toBeCreatedPreInboundLineList);
                    log.info("createdPreInboundLineList [BOM] : " + createdPreInboundLineList);

                    List<InboundLine> toBeCreatedInboundLineList = new ArrayList<>();
                    for (PreInboundLineEntity createdPreInboundLine : createdPreInboundLineList) {
                        log.info("PreInboundLine---------> : " + createdPreInboundLine);
                        InboundLine inboundLine = new InboundLine();
                        BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

                        // OrdQty
                        inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());

                        // OrdUOM
                        inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());

                        // IB_order_type_id
                        inboundLine.setInboundOrderTypeId(createdPreInboundLine.getInboundOrderTypeId());

                        // StatusId
                        inboundLine.setStatusId(createdPreInboundLine.getStatusId());

                        // MFR_PART
                        inboundLine.setManufacturerPartNo(createdPreInboundLine.getManufacturerPartNo());
                        inboundLine.setDescription(createdPreInboundLine.getItemDescription());
                        inboundLine.setBusinessPartnerCode(createdPreInboundLine.getBusinessPartnerCode());
                        inboundLine.setDeletionIndicator(0L);
                        inboundLine.setCreatedBy(createdPreInboundLine.getCreatedBy());
                        inboundLine.setCreatedOn(createdPreInboundLine.getCreatedOn());
                        toBeCreatedInboundLineList.add(inboundLine);
                    }

                    List<InboundLine> createdInboundLine = inboundLineRepository.saveAll(toBeCreatedInboundLineList);
                    log.info("createdInboundLine : " + createdInboundLine);
                    return createdPreInboundLineList;
                }
            } else {
                throw new BadRequestException("There is No Bom for this item.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.toString());
        }
        return null;
    }

    /**
     * @param companyCode
     * @param plantID
     * @param preInboundNo
     * @param refDocNumber
     * @param warehouseId
     * @param lineNo
     * @param itemCode
     * @param bomLine
     * @param preInboundLineEntity
     * @param loginUserID
     * @return
     */
    private PreInboundLineEntity createPreInboundLineBOMBased(String companyCode, String plantID, String preInboundNo, String refDocNumber,
                                                              String warehouseId, Long lineNo, String itemCode, BomLine bomLine, PreInboundLineEntity preInboundLineEntity, String loginUserID) {
        Warehouse warehouse = getWarehouse(warehouseId);

        PreInboundLineEntity preInboundLine = new PreInboundLineEntity();
        BeanUtils.copyProperties(preInboundLineEntity, preInboundLine, CommonUtils.getNullPropertyNames(preInboundLineEntity));

        preInboundLine.setLanguageId(warehouse.getLanguageId());
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantID);
        preInboundLine.setRefDocNumber(refDocNumber);
        preInboundLine.setInboundOrderTypeId(preInboundLineEntity.getInboundOrderTypeId());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(lineNo);

        // ITM_CODE
        preInboundLine.setItemCode(bomLine.getChildItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(itemCode, warehouseId, authTokenForMastersService.getAccess_token());
        preInboundLine.setItemDescription(imBasicData1.getDescription());

        // MFR
        preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());

        // ORD_QTY
        double orderQuantity = preInboundLineEntity.getOrderQty() * bomLine.getChildItemQuantity();
        preInboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preInboundLine.setOrderUom(preInboundLineEntity.getOrderUom());

        // EA_DATEpreInboundLineEntity
        preInboundLine.setExpectedArrivalDate(preInboundLineEntity.getExpectedArrivalDate());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preInboundLine.setStatusId(6L);

        // REF_FIELD_1
        preInboundLine.setReferenceField1("BOM");

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy(loginUserID);
        preInboundLine.setCreatedOn(new Date());
        return preInboundLine;
    }

    /**
     *
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param updatePreInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntity updatePreInboundLine(String preInboundNo, String warehouseId,
                                                     String refDocNumber, Long lineNo, String itemCode, UpdatePreInboundLine updatePreInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundLineEntity dbPreInboundLine = getPreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        BeanUtils.copyProperties(updatePreInboundLine, dbPreInboundLine, CommonUtils.getNullPropertyNames(updatePreInboundLine));
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineRepository.save(dbPreInboundLine);
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntity updatePreInboundLine(String preInboundNo, String warehouseId,
                                                     String refDocNumber, Long lineNo, String itemCode, Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundLineEntity dbPreInboundLine = getPreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        dbPreInboundLine.setStatusId(statusId);
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineRepository.save(dbPreInboundLine);
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<PreInboundLineEntity> preInboundLines = getPreInboundLines();
        preInboundLines.forEach(preiblines -> preiblines.setReferenceField1(asnNumber));
        preInboundLineRepository.saveAll(preInboundLines);
    }

    /**
     * deletePreInboundLine
     *
     * @param loginUserID
     * @param lineNo
     */
    public void deletePreInboundLine(String preInboundNo, String warehouseId,
                                     String refDocNumber, Long lineNo, String itemCode, String loginUserID) {
        PreInboundLineEntity preInboundLine = getPreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        if (preInboundLine != null && preInboundLine.getStatusId() == 6L) {
            preInboundLine.setDeletionIndicator(1L);
            preInboundLine.setUpdatedBy(loginUserID);
            preInboundLine.setUpdatedOn(new Date());
            preInboundLineRepository.save(preInboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preInboundNo);
        }
    }

    //=================================================================V2======================================================================

    /**
     * getPreInboundLines
     *
     * @return
     */
    public List<PreInboundLineEntityV2> getPreInboundLinesV2() {
        List<PreInboundLineEntityV2> preInboundLineList = preInboundLineV2Repository.findAll();
        preInboundLineList = preInboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preInboundLineList;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public PreInboundLineEntityV2 getPreInboundLineV2(String companyCode, String plantId, String languageId,
                                                      String preInboundNo, String warehouseId,
                                                      String refDocNumber, Long lineNo, String itemCode) {
        Optional<PreInboundLineEntityV2> preInboundLine =
                preInboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (preInboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",temCode: " + itemCode + " doesn't exist.");
        }
        return preInboundLine.get();
    }

    /**
     *
     * @param preInboundNo
     * @return
     */
    public List<PreInboundLineEntityV2> getPreInboundLineV2(String preInboundNo) {
        List<PreInboundLineEntityV2> preInboundLines =
                preInboundLineV2Repository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
        if (preInboundLines.isEmpty()) {
            throw new BadRequestException("The given values: preInboundNo:" + preInboundNo +
                    " doesn't exist.");
        }
        preInboundLines.forEach(preInboundLine -> {
            var quantity = inboundLineV2Repository.getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(
                    preInboundLine.getItemCode(), preInboundLine.getRefDocNumber(), preInboundLine.getPreInboundNo(),
                    preInboundLine.getLineNo(), preInboundLine.getWarehouseId());
            preInboundLine.setReferenceField5(quantity != null ? quantity.toString() : null);
        });
        return preInboundLines;
    }

    /**
     * createPreInboundLine
     *
     * @param newPreInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntityV2 createPreInboundLineV2(PreInboundLineEntityV2 newPreInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        PreInboundLineEntityV2 dbPreInboundLine = new PreInboundLineEntityV2();
        log.info("newPreInboundLine : " + newPreInboundLine);
        BeanUtils.copyProperties(newPreInboundLine, dbPreInboundLine, CommonUtils.getNullPropertyNames(newPreInboundLine));
        dbPreInboundLine.setDeletionIndicator(0L);
        dbPreInboundLine.setCreatedBy(loginUserID);
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setCreatedOn(new Date());
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineV2Repository.save(dbPreInboundLine);
    }

    /**
     * createPreInboundLine
     *
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param lineNo
     * @param loginUserID
     * @return
     */
    public List<PreInboundLineEntityV2> createPreInboundLineV2(String companyCode, String plantId, String languageId,
                                                               String preInboundNo, String warehouseId, String refDocNumber,
                                                               String itemCode, Long lineNo, String loginUserID) {
        try {
            // Query PreInboundLine
            PreInboundLineEntityV2 preInboundLineEntity = getPreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);

            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            BomHeader bomHeader = mastersService.getBomHeader(itemCode, warehouseId, authTokenForMastersService.getAccess_token());
            if (bomHeader != null) {
                BomLine[] bomLine =
                        mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(), authTokenForMastersService.getAccess_token());
                List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
                for (BomLine dbBomLine : bomLine) {
                    toBeCreatedPreInboundLineList.add(createPreInboundLineBOMBasedV2(companyCode,
                            plantId, languageId, preInboundNo, refDocNumber, warehouseId, lineNo, itemCode, dbBomLine,
                            preInboundLineEntity, loginUserID));
                }

                // Batch Insert - PreInboundLines
                if (!toBeCreatedPreInboundLineList.isEmpty()) {
                    List<PreInboundLineEntityV2> createdPreInboundLineList = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                    log.info("createdPreInboundLineList [BOM] : " + createdPreInboundLineList);

                    List<InboundLineV2> toBeCreatedInboundLineList = new ArrayList<>();
                    for (PreInboundLineEntityV2 createdPreInboundLine : createdPreInboundLineList) {
                        log.info("PreInboundLine---------> : " + createdPreInboundLine);
                        InboundLineV2 inboundLine = new InboundLineV2();
                        BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

                        // OrdQty
                        inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());

                        // OrdUOM
                        inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());

                        // IB_order_type_id
                        inboundLine.setInboundOrderTypeId(createdPreInboundLine.getInboundOrderTypeId());

                        // StatusId
                        inboundLine.setStatusId(createdPreInboundLine.getStatusId());

                        // MFR_PART
                        inboundLine.setManufacturerPartNo(createdPreInboundLine.getManufacturerPartNo());
                        inboundLine.setDescription(createdPreInboundLine.getItemDescription());
                        inboundLine.setBusinessPartnerCode(createdPreInboundLine.getBusinessPartnerCode());
                        inboundLine.setDeletionIndicator(0L);
                        inboundLine.setCreatedBy(createdPreInboundLine.getCreatedBy());
                        inboundLine.setCreatedOn(createdPreInboundLine.getCreatedOn());
                        toBeCreatedInboundLineList.add(inboundLine);
                    }

                    List<InboundLineV2> createdInboundLine = inboundLineV2Repository.saveAll(toBeCreatedInboundLineList);
                    log.info("createdInboundLine : " + createdInboundLine);
                    return createdPreInboundLineList;
                }
            } else {
                throw new BadRequestException("There is No Bom for this item.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.toString());
        }
        return null;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param preInboundNo
     * @param refDocNumber
     * @param warehouseId
     * @param lineNo
     * @param itemCode
     * @param bomLine
     * @param preInboundLineEntity
     * @param loginUserID
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(String companyCode, String plantId, String languageId,
                                                                  String preInboundNo, String refDocNumber,
                                                                  String warehouseId, Long lineNo, String itemCode,
                                                                  BomLine bomLine, PreInboundLineEntityV2 preInboundLineEntity, String loginUserID) throws ParseException {
        Warehouse warehouse = getWarehouse(warehouseId, companyCode, plantId, languageId);

        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
        BeanUtils.copyProperties(preInboundLineEntity, preInboundLine, CommonUtils.getNullPropertyNames(preInboundLineEntity));

        preInboundLine.setLanguageId(languageId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setRefDocNumber(refDocNumber);
        preInboundLine.setInboundOrderTypeId(preInboundLineEntity.getInboundOrderTypeId());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(lineNo);

        // ITM_CODE
        preInboundLine.setItemCode(bomLine.getChildItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(itemCode, languageId, companyCode, plantId, warehouseId, authTokenForMastersService.getAccess_token());
        preInboundLine.setItemDescription(imBasicData1.getDescription());

        // MFR
        preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());

        // ORD_QTY
        double orderQuantity = preInboundLineEntity.getOrderQty() * bomLine.getChildItemQuantity();
        preInboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preInboundLine.setOrderUom(preInboundLineEntity.getOrderUom());

        // EA_DATEpreInboundLineEntity
        preInboundLine.setExpectedArrivalDate(preInboundLineEntity.getExpectedArrivalDate());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preInboundLine.setStatusId(6L);

        // REF_FIELD_1
        preInboundLine.setReferenceField1("BOM");

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy(loginUserID);
        preInboundLine.setCreatedOn(new Date());
        return preInboundLine;
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param updatePreInboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntityV2 updatePreInboundLineV2(String companyCode, String plantId, String languageId,
                                                         String preInboundNo, String warehouseId,
                                                         String refDocNumber, Long lineNo, String itemCode,
                                                         PreInboundLineEntityV2 updatePreInboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundLineEntityV2 dbPreInboundLine = getPreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        BeanUtils.copyProperties(updatePreInboundLine, dbPreInboundLine, CommonUtils.getNullPropertyNames(updatePreInboundLine));
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineV2Repository.save(dbPreInboundLine);
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundLineEntityV2 updatePreInboundLineV2(String companyCode, String plantId, String languageId,
                                                         String preInboundNo, String warehouseId,
                                                       String refDocNumber, Long lineNo, String itemCode, Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreInboundLineEntityV2 dbPreInboundLine = getPreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        dbPreInboundLine.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
        dbPreInboundLine.setStatusDescription(statusDescription);
        dbPreInboundLine.setUpdatedBy(loginUserID);
        dbPreInboundLine.setUpdatedOn(new Date());
        return preInboundLineV2Repository.save(dbPreInboundLine);
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<PreInboundLineEntityV2> preInboundLines = getPreInboundLinesV2();
        preInboundLines.stream().forEach(preiblines -> preiblines.setReferenceField1(asnNumber));
        preInboundLineV2Repository.saveAll(preInboundLines);
    }

    /**
     *
     * @param searchPreInboundLine
     * @return
     * @throws ParseException
     */
    public List<PreInboundLineOutputV2> findPreInboundLineV2(SearchPreInboundLineV2 searchPreInboundLine) throws ParseException {
        if (searchPreInboundLine.getStartCreatedOn() != null && searchPreInboundLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundLine.getStartCreatedOn(), searchPreInboundLine.getEndCreatedOn());
            searchPreInboundLine.setStartCreatedOn(dates[0]);
            searchPreInboundLine.setEndCreatedOn(dates[1]);
        }

        if (searchPreInboundLine.getStartRefDocDate() != null && searchPreInboundLine.getEndRefDocDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundLine.getStartRefDocDate(), searchPreInboundLine.getEndRefDocDate());
            searchPreInboundLine.setStartRefDocDate(dates[0]);
            searchPreInboundLine.setEndRefDocDate(dates[1]);
        }

        PreInboundLineV2Specification spec = new PreInboundLineV2Specification(searchPreInboundLine);
        List<PreInboundLineEntityV2> results = preInboundLineV2Repository.findAll(spec);
        List<PreInboundLineOutputV2> preInboundLineOutputList = new ArrayList<>();
        if(results != null && !results.isEmpty()) {
            for(PreInboundLineEntityV2 preInboundLine : results) {
                PreInboundLineOutputV2 newPreInboundLineOutput = new PreInboundLineOutputV2();
                BeanUtils.copyProperties(preInboundLine, newPreInboundLineOutput, CommonUtils.getNullPropertyNames(preInboundLine));
                List<InventoryDetail> inventoryDetails = new ArrayList<>();
                SearchInventoryV2 searchInventoryV2 = getSearchInventoryV2(preInboundLine);
                List<IInventoryImpl> inventoryList = inventoryService.findInventoryNewV2(searchInventoryV2);
                if(inventoryList != null && !inventoryList.isEmpty()) {
                    for (IInventoryImpl inventory : inventoryList) {
                        InventoryDetail inventoryDetail = new InventoryDetail();
                        inventoryDetail.setStorageBin(inventory.getStorageBin());
                        inventoryDetail.setInventoryQty(inventory.getReferenceField4());
                        inventoryDetails.add(inventoryDetail);
                    }
                }
                newPreInboundLineOutput.setInventoryDetail(inventoryDetails);
                preInboundLineOutputList.add(newPreInboundLineOutput);
                log.info("PreInboundLine: " + newPreInboundLineOutput);
            }
        }
//		log.info("results: " + results);
        return preInboundLineOutputList;
    }

    /**
     *
     * @param preInboundLine
     * @return
     */
    private static SearchInventoryV2 getSearchInventoryV2(PreInboundLineEntityV2 preInboundLine) {
        SearchInventoryV2 searchInventoryV2 = new SearchInventoryV2();
        searchInventoryV2.setCompanyCodeId(Collections.singletonList(preInboundLine.getCompanyCode()));
        searchInventoryV2.setPlantId(Collections.singletonList(preInboundLine.getPlantId()));
        searchInventoryV2.setLanguageId(Collections.singletonList(preInboundLine.getLanguageId()));
        searchInventoryV2.setWarehouseId(Collections.singletonList(preInboundLine.getWarehouseId()));
        searchInventoryV2.setItemCode(Collections.singletonList(preInboundLine.getItemCode()));
        searchInventoryV2.setManufacturerName(Collections.singletonList(preInboundLine.getManufacturerName()));
        List<Long> binClassIdList = new ArrayList<>();
        binClassIdList.add(1L);
        binClassIdList.add(7L);
        searchInventoryV2.setBinClassId(binClassIdList);
        return searchInventoryV2;
    }

    /**
     * deletePreInboundLine
     *
     * @param loginUserID
     * @param lineNo
     */
    public void deletePreInboundLineV2(String companyCode, String plantId, String languageId,
                                       String preInboundNo, String warehouseId,
                                       String refDocNumber, Long lineNo, String itemCode, String loginUserID) throws ParseException {
        PreInboundLineEntityV2 preInboundLine = getPreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
        if (preInboundLine != null && preInboundLine.getStatusId() == 6L) {
            preInboundLine.setDeletionIndicator(1L);
            preInboundLine.setUpdatedBy(loginUserID);
            preInboundLine.setUpdatedOn(new Date());
            preInboundLineV2Repository.save(preInboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preInboundNo);
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    //DELETE
    public List<PreInboundLineEntityV2> deletePreInboundLine(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) throws ParseException {
        List<PreInboundLineEntityV2> preInboundLineEntityV2List = new ArrayList<>();
        List<PreInboundLineEntityV2> preInboundLineList =
                preInboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        log.info("preInboundLineList - Cancellation : " + preInboundLineList);
        if(preInboundLineList != null && !preInboundLineList.isEmpty()) {
            for (PreInboundLineEntityV2 updatePreInboundLine : preInboundLineList) {
                updatePreInboundLine.setDeletionIndicator(1L);
                updatePreInboundLine.setUpdatedBy(loginUserID);
                updatePreInboundLine.setUpdatedOn(new Date());
                PreInboundLineEntityV2 preInboundLineEntityV2 = preInboundLineV2Repository.save(updatePreInboundLine);
                preInboundLineEntityV2List.add(preInboundLineEntityV2);
            }
        }
        return preInboundLineEntityV2List;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    public List<PreInboundLineEntityV2> cancelPreInboundLine(String companyCode, String plantId, String languageId, String warehouseId,
                                                             String refDocNumber, String preInboundNo, String loginUserID) {
        List<PreInboundLineEntityV2> preInboundLineEntityV2List = new ArrayList<>();
        List<PreInboundLineEntityV2> preInboundLineList =
                preInboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("preInboundLineList - Order Cancellation : " + preInboundLineList);
        if(preInboundLineList != null && !preInboundLineList.isEmpty()) {
            for (PreInboundLineEntityV2 updatePreInboundLine : preInboundLineList) {
                updatePreInboundLine.setStatusId(96L);
                statusDescription = stagingLineV2Repository.getStatusDescription(96L, languageId);
                updatePreInboundLine.setStatusDescription(statusDescription);
                updatePreInboundLine.setUpdatedBy(loginUserID);
                updatePreInboundLine.setUpdatedOn(new Date());
                PreInboundLineEntityV2 preInboundLineEntityV2 = preInboundLineV2Repository.save(updatePreInboundLine);
                preInboundLineEntityV2List.add(preInboundLineEntityV2);
            }
        }
        return preInboundLineEntityV2List;
    }
}

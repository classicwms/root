package com.tekclover.wms.api.inbound.orders.service;


import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockReceiptServiceV2 extends BaseService {


    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    MastersService mastersService;

    @Autowired
    StorageBinRepository storageBinRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    GrLineService grLineService;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;






    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PickupLineV2 getPickupLineForLastBinCheck(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String itemCode, String manufacturerName) {
        String directReceiptStorageBin = "REC-AL-B2";   //storage-bin excluding direct stock receipt bin
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L, directReceiptStorageBin);
        if (pickupLine != null) {
            return pickupLine;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PutAwayLineV2 getPutAwayLineExistingItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String itemCode, String manufacturerName) {
        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.
                findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, 0L);
        if (dbPutawayLine != null) {
            return dbPutawayLine;
        }
        return null;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<PutAwayHeaderV2> getPutawayHeaderExistingBinItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName) {
        List<PutAwayHeaderV2> dbPutAwayHeader = putAwayHeaderV2Repository.
                findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 19L, 0L);
        if (dbPutAwayHeader != null) {
            return dbPutAwayHeader;
        }
        return null;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public List<String> getPutawayHeaderExistingBinCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId) {
        List<PutAwayHeaderV2> dbPutAwayHeader = putAwayHeaderV2Repository.
                findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicator(
                        companyCodeId, plantId, warehouseId, languageId, 19L, 0L);
        if (dbPutAwayHeader != null) {
            List<String> storageBin = dbPutAwayHeader.stream().map(PutAwayHeaderV2::getProposedStorageBin).collect(Collectors.toList());
            return storageBin;
        }
        return null;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            // Exception log
//            createPreInboundHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
//                    "PreInboundHeaderV2 with given values doesn't exists - " + refDocNumber);
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                    ", warehouseId: " + warehouseId + " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param preInboundHeaderEntity
     * @return
     */
    private PreInboundHeaderV2 getPreInboundLineItemsV2(PreInboundHeaderEntityV2 preInboundHeaderEntity) {
        PreInboundHeaderV2 header = new PreInboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
        List<PreInboundLineEntityV2> lineEntityList = preInboundLineV2Repository.findByWarehouseIdAndPreInboundNoAndDeletionIndicator(
                preInboundHeaderEntity.getWarehouseId(), preInboundHeaderEntity.getPreInboundNo(), 0L);
        log.info("lineEntityList : " + lineEntityList);

        if (!lineEntityList.isEmpty()) {
            header.setPreInboundLineV2(lineEntityList);
        }
        return header;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param refDocNumber
     * @return
     */
    public List<PutAwayHeaderV2> getPutAwayHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<Long> statusIds = Arrays.asList(19L, 20L);
        List<PutAwayHeaderV2> putAwayHeader =
                putAwayHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        statusIds,
                        0L
                );
        if (putAwayHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV2(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {

                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
//                PutAwayHeaderV2 dbPutAwayHeader = new PutAwayHeaderV2();

                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();

                Double cbmPerQuantity = 0D;
                Double cbm = 0D;
                Double allocatedVolume = 0D;
                Double occupiedVolume = 0D;
                Double remainingVolume = 0D;
                Double totalVolume = 0D;

                Double allocateQty = 0D;
                Double orderedQty = 0D;
                Double differenceQty = 0D;
                Double assignedProposedBinVolume = 0D;

                Long statusId = 0L;

                boolean capacityCheck = false;
                boolean storageBinCapacityCheck = false;

//                ImBasicData imBasicData = new ImBasicData();
//                imBasicData.setCompanyCodeId(companyCode);
//                imBasicData.setPlantId(plantId);
//                imBasicData.setLanguageId(languageId);
//                imBasicData.setWarehouseId(warehouseId);
//                imBasicData.setItemCode(itemCode);
//                imBasicData.setManufacturerName(newPutAwayLine.getManufacturerName());
//                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData,
//                        authTokenForMastersService.getAccess_token());
//                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

//                if (itemCodeCapacityCheck != null) {
//                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
//                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
//                        log.info("capacity Check: " + capacityCheck);
//                    }
//                }

                String confirmedStorageBin = newPutAwayLine.getConfirmedStorageBin();
                String proposedStorageBin = newPutAwayLine.getProposedStorageBin();
                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", "
                        + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 storageBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId,
                        warehouseId, newPutAwayLine.getConfirmedStorageBin());
                StorageBinV2 proposedBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId,
                        warehouseId, newPutAwayLine.getProposedStorageBin());

                PutAwayHeaderV2 findPutawayHeader = getPutawayHeaderV2(companyCode, plantId,
                        warehouseId, languageId, newPutAwayLine.getPutAwayNumber());
                List<PutAwayLineV2> findPutawayLine = getPutAwayLineV2ForPutawayConfirm(companyCode, plantId,
                        languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber());

                if (storageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(storageBin.getFloorId()));
                    if (storageBin.isCapacityCheck()) {
                        storageBinCapacityCheck = storageBin.isCapacityCheck();
                        log.info("confirmed storageBinCapacityCheck: " + storageBinCapacityCheck);
                    }
                }

                if (capacityCheck && !storageBinCapacityCheck) {
                    throw new BadRequestException(
                            "Selected Bin is not under Capacity Check. Kindly Select a Capacity Enabled Bin!");
                }
                if (!capacityCheck && storageBinCapacityCheck) {
                    throw new BadRequestException(
                            "Selected ItemCode is not under Capacity Check. Kindly Select a Capacity Enabled Item!");
                }

                if (capacityCheck && storageBinCapacityCheck) {

                    if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin != proposedBin: " + confirmedStorageBin + ", "
                                + proposedStorageBin);

                        if (newPutAwayLine.getCbmQuantity() != null) {
                            cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                        }
                        if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                            cbm = Double.valueOf(newPutAwayLine.getCbm());
                        }
                        if (storageBin.getTotalVolume() != null && storageBin.getTotalVolume() != "") {
                            totalVolume = Double.valueOf(storageBin.getTotalVolume());
                        }
                        if (storageBin.getAllocatedVolume() != null) {
                            allocatedVolume = Double.valueOf(storageBin.getAllocatedVolume());
                        }
                        if (storageBin.getOccupiedVolume() != null && storageBin.getOccupiedVolume() != "") {
                            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
                        }
                        if (storageBin.getRemainingVolume() != null && storageBin.getRemainingVolume() != "") {
                            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                        }

                        if (remainingVolume <= 0) {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocateQty = newPutAwayLine.getPutawayConfirmedQty();

                        if (remainingVolume < cbmPerQuantity) {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocatedVolume = allocateQty * cbmPerQuantity;
                        if (allocatedVolume <= remainingVolume) {
                            allocatedVolume = allocateQty * cbmPerQuantity;
                        } else {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }
                        if (totalVolume >= remainingVolume) {
                            remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                        } else {
                            remainingVolume = remainingVolume - allocatedVolume;
                        }
                        occupiedVolume = occupiedVolume + allocatedVolume;

                        log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                        if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                && remainingVolume.equals(totalVolume)) {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                    + remainingVolume + "," + totalVolume);
                            statusId = 0L;
                            log.info("StorageBin Emptied");
                        } else {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                    + remainingVolume + "," + totalVolume);
                            statusId = 1L;
                            log.info("StorageBin Occupied");
                        }

                        // confirmed Bin volume update
//                        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
//                                newPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, languageId, warehouseId,
//                                statusId, loginUserID, authTokenForMastersService.getAccess_token());

                        if (findPutawayLine == null) {
                            // proposed Bin revert volume update done during putaway header create
                            remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
//                            allocatedVolume = proposedBin.getAllocatedVolume();
                            occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            log.info("proposed Bin before confirm remainingVolume, occupiedVolume: " + remainingVolume
                                    + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + allocatedVolume;
                            occupiedVolume = occupiedVolume - allocatedVolume;

                            log.info("proposed bin after confirm remainingVolume, occupiedVolume: " + remainingVolume
                                    + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                    && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

//                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
//                                    newPutAwayLine.getProposedStorageBin(), companyCode, plantId, languageId,
//                                    warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());
                        }

                        log.info("Storage Bin occupied volume got updated");

                    }
                    if (confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info(
                                "confirmedStorageBin == proposedBin" + confirmedStorageBin + ", " + proposedStorageBin);

                        if (findPutawayHeader.getPutAwayQuantity() > newPutAwayLine.getPutawayConfirmedQty()) {
                            log.info("putAwayQty > confirmQty" + findPutawayHeader.getPutAwayQuantity() + ", "
                                    + newPutAwayLine.getPutawayConfirmedQty());

                            if (newPutAwayLine.getCbmQuantity() != null) {
                                cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                            }
                            if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                                cbm = Double.valueOf(newPutAwayLine.getCbm());
                            }
                            if (proposedBin.getTotalVolume() != null && proposedBin.getTotalVolume() != "") {
                                totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            }
                            if (proposedBin.getAllocatedVolume() != null) {
                                allocatedVolume = Double.valueOf(proposedBin.getAllocatedVolume());
                            }
                            if (proposedBin.getOccupiedVolume() != null && proposedBin.getOccupiedVolume() != "") {
                                occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            }
                            if (proposedBin.getRemainingVolume() != null && proposedBin.getRemainingVolume() != "") {
                                remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
                            }

                            allocateQty = newPutAwayLine.getPutawayConfirmedQty();
                            if (newPutAwayLine.getOrderQty() != null) {
                                orderedQty = newPutAwayLine.getOrderQty();
                            }
                            log.info("allocateQty(confirmed PutawayQty), putawayQty, orderQty: " + allocateQty + ", "
                                    + findPutawayHeader.getPutAwayQuantity() + ", " + orderedQty);

                            assignedProposedBinVolume = findPutawayHeader.getPutAwayQuantity() * cbmPerQuantity;
                            allocatedVolume = allocateQty * cbmPerQuantity;

                            log.info("assignedProposedBinVolume, allocatedVolume: " + assignedProposedBinVolume + ", "
                                    + allocatedVolume);

                            remainingVolume = remainingVolume + assignedProposedBinVolume - allocatedVolume;
                            occupiedVolume = occupiedVolume - assignedProposedBinVolume + allocatedVolume;

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                    && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            // confirmed Bin volume update
//                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
//                                    newPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, languageId,
//                                    warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                            log.info("Storage Bin occupied volume got updated");

                        }
                    }
                }

                // V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId,
                        warehouseId);

                newPutAwayLine.setCompanyDescription(description.getCompanyDesc());
                newPutAwayLine.setPlantDescription(description.getPlantDesc());
                newPutAwayLine.setWarehouseDescription(description.getWarehouseDesc());

                StagingLineEntityV2 dbStagingLineEntity = getStagingLineForPutAwayLineV2(companyCode,
                        plantId, languageId, warehouseId, newPutAwayLine.getPreInboundNo(),
                        newPutAwayLine.getRefDocNumber(), newPutAwayLine.getLineNo(), itemCode,
                        newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    if (newPutAwayLine.getManufacturerFullName() != null) {
                        newPutAwayLine.setManufacturerFullName(newPutAwayLine.getManufacturerFullName());
                    } else {
                        newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    }
                    if (newPutAwayLine.getMiddlewareId() != null) {
                        newPutAwayLine.setMiddlewareId(newPutAwayLine.getMiddlewareId());
                    } else {
                        newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    }
                    if (newPutAwayLine.getMiddlewareHeaderId() != null) {
                        newPutAwayLine.setMiddlewareHeaderId(newPutAwayLine.getMiddlewareHeaderId());
                    } else {
                        newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    }
                    if (newPutAwayLine.getMiddlewareTable() != null) {
                        newPutAwayLine.setMiddlewareTable(newPutAwayLine.getMiddlewareTable());
                    } else {
                        newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    }
                    if (newPutAwayLine.getPurchaseOrderNumber() != null) {
                        newPutAwayLine.setPurchaseOrderNumber(newPutAwayLine.getPurchaseOrderNumber());
                    } else {
                        newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    }
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                }

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine,
                        CommonUtils.getNullPropertyNames(newPutAwayLine));
                dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());

                dbPutAwayLine.setBranchCode(newPutAwayLine.getBranchCode());
                dbPutAwayLine.setTransferOrderNo(newPutAwayLine.getTransferOrderNo());
                dbPutAwayLine.setIsCompleted(newPutAwayLine.getIsCompleted());

                dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                dbPutAwayLine.setStatusId(20L);
                String statusDescription = stagingLineV2Repository.getStatusDescription(20L,
                        newPutAwayLine.getLanguageId());
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setPackBarcodes(newPutAwayLine.getPackBarcodes());
                dbPutAwayLine.setBarcodeId(newPutAwayLine.getBarcodeId());
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                log.info("putawayHeader: " + findPutawayHeader);
                if (findPutawayHeader != null) {
                    dbPutAwayLine.setCreatedOn(findPutawayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(findPutawayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(findPutawayHeader.getInboundOrderTypeId());
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository
                        .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                                newPutAwayLine.getLanguageId(), newPutAwayLine.getCompanyCode(),
                                newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId(),
                                newPutAwayLine.getGoodsReceiptNo(), newPutAwayLine.getPreInboundNo(),
                                newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber(),
                                newPutAwayLine.getLineNo(), newPutAwayLine.getItemCode(),
                                newPutAwayLine.getProposedStorageBin(),
                                Arrays.asList(newPutAwayLine.getConfirmedStorageBin()),
                                newPutAwayLine.getDeletionIndicator());

                log.info("Existing putawayline already created : " + existingPutAwayLine);

                if (existingPutAwayLine.isEmpty()) {

                    try {
                        String leadTime = putAwayLineV2Repository.getleadtime(companyCode, plantId, languageId,
                                warehouseId, newPutAwayLine.getPutAwayNumber(), new Date());
                        dbPutAwayLine.setReferenceField1(leadTime);
                        log.info("LeadTime: " + leadTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);

                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);

//                    boolean isInventoryCreated = false;
//                    boolean isInventoryMovemoentCreated = false;

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        /*
                         * Commenting out Inventory creation alone
                         */
//                        InventoryV2 inventory = new InventoryV2();
//                        BeanUtils.copyProperties(createdPutAwayLine, inventory, CommonUtils.getNullPropertyNames(createdPutAwayLine));
//                        inventory.setInventoryId(System.currentTimeMillis());
//                        inventory.setCompanyCodeId(createdPutAwayLine.getCompanyCode());
//                        inventory.setVariantCode(1L);                // VAR_ID
//                        inventory.setVariantSubCode("1");            // VAR_SUB_ID
//                        inventory.setStorageMethod("1");            // STR_MTD
//                        inventory.setBatchSerialNumber("1");        // STR_NO
//                        inventory.setBatchSerialNumber(newPutAwayLine.getBatchSerialNumber());
//                        inventory.setStorageBin(createdPutAwayLine.getConfirmedStorageBin());
//                        inventory.setBarcodeId(createdPutAwayLine.getBarcodeId());

                        // v2 code
//                        if (createdPutAwayLine.getCbm() == null) {
//                            createdPutAwayLine.setCbm("0");
//                        }
//                        if (createdPutAwayLine.getCbmQuantity() == null) {
//                            createdPutAwayLine.setCbmQuantity(0D);
//                        }
//                        inventory.setCbmPerQuantity(String.valueOf(Double.valueOf(createdPutAwayLine.getCbm()) / createdPutAwayLine.getCbmQuantity()));
//                        inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));

//                        Double invQty = 0D;
//                        Double cbmPerQty = 0D;
//                        Double invCbm = 0D;
//
//                        log.info("CapacityCheck for Create Inventory-----------> : " + capacityCheck);

//                        if (capacityCheck) {
//                            if (createdPutAwayLine.getCbmQuantity() != null) {
//                                inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));
//                            }
//                            if (createdPutAwayLine.getPutawayConfirmedQty() != null) {
//                                invQty = createdPutAwayLine.getPutawayConfirmedQty();
//                            }
//                            if (createdPutAwayLine.getCbmQuantity() == null) {
//
//                                if (createdPutAwayLine.getCbm() != null) {
//                                    cbm = Double.valueOf(createdPutAwayLine.getCbm());
//                                }
//                                cbmPerQty = cbm / invQty;
//                                inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
//                            }
//                            if (createdPutAwayLine.getCbm() != null) {
//                                invCbm = Double.valueOf(createdPutAwayLine.getCbm());
//                            }
//                            if (createdPutAwayLine.getCbm() == null) {
//                                invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
//                            }
//                            inventory.setCbm(String.valueOf(invCbm));
//                        }

                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
                        storageBinPutAway.setPlantId(dbPutAwayLine.getPlantId());
                        storageBinPutAway.setLanguageId(dbPutAwayLine.getLanguageId());
                        storageBinPutAway.setWarehouseId(dbPutAwayLine.getWarehouseId());
                        storageBinPutAway.setBin(dbPutAwayLine.getConfirmedStorageBin());

                        StorageBinV2 dbStorageBin = null;
                        try {
                            dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway,
                                    authTokenForMastersService.getAccess_token());
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }

//                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
//                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
//                            inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
//                            inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
//                        }
//                        if (itemCodeCapacityCheck != null) {
//                            inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
//                            inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
//                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        }

//                        if (dbStorageBin != null) {
//                            inventory.setBinClassId(dbStorageBin.getBinClassId());
//                            inventory.setReferenceField10(dbStorageBin.getStorageSectionId());
//                            inventory.setReferenceField5(dbStorageBin.getAisleNumber());
//                            inventory.setReferenceField6(dbStorageBin.getShelfId());
//                            inventory.setReferenceField7(dbStorageBin.getRowId());
//                            inventory.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
//                        }

//                        inventory.setCompanyDescription(dbPutAwayLine.getCompanyDescription());
//                        inventory.setPlantDescription(dbPutAwayLine.getPlantDescription());
//                        inventory.setWarehouseDescription(dbPutAwayLine.getWarehouseDescription());

                        /*
                         * Insert PA_CNF_QTY value in this field. Also Pass
                         * WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table and fetch
                         * ST_BIN/INV_QTY value. Update INV_QTY value by (INV_QTY - PA_CNF_QTY) . If
                         * this value becomes Zero, then delete the record"
                         */
//                        try {
//                            InventoryV2 existinginventory = inventoryService.getInventory(
//                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
//                                    createdPutAwayLine.getWarehouseId(),
//                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
//                                    createdPutAwayLine.getManufacturerName(), 3L);
//
////                            IInventoryImpl existinginventory = inventoryService.getInventoryforExistingBin(
////                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
////                                    createdPutAwayLine.getWarehouseId(),
////                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
////                                    createdPutAwayLine.getManufacturerName(), 3L);
//
//                            double INV_QTY = existinginventory.getInventoryQuantity() - createdPutAwayLine.getPutawayConfirmedQty();
//                            log.info("INV_QTY : " + INV_QTY);
//
////                            inventory.setPalletCode(existinginventory.getPalletCode());
////                            inventory.setCaseCode(existinginventory.getCaseCode());
////                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//
////                            if (capacityCheck) {
////                                if (existinginventory.getCbm() != null && createdPutAwayLine.getCbm() != null) {
////                                    invCbm = Double.valueOf(existinginventory.getCbm()) - Double.valueOf(createdPutAwayLine.getCbm());
////                                    log.info("INV_CBM: " + invCbm);
////                                }
////                                if (invCbm >= 0) {
////                                    existinginventory.setCbm(String.valueOf(invCbm));
////                                }
////                            }
//                            if (INV_QTY >= 0) {
////                                existinginventory.setInventoryQuantity(INV_QTY);
////                                InventoryV2 updatedInventory = inventoryV2Repository.save(existinginventory);
////                                log.info("updatedInventory--------> : " + updatedInventory);
//                                InventoryV2 inventory2 = new InventoryV2();
//                                BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                                stockTypeDesc = getStockTypeDesc(createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
//                                inventory2.setStockTypeDescription(stockTypeDesc);
//                                inventory2.setInventoryQuantity(INV_QTY);
//                                inventory2.setInventoryId(System.currentTimeMillis());
//                                InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                                log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//                            }
//
//                        } catch (Exception e) {
//                            log.info("Existing Inventory---Error-----> : " + e.toString());
//                        }

                        // INV_QTY
//                        inventory.setInventoryQuantity(createdPutAwayLine.getPutawayConfirmedQty());

//                        inventory.setStockTypeId(10L);
//                        stockTypeDesc = getStockTypeDesc(dbPutAwayLine.getCompanyCode(),dbPutAwayLine.getPlantId(),dbPutAwayLine.getLanguageId(),dbPutAwayLine.getWarehouseId(),10L);
//                        inventory.setStockTypeDescription(stockTypeDesc);

                        // INV_UOM
//                        inventory.setInventoryUom(createdPutAwayLine.getPutAwayUom());
//                        inventory.setReferenceDocumentNo(createdPutAwayLine.getRefDocNumber());
//                        inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        inventory.setCreatedBy(createdPutAwayLine.getCreatedBy());
//                        inventory.setCreatedOn(createdPutAwayLine.getCreatedOn());
                        // InventoryV2 createdInventory = inventoryV2Repository.save(inventory);
//                        log.info("createdInventory : " + createdInventory);
//                        log.info("createdInventory BinClassId : " + createdInventory.getBinClassId());
//
//                        if (createdInventory != null) {
//                            isInventoryCreated = true;
//                        }

                        /* Insert a record into INVENTORYMOVEMENT table */
//                        InventoryMovement createdInventoryMovement = createInventoryMovementV2(createdPutAwayLine);
//                        log.info("inventoryMovement created: " + createdInventoryMovement);
//                        log.info("inventoryMovement created binClassId: " + createdInventory.getBinClassId());

//                        if (createdInventoryMovement != null) {
//                            isInventoryMovemoentCreated = true;
//                        }

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
                                dbPutAwayLine.getCompanyCode(), dbPutAwayLine.getPlantId(),
                                dbPutAwayLine.getLanguageId(), dbPutAwayLine.getWarehouseId(), loginUserID,
                                authTokenForMastersService.getAccess_token());

//                        if (isInventoryCreated && isInventoryMovemoentCreated) {
//                        if (isInventoryMovemoentCreated) {
                        PutAwayHeaderV2 putAwayHeader = getPutAwayHeaderV2ForPutAwayLine(
                                createdPutAwayLine.getWarehouseId(), createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPutAwayNumber(),
                                createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId());

                        confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                        proposedStorageBin = putAwayHeader.getProposedStorageBin();
                        if (putAwayHeader != null) {
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty()
                                    + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            log.info("PutawayHeader StatusId : 20");
                            statusDescription = stagingLineV2Repository.getStatusDescription(
                                    putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated: " + putAwayHeader);

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(
                                        createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                        createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                        createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                        createdPutAwayLine.getItemCode(), createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info(
                                        "tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                                + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                                + ", " + putAwayHeader.getPutAwayQuantity() + ", "
                                                + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }

                                // dbPutawayQty = SumOfPALineQty, dbAssignedPutawayQty (current conf. qty) =
                                // Ref.Field.2
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200")
                                            || putAwayHeader.getWarehouseId().equalsIgnoreCase("100"))
                                            && proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//                                        if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        log.info("New PutawayHeader Creation: ");
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader,
                                                CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId,
                                                languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber); // PutAway Number

                                        newPutAwayHeader
                                                .setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(
                                                    String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }
                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//                                    Double PUTAWAY_QTY = (putAwayHeader.getPutAwayQuantity() != null ? putAwayHeader.getPutAwayQuantity() : 0) - (createdPutAwayLine.getPutawayConfirmedQty() != null ? createdPutAwayLine.getPutawayConfirmedQty() : 0);
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");

                                        statusDescription = stagingLineV2Repository.getStatusDescription(
                                                newPutAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);
                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("1.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200")
                                            || putAwayHeader.getWarehouseId().equalsIgnoreCase("100"))
                                            && !proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//                                        if (!proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {

                                        // create new putaway header when partial putaway done and confirmed storage
                                        // bin set as proposed bin for new putaway header
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader,
                                                CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId,
                                                languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber);
                                        newPutAwayHeader.setProposedStorageBin(confirmedStorageBin);

                                        newPutAwayHeader
                                                .setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(
                                                    String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }

                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//											Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;

                                        log.info("---------PUTAWAY_QTY---------1---------> : "
                                                + createdPutAwayLine.getPutawayConfirmedQty());
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty
                                                - createdPutAwayLine.getPutawayConfirmedQty();
                                        log.info("---------PUTAWAY_QTY---------2---------> : " + PUTAWAY_QTY);

                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(
                                                putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);

                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("2.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                }
//                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE
                        // table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = getInboundLineV2(
                                createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());
                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null
                                    && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedAcceptQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Accept qty cannot be greater than order qty");
                            }
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null
                                    && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) { // condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setStatusId(20L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(20L,
                                createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId,
                    refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);
            return createdPutAwayLines;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // GetPutawayHeader
    public PutAwayHeaderV2 getPutawayHeaderV2(String companyId, String plantId, String warehouseId, String languageId, String putAwayNumber) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndDeletionIndicator(
                companyId, plantId, warehouseId, languageId, putAwayNumber, 0L);
        return dbPutAwayHeader;
    }

    // Get PutawayLine
    public List<PutAwayLineV2> getPutAwayLineV2ForPutawayConfirm(String companyCode, String plantId, String languageId,
                                                                 String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param remainingVolume
     * @param occupiedVolume
     * @param allocatedVolume
     * @param storageBin
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param authToken
     */
    public void updateStorageBin(Double remainingVolume, Double occupiedVolume, Double allocatedVolume,
                                 String storageBin, String companyCode, String plantId, String languageId,
                                 String warehouseId, Long statusId, String loginUserId, String authToken) {

        StorageBinV2 modifiedStorageBin = new StorageBinV2();
        modifiedStorageBin.setRemainingVolume(String.valueOf(remainingVolume));
        modifiedStorageBin.setAllocatedVolume(allocatedVolume);
        modifiedStorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
        modifiedStorageBin.setCapacityCheck(true);
        modifiedStorageBin.setStatusId(statusId);

        StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(storageBin,
                modifiedStorageBin,
                companyCode,
                plantId,
                languageId,
                warehouseId,
                loginUserId,
                authToken);

        if (updateStorageBinV2 != null) {
            log.info("Storage Bin Volume Updated successfully ");
        }
    }


    // Get-StagingLine
    public StagingLineEntityV2 getStagingLineForPutAwayLineV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preInboundNo,
                                                              String refDocNumber, Long lineNo, String itemCode, String manufacturerName) {
        Optional<StagingLineEntityV2> stagingLineV2 = stagingLineV2Repository
                .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        manufacturerName,
                        0L);
        if (stagingLineV2 == null || stagingLineV2.isEmpty()) {
            return null;
        }
        log.info("dbStagingLine: " + stagingLineV2.get());
        return stagingLineV2.get();
    }


    /**
     *
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderV2ForPutAwayLine(String warehouseId, String preInboundNo, String refDocNumber, String putAwayNumber,
                                                            String companyCodeId, String plantId, String languageId) {
        PutAwayHeaderV2 putAwayHeader =
                putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        0L
                );
        if (putAwayHeader == null) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @return
     */
    public InboundLineV2 getInboundLineV2(String companyCode, String plantId,
                                          String languageId, String warehouseId, String refDocNumber,
                                          String preInboundNo, Long lineNo, String itemCode) {
        Optional<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",temCode: " + itemCode + " doesn't exist.");
        }

        return inboundLine.get();
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutawayHeaderByStatusIdV2(String companyId, String plantId, String warehouseId,
                                             String preInboundNo, String refDocNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderV2Repository.getPutawayHeaderCountByStatusId(companyId, plantId, warehouseId, preInboundNo, refDocNumber);
        return putAwayHeaderStatusIdCount;
    }

    /**
     * PartialAllocation
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<InboundLineV2> getInboundLineForInboundConfirmPartialAllocationV2(String companyCode, String plantId, String languageId,
                                                                                  String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLineV2> inboundLines = inboundLineV2Repository.getInboundLinesV2ForInboundConfirm(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,   20L);
        log.info("inboundLine : " + inboundLines.size());
        return inboundLines;
    }


    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @param packBarcodes
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String refDocNumber, String itemCode, String manufacturerName,
                                                                 Long lineNumber, String preInboundNo, String packBarcodes) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        20L,
                        packBarcodes,
                        0L);
        if (putAwayLine == null) {
            return null;
        }
        return putAwayLine;
    }



    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    public AXApiResponse updateInboundHeaderPartialConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("DSR--->Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = getPutawayHeaderByStatusIdV2(companyCode, plantId,
                    warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException(
                        "Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: "
                                + refDocNumber);
            }

            AXApiResponse axapiResponse = new AXApiResponse();

            List<InboundLineV2> inboundLineList = getInboundLineForInboundConfirmPartialAllocationV2(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

            if (inboundLineList != null) {
                for (InboundLineV2 inboundLine : inboundLineList) {
                    List<GrLineV2> grLineList = grLineService.getGrLineForInboundConformV2(companyCode, plantId,
                            languageId, warehouseId, refDocNumber, inboundLine.getItemCode(),
                            inboundLine.getManufacturerName(), inboundLine.getLineNo(), inboundLine.getPreInboundNo());
                    log.info("GrLine List: " + grLineList.size());
                    for (GrLineV2 grLine : grLineList) {
                        List<PutAwayLineV2> putAwayLineList = getPutAwayLineForInboundConfirmV2(
                                companyCode, plantId, languageId, warehouseId, refDocNumber, grLine.getItemCode(),
                                grLine.getManufacturerName(), grLine.getLineNo(), grLine.getPreInboundNo(),
                                grLine.getPackBarcodes());
                        log.info("PutawayLine List: " + putAwayLineList.size());
                        if (putAwayLineList != null) {
                            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                boolean createdInventory = grLineService.createInventoryNonCBMV2(putAwayLine);
//                            createInventoryMovementV2(putAwayLine);
                            }
                            log.info("Inventory Created Successfully -----> for All Putaway Lines");
                        }
                    }
                }
            }
            statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);

            inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmProc(companyCode, plantId, languageId,
                    warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("InboundLine updated");

            putAwayLineV2Repository.updatePutawayLineStatusUpdateInboundConfirmProc(companyCode, plantId, languageId,
                    warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("putAwayLine updated");

            String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);

            // Multiple Stored Procedure replaced with Single Procedure Call
            inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId,
                    languageId, warehouseId, refDocNumber, preInboundNo, 24L, 17L, statusDescription,
                    statusDescription17, loginUserID, new Date());
            log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");

            Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository
                    .getInboundLinesV2CountForInboundConfirmWithStatusId(companyCode, plantId, languageId, warehouseId,
                            refDocNumber, preInboundNo, 24L);
            Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
                inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
            }
            if (inboundLinesV2CountForInboundConfirm == null) {
                inboundLinesV2CountForInboundConfirm = 0L;
            }
            boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId
                    .equals(inboundLinesV2CountForInboundConfirm);
            log.info("Inbound Line 24_StatusCount, Line Count: " + isConditionMet + ", "
                    + inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
            if (isConditionMet) {
                log.info("Inbound Line 24-------< :  " + isConditionMet);
                // Multiple Stored Procedure replaced with Single Procedure Call
                inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId,
                        warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                log.info("Header Status updated using stored procedure");
            }
            axapiResponse.setStatusCode("200"); // HardCoded
            axapiResponse.setMessage("Success"); // HardCoded
            log.info("axapiResponse: " + axapiResponse);
            return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation [DSR]: Exception ----> " + e.toString());
        }
    }
}

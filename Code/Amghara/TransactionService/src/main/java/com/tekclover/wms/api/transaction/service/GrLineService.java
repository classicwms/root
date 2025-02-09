package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.SearchGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.GrLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.GrLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class GrLineService extends BaseService {
    @Autowired
    private StorageBinRepository storageBinRepository;

    @Autowired
    private GrLineRepository grLineRepository;

    @Autowired
    private GrHeaderRepository grHeaderRepository;

    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private StagingLineRepository stagingLineRepository;

    @Autowired
    private GrHeaderService grHeaderService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    //----------------------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private PreInboundHeaderService preInboundHeaderService;

    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    private GrLineV2Repository grLineV2Repository;

    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PickupLineService pickupLineService;

    @Autowired
    private PutAwayLineService putAwayLineService;

    String statusDescription = null;

    @Autowired
    private ErrorLogRepository exceptionLogRepo;

    //----------------------------------------------------------------------------------------------------


    /**
     * getGrLines
     *
     * @return
     */
    public List<GrLine> getGrLines() {
        List<GrLine> grLineList = grLineRepository.findAll();
        grLineList = grLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return grLineList;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @return
     */
    public GrLine getGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                            String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode) {
        Optional<GrLine> grLine = grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
                getLanguageId(),
                getCompanyCode(),
                getPlantId(),
                warehouseId,
                preInboundNo,
                refDocNumber,
                goodsReceiptNo,
                palletCode,
                caseCode,
                packBarcodes,
                lineNo,
                itemCode,
                0L);
        if (grLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine.get();
    }

    /**
     * PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
     *
     * @param preInboundNo
     * @param refDocNumber
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<GrLine> getGrLine(String preInboundNo, String refDocNumber, String packBarcodes, Long lineNo, String itemCode) {
        List<GrLine> grLine =
                grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        preInboundNo,
                        refDocNumber,
                        packBarcodes,
                        lineNo,
                        itemCode,
                        0L);
        if (grLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @return
     */
    public List<GrLine> getGrLine(String refDocNumber, String packBarcodes, String warehouseId, String preInboundNo, String caseCode) {
        List<GrLine> grLine =
                grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        packBarcodes,
                        warehouseId,
                        preInboundNo,
                        caseCode,
                        0L);
        if (grLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLine> getGrLine(String refDocNumber, String packBarcodes) {
        List<GrLine> grLine =
                grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        packBarcodes,
                        0L);
        if (grLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist in GRLine.");
        }

        return grLine;
    }

    /**
     * @param searchGrLine
     * @return
     * @throws ParseException
     */
    public List<GrLine> findGrLine(SearchGrLine searchGrLine) throws ParseException {
        GrLineSpecification spec = new GrLineSpecification(searchGrLine);
        List<GrLine> results = grLineRepository.findAll(spec);
        return results;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<GrLine> getGrLineForUpdate(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo, String itemCode) {
        List<GrLine> grLine =
                grLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (grLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",warehouseId: " + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param acceptQty
     * @param damageQty
     * @param loginUserID
     * @return
     */
    public List<PackBarcode> generatePackBarcode(Long acceptQty, Long damageQty, String warehouseId, String loginUserID) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 6;
        List<PackBarcode> packBarcodes = new ArrayList<>();

        // Accept Qty
        if (acceptQty != 0) {
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, warehouseId, authTokenForIDMasterService.getAccess_token());
            PackBarcode acceptQtyPackBarcode = new PackBarcode();
            acceptQtyPackBarcode.setQuantityType("A");
            acceptQtyPackBarcode.setBarcode(nextRangeNumber);
            packBarcodes.add(acceptQtyPackBarcode);
        }

        // Damage Qty
        if (damageQty != 0) {
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, warehouseId, authTokenForIDMasterService.getAccess_token());
            PackBarcode damageQtyPackBarcode = new PackBarcode();
            damageQtyPackBarcode.setQuantityType("D");
            damageQtyPackBarcode.setBarcode(nextRangeNumber);
            packBarcodes.add(damageQtyPackBarcode);
        }
        return packBarcodes;
    }

    /**
     * createGrLine
     *
     * @param newGrLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<GrLine> createGrLine(@Valid List<AddGrLine> newGrLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<GrLine> createdGRLines = new ArrayList<>();
        try {
            String warehouseId = null;
            List<AddGrLine> dupGrLines = getDuplicates(newGrLines);
            log.info("-------dupGrLines--------> " + dupGrLines);
            if (dupGrLines != null && !dupGrLines.isEmpty()) {
                newGrLines.removeAll(dupGrLines);
                newGrLines.add(dupGrLines.get(0));
                log.info("-------GrLines---removed-dupPickupLines-----> " + newGrLines);
            }

            // Inserting multiple records
            for (AddGrLine newGrLine : newGrLines) {
                warehouseId = newGrLine.getWarehouseId();

                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLine dbGrLine = new GrLine();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("A-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    dbGrLine.setStatusId(17L);
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    List<GrLine> oldGrLine = grLineRepository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
                            dbGrLine.getGoodsReceiptNo(), dbGrLine.getItemCode(), dbGrLine.getLineNo(),
                            dbGrLine.getLanguageId(),
                            dbGrLine.getCompanyCode(),
                            dbGrLine.getPlantId(),
                            dbGrLine.getRefDocNumber(),
                            dbGrLine.getPackBarcodes(),
                            dbGrLine.getWarehouseId(),
                            dbGrLine.getPreInboundNo(),
                            dbGrLine.getCaseCode(),
                            0L
                    );
                    GrLine createdGRLine = null;

                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        createdGRLine = grLineRepository.save(dbGrLine);
                        log.info("createdGRLine : " + createdGRLine);
                        createdGRLines.add(createdGRLine);

                        if (createdGRLine != null) {
                            // Record Insertion in PUTAWAYHEADER table
                            createPutAwayHeader(createdGRLine, loginUserID);
                        }
                    }
                }
                log.info("Records were inserted successfully...");
            }

            // STATUS updates
            /*
             * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/GR_NO/IB_LINE_NO/ITM_CODE in GRLINE table and
             * validate STATUS_ID of the all the filtered line items = 17 , if yes
             */
            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
            StatusId idStatus = idmasterService.getStatus(17L, warehouseId, authTokenForIDService.getAccess_token());
            for (GrLine grLine : createdGRLines) {
                /*
                 * 1. Update GRHEADER table with STATUS_ID=17 by Passing WH_ID/GR_NO/CASE_CODE/REF_DOC_NO and
                 * GR_CNF_BY with USR_ID and GR_CNF_ON with Server time
                 */
                List<GrHeader> grHeaders = grHeaderService.getGrHeader(grLine.getWarehouseId(), grLine.getGoodsReceiptNo(),
                        grLine.getCaseCode(), grLine.getRefDocNumber());
                for (GrHeader grHeader : grHeaders) {
                    if (grHeader.getCompanyCode() == null) {
                        grHeader.setCompanyCode(getCompanyCode());
                    }
                    grHeader.setStatusId(17L);
                    grHeader.setReferenceField10(idStatus.getStatus());
                    grHeader.setCreatedBy(loginUserID);
                    grHeader.setCreatedOn(new Date());
                    grHeader = grHeaderRepository.save(grHeader);
                    log.info("grHeader updated: " + grHeader);
                }

                /*
                 * '2. 'Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE/CASECODE in STAGINIGLINE table and
                 * update STATUS_ID as 17
                 */
                List<StagingLineEntity> stagingLineEntityList =
                        stagingLineService.getStagingLine(grLine.getWarehouseId(), grLine.getRefDocNumber(), grLine.getPreInboundNo(),
                                grLine.getLineNo(), grLine.getItemCode(), grLine.getCaseCode());
                for (StagingLineEntity stagingLineEntity : stagingLineEntityList) {
                    stagingLineEntity.setStatusId(17L);
                    stagingLineEntity = stagingLineRepository.save(stagingLineEntity);
                    log.info("stagingLineEntity updated: " + stagingLineEntity);
                }

                /*
                 * 3. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
                 * updated STATUS_ID as 17
                 */
                InboundLine inboundLine = inboundLineService.getInboundLine(grLine.getWarehouseId(),
                        grLine.getRefDocNumber(), grLine.getPreInboundNo(), grLine.getLineNo(), grLine.getItemCode());
                inboundLine.setStatusId(17L);
                inboundLine = inboundLineRepository.save(inboundLine);
                log.info("inboundLine updated : " + inboundLine);
            }

            return createdGRLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param grLineList
     * @return
     */
    public static List<AddGrLine> getDuplicates(List<AddGrLine> grLineList) {
        return getDuplicatesMap(grLineList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param addGrLineList
     * @return
     */
    private static Map<String, List<AddGrLine>> getDuplicatesMap(List<AddGrLine> addGrLineList) {
        return addGrLineList.stream().collect(Collectors.groupingBy(AddGrLine::uniqueAttributes));
    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
    private void createPutAwayHeader(GrLine createdGRLine, String loginUserID) {
        //  ASS_HE_NO
        if (createdGRLine != null && createdGRLine.getAssignedUserId() != null) {
            // Insert record into PutAwayHeader
            //private Double putAwayQuantity, private String putAwayUom;
            PutAwayHeader putAwayHeader = new PutAwayHeader();
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setCompanyCodeId(createdGRLine.getCompanyCode());
            putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
            // PA_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_CODE = 7;
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, createdGRLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
            putAwayHeader.setPutAwayNumber(nextPANumber);

            putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
            putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

            //-----------------PROP_ST_BIN---------------------------------------------
            /*
             * 1. Fetch ITM_CODE from GRLINE table and Pass WH_ID/ITM_CODE/BIN_CLASS_ID=1 in INVENTORY table and Fetch ST_BIN values.
             * Pass ST_BIN values into STORAGEBIN table  where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and
             * fetch the filtered values and sort the latest and insert.
             *
             * If WH_ID=111, fetch ST_BIN values of ST_SEC_ID= ZT and sort the latest and insert
             */
            List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");

            // Discussed to remove this condition
//			if (createdGRLine.getWarehouseId().equalsIgnoreCase(WAREHOUSEID_111)) {
//				storageSectionIds = Arrays.asList("ZT");
//			} 
//			List<Inventory> stBinInventoryList = 
//					inventoryRepository.findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(createdGRLine.getWarehouseId(), 
//							createdGRLine.getItemCode(), 1L, 0L);
            List<Inventory> stBinInventoryList =
                    inventoryRepository.findByWarehouseIdAndItemCodeAndBinClassIdAndReferenceField10InAndDeletionIndicator(createdGRLine.getWarehouseId(),
                            createdGRLine.getItemCode(), 1L, storageSectionIds, 0L);
            log.info("stBinInventoryList -----------> : " + stBinInventoryList);

            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            if (!stBinInventoryList.isEmpty()) {
                List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
                log.info("stBins -----------> : " + stBins);

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setStorageBin(stBins);
                storageBinPutAway.setStorageSectionIds(storageSectionIds);
                storageBinPutAway.setWarehouseId(createdGRLine.getWarehouseId());
                StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
                log.info("storagebin -----------> : " + storageBin);
                if (storageBin != null && storageBin.length > 0) {
                    putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
                }
            } else {
                // If ST_BIN value is null
                // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
                // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
                log.info("QuantityType : " + createdGRLine.getQuantityType());
                if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                    StorageBin[] storageBinEMPTY =
                            mastersService.getStorageBinByStatus(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
                    log.info("storageBinEMPTY -----------> : " + storageBinEMPTY);
                    List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
                    List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());

                    /*
                     * Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and
                     * PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and fetch the filteerd values and
                     * Sort the latest and Insert.
                     */
                    // Prod Issue - SQL Grammer on StorageBin-----23-08-2022
                    // Start
                    if (stBins != null && stBins.size() > 2000) {
                        List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
                        storageSectionIds = Arrays.asList("ZB", "ZG", "ZC", "ZT"); // Removing ZD
                        StorageBin[] storageBin = getStorageBinForSplitedList(splitedList, storageSectionIds,
                                createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());

                        // Provided Null else validation
                        log.info("storageBin2 -----------> : " + storageBin);
                        if (storageBin != null && storageBin.length > 0) {
                            putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
                        } else {
                            Long binClassID = 2L;
                            StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        }
                    } else {
                        StorageBin[] storageBin = getStorageBin(storageSectionIds, stBins, createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
                        if (storageBin != null && storageBin.length > 0) {
                            putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
                        } else {
                            Long binClassID = 2L;
                            StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        }
                    }
                    // End
                }

                /*
                 * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
                 * fetch ST_BIN values for STATUS_ID=EMPTY.
                 */
                if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                    StorageBin[] storageBinEMPTY =
                            mastersService.getStorageBinByStatus(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
                    List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
                    List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());

                    // Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZD and PUTAWAY_BLOCK and
                    // PICK_BLOCK columns are Null( FALSE)
                    if (stBins != null && stBins.size() > 2000) {
                        storageSectionIds = Arrays.asList("ZD");
                        List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
                        StorageBin[] storageBin = getStorageBinForSplitedList(splitedList, storageSectionIds,
                                createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
                        if (storageBin != null && storageBin.length > 0) {
                            putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
                        } else {
                            Long binClassID = 2L;
                            StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        }
                    } else {
                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setStorageBin(stBins);
                        storageBinPutAway.setStorageSectionIds(storageSectionIds);
                        storageBinPutAway.setWarehouseId(createdGRLine.getWarehouseId());
                        StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (storageBin != null && storageBin.length > 0) {
                            putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
                        } else {
                            Long binClassID = 2L;
                            StorageBin stBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        }
                    }
                }
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //PROP_HE_NO	<- PAWAY_HE_NO
            putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
            putAwayHeader.setStatusId(19L);
            putAwayHeader.setDeletionIndicator(0L);
            putAwayHeader.setCreatedBy(loginUserID);
            putAwayHeader.setCreatedOn(new Date());
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeader.setUpdatedOn(new Date());
            putAwayHeader = putAwayHeaderRepository.save(putAwayHeader);
            log.info("putAwayHeader : " + putAwayHeader);

            /*----------------Inventory tables Create---------------------------------------------*/
            Inventory createdinventory = createInventory(createdGRLine);

            /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
            createInventoryMovement(createdGRLine, createdinventory);
        }
    }

    /**
     * @param splitedList
     * @param storageSectionIds
     * @param authToken
     * @return
     */
    private StorageBin[] getStorageBinForSplitedList(List<List<String>> splitedList, List<String> storageSectionIds, String warehouseId, String authToken) {
        for (List<String> list : splitedList) {
            StorageBin[] storageBin = getStorageBin(storageSectionIds, list, warehouseId, authToken);
            if (storageBin != null && storageBin.length > 0) {
                return storageBin;
            }
        }
        return null;
    }

    /**
     * @param storageSectionIds
     * @param stBins
     * @param authToken
     * @return
     */
    private StorageBin[] getStorageBin(List<String> storageSectionIds, List<String> stBins, String warehouseId, String authToken) {
        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setStorageBin(stBins);
        storageBinPutAway.setStorageSectionIds(storageSectionIds);
        storageBinPutAway.setWarehouseId(warehouseId);
        StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authToken);
        return storageBin;
    }

    /**
     * @param <T>
     * @param list
     * @return
     */
    private static <T> List[] splitList(List<String> list) {
        // get the size of the list
        int size = list.size();

        // construct a new list from the returned view by `List.subList()` method
        List<String> first = new ArrayList<>(list.subList(0, (size + 1) / 2));
        List<String> second = new ArrayList<>(list.subList((size + 1) / 2, size));

        // return an array of lists to accommodate both lists
        return new List[]{first, second};
    }

    /**
     * @param createdGRLine
     * @param createdinventory
     */
    private void createInventoryMovement(GrLine createdGRLine, Inventory createdinventory) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(createdGRLine, inventoryMovement, CommonUtils.getNullPropertyNames(createdGRLine));
        inventoryMovement.setCompanyCodeId(createdGRLine.getCompanyCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(createdGRLine.getGoodsReceiptNo());

        // ST_BIN
        inventoryMovement.setStorageBin(createdinventory.getStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(createdGRLine.getGoodReceiptQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("P");

        // MVT_UOM
        inventoryMovement.setInventoryUom(createdGRLine.getOrderUom());

        inventoryMovement.setVariantCode(1L);
        inventoryMovement.setVariantSubCode("1");

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(createdGRLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(createdGRLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement : " + inventoryMovement);
    }

    /**
     * @param createdGRLine
     * @return
     */
    private Inventory createInventory(GrLine createdGRLine) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));
        inventory.setCompanyCodeId(createdGRLine.getCompanyCode());


        // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
        inventory.setVariantCode(1L);
        inventory.setVariantSubCode("1");
        inventory.setStorageMethod("1");
        inventory.setBatchSerialNumber("1");
        inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
        inventory.setBinClassId(3L);

        // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        StorageBin storageBin = mastersService.getStorageBin(createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
        inventory.setStorageBin(storageBin.getStorageBin());

        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
            inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
            inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
        }
        if (storageBin != null) {
            inventory.setReferenceField10(storageBin.getStorageSectionId());
            inventory.setReferenceField5(storageBin.getAisleNumber());
            inventory.setReferenceField6(storageBin.getShelfId());
            inventory.setReferenceField7(storageBin.getRowId());
        }

        // STCK_TYP_ID
        inventory.setStockTypeId(1L);

        // SP_ST_IND_ID
        inventory.setSpecialStockIndicatorId(1L);

        // INV_QTY
        inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());

        // INV_UOM
        inventory.setInventoryUom(createdGRLine.getOrderUom());
        inventory.setCreatedBy(createdGRLine.getCreatedBy());
        inventory.setCreatedOn(createdGRLine.getCreatedOn());
        Inventory createdinventory = inventoryRepository.save(inventory);
        log.info("created inventory : " + createdinventory);
        return createdinventory;
    }

    /**
     * updateGrLine
     *
     * @param loginUserID
     * @param itemCode
     * @param updateGrLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrLine updateGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode,
                               String loginUserID, UpdateGrLine updateGrLine)
            throws IllegalAccessException, InvocationTargetException {
        GrLine dbGrLine = getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                packBarcodes, lineNo, itemCode);
        BeanUtils.copyProperties(updateGrLine, dbGrLine, CommonUtils.getNullPropertyNames(updateGrLine));
        dbGrLine.setUpdatedBy(loginUserID);
        dbGrLine.setUpdatedOn(new Date());
        GrLine updatedGrLine = grLineRepository.save(dbGrLine);
        return updatedGrLine;
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<GrLine> grLines = getGrLines();
        grLines.forEach(g -> g.setReferenceField1(asnNumber));
        grLineRepository.saveAll(grLines);
    }

    /**
     * deleteGrLine
     *
     * @param loginUserID
     * @param itemCode
     */
    public void deleteGrLine(String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                             String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        GrLine grLine = getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                packBarcodes, lineNo, itemCode);
        if (grLine != null) {
            grLine.setDeletionIndicator(1L);
            grLine.setUpdatedBy(loginUserID);
            grLineRepository.save(grLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
    }

    //========================================================V2===================================================================

    /**
     * getGrLines
     *
     * @return
     */
    public List<GrLineV2> getGrLinesV2() {
        List<GrLineV2> grLineList = grLineV2Repository.findAll();
        grLineList = grLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return grLineList;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @return
     */
    public GrLineV2 getGrLineV2(String companyCode, String languageId, String plantId,
                                String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo,
                                String palletCode, String caseCode, String packBarcodes, Long lineNo, String itemCode) {
        Optional<GrLineV2> grLine = grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
                languageId,
                companyCode,
                plantId,
                warehouseId,
                preInboundNo,
                refDocNumber,
                goodsReceiptNo,
                palletCode,
                caseCode,
                packBarcodes,
                lineNo,
                itemCode,
                0L);
        if (grLine.isEmpty()) {
            //Exception Log 
            createGrLineLog(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, palletCode,
                    caseCode, packBarcodes, lineNo, itemCode, "GrLineV2 with goodsReceiptNo - " + goodsReceiptNo + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine.get();
    }

    /**
     * PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
     *
     * @param preInboundNo
     * @param refDocNumber
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<GrLineV2> getGrLineV2(String companyCode, String languageId, String plantId,
                                      String preInboundNo, String refDocNumber, String packBarcodes, Long lineNo, String itemCode) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        preInboundNo,
                        refDocNumber,
                        packBarcodes,
                        lineNo,
                        itemCode,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog1(languageId, companyCode, plantId, refDocNumber, preInboundNo, packBarcodes, lineNo,
                    itemCode, "The given values of GrLineV2 with lineNo - " + lineNo + " doesn't exists.");

            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @return
     */
    public List<GrLineV2> getGrLineV2(String companyCode, String languageId, String plantId,
                                      String refDocNumber, String packBarcodes, String warehouseId, String preInboundNo, String caseCode) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        packBarcodes,
                        warehouseId,
                        preInboundNo,
                        caseCode,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog2(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, packBarcodes, caseCode,
                    "The given values of GrLineV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",caseCode: " + caseCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
//    public GrLineV2 getGrLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
//                                                       String refDocNumber, String packBarcodes, String preInboundNo, String itemCode, String manufacturerName) {
//        GrLineV2 grLine =
//                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        refDocNumber,
//                        packBarcodes,
//                        warehouseId,
//                        preInboundNo,
//                        itemCode,
//                        manufacturerName,
//                        0L);
//        if (grLine == null) {
//            return null;
//        }
//
//        return grLine;
//    }
    public List<GrLineV2> getGrLineForInboundConformV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                       String refDocNumber, String itemCode, String manufacturerName, Long lineNumber, String preInboundNo) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        warehouseId,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        0L);
        if (grLine == null) {
            return null;
        }

        return grLine;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLineV2> getGrLineV2(String companyCode, String languageId, String plantId,
                                      String refDocNumber, String packBarcodes) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        packBarcodes,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog3(languageId, companyCode, plantId, refDocNumber, packBarcodes,
                    "The given values of GrLineV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist in GRLine.");
        }

        return grLine;
    }

    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLineV2> getGrLineForPutAwayLineCreateV2(String companyCode, String languageId, String plantId, String warehouseId,
                                                          String refDocNumber, String preInboundNumber, String packBarcodes) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNumber,
                        refDocNumber,
                        packBarcodes,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog4(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNumber,
                    packBarcodes, "The given values of GrLineV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist in GRLine.");
        }

        return grLine;
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLineV2> getGrLineV2(String companyCode, String languageId, String plantId,
                                      String warehouseId, String refDocNumber, String packBarcodes) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog5(languageId, companyCode, plantId, warehouseId, refDocNumber, packBarcodes,
                    "The given values of GrLineV2 with refDocNumber - " + refDocNumber + " doesn't exists.");

            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist in GRLine.");
        }

        return grLine;
    }

    /**
     *
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLineV2> getGrLineV2ForReversal(String companyCode, String languageId, String plantId,
                                                 String warehouseId, String refDocNumber, String packBarcodes) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        0L);
        return grLine;
    }

    /**
     *
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<GrLineV2> getGrLineV2ForReversalWithStatusId(String companyCode, String languageId, String plantId,
                                                             String warehouseId, String refDocNumber, String packBarcodes) {
        List<Long> statusIdList = new ArrayList<>();
        statusIdList.add(14L);
        statusIdList.add(17L);

        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndStatusIdInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        statusIdList,
                        0L);
        return grLine;
    }

    /**
     *
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @return
     */
    public List<GrLineV2> getGrLineV2ForReversal(String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber,
                                                 String packBarcodes, String itemCode, String manufacturerName, Long lineNumber) {

        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        0L);
        return grLine;
    }

    /**
     *
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
    public List<GrLineV2> getGrLineV2ForReversal(String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber,
                                                 String packBarcodes, String itemCode, String manufacturerName, Long lineNumber, String preInboundNo) {

        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndItemCodeAndManufacturerNameAndLineNoAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        preInboundNo,
                        0L);
        return grLine;
    }

    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<GrLineV2> getGrLineForGrHeaderConfirmV2(String companyCode, String languageId, String plantId,
                                                        String warehouseId, String refDocNumber, String preInboundNo) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (grLine.isEmpty()) {
            return null;
        }

        return grLine;
    }

    /**
     * @param searchGrLine
     * @return
     * @throws ParseException
     */
    public Stream<GrLineV2> findGrLineV2(SearchGrLineV2 searchGrLine) throws ParseException, java.text.ParseException {
        if (searchGrLine.getStartCreatedOn() != null
                && searchGrLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrLine.getStartCreatedOn(),
                    searchGrLine.getEndCreatedOn());
            searchGrLine.setStartCreatedOn(dates[0]);
            searchGrLine.setEndCreatedOn(dates[1]);
        }
        log.info("Grline Search Input - Stream: " + searchGrLine);
        GrLineV2Specification spec = new GrLineV2Specification(searchGrLine);
        Stream<GrLineV2> results = grLineV2Repository.stream(spec, GrLineV2.class);
        return results;
    }

 public List<GrLineImpl> findGrLineSQLV2(SearchGrLineV2 searchGrLine) throws ParseException, java.text.ParseException {
    if (searchGrLine.getStartCreatedOn() != null
            && searchGrLine.getEndCreatedOn() != null) {
        Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrLine.getStartCreatedOn(),
                searchGrLine.getEndCreatedOn());
        searchGrLine.setStartCreatedOn(dates[0]);
        searchGrLine.setEndCreatedOn(dates[1]);
    }
    if(searchGrLine.getCompanyCodeId() == null || searchGrLine.getCompanyCodeId().isEmpty()){
        searchGrLine.setCompanyCodeId(null);
    }
    if(searchGrLine.getPlantId() == null || searchGrLine.getPlantId().isEmpty()){
        searchGrLine.setPlantId(null);
    }
    if(searchGrLine.getLanguageId() == null || searchGrLine.getLanguageId().isEmpty()){
        searchGrLine.setLanguageId(null);
    }
    if(searchGrLine.getWarehouseId() == null || searchGrLine.getWarehouseId().isEmpty()){
        searchGrLine.setWarehouseId(null);
    }
    if(searchGrLine.getRefDocNumber() == null || searchGrLine.getRefDocNumber().isEmpty()){
        searchGrLine.setRefDocNumber(null);
    }
    if(searchGrLine.getPreInboundNo() == null || searchGrLine.getPreInboundNo().isEmpty()){
        searchGrLine.setPreInboundNo(null);
    }
    if(searchGrLine.getLineNo() == null || searchGrLine.getLineNo().isEmpty()){
        searchGrLine.setLineNo(null);
    }
    if(searchGrLine.getPackBarcodes() == null || searchGrLine.getPackBarcodes().isEmpty()){
        searchGrLine.setPackBarcodes(null);
    }
    if(searchGrLine.getItemCode() == null || searchGrLine.getItemCode().isEmpty()){
        searchGrLine.setItemCode(null);
    }
    if(searchGrLine.getCaseCode() == null || searchGrLine.getCaseCode().isEmpty()){
        searchGrLine.setCaseCode(null);
    }
    if(searchGrLine.getManufacturerName() == null || searchGrLine.getManufacturerName().isEmpty()){
        searchGrLine.setManufacturerName(null);
    }
    if(searchGrLine.getBarcodeId() == null || searchGrLine.getBarcodeId().isEmpty()){
        searchGrLine.setBarcodeId(null);
    }
    if(searchGrLine.getStatusId() == null || searchGrLine.getStatusId().isEmpty()){
        searchGrLine.setStatusId(null);
    }
    if(searchGrLine.getManufacturerCode() == null || searchGrLine.getManufacturerCode().isEmpty()){
        searchGrLine.setManufacturerCode(null);
    }
    if(searchGrLine.getOrigin() == null || searchGrLine.getOrigin().isEmpty()){
        searchGrLine.setOrigin(null);
    }
    if(searchGrLine.getInterimStorageBin() == null || searchGrLine.getInterimStorageBin().isEmpty()){
        searchGrLine.setInterimStorageBin(null);
    }
    if(searchGrLine.getBrand() == null || searchGrLine.getBrand().isEmpty()){
        searchGrLine.setBrand(null);
    }
    if(searchGrLine.getRejectType() == null || searchGrLine.getRejectType().isEmpty()){
        searchGrLine.setRejectType(null);
    }
    if(searchGrLine.getRejectReason() == null || searchGrLine.getRejectReason().isEmpty()){
        searchGrLine.setRejectReason(null);
    }
    if(searchGrLine.getInboundOrderTypeId() == null || searchGrLine.getInboundOrderTypeId().isEmpty()) {
        searchGrLine.setInboundOrderTypeId(null);
    }
     log.info("Grline Search Input - SQL: " + searchGrLine);
     List<GrLineImpl> results = grLineV2Repository.findGrLine(
             searchGrLine.getCompanyCodeId(),
             searchGrLine.getPlantId(),
             searchGrLine.getLanguageId(),
             searchGrLine.getWarehouseId(),
             searchGrLine.getRefDocNumber(),
             searchGrLine.getPreInboundNo(),
             searchGrLine.getPackBarcodes(),
             searchGrLine.getLineNo(),
             searchGrLine.getItemCode(),
             searchGrLine.getCaseCode(),
             searchGrLine.getManufacturerName(),
             searchGrLine.getBarcodeId(),
             searchGrLine.getStatusId(),
             searchGrLine.getManufacturerCode(),
             searchGrLine.getOrigin(),
             searchGrLine.getInterimStorageBin(),
             searchGrLine.getBrand(),
             searchGrLine.getRejectType(),
             searchGrLine.getRejectReason(),
             searchGrLine.getStartCreatedOn(),
             searchGrLine.getEndCreatedOn(),
             searchGrLine.getInboundOrderTypeId());
     log.info("Grline Search Output: " + results.size());
        return results;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<GrLineV2> getGrLineForUpdateV2(String companyCode, String languageId, String plantId,
                                               String warehouseId, String preInboundNo, String refDocNumber, Long lineNo, String itemCode) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (grLine.isEmpty()) {
            //Exception Log
            createGrLineLog6(languageId, companyCode, plantId, refDocNumber, preInboundNo, lineNo, itemCode,
                    "The given values of GrLineV2 with lineNo - " + lineNo + " doesn't exists.");

            throw new BadRequestException("The given values: " +
                    ",warehouseId: " + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }

        return grLine;
    }

    /**
     * @param acceptQty
     * @param damageQty
     * @param loginUserID
     * @return
     */
    public List<PackBarcode> generatePackBarcodeV2(String companyCode, String languageId, String plantId,
                                                   Long acceptQty, Long damageQty, String warehouseId, String loginUserID) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 6;
        List<PackBarcode> packBarcodes = new ArrayList<>();

        // Accept Qty
        if (acceptQty != 0) {
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            PackBarcode acceptQtyPackBarcode = new PackBarcode();
            acceptQtyPackBarcode.setQuantityType("A");
            acceptQtyPackBarcode.setBarcode(nextRangeNumber);
            packBarcodes.add(acceptQtyPackBarcode);
        }

        // Damage Qty
        if (damageQty != 0) {
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            PackBarcode damageQtyPackBarcode = new PackBarcode();
            damageQtyPackBarcode.setQuantityType("D");
            damageQtyPackBarcode.setBarcode(nextRangeNumber);
            packBarcodes.add(damageQtyPackBarcode);
        }
        return packBarcodes;
    }

    @Transactional
    public List<GrLineV2> createGrLineNonCBMV2(@Valid List<AddGrLineV2> newGrLines, String loginUserID) throws java.text.ParseException {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        try {

            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                if(newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }
                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("A-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    dbGrLine.setStatusId(14L);

                    //12-03-2024 - Ticket No. ALM/2024/006
                    if(dbGrLine.getGoodReceiptQty() < 0){
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if(newGrLine.getStatusId() == 24L){
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(), newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), newGrLine.getItemCode(), newGrLine.getManufacturerName(), newGrLine.getLineNo());
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                    if(dbGrQty != null) {
                        Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
                        if (newGrLine.getOrderQty() < totalGrQty){
                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
                        }
                    }

                    //V2 Code
                    IKeyValuePair description = stagingLineV2Repository.getDescription(newGrLine.getCompanyCode(),
                            newGrLine.getLanguageId(),
                            newGrLine.getPlantId(),
                            newGrLine.getWarehouseId());

                    statusDescription = stagingLineV2Repository.getStatusDescription(dbGrLine.getStatusId(), newGrLine.getLanguageId());
                    dbGrLine.setStatusDescription(statusDescription);

                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    dbGrLine.setMiddlewareId(newGrLine.getMiddlewareId());
                    dbGrLine.setMiddlewareHeaderId(newGrLine.getMiddlewareHeaderId());
                    dbGrLine.setMiddlewareTable(newGrLine.getMiddlewareTable());
                    dbGrLine.setManufacturerFullName(newGrLine.getManufacturerFullName());
                    dbGrLine.setReferenceDocumentType(newGrLine.getReferenceDocumentType());
                    dbGrLine.setPurchaseOrderNumber(newGrLine.getPurchaseOrderNumber());

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = 0D;
                    Double acceptQty = 0D;
                    Double damageQty = 0D;

                    if (newGrLine.getOrderQty() != null) {
                        invoiceQty = newGrLine.getOrderQty();
                    }
                    if (newGrLine.getAcceptedQty() != null) {
                        acceptQty = newGrLine.getAcceptedQty();
                    }
                    if (newGrLine.getDamageQty() != null) {
                        damageQty = newGrLine.getDamageQty();
                    }

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(newGrLine.getCompanyCode(),
                            newGrLine.getPlantId(),
                            newGrLine.getLanguageId(),
                            newGrLine.getWarehouseId(),
                            newGrLine.getPreInboundNo(),
                            newGrLine.getRefDocNumber(),
                            newGrLine.getLineNo(),
                            newGrLine.getItemCode(),
                            newGrLine.getManufacturerName());
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        dbGrLine.setOrderUom(dbStagingLineEntity.getOrderUom());
                        dbGrLine.setGrUom(dbStagingLineEntity.getOrderUom());
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, newGrLine.getLanguageId());
                        dbGrLine.setStatusDescription(statusDescription);
                    }

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setBranchCode(newGrLine.getBranchCode());
                    dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                    dbGrLine.setIsCompleted(newGrLine.getIsCompleted());

                    dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();

                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
                            goodsReceiptNo, dbGrLine.getItemCode(), dbGrLine.getLineNo(),
                            languageId, companyCode, plantId,
                            refDocNumber, dbGrLine.getPackBarcodes(), warehouseId,
                            preInboundNo, dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        try {
                            createdGRLine = grLineV2Repository.save(dbGrLine);
                        } catch (Exception e) {
                            createGrLineError = true;

                            //Exception Log
                            createGrLineLog7(dbGrLine, e.toString());

                            throw e;
                        }
                        log.info("createdGRLine : " + createdGRLine);
                        createdGRLines.add(createdGRLine);

                        if (createdGRLine != null && !createGrLineError) {
                            // Record Insertion in PUTAWAYHEADER table
                            createPutAwayHeaderNonCBMV2(createdGRLine, loginUserID);
                        }
                    }
					//Update staging Line using stored Procedure
                    stagingLineV2Repository.updateStagingLineUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            createdGRLine.getLineNo(), createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), new Date());
                    log.info("stagingLine Status updated using Stored Procedure ");

                    //Update InboundLine using Stored Procedure
                    inboundLineV2Repository.updateInboundLineStatusUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            createdGRLine.getLineNo(), createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), 17L, statusDescription, new Date());
                    log.info("inboundLine Status updated using Stored Procedure ");
                }
                log.info("Records were inserted successfully...");
            }

            // STATUS updates
            /*
             * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/GR_NO/IB_LINE_NO/ITM_CODE in GRLINE table and
             * validate STATUS_ID of the all the filtered line items = 17 , if yes
             */
//            List<StagingLineEntityV2> stagingLineList =
//                    stagingLineService.getStagingLineForGrConfirmV2(
//                            createdGRLines.get(0).getCompanyCode(),
//                            createdGRLines.get(0).getPlantId(),
//                            createdGRLines.get(0).getLanguageId(),
//                            createdGRLines.get(0).getWarehouseId(),
//                            createdGRLines.get(0).getRefDocNumber(),
//                            createdGRLines.get(0).getPreInboundNo());
//
//            Long createdStagingLinesCount = 0L;
//            Long createdGRLinesStatusId17Count = 0L;
//
//            if (stagingLineList != null) {
//                createdStagingLinesCount = stagingLineList.stream().count();
//            }
//            createdGRLinesStatusId17Count = grLineV2Repository.getGrLineStatus17Count(createdGRLines.get(0).getCompanyCode(),
//                    createdGRLines.get(0).getPlantId(),
//                    createdGRLines.get(0).getLanguageId(),
//                    createdGRLines.get(0).getWarehouseId(),
//                    createdGRLines.get(0).getRefDocNumber(),
//                    createdGRLines.get(0).getPreInboundNo(), 17L);
//            log.info("createdGRLinesStatusId17Count: " + createdGRLinesStatusId17Count);

//            log.info("createdStagingLinesCount, createdGRLinesStatusId17Count: " + createdStagingLinesCount + ", " + createdGRLinesStatusId17Count);
//            statusDescription = stagingLineV2Repository.getStatusDescription(17L, createdGRLines.get(0).getLanguageId());
//            grHeaderV2Repository.updateGrheaderStatusUpdateProc(
//                    createdGRLines.get(0).getCompanyCode(),
//                    createdGRLines.get(0).getPlantId(),
//                    createdGRLines.get(0).getLanguageId(),
//                    createdGRLines.get(0).getWarehouseId(),
//                    createdGRLines.get(0).getRefDocNumber(),
//                    createdGRLines.get(0).getPreInboundNo(),
//                    createdGRLines.get(0).getGoodsReceiptNo(),
//                    17L,
//                    statusDescription,
//                    new Date());
//            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");
//            for (GrLineV2 grLine : createdGRLines) {
                /*
                 * 1. Update GRHEADER table with STATUS_ID=17 by Passing WH_ID/GR_NO/CASE_CODE/REF_DOC_NO and
                 * GR_CNF_BY with USR_ID and GR_CNF_ON with Server time
                 */
//                if (createdStagingLinesCount.equals(createdGRLinesStatusId17Count)) {
//                    log.info("Updating GrHeader with StatusId 17 Initiated");
//                    GrHeaderV2 grHeader = grHeaderService.getGrHeaderV2(
//                            grLine.getWarehouseId(),
//                            grLine.getGoodsReceiptNo(),
//                            grLine.getCaseCode(),
//                            grLine.getCompanyCode(),
//                            grLine.getLanguageId(),
//                            grLine.getPlantId(),
//                            grLine.getRefDocNumber());
//                    if(grHeader != null) {
//                        if (grHeader.getCompanyCode() == null) {
//                            grHeader.setCompanyCode(grLine.getCompanyCode());
//                        }
//                        grHeader.setStatusId(17L);
//                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, grLine.getLanguageId());
//                        grHeader.setStatusDescription(statusDescription);
//                        grHeader.setCreatedBy(loginUserID);
//                        grHeader.setUpdatedOn(new Date());
//                        grHeader.setConfirmedOn(new Date());
//                        grHeader = grHeaderV2Repository.save(grHeader);
//                        log.info("grHeader updated: " + grHeader);
//                    }
//                }
                /*
                 * '2. 'Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE/CASECODE in STAGINIGLINE table and
                 * update STATUS_ID as 17
                 */

//                log.info("Updating StagingLine and InboundLine with StatusId 17 Initiated");
//                if (grLine.getAcceptedQty() == null) {
//                    grLine.setAcceptedQty(0D);
//                }
//                if (grLine.getDamageQty() == null) {
//                    grLine.setDamageQty(0D);
//                }
//                stagingLineV2Repository.updateStagingLineUpdateProc(
//                                grLine.getCompanyCode(),
//                                grLine.getPlantId(),
//                                grLine.getLanguageId(),
//                                grLine.getWarehouseId(),
//                                grLine.getRefDocNumber(),
//                                grLine.getPreInboundNo(),
//                                grLine.getItemCode(),
//                                grLine.getManufacturerName(),
//                                grLine.getLineNo(),
//                                new Date(),
//                                grLine.getAcceptedQty(),
//                                grLine.getDamageQty());
//                log.info("stagingLineEntity updated through Stored Procedure: ");
//                List<StagingLineEntityV2> stagingLineEntityList =
//                        stagingLineService.getStagingLineV2(
//                                grLine.getCompanyCode(),
//                                grLine.getPlantId(),
//                                grLine.getLanguageId(),
//                                grLine.getWarehouseId(),
//                                grLine.getRefDocNumber(),
//                                grLine.getPreInboundNo(),
//                                grLine.getLineNo(),
//                                grLine.getItemCode(),
//                                grLine.getCaseCode());
//                for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityList) {
//
//                    //v2 code
//                    if (stagingLineEntity.getRec_accept_qty() == null) {
//                        stagingLineEntity.setRec_accept_qty(0D);
//                    }
//                    if (grLine.getAcceptedQty() == null) {
//                        grLine.setAcceptedQty(0D);
//                    }
//                    if (stagingLineEntity.getRec_damage_qty() == null) {
//                        stagingLineEntity.setRec_damage_qty(0D);
//                    }
//                    if (grLine.getDamageQty() == null) {
//                        grLine.setDamageQty(0D);
//                    }
//
//                    Double rec_accept_qty = stagingLineEntity.getRec_accept_qty() + grLine.getAcceptedQty();
//                    Double rec_damage_qty = stagingLineEntity.getRec_damage_qty() + grLine.getDamageQty();
//
//                    stagingLineEntity.setRec_accept_qty(rec_accept_qty);
//                    stagingLineEntity.setRec_damage_qty(rec_damage_qty);
//
//                    if (grLine.getStatusId() == 17L) {
//                        stagingLineEntity.setStatusId(17L);
//                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, grLine.getLanguageId());
//                        stagingLineEntity.setStatusDescription(statusDescription);
//                    }
//                    stagingLineEntity = stagingLineV2Repository.save(stagingLineEntity);
//                    log.info("stagingLineEntity updated: " + stagingLineEntity);
//                }

                /*
                 * 3. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
                 * updated STATUS_ID as 17
                 */
//                if (grLine.getStatusId() == 17L) {
//                    inboundLineV2Repository.updateInboundLineStatusUpdateProc(
//                            grLine.getCompanyCode(),
//                            grLine.getPlantId(),
//                            grLine.getLanguageId(),
//                            grLine.getWarehouseId(),
//                            grLine.getRefDocNumber(),
//                            grLine.getPreInboundNo(),
//                            grLine.getItemCode(),
//                            grLine.getManufacturerName(),
//                            grLine.getLineNo(),
//                            17L,
//                            statusDescription,
//                            new Date()
//                    );
//                    log.info("inboundLine Status updated : ");
//                    InboundLineV2 inboundLine = inboundLineV2Repository.getInboundLineV2(grLine.getWarehouseId(),
//                            grLine.getLineNo(),
//                            grLine.getPreInboundNo(),
//                            grLine.getItemCode(),
//                            grLine.getCompanyCode(),
//                            grLine.getPlantId(),
//                            grLine.getLanguageId(),
//                            grLine.getRefDocNumber());
//                    inboundLine.setStatusId(17L);
//                    inboundLine.setStatusDescription(statusDescription);
//                    inboundLine = inboundLineV2Repository.save(inboundLine);
//                    log.info("inboundLine updated : " + inboundLine);
//                }
//            }

            //Update GrHeader using stored Procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, createdGRLines.get(0).getLanguageId());
            grHeaderV2Repository.updateGrheaderStatusUpdateProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");

            //Update staging Line using stored Procedure
//            stagingLineV2Repository.updateStagingLineUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,new Date());
//            log.info("stagingLine Status updated using Stored Procedure ");

            //Update InboundLine using Stored Procedure
//            inboundLineV2Repository.updateInboundLineStatusUpdateNewProc(
//                  companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,17L, statusDescription, new Date());
//            log.info("inboundLine Status updated using Stored Procedure ");

            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param createdGRLine
     * @param loginUserID
     */
    private void createPutAwayHeaderNonCBMV2(GrLineV2 createdGRLine, String loginUserID) throws java.text.ParseException {
        String itemCode = createdGRLine.getItemCode();
        String companyCode = createdGRLine.getCompanyCode();
        String plantId = createdGRLine.getPlantId();
        String languageId = createdGRLine.getLanguageId();
        String warehouseId = createdGRLine.getWarehouseId();
        String proposedStorageBin = createdGRLine.getInterimStorageBin();

        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(companyCode);
        storageBinPutAway.setPlantId(plantId);
        storageBinPutAway.setLanguageId(languageId);
        storageBinPutAway.setWarehouseId(warehouseId);

        Double cbm = 0D;

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        if (createdGRLine.getCbm() != null) {
            cbm = createdGRLine.getCbm();
            log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
        }
        outerloop:
//        while (true) {
            //  ASS_HE_NO
            if (createdGRLine != null) {
                // Insert record into PutAwayHeader
                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                putAwayHeader.setCompanyCodeId(companyCode);
                putAwayHeader.setReferenceField5(itemCode);

                // PA_NO
                long NUM_RAN_CODE = 7;
                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                log.info("PutAwayNumber Generated: " + nextPANumber);

                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

                //set bar code id for packbarcode
                putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());

                //set pack bar code for actual packbarcode
                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());

                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                //-----------------PROP_ST_BIN---------------------------------------------

                //V2 Code
                Long binClassId = 0L;                   //actual code follows
                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
                    binClassId = 1L;
                }
                if (createdGRLine.getInboundOrderTypeId() == 2) {
                    binClassId = 7L;
                }
                log.info("BinClassId : " + binClassId);

                List<IInventoryImpl> stBinInventoryList = inventoryService.getInventoryForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                        itemCode, createdGRLine.getManufacturerName(), binClassId);
                log.info("stBinInventoryList -----------> : " + stBinInventoryList.size());

                List<String> inventoryStorageBinList = null;
                if (stBinInventoryList != null && !stBinInventoryList.isEmpty()) {
                    inventoryStorageBinList = stBinInventoryList.stream().map(IInventoryImpl::getStorageBin).collect(Collectors.toList());
                }
                log.info("Inventory StorageBin List: " + inventoryStorageBinList);

                if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                    storageBinPutAway.setBinClassId(binClassId);
                    storageBinPutAway.setBin(proposedStorageBin);
                    StorageBinV2 storageBin = null;
                    try {
                        storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                    log.info("InterimStorageBin: " + storageBin);
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                    if (storageBin != null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        cbm = 0D;               //to break the loop
                    }
                    if (storageBin == null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        cbm = 0D;               //to break the loop
                    }
                }
                //BinClassId - 7 - Return Order(Sale Return)
                if (createdGRLine.getInboundOrderTypeId() == 2) {

                    storageBinPutAway.setBinClassId(binClassId);
                    log.info("BinClassId : " + binClassId);

                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    if (proposedBinClass7Bin != null) {
                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStBin);
                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                        log.info("Return Order --> BinClassId7 Proposed Bin: " + proposedStBin);
                        cbm = 0D;   //break the loop
                    }
                    if (proposedBinClass7Bin == null) {
                        binClassId = 2L;
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 stBin = mastersService.getStorageBin(
                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                        log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                        cbm = 0D;   //break the loop
                    }
                }

                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                    if (stBinInventoryList != null) {
                        log.info("BinClassId : " + binClassId);
                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                            if(createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                storageBinPutAway.setBinClassId(binClassId);
                                storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                StorageBinV2 proposedExistingBin = mastersService.getExistingStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedExistingBin != null) {
                                    proposedStorageBin = proposedExistingBin.getStorageBin();
                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                }
                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                                cbm = 0D;   //break the loop
                            }
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                storageBinPutAway.setBinClassId(7L);
                                StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedBinClass7Bin != null) {
                                    String proposedStBin = proposedBinClass7Bin.getStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                                    log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
                                    cbm = 0D;   //break the loop
                                }
                                if (proposedBinClass7Bin == null) {
                                    binClassId = 2L;
                                    log.info("BinClassId : " + binClassId);
                                    StorageBinV2 stBin = mastersService.getStorageBin(
                                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                    log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                    cbm = 0D;   //break the loop
                                }
                            }
                        }
                    }
                }

                //Last Picked Bin as Proposed Bin If it is empty
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {

//                    List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineForLastBinCheckV2(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                    PickupLineV2 pickupLineList = pickupLineService.getPickupLineForLastBinCheck(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                    log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
//                    String lastPickedStorageBinList = null;
                    if (pickupLineList != null) {
//                        lastPickedStorageBinList = pickupLineList.getPickedStorageBin();
//                    }
//                    log.info("LastPickedStorageBinList: " + lastPickedStorageBinList);

//                    if (lastPickedStorageBinList != null && !lastPickedStorageBinList.isEmpty()) {
//                        log.info("BinClassId : " + binClassId);

//                        storageBinPutAway.setStatusId(0L);
//                        storageBinPutAway.setBinClassId(1L);
//                        storageBinPutAway.setStorageBin(lastPickedStorageBinList);

//                        StorageBinV2 proposedNonCbmLastPickStorageBin = mastersService.getStorageBinNonCbmLastPicked(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                        log.info("proposedNonCbmLastPickStorageBin: " + proposedNonCbmLastPickStorageBin);
//                        if (proposedNonCbmLastPickStorageBin != null) {
                            putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
                            putAwayHeader.setLevelId(pickupLineList.getLevelId());
                            log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
                            log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
                            cbm = 0D;   //break the loop
//                        }
                    }
                }

                //Propose Empty Bin if Last picked bin is unavailable
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {
                    // If ST_BIN value is null
                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
                    log.info("QuantityType : " + createdGRLine.getQuantityType());
                    log.info("BinClassId : " + binClassId);

                    storageBinPutAway.setStatusId(0L);
                    StorageBinV2 proposedNonCbmStorageBin = null;

                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                        storageBinPutAway.setBinClassId(binClassId);

                    //Checking confirmed bin in putAway line for that item
                    PutAwayLineV2 existingBinPutAwayLineItemCheck = putAwayLineService.getPutAwayLineExistingItemCheckV2(companyCode, plantId, languageId, warehouseId,
                            itemCode, createdGRLine.getManufacturerName());
                    log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
                    if (existingBinPutAwayLineItemCheck != null) {
                        proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
                            putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
                        } else {
                            storageBinPutAway.setBin(proposedStorageBin);
                            StorageBinV2 getLevelIdForProposedBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (getLevelIdForProposedBin != null) {
                                putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
                            }
                        }
                        log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
                    }
                        List<PutAwayHeaderV2> existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV2(companyCode, plantId, languageId, warehouseId,
                                itemCode, createdGRLine.getManufacturerName());
                        log.info("existingBinItemCheck: " + existingBinItemCheck);
                        if (existingBinItemCheck != null && !existingBinItemCheck.isEmpty()) {
                            proposedStorageBin = existingBinItemCheck.get(0).getProposedStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.get(0).getLevelId()));
                            log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                            cbm = 0D;   //break the loop
                        }
                        List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                        log.info("existingBinCheck: " + existingBinCheck);
                        if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                            storageBinPutAway.setStorageBin(existingBinCheck);
                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedNonCbmStorageBin != null) {
                                proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                cbm = 0D;   //break the loop
                            }
                        }
                        if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                            List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                            log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                            log.info("BinClassId: " + binClassId);
                            storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                            if (proposedNonCbmStorageBin != null) {
                                proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));

                                cbm = 0D;   //break the loop
                            }
                            if (proposedNonCbmStorageBin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                cbm = 0D;   //break the loop
                            }
                        }
                    }

                    /*
                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
                     * fetch ST_BIN values for STATUS_ID=EMPTY.
                     */
                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                        binClassId = 7L;
                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (proposedBinClass7Bin != null) {
                            proposedStorageBin = proposedBinClass7Bin.getStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                            log.info("D --> BinClassId7 Proposed Bin: " + proposedStorageBin);
                            cbm = 0D;   //break the loop
                        }
                        if (proposedBinClass7Bin == null) {
                            binClassId = 2L;
                            log.info("BinClassId : " + binClassId);
                            StorageBinV2 stBin = mastersService.getStorageBin(
                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                            log.info("D --> reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                            cbm = 0D;   //break the loop
                        }
                    }
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////
                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                //PROP_HE_NO	<- PAWAY_HE_NO
                if (createdGRLine.getReferenceDocumentType() != null) {
                    putAwayHeader.setReferenceDocumentType(createdGRLine.getReferenceDocumentType());
                } else {
                    putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(createdGRLine.getInboundOrderTypeId()));
                }
                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                        languageId,
                        plantId,
                        warehouseId);

                putAwayHeader.setCompanyDescription(description.getCompanyDesc());
                putAwayHeader.setPlantDescription(description.getPlantDesc());
                putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2(companyCode, plantId, languageId, warehouseId,
                        createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());

                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());

                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());
            putAwayHeader.setCustomerCode(dbPreInboundHeader.getCustomerCode());
            putAwayHeader.setTransferRequestType(dbPreInboundHeader.getTransferRequestType());

                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                putAwayHeader.setStatusId(19L);
                statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
                putAwayHeader.setStatusDescription(statusDescription);

                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setCreatedBy(loginUserID);
                putAwayHeader.setUpdatedBy(loginUserID);
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());
                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                log.info("putAwayHeader : " + putAwayHeader);

                /*----------------Inventory tables Create---------------------------------------------*/
                InventoryV2 createdinventory = createInventoryNonCBMV2(createdGRLine);

                /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
            }
//            if (cbm == 0D) {
//                break outerloop;
//            }
//        }
    }

    /**
     * createGrLine
     *
     * @param newGrLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional
    public List<GrLineV2> createGrLineV2(@Valid List<AddGrLineV2> newGrLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        try {
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("A-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    dbGrLine.setStatusId(14L);

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            newGrLine.getCompanyCode(),
                            newGrLine.getPlantId(),
                            newGrLine.getLanguageId(),
                            newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(),
                            newGrLine.getPreInboundNo(),
                            newGrLine.getGoodsReceiptNo(),
                            newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(),
                            newGrLine.getItemCode(),
                            newGrLine.getManufacturerName(),
                            newGrLine.getLineNo()
                    );
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                    if(dbGrQty != null) {
                        Double totalGrQty = dbGrLine.getGoodReceiptQty() + dbGrQty;
                        if (newGrLine.getOrderQty() < totalGrQty){
                            throw new BadRequestException("Gr Qty is greater than Order Qty ");
                        }
                    }

                    //V2 Code
                    IKeyValuePair description = stagingLineV2Repository.getDescription(newGrLine.getCompanyCode(),
                            newGrLine.getLanguageId(),
                            newGrLine.getPlantId(),
                            newGrLine.getWarehouseId());

                    statusDescription = stagingLineV2Repository.getStatusDescription(dbGrLine.getStatusId(), newGrLine.getLanguageId());
                    dbGrLine.setStatusDescription(statusDescription);

                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    dbGrLine.setMiddlewareId(newGrLine.getMiddlewareId());
                    dbGrLine.setMiddlewareHeaderId(newGrLine.getMiddlewareHeaderId());
                    dbGrLine.setMiddlewareTable(newGrLine.getMiddlewareTable());
                    dbGrLine.setManufacturerFullName(newGrLine.getManufacturerFullName());
                    dbGrLine.setReferenceDocumentType(newGrLine.getReferenceDocumentType());
                    dbGrLine.setPurchaseOrderNumber(newGrLine.getPurchaseOrderNumber());

                    Double cbm = 0D;
                    Double cbmPerQty = 0D;
                    Double grQty = 0D;
                    Double length = 0D;
                    Double width = 0D;
                    Double height = 0D;
                    Double volume = 0D;
                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = 0D;
                    Double acceptQty = 0D;
                    Double damageQty = 0D;

                    boolean capacityCheck = false;

                    if (newGrLine.getGoodReceiptQty() != null) {
                        grQty = newGrLine.getGoodReceiptQty();
                    }
                    if (newGrLine.getOrderQty() != null) {
                        invoiceQty = newGrLine.getOrderQty();
                    }
                    if (newGrLine.getAcceptedQty() != null) {
                        acceptQty = newGrLine.getAcceptedQty();
                    }
                    if (newGrLine.getDamageQty() != null) {
                        damageQty = newGrLine.getDamageQty();
                    }

                    ImBasicData imBasicData = new ImBasicData();
                    imBasicData.setCompanyCodeId(newGrLine.getCompanyCode());
                    imBasicData.setPlantId(newGrLine.getPlantId());
                    imBasicData.setLanguageId(newGrLine.getLanguageId());
                    imBasicData.setWarehouseId(newGrLine.getWarehouseId());
                    imBasicData.setItemCode(newGrLine.getItemCode());
                    imBasicData.setManufacturerName(newGrLine.getManufacturerName());
                    ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 : " + itemCodeCapacityCheck);

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(newGrLine.getCompanyCode(),
                            newGrLine.getPlantId(),
                            newGrLine.getLanguageId(),
                            newGrLine.getWarehouseId(),
                            newGrLine.getPreInboundNo(),
                            newGrLine.getRefDocNumber(),
                            newGrLine.getLineNo(),
                            newGrLine.getItemCode(),
                            newGrLine.getManufacturerName());
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        dbGrLine.setOrderUom(dbStagingLineEntity.getOrderUom());
                        dbGrLine.setGrUom(dbStagingLineEntity.getOrderUom());
                    }

                    if (itemCodeCapacityCheck != null) {
                        if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                            capacityCheck = itemCodeCapacityCheck.getCapacityCheck();               //Capacity Check for putaway item
                        }
                    }
                    log.info("CapacityCheck -----------> : " + capacityCheck);

                    if (capacityCheck) {
                        if (itemCodeCapacityCheck != null) {
                            length = itemCodeCapacityCheck.getLength();
                            width = itemCodeCapacityCheck.getWidth();
                            height = itemCodeCapacityCheck.getHeight();
                            volume = length * width * height;
                            cbm = volume * grQty;
                            cbmPerQty = volume;
                        }
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, newGrLine.getLanguageId());
                        dbGrLine.setStatusDescription(statusDescription);
                    }

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }

                    dbGrLine.setCbm(cbm);
                    dbGrLine.setCbmQuantity(cbmPerQty);

                    dbGrLine.setBranchCode(newGrLine.getBranchCode());
                    dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                    dbGrLine.setIsCompleted(newGrLine.getIsCompleted());

                    dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
                            dbGrLine.getGoodsReceiptNo(), dbGrLine.getItemCode(), dbGrLine.getLineNo(),
                            dbGrLine.getLanguageId(), dbGrLine.getCompanyCode(), dbGrLine.getPlantId(),
                            dbGrLine.getRefDocNumber(), dbGrLine.getPackBarcodes(), dbGrLine.getWarehouseId(),
                            dbGrLine.getPreInboundNo(), dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        try {
                            createdGRLine = grLineV2Repository.save(dbGrLine);
                        } catch (Exception e) {
                            createGrLineError = true;

                            //Exception Log
                            createGrLineLog7(dbGrLine, e.toString());

                            throw new RuntimeException(e);
                        }
                        log.info("createdGRLine : " + createdGRLine);
                        createdGRLines.add(createdGRLine);

                        if (createdGRLine != null && !createGrLineError) {
                            // Record Insertion in PUTAWAYHEADER table
                            createPutAwayHeaderV2(createdGRLine, loginUserID);
                        }
                    }
                }
                log.info("Records were inserted successfully...");
            }

            // STATUS updates
            /*
             * Pass WH_ID/PRE_IB_NO/REF_DOC_NO/GR_NO/IB_LINE_NO/ITM_CODE in GRLINE table and
             * validate STATUS_ID of the all the filtered line items = 17 , if yes
             */
            List<StagingLineEntityV2> stagingLineList =
                    stagingLineService.getStagingLineForGrConfirmV2(
                            createdGRLines.get(0).getCompanyCode(),
                            createdGRLines.get(0).getPlantId(),
                            createdGRLines.get(0).getLanguageId(),
                            createdGRLines.get(0).getWarehouseId(),
                            createdGRLines.get(0).getRefDocNumber(),
                            createdGRLines.get(0).getPreInboundNo());

            Long createdStagingLinesCount = 0L;
            Long createdGRLinesStatusId17Count = 0L;

            if (stagingLineList != null) {
                createdStagingLinesCount = stagingLineList.stream().count();
            }
            createdGRLinesStatusId17Count = grLineV2Repository.getGrLineStatus17Count(createdGRLines.get(0).getCompanyCode(),
                    createdGRLines.get(0).getPlantId(),
                    createdGRLines.get(0).getLanguageId(),
                    createdGRLines.get(0).getWarehouseId(),
                    createdGRLines.get(0).getRefDocNumber(),
                    createdGRLines.get(0).getPreInboundNo(), 17L);
            log.info("createdGRLinesStatusId17Count: " + createdGRLinesStatusId17Count);

//            createdGRLinesStatusId17Count = createdGRLinesStatusId17Count + (createdGRLines.stream().filter(n -> n.getStatusId() == 17L).count());
            log.info("createdStagingLinesCount, createdGRLinesStatusId17Count: " + createdStagingLinesCount + ", " + createdGRLinesStatusId17Count);

            for (GrLineV2 grLine : createdGRLines) {
                /*
                 * 1. Update GRHEADER table with STATUS_ID=17 by Passing WH_ID/GR_NO/CASE_CODE/REF_DOC_NO and
                 * GR_CNF_BY with USR_ID and GR_CNF_ON with Server time
                 */
                if (createdStagingLinesCount.equals(createdGRLinesStatusId17Count)) {
                    log.info("Updating GrHeader with StatusId 17 Initiated");
                    List<GrHeaderV2> grHeaders = grHeaderV2Repository.getGrHeaderV2(
                            grLine.getWarehouseId(),
                            grLine.getGoodsReceiptNo(),
                            grLine.getCaseCode(),
                            grLine.getCompanyCode(),
                            grLine.getPlantId(),
                            grLine.getLanguageId(),
                            grLine.getRefDocNumber());
                    for (GrHeaderV2 grHeader : grHeaders) {
                        if (grHeader.getCompanyCode() == null) {
                            grHeader.setCompanyCode(grLine.getCompanyCode());
                        }
                        grHeader.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, grLine.getLanguageId());
                        grHeader.setStatusDescription(statusDescription);
                        grHeader.setCreatedBy(loginUserID);
                        grHeader.setUpdatedOn(new Date());
                        grHeader.setConfirmedOn(new Date());
                        grHeader = grHeaderV2Repository.save(grHeader);
                        log.info("grHeader updated: " + grHeader);
                    }
                }
                /*
                 * '2. 'Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE/CASECODE in STAGINIGLINE table and
                 * update STATUS_ID as 17
                 */

                log.info("Updating StagingLine and InboundLine with StatusId 17 Initiated");
                List<StagingLineEntityV2> stagingLineEntityList =
                        stagingLineService.getStagingLineV2(
                                grLine.getCompanyCode(),
                                grLine.getPlantId(),
                                grLine.getLanguageId(),
                                grLine.getWarehouseId(),
                                grLine.getRefDocNumber(),
                                grLine.getPreInboundNo(),
                                grLine.getLineNo(),
                                grLine.getItemCode(),
                                grLine.getCaseCode());
                for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityList) {

                    //v2 code
                    if (stagingLineEntity.getRec_accept_qty() == null) {
                        stagingLineEntity.setRec_accept_qty(0D);
                    }
                    if (grLine.getAcceptedQty() == null) {
                        grLine.setAcceptedQty(0D);
                    }
                    if (stagingLineEntity.getRec_damage_qty() == null) {
                        stagingLineEntity.setRec_damage_qty(0D);
                    }
                    if (grLine.getDamageQty() == null) {
                        grLine.setDamageQty(0D);
                    }

                    Double rec_accept_qty = stagingLineEntity.getRec_accept_qty() + grLine.getAcceptedQty();
                    Double rec_damage_qty = stagingLineEntity.getRec_damage_qty() + grLine.getDamageQty();

                    stagingLineEntity.setRec_accept_qty(rec_accept_qty);
                    stagingLineEntity.setRec_damage_qty(rec_damage_qty);

                    if (grLine.getStatusId() == 17L) {
                        stagingLineEntity.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, grLine.getLanguageId());
                        stagingLineEntity.setStatusDescription(statusDescription);
                    }
                    stagingLineEntity = stagingLineV2Repository.save(stagingLineEntity);
                    log.info("stagingLineEntity updated: " + stagingLineEntity);
                }

                /*
                 * 3. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
                 * updated STATUS_ID as 17
                 */
                if (grLine.getStatusId() == 17L) {
                    InboundLineV2 inboundLine = inboundLineV2Repository.getInboundLineV2(grLine.getWarehouseId(),
                            grLine.getLineNo(),
                            grLine.getPreInboundNo(),
                            grLine.getItemCode(),
                            grLine.getCompanyCode(),
                            grLine.getPlantId(),
                            grLine.getLanguageId(),
                            grLine.getRefDocNumber());
                    inboundLine.setStatusId(17L);
                    inboundLine.setStatusDescription(statusDescription);
                    inboundLine = inboundLineV2Repository.save(inboundLine);
                    log.info("inboundLine updated : " + inboundLine);
                }
            }
            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param grLineList
     * @return
     */
//    public static List<AddGrLineV2> getDuplicatesV2(List<AddGrLineV2> grLineList) {
//        return getDuplicatesMapV2(grLineList).values().stream()
//                .filter(duplicates -> duplicates.size() > 1)
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
//    }

    /**
     * @param addGrLineList
     * @return
     */
//    private static Map<String, List<AddGrLineV2>> getDuplicatesMapV2(List<AddGrLineV2> addGrLineList) {
//        return addGrLineList.stream().collect(Collectors.groupingBy(AddGrLineV2::uniqueAttributes));
//    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
//    private void createPutAwayHeaderV2(GrLineV2 createdGRLine, String loginUserID) {
//        Double cbm = null;
//
//        if (createdGRLine.getCbm() != null && createdGRLine.getCbm() == 0) {
//            cbm = null;
//        }
//        if (createdGRLine.getCbm() != null) {
//            cbm = createdGRLine.getCbm();
//        }
//
//        while (cbm == null || cbm != 0) {
//            //  ASS_HE_NO
//            if (createdGRLine != null && createdGRLine.getAssignedUserId() != null) {
//                // Insert record into PutAwayHeader
//                //private Double putAwayQuantity, private String putAwayUom;
//                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
//                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
//                putAwayHeader.setCompanyCodeId(createdGRLine.getCompanyCode());
//                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
//                // PA_NO
//                AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
//                long NUM_RAN_CODE = 7;
//                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE,
//                        createdGRLine.getCompanyCode(),
//                        createdGRLine.getPlantId(),
//                        createdGRLine.getLanguageId(),
//                        createdGRLine.getWarehouseId(),
//                        authTokenForIDMasterService.getAccess_token());
//                putAwayHeader.setPutAwayNumber(nextPANumber);
//
//                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
//
//                //set bar code id for packbarcode
//                putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());
//
//                //set pack bar code for actual packbarcode
//                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
//
//                //-----------------PROP_ST_BIN---------------------------------------------
//                /*
//                 * 1. Fetch ITM_CODE from GRLINE table and Pass WH_ID/ITM_CODE/BIN_CLASS_ID=1 in INVENTORY table and Fetch ST_BIN values.
//                 * Pass ST_BIN values into STORAGEBIN table  where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and
//                 * fetch the filtered values and sort the latest and insert.
//                 *
//                 * If WH_ID=111, fetch ST_BIN values of ST_SEC_ID= ZT and sort the latest and insert
//                 */
////				List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");
//
//                // Discussed to remove this condition
////			if (createdGRLine.getWarehouseId().equalsIgnoreCase(WAREHOUSEID_111)) {
////				storageSectionIds = Arrays.asList("ZT");
////			}
////			List<Inventory> stBinInventoryList =
////					inventoryRepository.findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(createdGRLine.getWarehouseId(),
////							createdGRLine.getItemCode(), 1L, 0L);
//
//                //V2 Code
////                Long binClassId = 1L;                   //currently(21-8-2023) hard code to get the output
//                Long binClassId = 0L;                   //actual code follows
//                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 2) {
//                    binClassId = 1L;
//                } else if (createdGRLine.getInboundOrderTypeId() == 3) {
//                    binClassId = 7L;
//                }
//
//                if (createdGRLine.getCbm() == null || cbm == null) {
//                    cbm = 0D;
//                }
//
//                List<InventoryV2> stBinInventoryList =
//                        inventoryV2Repository.findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(createdGRLine.getWarehouseId(),
//                                createdGRLine.getItemCode(), binClassId, 0L);
//                log.info("stBinInventoryList -----------> : " + stBinInventoryList);
//
//                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                if (createdGRLine.getInterimStorageBin() != null) {
//
//                    putAwayHeader.setProposedStorageBin(createdGRLine.getInterimStorageBin());
//                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//
//                    if (createdGRLine.getCbm() != null && createdGRLine.getCbm() != 0) {
//                        cbm = 0D;
//                    }
//
//                } else if (createdGRLine.getInterimStorageBin() == null && !stBinInventoryList.isEmpty()) {
//                    List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin).collect(Collectors.toList());
//                    log.info("stBins -----------> : " + stBins);
//
//                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//                    storageBinPutAway.setStorageBin(stBins);
//                    storageBinPutAway.setBinClassId(binClassId);
//                    storageBinPutAway.setWarehouseId(createdGRLine.getWarehouseId());
//                    StorageBinV2[] storageBin = mastersService.getStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                    log.info("storagebin -----------> : " + storageBin);
//                    if (createdGRLine.getCbm() == null || cbm == null) {
//                        cbm = 0D;
//                    }
//                    List<StorageBinV2> stBinList = new ArrayList<>();
//                    if (createdGRLine.getCbm() != null) {
//                        Double cbmPerQuantity = createdGRLine.getCbmQuantity();
////                        List<StorageBinV2> stBinList = Arrays.stream(storageBin)
////                                .filter(n -> n.getCapacityCheck() == 1 &&
////                                        cbmPerQuantity < Double.valueOf(n.getRemainingVolume()))
////                                .sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());
//
//                        for (StorageBinV2 storageBinV2 : storageBin) {
//                            if (storageBinV2.getRemainingVolume() == null) {
//                                storageBinV2.setRemainingVolume("0");
//                            }
//                            if (storageBinV2.isCapacityCheck() && cbmPerQuantity < Double.valueOf(storageBinV2.getRemainingVolume())) {
//                                stBinList.add(storageBinV2);
//                            }
//                        }
//
//                        if (stBinList != null && stBinList.size() > 0) {
//                            stBinList.stream().sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());
//                        }
//
//                        if (stBinList != null && stBinList.size() > 0) {
//                            String proposedStorageBin = stBinList.get(0).getStorageBin();
//
//                            if (stBinList.get(0).getTotalVolume() == null) {
//                                stBinList.get(0).setTotalVolume("0");
//                            }
//                            if (stBinList.get(0).getAllocatedVolume() == null) {
//                                stBinList.get(0).setAllocatedVolume(0D);
//                            }
//                            if (stBinList.get(0).getOccupiedVolume() == null) {
//                                stBinList.get(0).setOccupiedVolume("0");
//                            }
//                            if (stBinList.get(0).getRemainingVolume() == null) {
//                                stBinList.get(0).setRemainingVolume("0");
//                            }
//
//                            Double allocatedVolume;
//                            Double occupiedVolume = Double.valueOf(stBinList.get(0).getOccupiedVolume());
//                            Double remainingVolume = Double.valueOf(stBinList.get(0).getRemainingVolume());
//
//                            Integer qty = (int) (remainingVolume / cbmPerQuantity);
//                            allocatedVolume = qty * cbmPerQuantity;
//                            remainingVolume = remainingVolume - (allocatedVolume + occupiedVolume);
//                            cbm = cbm - allocatedVolume;
//
//                            StorageBinV2 modifiedStorageBin = new StorageBinV2();
//                            modifiedStorageBin.setRemainingVolume(String.valueOf(remainingVolume));
//                            modifiedStorageBin.setAllocatedVolume(allocatedVolume);
//
//                            StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(proposedStorageBin,
//                                    modifiedStorageBin,
//                                    createdGRLine.getCompanyCode(),
//                                    createdGRLine.getPlantId(),
//                                    createdGRLine.getLanguageId(),
//                                    createdGRLine.getWarehouseId(),
//                                    loginUserID,
//                                    authTokenForMastersService.getAccess_token());
//
//                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                            putAwayHeader.setPutAwayQuantity((remainingVolume / createdGRLine.getCbm()) * createdGRLine.getGoodReceiptQty());
//                        }
//                    }
//                    if ((stBinList == null || stBinList.isEmpty() || stBinList.size() == 0) && storageBin != null && storageBin.length > 0) {
//                        List<StorageBinV2> stbin = Arrays.stream(storageBin).sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());
////                        putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
//                        putAwayHeader.setProposedStorageBin(stbin.get(0).getStorageBin());
//                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//                        if (createdGRLine.getCbm() != null && createdGRLine.getCbm() != 0) {
//                            cbm = 0D;
//                        }
//                    }
//                } else {
//                    // If ST_BIN value is null
//                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
//                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
//
//                    if (createdGRLine.getCbm() != null && createdGRLine.getCbm() != 0) {
//                        cbm = 0D;
//                    }
//
//                    log.info("QuantityType : " + createdGRLine.getQuantityType());
//                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
//                        StorageBin[] storageBinEMPTY =
//                                mastersService.getStorageBinByStatus(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
//                        log.info("storageBinEMPTY -----------> : " + storageBinEMPTY);
//                        List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
//                        List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
//
//                        /*
//                         * Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZB,ZG,ZD,ZC,ZT and
//                         * PUTAWAY_BLOCK and PICK_BLOCK columns are Null( FALSE) and fetch the filteerd values and
//                         * Sort the latest and Insert.
//                         */
//                        // Prod Issue - SQL Grammer on StorageBin-----23-08-2022
//                        // Start
//                        if (stBins != null && stBins.size() > 2000) {
//                            List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
//                            StorageBinV2[] storageBin = getStorageBinForSplitedListV2(splitedList, binClassId,
//                                    createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
//
//                            // Provided Null else validation
//                            log.info("storageBin2 -----------> : " + storageBin);
//                            if (storageBin != null && storageBin.length > 0) {
//                                putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
//                            } else {
//                                Long binClassID = 2L;
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        createdGRLine.getCompanyCode(), createdGRLine.getPlantId(),
//                                        createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                            }
//                        } else {
//                            StorageBinV2[] storageBin = getStorageBinV2(binClassId, stBins, createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
//                            if (storageBin != null && storageBin.length > 0) {
//                                putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
//                            } else {
//                                Long binClassID = 2L;
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        createdGRLine.getCompanyCode(), createdGRLine.getPlantId(),
//                                        createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                            }
//                        }
//                        // End
//                    }
//
//                    /*
//                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
//                     * fetch ST_BIN values for STATUS_ID=EMPTY.
//                     */
//                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
//                        StorageBinV2[] storageBinEMPTY =
//                                mastersService.getStorageBinByStatusV2(createdGRLine.getWarehouseId(), 0L, authTokenForMastersService.getAccess_token());
//                        List<StorageBinV2> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
//                        List<String> stBins = storageBinEMPTYList.stream().map(StorageBinV2::getStorageBin).collect(Collectors.toList());
//
//                        // Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZD and PUTAWAY_BLOCK and
//                        // PICK_BLOCK columns are Null( FALSE)
//                        if (stBins != null && stBins.size() > 2000) {
//                            List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
//                            StorageBinV2[] storageBin = getStorageBinForSplitedListV2(splitedList, binClassId,
//                                    createdGRLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
//                            if (storageBin != null && storageBin.length > 0) {
//                                putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
//                            } else {
//                                Long binClassID = 2L;
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        createdGRLine.getCompanyCode(), createdGRLine.getPlantId(),
//                                        createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                            }
//                        } else {
//                            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//                            storageBinPutAway.setStorageBin(stBins);
//                            storageBinPutAway.setBinClassId(binClassId);
//                            storageBinPutAway.setWarehouseId(createdGRLine.getWarehouseId());
//                            StorageBinV2[] storageBin = mastersService.getStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                            if (storageBin != null && storageBin.length > 0) {
//                                putAwayHeader.setProposedStorageBin(storageBin[0].getStorageBin());
//                            } else {
//                                Long binClassID = 2L;
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        createdGRLine.getCompanyCode(), createdGRLine.getPlantId(),
//                                        createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                            }
//                        }
//                    }
//                }
//
//                /////////////////////////////////////////////////////////////////////////////////////////////////////
//
//                //PROP_HE_NO	<- PAWAY_HE_NO
//                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
//                putAwayHeader.setStatusId(19L);
//                statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
//                putAwayHeader.setStatusDescription(statusDescription);
//                putAwayHeader.setDeletionIndicator(0L);
//                putAwayHeader.setCreatedBy(loginUserID);
//                putAwayHeader.setCreatedOn(new Date());
//                putAwayHeader.setUpdatedBy(loginUserID);
//                putAwayHeader.setUpdatedOn(new Date());
//                putAwayHeader.setConfirmedOn(new Date());
//                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
//                log.info("putAwayHeader : " + putAwayHeader);
//
//                /*----------------Inventory tables Create---------------------------------------------*/
//                InventoryV2 createdinventory = createInventoryV2(createdGRLine);
//
//                /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory);
//            }
//        }
//    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
    private void createPutAwayHeaderV2(GrLineV2 createdGRLine, String loginUserID) throws java.text.ParseException {
        String itemCode = createdGRLine.getItemCode();
        String companyCode = createdGRLine.getCompanyCode();
        String plantId = createdGRLine.getPlantId();
        String languageId = createdGRLine.getLanguageId();
        String warehouseId = createdGRLine.getWarehouseId();
        String proposedStorageBin = createdGRLine.getInterimStorageBin();

        Double cbm = 0D;
        Double cbmPerQuantity = 0D;
        Double allocatedVolume = 0D;
        Double occupiedVolume = 0D;
        Double remainingVolume = 0D;
        Double totalVolume = 0D;
        Double putawayQty = 0D;

        boolean capacityCheck = false;
        boolean storageBinCapacityCheck = false;

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        if (createdGRLine.getCbm() != null) {
            cbm = createdGRLine.getCbm();
            log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
        }
        outerloop:
//        while (true) {
            //  ASS_HE_NO
//            if (createdGRLine != null && createdGRLine.getAssignedUserId() != null) {
            if (createdGRLine != null) {
                // Insert record into PutAwayHeader
                //private Double putAwayQuantity, private String putAwayUom;
                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                putAwayHeader.setCompanyCodeId(companyCode);
                putAwayHeader.setReferenceField5(itemCode);

                // PA_NO
                long NUM_RAN_CODE = 7;
                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number

//                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

                //set bar code id for packbarcode
                putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());

                //set pack bar code for actual packbarcode
                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());

                //-----------------PROP_ST_BIN---------------------------------------------

                //V2 Code
//                Long binClassId = 1L;                   //currently(21-8-2023) hard code to get the output
                Long binClassId = 0L;                   //actual code follows
                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
                    binClassId = 1L;
                }
                if (createdGRLine.getInboundOrderTypeId() == 2) {
                    binClassId = 7L;
                }
                log.info("BinClassId : " + binClassId);

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(createdGRLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                    capacityCheck = itemCodeCapacityCheck.getCapacityCheck();               //Capacity Check for putaway item
                }
                log.info("CapacityCheck -----------> : " + capacityCheck);

                List<InventoryV2> stBinInventoryList = inventoryService.getInventoryForPutawayHeader(itemCode, createdGRLine.getManufacturerName(),
                        binClassId, companyCode, plantId, languageId, warehouseId);
//                inventoryV2Repository.findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(warehouseId, itemCode, binClassId, 0L);
                log.info("stBinInventoryList -----------> : " + stBinInventoryList);

                List<String> inventoryStorageBinList = null;
                if (stBinInventoryList != null) {
                    inventoryStorageBinList = stBinInventoryList.stream().map(InventoryV2::getStorageBin).collect(Collectors.toList());
                }
                log.info("Inventory StorageBin List: " + inventoryStorageBinList);

                List<StorageBinV2> stBinList = new ArrayList<>();
                List<StorageBinV2> filterStorageBinList = new ArrayList<>();

                if (createdGRLine.getInterimStorageBin() != null) {

                    allocatedVolume = 0D;
                    occupiedVolume = 0D;
                    remainingVolume = 0D;
                    totalVolume = 0D;

                    StorageBinV2 storageBin = mastersService.getStorageBinV2(proposedStorageBin, warehouseId, companyCode, plantId, languageId, authTokenForMastersService.getAccess_token());
                    log.info("InterimStorageBin: " + storageBin);
                    if (storageBin != null) {
                        if (storageBin.isCapacityCheck()) {
                            storageBinCapacityCheck = storageBin.isCapacityCheck();
                        }

                    log.info("storageBinCapacityCheck -----------> : " + storageBinCapacityCheck);

                    if (storageBinCapacityCheck && capacityCheck) {
                        if (createdGRLine.getCbmQuantity() != null) {
                            cbmPerQuantity = createdGRLine.getCbmQuantity();
                        }

                        if (storageBin.getRemainingVolume() != null && !storageBin.getRemainingVolume().equals("")) {
                            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                        }

                        if (storageBinCapacityCheck && cbmPerQuantity < remainingVolume) {
                            if (storageBin.getTotalVolume() != null && !storageBin.getTotalVolume().equals("")) {
                                totalVolume = Double.valueOf(storageBin.getTotalVolume());
                            }
                            if (storageBin.getOccupiedVolume() != null && !storageBin.getOccupiedVolume().equals("")) {
                                occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
                            }
                            if (storageBin.getRemainingVolume() != null && !storageBin.getRemainingVolume().equals("")) {
                                remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                            }
                            if (createdGRLine.getCbm() != null) {
                                cbm = createdGRLine.getCbm();
                            }

                            allocatedVolume = cbm;

                            if (allocatedVolume <= remainingVolume) {
                                allocatedVolume = cbm;
                            }
                            if (allocatedVolume > remainingVolume) {
                                allocatedVolume = remainingVolume;
                            }
                            if (totalVolume >= remainingVolume) {
                                remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                            } else {
                                remainingVolume = remainingVolume - allocatedVolume;
                            }
                            occupiedVolume = occupiedVolume + allocatedVolume;
                            cbm = cbm - allocatedVolume;
                            log.info("occupiedVolume, remainingVolume, cbm: " + occupiedVolume + ", " + remainingVolume + ", " + cbm);

                            putawayQty = (allocatedVolume / createdGRLine.getCbm()) * createdGRLine.getGoodReceiptQty();
                            log.info("Putaway Qty: " + putawayQty);

                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, proposedStorageBin,
                                    companyCode, plantId, languageId, warehouseId, loginUserID, authTokenForMastersService.getAccess_token());

                            putAwayHeader.setPutAwayQuantity(putawayQty);
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        }
                    }

                    if (!capacityCheck && storageBinCapacityCheck) {
                        // Exception Log
                        createGrLineLog9(storageBin, "Storage Bin Capacity Check is enabled whereas item capacity check is disabled.");
                        throw new RuntimeException("Storage Bin Capacity Check is enabled whereas item capacity check is disabled ");
                    }
                    if (capacityCheck && !storageBinCapacityCheck) {
                        // Exception Log
                        createGrLineLog8(itemCodeCapacityCheck, "Item Capacity Check is enabled whereas Storage Bin capacity check is disabled.");
                        throw new RuntimeException("item Capacity Check is enabled whereas Storage Bin capacity check is disabled ");
                    }

                    if (!capacityCheck && !storageBinCapacityCheck) {                           //Item Capacity False and Storage Capacity False
                        putAwayHeader.setProposedStorageBin(createdGRLine.getInterimStorageBin());
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        cbm = 0D;               //to break the loop
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 5) {                           //Direct Stock Receipt - Fixed Bin
                        putAwayHeader.setProposedStorageBin(createdGRLine.getInterimStorageBin());
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        cbm = 0D;               //to break the loop
                    }
                }
            }
                //BinClassId - 7 - Return Order(Sale Return)
                if (createdGRLine.getInboundOrderTypeId() == 2) {

                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                    storageBinPutAway.setCompanyCodeId(companyCode);
                    storageBinPutAway.setPlantId(plantId);
                    storageBinPutAway.setLanguageId(languageId);
                    storageBinPutAway.setWarehouseId(warehouseId);
                    storageBinPutAway.setBinClassId(binClassId);
                    log.info("BinClassId : " + binClassId);

                    putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
                    log.info("Return Order PutAway Qty: " + putawayQty);
                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    if (proposedBinClass7Bin != null) {
                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStBin);
                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                        log.info("Return Order --> BinClassId7 Proposed Bin: " + proposedStBin);
                        cbm = 0D;   //break the loop
                    }
                    if (proposedBinClass7Bin == null) {
                        binClassId = 2L;
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 stBin = mastersService.getStorageBin(
                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                        log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                        cbm = 0D;   //break the loop
                    }

                    putAwayHeader.setPutAwayQuantity(putawayQty);
                }

                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                    if (stBinInventoryList != null) {

                        allocatedVolume = 0D;
                        occupiedVolume = 0D;
                        remainingVolume = 0D;
                        totalVolume = 0D;

                        String cbmPerQty = "0";                     //this declaration is only to get storage bin list
                        if (createdGRLine.getCbmQuantity() != null) {
                            cbmPerQty = String.valueOf(createdGRLine.getCbmQuantity());
                        }
                        if (stBinInventoryList != null) {
                            if (capacityCheck) {
                                stBinList = storageBinRepository.getStorageBinListByCompanyBranch(itemCode, warehouseId, companyCode, plantId, languageId,
                                        cbmPerQty, binClassId, createdGRLine.getManufacturerName());
                            }
                        }
                        log.info("stBinCompanyBranchFilterList -----------> : " + stBinList);
                        log.info("BinClassId : " + binClassId);

                            if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                                if (!capacityCheck) {

                                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                                    storageBinPutAway.setCompanyCodeId(companyCode);
                                    storageBinPutAway.setPlantId(plantId);
                                    storageBinPutAway.setLanguageId(languageId);
                                    storageBinPutAway.setWarehouseId(warehouseId);

                                    if(createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                        storageBinPutAway.setBinClassId(binClassId);
                                        storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                        StorageBinV2 proposedExistingBin = mastersService.getExistingStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                        if (proposedExistingBin != null) {
                                            proposedStorageBin = proposedExistingBin.getStorageBin();
                                            log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                        putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                        }
                                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                                    log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                                    cbm = 0D;   //break the loop
                                }
                                if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                    storageBinPutAway.setBinClassId(7L);
                                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                    if (proposedBinClass7Bin != null) {
                                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
                                        putAwayHeader.setProposedStorageBin(proposedStBin);
                                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                                        log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
                                        cbm = 0D;   //break the loop
                                    }
                                }
                            }
                        }

                        if (capacityCheck) {

                            cbmPerQuantity = createdGRLine.getCbmQuantity();

                            for (StorageBinV2 storageBinV2 : stBinList) {                   //filter capacity enabled bin
                                storageBinCapacityCheck = storageBinV2.isCapacityCheck();
                                if (storageBinV2.getRemainingVolume() != null && !storageBinV2.getRemainingVolume().equals("")) {
                                    remainingVolume = Double.valueOf(storageBinV2.getRemainingVolume());
                                }
                                if (storageBinCapacityCheck && cbmPerQuantity < remainingVolume) {
                                    filterStorageBinList.add(storageBinV2);
                                }
                            }
                            log.info("capacity check storagebin -----------> : " + filterStorageBinList);
                            if (filterStorageBinList != null && filterStorageBinList.size() > 0) {
                                filterStorageBinList.stream().sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());

                                StorageBinV2 proposedBin = filterStorageBinList.get(0);
                                proposedStorageBin = proposedBin.getStorageBin();

                                if (proposedBin.getTotalVolume() != null && !proposedBin.getTotalVolume().equals("")) {
                                    totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                                }
                                if (proposedBin.getOccupiedVolume() != null && !proposedBin.getOccupiedVolume().equals("")) {
                                    occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                                }
                                if (proposedBin.getRemainingVolume() != null && !proposedBin.getRemainingVolume().equals("")) {
                                    remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
                                }

                                allocatedVolume = cbm;
                                log.info("allocatedVolume, cbm: " + allocatedVolume + ", " + cbm);

                                if (allocatedVolume <= remainingVolume) {
                                    allocatedVolume = cbm;
                                }
                                if (allocatedVolume > remainingVolume) {
                                    allocatedVolume = remainingVolume;
                                }
                                log.info("allocatedVolume, remainingVolume: " + allocatedVolume + ", " + remainingVolume);

                                if (totalVolume >= remainingVolume) {
                                    remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                                } else {
                                    remainingVolume = remainingVolume - allocatedVolume;
                                }
                                occupiedVolume = occupiedVolume + allocatedVolume;
                                cbm = cbm - allocatedVolume;
                                log.info("occupiedVolume, remainingVolume, cbm: " + occupiedVolume + ", " + remainingVolume + ", " + cbm);

                                putawayQty = (allocatedVolume / createdGRLine.getCbm()) * createdGRLine.getGoodReceiptQty();
                                log.info("Putaway Qty: " + putawayQty);

                                updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, proposedStorageBin,
                                        companyCode, plantId, languageId, warehouseId, loginUserID, authTokenForMastersService.getAccess_token());

                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setPutAwayQuantity(putawayQty);
                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                            }
                        }
                    }
                }

//                if (stBinInventoryList == null || filterStorageBinList == null || filterStorageBinList.isEmpty()) {
//                    // If ST_BIN value is null
//                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
//                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
//                    if (createdGRLine.getCbmQuantity() != null) {
//                        cbmPerQuantity = createdGRLine.getCbmQuantity();
//                    }
//                    log.info("QuantityType : " + createdGRLine.getQuantityType());
//                    log.info("BinClassId : " + binClassId);
//                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
//
//                        StorageBin[] storageBinEMPTY =
//                                mastersService.getStorageBinByStatusV2(companyCode, plantId, languageId, warehouseId, 0L, authTokenForMastersService.getAccess_token());
//
//                        log.info("storageBinEMPTY -----------> : " + storageBinEMPTY);
//
//                        List<StorageBin> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
//                        List<String> stBins = storageBinEMPTYList.stream().map(StorageBin::getStorageBin).collect(Collectors.toList());
//
//                        log.info("storageBinEMPTY -----------> : " + stBins);
//
//                        // Prod Issue - SQL Grammer on StorageBin-----23-08-2022
//                        // Start
//                        if (stBins != null && stBins.size() > 2000) {
//                            List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
//                            StorageBinV2[] storageBin = getStorageBinForSplitedListV2(splitedList, binClassId, companyCode, plantId, languageId, warehouseId, authTokenForMastersService.getAccess_token());
//
//                            // Provided Null else validation
//                            log.info("storageBin2k -----------> : " + storageBin);
//                            if (storageBin != null && storageBin.length > 0) {
//
//                                proposedStorageBin = proposedEmptyStorageBin(capacityCheck, storageBin, cbm, cbmPerQuantity, loginUserID, authTokenForMastersService.getAccess_token());
//                                if (proposedStorageBin != null) {
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//
//                                } else {
//                                    Long binClassID = 2L;
//                                    StorageBinV2 stBin = mastersService.getStorageBin(
//                                            companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                }
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//
//                            } else {
//                                Long binClassID = 2L;
//                                log.info("BinClassID : " + binClassID);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            }
//                        } else {
//                            StorageBinV2[] storageBin = getStorageBinV2(binClassId, stBins, companyCode, plantId, languageId, warehouseId, authTokenForMastersService.getAccess_token());
//                            log.info("storageBin2 -----------> : " + storageBin);
//                            if (storageBin != null && storageBin.length > 0) {
//                                proposedStorageBin = proposedEmptyStorageBin(capacityCheck, storageBin, cbm, cbmPerQuantity, loginUserID, authTokenForMastersService.getAccess_token());
//                                if (proposedStorageBin != null) {
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                } else {
//                                    Long binClassID = 2L;
//                                    log.info("BinClassID : " + binClassID);
//                                    StorageBinV2 stBin = mastersService.getStorageBin(
//                                            companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                }
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            } else {
//                                Long binClassID = 2L;
//                                log.info("BinClassID : " + binClassID);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            }
//                        }
//                        // End
//                    }
//
//                    /*
//                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
//                     * fetch ST_BIN values for STATUS_ID=EMPTY.
//                     */
//                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
//                        binClassId = 7L;
//                        StorageBinV2[] storageBinEMPTY =
//                                mastersService.getStorageBinByStatusV2(companyCode, plantId, languageId, warehouseId, 0L, authTokenForMastersService.getAccess_token());
//                        log.info("storageBinEmpty for D -----------> : " + storageBinEMPTY);
//                        log.info("BinClassId : " + binClassId);
//                        List<StorageBinV2> storageBinEMPTYList = Arrays.asList(storageBinEMPTY);
//                        List<String> stBins = storageBinEMPTYList.stream().map(StorageBinV2::getStorageBin).collect(Collectors.toList());
//                        log.info("storageBinEmpty for D -----------> : " + stBins);
//                        // Pass ST_BIN values into STORAGEBIN table  where where ST_SEC_ID = ZD and PUTAWAY_BLOCK and
//                        // PICK_BLOCK columns are Null( FALSE)
//                        if (stBins != null && stBins.size() > 2000) {
//                            List<List<String>> splitedList = CommonUtils.splitArrayList(stBins, 1800); // SQL Query accepts max 2100 count only in IN condition
//                            StorageBinV2[] storageBin = getStorageBinForSplitedListV2(splitedList, binClassId, companyCode, plantId, languageId, warehouseId, authTokenForMastersService.getAccess_token());
//                            log.info("storageBin split list -----------> : " + storageBin);
//                            if (storageBin != null && storageBin.length > 0) {
//                                proposedStorageBin = proposedEmptyStorageBin(capacityCheck, storageBin, cbm, cbmPerQuantity, loginUserID, authTokenForMastersService.getAccess_token());
//                                if (proposedStorageBin != null) {
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                } else {
//                                    Long binClassID = 2L;
//                                    log.info("BinClassID : " + binClassID);
//                                    StorageBinV2 stBin = mastersService.getStorageBin(
//                                            companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                }
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            } else {
//                                Long binClassID = 2L;
//                                log.info("BinClassID : " + binClassID);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            }
//                        } else {
//                            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//                            storageBinPutAway.setStorageBin(stBins);
//                            storageBinPutAway.setBinClassId(binClassId);
//                            storageBinPutAway.setCompanyCodeId(companyCode);
//                            storageBinPutAway.setPlantId(plantId);
//                            storageBinPutAway.setLanguageId(languageId);
//                            storageBinPutAway.setWarehouseId(createdGRLine.getWarehouseId());
//                            StorageBinV2[] storageBin = mastersService.getStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                            log.info("storageBin split list -----------> : " + storageBin);
//                            if (storageBin != null && storageBin.length > 0) {
//                                proposedStorageBin = proposedEmptyStorageBin(capacityCheck, storageBin, cbm, cbmPerQuantity, loginUserID, authTokenForMastersService.getAccess_token());
//                                if (proposedStorageBin != null) {
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                } else {
//                                    Long binClassID = 2L;
//                                    log.info("BinClassID : " + binClassID);
//                                    StorageBinV2 stBin = mastersService.getStorageBin(
//                                            companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                }
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            } else {
//                                Long binClassID = 2L;
//                                log.info("BinClassID : " + binClassID);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassID, authTokenForMastersService.getAccess_token());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//
//                                putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                putAwayHeader.setPutAwayQuantity(putawayQty);
//                                cbm = 0D;   //break the loop
//                            }
//                        }
//                    }
//                }

                //Last Picked Bin as Proposed Bin If it is empty
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || filterStorageBinList == null || filterStorageBinList.isEmpty())) {

                    StorageBinV2 proposedCBMStorageBin = null;
                    StorageBinV2 proposedCbmPerQtyStorageBin = null;

                    String proposedStBin = null;

                    List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineForLastBinCheckV2(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                    log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
                    List<String> lastPickedStorageBinList = null;
                    if (pickupLineList != null) {
                        lastPickedStorageBinList = pickupLineList.stream().map(PickupLineV2::getPickedStorageBin).collect(Collectors.toList());
                    }
                    log.info("LastPickedStorageBinList: " + lastPickedStorageBinList);

                    if (lastPickedStorageBinList != null && !lastPickedStorageBinList.isEmpty()) {

                        log.info("BinClassId : " + binClassId);

                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setCompanyCodeId(companyCode);
                        storageBinPutAway.setPlantId(plantId);
                        storageBinPutAway.setLanguageId(languageId);
                        storageBinPutAway.setWarehouseId(warehouseId);
                        storageBinPutAway.setStatusId(0L);
                        storageBinPutAway.setBinClassId(1L);
                        storageBinPutAway.setStorageBin(lastPickedStorageBinList);

                        putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);

                        if (capacityCheck) {
                            log.info("Last Pick --->CapacityCheck:" + capacityCheck);

                            storageBinPutAway.setCbm(cbm);
                            proposedCBMStorageBin = mastersService.getStorageBinCBMLastPick(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedCBMStorageBin != null) {
                                log.info("proposedCBMStorageBin: " + proposedCBMStorageBin.getStorageBin());
                                proposedStBin = proposedCBMStorageBin.getStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedCBMStorageBin.getFloorId()));
                                emptyStorageBinUpdate(proposedCBMStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
                                cbm = 0D;   //break the loop
                            }

                            if (proposedCBMStorageBin == null) {
                                storageBinPutAway.setCbmPerQty(cbmPerQuantity);
                                proposedCbmPerQtyStorageBin = mastersService.getStorageBinCBMPerQtyLastPick(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedCbmPerQtyStorageBin != null) {
                                    log.info("proposedCbmPerQtyStorageBin: " + proposedCbmPerQtyStorageBin.getStorageBin());
                                    proposedStBin = proposedCbmPerQtyStorageBin.getStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedCbmPerQtyStorageBin.getFloorId()));
                                    Double remainingCbm = emptyCbmPerQtyStorageBinUpdate(proposedCbmPerQtyStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
                                    cbm = cbm - remainingCbm;
                                    putawayQty = putawayQuantityCalculation(remainingCbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
                                }
                            }
                            putAwayHeader.setPutAwayQuantity(putawayQty);
                        }
                        if (!capacityCheck) {
                            log.info("Last Pick --->CapacityCheck:" + capacityCheck);

                            StorageBinV2 proposedNonCbmLastPickStorageBin = mastersService.getStorageBinNonCbmLastPicked(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            log.info("proposedNonCbmLastPickStorageBin: " + proposedNonCbmLastPickStorageBin);
                            if (proposedNonCbmLastPickStorageBin != null) {
                                putAwayHeader.setProposedStorageBin(proposedNonCbmLastPickStorageBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmLastPickStorageBin.getFloorId()));
                                putAwayHeader.setPutAwayQuantity(putawayQty);
                                log.info("LastPick NonCBM Bin: " + proposedNonCbmLastPickStorageBin.getStorageBin());
                                log.info("LastPick NonCBM PutawayQty: " + putawayQty);
                                cbm = 0D;   //break the loop
                            }
                        }
                    }
                }

                //Propose Empty Bin if Last picked bin is unavailable
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || filterStorageBinList == null || filterStorageBinList.isEmpty())) {
                    // If ST_BIN value is null
                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
                    if (createdGRLine.getCbmQuantity() != null) {
                        cbmPerQuantity = createdGRLine.getCbmQuantity();
                    }
                    log.info("QuantityType : " + createdGRLine.getQuantityType());
                    log.info("BinClassId : " + binClassId);

                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                    storageBinPutAway.setCompanyCodeId(companyCode);
                    storageBinPutAway.setPlantId(plantId);
                    storageBinPutAway.setLanguageId(languageId);
                    storageBinPutAway.setWarehouseId(warehouseId);
                    storageBinPutAway.setStatusId(0L);

                    StorageBinV2 proposedCBMStorageBin = null;
                    StorageBinV2 proposedCbmPerQtyStorageBin = null;
                    StorageBinV2 proposedNonCbmStorageBin = null;

                    String proposedStBin = null;

                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                        storageBinPutAway.setBinClassId(binClassId);
                        putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
                        if (capacityCheck && binClassId != 7) {
                            log.info("CapacityCheck:" + capacityCheck);
                            storageBinPutAway.setCbm(cbm);
                            proposedCBMStorageBin = mastersService.getStorageBinCBM(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedCBMStorageBin != null) {
                                log.info("proposedCBMStorageBin: " + proposedCBMStorageBin.getStorageBin());
                                proposedStBin = proposedCBMStorageBin.getStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedCBMStorageBin.getFloorId()));
                                emptyStorageBinUpdate(proposedCBMStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
                                cbm = 0D;   //break the loop
                            }

                            if (proposedCBMStorageBin == null) {
                                storageBinPutAway.setCbmPerQty(cbmPerQuantity);
                                proposedCbmPerQtyStorageBin = mastersService.getStorageBinCbmPerQty(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedCbmPerQtyStorageBin != null) {
                                    log.info("proposedCbmPerQtyStorageBin: " + proposedCbmPerQtyStorageBin.getStorageBin());
                                    proposedStBin = proposedCbmPerQtyStorageBin.getStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedCbmPerQtyStorageBin.getFloorId()));
                                    Double remainingCbm = emptyCbmPerQtyStorageBinUpdate(proposedCbmPerQtyStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
                                    cbm = cbm - remainingCbm;
                                    putawayQty = putawayQuantityCalculation(remainingCbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
                                }
                            }

                            if (proposedCBMStorageBin == null && proposedCbmPerQtyStorageBin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("A --> CBM reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                cbm = 0D;   //break the loop
                            }
                        }

                        if (capacityCheck && binClassId == 7) {
                            log.info("CapacityCheck:" + capacityCheck);
                            binClassId = 7L;
                            storageBinPutAway.setBinClassId(binClassId);
                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedNonCbmStorageBin != null) {
                                proposedStBin = proposedNonCbmStorageBin.getStorageBin();
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(proposedStBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));

                                cbm = 0D;   //break the loop
                            }

                            if (proposedNonCbmStorageBin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                cbm = 0D;   //break the loop
                            }
                        }

                        if (!capacityCheck) {
                            log.info("CapacityCheck:" + capacityCheck);
                            List<PutAwayHeaderV2> existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV2(companyCode, plantId, languageId, warehouseId,
                                    itemCode, createdGRLine.getManufacturerName());
                            log.info("existingBinItemCheck: " + existingBinItemCheck);
                            if (existingBinItemCheck != null && !existingBinItemCheck.isEmpty()) {
                                proposedStorageBin = existingBinItemCheck.get(0).getProposedStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.get(0).getLevelId()));
                                log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                cbm = 0D;   //break the loop
                            }
                            List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                            log.info("existingBinCheck: " + existingBinCheck);
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                                storageBinPutAway.setStorageBin(existingBinCheck);
                                proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                    cbm = 0D;   //break the loop
                                }
                            }
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                                List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                                log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                                log.info("BinClassId: " + binClassId);
                                storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                                proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));

                                    cbm = 0D;   //break the loop
                                }
                                if (proposedNonCbmStorageBin == null) {
                                    binClassId = 2L;
                                    log.info("BinClassId : " + binClassId);
                                    StorageBinV2 stBin = mastersService.getStorageBin(
                                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                    log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                    cbm = 0D;   //break the loop
                                }
                            }
                        }
                        putAwayHeader.setPutAwayQuantity(putawayQty);
                    }

                    /*
                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
                     * fetch ST_BIN values for STATUS_ID=EMPTY.
                     */
                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                        binClassId = 7L;
                        storageBinPutAway.setBinClassId(binClassId);
                        putawayQty = putawayQuantityCalculation(cbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);

//                        if (capacityCheck && binClassId != 7) {
//                            log.info("CapacityCheck:" + capacityCheck);
//                            storageBinPutAway.setCbm(cbm);
//                            proposedCBMStorageBin = mastersService.getStorageBinCBM(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                            if (proposedCBMStorageBin != null) {
//                                proposedStBin = proposedCBMStorageBin.getStorageBin();
//                                log.info("proposedCBMStorageBin: " + proposedCBMStorageBin.getStorageBin());
//                                putAwayHeader.setProposedStorageBin(proposedStBin);
//                                emptyStorageBinUpdate(proposedCBMStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
//                                cbm = 0D;   //break the loop
//                            }
//                            if (proposedCBMStorageBin == null) {
//                                storageBinPutAway.setCbmPerQty(cbmPerQuantity);
//                                proposedCbmPerQtyStorageBin = mastersService.getStorageBinCbmPerQty(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                                if (proposedCbmPerQtyStorageBin != null) {
//                                    log.info("proposedCbmPerQtyStorageBin: " + proposedCbmPerQtyStorageBin.getStorageBin());
//                                    proposedStBin = proposedCbmPerQtyStorageBin.getStorageBin();
//                                    putAwayHeader.setProposedStorageBin(proposedStBin);
//                                    Double remainingCbm = emptyCbmPerQtyStorageBinUpdate(proposedCbmPerQtyStorageBin, cbm, loginUserID, authTokenForMastersService.getAccess_token());
//                                    cbm = cbm - remainingCbm;
//                                    putawayQty = putawayQuantityCalculation(remainingCbm, createdGRLine.getCbm(), createdGRLine.getGoodReceiptQty(), capacityCheck);
//                                }
//                            }
//                            if (proposedCBMStorageBin == null && proposedCbmPerQtyStorageBin == null) {
//                                binClassId = 2L;
//                                log.info("BinClassId : " + binClassId);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
//                                log.info("D --> CBMreserveBin: " + stBin.getStorageBin());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                cbm = 0D;   //break the loop
//                            }
//                        }

//                        if (capacityCheck && binClassId == 7) {
//                            log.info("CapacityCheck:" + capacityCheck);
//                            binClassId = 7L;
//                            storageBinPutAway.setBinClassId(binClassId);
//                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                            if (proposedNonCbmStorageBin != null) {
//                                proposedStBin = proposedNonCbmStorageBin.getStorageBin();
//                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
//                                putAwayHeader.setProposedStorageBin(proposedStBin);
//
//                                cbm = 0D;   //break the loop
//                            }
//                            if (proposedNonCbmStorageBin == null) {
//                                binClassId = 2L;
//                                log.info("BinClassId : " + binClassId);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
//                                log.info("D --> NonCBM reserveBin: " + stBin.getStorageBin());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                cbm = 0D;   //break the loop
//                            }
//                        }
//                        if (!capacityCheck) {
//                            log.info("D ---> CapacityCheck:" + capacityCheck);
//                            List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
//                            storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
//                            proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
//                            if (proposedNonCbmStorageBin != null) {
//                                proposedStBin = proposedNonCbmStorageBin.getStorageBin();
//                                putAwayHeader.setProposedStorageBin(proposedStBin);
//
//                                cbm = 0D;   //break the loop
//                            }
//                            if (proposedNonCbmStorageBin == null) {
//                                binClassId = 2L;
//                                log.info("BinClassId : " + binClassId);
//                                StorageBinV2 stBin = mastersService.getStorageBin(
//                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
//                                log.info("D --> NonCBMreserveBin: " + stBin.getStorageBin());
//                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                cbm = 0D;   //break the loop
//                            }
//                        }

                        log.info("D ---> CapacityCheck:" + capacityCheck);
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (proposedBinClass7Bin != null) {
                            proposedStBin = proposedBinClass7Bin.getStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                            log.info("D --> BinClassId7 Proposed Bin: " + proposedStBin);
                            cbm = 0D;   //break the loop
                        }
                        if (proposedBinClass7Bin == null) {
                            binClassId = 2L;
                            log.info("BinClassId : " + binClassId);
                            StorageBinV2 stBin = mastersService.getStorageBin(
                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                            log.info("D --> reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                            cbm = 0D;   //break the loop
                        }


                        putAwayHeader.setPutAwayQuantity(putawayQty);
                    }
                }


                /////////////////////////////////////////////////////////////////////////////////////////////////////
                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                //PROP_HE_NO	<- PAWAY_HE_NO
                if (createdGRLine.getReferenceDocumentType() != null) {
                    putAwayHeader.setReferenceDocumentType(createdGRLine.getReferenceDocumentType());
                } else {
                    putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(createdGRLine.getInboundOrderTypeId()));
                }
                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                        languageId,
                        plantId,
                        warehouseId);

                putAwayHeader.setCompanyDescription(description.getCompanyDesc());
                putAwayHeader.setPlantDescription(description.getPlantDesc());
                putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2(companyCode, plantId, languageId, warehouseId,
                        createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());

                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());

                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());

                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                putAwayHeader.setStatusId(19L);
                statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
                putAwayHeader.setStatusDescription(statusDescription);

                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setCreatedBy(loginUserID);
                putAwayHeader.setUpdatedBy(loginUserID);
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());
                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                log.info("putAwayHeader : " + putAwayHeader);

                /*----------------Inventory tables Create---------------------------------------------*/
                InventoryV2 createdinventory = createInventoryV2(createdGRLine);

                /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
        }
//            if (cbm == 0D) {
//                break outerloop;
//            }
//        }
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
     */
    public void updateStorageBin(Double remainingVolume, Double occupiedVolume, Double allocatedVolume,
                                 String storageBin, String companyCode, String plantId, String languageId,
                                 String warehouseId, String loginUserId, String authToken) {

        StorageBinV2 modifiedStorageBin = new StorageBinV2();
        modifiedStorageBin.setRemainingVolume(String.valueOf(remainingVolume));
        modifiedStorageBin.setAllocatedVolume(allocatedVolume);
        modifiedStorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
        modifiedStorageBin.setCapacityCheck(true);
        modifiedStorageBin.setStatusId(1L);

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

    /**
     * @param storageBin
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param authToken
     */
    public void updateEmptyStorageBinStatus(String storageBin, String companyCode, String plantId, String languageId,
                                            String warehouseId, String loginUserId, String authToken) {

        StorageBinV2 modifiedStorageBin = new StorageBinV2();
        modifiedStorageBin.setCapacityCheck(false);
        modifiedStorageBin.setStatusId(1L);

        StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(storageBin,
                modifiedStorageBin,
                companyCode,
                plantId,
                languageId,
                warehouseId,
                loginUserId,
                authToken);

        if (updateStorageBinV2 != null) {
            log.info("Storage Bin Status updated to occupied successfully ");
        }
    }

    /**
     * @param storageBin
     * @param cbm
     * @param loginUserID
     * @param authToken
     */
    public void emptyStorageBinUpdate(StorageBinV2 storageBin, Double cbm, String loginUserID, String authToken) {

        Double allocatedVolume = 0D;
        Double occupiedVolume = 0D;
        Double remainingVolume = 0D;
        Double totalVolume = 0D;

        if (storageBin.getOccupiedVolume() != null && !storageBin.getOccupiedVolume().equals("")) {
            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
        }
        if (storageBin.getTotalVolume() != null && !storageBin.getTotalVolume().equals("")) {
            totalVolume = Double.valueOf(storageBin.getTotalVolume());
        }
        if (storageBin.getRemainingVolume() != null && !storageBin.getRemainingVolume().equals("")) {
            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
        }

        allocatedVolume = cbm;
        occupiedVolume = occupiedVolume + allocatedVolume;
        if (totalVolume >= remainingVolume) {
            remainingVolume = totalVolume - (occupiedVolume);
        } else {
            remainingVolume = remainingVolume - allocatedVolume;
        }

        log.info("allocatedVolume, remainingVolume, occupiedVolume: " + allocatedVolume + ", " + remainingVolume + ", " + occupiedVolume);

        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, storageBin.getStorageBin(),
                storageBin.getCompanyCodeId(), storageBin.getPlantId(), storageBin.getLanguageId(), storageBin.getWarehouseId(), loginUserID, authToken);
    }

    /**
     * @param storageBin
     * @param cbm
     * @param loginUserID
     * @param authToken
     */
    public Double emptyCbmPerQtyStorageBinUpdate(StorageBinV2 storageBin, Double cbm, String loginUserID, String authToken) {

        Double allocatedVolume = 0D;
        Double occupiedVolume = 0D;
        Double remainingVolume = 0D;
        Double totalVolume = 0D;

        if (storageBin.getOccupiedVolume() != null && !storageBin.getOccupiedVolume().equals("")) {
            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
        }
        if (storageBin.getTotalVolume() != null && !storageBin.getTotalVolume().equals("")) {
            totalVolume = Double.valueOf(storageBin.getTotalVolume());
        }
        if (storageBin.getRemainingVolume() != null && !storageBin.getRemainingVolume().equals("")) {
            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
        }

        if (cbm <= remainingVolume) {
            allocatedVolume = cbm;
        }
        if (cbm > remainingVolume) {
            allocatedVolume = remainingVolume;
        }
        occupiedVolume = occupiedVolume + allocatedVolume;
        if (totalVolume >= remainingVolume) {
            remainingVolume = totalVolume - (occupiedVolume);
        } else {
            remainingVolume = remainingVolume - allocatedVolume;
        }

        log.info("allocatedVolume, remainingVolume, occupiedVolume: " + allocatedVolume + ", " + remainingVolume + ", " + occupiedVolume);

        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, storageBin.getStorageBin(),
                storageBin.getCompanyCodeId(), storageBin.getPlantId(), storageBin.getLanguageId(), storageBin.getWarehouseId(), loginUserID, authToken);
        cbm = occupiedVolume;
        log.info("CBM [for CbmPerQty]: " + cbm);
        return cbm;
    }

    /**
     * @param capacityCheck
     * @param storageBin
     * @param cbm
     * @param loginUserID
     * @param authToken
     * @return
     */
    private String proposedEmptyStorageBin(boolean capacityCheck, StorageBinV2[] storageBin, Double cbm, Double
            cbmPerQuantity, String loginUserID, String authToken) {
        List<StorageBinV2> stBinList = new ArrayList<>();
        List<StorageBinV2> filterStorageBinList = new ArrayList<>();
        String proposedStorageBin = null;
        log.info("capcity Check, CBM -----------> : " + capacityCheck + "," + cbm);
        if (capacityCheck) {
            Double finalCbm = cbm;
            Double finalCbmPerQty = cbmPerQuantity;
            stBinList = Arrays.stream(storageBin).filter(n -> n.getRemainingVolume() != null && !n.getRemainingVolume().equals("") && n.isCapacityCheck() == true).collect(Collectors.toList());
            log.info("Capacity - Empty Storage Bin: " + stBinList);
            if (stBinList != null && !stBinList.isEmpty()) {
                filterStorageBinList = stBinList.stream().filter(n -> Double.valueOf(n.getRemainingVolume()) >= finalCbm).sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());
                log.info("Capacity - Empty Storage Bin remaining volume greater than cbm: " + filterStorageBinList);
                if (filterStorageBinList == null || filterStorageBinList.isEmpty() || filterStorageBinList.size() == 0) {
                    filterStorageBinList = stBinList.stream().filter(n -> Double.valueOf(n.getRemainingVolume()) >= finalCbmPerQty).sorted(Comparator.comparing(StorageBinV2::getRemainingVolume)).collect(Collectors.toList());
                    log.info("Capacity - Empty Storage Bin remaining volume greater than cbmPerQty: " + filterStorageBinList);
                }
            }
            if (filterStorageBinList != null && !filterStorageBinList.isEmpty()) {
                proposedStorageBin = filterStorageBinList.get(0).getStorageBin();
                //Update StorageBin
                emptyStorageBinUpdate(filterStorageBinList.get(0), cbm, loginUserID, authToken);
            }
        }
        if (!capacityCheck) {
            stBinList = Arrays.stream(storageBin).filter(n -> n.isCapacityCheck() == false).collect(Collectors.toList());
            log.info("Non Capacity - Empty Storage Bin: " + stBinList);
            if (stBinList != null && !stBinList.isEmpty()) {
                proposedStorageBin = stBinList.get(0).getStorageBin();
                StorageBinV2 updateStorageBinStatus = stBinList.get(0);
                updateEmptyStorageBinStatus(updateStorageBinStatus.getStorageBin(),
                        updateStorageBinStatus.getCompanyCodeId(),
                        updateStorageBinStatus.getPlantId(),
                        updateStorageBinStatus.getLanguageId(),
                        updateStorageBinStatus.getWarehouseId(),
                        loginUserID, authToken);
            }
        }
        return proposedStorageBin;
    }

    /**
     * @param cbm
     * @param grlineCbm
     * @param grQty
     * @param capacityCheck
     * @return
     */
    public Double putawayQuantityCalculation(Double cbm, Double grlineCbm, Double grQty, boolean capacityCheck) {
        Double putawyQty = grQty;
        log.info("Capacity Check: " + capacityCheck);
        if (capacityCheck) {
            putawyQty = (cbm / grlineCbm) * grQty;
        }
        if (!capacityCheck) {
            putawyQty = grQty;
        }
        log.info("PutAwayQty: " + putawyQty);
        return putawyQty;
    }

    /**
     * @param splitedList
     * @param binClassId
     * @param warehouseId
     * @param authToken
     * @return
     */
    private StorageBinV2[] getStorageBinForSplitedListV2(List<List<String>> splitedList, Long
            binClassId, String companyCodeId, String plantId, String languageId, String warehouseId, String authToken) {
        for (List<String> list : splitedList) {
            StorageBinV2[] storageBin = getStorageBinV2(binClassId, list, companyCodeId, plantId, languageId, warehouseId, authToken);
            if (storageBin != null && storageBin.length > 0) {
                return storageBin;
            }
        }
        return null;
    }

    /**
     * @param binClassId
     * @param stBins
     * @param warehouseId
     * @param authToken
     * @return
     */
    private StorageBinV2[] getStorageBinV2(Long binClassId, List<String> stBins, String companyCodeId, String plantId,
                                           String languageId, String warehouseId, String authToken) {
        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setStorageBin(stBins);
        storageBinPutAway.setBinClassId(binClassId);
        storageBinPutAway.setCompanyCodeId(companyCodeId);
        storageBinPutAway.setPlantId(plantId);
        storageBinPutAway.setLanguageId(languageId);
        storageBinPutAway.setWarehouseId(warehouseId);
        StorageBinV2[] storageBin = mastersService.getStorageBinV2(storageBinPutAway, authToken);
        return storageBin;
    }

    /**
     *
     * @param createdGRLine
     * @param storageBin
     */
    private void createInventoryMovementV2(GrLineV2 createdGRLine, String storageBin) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(createdGRLine, inventoryMovement, CommonUtils.getNullPropertyNames(createdGRLine));
        inventoryMovement.setCompanyCodeId(createdGRLine.getCompanyCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        inventoryMovement.setManufacturerName(createdGRLine.getManufacturerName());
        inventoryMovement.setRefDocNumber(createdGRLine.getRefDocNumber());
        inventoryMovement.setCompanyDescription(createdGRLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(createdGRLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(createdGRLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(createdGRLine.getBarcodeId());
        inventoryMovement.setDescription(createdGRLine.getItemDescription());

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(createdGRLine.getGoodsReceiptNo());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY
        inventoryMovement.setMovementQty(createdGRLine.getGoodReceiptQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("P");

        // MVT_UOM
        inventoryMovement.setInventoryUom(createdGRLine.getOrderUom());

        inventoryMovement.setVariantCode(1L);
        inventoryMovement.setVariantSubCode("1");

        inventoryMovement.setPackBarcodes(createdGRLine.getPackBarcodes());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(createdGRLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(createdGRLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement : " + inventoryMovement);
    }

    /**
     * @param createdGRLine
     * @return
     */
    private InventoryV2 createInventoryNonCBMV2(GrLineV2 createdGRLine) {

        try {
            InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                    createdGRLine.getCompanyCode(),
                    createdGRLine.getPlantId(),
                    createdGRLine.getLanguageId(),
                    createdGRLine.getWarehouseId(),
                    createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(),
                    "99999", 3L, 0L);

            InventoryV2 createdinventory = null;

            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
                inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                Double totalQty = 0D;
                if (inventory.getReferenceField4() != null) {
                    totalQty = inventory.getReferenceField4() + createdGRLine.getGoodReceiptQty();
                }
                if (inventory.getReferenceField4() == null) {
                    totalQty = createdGRLine.getGoodReceiptQty();
                }
                inventory.setReferenceField4(totalQty);
                log.info("Total Inventory Qty : " + totalQty);
                if(createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory[Existing] : " + createdinventory);
            }

            if (dbInventory == null) {

                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));

                inventory.setCompanyCodeId(createdGRLine.getCompanyCode());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setManufacturerName(createdGRLine.getManufacturerName());

                if(createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBin storageBin = mastersService.getStorageBin(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
                log.info("storageBin: " + storageBin);
                inventory.setStorageBin(storageBin.getStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(createdGRLine.getCompanyCode());
                imBasicData.setPlantId(createdGRLine.getPlantId());
                imBasicData.setLanguageId(createdGRLine.getLanguageId());
                imBasicData.setWarehouseId(createdGRLine.getWarehouseId());
                imBasicData.setItemCode(createdGRLine.getItemCode());
                imBasicData.setManufacturerName(createdGRLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck != null) {
                    inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
                    inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
                    inventory.setDescription(itemCodeCapacityCheck.getDescription());
                }
                if (storageBin != null) {
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                if (dbInventory != null) {
                    inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory : " + createdinventory);
            }

            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    private InventoryV2 createInventoryV2(GrLineV2 createdGRLine) {

        try {
            InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                    createdGRLine.getCompanyCode(),
                    createdGRLine.getPlantId(),
                    createdGRLine.getLanguageId(),
                    createdGRLine.getWarehouseId(),
                    createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(),
                    "99999", 3L, 0L);

            InventoryV2 createdinventory = null;

            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
                inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                Double totalQty = 0D;
                if (inventory.getReferenceField4() != null) {
                    totalQty = inventory.getReferenceField4() + createdGRLine.getGoodReceiptQty();
                }
                if (inventory.getReferenceField4() == null) {
                    totalQty = createdGRLine.getGoodReceiptQty();
                }
                inventory.setReferenceField4(totalQty);
                log.info("Total Inventory Qty : " + totalQty);
                if(createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory[Existing] : " + createdinventory);
            }

            if (dbInventory == null) {

                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));

                inventory.setCompanyCodeId(createdGRLine.getCompanyCode());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setManufacturerName(createdGRLine.getManufacturerName());

                if(createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBin storageBin = mastersService.getStorageBin(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
                log.info("storageBin: " + storageBin);
                inventory.setStorageBin(storageBin.getStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(createdGRLine.getCompanyCode());
                imBasicData.setPlantId(createdGRLine.getPlantId());
                imBasicData.setLanguageId(createdGRLine.getLanguageId());
                imBasicData.setWarehouseId(createdGRLine.getWarehouseId());
                imBasicData.setItemCode(createdGRLine.getItemCode());
                imBasicData.setManufacturerName(createdGRLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                boolean capacityCheck = false;
                Double invQty = 0D;
                Double cbm = 0D;
                Double cbmPerQty = 0D;
                Double invCbm = 0D;
//            List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
                if (itemCodeCapacityCheck != null) {
                    inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
                    inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
                    inventory.setDescription(itemCodeCapacityCheck.getDescription());

                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();               //Capacity Check for putaway item
                    }
                    log.info("CapacityCheck -----------> : " + capacityCheck);
                }
                if (storageBin != null) {
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                if (dbInventory != null) {
                    inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                if (capacityCheck) {
                    if (createdGRLine.getCbmQuantity() != null) {
                        inventory.setCbmPerQuantity(String.valueOf(createdGRLine.getCbmQuantity()));
                    }
                    if (createdGRLine.getGoodReceiptQty() != null) {
                        invQty = createdGRLine.getGoodReceiptQty();
                    }
                    if (createdGRLine.getCbmQuantity() == null) {

                        if (createdGRLine.getCbm() != null) {
                            cbm = createdGRLine.getCbm();
                        }
                        cbmPerQty = cbm / invQty;
                        inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
                    }
                    if (createdGRLine.getCbm() != null) {
                        invCbm = createdGRLine.getCbm();
                    }
                    if (createdGRLine.getCbm() == null) {
                        invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
                    }
                    inventory.setCbm(String.valueOf(invCbm));
                }

                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("created inventory : " + createdinventory);
            }

            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @param loginUserID
     * @param updateGrLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public GrLineV2 updateGrLineV2(String companyCodeId, String plantId, String languageId,
                                   String warehouseId, String preInboundNo, String refDocNumber,
                                   String goodsReceiptNo, String palletCode, String caseCode,
                                   String packBarcodes, Long lineNo, String itemCode,
                                   String loginUserID, GrLineV2 updateGrLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        GrLineV2 dbGrLine = getGrLineV2(companyCodeId, languageId, plantId, warehouseId, preInboundNo, refDocNumber,
                goodsReceiptNo, palletCode, caseCode,
                packBarcodes, lineNo, itemCode);
        BeanUtils.copyProperties(updateGrLine, dbGrLine, CommonUtils.getNullPropertyNames(updateGrLine));
        dbGrLine.setUpdatedBy(loginUserID);
        dbGrLine.setUpdatedOn(new Date());
        GrLineV2 updatedGrLine = grLineV2Repository.save(dbGrLine);
        return updatedGrLine;
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<GrLineV2> grLines = getGrLinesV2();
        grLines.forEach(g -> g.setReferenceField1(asnNumber));
        grLineV2Repository.saveAll(grLines);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @param loginUserID
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void deleteGrLineV2(String companyCodeId, String plantId, String languageId,
                               String warehouseId, String preInboundNo, String refDocNumber,
                               String goodsReceiptNo, String palletCode, String caseCode,
                               String packBarcodes, Long lineNo, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        GrLineV2 grLine = getGrLineV2(companyCodeId, languageId, plantId, warehouseId, preInboundNo, refDocNumber,
                goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode);
        if (grLine != null) {
            grLine.setDeletionIndicator(1L);
            grLine.setUpdatedBy(loginUserID);
            grLine.setUpdatedOn(new Date());
            grLineV2Repository.save(grLine);
        } else {
            // Exception Log
            createGrLineLog(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, palletCode,
                    caseCode, packBarcodes, lineNo, itemCode, "Error in deleting GrLineV2 with goodsReceiptNo - " + goodsReceiptNo);

            throw new EntityNotFoundException("Error in deleting Id: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",packBarcodes: " + packBarcodes +
                    ",palletCode: " + palletCode +
                    ",caseCode: " + caseCode +
                    ",goodsReceiptNo: " + goodsReceiptNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws java.text.ParseException
     */
    //Delete GrLineV2
    public List<GrLineV2> deleteGrLineV2(String companyCode, String plantId, String languageId,
                                         String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) throws java.text.ParseException {

        List<GrLineV2> grLineV2s = new ArrayList<>();
        List<GrLineV2> dbGrLineList = grLineV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("GrLineList - cancellation : " + dbGrLineList);
        if (dbGrLineList != null && !dbGrLineList.isEmpty()) {
            for (GrLineV2 grLine : dbGrLineList) {
                grLine.setDeletionIndicator(1L);
                grLine.setUpdatedBy(loginUserID);
                grLine.setUpdatedOn(new Date());
                GrLineV2 grLineV2 = grLineV2Repository.save(grLine);
                grLineV2s.add(grLineV2);
            }
        }
        return grLineV2s;
    }

    //============================================GrLine_ExceptionLog==================================================
    private void createGrLineLog(String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
                                 String preInboundNo, String goodsReceiptNo, String palletCode, String caseCode,
                                 String packBarcodes, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(goodsReceiptNo);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(palletCode);
        errorLog.setReferenceField3(caseCode);
        errorLog.setReferenceField4(packBarcodes);
        errorLog.setReferenceField5(itemCode);
        errorLog.setReferenceField6(String.valueOf(lineNo));
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog1(String languageId, String companyCode, String plantId, String refDocNumber,
                                  String preInboundNo, String packBarcodes, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(String.valueOf(lineNo));
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(packBarcodes);
        errorLog.setReferenceField3(itemCode);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog2(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String preInboundNo, String packBarcodes, String caseCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField4(caseCode);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog3(String languageId, String companyCode, String plantId,
                                  String refDocNumber, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog4(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String preInboundNo, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog5(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog6(String languageId, String companyCode, String plantId, String refDocNumber,
                                  String preInboundNo, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(String.valueOf(lineNo));
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(itemCode);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog7(GrLineV2 grLineV2, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(grLineV2.getGoodsReceiptNo());
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(grLineV2.getLanguageId());
        errorLog.setCompanyCodeId(grLineV2.getCompanyCode());
        errorLog.setPlantId(grLineV2.getPlantId());
        errorLog.setWarehouseId(grLineV2.getWarehouseId());
        errorLog.setRefDocNumber(grLineV2.getRefDocNumber());
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog8(ImBasicData1 imBasicData1, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(imBasicData1.getUomId());
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(imBasicData1.getLanguageId());
        errorLog.setCompanyCodeId(imBasicData1.getCompanyCodeId());
        errorLog.setPlantId(imBasicData1.getPlantId());
        errorLog.setWarehouseId(imBasicData1.getWarehouseId());
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog9(StorageBinV2 storageBinV2, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(storageBinV2.getStorageBin());
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(storageBinV2.getLanguageId());
        errorLog.setCompanyCodeId(storageBinV2.getCompanyCodeId());
        errorLog.setPlantId(storageBinV2.getPlantId());
        errorLog.setWarehouseId(storageBinV2.getWarehouseId());
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    private void createGrLineLog10(List<AddGrLineV2> grLineV2List, String error) {

        for (AddGrLineV2 addGrLineV2 : grLineV2List) {
            ErrorLog errorLog = new ErrorLog();

            errorLog.setOrderTypeId(addGrLineV2.getGoodsReceiptNo());
            errorLog.setOrderDate(new Date());
            errorLog.setLanguageId(addGrLineV2.getLanguageId());
            errorLog.setCompanyCodeId(addGrLineV2.getCompanyCode());
            errorLog.setPlantId(addGrLineV2.getPlantId());
            errorLog.setWarehouseId(addGrLineV2.getWarehouseId());
            errorLog.setRefDocNumber(addGrLineV2.getRefDocNumber());
            errorLog.setErrorMessage(error);
            errorLog.setCreatedBy("MSD_API");
            errorLog.setCreatedOn(new Date());
            exceptionLogRepo.save(errorLog);
        }
    }

}
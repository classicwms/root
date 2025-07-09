package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.*;
import com.tekclover.wms.api.inbound.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.inbound.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.SearchGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.inbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.GrLineSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.GrLineV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GrLineService extends BaseService {
    @Autowired
    private StorageBinRepository storageBinRepository;

    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    DbConfigRepository dbConfigRepository;

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

    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

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
    StorageBinService storageBinService;

    String statusDescription = null;

    @Autowired
    private ErrorLogRepository exceptionLogRepo;

    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    InventoryTransRepository inventoryTransRepository;
//    @Autowired
//    InboundProcessingService inboundProcessingService;

    @Autowired
    InboundQualityHeaderService inboundQualityHeaderService;
    protected Long NUMBER_RANGE_CODE = 0L;
    protected String numberRangeId = null;
    protected IKeyValuePair description = null;

    protected static final String MFR_NAME = "NAMRATHA";

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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @return
     */
    public synchronized InventoryV2 getInventoryBinClassId3V4(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barCodeId, String alternateUom) {
        InventoryV2 dbInventoryV2 = new InventoryV2();
        IInventoryImpl inventory = inventoryV2Repository.getInboundInventoryV4(companyCode, plantId, languageId, warehouseId, barCodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, null, 3L, alternateUom);
        if (inventory != null) {
            BeanUtils.copyProperties(inventory, dbInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            return dbInventoryV2;
        }
        return null;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param storageBin
     * @return
     */
    public synchronized InventoryV2 getInventoryBinClassId1V4(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barcodeId, String alternateUom, String storageBin) {
        InventoryV2 dbInventoryV2 = new InventoryV2();
        IInventoryImpl inventory = inventoryV2Repository.getInboundInventoryV4(companyCode, plantId, languageId, warehouseId, barcodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, storageBin, 1L, alternateUom);
        if (inventory != null) {
            BeanUtils.copyProperties(inventory, dbInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            return dbInventoryV2;
        }
        return null;
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
    public List<GrLineV2> createGrLine(@Valid List<AddGrLineV2> newGrLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        try {
            String warehouseId = null;
            List<AddGrLineV2> dupGrLines = getDuplicates(newGrLines);
            log.info("-------dupGrLines--------> " + dupGrLines);
            if (dupGrLines != null && !dupGrLines.isEmpty()) {
                newGrLines.removeAll(dupGrLines);
                newGrLines.add(dupGrLines.get(0));
                log.info("-------GrLines---removed-dupPickupLines-----> " + newGrLines);
            }

            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                warehouseId = newGrLine.getWarehouseId();

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
                    dbGrLine.setStatusId(17L);
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
//                    List<GrLineV2> oldGrLine = grLineRepository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
//                            dbGrLine.getGoodsReceiptNo(), dbGrLine.getItemCode(), dbGrLine.getLineNo(),
//                            dbGrLine.getLanguageId(),
//                            dbGrLine.getCompanyCode(),
//                            dbGrLine.getPlantId(),
//                            dbGrLine.getRefDocNumber(),
//                            dbGrLine.getPackBarcodes(),
//                            dbGrLine.getWarehouseId(),
//                            dbGrLine.getPreInboundNo(),
//                            dbGrLine.getCaseCode(),
//                            0L
//                    );
                    GrLineV2 createdGRLine = null;

//                    //validate to check if grline is already exists
//                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                    createdGRLine = grLineRepository.save(dbGrLine);
                    log.info("createdGRLine : " + createdGRLine);
                    createdGRLines.add(createdGRLine);

                    if (createdGRLine != null) {
                        // Record Insertion in PUTAWAYHEADER table
                        createPutAwayHeader(createdGRLine, loginUserID);
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
            StatusId idStatus = idmasterService.getStatus(17L, "EN", authTokenForIDService.getAccess_token());
            for (GrLineV2 grLine : createdGRLines) {
                /*
                 * 1. Update GRHEADER table with STATUS_ID=17 by Passing WH_ID/GR_NO/CASE_CODE/REF_DOC_NO and
                 * GR_CNF_BY with USR_ID and GR_CNF_ON with Server time
                 */
                List<GrHeaderV2> grHeaders = grHeaderService.getGrHeader(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getWarehouseId(), grLine.getGoodsReceiptNo(),
                        grLine.getCaseCode(), grLine.getRefDocNumber());
                for (GrHeaderV2 grHeader : grHeaders) {
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
                List<StagingLineEntityV2> stagingLineEntityList =
                        stagingLineService.getStagingLine(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getWarehouseId(), grLine.getRefDocNumber(), grLine.getPreInboundNo(),
                                grLine.getLineNo(), grLine.getItemCode(), grLine.getCaseCode());
                for (StagingLineEntityV2 stagingLineEntity : stagingLineEntityList) {
                    stagingLineEntity.setStatusId(17L);
                    stagingLineEntity = stagingLineRepository.save(stagingLineEntity);
                    log.info("stagingLineEntity updated: " + stagingLineEntity);
                }

                /*
                 * 3. Then Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE in INBOUNDLINE table and
                 * updated STATUS_ID as 17
                 */
                InboundLineV2 inboundLine = inboundLineV2Repository.getInboundLineV2(grLine.getWarehouseId(), grLine.getLineNo(),
                        grLine.getPreInboundNo(),
                        grLine.getItemCode(),
                        grLine.getCompanyCode(),
                        grLine.getPlantId(),
                        grLine.getLanguageId(),
                        grLine.getRefDocNumber());
                if (inboundLine != null) {
                    inboundLineV2Repository.updateStatusId(17L, grLine.getWarehouseId(), grLine.getLineNo(),
                            grLine.getPreInboundNo(),
                            grLine.getItemCode(),
                            grLine.getCompanyCode(),
                            grLine.getPlantId(),
                            grLine.getLanguageId(),
                            grLine.getRefDocNumber());
//                    inboundLine.setStatusId(17L);
//                    inboundLine = inboundLineRepository.save(inboundLine);
                    log.info("inboundLine updated : ");
                }
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
    public static List<AddGrLineV2> getDuplicates(List<AddGrLineV2> grLineList) {
        return getDuplicatesMap(grLineList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param addGrLineList
     * @return
     */
    private static Map<String, List<AddGrLineV2>> getDuplicatesMap(List<AddGrLineV2> addGrLineList) {
        return addGrLineList.stream().collect(Collectors.groupingBy(AddGrLine::uniqueAttributes));
    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
    private void createPutAwayHeader(GrLineV2 createdGRLine, String loginUserID) {
        //  ASS_HE_NO
        if (createdGRLine != null) {
            // Insert record into PutAwayHeader
            //private Double putAwayQuantity, private String putAwayUom;
            PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setCompanyCodeId(createdGRLine.getCompanyCode());
            putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
            // PA_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_CODE = 7;
            String nextPANumber = getNextNumberRange(NUM_RAN_CODE, createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
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
            List<InventoryV2> stBinInventoryList =
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
                            StorageBin stBin = mastersService.getStorageBinNew(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
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
                            StorageBin stBin = mastersService.getStorageBinNew(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
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
                            StorageBin stBin = mastersService.getStorageBinNew(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), binClassID, authTokenForMastersService.getAccess_token());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        }
                    }
                }
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //PROP_HE_NO	<- PAWAY_HE_NO
            putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
            Long statusId = 19L;
            putAwayHeader.setStatusId(statusId);
            statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
            putAwayHeader.setStatusDescription(statusDescription);
            putAwayHeader.setDeletionIndicator(0L);
            putAwayHeader.setCreatedBy(loginUserID);
            putAwayHeader.setCreatedOn(new Date());
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeader.setUpdatedOn(new Date());
            putAwayHeader = putAwayHeaderRepository.save(putAwayHeader);
            log.info("putAwayHeader : " + putAwayHeader);

            /*----------------Inventory tables Create---------------------------------------------*/
            InventoryV2 createdinventory = createInventory(createdGRLine);

            /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
            createInventoryMovement(createdGRLine, createdinventory);
        }
    }

    private String getNextNumberRange(long numRanCode, String companyCode, String plantId, String languageId, String warehouseId, String accessToken) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        String nextNumberRange = idmasterService.getNextNumberRange(numRanCode, warehouseId, companyCode, plantId, languageId, accessToken);
        return nextNumberRange;
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
     * @param createdGRLine
     * @param createdinventory
     */
    private void createInventoryMovement(GrLineV2 createdGRLine, Inventory createdinventory) {
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
    private InventoryV2 createInventory(GrLineV2 createdGRLine) {
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

        // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        StorageBin storageBin = mastersService.getStorageBinNew(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
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
        InventoryV2 createdinventory = inventoryRepository.save(inventory);
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
     * @param searchGrLine
     * @return
     * @throws ParseException
     */
    public List<GrLineV2> findGrLineV2(SearchGrLineV2 searchGrLine) throws ParseException, java.text.ParseException {
        if (searchGrLine.getStartCreatedOn() != null
                && searchGrLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrLine.getStartCreatedOn(),
                    searchGrLine.getEndCreatedOn());
            searchGrLine.setStartCreatedOn(dates[0]);
            searchGrLine.setEndCreatedOn(dates[1]);
        }
        log.info("Grline Search Input - Stream: " + searchGrLine);
        GrLineV2Specification spec = new GrLineV2Specification(searchGrLine);

        try {
            // Fetch all records matching the specification
            List<GrLineV2> grLineList = grLineV2Repository.findAll(spec);

            // Log the number of records fetched
            log.info("Fetched {} records from the database", grLineList.size());

            // Process each GrLineV2 entity
            grLineList.forEach(grLine -> log.info("Processing GrLineV2: {}", grLine));

            return grLineList;
        } catch (Exception e) {
            log.error("Error occurred while fetching GrLineV2 records: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch GrLineV2 records", e);
        } finally {
            // Clear the DB context after processing
            DataBaseContextHolder.clear();
        }
    }


    public List<GrLineImpl> findGrLineSQLV2(SearchGrLineV2 searchGrLine) throws ParseException, java.text.ParseException {
        if (searchGrLine.getStartCreatedOn() != null
                && searchGrLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchGrLine.getStartCreatedOn(),
                    searchGrLine.getEndCreatedOn());
            searchGrLine.setStartCreatedOn(dates[0]);
            searchGrLine.setEndCreatedOn(dates[1]);
        }
        if (searchGrLine.getCompanyCodeId() == null || searchGrLine.getCompanyCodeId().isEmpty()) {
            searchGrLine.setCompanyCodeId(null);
        }
        if (searchGrLine.getPlantId() == null || searchGrLine.getPlantId().isEmpty()) {
            searchGrLine.setPlantId(null);
        }
        if (searchGrLine.getLanguageId() == null || searchGrLine.getLanguageId().isEmpty()) {
            searchGrLine.setLanguageId(null);
        }
        if (searchGrLine.getWarehouseId() == null || searchGrLine.getWarehouseId().isEmpty()) {
            searchGrLine.setWarehouseId(null);
        }
        if (searchGrLine.getRefDocNumber() == null || searchGrLine.getRefDocNumber().isEmpty()) {
            searchGrLine.setRefDocNumber(null);
        }
        if (searchGrLine.getPreInboundNo() == null || searchGrLine.getPreInboundNo().isEmpty()) {
            searchGrLine.setPreInboundNo(null);
        }
        if (searchGrLine.getLineNo() == null || searchGrLine.getLineNo().isEmpty()) {
            searchGrLine.setLineNo(null);
        }
        if (searchGrLine.getPackBarcodes() == null || searchGrLine.getPackBarcodes().isEmpty()) {
            searchGrLine.setPackBarcodes(null);
        }
        if (searchGrLine.getItemCode() == null || searchGrLine.getItemCode().isEmpty()) {
            searchGrLine.setItemCode(null);
        }
        if (searchGrLine.getCaseCode() == null || searchGrLine.getCaseCode().isEmpty()) {
            searchGrLine.setCaseCode(null);
        }
        if (searchGrLine.getManufacturerName() == null || searchGrLine.getManufacturerName().isEmpty()) {
            searchGrLine.setManufacturerName(null);
        }
        if (searchGrLine.getBarcodeId() == null || searchGrLine.getBarcodeId().isEmpty()) {
            searchGrLine.setBarcodeId(null);
        }
        if (searchGrLine.getStatusId() == null || searchGrLine.getStatusId().isEmpty()) {
            searchGrLine.setStatusId(null);
        }
        if (searchGrLine.getManufacturerCode() == null || searchGrLine.getManufacturerCode().isEmpty()) {
            searchGrLine.setManufacturerCode(null);
        }
        if (searchGrLine.getOrigin() == null || searchGrLine.getOrigin().isEmpty()) {
            searchGrLine.setOrigin(null);
        }
        if (searchGrLine.getInterimStorageBin() == null || searchGrLine.getInterimStorageBin().isEmpty()) {
            searchGrLine.setInterimStorageBin(null);
        }
        if (searchGrLine.getBrand() == null || searchGrLine.getBrand().isEmpty()) {
            searchGrLine.setBrand(null);
        }
        if (searchGrLine.getRejectType() == null || searchGrLine.getRejectType().isEmpty()) {
            searchGrLine.setRejectType(null);
        }
        if (searchGrLine.getRejectReason() == null || searchGrLine.getRejectReason().isEmpty()) {
            searchGrLine.setRejectReason(null);
        }
        if (searchGrLine.getInboundOrderTypeId() == null || searchGrLine.getInboundOrderTypeId().isEmpty()) {
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

    /**
     * Generate PackBarcode Modified for Knowell
     * Aakash Vinayak - 05/06/2025
     *
     * @param acceptQty
     * @param damageQty
     * @return
     */
    public List<PackBarcode> generatePackBarcodeV7(String companyCode, String languageId, String plantId,
                                                   Double acceptQty, Double damageQty, String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 6;
        List<PackBarcode> packBarcodes = new ArrayList<>();

        // Accept Qty
        if (acceptQty != null && acceptQty != 0.0) {
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            PackBarcode acceptQtyPackBarcode = new PackBarcode();
            acceptQtyPackBarcode.setQuantityType("A");
            acceptQtyPackBarcode.setBarcode(nextRangeNumber);
            packBarcodes.add(acceptQtyPackBarcode);
        }

        // Damage Qty
        if (damageQty != null && damageQty != 0.0) {
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
//            PackBarcode packBarcodeMob = new PackBarcode();
//            List<PackBarcode> packBarcodeList = new ArrayList<>();
            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {

//                if (newGrLine.getAcceptedQty() > 0.0) {
//                    packBarcodeMob.setQuantityType("A");
//                    packBarcodeMob.setBarcode(newGrLine.getPartner_item_barcode());
//                    packBarcodeList.add(packBarcodeMob);
//                } else if (newGrLine.getDamageQty() > 0.0) {
//                    packBarcodeMob.setQuantityType("D");
//                    packBarcodeMob.setBarcode(newGrLine.getPartner_item_barcode());
//                    packBarcodeList.add(packBarcodeMob);
//                }
//
//                newGrLine.setPackBarcodes(packBarcodeList);

                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
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
                    if (dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(), newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), newGrLine.getItemCode(), newGrLine.getManufacturerName(), newGrLine.getLineNo());
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                    if (dbGrQty != null) {
                        Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
                        log.info("totalGrQty -----> {}", totalGrQty);
//                        if (newGrLine.getOrderQty() < totalGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
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

                    dbGrLine.setIsPutAwayHeaderCreated(0L);
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

//                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
//                            goodsReceiptNo, dbGrLine.getItemCode(), dbGrLine.getLineNo(),
//                            languageId, companyCode, plantId,
//                            refDocNumber, dbGrLine.getPackBarcodes(), warehouseId,
//                            preInboundNo, dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
//                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
//                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                    try {
                        log.info("-----b4-----createGRLine : " + dbGrLine);
                        dbGrLine.setIsPutAwayHeaderCreated(9L);
                        createdGRLine = grLineV2Repository.saveAndFlush(dbGrLine);
                        log.info("---after---createdGRLine : " + createdGRLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    createdGRLines.add(createdGRLine);

//                        if (createdGRLine != null && !createGrLineError) {
//                            // Record Insertion in PUTAWAYHEADER table
//                            createPutAwayHeaderNonCBMV2(createdGRLine, loginUserID);
//                        }
//                    }
                    //Update staging Line using stored Procedure
                    log.info(companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + refDocNumber + "|" + preInboundNo + "|" + createdGRLine.getLineNo() + "|" + createdGRLine.getItemCode() + "|" + createdGRLine.getManufacturerName());
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

    @Transactional
    public List<GrLineV2> createGrLineNonCBMV4(@Valid List<AddGrLineV2> newGrLines, String loginUserID) throws Exception {

        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        String itemCode = null;
        String manufacturerName = null;
        String packBarcodes = "99999";

        try {
            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {

                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }

                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("Accept (A)-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(grQty);
                        dbGrLine.setOrderQty(grQty);
                        dbGrLine.setAcceptedQty(0D);
                        log.info("Damage (D)-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcodes);
                    dbGrLine.setGrUom(newGrLine.getOrderUom());
                    dbGrLine.setStatusId(14L);

                    if (dbGrLine.getGoodReceiptQty() != null && dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    if (dbGrLine.getOrderUom() == null) {
                        throw new BadRequestException("Uom is mandatory");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() != null && newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();
                    itemCode = dbGrLine.getItemCode();
                    manufacturerName = dbGrLine.getManufacturerName();

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), itemCode, manufacturerName, newGrLine.getLineNo(), dbGrLine.getGoodReceiptQty());
                    log.info("dbGrQty+newGrQty, OrdQty: " + dbGrQty + ", " + newGrLine.getOrderQty());
//                    if (dbGrQty != null) {
//                        if (newGrLine.getOrderQty() < dbGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
//                    }

                    description = getDescription(companyCode, plantId, languageId, warehouseId);
                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = newGrLine.getOrderQty() != null ? newGrLine.getOrderQty() : 0D;
                    Double acceptQty = newGrLine.getAcceptedQty() != null ? newGrLine.getAcceptedQty() : 0D;
                    Double damageQty = newGrLine.getDamageQty() != null ? newGrLine.getDamageQty() : 0D;

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber, newGrLine.getLineNo(),
                            itemCode, manufacturerName);
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity == null) {
                        dbStagingLineEntity = createLines(dbGrLine, loginUserID);
                    }

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        if (newGrLine.getAlternateUom() == null || newGrLine.getBagSize() == null || newGrLine.getAlternateUom().isBlank()) {
                            dbGrLine.setAlternateUom(dbStagingLineEntity.getAlternateUom());
                            dbGrLine.setNoBags(dbStagingLineEntity.getNoBags());
                            dbGrLine.setBagSize(dbStagingLineEntity.getBagSize());
                            dbGrLine.setMrp(dbStagingLineEntity.getMrp());
                            dbGrLine.setItemType(dbStagingLineEntity.getItemType());
                            dbGrLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                            dbGrLine.setSize(dbStagingLineEntity.getSize());
                            dbGrLine.setBrand(dbStagingLineEntity.getBrand());
                        }
                    }
                    //Calculate No of Bags for Damage Qty
                    if (dbGrLine.getQuantityType().equalsIgnoreCase("D")) {
//                        double actualQty = getQuantity(dbGrLine.getGoodReceiptQty(), dbGrLine.getBagSize());
                        double actualQty = dbGrLine.getGoodReceiptQty();
                        double NO_OF_BAGS = actualQty / dbGrLine.getBagSize();
                        dbGrLine.setNoBags(roundUp(NO_OF_BAGS));
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                    }
                    statusDescription = getStatusDescription(dbGrLine.getStatusId(), languageId);
                    dbGrLine.setStatusDescription(statusDescription);

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
                            goodsReceiptNo, itemCode, dbGrLine.getLineNo(), languageId, companyCode, plantId,
                            refDocNumber, dbGrLine.getPackBarcodes(), warehouseId, preInboundNo,
                            dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        // Lead Time
                        GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(languageId, companyCode, plantId, warehouseId, goodsReceiptNo, new Date());
                        if (implForLeadTime != null) {
                            if (!implForLeadTime.getDiffDays().equals("00")) {
                                String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                        + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffHours().equals("00")) {
                                String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                        + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                                String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else {
                                String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            }
                        }

                        try {
                            createdGRLine = grLineV2Repository.save(dbGrLine);

                            if (createdGRLine != null && createdGRLine.getBarcodeId() != null) {
                                stagingLineV2Repository.updateBarCode(
                                        createdGRLine.getBarcodeId(),
                                        createdGRLine.getCompanyCode(),
                                        createdGRLine.getPlantId(),
                                        createdGRLine.getLanguageId(),
                                        createdGRLine.getWarehouseId(),
                                        createdGRLine.getRefDocNumber(),
                                        createdGRLine.getPreInboundNo(),
                                        createdGRLine.getLineNo(),
                                        createdGRLine.getItemCode());
                                log.info(" Updated StagingLine PARTNER_ITEM_BARCODE with barcode: " + createdGRLine.getBarcodeId());
                            }
                        } catch (Exception e) {
                            createGrLineError = true;
                            //Exception Log
                            createGrLineLog7(dbGrLine, e.toString());
                            throw e;
                        }
                        log.info("createdGRLine : " + createdGRLine);
                        createdGRLines.add(createdGRLine);
                    }
                }

                log.info("Records were inserted successfully...");
            }

            if (createdGRLines != null) {
                createPutAwayHeaderNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, preInboundNo, refDocNumber, createdGRLines, loginUserID);
            }

            //GrHeader Status 17 Updating Using Stored Procedure when condition met - multiple procedure combined to single procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, languageId);
            grHeaderV2Repository.updateStatusProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("Status Update Using Stored Procedure ---> GrHeader, StagingLine, InboundLine");

            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param company
     * @param plant
     * @param language
     * @param warehouse
     * @param item
     * @param manufactureName
     * @param preInbound
     * @param refDocNo
     * @param createdGRLines
     * @param loginUserID
     * @throws Exception
     */
//    @Async("asyncExecutor")
    private void createPutAwayHeaderNonCBMV4(String company, String plant, String language,
                                             String warehouse, String item, String manufactureName,
                                             String preInbound, String refDocNo,
                                             List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
        try {

            String idMasterToken = getIDMasterAuthToken();
            //PA_NO
            NUMBER_RANGE_CODE = 7L;
            String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

            log.info("PA number ----------------> {}", nextPANumber);
            fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
            for (GrLineV2 createdGRLine : createdGRLines) {

                // Setting params
                String languageId = createdGRLine.getLanguageId();
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String warehouseId = createdGRLine.getWarehouseId();
                String itemCode = createdGRLine.getItemCode();
                String manufacturerName = createdGRLine.getManufacturerName();
                String preInboundNo = createdGRLine.getPreInboundNo();
                String refDocNumber = createdGRLine.getRefDocNumber();


                String proposedStorageBin = createdGRLine.getInterimStorageBin();
                String alternateUom = createdGRLine.getAlternateUom();
                Double bagSize = createdGRLine.getBagSize();

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(companyCode);
                storageBinPutAway.setPlantId(plantId);
                storageBinPutAway.setLanguageId(languageId);
                storageBinPutAway.setWarehouseId(warehouseId);

                //  ASS_HE_NO
                if (createdGRLine != null) {
                    // Insert record into PutAwayHeader
                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayHeader.setCompanyCodeId(companyCode);
                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                    //Inbound Quality Number
                    NUMBER_RANGE_CODE = 23L;
                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

                    //-----------------PROP_ST_BIN---------------------------------------------

                    //V2 Code
                    Long binClassId = 0L;                   //actual code follows
                    if (createdGRLine.getInboundOrderTypeId() == null) {
                        throw new BadRequestException("inbound order type id cannot be null");
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
                            createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
                            createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
                        binClassId = 1L;
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        binClassId = 7L;
                    }
                    log.info("BinClassId : " + binClassId);

//                String packBarCode = createdGRLine.getBatchSerialNumber() != null ? createdGRLine.getBatchSerialNumber() : "99999";
//                List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, createdGRLine.getBarcodeId(), createdGRLine.getBatchSerialNumber(), packBarCode, alternateUom, bagSize, binClassId);
                    List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, alternateUom, bagSize, binClassId);
//                    log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);

                    if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                        storageBinPutAway.setBinClassId(binClassId);
                        storageBinPutAway.setBin(proposedStorageBin);
                        StorageBinV2 storageBin = null;
                        try {
                            log.info("getStorageBin Input: " + storageBinPutAway);
                            storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }
                        log.info("InterimStorageBin: " + storageBin);
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                        if (storageBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        }
                        if (storageBin == null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        }
                    }
                    //BinClassId - 7 - Return Order(Sale Return)
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);

                        StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                        if (proposedBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                            log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
                        }
                    }

                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                        log.info("BinClassId : " + binClassId);
                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                storageBinPutAway.setBinClassId(binClassId);
                                storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
                                if (proposedExistingBin != null) {
                                    proposedStorageBin = proposedExistingBin.getStorageBin();
                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                }
                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                            }
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                storageBinPutAway.setBinClassId(7L);
                                StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                                if (proposedBin != null) {
                                    putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                                    log.info("Damage Qty --> Proposed Bin: " + proposedBin.getStorageBin());
                                }
                            }
                        }
                    }

                    //Last Picked Bin as Proposed Bin If it is empty
//                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
//
//                        PickupLineV2 pickupLineList = pickupLineService.getPickupLineForLastBinCheckV4(companyCode, plantId, languageId, warehouseId,
//                                itemCode, manufacturerName, alternateUom, bagSize);
//                        log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
//                        if (pickupLineList != null) {
//                            putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
//                            putAwayHeader.setLevelId(pickupLineList.getLevelId());
//                            log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
//                            log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
//                        }
//                    }

                    //Propose Empty Bin if Last picked bin is unavailable
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
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
                            PutAwayLineV2 existingBinPutAwayLineItemCheck = putAwayLineService.getPutAwayLineExistingItemCheckV4(companyCode, plantId, languageId, warehouseId,
                                    itemCode, manufacturerName, alternateUom, bagSize);
                            log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
                            if (existingBinPutAwayLineItemCheck != null) {
                                proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
                                    putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
                                } else {
                                    storageBinPutAway.setBin(proposedStorageBin);
                                    StorageBinV2 getLevelIdForProposedBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                                    if (getLevelIdForProposedBin != null) {
                                        putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
                                    }
                                }
                                log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
                            }
                            //Checking proposed bin in putAway header for that item
                            if (putAwayHeader.getProposedStorageBin() == null) {
                                PutAwayHeaderV2 existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV4(companyCode, plantId, languageId, warehouseId,
                                        itemCode, manufacturerName, alternateUom, bagSize);
                                log.info("existingBinItemCheck: " + existingBinItemCheck);
                                if (existingBinItemCheck != null) {
                                    proposedStorageBin = existingBinItemCheck.getProposedStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.getLevelId()));
                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                }
                            }
                            List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
                            log.info("existingBinCheck: " + existingBinCheck);
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                                storageBinPutAway.setStorageBin(existingBinCheck);
                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                }
                            }
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                                List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
                                log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                                log.info("BinClassId: " + binClassId);
                                storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                }
                                if (proposedNonCbmStorageBin == null) {
                                    StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                                    log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
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
                            StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                            if (proposedBin != null) {
                                putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                                log.info("D --> Proposed Bin: " + proposedBin.getStorageBin());
                            }
                        }
                    }
                    if (putAwayHeader.getProposedStorageBin() == null) {
                        StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                        log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber);
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

                    //PROP_HE_NO	<- PAWAY_HE_NO
                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                    putAwayHeader.setReferenceField5(itemCode);
                    putAwayHeader.setReferenceField6(manufacturerName);
                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                    Long statusId = 19L;
                    putAwayHeader.setStatusId(statusId);
                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    //PA_NO
                    NUMBER_RANGE_CODE = 6L;
                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setPackBarcodes(packBarcode);
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    // Updating Grline field -------------> PutAwayNumber
                    log.info("Updation of PutAwayNumber on GrLine Started");
                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                            putAwayHeader.getPutAwayNumber());

                    log.info("Updation of PutAwayNumber on GrLine Completed");

                    /*----------------Inventory tables Create---------------------------------------------*/
                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    //bypass quality header and line
                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }

    /**
     * @param company
     * @param plant
     * @param language
     * @param warehouse
     * @param item
     * @param manufactureName
     * @param preInbound
     * @param refDocNo
     * @param createdGRLines
     * @param loginUserID
     * @throws Exception
     */
    private void createPutAwayHeaderNonCBMV7(String company, String plant, String language,
                                             String warehouse, String item, String manufactureName,
                                             String preInbound, String refDocNo,
                                             List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
        try {

            String idMasterToken = getIDMasterAuthToken();
            //PA_NO
            NUMBER_RANGE_CODE = 7L;
            String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

            log.info("PA number ----------------> {}", nextPANumber);

            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

            for (GrLineV2 createdGRLine : createdGRLines) {

                // Setting params
                String languageId = createdGRLine.getLanguageId();
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String warehouseId = createdGRLine.getWarehouseId();
                String itemCode = createdGRLine.getItemCode();
                String manufacturerName = createdGRLine.getManufacturerName();
                String preInboundNo = createdGRLine.getPreInboundNo();
                String refDocNumber = createdGRLine.getRefDocNumber();


                String proposedStorageBin = createdGRLine.getInterimStorageBin();
                String alternateUom = createdGRLine.getAlternateUom();
                Double bagSize = createdGRLine.getBagSize();

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(companyCode);
                storageBinPutAway.setPlantId(plantId);
                storageBinPutAway.setLanguageId(languageId);
                storageBinPutAway.setWarehouseId(warehouseId);

                //  ASS_HE_NO
                if (createdGRLine != null) {
                    // Insert record into PutAwayHeader
                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayHeader.setCompanyCodeId(companyCode);
                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                    //Inbound Quality Number
                    NUMBER_RANGE_CODE = 23L;
                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

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
//                    log.info("Inventory StorageBin List: " + inventoryStorageBinList);

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
//                            cbm = 0D;               //to break the loop
                        }
                        if (storageBin == null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                            cbm = 0D;               //to break the loop
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
//                            cbm = 0D;   //break the loop
                        }
                        if (proposedBinClass7Bin == null) {
                            binClassId = 2L;
                            log.info("BinClassId : " + binClassId);
                            StorageBinV2 stBin = mastersService.getStorageBin(
                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                            log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
//                            cbm = 0D;   //break the loop
                        }
                    }

                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                        if (stBinInventoryList != null) {
                            log.info("BinClassId : " + binClassId);
                            if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                                if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
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
//                                    cbm = 0D;   //break the loop
                                }
                                if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                    storageBinPutAway.setBinClassId(7L);
                                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                                    if (proposedBinClass7Bin != null) {
                                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
                                        putAwayHeader.setProposedStorageBin(proposedStBin);
                                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                                        log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
//                                        cbm = 0D;   //break the loop
                                    }
                                    if (proposedBinClass7Bin == null) {
                                        binClassId = 2L;
                                        log.info("BinClassId : " + binClassId);
                                        StorageBinV2 stBin = mastersService.getStorageBin(
                                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                        log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
//                                        cbm = 0D;   //break the loop
                                    }
                                }
                            }
                        }
                    }

                    //Last Picked Bin as Proposed Bin If it is empty
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {

                        PickupLineV2 pickupLineList = getPickupLineForLastBinCheck(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                        log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
                        if (pickupLineList != null) {
                            putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
                            putAwayHeader.setLevelId(pickupLineList.getLevelId());
                            log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
                            log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
//                            cbm = 0D;   //break the loop
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
////                                cbm = 0D;   //break the loop
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
////                                    cbm = 0D;   //break the loop
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

////                                    cbm = 0D;   //break the loop
                                }
                                if (proposedNonCbmStorageBin == null) {
                                    binClassId = 2L;
                                    log.info("BinClassId : " + binClassId);
                                    StorageBinV2 stBin = mastersService.getStorageBin(
                                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                    log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                                    cbm = 0D;   //break the loop
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
//                                cbm = 0D;   //break the loop
                            }
                            if (proposedBinClass7Bin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("D --> reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
//                                cbm = 0D;   //break the loop
                            }
                        }
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber);
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

                    //PROP_HE_NO	<- PAWAY_HE_NO
                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                    putAwayHeader.setReferenceField5(itemCode);
                    putAwayHeader.setReferenceField6(manufacturerName);
                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                    Long statusId = 19L;
                    putAwayHeader.setStatusId(statusId);
                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    //PA_NO
                    NUMBER_RANGE_CODE = 6L;
                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setPackBarcodes(packBarcode);
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    // Updating Grline field -------------> PutAwayNumber
                    log.info("Updation of PutAwayNumber on GrLine Started");
                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                            putAwayHeader.getPutAwayNumber());

                    log.info("Updation of PutAwayNumber on GrLine Completed");

                    /*----------------Inventory tables Create---------------------------------------------*/
                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    //bypass quality header and line
                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
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

//    @Scheduled(fixedDelay = 15000)
//    private void schedulePostGRLineProcessV2() {
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
//        log.info("Create PutawayHeader Schedule Initiated : " + new Date());
//        GrLineV2 createdGRLine = getGrLineV2();
//        log.info("----schedulePostGRLineProcessV2-----createdGRLine------> " + createdGRLine);
//
//        if (createdGRLine != null) {
//            String companyCode = createdGRLine.getCompanyCode();
//            String plantId = createdGRLine.getPlantId();
//            String languageId = createdGRLine.getLanguageId();
//            String warehouseId = createdGRLine.getWarehouseId();
//            String refDocNumber = createdGRLine.getRefDocNumber();
//            Long inboundOrderTypeId = createdGRLine.getInboundOrderTypeId();
//            try {
//                log.info("PutAwayHeader Create started.....");
//                createPutAwayHeaderNonCBMV2(createdGRLine, createdGRLine.getCreatedBy());
//                log.info("PutAwayHeader Create Completed.....");
//
//                //putaway header successfully created - changing flag to 10
//                grLineV2Repository.updateGrLineStatusV2(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getPreInboundNo(),
//                        createdGRLine.getCreatedOn(), createdGRLine.getLineNo(), createdGRLine.getItemCode(), 10L);
//                log.info("GrLine status 10 updated..! ");
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.info("GrLine status 100 updated - putaway header create - failed..! ");
//                log.error("Exception occurred while create putaway header " + e.toString());
//
//                //putaway header create failed - changing flag to 100
//                grLineV2Repository.updateGrLineStatusV2(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getPreInboundNo(),
//                        createdGRLine.getCreatedOn(), createdGRLine.getLineNo(), createdGRLine.getItemCode(), 100L);
//                sendMail(companyCode, plantId, languageId, warehouseId, refDocNumber, getInboundOrderTypeTable(inboundOrderTypeId), e.toString());
//            }
//        }
//    }


//    @Scheduled(fixedDelay = 15000)
    private void schedulePostGRLineProcessV2() {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("Create PutawayHeader Schedule Initiated from Fahaheel : " + new Date());
        createGrLineSchedule();

//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("AUTO_LAP");
//        log.info("Create PutawayHeader Schedule Initiated from AutoLap : " + new Date());
//        createGrLineSchedule();

    }

    /**
     * create GrLine in Schedule
     * for modify code purpose of Fahaheel and autolap  (28-05-25)
     */
    public void createGrLineSchedule() {
        GrLineV2 createdGRLine = getGrLineV2();
        log.info("----schedulePostGRLineProcessV2-----createdGRLine------> " + createdGRLine);

        if (createdGRLine != null) {
            String companyCode = createdGRLine.getCompanyCode();
            String plantId = createdGRLine.getPlantId();
            String languageId = createdGRLine.getLanguageId();
            String warehouseId = createdGRLine.getWarehouseId();
            String refDocNumber = createdGRLine.getRefDocNumber();
            Long inboundOrderTypeId = createdGRLine.getInboundOrderTypeId();
            try {
                log.info("PutAwayHeader Create started.....");
                createPutAwayHeaderNonCBMV2(createdGRLine, createdGRLine.getCreatedBy());
                log.info("PutAwayHeader Create Completed.....");

                //putaway header successfully created - changing flag to 10
                grLineV2Repository.updateGrLineStatusV2(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getPreInboundNo(),
                        createdGRLine.getCreatedOn(), createdGRLine.getLineNo(), createdGRLine.getItemCode(), 10L);
                log.info("GrLine status 10 updated..! ");
            } catch (Exception e) {
                e.printStackTrace();
                log.info("GrLine status 100 updated - putaway header create - failed..! ");
                log.error("Exception occurred while create putaway header " + e.toString());

                //putaway header create failed - changing flag to 100
                grLineV2Repository.updateGrLineStatusV2(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getPreInboundNo(),
                        createdGRLine.getCreatedOn(), createdGRLine.getLineNo(), createdGRLine.getItemCode(), 100L);
                sendMail(companyCode, plantId, languageId, warehouseId, refDocNumber, getInboundOrderTypeTable(inboundOrderTypeId), e.toString());
            }
        }
    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
    public void createPutAwayHeaderNonCBMV2(GrLineV2 createdGRLine, String loginUserID) throws Exception {
        try {
            log.info("createdGRLine ------> {}", createdGRLine);

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
//                log.info("Inventory StorageBin List: " + inventoryStorageBinList);

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
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
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
//                if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {
//                    PickupLineV2 pickupLineList = pickupLineService.getPickupLineForLastBinCheck(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
//                    log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
//                    //                    String lastPickedStorageBinList = null;
//                    if (pickupLineList != null) {
//                        putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
//                        putAwayHeader.setLevelId(pickupLineList.getLevelId());
//                        log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
//                        log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
//                        cbm = 0D;   //break the loop
//                        //                        }
//                    }
//                }

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
                InventoryV2 createdinventory = inventoryService.createInventoryNonCBMV2(createdGRLine);

                /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
                //                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
            }
//            if (cbm == 0D) {
//                break outerloop;
//            }
//        }
        } catch (Exception e) {
            log.error("Create Putaway header exception : " + e.getLocalizedMessage());
            throw e;
        }
    }

    /**
     * @return
     */
    public GrLineV2 getGrLineV2() {
        GrLineV2 grLine = grLineV2Repository.findTopByIsPutAwayHeaderCreatedAndDeletionIndicatorOrderByCreatedOn(9L, 0L);
        log.info("GRLine for putaway header : " + grLine);
        if (grLine != null) {
            grLineV2Repository.updateGrLineStatusV2(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getPreInboundNo(),
                    grLine.getCreatedOn(), grLine.getLineNo(), grLine.getItemCode(), 1L);
            return grLine;
        }
        return null;
    }

    public StagingLineEntityV2 createLines(GrLineV2 grLine, String loginUserId) throws Exception {
        try {
            StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
            InboundLineV2 inboundLine = new InboundLineV2();
            PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(grLine, loginUserId);
            BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
            BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
            stagingLine.setStagingNo(grLine.getStagingNo());
            stagingLine.setPalletCode(grLine.getPalletCode());
            stagingLine.setCaseCode(grLine.getCaseCode());
            stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
            stagingLine.setRemarks("test");                     //Handled for UI - hardcoded

            preInboundLineV2Repository.save(preInboundLine);
            inboundLineV2Repository.save(inboundLine);
            StagingLineEntityV2 createdStagingLine = stagingLineV2Repository.save(stagingLine);

            return createdStagingLine;
        } catch (Exception e) {
            log.error("IB - Line Create Exception: " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public IKeyValuePair getDescription(String companyCodeId, String plantId, String languageId, String warehouseId) {
        return stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
    }

    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    private StorageBinV2 getReserveBin(String warehouseId, Long binClassId, String companyCode, String plantId, String languageId) {
        log.info("BinClassId : " + binClassId);
        return storageBinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
    }

    /**
     * @param grLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineV2(GrLineV2 grLine, String loginUserId) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(grLine, preInboundLine, CommonUtils.getNullPropertyNames(grLine));

            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setManufacturerCode(MFR_NAME);
            preInboundLine.setManufacturerName(MFR_NAME);
            preInboundLine.setManufacturerPartNo(MFR_NAME);
            if (grLine.getReferenceDocumentType() == null) {
                preInboundLine.setReferenceDocumentType(getInboundOrderTypeDesc(
                        grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getInboundOrderTypeId()));
            }

            if (preInboundLine.getOrderUom() != null) {
                AlternateUomImpl alternateUom = getUom(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(),
                        grLine.getWarehouseId(), grLine.getItemCode(), grLine.getOrderUom());
                if (alternateUom == null) {
                    throw new BadRequestException("AlternateUom is not available for this item : " + grLine.getItemCode());
                }
                if (alternateUom != null) {
                    preInboundLine.setOrderUom(alternateUom.getUom());
                    preInboundLine.setAlternateUom(alternateUom.getAlternateUom());
                    preInboundLine.setBagSize(alternateUom.getAlternateUomQty());
                    preInboundLine.setNoBags(grLine.getGoodReceiptQty());
                }
            }

            String barcodeId = generateBarCodeIdV4(grLine.getItemCode(), grLine.getRefDocNumber());
            preInboundLine.setBarcodeId(barcodeId);

            preInboundLine.setOrderQty(grLine.getGoodReceiptQty());
            preInboundLine.setStatusDescription(getStatusDescription(13L, grLine.getLanguageId()));
            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param referenceField
     * @param error
     */
    public void sendMail(String companyCodeId, String plantId, String languageId, String warehouseId,
                         String refDocNumber, String referenceField, String error) {
        try {
            InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
            inboundOrderCancelInput.setCompanyCodeId(companyCodeId);
            inboundOrderCancelInput.setPlantId(plantId);
            inboundOrderCancelInput.setLanguageId(languageId);
            inboundOrderCancelInput.setWarehouseId(warehouseId);
            inboundOrderCancelInput.setRefDocNumber(refDocNumber);
            inboundOrderCancelInput.setReferenceField1(referenceField);
            inboundOrderCancelInput.setRemarks(error);
            mastersService.sendMail(inboundOrderCancelInput);
        } catch (Exception ex) {
            log.error("Exception occurred while Sending Mail " + ex.toString());
//            throw ex;
        }
    }

    /**
     * @param itemCode
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeIdV4(String itemCode, String refDocNumber) {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        String orderNoLastThreeDigit = refDocNumber;
        if (refDocNumber != null && refDocNumber.length() >= 3) {
            orderNoLastThreeDigit = refDocNumber.substring(refDocNumber.length() - 3);
        } else {
            throw new IllegalArgumentException("refDocNumber must be at least 3 characters long");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(orderNoLastThreeDigit);
        stringBuilder.append(DateUtils.getDateMonth());
        return stringBuilder.toString();
    }


    /**
     * @param newGrLines
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<GrLineV2> createGrLineNonCBMV5(@Valid List<AddGrLineV2> newGrLines, String loginUserID) throws Exception {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        try {
            /*
             * In GR line, Create a packbarcode for every 10 (configured) qty of the selected ST_SEC_ID of the ST_BIN in putawayheader
             */

            // Inserting multiple records
            log.info("Grline--------->" + newGrLines);
            for (AddGrLineV2 newGrLine : newGrLines) {
                Long lineNo = newGrLine.getLineNo();
                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    Date expiryDateAsDate = null;
                    // [SELF_LIFE || EXPIRY_DATE
                    try {
                        Long self_life = imbasicdata1Repository.getSelfLife(newGrLine.getItemCode(), newGrLine.getCompanyCode(), newGrLine.getPlantId(),
                                newGrLine.getLanguageId(), newGrLine.getWarehouseId(), newGrLine.getManufacturerName());
                        Date manufacturerDate = newGrLine.getManufacturerDate();

                        if (self_life != null && manufacturerDate != null) {
                            LocalDate manuDate = manufacturerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate expiryDate = manuDate.plusDays(self_life);
                            log.info("Expiry Date: " + expiryDate);
                            expiryDateAsDate = Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        } else {
                            log.info("Self life or Manufacturer Date is null");
                        }

                    } catch (Exception e) {
                        log.info("Self_life calculation error --- " + e.getMessage());
                    }

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
//                        Double grQty = newGrLine.getAcceptedQty();
//                        Double grQty = newGrLine.getPieceQty();
                        if (newGrLine.getInboundOrderTypeId() == 11) {
                            Double grQty = newGrLine.getQtyInCreate();
                            dbGrLine.setGoodReceiptQty(grQty);
                            dbGrLine.setAcceptedQty(grQty);
                            dbGrLine.setDamageQty(0D);
                            log.info("A-------->: " + dbGrLine);
                        } else {
                            Double grQty = null;
                            if(newGrLine.getQtyInPiece() == 0 || newGrLine.getQtyInPiece() == null) {
                                grQty = newGrLine.getQtyInCase();
                            }else {
                                grQty =  newGrLine.getQtyInPiece();
                            }
                            dbGrLine.setGoodReceiptQty(grQty);
                            dbGrLine.setAcceptedQty(grQty);
                            dbGrLine.setDamageQty(0D);
                            log.info("A-------->: " + dbGrLine);
                        }
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
//                        Double grQty = newGrLine.getDamageQty();
//                        Double grQty = newGrLine.getPieceQty();
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }
                    log.info("Quantity type--->" + packBarcode.getQuantityType());
                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setStatusId(14L);

                    if (packBarcode.getBarcode() != null) {
                        dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    } else {
                        String barcode = getNextRangeNumber(6L, newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId());
                        log.info("NumberRange PackBarcode is ------> " + barcode);
                        dbGrLine.setPackBarcodes(barcode);
                    }
                    //12-03-2024 - Ticket No. ALM/2024/006
                    if (dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());

                    if (newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(), newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), newGrLine.getItemCode(), newGrLine.getManufacturerName(), newGrLine.getLineNo());
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                    if (dbGrQty != null) {
                        Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
//                        if (newGrLine.getOrderQty() < totalGrQty){
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
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
                    if (newGrLine.getCustomerId() != null) {
                        dbGrLine.setReferenceField6(newGrLine.getCustomerId());
                    }
                    if (newGrLine.getCustomerName() != null) {
                        dbGrLine.setReferenceField7(newGrLine.getCustomerName());
                    }


                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = 0D;
                    Double acceptQty = 0D;
                    Double damageQty = 0D;
                    if (newGrLine.getInboundOrderTypeId() == 11) {
                        if (newGrLine.getOrderQty() != null) {
                            invoiceQty = newGrLine.getQtyInCreate();
                        }
                    } else {
                        if (newGrLine.getOrderQty() != null) {
                            invoiceQty = newGrLine.getQtyInPiece();
                        }
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

//                    if (variance < 0D) {
//                        throw new BadRequestException("Variance Qty cannot be Less than 0");
//                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setBranchCode(newGrLine.getBranchCode());
                    dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                    dbGrLine.setIsCompleted(newGrLine.getIsCompleted());

                    if (newGrLine.getBarcodeId() != null) {
                        dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                    } else {
                        dbGrLine.setBarcodeId(newGrLine.getPartner_item_barcode());
                    }
                    dbGrLine.setManufacturerDate(newGrLine.getManufacturerDate());
                    dbGrLine.setExpiryDate(expiryDateAsDate);
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
                        // Lead Time
                        GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(dbGrLine.getLanguageId(), dbGrLine.getCompanyCode(),
                                dbGrLine.getPlantId(), dbGrLine.getWarehouseId(), dbGrLine.getGoodsReceiptNo(), new Date());
                        if (implForLeadTime != null) {
                            if (!implForLeadTime.getDiffDays().equals("00")) {
                                String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                        + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffHours().equals("00")) {
                                String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                        + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                                String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else {
                                String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            }
                        }

                        dbGrLine.setQtyInPiece(newGrLine.getQtyInPiece());
                        dbGrLine.setQtyInCase(newGrLine.getQtyInCase());
                        dbGrLine.setQtyInCreate(newGrLine.getQtyInCreate());
                        dbGrLine.setVehicleNo(newGrLine.getVehicleNo());
                        dbGrLine.setVehicleUnloadingDate(newGrLine.getVehicleUnloadingDate());
                        dbGrLine.setVehicleReportingDate(newGrLine.getVehicleReportingDate());
                        dbGrLine.setReceivingVariance(newGrLine.getReceivingVariance());
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

//                        if (createdGRLine != null && !createGrLineError) {
//                            // Record Insertion in PUTAWAYHEADER table
//                            createPutAwayHeaderNonCBMV2(createdGRLine, loginUserID);
//                        }
                    }

                }
                log.info("Records were inserted successfully...");

                log.info("NewCreated---->" + newGrLine.getNewCreated());
                // Creating inboundline and Stagingline
                if (newGrLine.getNewCreated().equals("True")) {

                    //PreInboundline
                    PreInboundLineEntityV2 preInboundLineEntityV2 = new PreInboundLineEntityV2();
                    BeanUtils.copyProperties(newGrLine, preInboundLineEntityV2, CommonUtils.getNullPropertyNames(newGrLine));
                    preInboundLineEntityV2.setCompanyCode(newGrLine.getCompanyCode());
                    preInboundLineEntityV2.setLanguageId(newGrLine.getLanguageId());
                    preInboundLineEntityV2.setPlantId(newGrLine.getPlantId());
                    preInboundLineEntityV2.setWarehouseId(newGrLine.getWarehouseId());
                    preInboundLineEntityV2.setPreInboundNo(newGrLine.getPreInboundNo());
                    preInboundLineEntityV2.setRefDocNumber(newGrLine.getRefDocNumber());
                    preInboundLineEntityV2.setItemCode(newGrLine.getItemCode());
                    preInboundLineEntityV2.setItemDescription(newGrLine.getItemDescription());
                    preInboundLineEntityV2.setLineNo(newGrLine.getLineNo());
                    preInboundLineEntityV2.setStatusDescription(newGrLine.getStatusDescription());
                    preInboundLineEntityV2.setDeletionIndicator(0L);
                    preInboundLineV2Repository.save(preInboundLineEntityV2);
                    //Inboundline
                    InboundLineV2 inboundLineV2 = new InboundLineV2();
                    BeanUtils.copyProperties(newGrLine, inboundLineV2, CommonUtils.getNullPropertyNames(newGrLine));
                    inboundLineV2.setCompanyCode(newGrLine.getCompanyCode());
                    inboundLineV2.setLanguageId(newGrLine.getLanguageId());
                    inboundLineV2.setPlantId(newGrLine.getPlantId());
                    inboundLineV2.setWarehouseId(newGrLine.getWarehouseId());
                    inboundLineV2.setPreInboundNo(newGrLine.getPreInboundNo());
                    inboundLineV2.setRefDocNumber(newGrLine.getRefDocNumber());
                    inboundLineV2.setItemCode(newGrLine.getItemCode());
                    inboundLineV2.setDeletionIndicator(0L);
                    inboundLineV2.setDescription(newGrLine.getItemDescription());
                    inboundLineV2.setLineNo(newGrLine.getLineNo());
                    inboundLineV2.setCompanyDescription(newGrLine.getCompanyDescription());
                    inboundLineV2.setPlantDescription(newGrLine.getPlantDescription());
                    inboundLineV2.setWarehouseDescription(newGrLine.getWarehouseDescription());
                    inboundLineV2.setStatusDescription(newGrLine.getStatusDescription());
                    inboundLineV2.setStatusId(newGrLine.getStatusId());
                    inboundLineV2Repository.save(inboundLineV2);

                    //Stagingline
                    StagingLineEntityV2 stagingLineEntityV2 = new StagingLineEntityV2();
                    BeanUtils.copyProperties(newGrLine, stagingLineEntityV2, CommonUtils.getNullPropertyNames(newGrLine));
                    stagingLineEntityV2.setCompanyCode(newGrLine.getCompanyCode());
                    stagingLineEntityV2.setLanguageId(newGrLine.getLanguageId());
                    stagingLineEntityV2.setPlantId(newGrLine.getPlantId());
                    stagingLineEntityV2.setWarehouseId(newGrLine.getWarehouseId());
                    stagingLineEntityV2.setPreInboundNo(newGrLine.getPreInboundNo());
                    stagingLineEntityV2.setRefDocNumber(newGrLine.getRefDocNumber());
                    stagingLineEntityV2.setStagingNo(newGrLine.getStagingNo());
                    stagingLineEntityV2.setItemCode(newGrLine.getItemCode());
                    stagingLineEntityV2.setPalletCode(newGrLine.getPalletCode());
                    stagingLineEntityV2.setCaseCode(newGrLine.getCaseCode());
                    stagingLineEntityV2.setCompanyDescription(newGrLine.getCompanyDescription());
                    stagingLineEntityV2.setPlantDescription(newGrLine.getPlantDescription());
                    stagingLineEntityV2.setWarehouseDescription(newGrLine.getWarehouseDescription());
                    stagingLineEntityV2.setStatusDescription(newGrLine.getStatusDescription());
                    stagingLineEntityV2.setStatusId(newGrLine.getStatusId());
                    stagingLineEntityV2.setDeletionIndicator(0L);
                    stagingLineEntityV2.setLineNo(newGrLine.getLineNo());
                    stagingLineV2Repository.save(stagingLineEntityV2);

//                    lineNo++;
                }
            }
            if (!createdGRLines.isEmpty()) {
                // Record Insertion in PUTAWAYHEADER table
                createPutAwayHeaderNonCBMV5(createdGRLines, loginUserID);
            }
            //Update GrHeader using stored Procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, createdGRLines.get(0).getLanguageId());
            grHeaderV2Repository.updateGrheaderStatusUpdateProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");

            //Update staging Line using stored Procedure
            stagingLineV2Repository.updateStagingLineUpdateNewProcV5(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, new Date());
            log.info("stagingLine Status updated using Stored Procedure ");

            //Update InboundLine using Stored Procedure
            inboundLineV2Repository.updateInboundLineStatusUpdateNewProcV5(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 17L, statusDescription, new Date());
            log.info("inboundLine Status updated using Stored Procedure ");

            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public List<GrLineV2> createGrLineNonCBMV7(@Valid List<AddGrLineV2> newGrLines, String loginUserID) throws Exception {

        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        String itemCode = null;
        String manufacturerName = null;
        String packBarcodes = "99999";

        try {
            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {

                // Generating PackBarcodes for incoming GrLines
                List<PackBarcode> grPackBarcode = generatePackBarcodeV7(newGrLine.getCompanyCode(), newGrLine.getLanguageId(), newGrLine.getPlantId(),
                        newGrLine.getAcceptedQty(), newGrLine.getDamageQty(), newGrLine.getWarehouseId());
                newGrLine.setPackBarcodes(grPackBarcode);

                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }

                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("Accept (A)-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(grQty);
                        dbGrLine.setOrderQty(grQty);
                        dbGrLine.setAcceptedQty(0D);
                        log.info("Damage (D)-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcodes);
                    dbGrLine.setGrUom(newGrLine.getOrderUom());
                    dbGrLine.setStatusId(14L);

                    if (dbGrLine.getGoodReceiptQty() != null && dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    if (dbGrLine.getOrderUom() == null) {
                        throw new BadRequestException("Uom is mandatory");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() != null && newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();
                    itemCode = dbGrLine.getItemCode();
                    manufacturerName = dbGrLine.getManufacturerName();

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), itemCode, manufacturerName, newGrLine.getLineNo(), dbGrLine.getGoodReceiptQty());
                    log.info("dbGrQty+newGrQty, OrdQty: " + dbGrQty + ", " + newGrLine.getOrderQty());
//                    if (dbGrQty != null) {
//                        if (newGrLine.getOrderQty() < dbGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
//                    }

                    description = getDescription(companyCode, plantId, languageId, warehouseId);
                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = newGrLine.getOrderQty() != null ? newGrLine.getOrderQty() : 0D;
                    Double acceptQty = newGrLine.getAcceptedQty() != null ? newGrLine.getAcceptedQty() : 0D;
                    Double damageQty = newGrLine.getDamageQty() != null ? newGrLine.getDamageQty() : 0D;

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber, newGrLine.getLineNo(),
                            itemCode, manufacturerName);
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity == null) {
                        dbStagingLineEntity = createLines(dbGrLine, loginUserID);
                    }

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        if (newGrLine.getAlternateUom() == null || newGrLine.getBagSize() == null || newGrLine.getAlternateUom().isBlank()) {
                            dbGrLine.setAlternateUom(dbStagingLineEntity.getAlternateUom());
                            dbGrLine.setNoBags(dbStagingLineEntity.getNoBags());
                            dbGrLine.setBagSize(dbStagingLineEntity.getBagSize());
                            dbGrLine.setMrp(dbStagingLineEntity.getMrp());
                            dbGrLine.setItemType(dbStagingLineEntity.getItemType());
                            dbGrLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                            dbGrLine.setSize(dbStagingLineEntity.getSize());
                            dbGrLine.setBrand(dbStagingLineEntity.getBrand());
                        }
                    }
                    //Calculate No of Bags for Damage Qty
                    if (dbGrLine.getQuantityType().equalsIgnoreCase("D")) {
//                        double actualQty = getQuantity(dbGrLine.getGoodReceiptQty(), dbGrLine.getBagSize());
                        double actualQty = dbGrLine.getGoodReceiptQty();
                        double NO_OF_BAGS = actualQty / dbGrLine.getBagSize();
                        dbGrLine.setNoBags(roundUp(NO_OF_BAGS));
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                    }
                    statusDescription = getStatusDescription(dbGrLine.getStatusId(), languageId);
                    dbGrLine.setStatusDescription(statusDescription);

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());
                    dbGrLine.setBarcodeId(newGrLine.getPartner_item_barcode());

                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
                            goodsReceiptNo, itemCode, dbGrLine.getLineNo(), languageId, companyCode, plantId,
                            refDocNumber, dbGrLine.getPackBarcodes(), warehouseId, preInboundNo,
                            dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        // Lead Time
                        GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(languageId, companyCode, plantId, warehouseId, goodsReceiptNo, new Date());
                        if (implForLeadTime != null) {
                            if (!implForLeadTime.getDiffDays().equals("00")) {
                                String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                        + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffHours().equals("00")) {
                                String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                        + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                                String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else {
                                String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            }
                        }

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
                    }
                }

                log.info("Records were inserted successfully...");
            }

            if (createdGRLines != null) {
                createPutAwayHeaderNonCBMV7(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, preInboundNo, refDocNumber, createdGRLines, loginUserID);
            }

            //GrHeader Status 17 Updating Using Stored Procedure when condition met - multiple procedure combined to single procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, languageId);
            grHeaderV2Repository.updateStatusProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("Status Update Using Stored Procedure ---> GrHeader, StagingLine, InboundLine");

            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param createdGRLines
     * @param loginUserID
     * @throws Exception
     */
    private void createPutAwayHeaderNonCBMV5(List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

            long NUM_RAN_CODE = 7;
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, createdGRLines.get(0).getCompanyCode(), createdGRLines.get(0).getPlantId(), createdGRLines.get(0).getLanguageId(), createdGRLines.get(0).getWarehouseId(), authTokenForIDMasterService.getAccess_token());

            log.info("PutAwayNumber Generated: " + nextPANumber);
            log.info("PutAwayNumber------->" + createdGRLines);

            fireBaseNotification(createdGRLines.get(0), nextPANumber, loginUserID);

            for (GrLineV2 createdGRLine : createdGRLines) {
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
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number

                    // PA_NO
//                long NUM_RAN_CODE = 7;
//                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
//                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
//                log.info("PutAwayNumber Generated: " + nextPANumber);

                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

                    //set bar code id for packbarcode
                    putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());

                    //set pack bar code for actual packbarcode
                    putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());

                    if (createdGRLine.getAcceptedQty() != 0 && createdGRLine.getAcceptedQty() != null) {
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getAcceptedQty());
                    } else {
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getDamageQty());
                    }

                    //-----------------PROP_ST_BIN---------------------------------------------

                    //V2 Code
                    Long binClassId = 0L;                   //actual code follows
                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 || createdGRLine.getInboundOrderTypeId() == 11L) {
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
//                    log.info("Inventory StorageBin List: " + inventoryStorageBinList);

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
//                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
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

                    if (createdGRLine.getInboundOrderTypeId() == 11L) {

                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);

                        StorageBinV2 proposedBinClass1Bin = mastersService.getStorageBinBinClassId1(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (proposedBinClass1Bin != null) {
                            String proposedStBin = proposedBinClass1Bin.getStorageBin();
                            putAwayHeader.setProposedStorageBin(proposedStBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedBinClass1Bin.getFloorId()));
                            log.info("Return Order --> BinClassId1 Proposed Bin: " + proposedStBin);
                            cbm = 0D;   //break the loop
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
                                if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
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
                        putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(companyCode, plantId, languageId, warehouseId, createdGRLine.getInboundOrderTypeId()));
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

                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());

                    putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                    putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
                    putAwayHeader.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());

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
                    putAwayHeader.setReferenceField6(createdGRLine.getReferenceField6());

                    putAwayHeader.setStatusId(19L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader.setQtyInCreate(createdGRLine.getQtyInCreate());
                    putAwayHeader.setQtyInCase(createdGRLine.getQtyInCase());
                    putAwayHeader.setQtyInPiece(createdGRLine.getQtyInPiece());
                    putAwayHeader.setManufacturerDate(createdGRLine.getManufacturerDate());
                    putAwayHeader.setVehicleNo(createdGRLine.getVehicleNo());
                    putAwayHeader.setVehicleUnloadingDate(createdGRLine.getVehicleUnloadingDate());
                    putAwayHeader.setVehicleReportingDate(createdGRLine.getVehicleReportingDate());
                    putAwayHeader.setReceivingVariance(createdGRLine.getReceivingVariance());
                    if (createdGRLine.getAcceptedQty() != null && createdGRLine.getAcceptedQty() != 0) {
                        if (createdGRLine.getOrderUom().equalsIgnoreCase("Case")) {
                            putAwayHeader.setOrderQty(createdGRLine.getQtyInCase());
                        }
                        if (createdGRLine.getOrderUom().equalsIgnoreCase("Crate")) {
                            putAwayHeader.setOrderQty(createdGRLine.getQtyInCreate());
                        }
                        if (createdGRLine.getOrderUom().equalsIgnoreCase("Piece")) {
                            putAwayHeader.setOrderQty(createdGRLine.getPieceQty());
                        }
                    } else {
                        putAwayHeader.setOrderQty(createdGRLine.getDamageQty());
                    }

                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    /*----------------Inventory tables Create---------------------------------------------*/
                    InventoryV2 createdinventory = createInventoryNonCBMV5(createdGRLine);

                    /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
                }
//            if (cbm == 0D) {
//                break outerloop;
//            }
            }
        } catch (Exception e) {
            log.error("Exception while PutAwayHeader create : " + e.toString());
            throw e;
        }
    }


    /**
     * @param createdGRLine
     * @return
     */
    public InventoryV2 createInventoryNonCBMV5(GrLineV2 createdGRLine) {
        try {
            log.info("createdGRLine-->" + createdGRLine);
            InventoryV2 dbInventory = null;
            String packparcode = createdGRLine.getBatchSerialNumber();
            if (createdGRLine.getBatchSerialNumber() != null) {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getManufacturerName(),
                        packparcode, 3L, createdGRLine.getBatchSerialNumber(), 0L);
            } else if (createdGRLine.getBarcodeId() != null) {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndBarcodeIdAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getBarcodeId(),
                        createdGRLine.getManufacturerName(),
                        "99999", 3L, 0L);
            } else {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getManufacturerName(),
                        "99999", 3L, 0L);
            }
            log.info("dbInventory----->" + dbInventory);
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
                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setPieceQty(createdGRLine.getPieceQty());
                inventory.setCaseQty(createdGRLine.getCaseQty());
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : " + createdinventory);
                } catch (Exception e1) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
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
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());

                if (createdGRLine.getBarcodeId() != null) {
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
                    inventory.setInventoryQuantity(round1(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty()));
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(round1(inventory.getInventoryQuantity()));
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(round1(createdGRLine.getGoodReceiptQty()));
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(round1(inventory.getInventoryQuantity()));
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setCrateQty(createdGRLine.getOrderQty());
                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                inventory.setQtyInCreate(createdGRLine.getQtyInCreate());
                inventory.setQtyInPiece(createdGRLine.getQtyInPiece());
                inventory.setQtyInCase(createdGRLine.getQtyInCase());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setVehicleNo(createdGRLine.getVehicleNo());
                inventory.setVehicleUnloadingDate(createdGRLine.getVehicleUnloadingDate());
                inventory.setVehicleReportingDate(createdGRLine.getVehicleReportingDate());
                inventory.setReceivingVariance(createdGRLine.getReceivingVariance());
                inventory.setReferenceField6(createdGRLine.getReferenceField6());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : " + createdinventory);
                } catch (Exception e) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }
            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Create Inventory method for Knowell
     *
     * @param createdGRLine
     * @return
     */
    public InventoryV2 createInventoryNonCBMV7(GrLineV2 createdGRLine) {
        try {
            log.info("createdGRLine-->" + createdGRLine);
            InventoryV2 dbInventory = null;
            String packparcode = createdGRLine.getBatchSerialNumber();
            if (createdGRLine.getBatchSerialNumber() != null) {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getManufacturerName(),
                        packparcode, 3L, createdGRLine.getBatchSerialNumber(), 0L);
            } else if (createdGRLine.getBarcodeId() != null) {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndBarcodeIdAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getBarcodeId(),
                        createdGRLine.getManufacturerName(),
                        "99999", 3L, 0L);
            } else {
                dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(),
                        createdGRLine.getItemCode(),
                        createdGRLine.getManufacturerName(),
                        "99999", 3L, 0L);
            }
            log.info("dbInventory----->" + dbInventory);
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
                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setPieceQty(createdGRLine.getPieceQty());
                inventory.setCaseQty(createdGRLine.getCaseQty());
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : " + createdinventory);
                } catch(Exception e) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
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
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());

                if (createdGRLine.getBarcodeId() != null) {
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
                    inventory.setInventoryQuantity(round1(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty()));
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(round1(inventory.getInventoryQuantity()));
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(round1(createdGRLine.getGoodReceiptQty()));
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(round1(inventory.getInventoryQuantity()));
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getItemCode());
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setCrateQty(createdGRLine.getOrderQty());
                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                inventory.setQtyInCreate(createdGRLine.getQtyInCreate());
                inventory.setQtyInPiece(createdGRLine.getQtyInPiece());
                inventory.setQtyInCase(createdGRLine.getQtyInCase());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setVehicleNo(createdGRLine.getVehicleNo());
                inventory.setVehicleUnloadingDate(createdGRLine.getVehicleUnloadingDate());
                inventory.setVehicleReportingDate(createdGRLine.getVehicleReportingDate());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : " + createdinventory);
                } catch (Exception e1) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }
            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param grLineV2
     */
    private void fireBaseNotification(GrLineV2 grLineV2, String putAwayNumber, String loginUserID) {
        try {
//            try {
//                DataBaseContextHolder.setCurrentDb("MT");
//                String profile = dbConfigRepository.getDbName(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(profile);

            log.info("Notification Input ----> | " + grLineV2.getCompanyCode() + " | " + grLineV2.getPlantId() + " | " + grLineV2.getLanguageId() + " | " + grLineV2.getWarehouseId());
            List<String> deviceToken = grLineV2Repository.getDeviceToken(grLineV2.getCompanyCode(), grLineV2.getPlantId(), grLineV2.getLanguageId(), grLineV2.getWarehouseId(), loginUserID);
            log.info("deviceToken ------> {}", deviceToken);
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String title = "Inbound Create";
                String message = "Putaway Header Created Sucessfully - " + putAwayNumber;

                NotificationSave notificationInput = new NotificationSave();
                notificationInput.setUserId(Collections.singletonList(loginUserID));
                notificationInput.setUserType(null);
                notificationInput.setMessage(message);
                notificationInput.setTopic(title);
                notificationInput.setReferenceNumber(grLineV2.getRefDocNumber());
                notificationInput.setDocumentNumber(grLineV2.getPreInboundNo());
                notificationInput.setCompanyCodeId(grLineV2.getCompanyCode());
                notificationInput.setPlantId(grLineV2.getPlantId());
                notificationInput.setLanguageId(grLineV2.getLanguageId());
                notificationInput.setWarehouseId(grLineV2.getWarehouseId());
                notificationInput.setCreatedOn(grLineV2.getCreatedOn());
                notificationInput.setCreatedBy(loginUserID);

                log.info("pushNotification started");
                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
                log.info("pushNotification completed");
            }
//            } finally {
//                DataBaseContextHolder.clear();
//            }
        } catch (Exception e) {
            log.error("Inbound firebase notification error", e); // This logs the full stack trace
        }
    }
}
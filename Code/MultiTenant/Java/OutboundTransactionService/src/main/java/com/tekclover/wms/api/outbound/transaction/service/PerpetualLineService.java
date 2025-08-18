package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.*;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.*;
import com.tekclover.wms.api.outbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.outbound.transaction.model.dto.*;
import com.tekclover.wms.api.outbound.transaction.repository.*;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.inventory.InventoryMovement;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PutAwayLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.CycleCountLine;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.outbound.transaction.repository.specification.PerpetualLineSpecification;
import com.tekclover.wms.api.outbound.transaction.repository.specification.PerpetualLineV2Specification;
import com.tekclover.wms.api.outbound.transaction.repository.specification.PerpetualZeroStkLineV2Specification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PerpetualLineService extends BaseService {
    @Autowired
    private PerpetualZeroStkLineRepository perpetualZeroStkLineRepository;
    @Autowired
    private CycleCountLineRepository cycleCountLineRepository;
    @Autowired
    private PerpetualHeaderV2Repository perpetualHeaderV2Repository;

    @Autowired
    PropertiesConfig propertiesConfig;
    @Autowired
    private PerpetualLineTempV2Repository perpetualLineTempV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PerpetualLineV2Repository perpetualLineV2Repository;

    private static final String WRITEOFF = "WRITEOFF";
    //    private static final String SKIP = "SKIP";
    private static final String SKIP = "CONFIRM";
    private static final String RECOUNT = "RECOUNT";

    @Autowired
    PerpetualLineRepository perpetualLineRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    MastersService mastersService;

    @Autowired
    PerpetualHeaderService perpetualHeaderService;

    @Autowired
    PerpetualLineService perpetualLineService;

    @Autowired
    ImBasicData1Repository imbasicdata1Repository;

    //=======================================================================================================================
    @Autowired
    private PickupLineRepository pickupLineRepository;

    @Autowired
    private PutAwayLineService putAwayLineService;

    @Autowired
    private PickupLineService pickupLineService;

    @Autowired
    private PerpetualZeroStkLineService perpetualZeroStkLineService;

    String statusDescription = null;
    //=======================================================================================================================

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getConnectorServiceApiUrl() {
        return propertiesConfig.getConnectorServiceUrl();
    }


    //=======================================================================================================================

    /**
     * getPerpetualLines
     *
     * @return
     */
    public List<PerpetualLine> getPerpetualLines() {
        List<PerpetualLine> perpetualLineList = perpetualLineRepository.findAll();
        perpetualLineList = perpetualLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return perpetualLineList;
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param statusId
     * @return
     */
    public List<PerpetualLine> getPerpetualLine(String companyCodeId, String languageId,
                                                String plantId, String warehouseId, List<Long> statusId) {

        return perpetualLineRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndStatusIdInAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, statusId, 0L);
    }

    /**
     *
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param cycleCounterId
     * @param statusId
     * @return
     */
    public List<PerpetualLine> getPerpetualLine(String companyCodeId, String languageId,
                                                String plantId, String warehouseId,String cycleCounterId, List<Long> statusId) {

        return perpetualLineRepository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndCycleCounterIdAndStatusIdInAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId,cycleCounterId, statusId, 0L);
    }

    /**
     * getPerpetualLine
     *
     * @param cycleCountNo
     * @return
     */
    public PerpetualLine getPerpetualLine(String warehouseId, String cycleCountNo,
                                          String storageBin, String itemCode, String packBarcodes) {
        PerpetualLine perpetualLine =
                perpetualLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
                        getCompanyCode(), getPlantId(), warehouseId, cycleCountNo, storageBin, itemCode,
                        packBarcodes, 0L);
        if (perpetualLine == null) {
            throw new BadRequestException("The given PerpetualLine ID - "
                    + " warehouseId: " + warehouseId + ","
                    + "cycleCountNo: " + cycleCountNo + ","
                    + "storageBin: " + storageBin + ","
                    + "itemCode: " + itemCode + ","
                    + "packBarcodes: " + packBarcodes + ","
                    + " doesn't exist.");
        }
        return perpetualLine;
    }

    /**
     * @param cycleCountNo
     * @return
     */
    public List<PerpetualLine> getPerpetualLine(String cycleCountNo) {
        List<PerpetualLine> perpetualLine = perpetualLineRepository.findByCycleCountNoAndDeletionIndicator(cycleCountNo, 0L);
        return perpetualLine;
    }

    /**
     * @param cycleCountNo
     * @param cycleCounterId
     * @return
     */
    public List<PerpetualLine> getPerpetualLine(String cycleCountNo, List<String> cycleCounterId) {
        List<PerpetualLine> perpetualLine =
                perpetualLineRepository.findByCycleCountNoAndCycleCounterIdInAndDeletionIndicator(cycleCountNo, cycleCounterId, 0L);
        return perpetualLine;
    }

    /**
     * @param searchPerpetualLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<PerpetualLine> findPerpetualLine(SearchPerpetualLine searchPerpetualLine)
            throws ParseException, java.text.ParseException {
        if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualLine.getStartCreatedOn(),
                    searchPerpetualLine.getEndCreatedOn());
            searchPerpetualLine.setStartCreatedOn(dates[0]);
            searchPerpetualLine.setEndCreatedOn(dates[1]);
        }

        PerpetualLineSpecification spec = new PerpetualLineSpecification(searchPerpetualLine);
        List<PerpetualLine> perpetualLineResults = perpetualLineRepository.findAll(spec);
        return perpetualLineResults;
    }

    /**
     * Stream
     *
     * @param searchPerpetualLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<PerpetualLine> findPerpetualLineStream(SearchPerpetualLine searchPerpetualLine)
            throws ParseException, java.text.ParseException {
        if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualLine.getStartCreatedOn(),
                    searchPerpetualLine.getEndCreatedOn());
            searchPerpetualLine.setStartCreatedOn(dates[0]);
            searchPerpetualLine.setEndCreatedOn(dates[1]);
        }

        PerpetualLineSpecification spec = new PerpetualLineSpecification(searchPerpetualLine);
        Stream<PerpetualLine> perpetualLineResults = perpetualLineRepository.stream(spec, PerpetualLine.class);
        return perpetualLineResults;
    }

    /**
     * createPerpetualLine
     *
     * @param newPerpetualLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PerpetualLine createPerpetualLine(AddPerpetualLine newPerpetualLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PerpetualLine dbPerpetualLine = new PerpetualLine();
        log.info("newPerpetualLine : " + newPerpetualLine);
        BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
        dbPerpetualLine.setDeletionIndicator(0L);
        dbPerpetualLine.setCreatedBy(loginUserID);
        dbPerpetualLine.setCreatedOn(new Date());
        dbPerpetualLine.setCountedBy(loginUserID);
        dbPerpetualLine.setCountedOn(new Date());
        return perpetualLineRepository.save(dbPerpetualLine);
    }

    /**
     * @param newPerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PerpetualLine> createPerpetualLine(List<AddPerpetualLine> newPerpetualLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PerpetualLine> newPerpetualLineList = new ArrayList<>();
        for (AddPerpetualLine newPerpetualLine : newPerpetualLines) {
            PerpetualLine dbPerpetualLine = new PerpetualLine();
            BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
            dbPerpetualLine.setDeletionIndicator(0L);
            dbPerpetualLine.setCreatedBy(loginUserID);
            dbPerpetualLine.setCreatedOn(new Date());
            dbPerpetualLine.setCountedBy(loginUserID);
            dbPerpetualLine.setCountedOn(new Date());
            newPerpetualLineList.add(dbPerpetualLine);
        }

        return perpetualLineRepository.saveAll(newPerpetualLineList);
    }

    /**
     * @param assignHHTUsers
     * @param loginUserID
     * @return
     */
    public List<PerpetualLine> updateAssingHHTUser(List<AssignHHTUserCC> assignHHTUsers, String loginUserID) {
        List<PerpetualLine> responseList = new ArrayList<>();
        for (AssignHHTUserCC assignHHTUser : assignHHTUsers) {
            perpetualLineRepository.updateHHTUser(assignHHTUser.getCycleCounterId(),
                    assignHHTUser.getCycleCounterName(), 72L, loginUserID, new Date(), assignHHTUser.getWarehouseId(), assignHHTUser.getCycleCountNo(),
                    assignHHTUser.getStorageBin(), assignHHTUser.getItemCode(), assignHHTUser.getPackBarcodes());
        }
        return responseList;
    }

    /**
     * @param updatePerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PerpetualLine> updatePerpetualLineForMobileCount(List<UpdatePerpetualLine> updatePerpetualLines,
                                                                 String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<PerpetualLine> responsePerpetualLines = new ArrayList<>();

        List<AddPerpetualLine> createBatchPerpetualLines = new ArrayList<>();
        List<PerpetualLine> updateBatchPerpetualLines = new ArrayList<>();
        for (UpdatePerpetualLine updatePerpetualLine : updatePerpetualLines) {
            PerpetualLine dbPerpetualLine = getPerpetualLine(updatePerpetualLine.getWarehouseId(), updatePerpetualLine.getCycleCountNo(),
                    updatePerpetualLine.getStorageBin(), updatePerpetualLine.getItemCode(), updatePerpetualLine.getPackBarcodes());
            if (dbPerpetualLine != null) { /* Update */
                BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));

                // INV_QTY
                double INV_QTY = updatePerpetualLine.getInventoryQuantity();
                dbPerpetualLine.setInventoryQuantity(INV_QTY);

                // CTD_QTY
                if (updatePerpetualLine.getCountedQty() != null) {
                    double CTD_QTY = updatePerpetualLine.getCountedQty();
                    dbPerpetualLine.setCountedQty(CTD_QTY);

                    // VAR_QTY = INV_QTY - CTD_QTY
                    double VAR_QTY = INV_QTY - CTD_QTY;
                    dbPerpetualLine.setVarianceQty(VAR_QTY);

                    /*
                     * HardCoded Value "78" if VAR_QTY = 0 and
                     * Hardcodeed value"74" - if VAR_QTY is greater than or less than Zero
                     */
                    if (VAR_QTY == 0) {
                        dbPerpetualLine.setStatusId(78L);
                    } else if (VAR_QTY > 0 || VAR_QTY < 0) {
                        dbPerpetualLine.setStatusId(74L);
                    }
                }

                dbPerpetualLine.setCountedBy(loginUserID);
                dbPerpetualLine.setCountedOn(new Date());
                updateBatchPerpetualLines.add(dbPerpetualLine);
            } else {
                // Create new Record
                AddPerpetualLine newPerpetualLine = new AddPerpetualLine();
                BeanUtils.copyProperties(updatePerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
                newPerpetualLine.setCycleCountNo(updatePerpetualLine.getCycleCountNo());
                newPerpetualLine.setDeletionIndicator(0L);
                newPerpetualLine.setCreatedBy(loginUserID);
                newPerpetualLine.setCreatedOn(new Date());
                newPerpetualLine.setCountedBy(loginUserID);
                newPerpetualLine.setCountedOn(new Date());
                createBatchPerpetualLines.add(newPerpetualLine);
            }
        }

        responsePerpetualLines.addAll(createPerpetualLine(createBatchPerpetualLines, loginUserID));
        responsePerpetualLines.addAll(perpetualLineRepository.saveAll(updateBatchPerpetualLines));
        return responsePerpetualLines;
    }

    /**
     * @param cycleCountNo
     * @param updatePerpetualLines
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public PerpetualUpdateResponse updatePerpetualLine(String cycleCountNo, List<UpdatePerpetualLine> updatePerpetualLines,
                                                       String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<PerpetualLine> responsePerpetualLines = new ArrayList<>();
        List<AddPerpetualLine> newPerpetualLines = new ArrayList<>();
        try {
            for (UpdatePerpetualLine updatePerpetualLine : updatePerpetualLines) {
                PerpetualLine dbPerpetualLine = getPerpetualLine(updatePerpetualLine.getWarehouseId(),
                        updatePerpetualLine.getCycleCountNo(),
                        updatePerpetualLine.getStorageBin(),
                        updatePerpetualLine.getItemCode(),
                        updatePerpetualLine.getPackBarcodes());
                BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
                dbPerpetualLine.setRemarks(updatePerpetualLine.getRemarks());
                dbPerpetualLine.setCycleCountAction(updatePerpetualLine.getCycleCountAction());

                /*
                 * 1. Action = WRITEOFF
                 * If ACTION = WRITEOFF , update ACTION field in PERPETUALLINE as WRITEOFF by passing unique fields and
                 * update in STATUS_ID field as "78"
                 */
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(WRITEOFF)) {
                    dbPerpetualLine.setStatusId(78L);
                    dbPerpetualLine.setCycleCountAction(WRITEOFF);
                    PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Inventory table update
                     * ---------------------------
                     * Fetch CNT_QTY of the selected ITM_CODE and Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE values in INVENTORY table
                     * and replace INV_QTY as CNT_QTY
                     */
                    updateInventory(updatedPerpetualLine);
                    createInventoryMovement(updatedPerpetualLine);
                }


                /*
                 * 2. Action = SKIP
                 * if ACTION = SKIP in UI,  update ACTION field in PERPETUALLINE as SKIP by passing unique fields
                 * and update in STATUS_ID field as "78"
                 */
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(SKIP)) {
                    dbPerpetualLine.setStatusId(78L);
                    dbPerpetualLine.setCycleCountAction(SKIP);
                    PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Inventory table update
                     * ---------------------------
                     * Insert a new record by passing WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN (fetch ST_BIN
                     * from STORAGEBIN table where BIN_CL_ID=5) values in INVENTORY table and append INV_QTY as
                     * VAR_QTY
                     */
//					createInventory (updatedPerpetualLine);
//					createInventoryMovement (updatedPerpetualLine);
                }

                /*
                 * 3. Action = RECOUNT (default Action Value)
                 * If ACTION = RECOUNT, update ACTION field in PERPETUALLINE as SKIP by passing unique fields
                 * and update in STATUS_ID field as "78"
                 */
                log.info("---------->updatePerpetualLine data : " + updatePerpetualLine);
                log.info("---------->RECOUNT : " + RECOUNT);
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(RECOUNT)) {
                    dbPerpetualLine.setStatusId(78L);
                    dbPerpetualLine.setCycleCountAction(RECOUNT);
                    PerpetualLine updatedPerpetualLine = perpetualLineRepository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Preparation of new PerpetualLines
                     */
                    AddPerpetualLine newPerpetualLine = new AddPerpetualLine();
                    BeanUtils.copyProperties(updatedPerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatedPerpetualLine));
                    newPerpetualLine.setStatusId(70L);
                    newPerpetualLines.add(newPerpetualLine);
                }
            }

            PerpetualHeader newlyCreatedPerpetualHeader = new PerpetualHeader();
            if (!newPerpetualLines.isEmpty()) {
                log.info("newPerpetualLines : " + newPerpetualLines);
                // Create new PerpetualHeader and Lines
                PerpetualHeaderEntity createdPerpetualHeader = createNewHeaderNLines(cycleCountNo, newPerpetualLines, loginUserID);
                if(createdPerpetualHeader != null) {
                    BeanUtils.copyProperties(createdPerpetualHeader, newlyCreatedPerpetualHeader, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
                }
            }

            // Update new PerpetualHeader
            PerpetualHeader dbPerpetualHeader = perpetualHeaderService.getPerpetualHeader(cycleCountNo);
            UpdatePerpetualHeader updatePerpetualHeader = new UpdatePerpetualHeader();
            BeanUtils.copyProperties(dbPerpetualHeader, updatePerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
            PerpetualHeader updatedPerpetualHeader = perpetualHeaderService.updatePerpetualHeader(dbPerpetualHeader.getWarehouseId(), dbPerpetualHeader.getCycleCountTypeId(),
                    dbPerpetualHeader.getCycleCountNo(), dbPerpetualHeader.getMovementTypeId(), dbPerpetualHeader.getSubMovementTypeId(), loginUserID);
            log.info("updatedPerpetualHeader : " + updatedPerpetualHeader);

            PerpetualUpdateResponse response = new PerpetualUpdateResponse();
            response.setPerpetualHeader(newlyCreatedPerpetualHeader);
            response.setPerpetualLines(responsePerpetualLines);
            log.info("PerpetualUpdateResponse------> : " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param cycleCountNo
     * @param newPerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private PerpetualHeaderEntity createNewHeaderNLines(String cycleCountNo, List<AddPerpetualLine> newPerpetualLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        log.info("newPerpetualLines : " + newPerpetualLines);
        if (newPerpetualLines != null) {
            PerpetualHeader dbPerpetualHeader = perpetualHeaderService.getPerpetualHeader(cycleCountNo);
            AddPerpetualHeader newPerpetualHeader = new AddPerpetualHeader();
            BeanUtils.copyProperties(dbPerpetualHeader, newPerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
            newPerpetualHeader.setAddPerpetualLine(newPerpetualLines);
            PerpetualHeaderEntity createdPerpetualHeader = perpetualHeaderService.createPerpetualHeader(newPerpetualHeader, loginUserID);
            log.info("createdPerpetualHeader : " + createdPerpetualHeader);
            return createdPerpetualHeader;
        }
        return null;
    }

    /**
     * @param updatePerpetualLine
     * @return
     */
    private Inventory updateInventory(PerpetualLine updatePerpetualLine) {
        Inventory inventory = inventoryService.getInventory(updatePerpetualLine.getWarehouseId(),
                updatePerpetualLine.getPackBarcodes(), updatePerpetualLine.getItemCode(),
                updatePerpetualLine.getStorageBin());
        if (inventory != null) {
            inventory.setInventoryQuantity(updatePerpetualLine.getCountedQty());
            Inventory updatedInventory = inventoryRepository.save(inventory);
            log.info("updatedInventory : " + updatedInventory);
            return updatedInventory;
        } else {
            return createInventory(updatePerpetualLine);
        }
    }

    /**
     * @param updatePerpetualLine
     * @return
     */
    private Inventory createInventory(PerpetualLine updatePerpetualLine) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(updatePerpetualLine, inventory, CommonUtils.getNullPropertyNames(updatePerpetualLine));
        inventory.setCompanyCodeId(updatePerpetualLine.getCompanyCodeId());

        // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
        inventory.setVariantCode(1L);
        inventory.setVariantSubCode("1");
        inventory.setStorageMethod("1");
        inventory.setBatchSerialNumber("1");
        inventory.setBinClassId(6L);

        // ST_BIN ---Pass WH_ID/BIN_CL_ID=5 in STORAGEBIN table and fetch ST_BIN value and update
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        StorageBin storageBin =
                mastersService.getStorageBin(updatePerpetualLine.getWarehouseId(), 5L, authTokenForMastersService.getAccess_token());
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
        inventory.setInventoryQuantity(updatePerpetualLine.getVarianceQty());

        // INV_UOM
        inventory.setInventoryUom(updatePerpetualLine.getInventoryUom());

        inventory.setCreatedBy(updatePerpetualLine.getCreatedBy());
        inventory.setCreatedOn(updatePerpetualLine.getCreatedOn());
        Inventory createdinventory = inventoryRepository.save(inventory);
        log.info("created inventory : " + createdinventory);
        return createdinventory;
    }

    /**
     * @param updatedPerpetualLine
     * @return
     */
    private InventoryMovement createInventoryMovement(PerpetualLine updatedPerpetualLine) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(updatedPerpetualLine, inventoryMovement, CommonUtils.getNullPropertyNames(updatedPerpetualLine));

        inventoryMovement.setLanguageId(updatedPerpetualLine.getLanguageId());
        inventoryMovement.setCompanyCodeId(updatedPerpetualLine.getCompanyCodeId());
        inventoryMovement.setPlantId(updatedPerpetualLine.getPlantId());
        inventoryMovement.setWarehouseId(updatedPerpetualLine.getWarehouseId());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(4L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        // ITEM_TEXT
        // Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventoryMovement.getItemCode(),
                inventoryMovement.getWarehouseId(), authTokenForMastersService.getAccess_token());
        inventoryMovement.setDescription(imBasicData1.getDescription());

        // MFR_PART of ITM_CODE from BASICDATA1
        inventoryMovement.setManufacturerName(imBasicData1.getManufacturerPartNo());

        /*
         * MVT_QTY_VAL
         * -----------------
         * Hard Coded value "P", if VAR_QTY is negative and Hard coded value "N", if VAR_QTY is positive
         */
        if (updatedPerpetualLine.getVarianceQty() < 0) {
            inventoryMovement.setMovementQtyValue("P");
        } else if (updatedPerpetualLine.getVarianceQty() > 0) {
            inventoryMovement.setMovementQtyValue("N");
        }

        inventoryMovement.setMovementQty(updatedPerpetualLine.getVarianceQty());
        inventoryMovement.setBatchSerialNumber("1");
        inventoryMovement.setMovementDocumentNo(updatedPerpetualLine.getCycleCountNo());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(updatedPerpetualLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(new Date());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("created InventoryMovement : " + inventoryMovement);
        return inventoryMovement;
    }

    //=====================================================================V2========================================================================

    /**
     * @return
     */
    public List<PerpetualLineV2> getPerpetualLinesV2() {
        List<PerpetualLineV2> perpetualLineList = perpetualLineV2Repository.findAll();
        perpetualLineList = perpetualLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return perpetualLineList;
    }


    /**
     * getPerpetualLine
     *
     * @param cycleCountNo
     * @return
     */
    public PerpetualLineV2 getPerpetualLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo,
                                              String storageBin, String itemCode, String manufacturerName, String packBarcodes) {
        PerpetualLineV2 perpetualLine =
                perpetualLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndManufacturerNameAndPackBarcodesAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, cycleCountNo, storageBin, itemCode, manufacturerName, packBarcodes, 0L);
        if (perpetualLine == null) {
            throw new BadRequestException("The given PerpetualLine ID - "
                    + " warehouseId: " + warehouseId + ","
                    + "cycleCountNo: " + cycleCountNo + ","
                    + "storageBin: " + storageBin + ","
                    + "itemCode: " + itemCode + ","
                    + "manufacturerName: " + manufacturerName + ","
                    + "packBarcodes: " + packBarcodes + ","
                    + " doesn't exist.");
        }
        return perpetualLine;
    }

    /**
     * @param cycleCountNo
     * @return
     */
    public List<PerpetualLineV2> getPerpetualLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo) {
//        List<PerpetualLineV2> perpetualLine = perpetualLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
//                companyCodeId, plantId, languageId, warehouseId, cycleCountNo, 0L);

        List<PerpetualLineV2> perpetualLine = perpetualLineV2Repository.getPerpetualLines(companyCodeId, plantId, warehouseId, cycleCountNo);
        return perpetualLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param cycleCountNo
     * @param statusId
     * @return
     */
    public List<PerpetualLineV2> getPerpetualLineForFindV2(String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, List<Long> statusId) {
        List<PerpetualLineV2> perpetualLine = perpetualLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStatusIdInAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, cycleCountNo, statusId, 0L);
        return perpetualLine;
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
    public PerpetualLineV2 getPerpetualLineForStockAdjustmentV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String itemCode, String manufacturerName) {
        PerpetualLineV2 perpetualLine =
                perpetualLineV2Repository.findPerpetualLineByItemCode(
                        itemCode, companyCodeId, plantId, languageId, warehouseId, manufacturerName);
        return perpetualLine;
    }

    /**
     * @param searchPerpetualLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<PerpetualLineV2> findPerpetualLineV2(SearchPerpetualLineV2 searchPerpetualLine) throws java.text.ParseException {
        if (searchPerpetualLine.getStartCreatedOn() != null && searchPerpetualLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualLine.getStartCreatedOn(),
                    searchPerpetualLine.getEndCreatedOn());
            searchPerpetualLine.setStartCreatedOn(dates[0]);
            searchPerpetualLine.setEndCreatedOn(dates[1]);
        }

        PerpetualLineV2Specification spec = new PerpetualLineV2Specification(searchPerpetualLine);
        PerpetualZeroStkLineV2Specification specification = new PerpetualZeroStkLineV2Specification(searchPerpetualLine);
        List<PerpetualLineV2> perpetualLineResults = perpetualLineV2Repository.findAll(spec);
        List<PerpetualZeroStockLine> perpetualZeroStockLineList = perpetualZeroStkLineRepository.findAll(specification);
        if(perpetualZeroStockLineList != null && !perpetualZeroStockLineList.isEmpty()) {
            for(PerpetualZeroStockLine perpetualZeroStockLine : perpetualZeroStockLineList) {
                PerpetualLineV2 dbPerpetualLine = new PerpetualLineV2();
                BeanUtils.copyProperties(perpetualZeroStockLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(perpetualZeroStockLine));
                perpetualLineResults.add(dbPerpetualLine);
            }
            log.info("Perpetual Line with Zero Stock: " + perpetualZeroStockLineList);
        }

        log.info("perpetualLineResults -----> {}", perpetualLineResults);

        return perpetualLineResults;
    }

    /**
     * createPerpetualLine
     *
     * @param newPerpetualLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PerpetualLineV2 createPerpetualLineV2(PerpetualLineV2 newPerpetualLine, String loginUserID) throws java.text.ParseException {
        PerpetualLineV2 dbPerpetualLine = new PerpetualLineV2();
        log.info("newPerpetualLine : " + newPerpetualLine);
        BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
        dbPerpetualLine.setDeletionIndicator(0L);
        dbPerpetualLine.setCreatedBy(loginUserID);
        dbPerpetualLine.setCreatedOn(new Date());
        dbPerpetualLine.setCountedBy(loginUserID);
        dbPerpetualLine.setCountedOn(new Date());
        return perpetualLineV2Repository.save(dbPerpetualLine);
    }

    /**
     * @param newPerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PerpetualLineV2> createPerpetualLineV2(List<PerpetualLineV2> newPerpetualLines, String loginUserID) {
        List<PerpetualLineV2> newPerpetualLineList = new ArrayList<>();
        for (PerpetualLineV2 newPerpetualLine : newPerpetualLines) {
            PerpetualLineV2 dbPerpetualLine = new PerpetualLineV2();
            BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
            dbPerpetualLine.setDeletionIndicator(0L);

            statusDescription = pickupLineRepository.getStatusDescription(dbPerpetualLine.getStatusId(), dbPerpetualLine.getLanguageId());
            dbPerpetualLine.setStatusDescription(statusDescription);

            dbPerpetualLine.setCreatedBy(loginUserID);
            dbPerpetualLine.setCreatedOn(new Date());
            dbPerpetualLine.setCountedBy(loginUserID);
            dbPerpetualLine.setCountedOn(new Date());
            newPerpetualLineList.add(dbPerpetualLine);
            perpetualLineV2Repository.delete(dbPerpetualLine);      // Deleting coz throwing duplicate error in MT
        }
        return perpetualLineV2Repository.saveAll(newPerpetualLineList);
    }

    /**
     * @param assignHHTUsers
     * @param loginUserID
     * @return
     */
    public List<PerpetualLineV2> updateAssingHHTUserV2(List<AssignHHTUserCC> assignHHTUsers, String loginUserID) {
        log.info("assignHHTUsers : " + assignHHTUsers);
        List<PerpetualLineV2> responseList = new ArrayList<>();
        for (AssignHHTUserCC assignHHTUser : assignHHTUsers) {

            statusDescription = pickupLineRepository.getStatusDescription(72L, assignHHTUser.getLanguageId());

            perpetualLineV2Repository.updateHHTUser(assignHHTUser.getCycleCounterId(),
                    assignHHTUser.getCycleCounterName(), 72L, statusDescription, loginUserID, new Date(),
                    assignHHTUser.getCompanyCodeId(), assignHHTUser.getPlantId(), assignHHTUser.getLanguageId(),
                    assignHHTUser.getWarehouseId(), assignHHTUser.getCycleCountNo(),
                    assignHHTUser.getStorageBin(), assignHHTUser.getItemCode(), assignHHTUser.getPackBarcodes());
            log.info("perpetual Line Updated with status 72, userId: " + assignHHTUser.getCycleCounterId());
        }
        PerpetualHeaderV2 dbPerpetualHeader = perpetualHeaderService.getPerpetualHeaderRecordV2(
                assignHHTUsers.get(0).getCompanyCodeId(),
                assignHHTUsers.get(0).getPlantId(),
                assignHHTUsers.get(0).getLanguageId(),
                assignHHTUsers.get(0).getWarehouseId(),
                assignHHTUsers.get(0).getCycleCountNo());

        if (dbPerpetualHeader != null) {
            List<PerpetualLineV2> perpetualLines = perpetualLineService.getPerpetualLineV2(assignHHTUsers.get(0).getCompanyCodeId(),
                    assignHHTUsers.get(0).getPlantId(),
                    assignHHTUsers.get(0).getLanguageId(),
                    assignHHTUsers.get(0).getWarehouseId(),
                    assignHHTUsers.get(0).getCycleCountNo());

            long count_72 = perpetualLines.stream().filter(a -> a.getStatusId() == 72L).count();
            log.info("status Count_72, perpetualLine Size : " + count_72 + ", " + perpetualLines.size());

            if (perpetualLines.size() >= count_72) {
                dbPerpetualHeader.setStatusId(73L);
                log.info("assignHHTUser statusId : " + dbPerpetualHeader.getStatusId());
                statusDescription = pickupLineRepository.getStatusDescription(dbPerpetualHeader.getStatusId(), assignHHTUsers.get(0).getLanguageId());
                dbPerpetualHeader.setStatusDescription(statusDescription);
                perpetualHeaderV2Repository.delete(dbPerpetualHeader);
                perpetualHeaderV2Repository.save(dbPerpetualHeader);
            }
        }

        responseList = getPerpetualLineV2(assignHHTUsers.get(0).getCompanyCodeId(),
                assignHHTUsers.get(0).getPlantId(),
                assignHHTUsers.get(0).getLanguageId(),
                assignHHTUsers.get(0).getWarehouseId(),
                assignHHTUsers.get(0).getCycleCountNo());

        return responseList;
    }

    /**
     * @param updatePerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PerpetualLineV2> updatePerpetualLineForMobileCountV2(List<PerpetualLineV2> updatePerpetualLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<PerpetualLineV2> responsePerpetualLines = new ArrayList<>();

        List<PerpetualLineV2> createBatchPerpetualLines = new ArrayList<>();
        List<PerpetualLineV2> updateBatchPerpetualLines = new ArrayList<>();
        List<PerpetualLineV2> filteredPerpetualLines = updatePerpetualLines.stream().filter(a -> a.getStatusId() != 47L).collect(Collectors.toList());
        for (PerpetualLineV2 updatePerpetualLine : filteredPerpetualLines) {
            if (updatePerpetualLine.getStatusId() != 47L) {
                PerpetualLineV2 dbPerpetualLine = getPerpetualLineV2(
                        updatePerpetualLine.getCompanyCodeId(), updatePerpetualLine.getPlantId(), updatePerpetualLine.getLanguageId(),
                        updatePerpetualLine.getWarehouseId(), updatePerpetualLine.getCycleCountNo(), updatePerpetualLine.getStorageBin(),
                        updatePerpetualLine.getItemCode(), updatePerpetualLine.getManufacturerName(), updatePerpetualLine.getPackBarcodes());
                if (dbPerpetualLine != null) { /* Update */
                    BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));

                    // INV_QTY
                    double INV_QTY = updatePerpetualLine.getInventoryQuantity();
                    dbPerpetualLine.setInventoryQuantity(INV_QTY);

                    double OB_QTY = 0D;
                    double IB_QTY = 0D;
                    List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForPerpetualCountV2(
                            updatePerpetualLine.getCompanyCodeId(),
                            updatePerpetualLine.getPlantId(),
                            updatePerpetualLine.getLanguageId(),
                            updatePerpetualLine.getWarehouseId(),
                            updatePerpetualLine.getItemCode(),
                            updatePerpetualLine.getManufacturerName(),
                            updatePerpetualLine.getStorageBin(),
                            updatePerpetualLine.getCountedOn());
                    if (putAwayLineList != null) {
                        IB_QTY = putAwayLineList.stream().mapToDouble(a -> a.getPutawayConfirmedQty()).sum();
                        dbPerpetualLine.setInboundQuantity(IB_QTY);
                    }
                    List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineForPerpetualCountV2(
                            updatePerpetualLine.getCompanyCodeId(),
                            updatePerpetualLine.getPlantId(),
                            updatePerpetualLine.getLanguageId(),
                            updatePerpetualLine.getWarehouseId(),
                            updatePerpetualLine.getItemCode(),
                            updatePerpetualLine.getManufacturerName(),
                            updatePerpetualLine.getStorageBin(),
                            updatePerpetualLine.getCreatedOn());
                    if (pickupLineList != null) {
                        OB_QTY = pickupLineList.stream().mapToDouble(a -> a.getPickConfirmQty()).sum();
                        dbPerpetualLine.setOutboundQuantity(OB_QTY);
                    }

                    Double AMS_VAR_QTY = (dbPerpetualLine.getFrozenQty() != null ? dbPerpetualLine.getFrozenQty() : 0) - (((dbPerpetualLine.getCountedQty() != null ? dbPerpetualLine.getCountedQty() : 0) + IB_QTY) - OB_QTY);
                    log.info("AMS_VAR_QTY: " + AMS_VAR_QTY);
                    dbPerpetualLine.setAmsVarianceQty(AMS_VAR_QTY);

                    // CTD_QTY
                    if (updatePerpetualLine.getCountedQty() != null) {
                        double CTD_QTY = updatePerpetualLine.getCountedQty();
                        dbPerpetualLine.setCountedQty(CTD_QTY);

                        // VAR_QTY = INV_QTY - CTD_QTY
//                    double VAR_QTY = INV_QTY - CTD_QTY;
                        double VAR_QTY = (INV_QTY + IB_QTY) - (OB_QTY + CTD_QTY);
                        dbPerpetualLine.setVarianceQty(VAR_QTY);

                        /*
                         * HardCoded Value "78" if VAR_QTY = 0 and
                         * Hardcodeed value"74" - if VAR_QTY is greater than or less than Zero
                         */
                        //status 78 commented because user doing count at first time
//                    if (VAR_QTY == 0) {
//                        dbPerpetualLine.setStatusId(78L);
//                    } else if (VAR_QTY > 0 || VAR_QTY < 0) {
                        dbPerpetualLine.setStatusId(74L);
//                    }
                    }

                    statusDescription = pickupLineRepository.getStatusDescription(dbPerpetualLine.getStatusId(), dbPerpetualLine.getLanguageId());
                    dbPerpetualLine.setStatusDescription(statusDescription);

                    dbPerpetualLine.setCountedBy(loginUserID);
                    dbPerpetualLine.setCountedOn(new Date());
                    updateBatchPerpetualLines.add(dbPerpetualLine);
                } else {
                    // Create new Record
                    PerpetualLineV2 newPerpetualLine = new PerpetualLineV2();
                    BeanUtils.copyProperties(updatePerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
                    newPerpetualLine.setCycleCountNo(updatePerpetualLine.getCycleCountNo());

                    statusDescription = pickupLineRepository.getStatusDescription(updatePerpetualLine.getStatusId(), updatePerpetualLine.getLanguageId());
                    newPerpetualLine.setStatusDescription(statusDescription);

                    newPerpetualLine.setDeletionIndicator(0L);
                    newPerpetualLine.setCreatedBy(loginUserID);
                    newPerpetualLine.setCreatedOn(new Date());
                    newPerpetualLine.setCountedBy(loginUserID);
                    newPerpetualLine.setCountedOn(new Date());
                    createBatchPerpetualLines.add(newPerpetualLine);
                }
            }
        }
        log.info("createPerpetualLineV2 initiated...");
        responsePerpetualLines.addAll(createPerpetualLineV2(createBatchPerpetualLines, loginUserID));
        log.info("createPerpetualLineV2 completed...");
        perpetualLineV2Repository.deleteAll(updateBatchPerpetualLines);               // Deleting coz throwing duplicate error in MT
        log.info("updateBatchPerpetualLines initiated...");
        responsePerpetualLines.addAll(perpetualLineV2Repository.saveAll(updateBatchPerpetualLines));
        log.info("updateBatchPerpetualLines completed...");
        return responsePerpetualLines;
    }

    /**
     * @param cycleCountNo
     * @param updatePerpetualLines
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public PerpetualUpdateResponseV2 updatePerpetualLineV2(String cycleCountNo, List<PerpetualLineV2> updatePerpetualLines,
                                                           String loginUserID) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<PerpetualLineV2> responsePerpetualLines = new ArrayList<>();
        List<PerpetualLineV2> newPerpetualLines = new ArrayList<>();
        try {
            for (PerpetualLineV2 updatePerpetualLine : updatePerpetualLines) {
            if(updatePerpetualLine.getStatusId() != 47L) {
                PerpetualLineV2 dbPerpetualLine = getPerpetualLineV2(
                        updatePerpetualLine.getCompanyCodeId(),
                        updatePerpetualLine.getPlantId(),
                        updatePerpetualLine.getLanguageId(),
                        updatePerpetualLine.getWarehouseId(),
                        updatePerpetualLine.getCycleCountNo(),
                        updatePerpetualLine.getStorageBin(),
                        updatePerpetualLine.getItemCode(),
                        updatePerpetualLine.getManufacturerName(),
                        updatePerpetualLine.getPackBarcodes());
                BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
                dbPerpetualLine.setRemarks(updatePerpetualLine.getRemarks());
                dbPerpetualLine.setCycleCountAction(updatePerpetualLine.getCycleCountAction());


                double INV_QTY = updatePerpetualLine.getInventoryQuantity();
                double CTD_QTY = updatePerpetualLine.getCountedQty();


                double OB_QTY = 0D;
                double IB_QTY = 0D;
                List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForPerpetualCountV2(
                        updatePerpetualLine.getCompanyCodeId(),
                        updatePerpetualLine.getPlantId(),
                        updatePerpetualLine.getLanguageId(),
                        updatePerpetualLine.getWarehouseId(),
                        updatePerpetualLine.getItemCode(),
                        updatePerpetualLine.getManufacturerName(),
                        updatePerpetualLine.getStorageBin(),
                        updatePerpetualLine.getCountedOn());
                if (putAwayLineList != null) {
                    IB_QTY = putAwayLineList.stream().mapToDouble(a -> a.getPutawayConfirmedQty()).sum();
                    dbPerpetualLine.setInboundQuantity(IB_QTY);
                }
                List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineForPerpetualCountV2(
                        updatePerpetualLine.getCompanyCodeId(),
                        updatePerpetualLine.getPlantId(),
                        updatePerpetualLine.getLanguageId(),
                        updatePerpetualLine.getWarehouseId(),
                        updatePerpetualLine.getItemCode(),
                        updatePerpetualLine.getManufacturerName(),
                        updatePerpetualLine.getStorageBin(),
                        updatePerpetualLine.getCreatedOn());
                if (pickupLineList != null) {
                    OB_QTY = pickupLineList.stream().mapToDouble(a -> a.getPickConfirmQty()).sum();
                    dbPerpetualLine.setOutboundQuantity(OB_QTY);
                }

                Double AMS_VAR_QTY = (dbPerpetualLine.getFrozenQty() != null ? dbPerpetualLine.getFrozenQty() : 0) - (((dbPerpetualLine.getCountedQty() != null ? dbPerpetualLine.getCountedQty() : 0) + IB_QTY) - OB_QTY);
                log.info("AMS_VAR_QTY: " + AMS_VAR_QTY);
                dbPerpetualLine.setAmsVarianceQty(AMS_VAR_QTY);

                /*
                 * 1. Action = WRITEOFF
                 * If ACTION = WRITEOFF , update ACTION field in PERPETUALLINE as WRITEOFF by passing unique fields and
                 * update in STATUS_ID field as "78"
                 */
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(WRITEOFF)) {
                    dbPerpetualLine.setStatusId(78L);
                    dbPerpetualLine.setCycleCountAction(WRITEOFF);

                    statusDescription = pickupLineRepository.getStatusDescription(78L, dbPerpetualLine.getLanguageId());
                    dbPerpetualLine.setStatusDescription(statusDescription);

                    perpetualLineV2Repository.delete(dbPerpetualLine);
                    PerpetualLineV2 updatedPerpetualLine = perpetualLineV2Repository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Inventory table update
                     * ---------------------------
                     * Fetch CNT_QTY of the selected ITM_CODE and Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE values in INVENTORY table
                     * and replace INV_QTY as CNT_QTY
                     */
//                    updateInventoryV2(updatedPerpetualLine);
//                    createInventoryMovementV2(updatedPerpetualLine);
                }


                /*
                 * 2. Action = SKIP
                 * if ACTION = SKIP in UI,  update ACTION field in PERPETUALLINE as SKIP by passing unique fields
                 * and update in STATUS_ID field as "78"
                 */
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(SKIP)) {

                    if (updatePerpetualLine.getSecondCountedQty() == null && updatePerpetualLine.getFirstCountedQty() != null) {
                        dbPerpetualLine.setSecondCountedQty(updatePerpetualLine.getCountedQty());
                        double VAR_QTY = (INV_QTY + IB_QTY) - (OB_QTY + CTD_QTY);
                        dbPerpetualLine.setVarianceQty(VAR_QTY);
                    }
                    if (updatePerpetualLine.getFirstCountedQty() == null) {
                        dbPerpetualLine.setFirstCountedQty(updatePerpetualLine.getCountedQty());
                    }

                    dbPerpetualLine.setStatusId(78L);
                    dbPerpetualLine.setCycleCountAction(SKIP);

                    statusDescription = pickupLineRepository.getStatusDescription(78L, dbPerpetualLine.getLanguageId());
                    dbPerpetualLine.setStatusDescription(statusDescription);

                    perpetualLineV2Repository.delete(dbPerpetualLine);
                    PerpetualLineV2 updatedPerpetualLine = perpetualLineV2Repository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Inventory table update
                     * ---------------------------
                     * Insert a new record by passing WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN (fetch ST_BIN
                     * from STORAGEBIN table where BIN_CL_ID=5) values in INVENTORY table and append INV_QTY as
                     * VAR_QTY
                     */
//					createInventory (updatedPerpetualLine);
//					createInventoryMovement (updatedPerpetualLine);
                }

                /*
                 * 3. Action = RECOUNT (default Action Value)
                 * If ACTION = RECOUNT, update ACTION field in PERPETUALLINE as SKIP by passing unique fields
                 * and update in STATUS_ID field as "78"
                 */
                log.info("---------->updatePerpetualLine data : " + updatePerpetualLine);
                log.info("---------->RECOUNT : " + RECOUNT);
//                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(RECOUNT) && updatePerpetualLine.getStatusId() == 75L) {
////                    dbPerpetualLine.setStatusId(78L);
//
//                    if (updatePerpetualLine.getSecondCountedQty() == null && updatePerpetualLine.getFirstCountedQty() != null) {
//                        dbPerpetualLine.setSecondCountedQty(updatePerpetualLine.getCountedQty());
//                        dbPerpetualLine.setCycleCountAction(SKIP);
//                        dbPerpetualLine.setCountedQty(0D);
//                        dbPerpetualLine.setStatusId(78L);
//                    }
//
//                    statusDescription = stagingLineV2Repository.getStatusDescription(dbPerpetualLine.getStatusId(), dbPerpetualLine.getLanguageId());
//                    dbPerpetualLine.setStatusDescription(statusDescription);
//
//                    PerpetualLineV2 updatedPerpetualLine = perpetualLineV2Repository.save(dbPerpetualLine);
//                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
//                    responsePerpetualLines.add(updatedPerpetualLine);
//
//                    /*
//                     * Preparation of new PerpetualLines
//                     */
////                    PerpetualLineV2 newPerpetualLine = new PerpetualLineV2();
////                    BeanUtils.copyProperties(updatedPerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatedPerpetualLine));
////                    newPerpetualLine.setStatusId(70L);
//
////                    statusDescription = stagingLineV2Repository.getStatusDescription(70L, updatedPerpetualLine.getLanguageId());
////                    newPerpetualLine.setStatusDescription(statusDescription);
//
////                    newPerpetualLines.add(newPerpetualLine);
//                }
                if (updatePerpetualLine.getCycleCountAction().equalsIgnoreCase(RECOUNT)) {
//                    dbPerpetualLine.setStatusId(78L);


//                    if (updatePerpetualLine.getFirstCountedQty() == null && updatePerpetualLine.getSecondCountedQty() == null) {
                    dbPerpetualLine.setFirstCountedQty(updatePerpetualLine.getCountedQty());
                    double VAR_QTY = (INV_QTY + IB_QTY) - (OB_QTY + CTD_QTY);
                    dbPerpetualLine.setVarianceQty(VAR_QTY);
                    dbPerpetualLine.setCycleCountAction(RECOUNT);
                    dbPerpetualLine.setCountedQty(0D);
                    dbPerpetualLine.setStatusId(75L);
//                    }

//                    if (updatePerpetualLine.getSecondCountedQty() != null && updatePerpetualLine.getFirstCountedQty() != null) {
//                        dbPerpetualLine.setStatusId(78L);
//                        dbPerpetualLine.setCycleCountAction(SKIP);
//                    }

                    statusDescription = pickupLineRepository.getStatusDescription(dbPerpetualLine.getStatusId(), dbPerpetualLine.getLanguageId());
                    dbPerpetualLine.setStatusDescription(statusDescription);

                    perpetualLineV2Repository.delete(dbPerpetualLine);
                    PerpetualLineV2 updatedPerpetualLine = perpetualLineV2Repository.save(dbPerpetualLine);
                    log.info("updatedPerpetualLine : " + updatedPerpetualLine);
                    responsePerpetualLines.add(updatedPerpetualLine);

                    /*
                     * Preparation of new PerpetualLines
                     */
//                    PerpetualLineV2 newPerpetualLine = new PerpetualLineV2();
//                    BeanUtils.copyProperties(updatedPerpetualLine, newPerpetualLine, CommonUtils.getNullPropertyNames(updatedPerpetualLine));
//                    newPerpetualLine.setStatusId(70L);

//                    statusDescription = stagingLineV2Repository.getStatusDescription(70L, updatedPerpetualLine.getLanguageId());
//                    newPerpetualLine.setStatusDescription(statusDescription);

//                    newPerpetualLines.add(newPerpetualLine);
                }
            }
        }

//            PerpetualHeaderV2 newlyCreatedPerpetualHeader = new PerpetualHeaderV2();
//            if (!newPerpetualLines.isEmpty()) {
//                log.info("newPerpetualLines : " + newPerpetualLines);
//                // Create new PerpetualHeader and Lines
//                PerpetualHeaderEntityV2 createdPerpetualHeader = createNewHeaderNLinesV2(cycleCountNo, newPerpetualLines, loginUserID);
//                BeanUtils.copyProperties(createdPerpetualHeader, newlyCreatedPerpetualHeader, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
//            }

            // Update new PerpetualHeader
            PerpetualHeaderV2 dbPerpetualHeader = perpetualHeaderService.getPerpetualHeaderV2(
                    updatePerpetualLines.get(0).getCompanyCodeId(),
                    updatePerpetualLines.get(0).getPlantId(),
                    updatePerpetualLines.get(0).getLanguageId(),
                    updatePerpetualLines.get(0).getWarehouseId(),
                    cycleCountNo);
            log.info("Perpetual Header: " + dbPerpetualHeader);
//            PerpetualHeaderV2 updatePerpetualHeader = new PerpetualHeaderV2();
//            BeanUtils.copyProperties(dbPerpetualHeader, updatePerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
            if(dbPerpetualHeader != null) {
                PerpetualHeaderV2 updatedPerpetualHeader = perpetualHeaderService.updatePerpetualHeaderV2(
                        dbPerpetualHeader.getCompanyCodeId(), dbPerpetualHeader.getPlantId(),
                        dbPerpetualHeader.getLanguageId(), dbPerpetualHeader.getWarehouseId(), dbPerpetualHeader.getCycleCountTypeId(),
                        dbPerpetualHeader.getCycleCountNo(), dbPerpetualHeader.getMovementTypeId(), dbPerpetualHeader.getSubMovementTypeId(), loginUserID);
                log.info("updatedPerpetualHeader : " + updatedPerpetualHeader);
            }

            PerpetualUpdateResponseV2 response = new PerpetualUpdateResponseV2();
            response.setPerpetualHeader(dbPerpetualHeader);
            response.setPerpetualLines(responsePerpetualLines);
            log.info("PerpetualUpdateResponse------> : " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param cycleCountNo
     * @param newPerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private PerpetualHeaderEntityV2 createNewHeaderNLinesV2(String cycleCountNo, List<PerpetualLineV2> newPerpetualLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        log.info("newPerpetualLines : " + newPerpetualLines);
        if (newPerpetualLines != null) {
            PerpetualHeaderV2 dbPerpetualHeader = perpetualHeaderService.getPerpetualHeaderV2(
                    newPerpetualLines.get(0).getCompanyCodeId(),
                    newPerpetualLines.get(0).getPlantId(),
                    newPerpetualLines.get(0).getLanguageId(),
                    newPerpetualLines.get(0).getWarehouseId(),
                    cycleCountNo);
            PerpetualHeaderEntityV2 newPerpetualHeader = new PerpetualHeaderEntityV2();
            BeanUtils.copyProperties(dbPerpetualHeader, newPerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
            newPerpetualHeader.setPerpetualLine(newPerpetualLines);
            PerpetualHeaderEntityV2 createdPerpetualHeader = perpetualHeaderService.createPerpetualHeaderV2(newPerpetualHeader, loginUserID);
            log.info("createdPerpetualHeader : " + createdPerpetualHeader);
            return createdPerpetualHeader;
        }
        return null;
    }

    /**
     * @param updatePerpetualLine
     * @return
     */
    private InventoryV2 updateInventoryV2(PerpetualLineV2 updatePerpetualLine) {
        InventoryV2 inventory = inventoryService.getInventoryV2(
                updatePerpetualLine.getCompanyCodeId(),
                updatePerpetualLine.getPlantId(),
                updatePerpetualLine.getLanguageId(),
                updatePerpetualLine.getWarehouseId(),
                updatePerpetualLine.getPackBarcodes(),
                updatePerpetualLine.getItemCode(),
                updatePerpetualLine.getStorageBin(),
                updatePerpetualLine.getManufacturerName());
        if (inventory != null) {
            InventoryV2 createInventory = new InventoryV2();
            BeanUtils.copyProperties(inventory, createInventory, CommonUtils.getNullPropertyNames(inventory));
            createInventory.setInventoryQuantity(updatePerpetualLine.getCountedQty());
            InventoryV2 updatedInventory = inventoryV2Repository.save(createInventory);
            log.info("updatedInventory : " + updatedInventory);
            return updatedInventory;
        } else {
            return createInventoryV2(updatePerpetualLine);
        }
    }

    /**
     * @param updatePerpetualLine
     * @return
     */
    private InventoryV2 createInventoryV2(PerpetualLineV2 updatePerpetualLine) {
        InventoryV2 inventory = new InventoryV2();
        BeanUtils.copyProperties(updatePerpetualLine, inventory, CommonUtils.getNullPropertyNames(updatePerpetualLine));
        inventory.setCompanyCodeId(updatePerpetualLine.getCompanyCodeId());
        inventory.setPlantId(updatePerpetualLine.getPlantId());
        inventory.setLanguageId(updatePerpetualLine.getLanguageId());
        inventory.setWarehouseId(updatePerpetualLine.getWarehouseId());

        // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
        inventory.setVariantCode(1L);
        inventory.setVariantSubCode("1");
        inventory.setStorageMethod("1");
        inventory.setBatchSerialNumber("1");
        inventory.setBinClassId(6L);

        inventory.setReferenceDocumentNo(updatePerpetualLine.getCycleCountNo());

        // ST_BIN ---Pass WH_ID/BIN_CL_ID=5 in STORAGEBIN table and fetch ST_BIN value and update
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        StorageBinV2 storageBin =
                mastersService.getStorageBin(
                        updatePerpetualLine.getCompanyCodeId(),
                        updatePerpetualLine.getPlantId(),
                        updatePerpetualLine.getLanguageId(),
                        updatePerpetualLine.getWarehouseId(),
                        5L, authTokenForMastersService.getAccess_token());
        if (storageBin != null) {
            log.info("StorageBin BinClass Id :5 - " + storageBin.getStorageBin());
            inventory.setStorageBin(storageBin.getStorageBin());
        }

//		List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(updatePerpetualLine.getCompanyCodeId());
        imBasicData.setPlantId(updatePerpetualLine.getPlantId());
        imBasicData.setLanguageId(updatePerpetualLine.getLanguageId());
        imBasicData.setWarehouseId(updatePerpetualLine.getWarehouseId());
        imBasicData.setItemCode(updatePerpetualLine.getItemCode());
        imBasicData.setManufacturerName(updatePerpetualLine.getManufacturerName());
        ImBasicData1 imbasicdata1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
        log.info("ImbasicData1: " + imbasicdata1);

        if (imbasicdata1 != null) {
            inventory.setReferenceField8(imbasicdata1.getDescription());
            inventory.setReferenceField9(imbasicdata1.getManufacturerPartNo());
            inventory.setDescription(imbasicdata1.getDescription());
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

        String stockTypeDesc = getStockTypeDesc(updatePerpetualLine.getCompanyCodeId(), updatePerpetualLine.getPlantId(), updatePerpetualLine.getLanguageId(), updatePerpetualLine.getWarehouseId(), 1L);
        inventory.setStockTypeDescription(stockTypeDesc);

        // SP_ST_IND_ID
        inventory.setSpecialStockIndicatorId(1L);

        // INV_QTY
        inventory.setInventoryQuantity(updatePerpetualLine.getVarianceQty());

        // INV_UOM
        inventory.setInventoryUom(updatePerpetualLine.getInventoryUom());

        inventory.setCreatedBy(updatePerpetualLine.getCreatedBy());
        inventory.setCreatedOn(updatePerpetualLine.getCreatedOn());
        inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 5));
        InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
        log.info("created inventory : " + createdinventory);
        return createdinventory;
    }

    /**
     * @param cycleCountNo
     * @param updatePerpetualLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public WarehouseApiResponse updatePerpetualLineConfirmV2(String cycleCountNo, List<PerpetualLineV2> updatePerpetualLines,
                                                             String loginUserID) throws IllegalAccessException, InvocationTargetException {
        List<PerpetualLineV2> responsePerpetualLines = new ArrayList<>();
        List<String> statusId78 = new ArrayList<>();
        List<String> statusId47 = new ArrayList<>();
        try {
            for (PerpetualLineV2 updatePerpetualLine : updatePerpetualLines) {
                if (updatePerpetualLine.getStatusId() != 47L) {
                    PerpetualLineV2 dbPerpetualLine = getPerpetualLineV2(
                            updatePerpetualLine.getCompanyCodeId(),
                            updatePerpetualLine.getPlantId(),
                            updatePerpetualLine.getLanguageId(),
                            updatePerpetualLine.getWarehouseId(),
                            updatePerpetualLine.getCycleCountNo(),
                            updatePerpetualLine.getStorageBin(),
                            updatePerpetualLine.getItemCode(),
                            updatePerpetualLine.getManufacturerName(),
                            updatePerpetualLine.getPackBarcodes());

                    if (dbPerpetualLine.getStatusId() == 78L) {
                        statusId78.add("True");
                        responsePerpetualLines.add(dbPerpetualLine);
                    }
                }
                if(updatePerpetualLine.getStatusId() == 47L) {
                    PerpetualZeroStockLine dbPerpetualLine = perpetualZeroStkLineService.getaPerpetualZeroStockLine(
                            updatePerpetualLine.getCompanyCodeId(),
                            updatePerpetualLine.getPlantId(),
                            updatePerpetualLine.getLanguageId(),
                            updatePerpetualLine.getWarehouseId(),
                            updatePerpetualLine.getCycleCountNo(),
                            updatePerpetualLine.getItemCode(),
                            updatePerpetualLine.getManufacturerName());
                    if (dbPerpetualLine.getStatusId() == 47L) {
                        statusId47.add("True");
                        if(updatePerpetualLine.getLineNo() == null){
                            updatePerpetualLine.setLineNo(dbPerpetualLine.getLineNo());
                        }
                        updatePerpetualLine.setPackBarcodes("99999");       //HardCode
                        updatePerpetualLine.setStorageBin("Z1-Y1-X1-W1");   //HardCode
                        updatePerpetualLine.setCountedQty(0D);
                        updatePerpetualLine.setDeletionIndicator(0L);
                        responsePerpetualLines.add(updatePerpetualLine);
                    }
                }
            }
            Long perpetualLineCount = updatePerpetualLines.stream().count();
            Long statusIdCount = statusId78.stream().filter(a -> a.equalsIgnoreCase("True")).count();
            Long statusId47Count = statusId47.stream().filter(a -> a.equalsIgnoreCase("True")).count();
            Long statusIdTotalCount = statusIdCount + statusId47Count;
            log.info("Count of Perpetual Line, statusId78, statusId47, total : " + perpetualLineCount + ", " + statusIdCount + "," + statusId47Count + "," + statusIdTotalCount);

            if (!perpetualLineCount.equals(statusIdTotalCount)) {
                throw new BadRequestException("Perpetual Lines are not completely Processed");
            }

//            PerpetualHeaderV2 newlyCreatedPerpetualHeader = new PerpetualHeaderV2();
//            if (!newPerpetualLines.isEmpty()) {
//                log.info("newPerpetualLines : " + newPerpetualLines);
            // Create new PerpetualHeader and Lines
//                PerpetualHeaderEntityV2 createdPerpetualHeader = createNewHeaderNLinesV2(cycleCountNo, newPerpetualLines, loginUserID);
//                BeanUtils.copyProperties(createdPerpetualHeader, newlyCreatedPerpetualHeader, CommonUtils.getNullPropertyNames(createdPerpetualHeader));
//            }
            if (perpetualLineCount.equals(statusIdTotalCount)) {
                // Update new PerpetualHeader
                PerpetualHeaderV2 dbPerpetualHeader = perpetualHeaderService.getPerpetualHeaderV2(
                        updatePerpetualLines.get(0).getCompanyCodeId(),
                        updatePerpetualLines.get(0).getPlantId(),
                        updatePerpetualLines.get(0).getLanguageId(),
                        updatePerpetualLines.get(0).getWarehouseId(),
                        cycleCountNo);
                if (dbPerpetualHeader.getStatusId() != 78L) {
                    throw new BadRequestException("Perpetual Header are not completely Processed");
                }
            }
//            PerpetualHeaderV2 updatePerpetualHeader = new PerpetualHeaderV2();
//            BeanUtils.copyProperties(dbPerpetualHeader, updatePerpetualHeader, CommonUtils.getNullPropertyNames(dbPerpetualHeader));
//            PerpetualHeaderV2 updatedPerpetualHeader = perpetualHeaderService.updatePerpetualHeaderV2(
//                    dbPerpetualHeader.getCompanyCodeId(), dbPerpetualHeader.getPlantId(),
//                    dbPerpetualHeader.getLanguageId(), dbPerpetualHeader.getWarehouseId(), dbPerpetualHeader.getCycleCountTypeId(),
//                    dbPerpetualHeader.getCycleCountNo(), dbPerpetualHeader.getMovementTypeId(), dbPerpetualHeader.getSubMovementTypeId(), loginUserID);
//            log.info("updatedPerpetualHeader : " + updatedPerpetualHeader);

//            PerpetualUpdateResponseV2 response = new PerpetualUpdateResponseV2();
//            response.setPerpetualHeader(newlyCreatedPerpetualHeader);
//            response.setPerpetualLines(responsePerpetualLines);
//            log.info("PerpetualUpdateResponse------> : " + response);

            WarehouseApiResponse warehouseApiResponse = null;
            AuthToken authTokenForConnectorService = authTokenService.getConnectorServiceAuthToken();
            if (responsePerpetualLines != null || !responsePerpetualLines.isEmpty()) {

                for (PerpetualLineV2 dbPerpetualLine : responsePerpetualLines) {
                    PerpetualLineTempV2 perpetualLineTempV2 = new PerpetualLineTempV2();
                    BeanUtils.copyProperties(dbPerpetualLine, perpetualLineTempV2, CommonUtils.getNullPropertyNames(dbPerpetualLine));
                    perpetualLineTempV2Repository.save(perpetualLineTempV2);
                }
                List<IKeyValuePair> updatePerpetualLine = perpetualLineTempV2Repository.getPickupLineCount(
                        updatePerpetualLines.get(0).getWarehouseId(),
                        updatePerpetualLines.get(0).getCompanyCodeId(),
                        updatePerpetualLines.get(0).getPlantId(),
                        cycleCountNo,
                        updatePerpetualLines.get(0).getLanguageId());
                List<UpdatePerpetualLineV2> updatePerpetualLineV2List = new ArrayList<>();
                for (IKeyValuePair iKeyValuePair : updatePerpetualLine) {
                    UpdatePerpetualLineV2 updatePerpetualLineV2 = new UpdatePerpetualLineV2();
                    updatePerpetualLineV2.setCycleCountNo(iKeyValuePair.getReferenceCycleCountNo());
                    updatePerpetualLineV2.setItemCode(iKeyValuePair.getItemCode());
                    updatePerpetualLineV2.setManufacturerName(iKeyValuePair.getManufacturerName());
                    updatePerpetualLineV2.setInventoryQty(iKeyValuePair.getInventoryQty());
                    updatePerpetualLineV2.setLineNo(iKeyValuePair.getLineNumber());
                    updatePerpetualLineV2List.add(updatePerpetualLineV2);
                }
                //update cyclecount order Table
                updatePerpetualStockCountOrderTable(updatePerpetualLineV2List);
                //push to AMS
                warehouseApiResponse = updatePerpetualLine(updatePerpetualLineV2List, authTokenForConnectorService.getAccess_token());
                if(warehouseApiResponse.getStatusCode().equalsIgnoreCase("200")){
                    PerpetualHeaderV2 dbPerpetualHeader = perpetualHeaderService.getPerpetualHeaderV2(
                            updatePerpetualLines.get(0).getCompanyCodeId(),
                            updatePerpetualLines.get(0).getPlantId(),
                            updatePerpetualLines.get(0).getLanguageId(),
                            updatePerpetualLines.get(0).getWarehouseId(),
                            cycleCountNo);
                    log.info("update perpetualHeader: --->ref_field_1--->True" + dbPerpetualHeader);
                    dbPerpetualHeader.setReferenceField1("True");
                    perpetualHeaderV2Repository.save(dbPerpetualHeader);
                }
                perpetualLineTempV2Repository.truncateTblperpetualtempline();
            }

            return warehouseApiResponse;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param updatedPerpetualLine
     * @return
     */
    private InventoryMovement createInventoryMovementV2(PerpetualLineV2 updatedPerpetualLine) throws java.text.ParseException {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(updatedPerpetualLine, inventoryMovement, CommonUtils.getNullPropertyNames(updatedPerpetualLine));

        inventoryMovement.setLanguageId(updatedPerpetualLine.getLanguageId());
        inventoryMovement.setCompanyCodeId(updatedPerpetualLine.getCompanyCodeId());
        inventoryMovement.setPlantId(updatedPerpetualLine.getPlantId());
        inventoryMovement.setWarehouseId(updatedPerpetualLine.getWarehouseId());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(4L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);

        inventoryMovement.setManufacturerName(updatedPerpetualLine.getManufacturerName());
        inventoryMovement.setCompanyDescription(updatedPerpetualLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(updatedPerpetualLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(updatedPerpetualLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(updatedPerpetualLine.getBarcodeId());
        inventoryMovement.setDescription(updatedPerpetualLine.getItemDesc());

        // ITEM_TEXT
        // Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(updatedPerpetualLine.getCompanyCodeId());
        imBasicData.setPlantId(updatedPerpetualLine.getPlantId());
        imBasicData.setLanguageId(updatedPerpetualLine.getLanguageId());
        imBasicData.setWarehouseId(updatedPerpetualLine.getWarehouseId());
        imBasicData.setItemCode(updatedPerpetualLine.getItemCode());
        imBasicData.setManufacturerName(updatedPerpetualLine.getManufacturerName());

        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());

        if (imBasicData1 != null) {

            inventoryMovement.setDescription(imBasicData1.getDescription());

            // MFR_PART of ITM_CODE from BASICDATA1
            inventoryMovement.setManufacturerName(imBasicData1.getManufacturerPartNo());
        }

        /*
         * MVT_QTY_VAL
         * -----------------
         * Hard Coded value "P", if VAR_QTY is negative and Hard coded value "N", if VAR_QTY is positive
         */
        if (updatedPerpetualLine.getVarianceQty() < 0) {
            inventoryMovement.setMovementQtyValue("P");
        } else if (updatedPerpetualLine.getVarianceQty() > 0) {
            inventoryMovement.setMovementQtyValue("N");
        }

        inventoryMovement.setMovementQty(updatedPerpetualLine.getVarianceQty());
        inventoryMovement.setBatchSerialNumber("1");
        inventoryMovement.setMovementDocumentNo(updatedPerpetualLine.getCycleCountNo());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(updatedPerpetualLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(new Date());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("created InventoryMovement : " + inventoryMovement);
        return inventoryMovement;
    }

    /**
     * @param updateStockCountLines
     */
    public void updatePerpetualStockCountOrderTable(List<UpdatePerpetualLineV2> updateStockCountLines) {
        if (updateStockCountLines != null) {
            log.info("Perpertual Lines to be Updated:" + updateStockCountLines);
            for (UpdatePerpetualLineV2 dbPerpetualLine : updateStockCountLines) {
                CycleCountLine updatePplCountedQty = cycleCountLineRepository.findByCycleCountNoAndItemCodeAndManufacturerNameAndLineOfEachItemCode(
                        dbPerpetualLine.getCycleCountNo(),
                        dbPerpetualLine.getItemCode(),
                        dbPerpetualLine.getManufacturerName(),
                        dbPerpetualLine.getLineNo());
                if (updatePplCountedQty != null) {
                    log.info("Perpertual Line to be Updated:" + updatePplCountedQty);

                    updatePplCountedQty.setCountedQty(dbPerpetualLine.getInventoryQty());
                    updatePplCountedQty.setIsCompleted("1");
//                    PerpetualLine updateCountedQty = perpetualLineRepository.save(updatePplCountedQty);
                    try {
                        cycleCountLineRepository.save(updatePplCountedQty);
//                        cycleCountLineRepository.updatePplLine(dbPerpetualLine.getInventoryQty(),
//                                1L,
//                                dbPerpetualLine.getCycleCountNo(),
//                                dbPerpetualLine.getItemCode(),
//                                dbPerpetualLine.getManufacturerName());
                        log.info("Perpertual Line CountedQty Updated, CountedQty: " + dbPerpetualLine + ", " + dbPerpetualLine.getInventoryQty());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    // PATCH - Confirm
    public WarehouseApiResponse updatePerpetualLine(List<UpdatePerpetualLineV2> updatePerpetualLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "MNRClara's RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "perpetual/updateCountedQty");

            ResponseEntity<WarehouseApiResponse> result =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WarehouseApiResponse.class);
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
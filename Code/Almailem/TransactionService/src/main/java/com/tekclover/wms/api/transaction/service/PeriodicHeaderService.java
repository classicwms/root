package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.*;
import com.tekclover.wms.api.transaction.model.cyclecount.periodic.v2.*;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountLine;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicHeaderV2Specification;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PeriodicLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PeriodicHeaderService extends BaseService {
    @Autowired
    private PeriodicZeroStkLineRepository periodicZeroStkLineRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PeriodicLineV2Repository periodicLineV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PeriodicHeaderV2Repository periodicHeaderV2Repository;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    MastersService mastersService;

    @Autowired
    PeriodicLineService periodicLineService;

    @Autowired
    PeriodicHeaderRepository periodicHeaderRepository;

    @Autowired
    PeriodicLineRepository periodicLineRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    StorageBinRepository storageBinRepository;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private PeriodicZeroStkLineService periodicZeroStkLineService;

    String statusDescription = null;

    /**
     * getPeriodicHeaders
     *
     * @return
     */
    public List<PeriodicHeaderEntity> getPeriodicHeaders() {
        List<PeriodicHeader> periodicHeaderList = periodicHeaderRepository.findAll();
        periodicHeaderList = periodicHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return convertToEntity(periodicHeaderList);
    }

    /**
     * getPeriodicHeader
     *
     * @param cycleCountTypeId
     * @return
     */
    public PeriodicHeader getPeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo) {
        PeriodicHeader periodicHeader =
                periodicHeaderRepository.findByCompanyCodeAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
                        getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo);
        if (periodicHeader != null && periodicHeader.getDeletionIndicator() == 0) {
            return periodicHeader;
        }
        throw new BadRequestException("The given PeriodicHeader ID : " + cycleCountTypeId + " doesn't exist.");
    }

    /**
     * @param searchPeriodicHeader
     * @return
     * @throws Exception
     */
    public List<PeriodicHeaderEntity> findPeriodicHeader(SearchPeriodicHeader searchPeriodicHeader)
            throws Exception {
        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
                    searchPeriodicHeader.getEndCreatedOn());
            searchPeriodicHeader.setStartCreatedOn(dates[0]);
            searchPeriodicHeader.setEndCreatedOn(dates[1]);
        }
        PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(searchPeriodicHeader);
        List<PeriodicHeader> periodicHeaderResults = periodicHeaderRepository.findAll(spec);
        List<PeriodicHeaderEntity> periodicHeaderEntityList = convertToEntity(periodicHeaderResults, searchPeriodicHeader);
        return periodicHeaderEntityList;
    }

    /**
     * @param searchPeriodicHeader - Stream
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<PeriodicHeader> findPeriodicHeaderStream(SearchPeriodicHeader searchPeriodicHeader)
            throws Exception {
        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
                    searchPeriodicHeader.getEndCreatedOn());
            searchPeriodicHeader.setStartCreatedOn(dates[0]);
            searchPeriodicHeader.setEndCreatedOn(dates[1]);
        }
        PeriodicHeaderSpecification spec = new PeriodicHeaderSpecification(searchPeriodicHeader);
        Stream<PeriodicHeader> periodicHeaderResults = periodicHeaderRepository.stream(spec, PeriodicHeader.class);
        return periodicHeaderResults;
    }

    /**
     * @param periodicheaderList
     * @return
     */
    private List<PeriodicHeaderEntity> convertToEntity(List<PeriodicHeader> periodicheaderList, SearchPeriodicHeader searchPeriodicHeader) {
        List<PeriodicHeaderEntity> listPeriodicHeaderEntity = new ArrayList<>();
        for (PeriodicHeader periodicheader : periodicheaderList) {
            SearchPeriodicLine searchPeriodicLine = new SearchPeriodicLine();
            searchPeriodicLine.setCycleCountNo(periodicheader.getCycleCountNo());

            if (searchPeriodicHeader.getCycleCounterId() != null) {
                searchPeriodicLine.setCycleCounterId(searchPeriodicHeader.getCycleCounterId());
            }

            if (searchPeriodicHeader.getLineStatusId() != null) {
                searchPeriodicLine.setLineStatusId(searchPeriodicHeader.getLineStatusId());
            }

            PeriodicLineSpecification spec = new PeriodicLineSpecification(searchPeriodicLine);
            List<PeriodicLine> periodicLineList = periodicLineRepository.findAll(spec);

            List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
            for (PeriodicLine periodicLine : periodicLineList) {
                PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
                BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
                listPeriodicLineEntity.add(perpetualLineEntity);
            }

            PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
            BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
            periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
            listPeriodicHeaderEntity.add(periodicheaderEntity);
        }
        return listPeriodicHeaderEntity;
    }

    /**
     * @param periodicheaderList
     * @return
     */
    private List<PeriodicHeaderEntity> convertToEntity(List<PeriodicHeader> periodicheaderList) {
        List<PeriodicHeaderEntity> listPeriodicHeaderEntity = new ArrayList<>();
        for (PeriodicHeader periodicheader : periodicheaderList) {
            List<PeriodicLine> periodicLineList = periodicLineService.getPeriodicLine(periodicheader.getCycleCountNo());
            List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
            for (PeriodicLine periodicLine : periodicLineList) {
                PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
                BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
                listPeriodicLineEntity.add(perpetualLineEntity);
            }

            PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
            BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
            periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
            listPeriodicHeaderEntity.add(periodicheaderEntity);
        }
        return listPeriodicHeaderEntity;
    }

    /**
     * @param periodicheader
     * @return
     */
    private PeriodicHeaderEntity convertToEntity(PeriodicHeader periodicheader) {
        List<PeriodicLine> perpetualLineList = periodicLineService.getPeriodicLine(periodicheader.getCycleCountNo());

        List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
        for (PeriodicLine periodicLine : perpetualLineList) {
            PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
            BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
            listPeriodicLineEntity.add(perpetualLineEntity);
        }

        PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
        BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
        periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
        return periodicheaderEntity;
    }

    /**
     * @param warehouseId
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    public Page<PeriodicLineEntity> runPeriodicHeader(String warehouseId, Integer pageNo,
                                                      Integer pageSize, String sortBy) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            Page<Inventory> inventoryList = inventoryRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L, pageable);
            log.info("inventoryList--size----> : " + inventoryList.getTotalElements());

            List<PeriodicLineEntity> periodicLineList = new ArrayList<>();
            for (Inventory inventory : inventoryList) {
                PeriodicLineEntity periodicLine = new PeriodicLineEntity();

                periodicLine.setLanguageId(inventory.getLanguageId());
                periodicLine.setCompanyCode(inventory.getCompanyCodeId());
                periodicLine.setPlantId(inventory.getPlantId());
                periodicLine.setWarehouseId(inventory.getWarehouseId());

                // ITM_CODE
                periodicLine.setItemCode(inventory.getItemCode());

                // Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
                List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
//				log.info("imbasicdata1 : " + imbasicdata1);
                periodicLine.setItemDesc(imbasicdata1.get(0).getDescription());

                // ST_BIN
                periodicLine.setStorageBin(inventory.getStorageBin());

                // ST_SEC_ID/ST_SEC
                // Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
                periodicLine.setStorageSectionId(storageBinRepository.findByStorageBin(inventory.getStorageBin()));

                // MFR_PART
                // Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values
                periodicLine.setManufacturerPartNo(imbasicdata1.get(0).getManufacturePart());

                // STCK_TYP_ID
                periodicLine.setStockTypeId(inventory.getStockTypeId());

                // SP_ST_IND_ID
                periodicLine.setSpecialStockIndicator(inventory.getSpecialStockIndicatorId());

                // PACK_BARCODE
                periodicLine.setPackBarcodes(inventory.getPackBarcodes());

                /*
                 * INV_QTY
                 * -------------
                 * Pass the filled WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN
                 * values in INVENTORY table and fetch INV_QTY/INV_UOM values and
                 * fill against each ITM_CODE values and this is non-editable"
                 */
                IInventory dbInventory = inventoryRepository.findInventoryForPeriodicRun(inventory.getWarehouseId(),
                        inventory.getPackBarcodes(), inventory.getItemCode(), inventory.getStorageBin());

                if (dbInventory != null) {
                    periodicLine.setInventoryQuantity(inventory.getInventoryQuantity());
                    periodicLine.setInventoryUom(inventory.getInventoryUom());
                }
                periodicLineList.add(periodicLine);
            }
            final Page<PeriodicLineEntity> page = new PageImpl<>(periodicLineList, pageable, inventoryList.getTotalElements());
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param list
     * @return
     */
    public static List[] split(List<String> list) {
        int size = list.size();
        List<String> first = new ArrayList<>(list.subList(0, (size) / 2));
        List<String> second = new ArrayList<>(list.subList((size) / 2, size));
        return new List[]{first, second};
    }

    /**
     * createPeriodicHeader
     *
     * @param newPeriodicHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeaderEntity createPeriodicHeader(AddPeriodicHeader newPeriodicHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            PeriodicHeader dbPeriodicHeader = new PeriodicHeader();
            BeanUtils.copyProperties(newPeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(newPeriodicHeader));
            dbPeriodicHeader.setLanguageId(getLanguageId());
            dbPeriodicHeader.setCompanyCode(getCompanyCode());
            dbPeriodicHeader.setPlantId(getPlantId());

            /*
             * Cycle Count No
             * --------------------
             * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID=15 values in NUMBERRANGE table and fetch NUM_RAN_CURRENT value
             * and add +1 and then update in PERIODICHEADER table during Save
             */
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_ID = 15;
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, newPeriodicHeader.getWarehouseId(),
                    authTokenForIDMasterService.getAccess_token());
            dbPeriodicHeader.setCycleCountNo(nextRangeNumber);

            // CC_TYP_ID - HardCoded Value "01"
            dbPeriodicHeader.setCycleCountTypeId(1L);

            // STATUS_ID - HardCoded Value "70"
            dbPeriodicHeader.setStatusId(70L);
            dbPeriodicHeader.setDeletionIndicator(0L);
            dbPeriodicHeader.setCreatedBy(loginUserID);
            dbPeriodicHeader.setCreatedOn(new Date());
            dbPeriodicHeader.setConfirmedBy(loginUserID);
            dbPeriodicHeader.setConfirmedOn(new Date());
            PeriodicHeader createdPeriodicHeader = periodicHeaderRepository.save(dbPeriodicHeader);
            List<PeriodicLine> periodicLineList = new ArrayList<>();
            log.info("newPeriodicHeader.getPeriodicLine() : " + newPeriodicHeader.getPeriodicLine());

            /*
             * Checking whether PeriodicLine has Status_ID 70
             */
            Set<String> setItemCodes = periodicLineList.stream().map(PeriodicLine::getItemCode).collect(Collectors.toSet());
//			List<String> listItemCodes = periodicLineRepository.findByStatusIdNotIn70(setItemCodes);
//			if (listItemCodes != null && listItemCodes.isEmpty()) {
//				throw new BadRequestException("Selected Items are already in Stock Count process.");
//			}

            for (PeriodicLine newPeriodicLine : newPeriodicHeader.getPeriodicLine()) {
//				if (listItemCodes.contains(newPeriodicLine.getItemCode())) {
                PeriodicLine dbPeriodicLine = new PeriodicLine();
                BeanUtils.copyProperties(newPeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(newPeriodicLine));

                // LANG_ID
                dbPeriodicLine.setLanguageId(getLanguageId());

                // WH_ID
                dbPeriodicLine.setWarehouseId(createdPeriodicHeader.getWarehouseId());

                // C_ID
                dbPeriodicLine.setCompanyCode(createdPeriodicHeader.getCompanyCode());

                // PLANT_ID
                dbPeriodicLine.setPlantId(createdPeriodicHeader.getPlantId());

                // CC_NO
                dbPeriodicLine.setCycleCountNo(createdPeriodicHeader.getCycleCountNo());
                dbPeriodicLine.setStatusId(70L);
                dbPeriodicLine.setDeletionIndicator(0L);
                dbPeriodicLine.setCreatedBy(loginUserID);
                dbPeriodicLine.setCreatedOn(new Date());
                //				PeriodicLine createdPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
                //				log.info("createdPeriodicLine : " + createdPeriodicLine);
                //				periodicLineList.add(createdPeriodicLine);
                periodicLineList.add(dbPeriodicLine);
//				}
            }

            // Batch Insert
//			List<PeriodicLine> createdPeriodicLine = periodicLineRepository.saveAll(periodicLineList);
            batchInsert(periodicLineList);

//			log.info("createdPeriodicLines : " + createdPeriodicLine.size());
            log.info("createdPeriodicLines : " + periodicLineList.size());

            PeriodicHeaderEntity periodicheaderEntity = new PeriodicHeaderEntity();
            BeanUtils.copyProperties(createdPeriodicHeader, periodicheaderEntity, CommonUtils.getNullPropertyNames(createdPeriodicHeader));
//
//			List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
//			for (PeriodicLine periodicLine : periodicLineList) {
//				PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
//				BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
//				listPeriodicLineEntity.add(perpetualLineEntity);
//			}
//
//			periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
            return periodicheaderEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param cycleCountTypeId
     * @param cycleCountNo
     * @param loginUserID
     * @param updatePeriodicHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeader updatePeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo, String loginUserID,
                                               UpdatePeriodicHeader updatePeriodicHeader) throws IllegalAccessException, InvocationTargetException {
        // Update Line Details
        List<PeriodicLine> lines = periodicLineService.updatePeriodicLineForMobileCount(updatePeriodicHeader.getUpdatePeriodicLine(), loginUserID);
        log.info("Lines Updated : " + lines);

        PeriodicHeader dbPeriodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
        BeanUtils.copyProperties(updatePeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(updatePeriodicHeader));

        /*
         * Pass CC_NO in PERPETUALLINE table and validate STATUS_ID of the selected records.
         * 1. If STATUS_ID=78 for all the selected records, update STATUS_ID of periodicheader table as "78" by passing CC_NO
         * 2. If STATUS_ID=74 for all the selected records, Update STATUS_ID of periodicheader table as "74" by passing CC_NO
         * Else Update STATUS_ID as "73"
         */
        List<PeriodicLine> PeriodicLines = periodicLineService.getPeriodicLine(cycleCountNo);
        long count_78 = PeriodicLines.stream().filter(a -> a.getStatusId() == 78L).count();
        long count_74 = PeriodicLines.stream().filter(a -> a.getStatusId() == 74L).count();

        if (PeriodicLines.size() == count_78) {
            dbPeriodicHeader.setStatusId(78L);
        } else if (PeriodicLines.size() == count_74) {
            dbPeriodicHeader.setStatusId(74L);
        } else {
            dbPeriodicHeader.setStatusId(73L);
        }

        dbPeriodicHeader.setCountedBy(loginUserID);
        dbPeriodicHeader.setCountedOn(new Date());
        return periodicHeaderRepository.save(dbPeriodicHeader);
    }

    /**
     * @param warehouseId
     * @param cycleCountTypeId
     * @param cycleCountNo
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeader updatePeriodicHeaderFromPeriodicLine(String warehouseId, Long cycleCountTypeId, String cycleCountNo, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        // Update Line Details
//		List<PeriodicLine> lines = periodicLineService.updatePeriodicLineForMobileCount (updatePeriodicHeader.getUpdatePeriodicLine(), loginUserID);
//		log.info("Lines Updated : " + lines);

        PeriodicHeader dbPeriodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
//		BeanUtils.copyProperties(updatePeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(updatePeriodicHeader));

        /*
         * Pass CC_NO in PERPETUALLINE table and validate STATUS_ID of the selected records.
         * 1. If STATUS_ID=78 for all the selected records, update STATUS_ID of periodicheader table as "78" by passing CC_NO
         * 2. If STATUS_ID=74 for all the selected records, Update STATUS_ID of periodicheader table as "74" by passing CC_NO
         * Else Update STATUS_ID as "73"
         */
        List<PeriodicLine> PeriodicLines = periodicLineService.getPeriodicLine(cycleCountNo);
        long count_78 = PeriodicLines.stream().filter(a -> a.getStatusId() == 78L).count();
        long count_74 = PeriodicLines.stream().filter(a -> a.getStatusId() == 74L).count();

        if (PeriodicLines.size() == count_78) {
            dbPeriodicHeader.setStatusId(78L);
        } else if (PeriodicLines.size() == count_74) {
            dbPeriodicHeader.setStatusId(74L);
        } else {
            dbPeriodicHeader.setStatusId(73L);
        }

        dbPeriodicHeader.setCountedBy(loginUserID);
        dbPeriodicHeader.setCountedOn(new Date());
        return periodicHeaderRepository.save(dbPeriodicHeader);
    }

    /**
     * deletePeriodicHeader
     *
     * @param loginUserID
     * @param cycleCountTypeId
     */
    public void deletePeriodicHeader(String companyCode, String plantId, String warehouseId, Long cycleCountTypeId,
                                     String cycleCountNo, String loginUserID) {
        PeriodicHeader periodicHeader = getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
        if (periodicHeader != null) {
            periodicHeader.setDeletionIndicator(1L);
            periodicHeader.setConfirmedBy(loginUserID);
            periodicHeader.setConfirmedOn(new Date());
            periodicHeaderRepository.save(periodicHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + cycleCountTypeId);
        }
    }

    /**
     * @param cycleCountNo
     * @return
     */
    public PeriodicHeader getPeriodicHeader(String cycleCountNo) {
        PeriodicHeader periodicHeader = periodicHeaderRepository.findByCycleCountNo(cycleCountNo);
        return periodicHeader;
    }

    public int[][] batchInsert(List<PeriodicLine> periodicLineList) {

        int batchSize = 500;

        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "insert into tblperiodicline (LANG_ID, C_ID, PLANT_ID, WH_ID,\n" +
                        "CC_NO, ST_BIN, ITM_CODE, PACK_BARCODE, \n" +
                        "VAR_ID, VAR_SUB_ID, \n" +
                        "STR_NO, STCK_TYP_ID, SP_ST_IND_ID, \n" +
                        "INV_QTY, INV_UOM, CTD_QTY, VAR_QTY, \n" +
                        "COUNTER_ID, COUNTER_NM, STATUS_ID, ACTION, \n" +
                        "REF_NO, APP_PROCESS_ID, APP_LVL, APP_CODE, \n" +
                        "APP_STATUS, REMARK, REF_FIELD_1, REF_FIELD_2, \n" +
                        "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, \n" +
                        "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, \n" +
                        "IS_DELETED, CC_CTD_BY, CC_CTD_ON, CC_CNF_BY, \n" +
                        "CC_CNF_ON, CC_CNT_BY, CC_CNT_ON) \n" +
                        "values(?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?, \n" +
                        "?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?)", periodicLineList, batchSize,
                new ParameterizedPreparedStatementSetter<PeriodicLine>() {

                    public void setValues(PreparedStatement ps, PeriodicLine periodicLine) throws SQLException {
                        ps.setString(1, periodicLine.getLanguageId());
                        ps.setString(2, periodicLine.getCompanyCode());
                        ps.setString(3, periodicLine.getPlantId());
                        ps.setString(4, periodicLine.getWarehouseId());
                        ps.setString(5, periodicLine.getCycleCountNo());
                        ps.setString(6, periodicLine.getStorageBin());
                        ps.setString(7, periodicLine.getItemCode());
                        ps.setString(8, periodicLine.getPackBarcodes());

                        if (periodicLine.getVariantCode() != null) {
                            ps.setLong(9, periodicLine.getVariantCode());
                        } else {
                            ps.setLong(9, 0L);
                        }

                        ps.setString(10, periodicLine.getVariantSubCode());
                        ps.setString(11, periodicLine.getBatchSerialNumber());

                        if (periodicLine.getStockTypeId() != null) {
                            ps.setLong(12, periodicLine.getStockTypeId());
                        } else {
                            ps.setLong(12, 0L);
                        }

                        ps.setString(13, periodicLine.getSpecialStockIndicator());

                        if (periodicLine.getInventoryQuantity() != null) {
                            ps.setDouble(14, periodicLine.getInventoryQuantity());
                        } else {
                            ps.setDouble(14, 0D);
                        }

                        ps.setString(15, periodicLine.getInventoryUom());

                        if (periodicLine.getCountedQty() != null) {
                            ps.setDouble(16, periodicLine.getCountedQty());
                        } else {
                            ps.setDouble(16, 0D);
                        }

                        if (periodicLine.getVarianceQty() != null) {
                            ps.setDouble(17, periodicLine.getVarianceQty());
                        } else {
                            ps.setDouble(17, 0D);
                        }

                        ps.setString(18, periodicLine.getCycleCounterId());
                        ps.setString(19, periodicLine.getCycleCounterName());
                        if (periodicLine.getStatusId() != null) {
                            ps.setLong(20, periodicLine.getStatusId());
                        } else {
                            ps.setLong(20, 0L);
                        }

                        ps.setString(21, periodicLine.getCycleCountAction());
                        ps.setString(22, periodicLine.getReferenceNo());
                        if (periodicLine.getApprovalProcessId() != null) {
                            ps.setLong(23, periodicLine.getApprovalProcessId());
                        } else {
                            ps.setLong(23, 0L);
                        }

                        ps.setString(24, periodicLine.getApprovalLevel());
                        ps.setString(25, periodicLine.getApproverCode());
                        ps.setString(26, periodicLine.getApprovalStatus());
                        ps.setString(27, periodicLine.getRemarks());
                        ps.setString(28, periodicLine.getReferenceField1());
                        ps.setString(29, periodicLine.getReferenceField2());
                        ps.setString(30, periodicLine.getReferenceField3());
                        ps.setString(31, periodicLine.getReferenceField4());
                        ps.setString(32, periodicLine.getReferenceField5());
                        ps.setString(33, periodicLine.getReferenceField6());
                        ps.setString(34, periodicLine.getReferenceField7());
                        ps.setString(35, periodicLine.getReferenceField8());
                        ps.setString(36, periodicLine.getReferenceField9());
                        ps.setString(37, periodicLine.getReferenceField10());

                        if (periodicLine.getDeletionIndicator() != null) {
                            ps.setLong(38, periodicLine.getDeletionIndicator());
                        } else {
                            ps.setLong(38, 0L);
                        }

                        ps.setString(39, periodicLine.getCreatedBy());

                        if (periodicLine.getCreatedOn() != null) {
                            ps.setDate(40, new java.sql.Date(periodicLine.getCreatedOn().getTime()));
                        } else {
                            ps.setDate(40, new java.sql.Date(new Date().getTime()));
                        }

                        ps.setString(41, periodicLine.getConfirmedBy());

                        if (periodicLine.getConfirmedOn() != null) {
                            ps.setDate(42, new java.sql.Date(periodicLine.getConfirmedOn().getTime()));
                        } else {
                            ps.setDate(42, new java.sql.Date(new Date().getTime()));
                        }

                        ps.setString(43, periodicLine.getCountedBy());

                        if (periodicLine.getCountedOn() != null) {
                            ps.setDate(44, new java.sql.Date(periodicLine.getCountedOn().getTime()));
                        } else {
                            ps.setDate(44, new java.sql.Date(new Date().getTime()));
                        }
                    }
                });
        return updateCounts;
    }

    //=========================================================================V2================================================================

    public List<PeriodicHeaderEntityV2> getPeriodicHeadersV2() {
        List<PeriodicHeaderV2> periodicHeaderList = periodicHeaderV2Repository.findAll();
        periodicHeaderList = periodicHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return convertToEntityV2(periodicHeaderList);
    }

    /**
     * getPeriodicHeader
     *
     * @param cycleCountTypeId
     * @return
     */
    public PeriodicHeaderV2 getPeriodicHeaderV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, Long cycleCountTypeId, String cycleCountNo) {
        PeriodicHeaderV2 periodicHeader =
                periodicHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
                        companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);
        if (periodicHeader != null && periodicHeader.getDeletionIndicator() == 0) {
            return periodicHeader;
        }
        throw new BadRequestException("The given PeriodicHeader ID : " + cycleCountTypeId + " doesn't exist.");
    }

    /**
     * @param searchPeriodicHeader - Stream
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<PeriodicHeaderV2> findPeriodicHeaderStreamV2(SearchPeriodicHeaderV2 searchPeriodicHeader)
            throws Exception {
        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
                    searchPeriodicHeader.getEndCreatedOn());
            searchPeriodicHeader.setStartCreatedOn(dates[0]);
            searchPeriodicHeader.setEndCreatedOn(dates[1]);
        }
        PeriodicHeaderV2Specification spec = new PeriodicHeaderV2Specification(searchPeriodicHeader);
        Stream<PeriodicHeaderV2> periodicHeaderResults = periodicHeaderV2Repository.stream(spec, PeriodicHeaderV2.class);
        return periodicHeaderResults;
    }


    /**
     * @param searchPeriodicHeaderV2
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<PeriodicHeaderV2> findPeriodicHeaderV2(SearchPeriodicHeaderV2 searchPeriodicHeaderV2)
            throws ParseException, java.text.ParseException {
        if (searchPeriodicHeaderV2.getStartCreatedOn() != null && searchPeriodicHeaderV2.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeaderV2.getStartCreatedOn(),
                    searchPeriodicHeaderV2.getEndCreatedOn());
            searchPeriodicHeaderV2.setStartCreatedOn(dates[0]);
            searchPeriodicHeaderV2.setEndCreatedOn(dates[1]);
        }

        PeriodicHeaderV2Specification spec = new PeriodicHeaderV2Specification(searchPeriodicHeaderV2);
        Stream<PeriodicHeaderV2> periodicHeaderV2s = periodicHeaderV2Repository.stream(spec, PeriodicHeaderV2.class);
        return periodicHeaderV2s;
    }

    public List<PeriodicHeaderEntityV2> findPeriodicHeaderEntityV2(SearchPeriodicHeaderV2 searchPeriodicHeaderV2)
            throws ParseException, java.text.ParseException {
        if (searchPeriodicHeaderV2.getStartCreatedOn() != null && searchPeriodicHeaderV2.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeaderV2.getStartCreatedOn(),
                    searchPeriodicHeaderV2.getEndCreatedOn());
            searchPeriodicHeaderV2.setStartCreatedOn(dates[0]);
            searchPeriodicHeaderV2.setEndCreatedOn(dates[1]);
        }

        log.info("searchPeriodicHeader: headerStatus " + searchPeriodicHeaderV2.getHeaderStatusId());
        log.info("searchPeriodicHeader: line status" + searchPeriodicHeaderV2.getLineStatusId());
        log.info("searchPeriodicHeader: " + searchPeriodicHeaderV2);

        PeriodicHeaderV2Specification spec = new PeriodicHeaderV2Specification(searchPeriodicHeaderV2);
        List<PeriodicHeaderV2> periodicHeaderV2s = periodicHeaderV2Repository.stream(spec, PeriodicHeaderV2.class).collect(Collectors.toList());
        log.info("searchPeriodicHeader results: " + periodicHeaderV2s);

        List<PeriodicHeaderEntityV2> results = convertToEntityForFindV2(periodicHeaderV2s, searchPeriodicHeaderV2);
        return results;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param cycleCountTypeId
     * @param cycleCountNo
     * @return
     */
    public PeriodicHeaderEntityV2 getPeriodicHeaderWithLineV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                              Long cycleCountTypeId, String cycleCountNo) {
        Optional<PeriodicHeaderV2> optPeriodicHeader =
                periodicHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, 0L);

        if (optPeriodicHeader.isEmpty()) {
            throw new BadRequestException("The given PeriodicHeader ID : " + cycleCountNo
                    + "cycleCountTypeId: " + cycleCountTypeId + ","
                    + "companyCode: " + companyCode + ","
                    + "plantId: " + plantId
                    + " doesn't exist.");
        }

        PeriodicHeaderV2 periodicHeaderV2 = optPeriodicHeader.get();
        return convertToEntityV2(periodicHeaderV2);
    }

    /**
     * @param periodicheaderList
     * @return
     */
    private List<PeriodicHeaderEntityV2> convertToEntityV2(List<PeriodicHeaderV2> periodicheaderList) {
        List<PeriodicHeaderEntityV2> listPeriodicHeaderEntity = new ArrayList<>();
        for (PeriodicHeaderV2 periodicheader : periodicheaderList) {
            List<PeriodicLineV2> periodicLineList = periodicLineService.getPeriodicLineV2(
                    periodicheader.getCompanyCode(),
                    periodicheader.getPlantId(),
                    periodicheader.getLanguageId(),
                    periodicheader.getWarehouseId(),
                    periodicheader.getCycleCountNo());
            List<PeriodicLineV2> listPeriodicLineEntity = new ArrayList<>();
            for (PeriodicLineV2 periodicLine : periodicLineList) {
                PeriodicLineV2 perpetualLineEntity = new PeriodicLineV2();
                BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
                listPeriodicLineEntity.add(perpetualLineEntity);
            }

            PeriodicHeaderEntityV2 periodicheaderEntity = new PeriodicHeaderEntityV2();
            BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
            periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
            listPeriodicHeaderEntity.add(periodicheaderEntity);
        }
        return listPeriodicHeaderEntity;
    }

    /**
     * @param periodicheaderList
     * @param searchPeriodicHeader
     * @return
     */
    private List<PeriodicHeaderEntityV2> convertToEntityForFindV2(List<PeriodicHeaderV2> periodicheaderList, SearchPeriodicHeaderV2 searchPeriodicHeader) {
        List<PeriodicHeaderEntityV2> listPeriodicHeaderEntity = new ArrayList<>();
        for (PeriodicHeaderV2 periodicheader : periodicheaderList) {
            log.info("Periodic Header: " + periodicheader);
            List<PeriodicLineV2> periodicLineList = periodicLineService.getPeriodicLineForFindV2(
                    periodicheader.getCompanyCode(),
                    periodicheader.getPlantId(),
                    periodicheader.getLanguageId(),
                    periodicheader.getWarehouseId(),
                    periodicheader.getCycleCountNo(),
                    searchPeriodicHeader.getLineStatusId());

            log.info("PeriodicLine result: " + periodicLineList);

            if (!periodicLineList.isEmpty()) {
                PeriodicHeaderEntityV2 periodicheaderEntity = new PeriodicHeaderEntityV2();
                BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
                periodicheaderEntity.setPeriodicLine(periodicLineList);
                listPeriodicHeaderEntity.add(periodicheaderEntity);
            }
        }
        return listPeriodicHeaderEntity;
    }

    /**
     * @param periodicheader
     * @return
     */
    private PeriodicHeaderEntityV2 convertToEntityV2(PeriodicHeaderV2 periodicheader) {
        List<PeriodicLineV2> perpetualLineList = periodicLineService.getPeriodicLineV2(
                periodicheader.getCompanyCode(),
                periodicheader.getPlantId(),
                periodicheader.getLanguageId(),
                periodicheader.getWarehouseId(),
                periodicheader.getCycleCountNo());
        List<PeriodicZeroStockLine> periodicZeroStockLineList = periodicZeroStkLineService.getPeriodicZeroStockLine(
                periodicheader.getCompanyCode(),
                periodicheader.getPlantId(),
                periodicheader.getLanguageId(),
                periodicheader.getWarehouseId(),
                periodicheader.getCycleCountNo());
        if(periodicZeroStockLineList != null && !periodicZeroStockLineList.isEmpty()){
            log.info("periodicZeroStockLineList : " + periodicZeroStockLineList.size());
            for(PeriodicZeroStockLine periodicZeroStockLine : periodicZeroStockLineList){
                PeriodicLineV2 dbPeriodicLineV2 = new PeriodicLineV2();
                BeanUtils.copyProperties(periodicZeroStockLine, dbPeriodicLineV2, CommonUtils.getNullPropertyNames(periodicZeroStockLine));
                perpetualLineList.add(dbPeriodicLineV2);
            }
        }

        PeriodicHeaderEntityV2 periodicHeaderEntityV2 = new PeriodicHeaderEntityV2();
        BeanUtils.copyProperties(periodicheader, periodicHeaderEntityV2, CommonUtils.getNullPropertyNames(periodicheader));
        periodicHeaderEntityV2.setPeriodicLine(perpetualLineList);
        return periodicHeaderEntityV2;
    }

    /**
     * @param warehouseId
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    public Page<PeriodicLineV2> runPeriodicHeaderV2(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, Integer pageNo, Integer pageSize, String sortBy) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            Page<InventoryV2> inventoryList = inventoryV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, 0L, pageable);
            log.info("inventoryList--size----> : " + inventoryList.getTotalElements());

            List<PeriodicLineV2> periodicLineList = new ArrayList<>();
            for (InventoryV2 inventory : inventoryList) {
                PeriodicLineV2 periodicLine = new PeriodicLineV2();

                periodicLine.setLanguageId(inventory.getLanguageId());
                periodicLine.setCompanyCode(inventory.getCompanyCodeId());
                periodicLine.setPlantId(inventory.getPlantId());
                periodicLine.setWarehouseId(inventory.getWarehouseId());

                // ITM_CODE
                periodicLine.setItemCode(inventory.getItemCode());

                // Pass ITM_CODE in IMBASICDATA table and fetch ITEM_TEXT values
//                List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());

                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(inventory.getCompanyCodeId());
                imBasicData.setPlantId(inventory.getPlantId());
                imBasicData.setLanguageId(inventory.getLanguageId());
                imBasicData.setWarehouseId(inventory.getWarehouseId());
                imBasicData.setItemCode(inventory.getItemCode());
                imBasicData.setManufacturerName(inventory.getManufacturerName());
                ImBasicData1 imbasicdata1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("imbasicdata1 : " + imbasicdata1);

                periodicLine.setItemDesc(imbasicdata1.getDescription());

                // ST_BIN
                periodicLine.setStorageBin(inventory.getStorageBin());

                // ST_SEC_ID/ST_SEC
                // Pass the ST_BIN in STORAGEBIN table and fetch ST_SEC_ID/ST_SEC values
                StorageBinV2 dbStorageBin = storageBinRepository.getStorageBin(companyCodeId, plantId, languageId, warehouseId, inventory.getStorageBin());
//                periodicLine.setStorageSectionId(storageBinRepository.findByStorageBin(inventory.getStorageBin()));
                if (dbStorageBin != null) {
                    periodicLine.setStorageSectionId(dbStorageBin.getStorageSectionId());
                }

                // MFR_PART
                // Pass ITM_CODE in IMBASICDATA table and fetch MFR_PART values
                periodicLine.setManufacturerPartNo(imbasicdata1.getManufacturerPartNo());

                // STCK_TYP_ID
                periodicLine.setStockTypeId(inventory.getStockTypeId());

                // SP_ST_IND_ID
                periodicLine.setSpecialStockIndicator(String.valueOf(inventory.getSpecialStockIndicatorId()));

                // PACK_BARCODE
                periodicLine.setPackBarcodes(inventory.getPackBarcodes());

                /*
                 * INV_QTY
                 * -------------
                 * Pass the filled WH_ID/ITM_CODE/PACK_BARCODE/ST_BIN
                 * values in INVENTORY table and fetch INV_QTY/INV_UOM values and
                 * fill against each ITM_CODE values and this is non-editable"
                 */
                IInventory dbInventory = inventoryV2Repository.findInventoryForPeriodicRunV2(
                        inventory.getCompanyCodeId(),
                        inventory.getPlantId(),
                        inventory.getLanguageId(),
                        inventory.getWarehouseId(),
                        inventory.getPackBarcodes(), inventory.getItemCode(),
                        inventory.getManufacturerName(), inventory.getStorageBin());

                if (dbInventory != null) {
                    periodicLine.setInventoryQuantity(inventory.getInventoryQuantity());
                    periodicLine.setInventoryUom(inventory.getInventoryUom());
                }
                periodicLineList.add(periodicLine);
            }
            final Page<PeriodicLineV2> page = new PageImpl<>(periodicLineList, pageable, inventoryList.getTotalElements());
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param list
     * @return
     */
    public static List[] splitV2(List<String> list) {
        int size = list.size();
        List<String> first = new ArrayList<>(list.subList(0, (size) / 2));
        List<String> second = new ArrayList<>(list.subList((size) / 2, size));
        return new List[]{first, second};
    }

    /**
     * createPeriodicHeader
     *
     * @param newPeriodicHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeaderEntityV2 createPeriodicHeaderV2(PeriodicHeaderEntityV2 newPeriodicHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            PeriodicHeaderV2 dbPeriodicHeader = new PeriodicHeaderV2();
            BeanUtils.copyProperties(newPeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(newPeriodicHeader));
            dbPeriodicHeader.setLanguageId(newPeriodicHeader.getLanguageId());
            dbPeriodicHeader.setCompanyCode(newPeriodicHeader.getCompanyCode());
            dbPeriodicHeader.setPlantId(newPeriodicHeader.getPlantId());

            /*
             * Cycle Count No
             * --------------------
             * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID=15 values in NUMBERRANGE table and fetch NUM_RAN_CURRENT value
             * and add +1 and then update in PERIODICHEADER table during Save
             */
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            long NUM_RAN_ID = 15;
            String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID,
                    newPeriodicHeader.getCompanyCode(),
                    newPeriodicHeader.getPlantId(),
                    newPeriodicHeader.getLanguageId(),
                    newPeriodicHeader.getWarehouseId(),
                    authTokenForIDMasterService.getAccess_token());
            dbPeriodicHeader.setCycleCountNo(nextRangeNumber);

            // CC_TYP_ID - HardCoded Value "01"
            dbPeriodicHeader.setCycleCountTypeId(1L);

            // STATUS_ID - HardCoded Value "70"
            dbPeriodicHeader.setStatusId(70L);
            dbPeriodicHeader.setDeletionIndicator(0L);
            dbPeriodicHeader.setCreatedBy(loginUserID);
            dbPeriodicHeader.setCreatedOn(new Date());
            dbPeriodicHeader.setConfirmedBy(loginUserID);
            dbPeriodicHeader.setConfirmedOn(new Date());
            PeriodicHeaderV2 createdPeriodicHeader = periodicHeaderV2Repository.save(dbPeriodicHeader);
            List<PeriodicLineV2> periodicLineList = new ArrayList<>();
            log.info("newPeriodicHeader.getPeriodicLine() : " + newPeriodicHeader.getPeriodicLine());

            /*
             * Checking whether PeriodicLine has Status_ID 70
             */
//            Set<String> setItemCodes = periodicLineList.stream().map(PeriodicLine::getItemCode).collect(Collectors.toSet());
//			List<String> listItemCodes = periodicLineRepository.findByStatusIdNotIn70(setItemCodes);
//			if (listItemCodes != null && listItemCodes.isEmpty()) {
//				throw new BadRequestException("Selected Items are already in Stock Count process.");
//			}

            for (PeriodicLineV2 newPeriodicLine : newPeriodicHeader.getPeriodicLine()) {
//				if (listItemCodes.contains(newPeriodicLine.getItemCode())) {
                PeriodicLineV2 dbPeriodicLine = new PeriodicLineV2();
                BeanUtils.copyProperties(newPeriodicLine, dbPeriodicLine, CommonUtils.getNullPropertyNames(newPeriodicLine));

                // LANG_ID
                dbPeriodicLine.setLanguageId(newPeriodicHeader.getLanguageId());

                // WH_ID
                dbPeriodicLine.setWarehouseId(createdPeriodicHeader.getWarehouseId());

                // C_ID
                dbPeriodicLine.setCompanyCode(createdPeriodicHeader.getCompanyCode());

                // PLANT_ID
                dbPeriodicLine.setPlantId(createdPeriodicHeader.getPlantId());

                // CC_NO
                dbPeriodicLine.setCycleCountNo(createdPeriodicHeader.getCycleCountNo());
                dbPeriodicLine.setStatusId(70L);
                dbPeriodicLine.setDeletionIndicator(0L);
                dbPeriodicLine.setCreatedBy(loginUserID);
                dbPeriodicLine.setCreatedOn(new Date());
                //				PeriodicLine createdPeriodicLine = periodicLineRepository.save(dbPeriodicLine);
                //				log.info("createdPeriodicLine : " + createdPeriodicLine);
                //				periodicLineList.add(createdPeriodicLine);
                periodicLineList.add(dbPeriodicLine);
//				}
            }

            // Batch Insert
            List<PeriodicLineV2> createdPeriodicLine = periodicLineV2Repository.saveAll(periodicLineList);
//            batchInsertV2(periodicLineList);

//			log.info("createdPeriodicLines : " + createdPeriodicLine.size());
            log.info("createdPeriodicLines : " + periodicLineList.size());

            PeriodicHeaderEntityV2 periodicheaderEntity = new PeriodicHeaderEntityV2();
            BeanUtils.copyProperties(createdPeriodicHeader, periodicheaderEntity, CommonUtils.getNullPropertyNames(createdPeriodicHeader));
//
//			List<PeriodicLineEntity> listPeriodicLineEntity = new ArrayList<>();
//			for (PeriodicLine periodicLine : periodicLineList) {
//				PeriodicLineEntity perpetualLineEntity = new PeriodicLineEntity();
//				BeanUtils.copyProperties(periodicLine, perpetualLineEntity, CommonUtils.getNullPropertyNames(periodicLine));
//				listPeriodicLineEntity.add(perpetualLineEntity);
//			}
//
//			periodicheaderEntity.setPeriodicLine(listPeriodicLineEntity);
            return periodicheaderEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param cycleCountTypeId
     * @param cycleCountNo
     * @param loginUserID
     * @param updatePeriodicHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeaderV2 updatePeriodicHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                   Long cycleCountTypeId, String cycleCountNo, String loginUserID,
                                                   PeriodicHeaderEntityV2 updatePeriodicHeader) throws IllegalAccessException, InvocationTargetException {
        // Update Line Details
        try {
            List<PeriodicLineV2> lines = periodicLineService.updatePeriodicLineForMobileCountV2(updatePeriodicHeader.getPeriodicLine(), loginUserID);
            log.info("Lines Updated : " + lines);

            PeriodicHeaderV2 dbPeriodicHeader =
                    getPeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);
            BeanUtils.copyProperties(updatePeriodicHeader, dbPeriodicHeader, CommonUtils.getNullPropertyNames(updatePeriodicHeader));

            /*
             * Pass CC_NO in PERPETUALLINE table and validate STATUS_ID of the selected records.
             * 1. If STATUS_ID=78 for all the selected records, update STATUS_ID of periodicheader table as "78" by passing CC_NO
             * 2. If STATUS_ID=74 for all the selected records, Update STATUS_ID of periodicheader table as "74" by passing CC_NO
             * Else Update STATUS_ID as "73"
             */
            List<PeriodicLineV2> PeriodicLines = periodicLineService.getPeriodicLineV2(companyCode, plantId, languageId, warehouseId, cycleCountNo);
            long count_78 = PeriodicLines.stream().filter(a -> a.getStatusId() == 78L).count();
            long count_74 = PeriodicLines.stream().filter(a -> a.getStatusId() == 74L).count();
            long count_47 = PeriodicLines.stream().filter(a -> a.getStatusId() == 47L).count();
            long totalCount78 = count_78 + count_47;
            long totalCount74 = count_74 + count_47;

            if (PeriodicLines.size() == totalCount78) {
                dbPeriodicHeader.setStatusId(78L);
            } else if (PeriodicLines.size() == totalCount74) {
                dbPeriodicHeader.setStatusId(74L);
            } else {
                dbPeriodicHeader.setStatusId(73L);
            }

            statusDescription = stagingLineV2Repository.getStatusDescription(dbPeriodicHeader.getStatusId(), languageId);
            dbPeriodicHeader.setStatusDescription(statusDescription);

            dbPeriodicHeader.setCountedBy(loginUserID);
            dbPeriodicHeader.setCountedOn(new Date());
            return periodicHeaderV2Repository.save(dbPeriodicHeader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param cycleCountTypeId
     * @param cycleCountNo
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PeriodicHeaderV2 updatePeriodicHeaderFromPeriodicLineV2(String companyCodeId, String plantId, String languageId,
                                                                   String warehouseId, Long cycleCountTypeId, String cycleCountNo, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        PeriodicHeaderV2 dbPeriodicHeader = getPeriodicHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);

        List<PeriodicLineV2> PeriodicLines = periodicLineService.getPeriodicLineV2(companyCodeId, plantId, languageId, warehouseId, cycleCountNo);
        long count_78 = PeriodicLines.stream().filter(a -> a.getStatusId() == 78L).count();
        long count_74 = PeriodicLines.stream().filter(a -> a.getStatusId() == 74L).count();
        long count_47 = PeriodicLines.stream().filter(a -> a.getStatusId() == 47L).count();
        long totalCount78 = count_78 + count_47;
        long totalCount74 = count_74 + count_47;

        if (PeriodicLines.size() == totalCount78) {
            dbPeriodicHeader.setStatusId(78L);
        } else if (PeriodicLines.size() == totalCount74) {
            dbPeriodicHeader.setStatusId(74L);
        } else {
            dbPeriodicHeader.setStatusId(73L);
        }

        statusDescription = stagingLineV2Repository.getStatusDescription(dbPeriodicHeader.getStatusId(), languageId);
        dbPeriodicHeader.setStatusDescription(statusDescription);

        dbPeriodicHeader.setCountedBy(loginUserID);
        dbPeriodicHeader.setCountedOn(new Date());
        return periodicHeaderV2Repository.save(dbPeriodicHeader);
    }

    /**
     * deletePeriodicHeader
     *
     * @param loginUserID
     * @param cycleCountTypeId
     */
    public void deletePeriodicHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, Long cycleCountTypeId,
                                       String cycleCountNo, String loginUserID) {
        PeriodicHeaderV2 periodicHeader = getPeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);
        if (periodicHeader != null) {
            periodicHeader.setDeletionIndicator(1L);
            periodicHeader.setConfirmedBy(loginUserID);
            periodicHeader.setConfirmedOn(new Date());
            periodicHeaderV2Repository.save(periodicHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + cycleCountTypeId);
        }
    }

    /**
     * @param cycleCountNo
     * @return
     */
    public PeriodicHeaderV2 getPeriodicHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String cycleCountNo) {
        PeriodicHeaderV2 periodicHeader = periodicHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNo(
                companyCode, plantId, languageId, warehouseId, cycleCountNo);
        return periodicHeader;
    }

    public int[][] batchInsertV2(List<PeriodicLineV2> periodicLineList) {

        int batchSize = 500;

        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "insert into tblperiodicline (LANG_ID, C_ID, PLANT_ID, WH_ID,\n" +
                        "CC_NO, ST_BIN, ITM_CODE, PACK_BARCODE, \n" +
                        "VAR_ID, VAR_SUB_ID, \n" +
                        "STR_NO, STCK_TYP_ID, SP_ST_IND_ID, \n" +
                        "INV_QTY, INV_UOM, CTD_QTY, VAR_QTY, \n" +
                        "COUNTER_ID, COUNTER_NM, STATUS_ID, ACTION, \n" +
                        "REF_NO, APP_PROCESS_ID, APP_LVL, APP_CODE, \n" +
                        "APP_STATUS, REMARK, REF_FIELD_1, REF_FIELD_2, \n" +
                        "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, \n" +
                        "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, \n" +
                        "IS_DELETED, CC_CTD_BY, CC_CTD_ON, CC_CNF_BY, \n" +
                        "CC_CNF_ON, CC_CNT_BY, CC_CNT_ON) \n" +
                        "values(?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?, \n" +
                        "?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?,?, \n" +
                        "?,?,?)", periodicLineList, batchSize,
                new ParameterizedPreparedStatementSetter<PeriodicLineV2>() {

                    public void setValues(PreparedStatement ps, PeriodicLineV2 periodicLine) throws SQLException {
                        ps.setString(1, periodicLine.getLanguageId());
                        ps.setString(2, periodicLine.getCompanyCode());
                        ps.setString(3, periodicLine.getPlantId());
                        ps.setString(4, periodicLine.getWarehouseId());
                        ps.setString(5, periodicLine.getCycleCountNo());
                        ps.setString(6, periodicLine.getStorageBin());
                        ps.setString(7, periodicLine.getItemCode());
                        ps.setString(8, periodicLine.getPackBarcodes());

                        if (periodicLine.getVariantCode() != null) {
                            ps.setLong(9, periodicLine.getVariantCode());
                        } else {
                            ps.setLong(9, 0L);
                        }

                        ps.setString(10, periodicLine.getVariantSubCode());
                        ps.setString(11, periodicLine.getBatchSerialNumber());

                        if (periodicLine.getStockTypeId() != null) {
                            ps.setLong(12, periodicLine.getStockTypeId());
                        } else {
                            ps.setLong(12, 0L);
                        }

                        ps.setString(13, String.valueOf(periodicLine.getSpecialStockIndicator()));

                        if (periodicLine.getInventoryQuantity() != null) {
                            ps.setDouble(14, periodicLine.getInventoryQuantity());
                        } else {
                            ps.setDouble(14, 0D);
                        }

                        ps.setString(15, periodicLine.getInventoryUom());

                        if (periodicLine.getCountedQty() != null) {
                            ps.setDouble(16, periodicLine.getCountedQty());
                        } else {
                            ps.setDouble(16, 0D);
                        }

                        if (periodicLine.getVarianceQty() != null) {
                            ps.setDouble(17, periodicLine.getVarianceQty());
                        } else {
                            ps.setDouble(17, 0D);
                        }

                        ps.setString(18, periodicLine.getCycleCounterId());
                        ps.setString(19, periodicLine.getCycleCounterName());
                        if (periodicLine.getStatusId() != null) {
                            ps.setLong(20, periodicLine.getStatusId());
                        } else {
                            ps.setLong(20, 0L);
                        }

                        ps.setString(21, periodicLine.getCycleCountAction());
                        ps.setString(22, periodicLine.getReferenceNo());
                        if (periodicLine.getApprovalProcessId() != null) {
                            ps.setLong(23, periodicLine.getApprovalProcessId());
                        } else {
                            ps.setLong(23, 0L);
                        }

                        ps.setString(24, periodicLine.getApprovalLevel());
                        ps.setString(25, periodicLine.getApproverCode());
                        ps.setString(26, periodicLine.getApprovalStatus());
                        ps.setString(27, periodicLine.getRemarks());
                        ps.setString(28, periodicLine.getReferenceField1());
                        ps.setString(29, periodicLine.getReferenceField2());
                        ps.setString(30, periodicLine.getReferenceField3());
                        ps.setString(31, periodicLine.getReferenceField4());
                        ps.setString(32, periodicLine.getReferenceField5());
                        ps.setString(33, periodicLine.getReferenceField6());
                        ps.setString(34, periodicLine.getReferenceField7());
                        ps.setString(35, periodicLine.getReferenceField8());
                        ps.setString(36, periodicLine.getReferenceField9());
                        ps.setString(37, periodicLine.getReferenceField10());

                        if (periodicLine.getDeletionIndicator() != null) {
                            ps.setLong(38, periodicLine.getDeletionIndicator());
                        } else {
                            ps.setLong(38, 0L);
                        }

                        ps.setString(39, periodicLine.getCreatedBy());

                        if (periodicLine.getCreatedOn() != null) {
                            ps.setDate(40, new java.sql.Date(periodicLine.getCreatedOn().getTime()));
                        } else {
                            ps.setDate(40, new java.sql.Date(new Date().getTime()));
                        }

                        ps.setString(41, periodicLine.getConfirmedBy());

                        if (periodicLine.getConfirmedOn() != null) {
                            ps.setDate(42, new java.sql.Date(periodicLine.getConfirmedOn().getTime()));
                        } else {
                            ps.setDate(42, new java.sql.Date(new Date().getTime()));
                        }

                        ps.setString(43, periodicLine.getCountedBy());

                        if (periodicLine.getCountedOn() != null) {
                            ps.setDate(44, new java.sql.Date(periodicLine.getCountedOn().getTime()));
                        } else {
                            ps.setDate(44, new java.sql.Date(new Date().getTime()));
                        }
                    }
                });
        return updateCounts;
    }

    /**
     * @param cycleCountHeader
     * @return
     */
    public PeriodicHeaderEntityV2 processStockCountReceived(CycleCountHeader cycleCountHeader) {

        PeriodicHeaderEntityV2 periodicHeaderEntityV2 = new PeriodicHeaderEntityV2();
        PeriodicHeaderV2 newPeriodicHeaderV2 = new PeriodicHeaderV2();
        List<PeriodicLineV2> periodicLineV2s = new ArrayList<>();
        List<PeriodicZeroStockLine> periodicZeroStockLines = new ArrayList<>();

        // Get Warehouse
        Optional<Warehouse> dbWarehouse =
                warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        cycleCountHeader.getCompanyCode(),
                        cycleCountHeader.getBranchCode(),
                        "EN",
                        0L
                );
        log.info("dbWarehouse : " + dbWarehouse);
        if (dbWarehouse == null || dbWarehouse.isEmpty()) {
            throw new BadRequestException("Warehouse can't be null");
        }

        newPeriodicHeaderV2.setCompanyCode(cycleCountHeader.getCompanyCode());
        newPeriodicHeaderV2.setPlantId(cycleCountHeader.getBranchCode());
        newPeriodicHeaderV2.setWarehouseId(dbWarehouse.get().getWarehouseId());
        newPeriodicHeaderV2.setLanguageId("EN");
        newPeriodicHeaderV2.setMiddlewareId(String.valueOf(cycleCountHeader.getMiddlewareId()));
        newPeriodicHeaderV2.setMiddlewareTable(cycleCountHeader.getMiddlewareTable());
        newPeriodicHeaderV2.setReferenceCycleCountNo(cycleCountHeader.getCycleCountNo());

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 14;
        String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID,
                newPeriodicHeaderV2.getCompanyCode(),
                newPeriodicHeaderV2.getPlantId(),
                newPeriodicHeaderV2.getLanguageId(),
                newPeriodicHeaderV2.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        newPeriodicHeaderV2.setCycleCountNo(nextRangeNumber);

        // CC_TYP_ID
        newPeriodicHeaderV2.setCycleCountTypeId(1L);

        // STATUS_ID - HardCoded Value "70"
        newPeriodicHeaderV2.setStatusId(70L);
        statusDescription = stagingLineV2Repository.getStatusDescription(70L, newPeriodicHeaderV2.getLanguageId());
        newPeriodicHeaderV2.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(newPeriodicHeaderV2.getCompanyCode(),
                newPeriodicHeaderV2.getLanguageId(),
                newPeriodicHeaderV2.getPlantId(),
                newPeriodicHeaderV2.getWarehouseId());
        newPeriodicHeaderV2.setCompanyDescription(description.getCompanyDesc());
        newPeriodicHeaderV2.setPlantDescription(description.getPlantDesc());
        newPeriodicHeaderV2.setWarehouseDescription(description.getWarehouseDesc());

        newPeriodicHeaderV2.setDeletionIndicator(0L);
        newPeriodicHeaderV2.setCreatedBy("MW_AMS");
        newPeriodicHeaderV2.setCountedBy("MW_AMS");
        newPeriodicHeaderV2.setCreatedOn(new Date());
        newPeriodicHeaderV2.setCountedOn(new Date());
        PeriodicHeaderV2 createdPeriodicHeaderV2 = periodicHeaderV2Repository.save(newPeriodicHeaderV2);
        log.info("createdPeriodicHeaderV2 : " + createdPeriodicHeaderV2);

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        for (CycleCountLine cycleCountLine : cycleCountHeader.getLines()) {

            List<IInventoryImpl> dbInventoryList = inventoryService.getInventoryForPerpetualCountV2(newPeriodicHeaderV2.getCompanyCode(),
                    newPeriodicHeaderV2.getPlantId(),
                    newPeriodicHeaderV2.getLanguageId(),
                    newPeriodicHeaderV2.getWarehouseId(),
                    cycleCountLine.getItemCode(),
                    cycleCountLine.getManufacturerName());

            if (dbInventoryList != null) {
                for (IInventoryImpl dbInventory : dbInventoryList) {

                    PeriodicLineV2 periodicLineV2 = new PeriodicLineV2();

                    StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                    storageBinPutAway.setCompanyCodeId(dbInventory.getCompanyCodeId());
                    storageBinPutAway.setPlantId(dbInventory.getPlantId());
                    storageBinPutAway.setLanguageId(dbInventory.getLanguageId());
                    storageBinPutAway.setWarehouseId(dbInventory.getWarehouseId());
                    storageBinPutAway.setBin(dbInventory.getStorageBin());

                    StorageBinV2 dbStorageBin = null;
                    try {
                        dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                    if (dbStorageBin != null) {
                        periodicLineV2.setStorageSectionId(dbStorageBin.getStorageSectionId());
                        periodicLineV2.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                        periodicLineV2.setVariantCode(dbInventory.getVariantCode());
                        periodicLineV2.setVariantSubCode(dbInventory.getVariantSubCode());
                    }

                    periodicLineV2.setCompanyCode(dbInventory.getCompanyCodeId());
                    periodicLineV2.setPlantId(dbInventory.getPlantId());
                    periodicLineV2.setWarehouseId(dbInventory.getWarehouseId());
                    periodicLineV2.setLanguageId(dbInventory.getLanguageId());
                    periodicLineV2.setItemCode(dbInventory.getItemCode());
                    periodicLineV2.setManufacturerPartNo(dbInventory.getManufacturerCode());
                    periodicLineV2.setManufacturerName(dbInventory.getManufacturerName());
                    periodicLineV2.setManufacturerCode(cycleCountLine.getManufacturerCode());
                    periodicLineV2.setLineNo(cycleCountLine.getLineOfEachItemCode());

                    periodicLineV2.setCycleCountNo(nextRangeNumber);
                    periodicLineV2.setReferenceNo(cycleCountLine.getCycleCountNo());
                    periodicLineV2.setStorageBin(dbInventory.getStorageBin());
                    periodicLineV2.setItemCode(dbInventory.getItemCode());
                    periodicLineV2.setItemDesc(dbInventory.getReferenceField8());
                    periodicLineV2.setPackBarcodes(dbInventory.getPackBarcodes());
                    periodicLineV2.setStockTypeId(dbInventory.getStockTypeId());
                    periodicLineV2.setSpecialStockIndicator(String.valueOf(dbInventory.getSpecialStockIndicatorId()));
//                    periodicLineV2.setInventoryQuantity(dbInventory.getInventoryQuantity());
                    periodicLineV2.setInventoryQuantity(dbInventory.getReferenceField4());                              //Total Qty
                    periodicLineV2.setInventoryUom(cycleCountLine.getUom());
                    periodicLineV2.setFrozenQty(cycleCountLine.getFrozenQty());

                    periodicLineV2.setStatusId(70L);
                    periodicLineV2.setStatusDescription(statusDescription);

                    periodicLineV2.setCompanyDescription(description.getCompanyDesc());
                    periodicLineV2.setPlantDescription(description.getPlantDesc());
                    periodicLineV2.setWarehouseDescription(description.getWarehouseDesc());

                    if (dbInventory.getBarcodeId() != null) {
                        periodicLineV2.setBarcodeId(dbInventory.getBarcodeId());
                    }

                    if (dbInventory.getBarcodeId() == null) {
                        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(dbInventory.getItemCode(),
                                dbInventory.getCompanyCodeId(),
                                dbInventory.getPlantId(),
                                dbInventory.getWarehouseId(),
                                dbInventory.getManufacturerName(),
                                dbInventory.getLanguageId());
                        log.info("Barcode : " + barcode);
                        if (barcode != null && !barcode.isEmpty()) {
                            periodicLineV2.setBarcodeId(barcode.get(0));
                        }
                    }

                    periodicLineV2.setDeletionIndicator(0L);
                    periodicLineV2.setCreatedBy("MW_AMS");
                    periodicLineV2.setCreatedOn(new Date());
                    periodicLineV2s.add(periodicLineV2);
                }
            }

            //Item Not present in Inventory ---> Lines Insert as Inv_qty '0'
            if(dbInventoryList == null){
                PeriodicZeroStockLine dbPeriodicLine = new PeriodicZeroStockLine();
                dbPeriodicLine.setCompanyCode(newPeriodicHeaderV2.getCompanyCode());
                dbPeriodicLine.setPlantId(newPeriodicHeaderV2.getPlantId());
                dbPeriodicLine.setWarehouseId(newPeriodicHeaderV2.getWarehouseId());
                dbPeriodicLine.setLanguageId(newPeriodicHeaderV2.getLanguageId());
                dbPeriodicLine.setItemCode(cycleCountLine.getItemCode());
                dbPeriodicLine.setManufacturerPartNo(cycleCountLine.getManufacturerName());
                dbPeriodicLine.setManufacturerName(cycleCountLine.getManufacturerName());
                dbPeriodicLine.setManufacturerCode(cycleCountLine.getManufacturerCode());
                dbPeriodicLine.setLineNo(cycleCountLine.getLineOfEachItemCode());

                dbPeriodicLine.setCycleCountNo(nextRangeNumber);
                dbPeriodicLine.setReferenceNo(cycleCountLine.getCycleCountNo());

                //Get Item Description
                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(newPeriodicHeaderV2.getCompanyCode());
                imBasicData.setPlantId(newPeriodicHeaderV2.getPlantId());
                imBasicData.setLanguageId(newPeriodicHeaderV2.getLanguageId());
                imBasicData.setWarehouseId(newPeriodicHeaderV2.getWarehouseId());
                imBasicData.setItemCode(cycleCountLine.getItemCode());
                imBasicData.setManufacturerName(cycleCountLine.getManufacturerName());
                ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 : " + imBasicData1);

                if(imBasicData1 != null) {
                    dbPeriodicLine.setItemDesc(imBasicData1.getDescription());
                }
                dbPeriodicLine.setInventoryQuantity(0D);                              //Total Qty
                dbPeriodicLine.setInventoryUom(cycleCountLine.getUom());
                dbPeriodicLine.setFrozenQty(cycleCountLine.getFrozenQty());

                dbPeriodicLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, newPeriodicHeaderV2.getLanguageId());
                dbPeriodicLine.setStatusDescription(statusDescription);

                dbPeriodicLine.setCompanyDescription(description.getCompanyDesc());
                dbPeriodicLine.setPlantDescription(description.getPlantDesc());
                dbPeriodicLine.setWarehouseDescription(description.getWarehouseDesc());

                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(cycleCountLine.getItemCode(),
                        newPeriodicHeaderV2.getCompanyCode(),
                        newPeriodicHeaderV2.getPlantId(),
                        newPeriodicHeaderV2.getWarehouseId(),
                        cycleCountLine.getManufacturerName(),
                        newPeriodicHeaderV2.getLanguageId());
                log.info("Barcode : " + barcode);
                if (barcode != null && !barcode.isEmpty()) {
                    dbPeriodicLine.setBarcodeId(barcode.get(0));
                }


                dbPeriodicLine.setDeletionIndicator(0L);
                dbPeriodicLine.setCreatedBy("MW_AMS");
                dbPeriodicLine.setCreatedOn(new Date());
                periodicZeroStockLines.add(dbPeriodicLine);
            }
        }

        List<PeriodicLineV2> createdPeriodicLine = periodicLineV2Repository.saveAll(periodicLineV2s);
        log.info("createdPeriodicLine: " + createdPeriodicLine);
        List<PeriodicZeroStockLine> createdPeriodicZeroStockLine = periodicZeroStkLineRepository.saveAll(periodicZeroStockLines);
        log.info("createdPeriodicZeroStockLine: " + createdPeriodicZeroStockLine);

        BeanUtils.copyProperties(createdPeriodicHeaderV2, periodicHeaderEntityV2, CommonUtils.getNullPropertyNames(createdPeriodicHeaderV2));
        periodicHeaderEntityV2.setPeriodicLine(periodicLineV2s);

        return periodicHeaderEntityV2;
    }

    /**
     * @param periodicheaderList
     * @param searchPeriodicHeader
     * @return
     */
    private List<PeriodicHeaderEntityV2> convertToEntityV2(List<PeriodicHeaderV2> periodicheaderList, SearchPeriodicHeaderV2 searchPeriodicHeader) {
        List<PeriodicHeaderEntityV2> listPeriodicHeaderEntity = new ArrayList<>();
        for (PeriodicHeaderV2 periodicheader : periodicheaderList) {
            SearchPeriodicLineV2 searchPeriodicLine = new SearchPeriodicLineV2();
            searchPeriodicLine.setCycleCountNo(Collections.singletonList(periodicheader.getCycleCountNo()));
            if (searchPeriodicHeader.getCompanyCodeId() != null) {
                searchPeriodicLine.setCompanyCode(searchPeriodicHeader.getCompanyCodeId());
            }
            if (searchPeriodicHeader.getPlantId() != null) {
                searchPeriodicLine.setPlantId(searchPeriodicHeader.getPlantId());
            }
            if (searchPeriodicHeader.getWarehouseId() != null) {
                searchPeriodicLine.setWarehouseId(searchPeriodicHeader.getWarehouseId());
            }

            if (searchPeriodicHeader.getCycleCounterId() != null) {
                searchPeriodicLine.setCycleCounterId(searchPeriodicHeader.getCycleCounterId());
            }

            if (searchPeriodicHeader.getLineStatusId() != null) {
                searchPeriodicLine.setLineStatusId(searchPeriodicHeader.getLineStatusId());
            }

            log.info("SearchPeriodicLine Input: " + searchPeriodicLine);
            PeriodicLineV2Specification spec = new PeriodicLineV2Specification(searchPeriodicLine);
            List<PeriodicLineV2> periodicLineList = periodicLineV2Repository.stream(spec, PeriodicLineV2.class).collect(Collectors.toList());
            log.info("periodicLine List: " + periodicLineList);

            if (!periodicLineList.isEmpty()) {
                PeriodicHeaderEntityV2 periodicheaderEntity = new PeriodicHeaderEntityV2();
                BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
                periodicheaderEntity.setPeriodicLine(periodicLineList);
                listPeriodicHeaderEntity.add(periodicheaderEntity);
            }

//            if(periodicLineList == null || periodicLineList.isEmpty()) {
//                return listPeriodicHeaderEntity;
//            }
//
//            PeriodicHeaderEntityV2 periodicheaderEntity = new PeriodicHeaderEntityV2();
//            BeanUtils.copyProperties(periodicheader, periodicheaderEntity, CommonUtils.getNullPropertyNames(periodicheader));
//            periodicheaderEntity.setPeriodicLine(periodicLineList);
//            listPeriodicHeaderEntity.add(periodicheaderEntity);
        }
        log.info("PeriodicHeader Find Results: " + listPeriodicHeaderEntity);
        return listPeriodicHeaderEntity;
    }

}
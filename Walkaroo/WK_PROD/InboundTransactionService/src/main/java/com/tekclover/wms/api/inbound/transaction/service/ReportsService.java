package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.TransactionReport;
import com.tekclover.wms.api.inbound.transaction.model.TransactionReportRes;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.inbound.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.inbound.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.inbound.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.SearchPutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;

import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.SearchPickupLineV2;
import com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2.OutboundHeader;
import com.tekclover.wms.api.inbound.transaction.model.report.*;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.StockMovementReportNewSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.StockReportOutputSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.TransactionHistoryReportSpecification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.partition;

@Slf4j
@Service
public class ReportsService extends BaseService {
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private PeriodicLineRepository periodicLineRepository;
    @Autowired
    private PerpetualLineRepository perpetualLineRepository;
    @Autowired
    private PickupHeaderRepository pickupHeaderRepository;
    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;
    @Autowired
    private GrHeaderRepository grHeaderRepository;
    @Autowired
    private QualityHeaderRepository qualityHeaderRepository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private PeriodicHeaderRepository periodicHeaderRepository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    InboundHeaderService inboundHeaderService;

    @Autowired
    InboundLineService inboundLineService;

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    MastersService mastersService;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    StagingHeaderService stagingHeaderService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    PickupHeaderService pickupHeaderService;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    StorageBinRepository storagebinRepository;

    @Autowired
    ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    OutboundLineRepository outboundLineRepository;

    @Autowired
    InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    WorkBookService workBookService;

    @Autowired
    ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    InboundLineRepository inboundLineRepository;

    @Autowired
    PerpetualLineService perpetualLineService;

    @Autowired
    PeriodicLineService periodicLineService;

    @Autowired
    TransactionHistoryReportRepository transactionHistoryReportRepository;

    @Autowired
    PickupLineRepository pickupLineRepository;

    @Autowired
    StockMovementReportRepository stockMovementReportRepository;

    @Autowired
    StockMovementReport1Repository stockMovementReport1Repository;

    @Autowired
    TransactionHistoryResultRepository transactionHistoryResultRepository;

    @Autowired
    GrHeaderService grHeaderService;

    @Autowired
    PeriodicHeaderService periodicHeaderService;

    @Autowired
    StockReportOutputRepository stockReportOutputRepository;

    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    DeliveryConfirmationRepository deliveryConfirmationRepository;


    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param itemCode
     * @param itemText
     * @param stockTypeText
     * @return
     */
    public List<StockReport> getAllStockReport(List<String> languageId, List<String> companyCodeId, List<String> plantId,
                                               List<String> warehouseId, List<String> itemCode, List<String> manufacturerName, String itemText, String stockTypeText) {

        if (languageId == null) {
            throw new BadRequestException("languageId can't be blank");
        }

        if (companyCodeId == null) {
            throw new BadRequestException("companyCodeId can't be blank");
        }

        if (plantId == null) {
            throw new BadRequestException("plantId can't be blank");
        }

        if (warehouseId == null) {
            throw new BadRequestException("WarehouseId can't be blank.");
        }

        if (stockTypeText == null) {
            throw new BadRequestException("StockTypeText can't be blank.");
        }

        if (itemCode != null && itemCode.isEmpty()) {
            itemCode = null;
        }
        if (manufacturerName != null && manufacturerName.isEmpty()) {
            manufacturerName = null;
        }

        if (itemText != null && itemText.trim().equals("")) {
            itemText = null;
        }

        List<StockReport> stockReportList = new ArrayList<>();
        List<StockReportImpl> reportList = inventoryV2Repository.getAllStockReportNew(
                languageId, companyCodeId, plantId,
                warehouseId,
                itemCode,
                itemText,
                manufacturerName,
                stockTypeText);

        reportList.forEach(data -> {
            StockReport stockReport = new StockReport();
            BeanUtils.copyProperties(data, stockReport);
            stockReportList.add(stockReport);
        });
        return stockReportList;
    }

    /**
     * @param searchStockReport
     * @return
     */
    public List<StockReportImpl> stockReport(SearchStockReport searchStockReport) {

        if (searchStockReport.getCompanyCodeId() == null || searchStockReport.getCompanyCodeId().isEmpty()) {
            searchStockReport.setCompanyCodeId(null);
        }

        if (searchStockReport.getPlantId() == null || searchStockReport.getPlantId().isEmpty()) {
            searchStockReport.setPlantId(null);
        }

        if (searchStockReport.getLanguageId() == null || searchStockReport.getLanguageId().isEmpty()) {
            searchStockReport.setLanguageId(null);
        }

        if (searchStockReport.getWarehouseId() == null || searchStockReport.getWarehouseId().isEmpty()) {
            searchStockReport.setWarehouseId(null);
        }

//        if (searchStockReport.getStockTypeText() == null) {
//            throw new BadRequestException("StockTypeText can't be blank.");
//        }

        if (searchStockReport.getItemCode() == null || searchStockReport.getItemCode().isEmpty()) {
            searchStockReport.setItemCode(null);
        }
        if (searchStockReport.getManufacturerName() == null || searchStockReport.getManufacturerName().isEmpty()) {
            searchStockReport.setManufacturerName(null);
        }

        if (searchStockReport.getItemText() == null || searchStockReport.getItemText().isEmpty()) {
            searchStockReport.setItemText(null);
        }

        List<StockReportImpl> reportList = inventoryV2Repository.stockReportNew(
                searchStockReport.getLanguageId(),
                searchStockReport.getCompanyCodeId(),
                searchStockReport.getPlantId(),
                searchStockReport.getWarehouseId(),
                searchStockReport.getItemCode(),
                searchStockReport.getItemText(),
                searchStockReport.getManufacturerName());
//                searchStockReport.getStockTypeText());

        return reportList;
    }

    public Stream<StockReportOutput> stockReportUsingStoredProcedure(SearchStockReportInput searchStockReport) {

        if (searchStockReport.getCompanyCodeId() == null) {
            throw new BadRequestException("Company Code Cannot be Null");
        }

        if (searchStockReport.getPlantId() == null) {
            throw new BadRequestException("Plant Cannot be Null");
        }

        if (searchStockReport.getLanguageId() == null) {
            throw new BadRequestException("Language Cannot be Null");
        }

        if (searchStockReport.getWarehouseId() == null) {
            throw new BadRequestException("warehouse Cannot be Null");
        }

        if (searchStockReport.getStockTypeText() == null || searchStockReport.getStockTypeText().equalsIgnoreCase("ALL")) {
            searchStockReport.setStockTypeText("0");
        }
        if (searchStockReport.getStockTypeText().equalsIgnoreCase("ONHAND")) {
            searchStockReport.setStockTypeText("1");
        }
        if (searchStockReport.getStockTypeText().equalsIgnoreCase("DAMAGED")) {
            searchStockReport.setStockTypeText("7");
        }
        if (searchStockReport.getItemCode() == null) {
            searchStockReport.setItemCode("0");
        }
        if (searchStockReport.getManufacturerName() == null) {
            searchStockReport.setManufacturerName("0");
        }

        if (searchStockReport.getItemText() == null) {
            searchStockReport.setItemText("0");
        }

        stockReportOutputRepository.updateSpStockReport(
                searchStockReport.getCompanyCodeId(),
                searchStockReport.getPlantId(),
                searchStockReport.getLanguageId(),
                searchStockReport.getWarehouseId(),
                searchStockReport.getItemCode(),
                searchStockReport.getManufacturerName(),
                searchStockReport.getItemText(),
                searchStockReport.getStockTypeText()
        );
        log.info("Report Generated successfully through Stored Procedure");
        StockReportOutputSpecification specification = new StockReportOutputSpecification();
        Stream<StockReportOutput> reportList = stockReportOutputRepository.stream(specification, StockReportOutput.class);
        log.info("Stock Report Output -----> ");
        return reportList;
    }

    /**
     * Inventory Report -------------------------
     *
     * @param warehouseId
     * @param itemCode
     * @param storageBin
     * @param stockTypeText
     * @param stSectionIds
     * @param sortBy
     * @param pageSize
     * @param pageNo
     * @param sortBy
     * @param pageSize
     * @param pageNo
     * @return
     */
    public Page<InventoryReport> getInventoryReport(List<String> warehouseId, List<String> itemCode, String storageBin,
                                                    String stockTypeText, List<String> stSectionIds, Integer pageNo, Integer pageSize, String sortBy) {
        try {
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

            if (warehouseId == null) {
                throw new BadRequestException("WarehouseId can't be blank.");
            }

            SearchInventory searchInventory = new SearchInventory();
            searchInventory.setWarehouseId(warehouseId);
            searchInventory.setItemCode(itemCode);
            searchInventory.setStorageBin(new ArrayList<>());
            /*
             * If ST_SEC_ID field value is entered in Search field, Pass ST_SEC_ID in
             * STORAGE_BIN table and fetch ST_BIN values and pass these values in INVENTORY
             * table to fetch the output values
             */
            if (stSectionIds != null) {
                StorageBin[] dbStorageBin = mastersService.getStorageBinBySectionId(warehouseId.get(0), stSectionIds,
                        authTokenForMastersService.getAccess_token());
                List<String> stBins = Arrays.asList(dbStorageBin).stream().map(StorageBin::getStorageBin)
                        .collect(Collectors.toList());
                searchInventory.setStorageBin(stBins);
            }

            if (storageBin != null && !storageBin.trim().equals("")) {
                searchInventory.getStorageBin().add(storageBin);
            }
            searchInventory.setStockTypeId(new ArrayList<>());
            if (stockTypeText.equals("ALL")) {
                searchInventory.getStockTypeId().add(1L);
                searchInventory.getStockTypeId().add(7L);
            } else if (stockTypeText.equals("HOLD")) {
                searchInventory.getStockTypeId().add(7L);
            } else if (stockTypeText.equals("ON HAND")) {
                searchInventory.getStockTypeId().add(1L);
            }

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
            Page<Inventory> inventoryList = inventoryService.findInventory(searchInventory, pageNo, pageSize, sortBy);
            List<InventoryReport> reportInventoryList = new ArrayList<>();
            for (Inventory dbInventory : inventoryList) {
                InventoryReport reportInventory = new InventoryReport();

                // WH_ID
                reportInventory.setWarehouseId(dbInventory.getWarehouseId());

                // ITM_CODE
                reportInventory.setItemCode(dbInventory.getItemCode());
                log.info("dbInventory.getItemCode() : " + dbInventory.getItemCode());

                /*
                 * ITEM_TEXT
                 *
                 * Pass the fetched ITM_CODE values in IMBASICDATA1 table and fetch MFR_SKU
                 * values
                 */
                ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(dbInventory.getItemCode(),
                        dbInventory.getWarehouseId(), authTokenForMastersService.getAccess_token());
                log.info("imBasicData1 : " + imBasicData1);

                if (imBasicData1 != null) {
                    reportInventory.setDescription(imBasicData1.getDescription());
                }

                // INV_UOM
                reportInventory.setUom(dbInventory.getInventoryUom());

                // ST_BIN
                reportInventory.setStorageBin(dbInventory.getStorageBin());
                log.info("dbInventory.getStorageBin() : " + dbInventory.getStorageBin());

                /*
                 * ST_SEC_ID Pass the selected ST_BIN values into STORAGEBIN table and fetch
                 * ST_SEC_ID values
                 */
                StorageBin stBin = mastersService.getStorageBin(dbInventory.getStorageBin(), warehouseId.get(0),
                        authTokenForMastersService.getAccess_token());
                reportInventory.setStorageSectionId(stBin.getStorageSectionId());

                // PACK_BARCODE
                reportInventory.setPackBarcodes(dbInventory.getPackBarcodes());

                // INV_QTY
                reportInventory.setInventoryQty(dbInventory.getInventoryQuantity());

                // STCK_TYP_ID/STCK_TYP_TEXT
                reportInventory.setStockType(dbInventory.getStockTypeId());
                reportInventoryList.add(reportInventory);
            }
            final Page<InventoryReport> page = new PageImpl<>(reportInventoryList, pageable,
                    inventoryList.getTotalElements());
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
//	public Page<InventoryReport> scheduleInventoryReport(Integer pageNo, Integer pageSize, String sortBy) {
//		List<String> warehouseId = new ArrayList<>();
//		warehouseId.add("110");
//		warehouseId.add("111");
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
//		Page<Inventory> inventoryList = inventoryRepository.findByWarehouseIdInAndDeletionIndicator (warehouseId, 0L, pageable);
//
////		Page<Inventory> inventoryList = inventoryRepository.findByWarehouseIdInAndDeletionIndicatorAndItemCode (warehouseId, 0L, "020309497", pageable);
//		List<InventoryReport> reportInventoryList = new ArrayList<>();
//
//		for (Inventory dbInventory : inventoryList) {
//			boolean isInventoryNeedUpdate = false;
//			InventoryReport reportInventory = new InventoryReport();
//
//			// WH_ID
//			reportInventory.setWarehouseId(dbInventory.getWarehouseId());
//
//			// ITM_CODE
//			reportInventory.setItemCode(dbInventory.getItemCode());
//
//			/*
//			 * ITEM_TEXT
//			 * -------------------------------------------------------------------------
//			 * Pass the fetched ITM_CODE values in IMBASICDATA1 table and fetch MFR_SKU
//			 * values
//			 */
//
//			try {
//				ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(dbInventory.getItemCode(),
//						dbInventory.getWarehouseId(), authTokenForMastersService.getAccess_token());
//
//				if (imBasicData1 != null) {
//					reportInventory.setDescription(imBasicData1.getDescription());
//					reportInventory.setMfrPartNumber(imBasicData1.getManufacturerPartNo());
//
//					if (dbInventory.getReferenceField8() == null) {
//						dbInventory.setReferenceField8(imBasicData1.getDescription());
//						isInventoryNeedUpdate = true;
//					}
//
//					if (dbInventory.getReferenceField9() == null) {
//						dbInventory.setReferenceField9(imBasicData1.getManufacturerPartNo());
//						isInventoryNeedUpdate = true;
//					}
//				}
//			} catch(Exception e) {
//				log.info("ERROR : imBasicData1 master get error " + dbInventory.getItemCode() + " " +
//						dbInventory.getWarehouseId(), e);
//			}
//
//			// INV_UOM
//			reportInventory.setUom(dbInventory.getInventoryUom());
//
//			// ST_BIN
//			reportInventory.setStorageBin(dbInventory.getStorageBin());
//
//			/*
//			 * ST_SEC_ID Pass the selected ST_BIN values into STORAGEBIN table and fetch
//			 * ST_SEC_ID values
//			 */
//			try {
//				StorageBin stBin = mastersService.getStorageBin(dbInventory.getStorageBin(),
//						authTokenForMastersService.getAccess_token());
//				reportInventory.setStorageSectionId(stBin.getStorageSectionId());
//
//				if (dbInventory.getReferenceField10() == null) {
//					dbInventory.setReferenceField10(stBin.getStorageSectionId());
//					isInventoryNeedUpdate = true;
//				}
//			} catch(Exception e) {
//				log.error("ERROR : stBin master get error "+ dbInventory.getStorageBin(), e);
//			}
//
//			// PACK_BARCODE
//			reportInventory.setPackBarcodes(dbInventory.getPackBarcodes());
//
//			// INV_QTY
//			try {
//				reportInventory.setInventoryQty(dbInventory.getInventoryQuantity() != null ? dbInventory.getInventoryQuantity() : 0);
//				reportInventory.setAllocatedQty(dbInventory.getAllocatedQuantity() != null ? dbInventory.getAllocatedQuantity() : 0);
//
//				reportInventory.setTotalQuantity(Double.sum(dbInventory.getInventoryQuantity() != null ? dbInventory.getInventoryQuantity() : 0,
//						dbInventory.getAllocatedQuantity() != null ? dbInventory.getAllocatedQuantity() : 0 ) );
//			} catch(Exception e) {
//				log.error("ERROR : ALL_QTY , TOTAL_QTY CALCULATE  ", e);
//			}
//
//
//			// STCK_TYP_ID/STCK_TYP_TEXT
//			reportInventory.setStockType(dbInventory.getStockTypeId());
//
//			if (isInventoryNeedUpdate) {
//				inventoryRepository.save(dbInventory);
//				log.info("dbInventory got updated");
//			}
//
//			reportInventoryList.add(reportInventory);
//		}
//		final Page<InventoryReport> page = new PageImpl<>(reportInventoryList, pageable,
//				inventoryList.getTotalElements());
//		return page;
//	}

    /**
     * @return
     */
    public List<InventoryReport> generateInventoryReport() {
        List<String> warehouseId = new ArrayList<>();
        warehouseId.add("110");
        warehouseId.add("111");
        List<Inventory> inventoryList = inventoryRepository.findByWarehouseIdInAndDeletionIndicator(warehouseId, 0L);
        List<InventoryReport> reportInventoryList = new ArrayList<>();

        inventoryList = inventoryList.stream()
                .filter(data -> data != null &&
                        (
                                (data.getInventoryQuantity() != null && data.getInventoryQuantity() > 0) ||
                                        (data.getAllocatedQuantity() != null && data.getAllocatedQuantity() > 0)
                        ))
                .collect(Collectors.toList());

        for (Inventory dbInventory : inventoryList) {
            InventoryReport reportInventory = new InventoryReport();

            // WH_ID
            reportInventory.setWarehouseId(dbInventory.getWarehouseId());

            // ITM_CODE
            reportInventory.setItemCode(dbInventory.getItemCode());

            /*
             * ITEM_TEXT
             * -------------------------------------------------------------------------
             * Pass the fetched ITM_CODE values in IMBASICDATA1 table and fetch MFR_SKU
             * values
             */
            //HARESSH- 23-08-2022 - commented since data is already directly stored during inventory creation
//			try {
//				ImBasicData1 imbasicdata1 = imbasicdata1Repository.findByItemCodeAndWarehouseIdInAndDeletionIndicator(
//						dbInventory.getItemCode(), warehouseId, 0L);
//				if (imbasicdata1 != null) {
//					reportInventory.setDescription(imbasicdata1.getDescription());
//					reportInventory.setMfrPartNumber(imbasicdata1.getManufacturerPartNo());
//				}
//			} catch(Exception e) {
//				log.info("ERROR : imBasicData1 master get error " + dbInventory.getItemCode() + " " +
//						dbInventory.getWarehouseId(), e);
//			}

            reportInventory.setDescription(dbInventory.getReferenceField8());
            reportInventory.setMfrPartNumber(dbInventory.getReferenceField9());
            reportInventory.setStorageSectionId(dbInventory.getReferenceField10());
            reportInventory.setAisleId(dbInventory.getReferenceField5());
            reportInventory.setLevelId(dbInventory.getReferenceField6());
            reportInventory.setRowId(dbInventory.getReferenceField7());


            // INV_UOM
            reportInventory.setUom(dbInventory.getInventoryUom());

            // ST_BIN
            reportInventory.setStorageBin(dbInventory.getStorageBin());

            /*
             * ST_SEC_ID Pass the selected ST_BIN values into STORAGEBIN table and fetch
             * ST_SEC_ID values
             */

            //HARESSH- 23-08-2022 - commented since data is already directly stored during inventory creation
//			try {
//				String storagebin = storagebinRepository.findByStorageBin(dbInventory.getStorageBin());
//				reportInventory.setStorageSectionId(storagebin);
//			} catch(Exception e) {
//				log.info("ERROR : stBin master get error "+ dbInventory.getStorageBin(), e);
//			}

            // PACK_BARCODE
            reportInventory.setPackBarcodes(dbInventory.getPackBarcodes());

            // INV_QTY
            try {
                reportInventory.setInventoryQty(dbInventory.getInventoryQuantity() != null ? dbInventory.getInventoryQuantity() : 0);
                reportInventory.setAllocatedQty(dbInventory.getAllocatedQuantity() != null ? dbInventory.getAllocatedQuantity() : 0);

                reportInventory.setTotalQuantity(Double.sum(dbInventory.getInventoryQuantity() != null ? dbInventory.getInventoryQuantity() : 0,
                        dbInventory.getAllocatedQuantity() != null ? dbInventory.getAllocatedQuantity() : 0));
            } catch (Exception e) {
                log.info("ERROR : ALL_QTY , TOTAL_QTY CALCULATE  ", e);
            }

            // STCK_TYP_ID/STCK_TYP_TEXT
            reportInventory.setStockType(dbInventory.getStockTypeId());
            reportInventoryList.add(reportInventory);
        }
        return reportInventoryList;
    }

    /**
     *
     * @param warehouseId
     * @param itemCode
     * @param fromCreatedOn
     * @param toCreatedOn
     * @return
     * @throws java.text.ParseException
     */
//	public List<StockMovementReport> getStockMovementReport(String warehouseId, String itemCode, String fromCreatedOn,
//			String toCreatedOn) throws java.text.ParseException {
//		// Warehouse
//		if (warehouseId == null) {
//			throw new BadRequestException("WarehouseId can't be blank.");
//		}
//
//		// Item Code
//		if (itemCode == null) {
//			throw new BadRequestException("ItemCode can't be blank.");
//		}
//
//		// Date
//		if (fromCreatedOn == null || toCreatedOn == null) {
//			throw new BadRequestException("CreatedOn can't be blank.");
//		}
//
//		/*
//		 * Pass the Search paramaters (WH_ID,ITM_CODE,IM_CTD_ON) values in
//		 * INVENOTRYMOVEMENT table for the selected date and fetch the below output
//		 * values
//		 */
//		Date fromDate = null;
//		Date toDate = null;
//		try {
//			fromDate = DateUtils.convertStringToDate(fromCreatedOn);
//			fromDate = DateUtils.addTimeToDate(fromDate);
//			toDate = DateUtils.convertStringToDate(toCreatedOn);
//			toDate = DateUtils.addDayEndTimeToDate(toDate);
//		} catch (Exception e) {
//			throw new BadRequestException("Date shoud be in MM-dd-yyyy format.");
//		}
//
//		List<InventoryMovement> inventoryMovementSearchResults_123 = inventoryMovementRepository
//				.findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeInOrderByCreatedOnAsc(warehouseId,
//						itemCode, fromDate, toDate, 1L, Arrays.asList(2L, 3L));
//		List<StockMovementReport> reportStockMovementList_1 = fillData(inventoryMovementSearchResults_123);
//		log.info("reportStockMovementList_1 : " + reportStockMovementList_1);
//		stockMovementReportRepository.saveAll(reportStockMovementList_1);
//
//		List<InventoryMovement> inventoryMovementSearchResults_35 = inventoryMovementRepository
//				.findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeInOrderByCreatedOnAsc(warehouseId,
//						itemCode, fromDate, toDate, 3L, Arrays.asList(5L));
//		List<StockMovementReport> reportStockMovementList_2 = fillData(inventoryMovementSearchResults_35);
//		log.info("reportStockMovementList_2 : " + reportStockMovementList_2);
//		stockMovementReportRepository.saveAll(reportStockMovementList_2);
//
//		reportStockMovementList_1.addAll(reportStockMovementList_2);
//		return reportStockMovementList_1;
//	}

    /**
     * @param inventoryMovementSearchResults
     * @return
     */
    private List<StockMovementReport> fillData(List<InventoryMovement> inventoryMovementSearchResults) {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        List<StockMovementReport> reportStockMovementList = new ArrayList<>();
        for (InventoryMovement inventoryMovement : inventoryMovementSearchResults) {
            StockMovementReport stockMovementReport = new StockMovementReport();
            stockMovementReport.setStockMovementReportId(System.currentTimeMillis());

            // WH_ID
            stockMovementReport.setWarehouseId(inventoryMovement.getWarehouseId());

            // ITM_CODE
            stockMovementReport.setItemCode(inventoryMovement.getItemCode());

            /*
             * MFR_SKU -------------- Pass the fetched ITM_CODE values in IMBASICDATA1 table
             * and fetch MFR_SKU values
             */
            ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(inventoryMovement.getItemCode(),
                    inventoryMovement.getWarehouseId(), authTokenForMastersService.getAccess_token());
            stockMovementReport.setManufacturerSKU(imBasicData1.getManufacturerPartNo());

            // ITEM_TEXT
            stockMovementReport.setItemText(imBasicData1.getDescription());

            // MVT_QTY
            /*
             * "Fetch MVT_QTY values where 1. MVT_TYP_ID = 1, SUB_MVT_TYP_ID=2,3 (Subtract
             * MVT_QTY values between 2 and 3) 2. MVT_TYP_ID = 3, SUB_MVT_TYP_ID=5 3.
             * MVT_TYP_ID = 2"
             */
            if (inventoryMovement.getMovementType() == 1L && inventoryMovement.getSubmovementType() == 2L) {
                stockMovementReport.setMovementQty(inventoryMovement.getMovementQty());
            } else if (inventoryMovement.getMovementType() == 1L && inventoryMovement.getSubmovementType() == 3L) {
                stockMovementReport.setMovementQty(-inventoryMovement.getMovementQty()); // Assign -ve number
            } else if (inventoryMovement.getMovementType() == 3L && inventoryMovement.getSubmovementType() == 5L) {
                stockMovementReport.setMovementQty(inventoryMovement.getMovementQty());
            }

            /*
             * Document type --------------------------------------------------- If
             * MVT_TYP_ID = 1, Hard Coded value "Inbound" and if MVT_TYP_ID=3, Hard Coded
             * Value "Outbound", MVT_TYP_ID=2, Harcoded Value "Transfer"
             */
            if (inventoryMovement.getMovementType() == 1L) {
                stockMovementReport.setDocumentType("Inbound");
            } else if (inventoryMovement.getMovementType() == 3L) {
                stockMovementReport.setDocumentType("Outbound");
            }

            // Document Number
            stockMovementReport.setDocumentNumber(inventoryMovement.getRefDocNumber());

            /*
             * PARTNER_CODE --------------------------- 1. For MVT_TYP_ID = 1 records, pass
             * REF_DOC_NO in INBOUNDLINE table and fetch PARTNER_CODE values and fill 2. For
             * MVT_TYP_ID = 3 records, pass REF_DOC_NO in OUTBOUNDHEADER table and fetch
             * PARTNER_CODE values and fill 3. For MVT_TYP_ID = 2, Hard Coded Value "" BIN
             * to BIN"""
             */
            if (inventoryMovement.getMovementType() == 1L) {
                List<InboundLine> inboundLine = inboundLineService.getInboundLine(inventoryMovement.getRefDocNumber(), inventoryMovement.getWarehouseId());
                log.info("inboundLine : " + inboundLine);
                if (!inboundLine.isEmpty()) {
                    stockMovementReport.setCustomerCode(inboundLine.get(0).getBusinessPartnerCode());
                }
            } else if (inventoryMovement.getMovementType() == 3L) {
                OutboundHeader outboundHeader = outboundHeaderService.getOutboundHeader(inventoryMovement.getRefDocNumber(), inventoryMovement.getWarehouseId());
                log.info("outboundHeader : " + outboundHeader);
                if (outboundHeader != null) {
                    stockMovementReport.setCustomerCode(outboundHeader.getPartnerCode());
                }
            }

            // Date & Time
            Date date = inventoryMovement.getCreatedOn();
            LocalDateTime datetime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String currentDate = datetime.format(newPattern);

            DateTimeFormatter newTimePattern = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTime = datetime.format(newTimePattern);
            stockMovementReport.setCreatedOn(currentDate);
            stockMovementReport.setCreatedTime(currentTime);

            Double balanceOHQty = 0D;
            Double movementQty = 0D;

            // BAL_OH_QTY
            if (inventoryMovement.getBalanceOHQty() != null) {
                balanceOHQty = inventoryMovement.getBalanceOHQty();
                stockMovementReport.setBalanceOHQty(balanceOHQty);
            }

            if (inventoryMovement.getMovementQty() != null) {
                movementQty = inventoryMovement.getMovementQty();
            }

            /*
             * Opening Stock ----------------------------- 1 For MVT_TYP_ID = 1 , opening
             * stock = BAL_OH_QTY - MVT_QTY 2. For MVT-TYP_ID = 3, opening stock =
             * BAL_OH_QTY + MVT_QTY 3. For MVT_TYP_ID = 2, Opening stock = BAL_OH_QTY +
             * MVT_QTY
             */
            if (inventoryMovement.getMovementType() == 1) {
                Double openingStock = balanceOHQty - movementQty;
                stockMovementReport.setOpeningStock(openingStock);
            } else if (inventoryMovement.getMovementType() == 3) {
                Double openingStock = balanceOHQty + movementQty;
                stockMovementReport.setOpeningStock(openingStock);
            }
            reportStockMovementList.add(stockMovementReport);
        }
        return reportStockMovementList;
    }

    /**
     * @param asnNumber
     * @return
     * @throws Exception
     */
    public ReceiptConfimationReport getReceiptConfimationReport(String asnNumber) throws Exception {
        if (asnNumber == null) {
            throw new BadRequestException("ASNNumber can't be blank");
        }

        ReceiptConfimationReport receiptConfimation;
        try {
            receiptConfimation = new ReceiptConfimationReport();
            ReceiptHeader receiptHeader = new ReceiptHeader();

            // 22-08-2022-Hareesh //commented the not used method call

//			SearchInboundHeader searchInboundHeader = new SearchInboundHeader();
//			searchInboundHeader.setRefDocNumber(Arrays.asList(asnNumber));
//			List<InboundHeader> inboundHeaderSearchResults = inboundHeaderService
//					.findInboundHeader(searchInboundHeader);
//			log.info("inboundHeaderSearchResults : " + inboundHeaderSearchResults);

//			List<InboundLine> inboundLineSearchResults = inboundLineService.getInboundLine(asnNumber);
            List<InboundLineV2> inboundLineSearchResults = inboundLineService.getInboundLineV2(asnNumber);
//			log.info("inboundLineSearchResults ------>: " + inboundLineSearchResults);

            double sumTotalOfExpectedQty = 0.0;
            double sumTotalOfAccxpectedQty = 0.0;
            double sumTotalOfDamagedQty = 0.0;
            double sumTotalOfMissingORExcess = 0.0;
            List<Receipt> receiptList = new ArrayList<>();
            log.info("inboundLine---------> : " + inboundLineSearchResults);
            if (!inboundLineSearchResults.isEmpty()) {
                // Supplier - PARTNER_CODE
                receiptHeader.setSupplier(inboundLineSearchResults.get(0).getBusinessPartnerCode());

                receiptHeader.setSupplierName(inboundLineSearchResults.get(0).getSupplierName());

                // Container No
                receiptHeader.setContainerNo(inboundLineSearchResults.get(0).getContainerNo());

                // Order Number - REF_DOC_NO
                receiptHeader.setOrderNumber(inboundLineSearchResults.get(0).getRefDocNumber());

                // Order Type -> PREINBOUNDHEADER - REF_DOC_TYPE
                // Pass REF_DOC_NO in PREINBOUNDHEADER and fetch REF_DOC_TYPE
                String referenceDocumentType = preInboundHeaderService.getReferenceDocumentTypeFromPreInboundHeader(
                        inboundLineSearchResults.get(0).getWarehouseId(), inboundLineSearchResults.get(0).getPreInboundNo(),
                        inboundLineSearchResults.get(0).getRefDocNumber());
                receiptHeader.setOrderType(referenceDocumentType);
                log.info("preInboundHeader referenceDocumentType--------> : " + referenceDocumentType);
            }
            for (InboundLineV2 inboundLine : inboundLineSearchResults) {


                Receipt receipt = new Receipt();

                // SKU - ITM_CODE
                receipt.setSku(inboundLine.getItemCode());

                // Description - ITEM_TEXT
                receipt.setDescription(inboundLine.getDescription());

                // Mfr.Sku - MFR_PART
//				receipt.setMfrSku(inboundLine.getManufacturerPartNo());
                receipt.setMfrSku(inboundLine.getManufacturerName());

                // Expected - ORD_QTY
                double expQty = 0;
                if (inboundLine.getOrderQty() != null) {
                    expQty = inboundLine.getOrderQty();
                    receipt.setExpectedQty(expQty);
                    sumTotalOfExpectedQty += expQty;
                    log.info("expQty------#--> : " + expQty);
                }

                // Accepted - ACCEPT_QTY
                double acceptQty = 0;
                if (inboundLine.getAcceptedQty() != null) {
                    acceptQty = inboundLine.getAcceptedQty();
                    receipt.setAcceptedQty(acceptQty);
                    sumTotalOfAccxpectedQty += acceptQty;
                    log.info("acceptQty------#--> : " + acceptQty);
                }

                // Damaged - DAMAGE_QTY
                double damageQty = 0;
                if (inboundLine.getDamageQty() != null) {
                    damageQty = inboundLine.getDamageQty();
                    receipt.setDamagedQty(damageQty);
                    sumTotalOfDamagedQty += damageQty;
                    log.info("damageQty------#--> : " + damageQty);
                }

                // Missing/Excess - SUM(Accepted + Damaged) - Expected
                double missingORExcessSum = (acceptQty + damageQty) - expQty;
                sumTotalOfMissingORExcess += missingORExcessSum;
                receipt.setMissingORExcess(missingORExcessSum);
                log.info("missingORExcessSum------#--> : " + missingORExcessSum);

                // Status
                /*
                 * 1. If Missing/Excess = 0, then hardcode Status as ""Received"" 2. If Damage
                 * qty is greater than zero, then Hard code status ""Damage Received"" 3. If
                 * Missing/Excess is less than 0, then hardcode Status as ""Partial Received""
                 * 4. If Missing/Excess is excess than 0, then hardcode Status as ""Excess
                 * Received"" 5. If Sum (Accepted Qty +Damaged qty) is 0, then Hardcode status
                 * as ""Not yet received""
                 */
                if (missingORExcessSum == 0) {
                    receipt.setStatus("Received");
                } else if (damageQty > 0) {
                    receipt.setStatus("Damage Received");
                } else if (missingORExcessSum < 0) {
                    receipt.setStatus("Partial Received");
                } else if (missingORExcessSum > 0) {
                    receipt.setStatus("Excess Received");
                } else if (sumTotalOfMissingORExcess == 0) {
                    receipt.setStatus("Not Received");
                }
                log.info("receipt------#--> : " + receipt);
                receiptList.add(receipt);
            }

            receiptHeader.setExpectedQtySum(sumTotalOfExpectedQty);
            receiptHeader.setAcceptedQtySum(sumTotalOfAccxpectedQty);
            receiptHeader.setDamagedQtySum(sumTotalOfDamagedQty);
            receiptHeader.setMissingORExcessSum(sumTotalOfMissingORExcess);

            receiptConfimation.setReceiptHeader(receiptHeader);
            receiptConfimation.setReceiptList(receiptList);
            log.info("receiptConfimation : " + receiptConfimation);
            return receiptConfimation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReceiptConfimationReport getReceiptConfimationReportV2(String asnNumber, String preInboundNo, String companyCodeId, String plantId,
                                                                  String languageId, String warehouseId) throws Exception {
        if (asnNumber == null) {
            throw new BadRequestException("ASNNumber can't be blank");
        }
        if (preInboundNo == null || companyCodeId == null || plantId == null || languageId == null || warehouseId == null) {
            throw new BadRequestException("paramenters can't be blank");
        }

        ReceiptConfimationReport receiptConfimation;
        try {
            receiptConfimation = new ReceiptConfimationReport();
            ReceiptHeader receiptHeader = new ReceiptHeader();

            // 22-08-2022-Hareesh //commented the not used method call

//			SearchInboundHeader searchInboundHeader = new SearchInboundHeader();
//			searchInboundHeader.setRefDocNumber(Arrays.asList(asnNumber));
//			List<InboundHeader> inboundHeaderSearchResults = inboundHeaderService
//					.findInboundHeader(searchInboundHeader);
//			log.info("inboundHeaderSearchResults : " + inboundHeaderSearchResults);

//			List<InboundLine> inboundLineSearchResults = inboundLineService.getInboundLine(asnNumber);
            log.info("c_id, plant_id, lang_id, wh_id, preInboundNo, ref_doc_no: " +
                    companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId + ", " + asnNumber + ", " + preInboundNo);
            List<InboundLineV2> inboundLineSearchResults = inboundLineService.getInboundLineForReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId);
            log.info("inboundLineSearchResults ------>: " + inboundLineSearchResults.size());

            double sumTotalOfExpectedQty = 0.0;
            double sumTotalOfAccxpectedQty = 0.0;
            double sumTotalOfDamagedQty = 0.0;
            double sumTotalOfMissingORExcess = 0.0;
            List<Receipt> receiptList = new ArrayList<>();
            log.info("inboundLine---------> : " + inboundLineSearchResults.size());
            if (!inboundLineSearchResults.isEmpty()) {
                // Supplier - PARTNER_CODE
                receiptHeader.setSupplier(inboundLineSearchResults.get(0).getBusinessPartnerCode());

                receiptHeader.setSupplierName(inboundLineSearchResults.get(0).getSupplierName());

                // Container No
                receiptHeader.setContainerNo(inboundLineSearchResults.get(0).getContainerNo());

                // Order Number - REF_DOC_NO
                receiptHeader.setOrderNumber(inboundLineSearchResults.get(0).getRefDocNumber());

                // Order Type -> PREINBOUNDHEADER - REF_DOC_TYPE
                // Pass REF_DOC_NO in PREINBOUNDHEADER and fetch REF_DOC_TYPE
                String referenceDocumentType = preInboundHeaderService.getReferenceDocumentTypeFromPreInboundHeader(
                        inboundLineSearchResults.get(0).getWarehouseId(), inboundLineSearchResults.get(0).getPreInboundNo(),
                        inboundLineSearchResults.get(0).getRefDocNumber());
                receiptHeader.setOrderType(referenceDocumentType);
                log.info("preInboundHeader referenceDocumentType--------> : " + referenceDocumentType);
            }
            for (InboundLineV2 inboundLine : inboundLineSearchResults) {


                Receipt receipt = new Receipt();

                // SKU - ITM_CODE
                receipt.setSku(inboundLine.getItemCode());

                // Description - ITEM_TEXT
                receipt.setDescription(inboundLine.getDescription());

                // Mfr.Sku - MFR_PART
//				receipt.setMfrSku(inboundLine.getManufacturerPartNo());
                receipt.setMfrSku(inboundLine.getManufacturerName());

                // Expected - ORD_QTY
                double expQty = 0;
                if (inboundLine.getOrderQty() != null) {
                    expQty = inboundLine.getOrderQty();
                    receipt.setExpectedQty(expQty);
                    sumTotalOfExpectedQty += expQty;
                    log.info("expQty------#--> : " + expQty);
                }

                // Accepted - ACCEPT_QTY
                double acceptQty = 0;
                if (inboundLine.getAcceptedQty() != null) {
                    acceptQty = inboundLine.getAcceptedQty();
                    receipt.setAcceptedQty(acceptQty);
                    sumTotalOfAccxpectedQty += acceptQty;
                    log.info("acceptQty------#--> : " + acceptQty);
                }

                // Damaged - DAMAGE_QTY
                double damageQty = 0;
                if (inboundLine.getDamageQty() != null) {
                    damageQty = inboundLine.getDamageQty();
                    receipt.setDamagedQty(damageQty);
                    sumTotalOfDamagedQty += damageQty;
                    log.info("damageQty------#--> : " + damageQty);
                }

                // Missing/Excess - SUM(Accepted + Damaged) - Expected
                double missingORExcessSum = (acceptQty + damageQty) - expQty;
                sumTotalOfMissingORExcess += missingORExcessSum;
                receipt.setMissingORExcess(missingORExcessSum);
                log.info("missingORExcessSum------#--> : " + missingORExcessSum);

                // Status
                /*
                 * 1. If Missing/Excess = 0, then hardcode Status as ""Received"" 2. If Damage
                 * qty is greater than zero, then Hard code status ""Damage Received"" 3. If
                 * Missing/Excess is less than 0, then hardcode Status as ""Partial Received""
                 * 4. If Missing/Excess is excess than 0, then hardcode Status as ""Excess
                 * Received"" 5. If Sum (Accepted Qty +Damaged qty) is 0, then Hardcode status
                 * as ""Not yet received""
                 */
                if (missingORExcessSum == 0) {
                    receipt.setStatus("Received");
                } else if (damageQty > 0) {
                    receipt.setStatus("Damage Received");
                } else if (missingORExcessSum < 0) {
                    receipt.setStatus("Partial Received");
                } else if (missingORExcessSum > 0) {
                    receipt.setStatus("Excess Received");
                } else if (sumTotalOfMissingORExcess == 0) {
                    receipt.setStatus("Not Received");
                }
                log.info("receipt------#--> : " + receipt);
                receiptList.add(receipt);
            }

            receiptHeader.setExpectedQtySum(sumTotalOfExpectedQty);
            receiptHeader.setAcceptedQtySum(sumTotalOfAccxpectedQty);
            receiptHeader.setDamagedQtySum(sumTotalOfDamagedQty);
            receiptHeader.setMissingORExcessSum(sumTotalOfMissingORExcess);

            receiptConfimation.setReceiptHeader(receiptHeader);
            receiptConfimation.setReceiptList(receiptList);
            log.info("receiptConfimation : " + receiptConfimation);
            return receiptConfimation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * getMobileDashboard
     *
     * @param warehouseId
     * @return
     */
    public MobileDashboard getMobileDashboard(String companyCode, String plantId, String warehouseId, String languageId, String loginUserID) {
        MobileDashboard mobileDashboard = new MobileDashboard();

        /*--------------Inbound--------------------------------*/
        MobileDashboard.InboundCount inboundCount = mobileDashboard.new InboundCount();

        // -------------Cases------------------------------------
        // Pass Login WH_ID into STAGINGHEADER table and fetch the count of records
        // where STATUS_ID=12
//		List<StagingHeader> stagingHeaderList = stagingHeaderService.getStagingHeaderCount(warehouseId);
//		long cases = stagingHeaderList.stream().count();
//		inboundCount.setCases(cases);

        List<Long> statusId = Arrays.asList(16L);
        List<GrHeader> grHeaders = grHeaderService.getGrHeader(companyCode, plantId, languageId, warehouseId, statusId);
        long cases = grHeaders.stream().count();
        inboundCount.setCases(cases);

        // -------------Putaway----------------------------------
        // Pass Login WH_ID into PUTAWAYHEADER table and fetch the count of records
        // where STATUS_ID=19
        // and IB_ORD_TYP_ID= 1 and 3
        List<Long> orderTypeId = Arrays.asList(1L, 3L, 4L, 5L);
        List<PutAwayHeader> putAwayHeaderList = putAwayHeaderService.getPutAwayHeaderCount(
                companyCode, plantId, languageId, warehouseId, orderTypeId);
        long putaway = putAwayHeaderList.stream().count();
        inboundCount.setPutaway(putaway);

        // -------------Reversals-------------------------------
        // Pass Login WH_ID into PUTAWAYHEADER table and fetch the count of records
        // where STATUS_ID=19
        // and IB_ORD_TYP_ID= 2 and 4
        orderTypeId = Arrays.asList(2L);
        putAwayHeaderList = putAwayHeaderService.getPutAwayHeaderCount(companyCode, plantId, languageId, warehouseId, orderTypeId);
        long reversals = putAwayHeaderList.stream().count();
        inboundCount.setReversals(reversals);

        //Get LevelId from User HHtUser
        String levelId = pickupLineRepository.getLevelId(companyCode, plantId, languageId, warehouseId, loginUserID);

        /*--------------Outbound--------------------------------*/
        MobileDashboard.OutboundCount outboundCount = mobileDashboard.new OutboundCount();

        // --------------Picking---------------------------------------------------------------------------
        // Pass Login WH_ID into PICKUPHEADER table and fetch the count of records where
        // STATUS_ID=48 and
        // OB_ORD_TYP_ID= 0,1 and 3
        orderTypeId = Arrays.asList(0L, 1L, 3L);
        List<PickupHeaderV2> pickupHeaderList =
                pickupHeaderService.getPickupHeaderCount(companyCode, plantId, languageId, warehouseId, levelId, orderTypeId);
        long picking = pickupHeaderList.stream().count();
        outboundCount.setPicking(picking);

        // -------------Reversals-------------------------------------------------------------------------
        // Pass Login WH_ID into PICKUPHEADER table and fetch the count of records where
        // STATUS_ID=48 and
        // OB_ORD_TYP_ID= 2
        orderTypeId = Arrays.asList(2L);
        pickupHeaderList = pickupHeaderService.getPickupHeaderCount(companyCode, plantId, languageId, warehouseId, levelId, orderTypeId);
        reversals = pickupHeaderList.stream().count();
        outboundCount.setReversals(reversals);

        // -----------Quality-----------------------------------------------------------------------------
        // Pass Login WH_ID into QUALITYHEADER table and fetch the count of records
        // where STATUS_ID=54


        List<Long> qualityHeaderCount = qualityHeaderRepository.getQualityHeaderCount(companyCode, plantId, languageId, warehouseId, 54L);
        long quality = qualityHeaderCount.stream().count();

        outboundCount.setQuality(quality);

        /*--------------StockCount--------------------------------*/
        MobileDashboard.StockCount stockCount = mobileDashboard.new StockCount();

        List<Long> status2 = Arrays.asList(72L, 75L);

        List<PerpetualLine> perpetualLines = perpetualLineService.getPerpetualLine(companyCode, languageId, plantId, warehouseId, loginUserID, status2);
        List<PeriodicLine> periodicLines = periodicLineService.getPeriodicLine(companyCode, languageId, plantId, warehouseId, loginUserID, status2);

        long perpetualCount = perpetualLines.stream().count();
        long periodicLineCount = periodicLines.stream().count();

        stockCount.setPerpertual(perpetualCount);
        stockCount.setPeriodic(periodicLineCount);

        //------------PERPETUAL & PERIODIC COUNT----------------------------------------------------------
        // Pass Login WH_ID into PERPETUAL & PERIODIC LINE table and fetch the count of records
        // where STATUS_ID = 72
//		try {
//			SearchPerpetualLine searchPerpetualLine = new SearchPerpetualLine();
////			searchPerpetualLine.setCompanyCodeId(Collections.singletonList(companyCode));
////			searchPerpetualLine.setLanguageId(Collections.singletonList(languageId));
////			searchPerpetualLine.setPlantId(Collections.singletonList(plantId));
//			searchPerpetualLine.setWarehouseId(warehouseId);
//			searchPerpetualLine.setLineStatusId(Arrays.asList(72L));
//			List<PerpetualLine> perpetualLines = perpetualLineService.findPerpetualLine(searchPerpetualLine);
//			long perpetualCount = perpetualLines.stream().count();
//
//			SearchPeriodicLine searchPeriodicLine = new SearchPeriodicLine();
//			searchPeriodicLine.setWarehouseId(warehouseId);
//			searchPeriodicLine.setLineStatusId(Arrays.asList(72L));
//			List<PeriodicLine> periodicLines = periodicLineService.findPeriodicLine(searchPeriodicLine);
//			long periodicLineCount = periodicLines.stream().count();
//
//			stockCount.setPerpertual(perpetualCount);
//			stockCount.setPeriodic(periodicLineCount);
//			log.info("stockCount : " + stockCount);

//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        mobileDashboard.setInboundCount(inboundCount);
        mobileDashboard.setOutboundCount(outboundCount);
        mobileDashboard.setStockCount(stockCount);
        return mobileDashboard;
    }


    //Find MobileDashBoard
    public MobileDashboard findMobileDashBoard(FindMobileDashBoard findMobileDashBoard) throws Exception {

        MobileDashboard mobileDashboard = new MobileDashboard();

        List<String> companyCode = findMobileDashBoard.getCompanyCode();
        List<String> plantId = findMobileDashBoard.getPlantId();
        List<String> warehouseId = findMobileDashBoard.getWarehouseId();
        List<String> languageId = findMobileDashBoard.getLanguageId();
        List<String> userId = findMobileDashBoard.getUserID();

        /*--------------Inbound--------------------------------*/
        MobileDashboard.InboundCount inboundCount = mobileDashboard.new InboundCount();

        Long grHeaders = grHeaderRepository.grHeaderCount(companyCode, plantId, languageId, warehouseId, 14L);
        log.info ("-------companyCode, plantId, languageId, warehouseId---------count: " + companyCode + plantId + languageId + warehouseId);
        log.info ("-------grHeaders---------count: " + grHeaders);
        inboundCount.setCases(grHeaders);

        // -------------Putaway----------------------------------
//        List<Long> orderTypeId = Arrays.asList(1L, 3L, 4L, 5L);
        List<Long> orderTypeId = Arrays.asList(1L);
        Long putAwayHeaderList = putAwayHeaderRepository.getPutAwayHeaderCount(companyCode, plantId, warehouseId, languageId, 19L, orderTypeId);
        inboundCount.setPutaway(putAwayHeaderList);

        // -------------Reversals-------------------------------
        orderTypeId = Arrays.asList(2L);
        Long putAwayHeaderReversals = putAwayHeaderRepository.getPutAwayHeaderCount(companyCode, plantId, warehouseId, languageId, 19L, orderTypeId);
        inboundCount.setReversals(putAwayHeaderReversals);

        /*--------------Outbound--------------------------------*/
        MobileDashboard.OutboundCount outboundCount = mobileDashboard.new OutboundCount();

        //Get LevelId from User HHtUser
//        String levelId = pickupLineRepository.getLevelIdForUserId(companyCode, plantId, languageId, warehouseId, userId);

        // --------------Picking---------------------------------------------------------------------------
        orderTypeId = Arrays.asList(0L, 1L, 3L);
//        List<PickupHeaderV2> pickupHeaderList =
//                pickupHeaderService.getPickupHeaderCount(companyCode, plantId, languageId, warehouseId, levelId, orderTypeId);
        Long pickupHeaderList = pickupHeaderRepository.getPickupHeaderCount(companyCode, plantId, warehouseId, languageId, userId, 48L, orderTypeId);
        outboundCount.setPicking(pickupHeaderList);

        // -------------Reversals-------------------------------------------------------------------------
        orderTypeId = Arrays.asList(2L);                //Returns
        Long pickupHeaderListReversal = pickupHeaderRepository.getPickupHeaderCount(companyCode, plantId, warehouseId, languageId, userId, 48L, orderTypeId);
        outboundCount.setReversals(pickupHeaderListReversal);

        // -----------Quality-----------------------------------------------------------------------------
        Long qualityHeaderCount = qualityHeaderRepository.getQualityCount(companyCode, plantId, languageId, warehouseId, 54L);
//        Long quality = qualityHeaderCount.stream().count();
        outboundCount.setQuality(qualityHeaderCount);

        // -----------Tracking-----------------------------------------------------------------------------
        List<Long> statusId = Arrays.asList(48L, 50L, 57L);
        Long tracking = outboundLineV2Repository.gettrackingCount(companyCode, plantId, languageId, warehouseId, statusId);
        outboundCount.setTracking(tracking);
        /*--------------StockCount--------------------------------*/
        MobileDashboard.StockCount stockCount = mobileDashboard.new StockCount();

        List<Long> status2 = Arrays.asList(72L, 75L);

        Long perpetualLines = perpetualLineRepository.getPerpetualLineCount(companyCode, plantId, warehouseId, languageId, status2, userId);
        Long periodicLines = periodicLineRepository.getPeriodicLineCount(companyCode, plantId, warehouseId, languageId, status2, userId);

        stockCount.setPerpertual(perpetualLines);
        stockCount.setPeriodic(periodicLines);

        mobileDashboard.setInboundCount(inboundCount);
        mobileDashboard.setOutboundCount(outboundCount);
        mobileDashboard.setStockCount(stockCount);
        return mobileDashboard;

    }

    /**
     * AwaitingASN Count
     *
     * @param warehouseId
     * @param fromCreatedOn
     * @param toCreatedOn
     * @return
     * @throws java.text.ParseException
     */
    @Transactional
    private long getAwaitingASNCount(String warehouseId, Date fromCreatedOn, Date toCreatedOn)
            throws java.text.ParseException {
        /*
         * Receipts - Awaiting ASN -------------------------- Pass the logged in WH_ID
         * and current date in CR_CTD_ON field in CONTAINERRECEIPT table and fetch the
         * count of records where REF_DOC_NO is Null
         */
        Date[] dates = DateUtils.addTimeToDatesForSearch(fromCreatedOn, toCreatedOn);
        fromCreatedOn = dates[0];
        toCreatedOn = dates[1];
        long awaitingASNCount = containerReceiptRepository.countByWarehouseIdAndContainerReceivedDateBetweenAndRefDocNumberIsNull(warehouseId, fromCreatedOn, toCreatedOn);
        log.info("awaitingASNCount : " + awaitingASNCount);
        return awaitingASNCount;
    }

    /**
     * @param warehouseId
     * @param startConfirmedOn
     * @param endConfirmedOn
     * @return
     * @throws Exception
     */
    @Transactional
    private long getContainerReceivedCount(String warehouseId, Date startConfirmedOn, Date endConfirmedOn)
            throws Exception {
        /*
         * Container Received ------------------------ Pass the logged in WH_ID and
         * current date in IB_CNF_ON field in INBOUNDHEADER table and fetch the count of
         * records
         */
        Date[] dates = DateUtils.addTimeToDatesForSearch(startConfirmedOn, endConfirmedOn);
        startConfirmedOn = dates[0];
        endConfirmedOn = dates[1];
        long containerReceivedCount = inboundHeaderRepository.countByWarehouseIdAndConfirmedOnBetweenAndStatusIdAndDeletionIndicator(
                warehouseId, startConfirmedOn, endConfirmedOn, 24L, 0L);
        log.info("containerReceivedCount : " + containerReceivedCount);
        return containerReceivedCount;
    }

    /**
     * @param warehouseId
     * @param startConfirmedOn
     * @param endConfirmedOn
     * @return
     * @throws Exception
     */
    @Transactional
    private long getItemReceivedCount(String warehouseId, Date startConfirmedOn, Date endConfirmedOn) throws Exception {
        /*
         * Item Received ------------------- Pass the logged in WH_ID and current date
         * in IB_CNF_ON field in INBOUNDLINE table and fetch the count of records where
         * REF_FIELD_1 is Null
         */
        Date[] dates = DateUtils.addTimeToDatesForSearch(startConfirmedOn, endConfirmedOn);
        startConfirmedOn = dates[0];
        endConfirmedOn = dates[1];
        long itemReceivedCount = inboundLineRepository.countByWarehouseIdAndConfirmedOnBetweenAndStatusIdAndReferenceField1IsNull(
                warehouseId, startConfirmedOn, endConfirmedOn, 24L);
        log.info("itemReceivedCount : " + itemReceivedCount);
        return itemReceivedCount;
    }

    /**
     * @param warehouseId
     * @param fromDeliveryDate
     * @param toDeliveryDate
     * @return
     * @throws Exception
     */
    private long getShippedLineCount(String warehouseId, Date fromDeliveryDate, Date toDeliveryDate) throws Exception {
        /*
         * Shipped Line --------------------- Shipped Line Pass the logged in WH_ID and
         * Current date as DLV_CNF_ON in OUTBOUNDLINE table and fetch Count of
         * OB_LINE_NO values where REF_FIELD_2=Null and DLV_QTY > 0
         */
        log.info("---getShippedLineCount>>>>fromDeliveryDate-----> : " + fromDeliveryDate);
        log.info("---getShippedLineCount>>>>toDeliveryDate-----> : " + toDeliveryDate);
        long shippedLineCount =
                outboundLineRepository.countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
                        warehouseId, fromDeliveryDate, toDeliveryDate, 59L, 0L, Double.valueOf(0));

        log.info("shippedLineCount : " + shippedLineCount);
        return shippedLineCount;
    }

    /**
     * @param warehouseId
     * @param fromDeliveryDate
     * @param toDeliveryDate
     * @param type
     * @return
     * @throws Exception
     */
    private long getNormalNSpecialCount(String warehouseId, Date fromDeliveryDate, Date toDeliveryDate, String type)
            throws Exception {
        /*
         * Normal ---------------- Pass the logged in WH_ID and Current date as
         * DLV_CNF_ON in OUTBOUNDLINE table and fetch Count of OB_LINE_NO values where
         * REF_FIELD_1=N, REF_FIELD_2=Null and DLV_QTY>0 (Shipped Lines)
         */
        long normalCount =
                outboundLineRepository.countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField1AndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
                        warehouseId, fromDeliveryDate, toDeliveryDate, 59L, 0L, type, Double.valueOf(0));

        log.info("normalCount : " + normalCount);
        return normalCount;
    }

    /**
     *
     * @return
     */
//	public WorkBookSheetDTO exportXlsxFile() {
//		try {
//			int pageSize = 500;
//			Page<InventoryReport> pageResult = scheduleInventoryReport(0, pageSize, "itemCode");
//			List<InventoryReport> listRecords = new ArrayList<>();
//			listRecords.addAll(pageResult.getContent());
//
//			for (int pageNo = 1; pageNo <= pageResult.getTotalPages(); pageNo++) {
//				pageResult = scheduleInventoryReport(pageNo, pageSize, "itemCode");
//				listRecords.addAll(pageResult.getContent());
//				log.info("listRecords count: " + listRecords.size());
//			}
//
//			WorkBookSheetDTO workBookSheetDTO = workBookService.createWorkBookWithSheet("inventory");
//			CellStyle headerStyle = workBookSheetDTO.getStyle();
//			CellStyle cellStyle = workBookService.createLineCellStyle(workBookSheetDTO.getWorkbook());
//			CellStyle decimalFormatCells = workBookService.createLineCellStyle(workBookSheetDTO.getWorkbook());
//
//			listRecords = listRecords.stream()
//					.filter(data-> data != null &&
//							(
//								(data.getInventoryQty() != null && data.getInventoryQty() > 0) ||
//								(data.getAllocatedQty() != null && data.getAllocatedQty() > 0)
//							))
//					.collect(Collectors.toList());
//
//			/*
//			 * private String WAREHOUSEID; // WH_ID private String ITEMCODE; // ITM_CODE
//			 * private String DESCRIPTION; // ITEM_TEXT private String UOM; // INV_UOM
//			 * private String STORAGEBIN; // ST_BIN private String STORAGESECTIONID; //
//			 * ST_SEC_ID private String PACKBARCODES; // PACK_BARCODE private Double
//			 * INVENTORYQTY; // INV_QTY private Long STOCKTYPE; // STCK_TYP_TEXT
//			 */
//			ArrayList<String> headerData = new ArrayList<>();
//			headerData.add("WAREHOUSEID");
//			headerData.add("ITEMCODE");
//			headerData.add("DESCRIPTION");
//			headerData.add("MFR PART NO");
//			headerData.add("UOM");
//			headerData.add("STORAGEBIN");
//			headerData.add("STORAGESECTIONID");
//			headerData.add("PACKBARCODES");
//			headerData.add("INVENTORYQTY");
//			headerData.add("ALLOCATEDQTY");
//			headerData.add("TOTALQTY");
//			headerData.add("STOCKTYPE");
//
//			this.createHeaderRow(workBookSheetDTO.getWorkbook().getSheet("inventory"), headerStyle, headerData);
//
//			int rowIndex = 1;
//			for (InventoryReport data : listRecords) {
//				int cellIndex = 0;
//				Row row = workBookSheetDTO.getWorkbook().getSheet("inventory").createRow(rowIndex++);
//				try {
//					row.createCell(cellIndex).setCellValue(data.getWarehouseId());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getItemCode());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getDescription());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getMfrPartNumber());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getUom());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getStorageBin());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getStorageSectionId());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getPackBarcodes());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getInventoryQty());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getAllocatedQty() != null ? data.getAllocatedQty() : 0);
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getTotalQuantity() != null ? data.getTotalQuantity() : 0);
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//					cellIndex++;
//
//					row.createCell(cellIndex).setCellValue(data.getStockType());
//					row.getCell(cellIndex).setCellStyle(cellStyle);
//				} catch (Exception e){
//					log.info("ERROR : Excel inventory bind row " + rowIndex, e);
//				}
//			}
//
//			OutputStream fout = new FileOutputStream("inventory.xlsx");
//			workBookSheetDTO.getWorkbook().write(fout);
//			return workBookSheetDTO;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

    /**
     * @param os
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream getOutputStreamToByteArray(OutputStream os) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(bos.toByteArray());
        byte[] arr = bos.toByteArray();
        os.write(arr);
        return bos;
    }

    /**
     * @param sheet
     * @param headerStyle
     * @param header
     */
    public void createHeaderRow(Sheet sheet, CellStyle headerStyle, List<String> header) {
        Row headerRows = sheet.createRow(0);
        int k = 0;
        for (String value : header) {
            headerRows.createCell(k);
            headerRows.getCell(k).setCellValue(value.toString());
            headerRows.getCell(k).setCellStyle(headerStyle);
            k++;
        }
    }

    /**
     * @param warehouseId
     * @return
     * @throws Exception
     */
    @Transactional
    public Dashboard getDashboardCount(String warehouseId) throws Exception {
        Dashboard dashboard = new Dashboard();

        /*--------------------------DAY-----------------------------------------------*/
        Dashboard.Day day = dashboard.new Day();
        Dashboard.Day.Receipts dayReceipts = day.new Receipts();

        /*-----------------------Receipts--------------------------------------------*/
        // Awaiting ASN
        long awaitingASNCount = getAwaitingASNCount(warehouseId, DateUtils.dateSubtract(1), DateUtils.dateSubtract(1));
        dayReceipts.setAwaitingASN(awaitingASNCount);

        long containerReceivedCount = getContainerReceivedCount(warehouseId, DateUtils.dateSubtract(1), DateUtils.dateSubtract(1));
        dayReceipts.setContainerReceived(containerReceivedCount);

        long itemReceivedCount = getItemReceivedCount(warehouseId, DateUtils.dateSubtract(1), DateUtils.dateSubtract(1));
        dayReceipts.setItemReceived(itemReceivedCount);

        //Shipping Day
        Dashboard.Day.Shipping dayShipping = day.new Shipping();

        Date fromDate = DateUtils.dateSubtract(1, 14, 0, 0); // From Yesterday 14:00 to Today 15:00
        Date endDate = DateUtils.dateSubtract(0, 13, 59, 59);
        long shippedLineCount = getShippedLineCount(warehouseId, fromDate, endDate);
        dayShipping.setShippedLine(shippedLineCount);

        long normalCount = getNormalNSpecialCount(warehouseId, fromDate, endDate, "N");
        dayShipping.setNormal(normalCount);

        long specialCount = getNormalNSpecialCount(warehouseId, fromDate, endDate, "S");
        dayShipping.setSpecial(specialCount);

        day.setReceipts(dayReceipts);
        day.setShipping(dayShipping);
        dashboard.setDay(day);

        /*--------------------------MONTH--------------------------------------------*/
        Dashboard.Month month = dashboard.new Month();
        Dashboard.Month.Receipts monthReceipts = month.new Receipts();

        /*-----------------------Receipts--------------------------------------------*/
        // Awaiting ASN
        LocalDate today = LocalDate.now();
        today = today.withDayOfMonth(1);
        log.info("First day of current month: " + today.withDayOfMonth(1));
        Date beginningOfMonth = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

        awaitingASNCount = getAwaitingASNCount(warehouseId, beginningOfMonth, new Date());
        monthReceipts.setAwaitingASN(awaitingASNCount);

        containerReceivedCount = getContainerReceivedCount(warehouseId, beginningOfMonth, new Date());
        monthReceipts.setContainerReceived(containerReceivedCount);

        itemReceivedCount = getItemReceivedCount(warehouseId, beginningOfMonth, new Date());
        monthReceipts.setItemReceived(itemReceivedCount);

        //Shipping Month
        Dashboard.Month.Shipping monthShipping = month.new Shipping();

        LocalDate currentDay = LocalDate.now();
        LocalDate beginDayOfMonth = today.withDayOfMonth(1);
        fromDate = DateUtils.addTimeToDate(beginDayOfMonth, 14, 0, 0);
        endDate = DateUtils.addTimeToDate(currentDay, 13, 59, 59);

//		shippedLineCount = getShippedLineCount(warehouseId, beginningOfMonth, new Date());
        shippedLineCount = getShippedLineCount(warehouseId, fromDate, endDate);
        monthShipping.setShippedLine(shippedLineCount);

//		normalCount = getNormalNSpecialCount(warehouseId, beginningOfMonth, new Date(), "N");
        normalCount = getNormalNSpecialCount(warehouseId, fromDate, endDate, "N");
        monthShipping.setNormal(normalCount);

//		specialCount = getNormalNSpecialCount(warehouseId, beginningOfMonth, new Date(), "S");
        specialCount = getNormalNSpecialCount(warehouseId, fromDate, endDate, "S");
        monthShipping.setSpecial(specialCount);

        //Bin Status
        Dashboard.BinStatus binStatus = dashboard.new BinStatus();

        long statusCount = storagebinRepository.countByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 0L, 0L);
        binStatus.setStatusEqualToZeroCount(statusCount);

        statusCount = storagebinRepository.countByWarehouseIdAndStatusIdNotAndDeletionIndicator(warehouseId, 0L, 0L);
        binStatus.setStatusNotEqualToZeroCount(statusCount);

        month.setReceipts(monthReceipts);
        month.setShipping(monthShipping);
        dashboard.setMonth(month);
        dashboard.setBinStatus(binStatus);

        return dashboard;
    }

    /**
     * @param fastSlowMovingDashboardRequest
     * @return
     * @throws Exception
     */
    @Transactional
    public List<FastSlowMovingDashboard> getFastSlowMovingDashboard(FastSlowMovingDashboardRequest fastSlowMovingDashboardRequest) throws Exception {

        log.info("Fast slow moving dashboard request {}", fastSlowMovingDashboardRequest);
        if (fastSlowMovingDashboardRequest.getWarehouseId() == null || fastSlowMovingDashboardRequest.getWarehouseId().isEmpty()) {
            throw new BadRequestException("Please provide valid warehouseId");
        }
        if (fastSlowMovingDashboardRequest.getFromDate() == null || fastSlowMovingDashboardRequest.getToDate() == null) {
            throw new BadRequestException("Please provide valid from date and to date");
        }
        return getFastSlowMovingDashboardData(fastSlowMovingDashboardRequest.getWarehouseId(),
                fastSlowMovingDashboardRequest.getFromDate(), fastSlowMovingDashboardRequest.getToDate());
    }

    /**
     * @param searchImBasicData1
     * @return
     */
    public Stream<TransactionHistoryReport> getTransactionHistoryReport(FindImBasicData1 searchImBasicData1) {
        try {
            if (searchImBasicData1.getFromCreatedOn() != null && searchImBasicData1.getToCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getFromCreatedOn(),
                        searchImBasicData1.getToCreatedOn());
                searchImBasicData1.setFromCreatedOn(dates[0]);
                searchImBasicData1.setToCreatedOn(dates[1]);
            }

            Date openingStockDateFrom = null;
            Date openingStockDateTo = null;
            Date closingStockDateFrom = null;
            Date closingStockDateTo = null;

            String itemCode = "0";
            String manufacturerName = "0";

            try {
                openingStockDateFrom = DateUtils.convertStringToDateByYYYYMMDD("2022-06-20");
                Date[] dates = DateUtils.addTimeToDatesForSearch(openingStockDateFrom, searchImBasicData1.getFromCreatedOn());
                openingStockDateFrom = dates[0];
                openingStockDateTo = DateUtils.dateSubtract(dates[1]);
                log.info("----Opening Stock----> dateFrom & dateTo---> : " + openingStockDateFrom + "," + openingStockDateTo);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            try {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getFromCreatedOn(),
                        searchImBasicData1.getToCreatedOn());
                closingStockDateFrom = dates[0];
                closingStockDateTo = dates[1];
                log.info("----Closing Stock----> dateFrom & dateTo---> : " + closingStockDateFrom + "," + closingStockDateTo);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            if (searchImBasicData1 != null) {
                if (searchImBasicData1.getItemCode() == null || searchImBasicData1.getItemCode().isEmpty()) {
                    itemCode = "0";
                }
                if (searchImBasicData1.getItemCode() != null && !searchImBasicData1.getItemCode().isEmpty() && searchImBasicData1.getItemCode().size() == 1) {
                    itemCode = searchImBasicData1.getItemCode().get(0);
                }
                if (searchImBasicData1.getItemCode() != null && searchImBasicData1.getItemCode().size() > 1) {
                    throw new RuntimeException("Item Code can be null or Single");
                }
            }

            if (searchImBasicData1 != null) {
                if (searchImBasicData1.getManufacturerName() == null) {
                    manufacturerName = "0";
                }
                if (searchImBasicData1.getManufacturerName() != null) {
                    manufacturerName = searchImBasicData1.getManufacturerName();
                }
            }

            /*
             * Pass ITM_CODE values into INVENTORY_STOCK table and fetch sum of INV_QTY+ ALLOC_QTY where BIN_CL_ID=1 and Sum by ITM_CODE
             * This is stock for 20-06-2022 (A)
             */

            /*
             * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters
             * into PUTAWAYLINE table where status_ID = 20 and IS_DELETED = 0
             * Fetch SUM of PA_CNF_QTY and group by ITM_CODE(B)
             */

            /*
             * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters into PICKUPLINE table
             * where status_ID=50 and IS_DELETED=0
             * Fetch SUM of PU_QTY and group by ITM_CODE{C}
             */

            /*
             * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters into INVENTORYMOVEMENT table
             * where MVT_TYP_ID=4, SUB_MVT_TYP_ID=1 and IS_DELETED=0
             * Fetch SUM of MVT_QTY and group by ITM_CODE(D)
             */

            // Opening stock - 3 column - E [openingStock = ((sumOfInvQty_AllocQty + sumOfPAConfirmQty + sumOfMvtQty) - sumOfPickupLineQty);]

            //--------------------------------------------------------------closing stock---------------------------------------------------------------------

            // Output Column - 7 - (E+F+H) - G [closingStock = ((openingStock + sumOfPAConfirmQty_4 + sumOfMvtQty_6) - sumOfPickupLineQty_5);]

            transactionHistoryResultRepository.SP_THR(
                    searchImBasicData1.getCompanyCodeId(),
                    searchImBasicData1.getPlantId(),
                    searchImBasicData1.getLanguageId(),
                    searchImBasicData1.getWarehouseId(),
                    itemCode,
                    manufacturerName,
                    openingStockDateFrom,
                    openingStockDateTo,
                    closingStockDateFrom,
                    closingStockDateTo);
            log.info("Report Generated successfully through Stored Procedure");

            TransactionHistoryReportSpecification specification = new TransactionHistoryReportSpecification();
            Stream<TransactionHistoryReport> inventoryStockList = transactionHistoryReportRepository.stream(specification, TransactionHistoryReport.class);
            log.info("TransactionHistoryReport -----> Output");

            return inventoryStockList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param fromCreatedOn
     * @param toCreatedOn
     * @return
     * @throws java.text.ParseException
     */
    @Transactional
    private List<FastSlowMovingDashboard> getFastSlowMovingDashboardData(String warehouseId, Date fromCreatedOn, Date toCreatedOn)
            throws java.text.ParseException {
        List<FastSlowMovingDashboard> itemDataList = new ArrayList<>();

        List<FastSlowMovingDashboard> fastMoving = new ArrayList<>();
        List<FastSlowMovingDashboard> averageMoving = new ArrayList<>();
        List<FastSlowMovingDashboard> slowMoving = new ArrayList<>();
        /*
         * Receipts - Awaiting ASN -------------------------- Pass the logged in WH_ID
         * and current date in CR_CTD_ON field in CONTAINERRECEIPT table and fetch the
         * count of records where REF_DOC_NO is Null
         */
        Date[] dates = DateUtils.addTimeToDatesForSearch(fromCreatedOn, toCreatedOn);
        fromCreatedOn = dates[0];
        toCreatedOn = dates[1];
        List<FastSlowMovingDashboard.FastSlowMovingDashboardImpl> itemData = outboundLineRepository.getFastSlowMovingDashboardData(warehouseId, fromCreatedOn, toCreatedOn);
//		log.info("FastSlowMovingDashboard itemData : " + itemData);
        if (itemData != null && !itemData.isEmpty()) {
            int splitSize = itemData.size() / 3;
            for (FastSlowMovingDashboard.FastSlowMovingDashboardImpl item : itemData) {
                FastSlowMovingDashboard data = new FastSlowMovingDashboard();
                data.setItemCode(item.getItemCode());
                data.setDeliveryQuantity(item.getDeliveryQuantity());
                data.setItemText(item.getItemText());

                if (fastMoving.size() < splitSize) {
                    data.setType("FAST");
                    fastMoving.add(data);
                } else if (fastMoving.size() == splitSize && averageMoving.size() < splitSize) {
                    data.setType("AVERAGE");
                    averageMoving.add(data);
                } else {
                    data.setType("SLOW");
                    slowMoving.add(data);
                }
            }
        }
        itemDataList.addAll(fastMoving.stream().limit(100).collect(Collectors.toList()));
        itemDataList.addAll(averageMoving.stream().limit(100).collect(Collectors.toList()));
        itemDataList.addAll(slowMoving.stream().collect(lastN(100)));
        return itemDataList;
    }

    /**
     * @param <T>
     * @param n
     * @return
     */
    public static <T> Collector<T, ?, List<T>> lastN(int n) {
        return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
            if (acc.size() == n)
                acc.pollFirst();
            acc.add(t);
        }, (acc1, acc2) -> {
            while (acc2.size() < n && !acc1.isEmpty()) {
                acc2.addFirst(acc1.pollLast());
            }
            return acc2;
        }, ArrayList::new);
    }

    //-------------------------------------------------Get all StockMovementReport---------------------------------

    /**
     * getStockMovementReports
     *
     * @return
     */
    public List<StockMovementReport> getStockMovementReports() {
        List<StockMovementReport> stockMovementReportList = stockMovementReportRepository.findAll();
        return stockMovementReportList;
    }

    //-------------------------------------------------Get all StockMovementReport New---------------------------------

    /**
     * @return
     * @throws Exception
     */
    public Stream<StockMovementReport1> findStockMovementReportNew() throws Exception {
        StockMovementReportNewSpecification spec = new StockMovementReportNewSpecification();
        Stream<StockMovementReport1> results = stockMovementReport1Repository.stream(spec, StockMovementReport1.class);
        return results;
    }

    //=======================================================Walkaroo-V3==========================================================

    public List<StorageBinDashBoardImpl> getStorageBinDashBoardCount(StorageBinDashBoardInput storageBinDashBoardInput) throws Exception {
        try {
            Long binClassId = storageBinDashBoardInput.getBinClassId() != null ? storageBinDashBoardInput.getBinClassId() : 1L;
            return inventoryV2Repository.getStorageBinDashBoardV3(
                    storageBinDashBoardInput.getCompanyCodeId(),
                    storageBinDashBoardInput.getPlantId(),
                    storageBinDashBoardInput.getLanguageId(),
                    storageBinDashBoardInput.getWarehouseId(),
                    storageBinDashBoardInput.getStorageBin(),
                    binClassId);
        } catch (Exception e) {
            log.error("Exception while storageBinDashboard Count : " + storageBinDashBoardInput);
            throw e;
        }
    }

    /**
     * @param searchBinningProductivityReport
     * @return
     * @throws java.text.ParseException
     */
    public List<BinningProductivityReport> binningProductivityReport(SearchBinningProductivityReport searchBinningProductivityReport) throws Exception {

        if (searchBinningProductivityReport.getCompanyCodeId() == null || searchBinningProductivityReport.getCompanyCodeId().isEmpty()) {
            searchBinningProductivityReport.setCompanyCodeId(null);
        }
        if (searchBinningProductivityReport.getPlantId() == null || searchBinningProductivityReport.getPlantId().isEmpty()) {
            searchBinningProductivityReport.setPlantId(null);
        }
        if (searchBinningProductivityReport.getLanguageId() == null || searchBinningProductivityReport.getLanguageId().isEmpty()) {
            searchBinningProductivityReport.setLanguageId(null);
        }
        if (searchBinningProductivityReport.getWarehouseId() == null || searchBinningProductivityReport.getWarehouseId().isEmpty()) {
            searchBinningProductivityReport.setWarehouseId(null);
        }
        if (searchBinningProductivityReport.getRefDocNo() == null || searchBinningProductivityReport.getRefDocNo().isEmpty()) {
            searchBinningProductivityReport.setRefDocNo(null);
        }
        if (searchBinningProductivityReport.getPreInboundNo() == null || searchBinningProductivityReport.getPreInboundNo().isEmpty()) {
            searchBinningProductivityReport.setPreInboundNo(null);
        }
        if (searchBinningProductivityReport.getBinner() == null || searchBinningProductivityReport.getBinner().isEmpty()) {
            searchBinningProductivityReport.setBinner(null);
        }
        if (searchBinningProductivityReport.getInboundOrderTypeId() == null || searchBinningProductivityReport.getInboundOrderTypeId().isEmpty()) {
            searchBinningProductivityReport.setInboundOrderTypeId(null);
        }
        if (searchBinningProductivityReport.getStatusId() == null || searchBinningProductivityReport.getStatusId().isEmpty()) {
            searchBinningProductivityReport.setStatusId(null);
        }
        if (searchBinningProductivityReport.getStartDate() != null && searchBinningProductivityReport.getEndDate() != null) {
            Date[] date = DateUtils.convertStringToDate(searchBinningProductivityReport.getStartDate(), searchBinningProductivityReport.getEndDate());
            searchBinningProductivityReport.setStartConfirmedOn(date[0]);
            searchBinningProductivityReport.setEndConfirmedOn(date[1]);
        }
        log.info("searchBinningProductivityReport Input: " + searchBinningProductivityReport);

        List<PickingProductivityImpl> results = putAwayLineV2Repository.findBinningProductivityReport(
                searchBinningProductivityReport.getCompanyCodeId(),
                searchBinningProductivityReport.getPlantId(),
                searchBinningProductivityReport.getLanguageId(),
                searchBinningProductivityReport.getWarehouseId(),
                searchBinningProductivityReport.getRefDocNo(),
                searchBinningProductivityReport.getPreInboundNo(),
                searchBinningProductivityReport.getBinner(),
                searchBinningProductivityReport.getInboundOrderTypeId(),
                searchBinningProductivityReport.getStatusId(),
                searchBinningProductivityReport.getStartConfirmedOn(),
                searchBinningProductivityReport.getEndConfirmedOn());

        List<BinningProductivityReport> reportResult = new ArrayList<>();
        if (results != null && !results.isEmpty()) {
            for (PickingProductivityImpl ipicking : results) {

                SearchPutAwayLineV2 searchPutAwayLine = new SearchPutAwayLineV2();
                searchPutAwayLine.setCompanyCodeId(searchBinningProductivityReport.getCompanyCodeId());
                searchPutAwayLine.setPlantId(searchBinningProductivityReport.getPlantId());
                searchPutAwayLine.setLanguageId(searchBinningProductivityReport.getLanguageId());
                searchPutAwayLine.setWarehouseId(searchBinningProductivityReport.getWarehouseId());
                searchPutAwayLine.setInboundOrderTypeId(searchBinningProductivityReport.getInboundOrderTypeId());
                searchPutAwayLine.setRefDocNumber(searchBinningProductivityReport.getRefDocNo());
                searchPutAwayLine.setPreInboundNo(searchBinningProductivityReport.getPreInboundNo());
                searchPutAwayLine.setStatusId(searchBinningProductivityReport.getStatusId());
                searchPutAwayLine.setFromConfirmedDate(searchBinningProductivityReport.getStartConfirmedOn());
                searchPutAwayLine.setToConfirmedDate(searchBinningProductivityReport.getEndConfirmedOn());
                searchPutAwayLine.setConfirmedBy(Collections.singletonList(ipicking.getAssignedPickerId()));

                List<PutAwayLineV2> putAwayLineList = putAwayLineService.findPutAwayLineV2(searchPutAwayLine);
                if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
                    for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                        BinningProductivityReport binningProductivity = new BinningProductivityReport();
                        binningProductivity.setCompanyCodeId(ipicking.getCompanyCodeId());
                        binningProductivity.setPlantId(ipicking.getPlantId());
                        binningProductivity.setLanguageId(ipicking.getLanguageId());
                        binningProductivity.setWarehouseId(ipicking.getWarehouseId());
                        binningProductivity.setCompanyDescription(ipicking.getCompanyDescription());
                        binningProductivity.setPlantDescription(ipicking.getPlantDescription());
                        binningProductivity.setWarehouseDescription(ipicking.getWarehouseDescription());
                        binningProductivity.setAssignedPickerId(ipicking.getAssignedPickerId());
                        binningProductivity.setOrders(ipicking.getOrders());
                        binningProductivity.setLeadTime(ipicking.getLeadTime());
                        binningProductivity.setAvgLeadTime(ipicking.getAvgLeadTime());
                        binningProductivity.setParts(ipicking.getParts());
                        binningProductivity.setPartsPerHr(ipicking.getPartsPerHr());
                        binningProductivity.setTotalLeadTime(ipicking.getTotalLeadTime());
                        binningProductivity.setTotalPartsPerHr(ipicking.getTotalPartsPerHr());
                        binningProductivity.setAvgTotalLeadTime(ipicking.getAvgTotalLeadTime());
                        binningProductivity.setTotalParts(ipicking.getTotalParts());
                        binningProductivity.setTotalOrders(ipicking.getTotalOrders());
                        BeanUtils.copyProperties(putAwayLine, binningProductivity, CommonUtils.getNullPropertyNames(putAwayLine));
                        reportResult.add(binningProductivity);
                    }
                } else {
                    BinningProductivityReport binningProductivity = new BinningProductivityReport();
                    binningProductivity.setCompanyCodeId(ipicking.getCompanyCodeId());
                    binningProductivity.setPlantId(ipicking.getPlantId());
                    binningProductivity.setLanguageId(ipicking.getLanguageId());
                    binningProductivity.setWarehouseId(ipicking.getWarehouseId());
                    binningProductivity.setCompanyDescription(ipicking.getCompanyDescription());
                    binningProductivity.setPlantDescription(ipicking.getPlantDescription());
                    binningProductivity.setWarehouseDescription(ipicking.getWarehouseDescription());
                    binningProductivity.setAssignedPickerId(ipicking.getAssignedPickerId());
                    binningProductivity.setOrders(ipicking.getOrders());
                    binningProductivity.setLeadTime(ipicking.getLeadTime());
                    binningProductivity.setAvgLeadTime(ipicking.getAvgLeadTime());
                    binningProductivity.setParts(ipicking.getParts());
                    binningProductivity.setPartsPerHr(ipicking.getPartsPerHr());
                    binningProductivity.setTotalLeadTime(ipicking.getTotalLeadTime());
                    binningProductivity.setTotalPartsPerHr(ipicking.getTotalPartsPerHr());
                    binningProductivity.setAvgTotalLeadTime(ipicking.getAvgTotalLeadTime());
                    binningProductivity.setTotalParts(ipicking.getTotalParts());
                    binningProductivity.setTotalOrders(ipicking.getTotalOrders());
                    reportResult.add(binningProductivity);
                }
            }
        }

        log.info("binningProductivityReport result : " + reportResult.size());
        return reportResult;
    }

    // PutAway
    public List<CharData> findPutAwayReport(FindReport findReport) throws java.text.ParseException {

        log.info("findCharData Input: " + findReport);

        // Initialize variables for storing results
        List<Object[]> results;

        if (findReport.getStartDate() != null && findReport.getEndDate() != null) {
            Date[] date = DateUtils.convertStringToDate(findReport.getStartDate(), findReport.getEndDate());
            findReport.setStartConfirmedOn(date[0]);
            findReport.setEndConfirmedOn(date[1]);
        }

        // Check if leadTime is requested or quantity
        if (findReport.getLeadTime() == 1 && findReport.getQuantity() == 0) {
            results = putAwayLineV2Repository.getLeadTime(findReport.getStartConfirmedOn(), findReport.getEndConfirmedOn());
        } else if (findReport.getQuantity() == 1 && findReport.getLeadTime() == 0) {
            results = putAwayLineV2Repository.getQuantity(findReport.getStartConfirmedOn(), findReport.getEndConfirmedOn());
        } else if (findReport.getAverageLeadTime() == 1 && findReport.getLeadTime() == 0 && findReport.getQuantity() == 0) {
            results = putAwayLineV2Repository.getAverageLeadTime(findReport.getStartConfirmedOn(), findReport.getEndConfirmedOn());
        } else if (findReport.getProductivity() == 1 && findReport.getLeadTime() == 0 && findReport.getQuantity() == 0 && findReport.getAverageLeadTime() == 0) {
            results = putAwayLineV2Repository.getProductivity(findReport.getStartConfirmedOn(), findReport.getEndConfirmedOn());
        } else {
            throw new IllegalArgumentException("Invalid input: either leadTime or quantity or averageLeadTime or productivity must be 1");
        }


        CharData charData = new CharData();
        List<SeriesData> seriesList = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        // Process the results
        results.stream().forEach(result -> {
            String assignUser = (String) result[0];                // AssignUser
            Date pickDate = (Date) result[1];                     // PickDate
            Integer value = ((Number) result[2]).intValue();     // Value

            String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(pickDate);

            if (!categories.contains(formattedDate)) {
                categories.add(formattedDate);
            }

            SeriesData seriesData = seriesList.stream()
                    .filter(s -> s.getName().equals(assignUser))
                    .findFirst()
                    .orElse(null);

            if (seriesData == null) {
                seriesData = new SeriesData();
                seriesData.setName(assignUser);
                seriesData.setData(new ArrayList<>());
                seriesList.add(seriesData);
            }

            seriesData.getData().add(value);
        });

        for (SeriesData seriesData : seriesList) {
            while (seriesData.getData().size() < categories.size()) {
                seriesData.getData().add(0);
            }
        }
        charData.setSeries(seriesList);
        charData.setCategories(categories);

        return Collections.singletonList(charData);
    }

    //=========================================TRANSACTION REPORT=====================================================

    /**
     *
     * @param request input
     * @return
     * @throws Exception
     */
    public List<TransactionReportRes> findTransactionReport(TransactionReport request) throws Exception {

        log.info("Transaction Report Input Values ------------> " + request);

        List<InventoryV2> inventoryList =
                inventoryV2Repository.getItemBarcodeInvQty(request.getCompanyId(), request.getPlantId(),
                        request.getWarehouseId(), request.getItemCode(), request.getHuNumber());

        if (inventoryList.isEmpty()) {
            throw new Exception("No inventory records found ");
        }

        List<String> barcodeIds = inventoryList.stream().map(InventoryV2::getBarcodeId).distinct().collect(Collectors.toList());
        log.info("Barcode's List ----------> " + barcodeIds);

        List<List<String>> chunks = partition(barcodeIds, 2000);

        List<TransactionReportRes> result = new ArrayList<>();
        for (List<String> chunk : chunks) {
            /* Fetch GR & Delivery in bulk */
            List<GrLineV2> grLines =
                    grLineV2Repository.findGrLineBulk(nullIfEmpty(request.getItemCode()), chunk);

            List<DeliveryConfirmation> deliveries =
                    deliveryConfirmationRepository.findDLVBulk(
                            nullIfEmpty(request.getItemCode()), chunk);

            /* Convert to Map */
            Map<String, GrLineV2> grLineMap =
                    grLines.stream()
                            .collect(Collectors.toMap(GrLineV2::getBarcodeId, g -> g, (a, b) -> a));

            Map<String, DeliveryConfirmation> deliveryMap =
                    deliveries.stream().collect(Collectors.toMap(DeliveryConfirmation::getHuSerialNo, d -> d, (a, b) -> a));

            for (InventoryV2 inventory : inventoryList) {

                TransactionReportRes res = new TransactionReportRes();
                res.setItemCode(inventory.getItemCode());
                res.setHuNumber(inventory.getBarcodeId());
                res.setInvQty(inventory.getInventoryQuantity());

                GrLineV2 gr = grLineMap.get(inventory.getBarcodeId());
                if (gr != null) {
                    res.setInboundDocNo(gr.getRefDocNumber());
                    res.setInboundQty(gr.getOrderQty());
                }

                DeliveryConfirmation dlv = deliveryMap.get(inventory.getBarcodeId());
                if (dlv != null) {
                    res.setOutboundDocNo(dlv.getOutbound());
                    res.setOutboundQty(dlv.getPickedQty());
                }

                double inbound = res.getInboundQty() != null ? res.getInboundQty() : 0.0;
                double outbound = res.getOutboundQty() != null ? res.getOutboundQty() : 0.0;

                res.setExpectedInvQty(inbound - outbound);
                res.setVariance(res.getExpectedInvQty() - res.getInvQty());

                result.add(res);
            }
        }
        return result;
    }

    /* Utility */
    private List<String> nullIfEmpty(List<String> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }


}
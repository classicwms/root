package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.inbound.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.GrHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.report.*;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.StockMovementReportNewSpecification;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ReportsService extends BaseService {

    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;
    @Autowired
    private GrHeaderRepository grHeaderRepository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;
    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;
    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;
    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;
    @Autowired
    private GrLineV2Repository grLineV2Repository;
    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;
    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    private InboundOrderLinesV2Repository inboundOrderLinesV2Repository;


    @Autowired
    InventoryService inventoryService;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    InboundHeaderService inboundHeaderService;

    @Autowired
    InboundLineService inboundLineService;

    @Autowired
    MastersService mastersService;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    StagingHeaderService stagingHeaderService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    StorageBinRepository storagebinRepository;

    @Autowired
    ImBasicData1Repository imbasicdata1Repository;
    @Autowired
    InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    InboundLineRepository inboundLineRepository;

    @Autowired
    InventoryStockRepository inventoryStockRepository;
    @Autowired
    TransactionHistoryReportRepository transactionHistoryReportRepository;

    @Autowired
    PutAwayLineRepository putAwayLineRepository;

    @Autowired
    OutboundLineRepository outboundLineRepository;

    @Autowired
    GrHeaderService grHeaderService;


    /**
     * Stock Report ---------------------
     *
     * @param warehouseId
     * @param itemCode
     * @param itemText
     * @param stockTypeId
     * @return
     */
//	public Page<StockReport> getStockReport(List<String> warehouseId, List<String> itemCode, String itemText,
//			String stockTypeText, Integer pageNo, Integer pageSize, String sortBy) {
//		if (warehouseId == null) {
//			throw new BadRequestException("WarehouseId can't be blank.");
//		}
//
//		if (stockTypeText == null) {
//			throw new BadRequestException("StockTypeText can't be blank.");
//		}
//
//		try {
//			SearchInventory searchInventory = new SearchInventory();
//			searchInventory.setWarehouseId(warehouseId);
//
//			if (itemCode != null) {
//				searchInventory.setItemCode(itemCode);
//			}
//
//			if (itemText != null) {
//				searchInventory.setDescription(itemText);
//			}
//
//			List<Long> stockTypeIdList = null;
//			if (stockTypeText.equalsIgnoreCase("ALL")) {
//				stockTypeIdList = Arrays.asList(1L, 7L);
//			} else if (stockTypeText.equalsIgnoreCase("ON HAND")) {
//				stockTypeIdList = Arrays.asList(1L);
//			} else if (stockTypeText.equalsIgnoreCase("DAMAGED")) {
//				stockTypeIdList = Arrays.asList(1L);
//			} else if (stockTypeText.equalsIgnoreCase("HOLD")) {
//				stockTypeIdList = Arrays.asList(7L);
//			}
//
//			searchInventory.setStockTypeId(stockTypeIdList);
//			log.info("searchInventory : " + searchInventory);
//
//			Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
//			Page<Inventory> inventoryList = inventoryService.findInventory(searchInventory, pageNo, pageSize, sortBy);
//			log.info("inventoryList : " + inventoryList);
//
//			List<StockReport> stockReportList = new ArrayList<>();
//			Set<String> uniqueItemCode = new HashSet<>();
//			for (Inventory inventory : inventoryList) {
//				if (uniqueItemCode.add(inventory.getItemCode())) {
//					StockReport stockReport = new StockReport();
//
//					// WH_ID
//					stockReport.setWarehouseId(inventory.getWarehouseId());
//
//					// ITM_CODE
//					stockReport.setItemCode(inventory.getItemCode());
//
//					/*
//					 * MFR_SKU -------------- Pass the fetched ITM_CODE values in IMBASICDATA1 table
//					 * and fetch MFR_SKU values
//					 */
//					ImBasicData1 imBasicData1 = imbasicdata1Repository.findByItemCodeAndWarehouseIdAndDeletionIndicator(
//							inventory.getItemCode(), inventory.getWarehouseId(), 0L);
//					if (imBasicData1 != null) {
//						stockReport.setManufacturerSKU(imBasicData1.getManufacturerPartNo());
//						stockReport.setItemText(imBasicData1.getDescription());
//					} else {
//						stockReport.setManufacturerSKU("");
//						stockReport.setItemText("");
//					}
//
//					if (stockTypeText.equalsIgnoreCase("ALL")) {
//						/*
//						 * For onhand, damageqty -> stock_type_id is 1 For Hold -> stok_type_id is 7
//						 */
//						// ON HAND
//						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZC", "ZT");
//						double ON_HAND_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
//								storageSectionIds);
//						stockReport.setOnHandQty(ON_HAND_INVQTY);
//
//						// DAMAGED
//						storageSectionIds = Arrays.asList("ZD");
//						double DAMAGED_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
//								storageSectionIds);
//						stockReport.setDamageQty(DAMAGED_INVQTY);
//
//						// HOLD
//						storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");
//						double HOLD_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 7L,
//								storageSectionIds);
//						stockReport.setHoldQty(HOLD_INVQTY);
//
//						// Available Qty
//						double AVAILABLE_QTY = ON_HAND_INVQTY + DAMAGED_INVQTY + HOLD_INVQTY;
//						stockReport.setAvailableQty(AVAILABLE_QTY);
//
//						if (AVAILABLE_QTY != 0) {
//							stockReportList.add(stockReport);
//						}
//						log.info("ALL-------stockReport:" + stockReport);
//					} else if (stockTypeText.equalsIgnoreCase("ON HAND")) {
//						// stock_type_id = 1
//						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZC", "ZT");
//						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
//								storageSectionIds);
//						if (INV_QTY != 0) {
//							stockReport.setOnHandQty(INV_QTY);
//							stockReport.setDamageQty(0D);
//							stockReport.setHoldQty(0D);
//							stockReport.setAvailableQty(INV_QTY);
//							log.info("ON HAND-------stockReport:" + stockReport);
//							stockReportList.add(stockReport);
//						}
//					} else if (stockTypeText.equalsIgnoreCase("DAMAGED")) {
//						// stock_type_id = 1
//						List<String> storageSectionIds = Arrays.asList("ZD");
//						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
//								storageSectionIds);
//
//						if (INV_QTY != 0) {
//							stockReport.setDamageQty(INV_QTY);
//							stockReport.setOnHandQty(0D);
//							stockReport.setHoldQty(0D);
//							stockReport.setAvailableQty(INV_QTY);
//
//							log.info("DAMAGED-------stockReport:" + stockReport);
//							stockReportList.add(stockReport);
//						}
//					} else if (stockTypeText.equalsIgnoreCase("HOLD")) {
//						// STCK_TYP_ID = 7
//						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");
//						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 7L,
//								storageSectionIds);
//
//						if (INV_QTY != 0) {
//							stockReport.setHoldQty(INV_QTY);
//							stockReport.setOnHandQty(0D);
//							stockReport.setDamageQty(0D);
//							stockReport.setAvailableQty(INV_QTY);
//							log.info("HOLD-------stockReport:" + stockReport);
//							stockReportList.add(stockReport);
//						}
//					}
//				}
//			}
//			log.info("stockReportList : " + stockReportList);
//			final Page<StockReport> page = new PageImpl<>(stockReportList, pageable, inventoryList.getTotalElements());
//			return page;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

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
            double sumOfNoOfBags = 0;
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

                double noOfBags = 0;
                if (inboundLine.getNoBags() != null) {
                    noOfBags = inboundLine.getNoBags();
                    receipt.setNoOfBags(noOfBags);
                    sumOfNoOfBags += noOfBags;
                    log.info("expQty------#--> : " + noOfBags);
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

            //---------------------------------------------------------------------------------------
            // Group by SKU and sum the fields
            Map<String, Receipt> groupedReceipts = new LinkedHashMap<>();

            for (Receipt r : receiptList) {
                groupedReceipts.compute(r.getSku(), (sku, existing) -> {
                    if (existing == null) {
                        Receipt newReceipt = new Receipt();
                        newReceipt.setSku(r.getSku());
                        newReceipt.setDescription(r.getDescription());
                        newReceipt.setMfrSku(r.getMfrSku());
                        newReceipt.setExpectedQty(round1(r.getExpectedQty()));
                        newReceipt.setAcceptedQty(round1(r.getAcceptedQty()));
                        newReceipt.setDamagedQty(round1(r.getDamagedQty()));
//                        newReceipt.setExpectedQty(r.getExpectedQty() != null ? r.getExpectedQty() : 0.0);
//                        newReceipt.setAcceptedQty(r.getAcceptedQty() != null ? r.getAcceptedQty() : 0.0);
//                        newReceipt.setDamagedQty(r.getDamagedQty() != null ? r.getDamagedQty() : 0.0);
                        newReceipt.setMissingORExcess(r.getMissingORExcess() != null ? r.getMissingORExcess() : 0.0);
                        newReceipt.setStatus(r.getStatus());
                        newReceipt.setNoOfBags(r.getNoOfBags() != null ? r.getNoOfBags() : 0.0);
                        return newReceipt;
                    } else {
                        existing.setExpectedQty(round1(existing.getExpectedQty()) + round1(r.getExpectedQty()));
                        existing.setAcceptedQty(round1(existing.getAcceptedQty()) + round1(r.getAcceptedQty()));
                        existing.setDamagedQty(round1(existing.getDamagedQty()) + round1(r.getDamagedQty()));
//                        existing.setExpectedQty((existing.getExpectedQty() != null ? existing.getExpectedQty() : 0.0) + (r.getExpectedQty() != null ? r.getExpectedQty() : 0.0));
//                        existing.setAcceptedQty((existing.getAcceptedQty() != null ? existing.getAcceptedQty() : 0.0) + (r.getAcceptedQty() != null ? r.getAcceptedQty() : 0.0));
//                        existing.setDamagedQty((existing.getDamagedQty() != null ? existing.getDamagedQty() : 0.0) + (r.getDamagedQty() != null ? r.getDamagedQty() : 0.0));
                        existing.setMissingORExcess((existing.getMissingORExcess() != null ? existing.getMissingORExcess() : 0.0) + (r.getMissingORExcess() != null ? r.getMissingORExcess() : 0.0));
                        existing.setNoOfBags((existing.getNoOfBags() != null ? existing.getNoOfBags() : 0.0) + (r.getNoOfBags() != null ? r.getNoOfBags() : 0.0));
                        return existing;
                    }
                });
            }
//---------------------------------------------------------------------------------------
            receiptHeader.setExpectedQtySum(round1(sumTotalOfExpectedQty));
            receiptHeader.setAcceptedQtySum(round1(sumTotalOfAccxpectedQty));
            receiptHeader.setDamagedQtySum(round1(sumTotalOfDamagedQty));
            receiptHeader.setMissingORExcessSum(sumTotalOfMissingORExcess);
            receiptHeader.setNoOfBagsSum(sumOfNoOfBags);

            receiptConfimation.setReceiptHeader(receiptHeader);
            //  receiptConfimation.setReceiptList(receiptList);
            receiptConfimation.setReceiptList(new ArrayList<>(groupedReceipts.values()));
            log.info("receiptConfimation : " + receiptConfimation);
            return receiptConfimation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * processInventory
     *
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @return
     */
    private double getInventoryQty(String warehouseId, String itemCode, Long stockTypeId,
                                   List<String> storageSectionIds) {
        try {
            List<Inventory> stBinInventoryList = inventoryService.getInventoryForStockReport(warehouseId, itemCode,
                    stockTypeId);
            if (!stBinInventoryList.isEmpty()) {
                List<String> stBins = stBinInventoryList.stream().map(Inventory::getStorageBin)
                        .collect(Collectors.toList());
                log.info("stBins : " + stBins);
                log.info("storageSectionIds : " + storageSectionIds);

                List<StorageBin> storagebinList = storagebinRepository
                        .findByStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
                                stBins, storageSectionIds, 0, 0, 0L);
                log.info("storagebinList : " + storagebinList);
                if (storagebinList != null && !storagebinList.isEmpty()) {
                    List<String> storageBinList = storagebinList.stream().map(StorageBin::getStorageBin)
                            .collect(Collectors.toList());
                    log.info("inventory-params-----> : wh_id:" + warehouseId + "," + ",itemCode:" + itemCode
                            + ",storageBinList:" + storageBinList + ",stockTypeId: " + stockTypeId);

                    List<Long> inventoryQtyCountList = inventoryService.getInventoryQtyCount(warehouseId, itemCode,
                            storageBinList, stockTypeId);
                    log.info("inventoryList--------> : "
                            + inventoryQtyCountList.stream().mapToLong(Long::longValue).sum());

                    long qty = inventoryQtyCountList.stream().mapToLong(Long::longValue).sum();
                    log.info("inventoryList----qty----> : " + qty);

                    return qty;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
     *
     * @param warehouseId
     * @param itemCode
     * @param fromCreatedOn
     * @param toCreatedOn
     * @return
     */
//	public List<InventoryStock> getInventoryStockReport(FindImBasicData1 searchImBasicData1) {
//		try {
//
//			if (searchImBasicData1.getFromCreatedOn() != null && searchImBasicData1.getFromCreatedOn() != null) {
//				Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getFromCreatedOn(),
//						searchImBasicData1.getToCreatedOn());
//				searchImBasicData1.setFromCreatedOn(dates[0]);
//				searchImBasicData1.setToCreatedOn(dates[1]);
//			}
//
//			ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
//			List<ImBasicData1> resultsImBasicData1 = imbasicdata1Repository.findAll(spec);
//			log.info("resultsImBasicData1 : " + resultsImBasicData1);
//			List<InventoryStock> inventoryStockList = new ArrayList<>();
//
//			// 3. Pass WH_ID in IMBASICDATA1 table and fetch the ITM_CODES
//			Set<String> itemCodeSet = resultsImBasicData1.parallelStream().map(ImBasicData1::getItemCode).collect(Collectors.toSet());
//			log.info("itemCodeSet : " + itemCodeSet);
//
//			itemCodeSet.parallelStream().forEach(i -> {
//				InventoryStock inventoryStock = new InventoryStock();
//				inventoryStock.setWarehouseId(searchImBasicData1.getWarehouseId());
//				inventoryStock.setItemCode(i);
//
//				/*
//				 * Pass ITM_CODE values into INVENTORY_STOCK table and fetch sum of INV_QTY+ ALLOC_QTY where BIN_CL_ID=1 and Sum by ITM_CODE
//				 * This is stock for 20-06-2022 (A)
//				 */
//				Double sumOfInvQty_AllocQty = inventoryStockRepository.findSumOfInventoryQtyAndAllocQty(searchImBasicData1.getItemCode());
//
//				sumOfInvQty_AllocQty = (sumOfInvQty_AllocQty != null) ? sumOfInvQty_AllocQty : 0D;
//				log.info("INVENTORY_STOCK : " + sumOfInvQty_AllocQty);
//
//				/*
//				 * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters
//				 * into PUTAWAYLINE table where status_ID = 20 and IS_DELETED = 0
//				 * Fetch SUM of PA_CNF_QTY and group by ITM_CODE(B)
//				 */
//				Date dateFrom = null;
//				Date dateTo = null;
//				try {
//					dateFrom = DateUtils.convertStringToDateByYYYYMMDD ("2022-06-20");
//					Date[] dates = DateUtils.addTimeToDatesForSearch(dateFrom, searchImBasicData1.getFromCreatedOn());
//					dateFrom = dates[0];
//					dateTo = dates[1];
//				} catch (java.text.ParseException e) {
//					e.printStackTrace();
//				}
//
//				Double sumOfPAConfirmQty = putAwayLineRepository.findSumOfPAConfirmQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfPAConfirmQty = (sumOfPAConfirmQty != null) ? sumOfPAConfirmQty : 0D;
//				log.info("PUTAWAYLINE : " + sumOfPAConfirmQty);
//
//				/*
//				 * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters into PICKUPLINE table
//				 * where status_ID=50 and IS_DELETED=0
//				 * Fetch SUM of PU_QTY and group by ITM_CODE{C}
//				 */
//				Double sumOfPickupLineQty = pickupLineRepository.findSumOfPickupLineQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfPickupLineQty = (sumOfPickupLineQty != null) ? sumOfPickupLineQty : 0D;
//				log.info("PICKUPLINE : " + sumOfPickupLineQty);
//
//				/*
//				 * Pass ITM_CODE values and From date 20-06-2022 and to date as From date of selection parameters into INVENTORYMOVEMENT table
//				 * where MVT_TYP_ID=4, SUB_MVT_TYP_ID=1 and IS_DELETED=0
//				 * Fetch SUM of MVT_QTY and group by ITM_CODE(D)
//				 */
//				Double sumOfMvtQty = inventoryMovementRepository.findSumOfMvtQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfMvtQty = (sumOfMvtQty != null) ? sumOfMvtQty : 0D;
//				log.info("INVENTORYMOVEMENT : " + sumOfMvtQty);
//
//				// Opening stock - 3 column - E
//				Double openingStock = ((sumOfInvQty_AllocQty + sumOfPAConfirmQty + sumOfPickupLineQty) - sumOfMvtQty);
//				log.info("openingStock : " + openingStock);
//				inventoryStock.setOpeningStock(openingStock);
//
//				try {
//					Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getFromCreatedOn(),
//							searchImBasicData1.getToCreatedOn());
//					dateFrom = dates[0];
//					dateTo = dates[1];
//					log.info("----SecII----> dateFrom & dateTo---> : " + dateFrom + "," + dateTo);
//				} catch (java.text.ParseException e) {
//					e.printStackTrace();
//				}
//
//				// Output Column - 4 - PUTAWAYLINE
//				Double sumOfPAConfirmQty_4 = putAwayLineRepository.findSumOfPAConfirmQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfPAConfirmQty_4 = (sumOfPAConfirmQty_4 != null) ? sumOfPAConfirmQty_4 : 0D;
//				log.info("PUTAWAYLINE_4 : " + sumOfPAConfirmQty_4);
//				inventoryStock.setInboundQty(sumOfPAConfirmQty_4);
//
//				// Output Column - 5 - PICKUPLINE
//				Double sumOfPickupLineQty_5 = pickupLineRepository.findSumOfPickupLineQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfPickupLineQty_5 = (sumOfPickupLineQty_5 != null) ? sumOfPickupLineQty_5 : 0D;
//				log.info("PICKUPLINE_5 : " + sumOfPickupLineQty_5);
//				inventoryStock.setOutboundQty(sumOfPickupLineQty_5);
//
//				// Output Column - 6 -INVENTORYMOVEMENT
//				Double sumOfMvtQty_6 = inventoryMovementRepository.findSumOfMvtQty (searchImBasicData1.getItemCode(), dateFrom, dateTo);
//				sumOfMvtQty_6 = (sumOfMvtQty_6 != null) ? sumOfMvtQty_6 : 0D;
//				log.info("INVENTORYMOVEMENT_6 : " + sumOfMvtQty_6);
//				inventoryStock.setStockAdjustmentQty(sumOfMvtQty_6);
//
//				// Output Column - 7 - (E+F+H) - G
//				Double closingStock = ((openingStock + sumOfPAConfirmQty_4 + sumOfMvtQty_6) - sumOfPickupLineQty_5);
//				log.info("closingStock : " + closingStock);
//				inventoryStock.setClosingStock(closingStock);
//
//				inventoryStockList.add(inventoryStock);
//			});
//			return inventoryStockList;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}


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

    // Notifications Dashboard
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

    //---------------------------------------------Inbound Reversal------------------------------------------------------------


    public void inboundReversal(String companyCodeId, String plantId, String warehouseId,
                                String refDocNumber, String preInboundNo) {

        if (refDocNumber != null && preInboundNo != null) {

            log.info("Starting deletion process for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);

            List<PutAwayLineV2> putAwayLine = putAwayLineV2Repository.findByRefDocNumberAndPreInboundNo(refDocNumber, preInboundNo);
            if (!putAwayLine.isEmpty()) {
                log.warn("Inbound already confirmed for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);
                throw new RuntimeException("Already Inbound confirmed for the given RefDocNumber and PreInboundNo");
            }

            //delete ibOrder2 and ibOrderLines2
//            inboundOrderReversal(refDocNumber);

            log.info("Checking existence of PreInboundHeader...");
            PreInboundHeaderEntityV2 preInboundHeader = preInboundHeaderV2Repository.findByRefDocNumberAndPreInboundNo(refDocNumber, preInboundNo);

            if (preInboundHeader != null) {
                log.info("Deleting all related records for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);
                preInboundHeaderV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                preInboundLineV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                inboundHeaderV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                inboundLineV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                stagingHeaderV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                stagingLineV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                grHeaderV2Repository.softDeleteByRefDocNo(refDocNumber, preInboundNo);
                log.info("Delete completed for header, line, staging, and GR header records.");
            } else {
                log.warn("PreInboundHeader not found for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);
            }

            List<GrLineV2> grLine = grLineV2Repository.findByRefDocNumberAndPreInboundNo(refDocNumber, preInboundNo);

            if (!grLine.isEmpty()) {
                log.info("Processing GR lines for deletion and inventory adjustment...");
                for (GrLineV2 grLineV2 : grLine) {

                    putAwayHeaderV2Repository.softDeleteByRefDocNo(grLineV2.getRefDocNumber(), grLineV2.getPreInboundNo(), grLineV2.getBarcodeId());
                    grLineV2Repository.softDeleteByRefDocNo(grLineV2.getRefDocNumber(), grLineV2.getPreInboundNo(), grLineV2.getBarcodeId());
                    log.info("Deleted GR Line and PutAwayHeader for BarcodeId: {}", grLineV2.getBarcodeId());

                    InventoryV2 inventory = inventoryV2Repository.findInventoryId(
                            grLineV2.getCompanyCode(),
                            grLineV2.getPlantId(),
                            grLineV2.getLanguageId(),
                            grLineV2.getWarehouseId(),
                            grLineV2.getItemCode(),
                            grLineV2.getBarcodeId()
                    );

                    if (inventory != null) {
                        Double updatedQty = inventory.getInventoryQuantity() - grLineV2.getOrderQty();
                        log.info("Updating inventory for ItemCode: {} | OldQty: {} | OrderQty: {} | NewQty: {}",
                                grLineV2.getItemCode(), inventory.getInventoryQuantity(), grLineV2.getOrderQty(), updatedQty);

                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        inventoryV2.setInventoryQuantity(updatedQty);
                        inventoryV2.setReferenceField4(updatedQty);
                        inventoryV2Repository.save(inventoryV2);
                        log.info("Inventory updated and saved for BarcodeId: {}", grLineV2.getBarcodeId());
                    } else {
                        log.warn("Inventory not found for BarcodeId: {}", grLineV2.getBarcodeId());
                    }
                }
            }
//            else {
//                log.warn("GR Header not found for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);
//            }

            log.info("Completed deletion and inventory adjustment for RefDocNumber: {} and PreInboundNo: {}", refDocNumber, preInboundNo);

        } else {
            log.error("RefDocNumber and PreInboundNo cannot be null");
            throw new RuntimeException("RefDocNumber and PreInboundNo cannot be null");
        }

    }


    public void inboundOrderReversal(String refDocNumber) {

        List<InboundOrderV2> inboundOrderList = inboundOrderV2Repository.findByRefDocumentNo(refDocNumber);

        if (inboundOrderList.isEmpty()) {
            throw new RuntimeException("No inbound orders found for RefDocNumber");
        }
        for (InboundOrderV2 order : inboundOrderList) {
            List<InboundOrderLinesV2> inboundOrderLines = inboundOrderLinesV2Repository.findByOrderIdAndInboundOrderHeaderId(
                    order.getOrderId(), order.getInboundOrderHeaderId());

            if (inboundOrderLines != null) {
                inboundOrderLinesV2Repository.deleteAll(inboundOrderLines);
            }
        }
        inboundOrderV2Repository.deleteAll(inboundOrderList);
        log.info("Deleted all InboundOrders for RefDocNumber: {}", refDocNumber);
    }
    public List<BarcodeGeneration> postBarcode(List<AddBarcodeGeneration> barcode){

        List<BarcodeGeneration> generations = new ArrayList<>();

        for(AddBarcodeGeneration barcodeGeneration : barcode) {
            BarcodeGeneration barcode1 = new BarcodeGeneration();

            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextNumberRange = idmasterService.getNextNumberRange(29L, barcodeGeneration.getWarehouseId(), barcodeGeneration.getCompanyCodeId(), barcodeGeneration.getPlantId(), barcodeGeneration.getLanguageId(), authTokenForIDMasterService.getAccess_token());
            barcode1.setBarcodeId(nextNumberRange);

            barcode1.setCompanyCodeId(barcodeGeneration.getCompanyCodeId());
            barcode1.setPlantId(barcodeGeneration.getPlantId());
            barcode1.setWarehouseId(barcodeGeneration.getWarehouseId());
            barcode1.setItemCode(barcodeGeneration.getItemCode());
            barcode1.setItemDescription(barcodeGeneration.getItemDescription());
            barcode1.setWeight(barcodeGeneration.getWeight());
            generations.add(barcode1);
            log.info("new BarcodeGenerated" + generations);


        }
        return generations;
    }
}

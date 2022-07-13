package com.tekclover.wms.api.transaction.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundLine;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.ContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.SearchContainerReceipt;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundLineReport;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.report.Dashboard;
import com.tekclover.wms.api.transaction.model.report.InventoryReport;
import com.tekclover.wms.api.transaction.model.report.MetricsSummary;
import com.tekclover.wms.api.transaction.model.report.MobileDashboard;
import com.tekclover.wms.api.transaction.model.report.OrderStatusReport;
import com.tekclover.wms.api.transaction.model.report.Receipt;
import com.tekclover.wms.api.transaction.model.report.ReceiptConfimationReport;
import com.tekclover.wms.api.transaction.model.report.ReceiptHeader;
import com.tekclover.wms.api.transaction.model.report.SearchOrderStatusReport;
import com.tekclover.wms.api.transaction.model.report.ShipmentDeliveryReport;
import com.tekclover.wms.api.transaction.model.report.ShipmentDeliverySummary;
import com.tekclover.wms.api.transaction.model.report.ShipmentDeliverySummaryReport;
import com.tekclover.wms.api.transaction.model.report.ShipmentDispatch;
import com.tekclover.wms.api.transaction.model.report.ShipmentDispatchHeader;
import com.tekclover.wms.api.transaction.model.report.ShipmentDispatchList;
import com.tekclover.wms.api.transaction.model.report.ShipmentDispatchSummaryReport;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport;
import com.tekclover.wms.api.transaction.model.report.StockReport;
import com.tekclover.wms.api.transaction.model.report.SummaryMetrics;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.StorageBinRepository;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportsService extends BaseService {

	@Autowired
	InventoryService inventoryService;

	@Autowired
	InventoryMovementService inventoryMovementService;

	@Autowired
	PreInboundHeaderService preInboundHeaderService;

	@Autowired
	InboundHeaderService inboundHeaderService;

	@Autowired
	InboundLineService inboundLineService;

	@Autowired
	OutboundHeaderService outboundHeaderService;

	@Autowired
	OutboundLineService outboundLineService;

	@Autowired
	MastersService mastersService;

	@Autowired
	AuthTokenService authTokenService;

	@Autowired
	ContainerReceiptService containerReceiptService;

	@Autowired
	StagingHeaderService stagingHeaderService;

	@Autowired
	PutAwayHeaderService putAwayHeaderService;

	@Autowired
	PickupHeaderService pickupHeaderService;

	@Autowired
	QualityHeaderService qualityHeaderService;

	@Autowired
	StorageBinRepository storagebinRepository;

	@Autowired
	ImBasicData1Repository imbasicdata1Repository;

	@Autowired
	OutboundHeaderRepository outboundHeaderRepository;

	@Autowired
	OutboundLineRepository outboundLineRepository;

	@Autowired
	InventoryMovementRepository inventoryMovementRepository;

	@Autowired
	InventoryRepository inventoryRepository;

	@Autowired
	WorkBookService workBookService;

	/**
	 * Stock Report ---------------------
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param itemText
	 * @param stockTypeId
	 * @return
	 */
	public Page<StockReport> getStockReport(List<String> warehouseId, List<String> itemCode, String itemText,
			String stockTypeText, Integer pageNo, Integer pageSize, String sortBy) {
		if (warehouseId == null) {
			throw new BadRequestException("WarehouseId can't be blank.");
		}

		if (stockTypeText == null) {
			throw new BadRequestException("StockTypeText can't be blank.");
		}

		try {
			SearchInventory searchInventory = new SearchInventory();
			searchInventory.setWarehouseId(warehouseId);

			if (itemCode != null) {
				searchInventory.setItemCode(itemCode);
			}

			if (itemText != null) {
				searchInventory.setDescription(itemText);
			}

			List<Long> stockTypeIdList = null;
			if (stockTypeText.equalsIgnoreCase("ALL")) {
				stockTypeIdList = Arrays.asList(1L, 7L);
			} else if (stockTypeText.equalsIgnoreCase("ON HAND")) {
				stockTypeIdList = Arrays.asList(1L);
			} else if (stockTypeText.equalsIgnoreCase("DAMAGED")) {
				stockTypeIdList = Arrays.asList(1L);
			} else if (stockTypeText.equalsIgnoreCase("HOLD")) {
				stockTypeIdList = Arrays.asList(7L);
			}

			searchInventory.setStockTypeId(stockTypeIdList);
			log.info("searchInventory : " + searchInventory);

			Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
			Page<Inventory> inventoryList = inventoryService.findInventory(searchInventory, pageNo, pageSize, sortBy);
			log.info("inventoryList : " + inventoryList);

			List<StockReport> stockReportList = new ArrayList<>();
			Set<String> uniqueItemCode = new HashSet<>();
			for (Inventory inventory : inventoryList) {
				if (uniqueItemCode.add(inventory.getItemCode())) {
					StockReport stockReport = new StockReport();

					// WH_ID
					stockReport.setWarehouseId(inventory.getWarehouseId());

					// ITM_CODE
					stockReport.setItemCode(inventory.getItemCode());

					/*
					 * MFR_SKU -------------- Pass the fetched ITM_CODE values in IMBASICDATA1 table
					 * and fetch MFR_SKU values
					 */
					ImBasicData1 imBasicData1 = imbasicdata1Repository.findByItemCodeAndWarehouseIdAndDeletionIndicator(
							inventory.getItemCode(), inventory.getWarehouseId(), 0L);
					if (imBasicData1 != null) {
						stockReport.setManufacturerSKU(imBasicData1.getManufacturerPartNo());
						stockReport.setItemText(imBasicData1.getDescription());
					} else {
						stockReport.setManufacturerSKU("");
						stockReport.setItemText("");
					}

					if (stockTypeText.equalsIgnoreCase("ALL")) {
						/*
						 * For onhand, damageqty -> stock_type_id is 1 For Hold -> stok_type_id is 7
						 */
						// ON HAND
						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZC", "ZT");
						double ON_HAND_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
								storageSectionIds);
						stockReport.setOnHandQty(ON_HAND_INVQTY);

						// DAMAGED
						storageSectionIds = Arrays.asList("ZD");
						double DAMAGED_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
								storageSectionIds);
						stockReport.setDamageQty(DAMAGED_INVQTY);

						// HOLD
						storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");
						double HOLD_INVQTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 7L,
								storageSectionIds);
						stockReport.setHoldQty(HOLD_INVQTY);

						// Available Qty
						double AVAILABLE_QTY = ON_HAND_INVQTY + DAMAGED_INVQTY + HOLD_INVQTY;
						stockReport.setAvailableQty(AVAILABLE_QTY);

						if (AVAILABLE_QTY != 0) {
							stockReportList.add(stockReport);
						}
						log.info("ALL-------stockReport:" + stockReport);
					} else if (stockTypeText.equalsIgnoreCase("ON HAND")) {
						// stock_type_id = 1
						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZC", "ZT");
						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
								storageSectionIds);
						if (INV_QTY != 0) {
							stockReport.setOnHandQty(INV_QTY);
							stockReport.setDamageQty(0D);
							stockReport.setHoldQty(0D);
							stockReport.setAvailableQty(INV_QTY);
							log.info("ON HAND-------stockReport:" + stockReport);
							stockReportList.add(stockReport);
						}
					} else if (stockTypeText.equalsIgnoreCase("DAMAGED")) {
						// stock_type_id = 1
						List<String> storageSectionIds = Arrays.asList("ZD");
						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 1L,
								storageSectionIds);

						if (INV_QTY != 0) {
							stockReport.setDamageQty(INV_QTY);
							stockReport.setOnHandQty(0D);
							stockReport.setHoldQty(0D);
							stockReport.setAvailableQty(INV_QTY);

							log.info("DAMAGED-------stockReport:" + stockReport);
							stockReportList.add(stockReport);
						}
					} else if (stockTypeText.equalsIgnoreCase("HOLD")) {
						// STCK_TYP_ID = 7
						List<String> storageSectionIds = Arrays.asList("ZB", "ZG", "ZD", "ZC", "ZT");
						double INV_QTY = getInventoryQty(inventory.getWarehouseId(), inventory.getItemCode(), 7L,
								storageSectionIds);

						if (INV_QTY != 0) {
							stockReport.setHoldQty(INV_QTY);
							stockReport.setOnHandQty(0D);
							stockReport.setDamageQty(0D);
							stockReport.setAvailableQty(INV_QTY);
							log.info("HOLD-------stockReport:" + stockReport);
							stockReportList.add(stockReport);
						}
					}
				}
			}
			log.info("stockReportList : " + stockReportList);
			final Page<StockReport> page = new PageImpl<>(stockReportList, pageable, inventoryList.getTotalElements());
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

			/*
			 * If ST_SEC_ID field value is entered in Search field, Pass ST_SEC_ID in
			 * STORAGE_BIN table and fetch ST_BIN values and pass these values in INVENTORY
			 * table to fetch the output values
			 */
			if (stSectionIds != null) {
				StorageBin[] dbStorageBin = mastersService.getStorageBinBySectionId(stSectionIds,
						authTokenForMastersService.getAccess_token());
				List<String> stBins = Arrays.asList(dbStorageBin).stream().map(StorageBin::getStorageBin)
						.collect(Collectors.toList());
				searchInventory.setStorageBin(stBins);
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
				StorageBin stBin = mastersService.getStorageBin(dbInventory.getStorageBin(),
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
	public Page<InventoryReport> scheduleInventoryReport(Integer pageNo, Integer pageSize, String sortBy) {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
		Page<Inventory> inventoryList = inventoryRepository.findAll(pageable);
		List<InventoryReport> reportInventoryList = new ArrayList<>();
		for (Inventory dbInventory : inventoryList) {
			InventoryReport reportInventory = new InventoryReport();

			// WH_ID
			reportInventory.setWarehouseId(dbInventory.getWarehouseId());

			// ITM_CODE
			reportInventory.setItemCode(dbInventory.getItemCode());

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
			StorageBin stBin = mastersService.getStorageBin(dbInventory.getStorageBin(),
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
	public List<StockMovementReport> getStockMovementReport(String warehouseId, String itemCode, String fromCreatedOn,
			String toCreatedOn) throws java.text.ParseException {
		// Warehouse
		if (warehouseId == null) {
			throw new BadRequestException("WarehouseId can't be blank.");
		}

		// Item Code
		if (itemCode == null) {
			throw new BadRequestException("ItemCode can't be blank.");
		}

		// Date
		if (fromCreatedOn == null || toCreatedOn == null) {
			throw new BadRequestException("CreatedOn can't be blank.");
		}

		/*
		 * Pass the Search paramaters (WH_ID,ITM_CODE,IM_CTD_ON) values in
		 * INVENOTRYMOVEMENT table for the selected date and fetch the below output
		 * values
		 */
		Date fromDate = null;
		Date toDate = null;
		try {
			fromDate = DateUtils.convertStringToDate(fromCreatedOn);
			fromDate = DateUtils.addTimeToDate(fromDate);
			toDate = DateUtils.convertStringToDate(toCreatedOn);
			toDate = DateUtils.addDayEndTimeToDate(toDate);
		} catch (Exception e) {
			throw new BadRequestException("Date shoud be in MM-dd-yyyy format.");
		}

//		SearchInventoryMovement searchInventoryMovement = new SearchInventoryMovement();
//		searchInventoryMovement.setWarehouseId(Arrays.asList(warehouseId));
//		searchInventoryMovement.setItemCode(Arrays.asList(itemCode));
//		searchInventoryMovement.setFromCreatedOn(fromDate);
//		searchInventoryMovement.setToCreatedOn(toDate);
////		searchInventoryMovement.setSubmovementType(Arrays.asList(2L, 3L));
//		List<InventoryMovement> inventoryMovementSearchResults = 
//				inventoryMovementService.findInventoryMovement(searchInventoryMovement);
//		log.info("inventoryMovementSearchResults------> : " + inventoryMovementSearchResults);

		List<InventoryMovement> inventoryMovementSearchResults_123 = inventoryMovementRepository
				.findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeIn(warehouseId,
						itemCode, fromDate, toDate, 1L, Arrays.asList(2L, 3L));
		List<StockMovementReport> reportStockMovementList_1 = fillData(inventoryMovementSearchResults_123);
		log.info("reportStockMovementList_1 : " + reportStockMovementList_1);

		List<InventoryMovement> inventoryMovementSearchResults_35 = inventoryMovementRepository
				.findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeIn(warehouseId,
						itemCode, fromDate, toDate, 3L, Arrays.asList(5L));
		List<StockMovementReport> reportStockMovementList_2 = fillData(inventoryMovementSearchResults_35);
		log.info("reportStockMovementList_2 : " + reportStockMovementList_2);

		reportStockMovementList_1.addAll(reportStockMovementList_2);
		return reportStockMovementList_1;
	}

	private List<StockMovementReport> fillData(List<InventoryMovement> inventoryMovementSearchResults) {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		List<StockMovementReport> reportStockMovementList = new ArrayList<>();
		for (InventoryMovement inventoryMovement : inventoryMovementSearchResults) {
			StockMovementReport stockMovementReport = new StockMovementReport();

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
				List<InboundLine> inboundLine = inboundLineService.getInboundLine(inventoryMovement.getRefDocNumber());
				log.info("inboundLine : " + inboundLine);
				if (!inboundLine.isEmpty()) {
					stockMovementReport.setCustomerCode(inboundLine.get(0).getVendorCode());
				}
			} else if (inventoryMovement.getMovementType() == 3L) {
				OutboundHeader outboundHeader = outboundHeaderService
						.getOutboundHeader(inventoryMovement.getRefDocNumber());
				log.info("outboundHeader : " + outboundHeader);
				if (outboundHeader != null) {
					stockMovementReport.setCustomerCode(outboundHeader.getPartnerCode());
				}
			}

			// Date & Time
			Date date = inventoryMovement.getCreatedOn();
			LocalDateTime datetime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
	 * getOrderStatusReport
	 * 
	 * @param warehouseId
	 * @param fromDeliveryDate
	 * @param toDeliveryDate
	 * @param customerCode
	 * @param orderNumber
	 * @param orderType
	 * @param statusId
	 * @return
	 * @throws java.text.ParseException
	 * @throws ParseException
	 */
	public List<OrderStatusReport> getOrderStatusReport(String warehouseId, String fromDeliveryDate,
			String toDeliveryDate, List<String> customerCode, List<String> orderNumber, List<String> orderType,
			List<Long> statusId) throws ParseException, java.text.ParseException {
		// WH_ID
		if (warehouseId == null) {
			throw new BadRequestException("WarehouseId can't be blank.");
		}

		// WH_ID
		if (fromDeliveryDate == null || toDeliveryDate == null) {
			throw new BadRequestException("DeliveryDate can't be blank.");
		}

		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		SearchOrderStatusReport searchOutboundLine = new SearchOrderStatusReport();
		searchOutboundLine.setWarehouseId(warehouseId);

		try {
			Date fromDate = DateUtils.convertStringToDate(fromDeliveryDate);
			Date toDate = DateUtils.convertStringToDate(toDeliveryDate);

			searchOutboundLine.setFromDeliveryDate(fromDate);
			searchOutboundLine.setToDeliveryDate(toDate);
			log.info("Date: " + fromDate + "," + toDate);
		} catch (Exception e) {
			throw new BadRequestException("Date shoud be in MM-dd-yyyy format.");
		}

		if (customerCode != null && !customerCode.isEmpty()) {
			searchOutboundLine.setPartnerCode(customerCode);
		}

		if (orderNumber != null && !orderNumber.isEmpty()) {
			searchOutboundLine.setRefDocNumber(orderNumber);
		}

		if (orderType != null && !orderType.isEmpty()) {
			searchOutboundLine.setOrderType(orderType);
		}

		if (statusId != null && !statusId.isEmpty()) {
			searchOutboundLine.setStatusId(statusId);
		}

		List<OutboundLine> outboundLineSearchResults = outboundLineService
				.findOutboundLineOrderStatusReport(searchOutboundLine);
		log.info("outboundLineSearchResults--------> : " + outboundLineSearchResults);

		List<OrderStatusReport> reportOrderStatusReportList = new ArrayList<>();
		for (OutboundLine outboundLine : outboundLineSearchResults) {
			OrderStatusReport orderStatusReport = new OrderStatusReport(); // WH_ID
			orderStatusReport.setWarehouseId(outboundLine.getWarehouseId()); // DLV_CNF_ON
			orderStatusReport.setSoNumber(outboundLine.getRefDocNumber()); // REF_DOC_NO
			orderStatusReport.setDoNumber(outboundLine.getDeliveryOrderNo()); // DLV_ORD_NO
			orderStatusReport.setCustomerCode(outboundLine.getPartnerCode()); // PARTNER_CODE

			/*
			 * Customer name ---------------------- Pass PARTNER_CODE in BUSINESSPARTNER
			 * table and fetch PARTNER_NM
			 */
			try {
				BusinessPartner businessPartner = mastersService.getBusinessPartner(outboundLine.getPartnerCode(),
						authTokenForMastersService.getAccess_token());
				orderStatusReport.setCustomerName(businessPartner.getPartnerName());
			} catch (Exception e) {
				log.info("BusinessPartner not found for : " + outboundLine.getPartnerCode());
				orderStatusReport.setCustomerName(null);
			}

			orderStatusReport.setSku(outboundLine.getItemCode()); // ITM_CODE
			orderStatusReport.setSkuDescription(outboundLine.getDescription()); // ITEM_TEXT
			orderStatusReport.setOrderedQty(outboundLine.getOrderQty()); // ORD_QTY
			orderStatusReport.setDeliveredQty(outboundLine.getDeliveryQty()); // DLV_QTY
			orderStatusReport.setDeliveryConfirmedOn(outboundLine.getDeliveryConfirmedOn());// DLV_CNF_ON

			/*
			 * Order Received Date ------------------------------ Pass REF_DOC_NO in
			 * OUTBOUNDHEADER table and fetch REF_DOC_DATE
			 */
			OutboundHeader outboundHeader = outboundHeaderService.getOutboundHeader(outboundLine.getRefDocNumber());
			orderStatusReport.setOrderReceivedDate(outboundHeader.getRefDocDate());

			/*
			 * Expected Delivery date ------------------------------- Pass REF_DOC_NO in
			 * OUTBOUNDHEADER table and fetch REQ_DEL_DATE
			 */
			orderStatusReport.setExpectedDeliveryDate(outboundHeader.getRequiredDeliveryDate());

			// % of Delivered - (DLV_QTY/ORD_QTY)*100
			double deliveryQty = 0;
			double orderQty = 0;
			if (outboundLine.getDeliveryQty() != null) {
				deliveryQty = outboundLine.getDeliveryQty();
			}

			if (outboundLine.getOrderQty() != null) {
				orderQty = outboundLine.getOrderQty();
			}
			double percOfDlv = Math.round((deliveryQty / orderQty) * 100);
			orderStatusReport.setPercentageOfDelivered(percOfDlv);

			// STATUS_ID
			/*
			 * Hard coded Options Delivered- if STATUS_ID 59, Partial deliveries -If
			 * STATUS_ID 42,43,48,50,55 , Not fulfilled- STATUS_ID 51,47)
			 */
			Long status = outboundLine.getStatusId();
			if (status == 59L) {
				orderStatusReport.setStatusId("Delivered");
			} else if (status == 42L || status == 43L || status == 48L || status == 50L || status == 55L) {
				orderStatusReport.setStatusId("In Progress");
			} else if (status == 51L || status == 47L) {
				orderStatusReport.setStatusId("Not fulfilled");
			}

			reportOrderStatusReportList.add(orderStatusReport);
		}
		return reportOrderStatusReportList;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param fromDeliveryDate
	 * @param toDeliveryDate
	 * @param storeCode
	 * @param soType
	 * @param orderNumber
	 * @return
	 * @throws java.text.ParseException
	 * @throws ParseException
	 */
	public List<ShipmentDeliveryReport> getShipmentDeliveryReport(String warehouseId, String fromDeliveryDate,
			String toDeliveryDate, String storeCode, List<String> soType, String orderNumber)
			throws ParseException, java.text.ParseException {
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

		try {
			// WH_ID
			if (warehouseId == null) {
				throw new BadRequestException("WarehouseId can't be blank.");
			}

			if (orderNumber == null) {
				throw new BadRequestException("OrderNumber can't be blank.");
			}

			SearchOutboundLineReport searchOutboundLineReport = new SearchOutboundLineReport();
			searchOutboundLineReport.setWarehouseId(warehouseId);
			searchOutboundLineReport.setRefDocNumber(orderNumber);

			if (!storeCode.isEmpty()) {
				searchOutboundLineReport.setPartnerCode(storeCode);
				log.info("storeCode: " + storeCode);
			}

			if (!soType.isEmpty()) {
				searchOutboundLineReport.setSoTypeRefField1(soType);
				log.info("soType: " + soType);
			}

			if (fromDeliveryDate != null && fromDeliveryDate.trim().length() > 0 && toDeliveryDate != null
					&& toDeliveryDate.trim().length() > 0) {
				log.info("fromDeliveryDate : " + fromDeliveryDate);
				log.info("toDeliveryDate : " + toDeliveryDate);

				Date fromDeliveryDate_d = DateUtils.convertStringToDate(fromDeliveryDate);
				fromDeliveryDate_d = DateUtils.addTimeToDate(fromDeliveryDate_d);

				Date toDeliveryDate_d = DateUtils.convertStringToDate(toDeliveryDate);
				toDeliveryDate_d = DateUtils.addDayEndTimeToDate(toDeliveryDate_d);

				log.info("Date: " + fromDeliveryDate_d + "," + toDeliveryDate_d);

				searchOutboundLineReport.setStartConfirmedOn(fromDeliveryDate_d);
				searchOutboundLineReport.setEndConfirmedOn(toDeliveryDate_d);
				log.info("fromDeliveryDate_d : " + fromDeliveryDate_d);
			}

			List<OutboundLine> outboundLineSearchResults = outboundLineService
					.findOutboundLineReport(searchOutboundLineReport);
			log.info("outboundLineSearchResults : " + outboundLineSearchResults);
			double total = 0;
			if (outboundLineSearchResults.isEmpty()) {
				log.info("Resultset is EMPTY");
			} else {
				total = outboundLineSearchResults.stream().filter(a -> a.getDeliveryQty() != null)
						.mapToDouble(OutboundLine::getDeliveryQty).sum();
				log.info("total : " + total);
			}

			List<ShipmentDeliveryReport> shipmentDeliveryList = new ArrayList<>();
			for (OutboundLine outboundLine : outboundLineSearchResults) {
				ShipmentDeliveryReport shipmentDelivery = new ShipmentDeliveryReport();
				shipmentDelivery.setDeliveryDate(outboundLine.getDeliveryConfirmedOn());
				shipmentDelivery.setDeliveryTo(outboundLine.getPartnerCode());
				shipmentDelivery.setOrderType(outboundLine.getReferenceField1());
				shipmentDelivery.setCustomerRef(outboundLine.getRefDocNumber()); // REF_DOC_NO
				shipmentDelivery.setCommodity(outboundLine.getItemCode());
				shipmentDelivery.setDescription(outboundLine.getDescription());

				// Obtain Partner Name
				BusinessPartner partner = mastersService.getBusinessPartner(outboundLine.getPartnerCode(),
						authTokenForMastersService.getAccess_token());
				shipmentDelivery.setPartnerName(partner.getPartnerName());

				/*
				 * MFR_PART
				 */
				ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCode(outboundLine.getItemCode(),
						warehouseId, authTokenForMastersService.getAccess_token());
				if (imBasicData1 != null) {
					shipmentDelivery.setManfCode(imBasicData1.getManufacturerPartNo());
				}

				shipmentDelivery.setQuantity(outboundLine.getDeliveryQty()); // DLV_QTY
				shipmentDelivery.setTotal(total);
				shipmentDeliveryList.add(shipmentDelivery);
			}
			log.info("shipmentDeliveryList : " + shipmentDeliveryList);
			return shipmentDeliveryList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param fromDeliveryDate
	 * @param toDeliveryDate
	 * @param customerCode
	 * @return
	 * @throws java.text.ParseException
	 * @throws ParseException
	 */
	public ShipmentDeliverySummaryReport getShipmentDeliverySummaryReport(String fromDeliveryDate,
			String toDeliveryDate, List<String> customerCode) throws ParseException, java.text.ParseException {
		/*
		 * Pass the Input Parameters in Outbound Line table (From and TO date in
		 * DLV_CNF_ON fields) and fetch the below Fields, If Customer Code is Selected
		 * all, Pass all the values into OUTBOUNDLINE table
		 */
		// Date range
		if (fromDeliveryDate == null || toDeliveryDate == null) {
			throw new BadRequestException("DeliveryDate can't be blank.");
		}

		Date fromDeliveryDate_d = null;
		Date toDeliveryDate_d = null;
		try {
			fromDeliveryDate_d = DateUtils.convertStringToDate(fromDeliveryDate);
			fromDeliveryDate_d = DateUtils.addTimeToDate(fromDeliveryDate_d);
			toDeliveryDate_d = DateUtils.convertStringToDate(toDeliveryDate);
			toDeliveryDate_d = DateUtils.addDayEndTimeToDate(toDeliveryDate_d);
			log.info("Date: " + fromDeliveryDate_d + "," + toDeliveryDate_d);
		} catch (Exception e) {
			throw new BadRequestException("Date shoud be in MM-dd-yyyy format.");
		}

		List<OutboundHeader> outboundHeaderList = outboundHeaderRepository
				.findByStatusIdAndDeliveryConfirmedOnBetween(59L, fromDeliveryDate_d, toDeliveryDate_d);
		ShipmentDeliverySummaryReport shipmentDeliverySummaryReport = new ShipmentDeliverySummaryReport();
		List<ShipmentDeliverySummary> shipmentDeliverySummaryList = new ArrayList<>();
		String warehouseId = null;
		try {
			for (OutboundHeader outboundHeader : outboundHeaderList) {
				// Report Preparation
				ShipmentDeliverySummary shipmentDeliverySummary = new ShipmentDeliverySummary();

				shipmentDeliverySummary.setSo(outboundHeader.getRefDocNumber()); // SO
				shipmentDeliverySummary.setExpectedDeliveryDate(outboundHeader.getRequiredDeliveryDate()); // DEL_DATE
				shipmentDeliverySummary.setDeliveryDateTime(outboundHeader.getDeliveryConfirmedOn()); // DLV_CNF_ON
				shipmentDeliverySummary.setBranchCode(outboundHeader.getPartnerCode()); // PARTNER_CODE/PARTNER_NM
				shipmentDeliverySummary.setOrderType(outboundHeader.getReferenceField1());

				// Line Ordered
				List<Long> countOfOrderedLines = outboundLineService.getCountofOrderedLines(
						outboundHeader.getWarehouseId(), outboundHeader.getPreOutboundNo(),
						outboundHeader.getRefDocNumber());

				// Line Shipped
				List<Long> deliveryLines = outboundLineService.getDeliveryLines(outboundHeader.getWarehouseId(),
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

				// Ordered Qty
				List<Long> sumOfOrderedQty = outboundLineService.getSumOfOrderedQty(outboundHeader.getWarehouseId(),
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

				// Shipped Qty
				List<Long> sumOfDeliveryQtyList = outboundLineService.getDeliveryQty(outboundHeader.getWarehouseId(),
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());

				double countOfOrderedLinesvalue = countOfOrderedLines.stream().mapToLong(Long::longValue).sum();
				double deliveryLinesCount = deliveryLines.stream().mapToLong(Long::longValue).sum();
				double sumOfOrderedQtyValue = sumOfOrderedQty.stream().mapToLong(Long::longValue).sum();
				double sumOfDeliveryQty = sumOfDeliveryQtyList.stream().mapToLong(Long::longValue).sum();

				shipmentDeliverySummary.setLineOrdered(countOfOrderedLinesvalue);
				shipmentDeliverySummary.setLineShipped(deliveryLinesCount);
				shipmentDeliverySummary.setOrderedQty(sumOfOrderedQtyValue);
				shipmentDeliverySummary.setShippedQty(sumOfDeliveryQty);

				// % Shipped - Divide (Shipped lines/Ordered Lines)*100
				double percShipped = Math.round((deliveryLinesCount / countOfOrderedLinesvalue) * 100);
				shipmentDeliverySummary.setPercentageShipped(percShipped);
				log.info("shipmentDeliverySummary : " + shipmentDeliverySummary);

				shipmentDeliverySummaryList.add(shipmentDeliverySummary);
				log.info("shipmentDeliverySummaryReportList : " + shipmentDeliverySummaryList);
			}

			// --------------------------------------------------------------------------------------------------------------------------------
			/*
			 * Partner Code : 101, 102, 103, 107, 109, 111 - Normal
			 */
			List<String> partnerCodes = Arrays.asList("101", "102", "103", "107", "109", "111");
			List<SummaryMetrics> summaryMetricsList = new ArrayList<>();
			for (String pCode : partnerCodes) {
				SummaryMetrics partnerCode_N = getMetricsDetails("N", warehouseId, pCode, "N", fromDeliveryDate_d,
						toDeliveryDate_d);
				SummaryMetrics partnerCode_S = getMetricsDetails("S", warehouseId, pCode, "S", fromDeliveryDate_d,
						toDeliveryDate_d);

				if (partnerCode_N != null) {
					summaryMetricsList.add(partnerCode_N);
				}

				if (partnerCode_S != null) {
					summaryMetricsList.add(partnerCode_S);
				}
			}

			shipmentDeliverySummaryReport.setShipmentDeliverySummary(shipmentDeliverySummaryList);
			shipmentDeliverySummaryReport.setSummaryMetrics(summaryMetricsList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shipmentDeliverySummaryReport;
	}

	/**
	 * ShipmentDeliverySummary Report
	 * 
	 * @param fromDeliveryDate
	 * @param toDeliveryDate
	 * @param customerCode
	 * @return
	 * @throws java.text.ParseException
	 * @throws ParseException
	 */
	public ShipmentDispatchSummaryReport getShipmentDispatchSummaryReport(String fromDeliveryDate,
			String toDeliveryDate, List<String> customerCode) throws ParseException, java.text.ParseException {
		// Date range
		if (fromDeliveryDate == null || toDeliveryDate == null) {
			throw new BadRequestException("DeliveryDate can't be blank.");
		}

		/*
		 * Pass the Input Parameters in Outbound Line table (Delivery date with From
		 * 00:00 and TO 23:49 in DLV_CNF_ON fields) and fetch the below Fields, If
		 * Customer Code is Selected as all or passed as Null, Pass Date ranges alone
		 * into OUTBOUNDLINE table
		 */
		Date fromDate = null;
		Date toDate = null;
		try {
			fromDate = DateUtils.convertStringToDate(fromDeliveryDate);
			fromDate = DateUtils.addTimeToDate(fromDate);
			log.info("Date---f----->: " + fromDate);

			toDate = DateUtils.convertStringToDate(toDeliveryDate);
			toDate = DateUtils.addDayEndTimeToDate(toDate);
			log.info("Date---t----->: " + toDate);
		} catch (Exception e) {
			throw new BadRequestException("Date shoud be in MM-dd-yyyy format.");
		}

		SearchOutboundLine searchOutboundLine = new SearchOutboundLine();
		searchOutboundLine.setFromDeliveryDate(fromDate);
		searchOutboundLine.setToDeliveryDate(toDate);

		if (!customerCode.isEmpty()) {
			searchOutboundLine.setPartnerCode(customerCode);
		}

		List<OutboundLine> outboundLineSearchResults = outboundLineService
				.findOutboundLineShipmentReport(searchOutboundLine);
		log.info("outboundLineSearchResults----->: " + outboundLineSearchResults);

		ShipmentDispatchSummaryReport shipmentDispatchSummary = new ShipmentDispatchSummaryReport();

		// Printed on
		List<String> partnerCodes = outboundLineSearchResults.stream().map(OutboundLine::getPartnerCode).distinct()
				.collect(Collectors.toList());
		log.info("partnerCodes----->: " + partnerCodes);

		List<ShipmentDispatch> listShipmentDispatch = new ArrayList<>();
		for (String partnerCode : partnerCodes) {
			ShipmentDispatch shipmentDispatch = new ShipmentDispatch();

			// Header
			ShipmentDispatchHeader header = new ShipmentDispatchHeader();
			header.setPartnerCode(partnerCode);

			// Obtaining Child
			List<OutboundLine> partnerOBLines = outboundLineSearchResults.stream()
					.filter(a -> a.getPartnerCode().equalsIgnoreCase(partnerCode)).collect(Collectors.toList());
			// List
			List<ShipmentDispatchList> shipmentDispatchList = new ArrayList<>();
			double totalLinesOrdered = 0.0;
			double totalLinesShipped = 0.0;
			double totalOrderedQty = 0.0;
			double totalShippedQty = 0.0;

			for (OutboundLine outboundLine : partnerOBLines) {
				ShipmentDispatchList list = new ShipmentDispatchList();
				list.setSoNumber(outboundLine.getRefDocNumber()); // REF_DOC_NO

				/*
				 * REF_DOC_DATE ----------------- Pass REF_DOC_NO in OUTBOUNDHEADER table and
				 * fetch RFEF_DOC_DATE field value
				 */
				OutboundHeader outboundHeader = null;
				try {
					outboundHeader = outboundHeaderService.getOutboundHeader(outboundLine.getRefDocNumber());
					log.info("outboundHeader : " + outboundHeader);
				} catch (Exception e) {
					List<OutboundHeader> outboundHeaders = outboundHeaderService.getOutboundHeaders();
					outboundHeaders = outboundHeaders.stream()
							.filter(a -> a.getRefDocNumber().equalsIgnoreCase(outboundLine.getRefDocNumber()))
							.collect(Collectors.toList());
					outboundHeader = outboundHeaders.get(0);
					log.info("outboundHeader : " + outboundHeader);
				}
				list.setOrderReceiptTime(outboundHeader.getRefDocDate());

				/*
				 * Lines ordered ----------------- Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE
				 * in OUTBOUNDLINE and fetch the COUNT of Lines for OB_LINE_NO where REF_FIELD_2
				 * = Null and display
				 */
				SearchOutboundLine obLineSearch = new SearchOutboundLine();
				obLineSearch.setWarehouseId(Arrays.asList(outboundLine.getWarehouseId()));
				obLineSearch.setPreOutboundNo(Arrays.asList(outboundLine.getPreOutboundNo()));
				obLineSearch.setPartnerCode(partnerCodes);
				obLineSearch.setRefDocNumber(Arrays.asList(outboundLine.getRefDocNumber()));

				List<OutboundLine> obResults = outboundLineService.findOutboundLineShipmentReport(obLineSearch);
				if (!obResults.isEmpty()) {
					obResults = obResults.stream().filter(a -> a.getReferenceField2() == null)
							.collect(Collectors.toList());
					double linesOrdered = obResults.stream().count();
					list.setLinesOrdered(linesOrdered);
					totalLinesOrdered += linesOrdered;
					log.info("linesOrdered : " + linesOrdered);
					log.info("totalLinesOrdered : " + totalLinesOrdered);
				}

				/*
				 * Lines shipped
				 * -----------------------------------------------------------------------------
				 * -------- Pass PRE_OB_NO/OB_LINE_NO/ITM_CODE/REF_DOC_NO/PARTNER_CODE in
				 * OUTBOUNDLINE table and fetch Count of OB_LINE_NO values where
				 * REF_FIELD_2=Null and DLV_QTY>0
				 */
				SearchOutboundLine obLineSearch1 = new SearchOutboundLine();
				obLineSearch1.setPreOutboundNo(Arrays.asList(outboundLine.getPreOutboundNo()));
				obLineSearch1.setLineNumber(Arrays.asList(outboundLine.getLineNumber()));
				obLineSearch1.setItemCode(Arrays.asList(outboundLine.getItemCode()));
				obLineSearch1.setPartnerCode(partnerCodes);
				obLineSearch1.setRefDocNumber(Arrays.asList(outboundLine.getRefDocNumber()));
				obResults = outboundLineService.findOutboundLineShipmentReport(obLineSearch1);
				log.info("obResults : " + obResults);

				if (!obResults.isEmpty()) {
					double linesShipped = obResults.stream().filter(
							a -> a.getReferenceField2() == null && a.getDeliveryQty() != null && a.getDeliveryQty() > 0)
							.collect(Collectors.toList()).stream().count();

					totalLinesShipped += linesShipped;
					list.setLinesShipped(linesShipped);
					log.info("linesShipped : " + linesShipped);
					log.info("totalLinesShipped : " + totalLinesShipped);
				}

				/*
				 * Ordered Qty ----------------- Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE in
				 * OUTBOUNDLINE and fetch the SUM of ORD_QTY values for OB_LINE_NO where
				 * REF_FIELD_2 = Null and display
				 */
				double orderedQty = obResults.stream()
						.filter(a -> a.getReferenceField2() == null && a.getOrderQty() != null)
						.mapToDouble(OutboundLine::getOrderQty).sum();
				list.setOrderedQty(orderedQty);
				totalOrderedQty += orderedQty;
				log.info("orderedQty : " + orderedQty);
				log.info("totalOrderedQty : " + totalOrderedQty);

				/*
				 * Shipped qty ------------- Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CDOE in
				 * OUTBOUNDLINE and fetch the SUM of DLV_QTY values for OB_LINE_NO where
				 * REF_FIELD_2 = Null and display
				 */
				double shippedQty = obResults.stream()
						.filter(a -> a.getReferenceField2() == null && a.getDeliveryQty() != null)
						.mapToDouble(OutboundLine::getDeliveryQty).sum();
				list.setShippedQty(shippedQty);
				totalShippedQty += shippedQty;
				log.info("shippedQty : " + shippedQty);
				log.info("totalShippedQty : " + totalShippedQty);

				/*
				 * % shipped - Divide (Shipped lines/Ordered Lines)*100
				 */
				double percShipped = Math.round((shippedQty / orderedQty) * 100);
				list.setPerentageShipped(percShipped);
				shipmentDispatchList.add(list);
				log.info("percentage Shipped : " + percShipped);
			}

			/*
			 * Total logic
			 */
			header.setTotalLinesOrdered(totalLinesOrdered);
			header.setTotalLinesShipped(totalLinesShipped);
			header.setTotalOrderedQty(totalOrderedQty);
			header.setTotalShippedQty(totalShippedQty);
			double averagePercentage = Math.round((totalShippedQty / totalOrderedQty) * 100);
			header.setAveragePercentage(averagePercentage);
			log.info("header : " + header);

			shipmentDispatch.setHeader(header);
			shipmentDispatch.setShipmentDispatchList(shipmentDispatchList);
			listShipmentDispatch.add(shipmentDispatch);
		}

		shipmentDispatchSummary.setShipmentDispatch(listShipmentDispatch);
		log.info("listShipmentDispatch : " + listShipmentDispatch);
		return shipmentDispatchSummary;
	}

	/**
	 * 
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

			SearchInboundHeader searchInboundHeader = new SearchInboundHeader();
			searchInboundHeader.setRefDocNumber(Arrays.asList(asnNumber));
			List<InboundHeader> inboundHeaderSearchResults = inboundHeaderService
					.findInboundHeader(searchInboundHeader);
//			log.info("inboundHeaderSearchResults : " + inboundHeaderSearchResults);

			List<InboundLine> inboundLineSearchResults = inboundLineService.getInboundLine(asnNumber);
//			log.info("inboundLineSearchResults ------>: " + inboundLineSearchResults);

			double sumTotalOfExpectedQty = 0.0;
			double sumTotalOfAccxpectedQty = 0.0;
			double sumTotalOfDamagedQty = 0.0;
			double sumTotalOfMissingORExcess = 0.0;
			List<Receipt> receiptList = new ArrayList<>();
			for (InboundLine inboundLine : inboundLineSearchResults) {
				log.info("inboundLine---------> : " + inboundLine);
				// Supplier - PARTNER_CODE
				receiptHeader.setSupplier(inboundLine.getVendorCode());

				// Container No
				receiptHeader.setContainerNo(inboundLine.getContainerNo());

				// Order Number - REF_DOC_NO
				receiptHeader.setOrderNumber(inboundLine.getRefDocNumber());

				// Order Type -> PREINBOUNDHEADER - REF_DOC_TYPE
				// Pass REF_DOC_NO in PREINBOUNDHEADER and fetch REF_DOC_TYPE
				PreInboundHeader preInboundHeader = preInboundHeaderService.getPreInboundHeader(
						inboundLine.getWarehouseId(), inboundLine.getPreInboundNo(), inboundLine.getRefDocNumber());
				receiptHeader.setOrderType(preInboundHeader.getReferenceDocumentType());
				log.info("preInboundHeader--------> : " + preInboundHeader);
//				}

				Receipt receipt = new Receipt();

				// SKU - ITM_CODE
				receipt.setSku(inboundLine.getItemCode());

				// Description - ITEM_TEXT
				receipt.setDescription(inboundLine.getDescription());

				// Mfr.Sku - MFR_PART
				receipt.setMfrSku(inboundLine.getManufacturerPartNo());

				// Expected - ORD_QTY
				double expQty = 0;
				if (inboundLine.getOrderedQuantity() != null) {
					expQty = inboundLine.getOrderedQuantity();
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

				receiptHeader.setExpectedQtySum(sumTotalOfExpectedQty);
				receiptHeader.setAcceptedQtySum(sumTotalOfAccxpectedQty);
				receiptHeader.setDamagedQtySum(sumTotalOfDamagedQty);
				receiptHeader.setMissingORExcessSum(sumTotalOfMissingORExcess);
			}

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
	 * 
	 * @param type
	 * @param warehouseId
	 * @param partnerCode
	 * @param toDeliveryDate_d
	 * @param fromDeliveryDate_d
	 * @param refField2
	 * @return
	 * @throws java.text.ParseException
	 * @throws ParseException
	 */
	private SummaryMetrics getMetricsDetails(String type, String warehouseId, String partnerCode, String refField1,
			Date fromDeliveryDate_d, Date toDeliveryDate_d) throws ParseException, java.text.ParseException {
		List<OutboundHeader> outboundHeaderList = outboundHeaderRepository
				.findByStatusIdAndPartnerCodeAndDeliveryConfirmedOnBetween(59L, partnerCode, fromDeliveryDate_d,
						toDeliveryDate_d);
		List<String> refDocNoList = outboundHeaderList.stream()
				.filter(a -> a.getReferenceField1().equalsIgnoreCase(refField1)).map(OutboundHeader::getRefDocNumber)
				.collect(Collectors.toList());
		log.info("refDocNoList : " + refDocNoList);

		List<String> refField1List = outboundHeaderList.stream().map(OutboundHeader::getReferenceField1)
				.collect(Collectors.toList());

		Long totalOrdeCount = refField1List.stream().filter(a -> a.equalsIgnoreCase(refField1)).count();
		log.info("refField1List : " + refField1List + "," + totalOrdeCount);

		/*
		 * 101- Line items(N) ----------------------- Pass the selected REF_DOC_NO in
		 * OUTBOUNDLINE table and fetch the COUNT of Lines for OB_LINE_NO where
		 * REF_FIELD_2 = Null and display (Ordered Lines)
		 */
		List<Long> outboundLineLineItems = outboundLineService.getLineItem_NByRefDocNoAndRefField2IsNull(refDocNoList,
				fromDeliveryDate_d, toDeliveryDate_d);

		if (!outboundLineLineItems.isEmpty()) {
			double line_item_N = outboundLineLineItems.stream().count();
			log.info("line_item_N : " + line_item_N);

			/*
			 * 101 % Shipped(N) -------------------------- Pass the selected REF_DOC_NO in
			 * OUTBOUNDLINE table and fetch Count of OB_LINE_NO values where
			 * REF_FIELD_2=Null and DLV_QTY>0 (Shipped Lines) % Shipped = (Shipped
			 * Lines/Order Lines) * 100"
			 */
			List<Long> shipped_lines_NList = outboundLineService.getShippedLines(refDocNoList, fromDeliveryDate_d,
					toDeliveryDate_d);
			double shipped_lines_N = shipped_lines_NList.stream().count();
			double percShipped_N = Math.round((shipped_lines_N / line_item_N) * 100);

			// Populate Metrics
			MetricsSummary metricsSummary = new MetricsSummary();
			metricsSummary.setTotalOrder(totalOrdeCount);
			metricsSummary.setLineItems(line_item_N);
			metricsSummary.setPercentageShipped(percShipped_N);

			SummaryMetrics summaryMetrics = new SummaryMetrics();
			summaryMetrics.setPartnerCode(partnerCode);
			summaryMetrics.setType(type);
			summaryMetrics.setMetricsSummary(metricsSummary);
			return summaryMetrics;
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
	 * 
	 * @param warehouseId
	 * @return
	 * @throws Exception
	 */
	public Dashboard getDashboard(String warehouseId) throws Exception {

		Dashboard dashboard = new Dashboard();

		/*--------------------------DAY-----------------------------------------------*/
		Dashboard.Day day = dashboard.new Day();
		Dashboard.Day.Receipts dayReceipts = day.new Receipts();
		Dashboard.Day.Shipping dayShipping = day.new Shipping();

		/*-----------------------Receipts--------------------------------------------*/
		// Awaiting ASN
		long awaitingASNCount = getAwaitingASNCount(warehouseId, new Date(), new Date());
		dayReceipts.setAwaitingASN(awaitingASNCount);

		// Container Received
		long containerReceivedCount = getContainerReceivedCount(warehouseId, new Date(), new Date());
		dayReceipts.setContainerReceived(containerReceivedCount);

		// Item Received
		long itemReceivedCount = getItemReceivedCount(warehouseId, new Date(), new Date());
		dayReceipts.setItemReceived(itemReceivedCount);

		/*-----------------------Shipping---------------------------------------------*/
		// ShippedLine Count
		long shippedLineCount = getShippedLineCount(warehouseId, new Date(), new Date());
		dayShipping.setShippedLine(shippedLineCount);

		// Normal Count
		long normalCount = getNormalNSpecialCount(warehouseId, new Date(), new Date(), "N");
		dayShipping.setNormal(normalCount);

		// Special Count
		long specialCount = getNormalNSpecialCount(warehouseId, new Date(), new Date(), "S");
		dayShipping.setSpecial(specialCount);

		day.setReceipts(dayReceipts);
		day.setShipping(dayShipping);
		dashboard.setDay(day);

		/*--------------------------MONTH--------------------------------------------*/
		Dashboard.Month month = dashboard.new Month();
		Dashboard.Month.Receipts monthReceipts = month.new Receipts();
		Dashboard.Month.Shipping monthShipping = month.new Shipping();

		/*-----------------------Receipts--------------------------------------------*/
		// Awaiting ASN
		LocalDate today = LocalDate.now();
		today = today.withDayOfMonth(1);
		log.info("First day of current month: " + today.withDayOfMonth(1));
		Date beginningOfMonth = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
		awaitingASNCount = getAwaitingASNCount(warehouseId, beginningOfMonth, new Date());
		monthReceipts.setAwaitingASN(awaitingASNCount);

		// Container Received
		containerReceivedCount = getContainerReceivedCount(warehouseId, beginningOfMonth, new Date());
		monthReceipts.setContainerReceived(containerReceivedCount);

		// Item Received
		itemReceivedCount = getItemReceivedCount(warehouseId, beginningOfMonth, new Date());
		monthReceipts.setItemReceived(itemReceivedCount);

		/*-----------------------Shipping---------------------------------------------*/
		// ShippedLine Count
		shippedLineCount = getShippedLineCount(warehouseId, beginningOfMonth, new Date());
		monthShipping.setShippedLine(shippedLineCount);

		// Normal Count
		normalCount = getNormalNSpecialCount(warehouseId, beginningOfMonth, new Date(), "N");
		monthShipping.setNormal(normalCount);

		// Special Count
		specialCount = getNormalNSpecialCount(warehouseId, beginningOfMonth, new Date(), "S");
		monthShipping.setSpecial(specialCount);

		month.setReceipts(monthReceipts);
		month.setShipping(monthShipping);
		dashboard.setMonth(month);

		/*
		 * --------------------------Bin
		 * Status-------------------------------------------------- Pass the Logged in
		 * WH_ID in STORAGBIN table and fetch Count of Records where Status_ID = 0 and
		 * Status_ID is not equal to Zero
		 */
		Dashboard.BinStatus binStatus = dashboard.new BinStatus();
		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
		StorageBin[] storageBins = mastersService.getStorageBinByStatus(warehouseId, 0L,
				authTokenForMastersService.getAccess_token());
		long statusCount = Arrays.asList(storageBins).stream().count();
		binStatus.setStatusEqualToZeroCount(statusCount);

		storageBins = mastersService.getStorageBinByStatusNotEqual(warehouseId, 0L,
				authTokenForMastersService.getAccess_token());
		statusCount = Arrays.asList(storageBins).stream().count();
		binStatus.setStatusNotEqualToZeroCount(statusCount);

		dashboard.setBinStatus(binStatus);
		return dashboard;
	}

	/**
	 * getMobileDashboard
	 * 
	 * @param warehouseId
	 * @return
	 */
	public MobileDashboard getMobileDashboard(String warehouseId) {
		MobileDashboard mobileDashboard = new MobileDashboard();

		/*--------------Inbound--------------------------------*/
		MobileDashboard.InboundCount inboundCount = mobileDashboard.new InboundCount();

		// -------------Cases------------------------------------
		// Pass Login WH_ID into STAGINGHEADER table and fetch the count of records
		// where STATUS_ID=12
		List<StagingHeader> stagingHeaderList = stagingHeaderService.getStagingHeaderCount(warehouseId);
		long cases = stagingHeaderList.stream().count();
		inboundCount.setCases(cases);

		// -------------Putaway----------------------------------
		// Pass Login WH_ID into PUTAWAYHEADER table and fetch the count of records
		// where STATUS_ID=19
		// and IB_ORD_TYP_ID= 1 and 3
		List<Long> orderTypeId = Arrays.asList(1L, 3L);
		List<PutAwayHeader> putAwayHeaderList = putAwayHeaderService.getPutAwayHeaderCount(warehouseId, orderTypeId);
		long putaway = putAwayHeaderList.stream().count();
		inboundCount.setPutaway(putaway);

		// -------------Reversals-------------------------------
		// Pass Login WH_ID into PUTAWAYHEADER table and fetch the count of records
		// where STATUS_ID=19
		// and IB_ORD_TYP_ID= 2 and 4
		orderTypeId = Arrays.asList(2L, 4L);
		putAwayHeaderList = putAwayHeaderService.getPutAwayHeaderCount(warehouseId, orderTypeId);
		long reversals = putAwayHeaderList.stream().count();
		inboundCount.setReversals(reversals);

		/*--------------Outbound--------------------------------*/
		MobileDashboard.OutboundCount outboundCount = mobileDashboard.new OutboundCount();

		// --------------Picking----------------------------------
		// Pass Login WH_ID into PICKUPHEADER table and fetch the count of records where
		// STATUS_ID=48 and
		// OB_ORD_TYP_ID= 0,1 and 3
		orderTypeId = Arrays.asList(0L, 1L, 3L);
		List<PickupHeader> pickupHeaderList = pickupHeaderService.getPickupHeaderCount(warehouseId, orderTypeId);
		long picking = pickupHeaderList.stream().count();
		outboundCount.setPicking(picking);

		// -------------Reversals--------------------------------
		// Pass Login WH_ID into PICKUPHEADER table and fetch the count of records where
		// STATUS_ID=48 and
		// OB_ORD_TYP_ID= 2
		orderTypeId = Arrays.asList(2L);
		pickupHeaderList = pickupHeaderService.getPickupHeaderCount(warehouseId, orderTypeId);
		reversals = pickupHeaderList.stream().count();
		outboundCount.setReversals(reversals);

		// -----------Quality-----------------------------------
		// Pass Login WH_ID into QUALITYHEADER table and fetch the count of records
		// where STATUS_ID=54
		List<QualityHeader> qualityHeader = qualityHeaderService.getQualityHeaderCount(warehouseId);
		long quality = qualityHeader.stream().count();
		outboundCount.setQuality(quality);

		mobileDashboard.setInboundCount(inboundCount);
		mobileDashboard.setOutboundCount(outboundCount);
		return mobileDashboard;
	}

	/**
	 * AwaitingASN Count
	 * 
	 * @param warehouseId
	 * @param date
	 * @return
	 * @throws java.text.ParseException
	 */
	private long getAwaitingASNCount(String warehouseId, Date fromCreatedOn, Date toCreatedOn)
			throws java.text.ParseException {
		/*
		 * Receipts - Awaiting ASN -------------------------- Pass the logged in WH_ID
		 * and current date in CR_CTD_ON field in CONTAINERRECEIPT table and fetch the
		 * count of records where REF_DOC_NO is Null
		 */
		SearchContainerReceipt searchContainerReceipt = new SearchContainerReceipt();
		searchContainerReceipt.setWarehouseId(Arrays.asList(warehouseId));
		searchContainerReceipt.setFromCreatedOn(fromCreatedOn);
		searchContainerReceipt.setToCreatedOn(toCreatedOn);
		List<ContainerReceipt> containerReceipt = containerReceiptService.findContainerReceipt(searchContainerReceipt);
		long awaitingASNCount = containerReceipt.stream().filter(c -> c.getRefDocNumber() == null).count();
		log.info("awaitingASNCount : " + awaitingASNCount);
		return awaitingASNCount;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private long getContainerReceivedCount(String warehouseId, Date startConfirmedOn, Date endConfirmedOn)
			throws Exception {
		/*
		 * Container Received ------------------------ Pass the logged in WH_ID and
		 * current date in IB_CNF_ON field in INBOUNDHEADER table and fetch the count of
		 * records
		 */
		SearchInboundHeader searchInboundHeader = new SearchInboundHeader();
		searchInboundHeader.setWarehouseId(Arrays.asList(warehouseId));
		searchInboundHeader.setStartConfirmedOn(startConfirmedOn);
		searchInboundHeader.setEndConfirmedOn(endConfirmedOn);
		List<InboundHeader> inboundHeaderSearchResults = inboundHeaderService.findInboundHeader(searchInboundHeader);
		long containerReceivedCount = inboundHeaderSearchResults.stream().count();
		log.info("containerReceivedCount : " + containerReceivedCount);
		return containerReceivedCount;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private long getItemReceivedCount(String warehouseId, Date startConfirmedOn, Date endConfirmedOn) throws Exception {
		/*
		 * Item Received ------------------- Pass the logged in WH_ID and current date
		 * in IB_CNF_ON field in INBOUNDLINE table and fetch the count of records where
		 * REF_FIELD_1 is Null
		 */
		SearchInboundLine searchInboundLine = new SearchInboundLine();
		searchInboundLine.setWarehouseId(warehouseId);
		searchInboundLine.setReferenceField1(null);
		searchInboundLine.setStartConfirmedOn(startConfirmedOn);
		searchInboundLine.setEndConfirmedOn(endConfirmedOn);
		List<InboundLine> inboundLineSearchResults = inboundLineService.findInboundLine(searchInboundLine);
		long itemReceivedCount = inboundLineSearchResults.stream().count();
		log.info("itemReceivedCount : " + itemReceivedCount);
		return itemReceivedCount;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private long getShippedLineCount(String warehouseId, Date fromDeliveryDate, Date toDeliveryDate) throws Exception {
		/*
		 * Shipped Line --------------------- Shipped Line Pass the logged in WH_ID and
		 * Current date as DLV_CNF_ON in OUTBOUNDLINE table and fetch Count of
		 * OB_LINE_NO values where REF_FIELD_2=Null and DLV_QTY > 0
		 */
		SearchOutboundLine searchOutboundLine = new SearchOutboundLine();
		searchOutboundLine.setWarehouseId(Arrays.asList(warehouseId));
		searchOutboundLine.setFromDeliveryDate(fromDeliveryDate);
		searchOutboundLine.setToDeliveryDate(toDeliveryDate);
		List<OutboundLine> outboundLineSearchResults = outboundLineService.findOutboundLine(searchOutboundLine);
		long shippedLineCount = outboundLineSearchResults.stream()
				.filter(o -> o.getReferenceField2() == null && o.getDeliveryQty() > 0).count();

		log.info("shippedLineCount : " + shippedLineCount);
		return shippedLineCount;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param date
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
		SearchOutboundLine searchOutboundLine = new SearchOutboundLine();
		searchOutboundLine.setWarehouseId(Arrays.asList(warehouseId));
		searchOutboundLine.setFromDeliveryDate(fromDeliveryDate);
		searchOutboundLine.setToDeliveryDate(toDeliveryDate);
		List<OutboundLine> outboundLineSearchResults = outboundLineService.findOutboundLine(searchOutboundLine);
		long normalCount = outboundLineSearchResults.stream().filter(o -> o.getReferenceField1().equalsIgnoreCase(type)
				&& o.getReferenceField2() == null && o.getDeliveryQty() > 0).count();

		log.info("normalCount : " + normalCount);
		return normalCount;
	}

	/**
	 * 
	 * @return
	 */
	public WorkBookSheetDTO exportXlsxFile() {

		int pageSize = 500;
		Page<InventoryReport> pageResult = scheduleInventoryReport(0, pageSize, "itemCode");
		log.info("pageResult : " + pageResult.getTotalElements());
		List<InventoryReport> listRecords = new ArrayList<>();
		listRecords.addAll(pageResult.getContent());
		
		log.info("listRecords : " + listRecords);
		log.info("pageResult.getTotalPages() : " + pageResult.getTotalPages());

		for (int pageNo = 1; pageNo <= pageResult.getTotalPages(); pageNo++) {
			pageResult = scheduleInventoryReport(pageNo, pageSize, "itemCode");
			listRecords.addAll(pageResult.getContent());
			log.info("listRecords : " + listRecords.size());
		}

		WorkBookSheetDTO workBookSheetDTO = workBookService.createWorkBookWithSheet("inventory");
		CellStyle headerStyle = workBookSheetDTO.getStyle();
		CellStyle cellStyle = workBookService.createLineCellStyle(workBookSheetDTO.getWorkbook());
		CellStyle decimalFormatCells = workBookService.createLineCellStyle(workBookSheetDTO.getWorkbook());

		try {

			/*
			 * private String WAREHOUSEID; // WH_ID private String ITEMCODE; // ITM_CODE
			 * private String DESCRIPTION; // ITEM_TEXT private String UOM; // INV_UOM
			 * private String STORAGEBIN; // ST_BIN private String STORAGESECTIONID; //
			 * ST_SEC_ID private String PACKBARCODES; // PACK_BARCODE private Double
			 * INVENTORYQTY; // INV_QTY private Long STOCKTYPE; // STCK_TYP_TEXT
			 */
			ArrayList<String> headerData = new ArrayList<>();
			headerData.add("WAREHOUSEID");
			headerData.add("ITEMCODE");
			headerData.add("DESCRIPTION");
			headerData.add("UOM");
			headerData.add("STORAGEBIN");
			headerData.add("STORAGESECTIONID");
			headerData.add("PACKBARCODES");
			headerData.add("INVENTORYQTY");
			headerData.add("STOCKTYPE");

			this.createHeaderRow(workBookSheetDTO.getWorkbook().getSheet("inventory"), headerStyle, headerData);

			int rowIndex = 1;
			for (InventoryReport data : listRecords) {
				log.info("data : " + data);
				int cellIndex = 0;

				Row row = workBookSheetDTO.getWorkbook().getSheet("inventory").createRow(rowIndex++);
				row.createCell(cellIndex).setCellValue(data.getWarehouseId());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getItemCode());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getDescription());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getUom());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getStorageBin());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getStorageSectionId());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getPackBarcodes());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getInventoryQty());
				row.getCell(cellIndex).setCellStyle(cellStyle);
				cellIndex++;

				row.createCell(cellIndex).setCellValue(data.getStockType());
				row.getCell(cellIndex).setCellStyle(cellStyle);
			}
			
			OutputStream fout = new FileOutputStream("inventory.xlsx");
			workBookSheetDTO.getWorkbook().write(fout);
                
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workBookSheetDTO;
	}

	public ByteArrayOutputStream getOutputStreamToByteArray(OutputStream os) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(bos.toByteArray());
        byte[] arr = bos.toByteArray();
        os.write(arr);
        return bos;
    }
	
	/**
	 * 
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
}

package com.tekclover.wms.api.outbound.transaction.service;

import java.text.ParseException;
import java.util.*;

import com.tekclover.wms.api.outbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.controller.exception.OutboundOrderRequestException;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.CycleCountLine;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.periodic.PeriodicHeaderV1;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.periodic.PeriodicLineV1;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.perpetual.PerpetualLineV1;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.*;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation.ReturnPO;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation.SalesOrder;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation.Shipment;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.outbound.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.outbound.transaction.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.cyclecount.perpetual.PerpetualHeaderV1;
import com.tekclover.wms.api.outbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipment;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@Slf4j
@Service
public class WarehouseService extends BaseService {
	@Autowired
    PropertiesConfig propertiesConfig;
	
	@Autowired
	OrderService orderService;

	@Autowired
	WarehouseRepository warehouseRepository;

	@Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

	@Autowired
	StockAdjustmentMiddlewareService stockAdjustmentService;

	@Autowired
	InventoryV2Repository inventoryV2Repository;

	/**
	 * 
	 * @return
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}


	/**
	 * ShipmentOrder
	 * @param shipmenOrder
	 * @return
	 */
	public ShipmentOrder postSO(ShipmentOrder shipmenOrder, boolean isRerun) {
		log.info("ShipmenOrder received from External: " + shipmenOrder);
		OutboundOrder savedSoHeader = saveSO (shipmenOrder, isRerun);						// Without Nongo
		log.info("savedSoHeader: " + savedSoHeader.getRefDocumentNo());
		return shipmenOrder;
	}

	private OutboundOrder saveSO (ShipmentOrder shipmenOrder, boolean isRerun) {
		try {
			SOHeader soHeader = shipmenOrder.getSoHeader();

			// Warehouse ID Validation
			validateWarehouseId (soHeader.getWareHouseId());

			// Checking for duplicate RefDocNumber
			OutboundOrder obOrder = orderService.getOBOrderById(soHeader.getTransferOrderNumber());

			if (obOrder != null) {
				throw new OutboundOrderRequestException("TransferOrderNumber is getting duplicated. This order already exists in the System.");
			}

			List<SOLine> soLines = shipmenOrder.getSoLine();

			OutboundOrder apiHeader = new OutboundOrder();
			apiHeader.setOrderId(soHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(soHeader.getWareHouseId());
			apiHeader.setPartnerCode(soHeader.getStoreID());
			apiHeader.setPartnerName(soHeader.getStoreName());
			apiHeader.setRefDocumentNo(soHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(0L);
			apiHeader.setRefDocumentType("SO");						// Hardcoded value "SO"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());

			try {
				Date reqDelDate = DateUtils.convertStringToDate(soHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			for (SOLine soLine : soLines) {
				OutboundOrderLine apiLine = new OutboundOrderLine();
				apiLine.setLineReference(soLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());							// ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());					// ORD_QTY
				apiLine.setUom(soLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());		// ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				orderLines.add(apiLine);
			}

			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (shipmenOrder.getSoLine() != null && !shipmenOrder.getSoLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("ShipmentOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (shipmenOrder.getSoLine() == null || shipmenOrder.getSoLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("ShipmentOrder Order Failed: " + createdOrder);
				throw new BadRequestException("ShipmentOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * ShipmentConfirmation API
	 * @param shipment
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postShipmentConfirmation (Shipment shipment, String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceShipmentUrl());
		HttpEntity<?> entity = new HttpEntity<>(shipment, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/**
	 * InterWarehouseShipmentConfirmation
	 * @param iwhShipment
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postInterWarehouseShipmentConfirmation(InterWarehouseShipment iwhShipment,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceIWHouseShipmentUrl());
		HttpEntity<?> entity = new HttpEntity<>(iwhShipment, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/**
	 * SaleOrderConfirmation
	 * @param salesOrder
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postSaleOrderConfirmation(
			SalesOrder salesOrder,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceSalesOrderUrl());
		HttpEntity<?> entity = new HttpEntity<>(salesOrder, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/**
	 * ReturnPO
	 * @param returnPO
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postReturnPOConfirmation(
			ReturnPO returnPO,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceReturnPOUrl());
		HttpEntity<?> entity = new HttpEntity<>(returnPO, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	/**
	 * 
	 * @param wareHouseId
	 * @return 
	 */
	private boolean validateWarehouseId(String wareHouseId) {
		log.info("wareHouseId: " + wareHouseId);
//		if (wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_110) || wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_111)) {
//			log.info("wareHouseId:------------> " + wareHouseId);
//			return true;
//		} else {
//			throw new BadRequestException("Warehouse Id must be either 110 or 111");
//		}
		if (wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_100) || wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_200)) {
			log.info("wareHouseId:------------> " + wareHouseId);
			return true;
		} else {
			throw new BadRequestException("Warehouse Id must be either 100 or 200");
		}
	}
	

	/*---------------------------------CycleCountOrder------------------------------------------*/

	/**
	 * @param perpetual
	 * @return
	 */
	public CycleCountHeader postPerpetual(Perpetual perpetual) {
		log.info("CycleCountHeaderOrder received from External: " + perpetual);
		CycleCountHeader savedCycleCount = savePerpetual(perpetual);
		log.info("Perpetual: " + perpetual);
		return savedCycleCount;
	}

	// Perpetual
	private CycleCountHeader savePerpetual(Perpetual perpetual) {
		try {
			PerpetualHeaderV1 perpetualHeaderV1 = perpetual.getPerpetualHeaderV1();
			List<PerpetualLineV1> perpetualLineV1List = perpetual.getPerpetualLineV1();
			CycleCountHeader apiHeader = new CycleCountHeader();
			apiHeader.setCompanyCode(perpetualHeaderV1.getCompanyCode());
			apiHeader.setCycleCountNo(perpetualHeaderV1.getCycleCountNo());
			apiHeader.setOrderId(perpetualHeaderV1.getCycleCountNo());
			apiHeader.setBranchCode(perpetualHeaderV1.getBranchCode());
			apiHeader.setBranchName(perpetualHeaderV1.getBranchName());
			apiHeader.setIsNew(perpetualHeaderV1.getIsNew());
			apiHeader.setCycleCountCreationDate(new Date());
			apiHeader.setMiddlewareId(perpetualHeaderV1.getMiddlewareId());
			apiHeader.setMiddlewareTable(perpetualHeaderV1.getMiddlewareTable());
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setStockCountType("PERPETUAL");
			apiHeader.setIsCancelled(perpetualHeaderV1.getIsCancelled());
			apiHeader.setIsCompleted(perpetualHeaderV1.getIsCompleted());
			apiHeader.setUpdatedOn(perpetualHeaderV1.getUpdatedOn());

			Set<CycleCountLine> cycleCountLines = new HashSet<>();
			for (PerpetualLineV1 perpetualLineV1 : perpetualLineV1List) {
				CycleCountLine apiLine = new CycleCountLine();
				apiLine.setCycleCountNo(perpetualLineV1.getCycleCountNo());                                // CC_NO
				apiLine.setLineOfEachItemCode(perpetualLineV1.getLineNoOfEachItemCode());                    // INV_NO
				apiLine.setItemCode(perpetualLineV1.getItemCode());                                        // ITM_CODE
				apiLine.setItemDescription(perpetualLineV1.getItemDescription());                        // ITM_DESC
				apiLine.setUom(perpetualLineV1.getUom());                                                    // UOM
				apiLine.setManufacturerCode(perpetualLineV1.getManufacturerCode());                        // MANU_FAC_CODE
				apiLine.setManufacturerName(perpetualLineV1.getManufacturerName());                        // MANU_FAC_NM
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setMiddlewareId(perpetualLineV1.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(perpetualLineV1.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(perpetualLineV1.getMiddlewareTable());
				apiLine.setFrozenQty(perpetualLineV1.getFrozenQty());
				apiLine.setCountedQty(perpetualLineV1.getCountedQty());
				apiLine.setIsCancelled(perpetualLineV1.getIsCancelled());
				apiLine.setIsCompleted(perpetualLineV1.getIsCompleted());
				apiLine.setStockCountType("PERPETUAL");

				cycleCountLines.add(apiLine);
			}
			apiHeader.setLines(cycleCountLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (perpetual.getPerpetualLineV1() != null && !perpetual.getPerpetualLineV1().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				CycleCountHeader cycleCountHeader = orderService.createCycleCountOrder(apiHeader);
				log.info("Perpetual Order Success: " + cycleCountHeader);
				return cycleCountHeader;
			} else if (perpetual.getPerpetualLineV1() == null || perpetual.getPerpetualLineV1().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				CycleCountHeader createOrder = orderService.createCycleCountOrder(apiHeader);
				log.info("Perpetual Order Failed : " + createOrder);
				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	//Periodic

	/**
	 * @param periodic
	 * @return
	 */
	public CycleCountHeader postPeriodic(Periodic periodic) {
		log.info("Periodic received from External: " + periodic);
		CycleCountHeader savedCycleCount = savePeriodic(periodic);
		log.info("Periodic: " + periodic);
		return savedCycleCount;
	}

	// periodic
	private CycleCountHeader savePeriodic(Periodic periodic) {
		try {
			PeriodicHeaderV1 periodicHeaderV1 = periodic.getPeriodicHeaderV1();
			List<PeriodicLineV1> periodicLineV1List = periodic.getPeriodicLineV1();
			CycleCountHeader apiHeader = new CycleCountHeader();
			apiHeader.setCompanyCode(periodicHeaderV1.getCompanyCode());
			apiHeader.setCycleCountNo(periodicHeaderV1.getCycleCountNo());
			apiHeader.setOrderId(periodicHeaderV1.getCycleCountNo());
			apiHeader.setBranchCode(periodicHeaderV1.getBranchCode());
			apiHeader.setBranchName(periodicHeaderV1.getBranchName());
			apiHeader.setIsNew(periodicHeaderV1.getIsNew());
			apiHeader.setIsCancelled(periodicHeaderV1.getIsCancelled());
			apiHeader.setIsCompleted(periodicHeaderV1.getIsCompleted());
			apiHeader.setUpdatedOn(periodicHeaderV1.getUpdatedOn());
			apiHeader.setCycleCountCreationDate(periodicHeaderV1.getCycleCountCreationDate());
			apiHeader.setStockCountType("PERIODIC");
			apiHeader.setMiddlewareId(periodicHeaderV1.getMiddlewareId());
			apiHeader.setMiddlewareTable(periodicHeaderV1.getMiddlewareTable());

			Set<CycleCountLine> cycleCountLines = new HashSet<>();
			for (PeriodicLineV1 periodicLineV1 : periodicLineV1List) {
				CycleCountLine apiLine = new CycleCountLine();
				apiLine.setCycleCountNo(periodicLineV1.getCycleCountNo());                                // CC_NO
				apiLine.setLineOfEachItemCode(periodicLineV1.getLineNoOfEachItemCode());                    // INV_NO
				apiLine.setItemCode(periodicLineV1.getItemCode());                                        // ITM_CODE
				apiLine.setItemDescription(periodicLineV1.getItemDescription());                        // ITM_DESC
				apiLine.setUom(periodicLineV1.getUom());                                                    // UOM
				apiLine.setManufacturerCode(periodicLineV1.getManufacturerCode());                        // MANU_FAC_CODE
				apiLine.setManufacturerName(periodicLineV1.getManufacturerName());                        // MANU_FAC_NM
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setCountedQty(periodicLineV1.getCountedQty());
				apiLine.setFrozenQty(periodicLineV1.getFrozenQty());
				apiLine.setIsCancelled(periodicLineV1.getIsCancelled());
				apiLine.setIsCompleted(periodicLineV1.getIsCompleted());
				apiLine.setStockCountType("PERIODIC");
				apiLine.setMiddlewareId(periodicLineV1.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(periodicLineV1.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(periodicLineV1.getMiddlewareTable());

				cycleCountLines.add(apiLine);
			}
			apiHeader.setLines(cycleCountLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (periodic.getPeriodicLineV1() != null && !periodic.getPeriodicLineV1().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				CycleCountHeader cycleCountHeader = orderService.createCycleCountOrder(apiHeader);
				log.info("Periodic Order Success: " + cycleCountHeader);
				return cycleCountHeader;
			} else if (periodic.getPeriodicLineV1() == null || periodic.getPeriodicLineV1().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				CycleCountHeader createOrder = orderService.createCycleCountOrder(apiHeader);
				log.info("Periodic Order Failed : " + createOrder);
				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * Post SalesInvoice
	 *
	 * @param salesInvoice
	 * @return
	 */
	public OutboundOrderV2 postSalesInvoice(SalesInvoice salesInvoice) throws ParseException {
		log.info("SalesInvoice received from external: " + salesInvoice);
		OutboundOrderV2 savedSalesInvoice = saveSalesInvoice(salesInvoice);
		log.info("Saved SalesInvoice: " + savedSalesInvoice);
		return savedSalesInvoice;
	}

	/**
	 *
	 * @param stockAdjustmentList
	 * @return
	 */
	public List<StockAdjustment> postStockAdjustmentUpload(List<StockAdjustment> stockAdjustmentList) {
		List<StockAdjustment> savedStockAdjustmentList = new ArrayList<>();
		for(StockAdjustment stockAdjustment : stockAdjustmentList) {
			log.info("StockAdjustment received from external: " + stockAdjustment);
			StockAdjustment savedStockAdjustment = saveStockAdjustment(stockAdjustment);
			log.info("Saved StockAdjustment: " + savedStockAdjustment);
			savedStockAdjustmentList.add(savedStockAdjustment);
		}
		return savedStockAdjustmentList;
	}

	/**
	 *
	 * @param stockAdjustment
	 * @return
	 */
	public StockAdjustment postStockAdjustment(StockAdjustment stockAdjustment) {
		log.info("StockAdjustment received from external: " + stockAdjustment);
		StockAdjustment savedStockAdjustment = saveStockAdjustment(stockAdjustment);
		log.info("Saved StockAdjustment: " + savedStockAdjustment);
		return savedStockAdjustment;
	}

	//Save SalesInvoice
	private OutboundOrderV2 saveSalesInvoice(SalesInvoice salesInvoice) throws ParseException {
		try {
//			OutboundOrderV2 duplicateOrder = orderService.getOBOrderByIdV2(salesInvoice.getSalesInvoiceNumber());
//			if (duplicateOrder != null) {
//				throw new OutboundOrderRequestException("Sales Invoice is already posted and it can't be duplicated");
//			}

			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			apiHeader.setCompanyCode(salesInvoice.getCompanyCode());
			apiHeader.setOrderId(salesInvoice.getSalesInvoiceNumber());
			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			apiHeader.setBranchCode(salesInvoice.getBranchCode());
			apiHeader.setSalesOrderNumber(salesInvoice.getSalesOrderNumber());
			apiHeader.setPickListNumber(salesInvoice.getPickListNumber());

			apiHeader.setCustomerId(salesInvoice.getCustomerId());
			apiHeader.setCustomerName(salesInvoice.getCustomerName());
			apiHeader.setPhoneNumber(salesInvoice.getPhoneNumber());
			apiHeader.setAlternateNo(salesInvoice.getAlternateNo());
			apiHeader.setSalesInvoiceNumber(salesInvoice.getSalesInvoiceNumber());
			apiHeader.setDeliveryType(salesInvoice.getDeliveryType());
			apiHeader.setAddress(salesInvoice.getAddress());
			apiHeader.setStatus(salesInvoice.getStatus());

			apiHeader.setMiddlewareId(salesInvoice.getMiddlewareId());
			apiHeader.setMiddlewareTable(salesInvoice.getMiddlewareTable());

			// Get Warehouse
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							salesInvoice.getCompanyCode(), salesInvoice.getBranchCode(),
							"EN", 0L);
			log.info("dbWarehouse: " + dbWarehouse);
			validateWarehouseId(dbWarehouse.get().getWarehouseId());
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());

			try {
				Date date = DateUtils.convertStringToDate2(salesInvoice.getInvoiceDate());
				apiHeader.setSalesInvoiceDate(date);
				apiHeader.setRequiredDeliveryDate(date);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be yyyy-MM-dd");
			}

			apiHeader.setRefDocumentNo(salesInvoice.getSalesInvoiceNumber());
			apiHeader.setRefDocumentType("Sales Invoice");
			apiHeader.setOutboundOrderTypeID(4L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setOrderProcessedOn(new Date());

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					salesInvoice.getCompanyCode(), salesInvoice.getBranchCode(), dbWarehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setBranchName(iKeyValuePair.getPlantDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}

			if (salesInvoice != null) {
				try {
					apiHeader.setProcessedStatusId(0L);
					log.info("SalesInvoice: " + apiHeader);
					OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
					log.info("SalesInvoice Order Success: " + createdOrder);
					return createdOrder;
				} catch (Exception e) {
					apiHeader.setProcessedStatusId(100L);
					log.info("SalesInvoice: " + apiHeader);
					OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
					log.info("SalesInvoice Order Failed: " + createdOrder);
					throw e;
				}
			}
//			else if (salesInvoice == null) {
//				// throw the error as Lines are Empty and set the Indicator as '100'
//				apiHeader.setProcessedStatusId(100L);
//				log.info("SalesInvoice: " + apiHeader);
//				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//				log.info("SalesInvoice Order Failed: " + createdOrder);
//				throw new BadRequestException("SalesInvoice Order doesn't contain any Lines.");
//			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	//Save StockAdjustment
	private StockAdjustment saveStockAdjustment(StockAdjustment stockAdjustment) {
		try {

			StockAdjustment dbStockAdjustment = new StockAdjustment();
			dbStockAdjustment.setCompanyCode(stockAdjustment.getCompanyCode());
			dbStockAdjustment.setBranchCode(stockAdjustment.getBranchCode());
			dbStockAdjustment.setBranchName(stockAdjustment.getBranchName());
			dbStockAdjustment.setDateOfAdjustment(stockAdjustment.getDateOfAdjustment());
			dbStockAdjustment.setIsDamage(stockAdjustment.getIsDamage());
			dbStockAdjustment.setIsCycleCount(stockAdjustment.getIsCycleCount());
			dbStockAdjustment.setItemCode(stockAdjustment.getItemCode());
			dbStockAdjustment.setItemDescription(stockAdjustment.getItemDescription());
			dbStockAdjustment.setAdjustmentQty(stockAdjustment.getAdjustmentQty());
			dbStockAdjustment.setUnitOfMeasure(stockAdjustment.getUnitOfMeasure());
			dbStockAdjustment.setManufacturerCode(stockAdjustment.getManufacturerCode());
			dbStockAdjustment.setManufacturerName(stockAdjustment.getManufacturerName());
			dbStockAdjustment.setRemarks(stockAdjustment.getRemarks());
			dbStockAdjustment.setAmsReferenceNo(stockAdjustment.getAmsReferenceNo());
			dbStockAdjustment.setIsCompleted(stockAdjustment.getIsCompleted());
			dbStockAdjustment.setUpdatedOn(stockAdjustment.getUpdatedOn());
			dbStockAdjustment.setMiddlewareId(stockAdjustment.getStockAdjustmentId());
			dbStockAdjustment.setMiddlewareTable(stockAdjustment.getMiddlewareTable());

			// Get Warehouse
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(),
							"EN", 0L);
			log.info("dbWarehouse: " + dbWarehouse);
			validateWarehouseId(dbWarehouse.get().getWarehouseId());
			dbStockAdjustment.setWarehouseId(dbWarehouse.get().getWarehouseId());

			dbStockAdjustment.setRefDocType("Stock Adjustment");
			dbStockAdjustment.setOrderReceivedOn(new Date());
//			dbStockAdjustment.setOrderProcessedOn(new Date());

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					stockAdjustment.getCompanyCode(), stockAdjustment.getBranchCode(), dbWarehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				dbStockAdjustment.setCompanyDescription(iKeyValuePair.getCompanyDesc());
				dbStockAdjustment.setPlantDescription(iKeyValuePair.getPlantDesc());
				dbStockAdjustment.setWarehouseDescription(iKeyValuePair.getWarehouseDesc());
			}

			if (dbStockAdjustment != null) {
				try {
					dbStockAdjustment.setProcessedStatusId(0L);
					log.info("stockAdjustment: " + dbStockAdjustment);
					StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
					log.info("StockAdjustment Order Success: " + createdOrder);
					return createdOrder;
				} catch (Exception e) {
					dbStockAdjustment.setProcessedStatusId(100L);
					log.info("StockAdjustment: " + dbStockAdjustment);
					StockAdjustment createdOrder = stockAdjustmentService.createStockAdjustment(dbStockAdjustment);
					log.info("StockAdjustment Order Failed: " + createdOrder);
					throw e;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * @param salesOrder
	 * @return
	 */
	public SalesOrderV2 postSalesOrderV2(SalesOrderV2 salesOrder) {
		log.info("SalesOrderHeader received from External: " + salesOrder);
		OutboundOrderV2 savedSoHeader = saveSalesOrderV2(salesOrder);                                // Without Nongo
		log.info("salesOrderHeader: " + savedSoHeader);
		return salesOrder;
	}

	// POST
	@Transactional
	private OutboundOrderV2 saveSalesOrderV2(@Valid SalesOrderV2 salesOrder) {
		try {
			SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();
			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
			if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
			} else {
				Optional<Warehouse> warehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
								salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
								0L);
				// Warehouse ID Validation
//                validateWarehouseId(warehouse.get().getWarehouseId());
				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			}


			apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
			apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
			apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);

			if (salesOrderHeader.getPickListNumber() != null && salesOrderHeader.getStoreID() != null) {
				apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
				apiHeader.setPartnerCode(salesOrderHeader.getStoreID());
				apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
				apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
			} else {
				apiHeader.setOrderId(salesOrderHeader.getSalesOrderNumber());
				apiHeader.setPartnerCode(salesOrderHeader.getBranchCode());
				apiHeader.setRefDocumentNo(salesOrderHeader.getSalesOrderNumber());
				apiHeader.setPickListNumber(salesOrderHeader.getSalesOrderNumber());
			}


			apiHeader.setPartnerName(salesOrderHeader.getStoreName());
			apiHeader.setPickListStatus(salesOrderHeader.getStatus());

			if (salesOrderHeader.getOrderType() != null) {
				apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
			} else {
				apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
			}
			apiHeader.setRefDocumentType("SALES ORDER");
			apiHeader.setCustomerType("INVOICE");                                //HardCoded
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());

			apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());

			try {
				Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be yyyy-MM-dd");
			}

			List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			String barcodeId = null;
			for (SalesOrderLineV2 soLine : salesOrderLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));

				if (salesOrderHeader.getStoreID() != null) {
					apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				} else {
					apiLine.setSourceBranchCode(salesOrderHeader.getBranchCode());
				}

				apiLine.setBrand(soLine.getBrand());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());

				apiLine.setManufacturerCode(MFR_NAME);
				apiLine.setManufacturerName(MFR_NAME);

				apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
				apiLine.setStoreID(salesOrderHeader.getStoreID());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setCustomerType("INVOICE");                                //HardCoded
				if (salesOrderHeader.getOrderType() != null) {
					apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
				} else {
					apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
				}
				barcodeId = soLine.getBarcodeId() != null ? soLine.getBarcodeId() : soLine.getSku();
				apiLine.setBarcodeId(barcodeId);

				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
				apiLine.setPickListNo(soLine.getPickListNo());

				apiLine.setMiddlewareId(soLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

				log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + apiHeader.getCompanyCode() + " plantId ----> " + apiHeader.getBranchCode() + " languageId ----> " + apiHeader.getLanguageId() +
						", warehouseId -----> " + apiHeader.getWarehouseID() + "itemCode -----> " + apiLine.getItemCode() + " refDocumentNo -----> " + apiHeader.getRefDocumentNo());

//				Double INV_QTY = inventoryV2Repository.getCurrentCaseQtyWithoutBarcodeId(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//						apiHeader.getWarehouseID(), apiLine.getItemCode(), apiHeader.getRefDocumentNo());

//				log.info("Inventory Qty for incoming order according to ItemCode INV_QTY ----> {}", INV_QTY);

				Double ordQty = soLine.getExpectedQtyInPieces() / soLine.getExpectedQtyInCases();  // 50 / 2 => 25
				apiLine.setExpectedQty(soLine.getExpectedQtyInPieces());     // 25
				apiLine.setOrderedQty(soLine.getExpectedQtyInPieces());      // 25
//				apiLine.setBagSize(INV_QTY);         // 25
//				if (soLine.getNoBags() != null) {
//					apiLine.setNoBags(soLine.getNoBags());
//				} else {
//					apiLine.setNoBags(soLine.getExpectedQtyInCases());
//				}
				if(soLine.getExpectedQtyInCases() != null) {
					apiLine.setNoBags(soLine.getExpectedQtyInCases());
				} else {
					apiLine.setNoBags(soLine.getNoBags());
				}
				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV4(apiHeader);
				log.info("SalesOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Failed: " + createdOrder);
				throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw new BadRequestException(e.toString());
		}
		return null;
	}

	public OutboundOrderV2 postSalesOrderV5(@Valid SalesOrderV2 salesOrder) {
		try {
			SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();

			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));

			if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
			} else {
				Optional<Warehouse> warehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
								salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
								0L);
				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			}

			apiHeader.setBranchCode(salesOrderHeader.getStoreID());
			apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
			apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);

			apiHeader.setCustomerId(salesOrderHeader.getCustomerId());
			apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
			apiHeader.setPartnerCode(salesOrderHeader.getCustomerId());
			apiHeader.setPartnerName(salesOrderHeader.getCustomerName());
			apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
			apiHeader.setPickListStatus(salesOrderHeader.getStatus());
			apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
			if (salesOrderHeader.getOrderType() != null) {
				apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
			} else {
				apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
			}

			apiHeader.setRefDocumentType("PICK LIST");

			apiHeader.setCustomerType("INVOICE");								//HardCoded
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());

			apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());

			try {
				Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}

			List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (SalesOrderLineV2 soLine : salesOrderLines) {
				String barcodeId = null;
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));
				apiLine.setBrand(soLine.getBrand());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
				apiLine.setManufacturerCode(MFR_NAME_V5);
				apiLine.setManufacturerName(MFR_NAME_V5);
				apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
				apiLine.setStoreID(salesOrderHeader.getStoreID());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setCustomerType("INVOICE");								//HardCoded
				if (salesOrderHeader.getOrderType() != null) {
					apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
				} else {
					apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
				}
//				barcodeId = soLine.getBarcodeId() != null ? soLine.getBarcodeId() : soLine.getSku().trim();
				if(soLine.getBarcodeId() != null) {
					apiLine.setBarcodeId(soLine.getBarcodeId());
				}
				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku().trim());                    // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
				apiLine.setUom(soLine.getUom());                                // ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
				apiLine.setPickListNo(soLine.getPickListNo());

				apiLine.setMiddlewareId(soLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				apiHeader.setExecuted(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Failed: " + createdOrder);
				throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw new BadRequestException("Exception while saving sales Order-PickList - " + e.toString());
		}
		return null;
	}

	public OutboundOrderV2 emptyCrateOrderV5(@Valid SalesOrderV2 salesOrder) {
		try {
			SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();

			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));

			if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
			} else {
				Optional<Warehouse> warehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
								salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
								0L);
				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			}

			apiHeader.setBranchCode(salesOrderHeader.getStoreID());
			apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
			apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);

			apiHeader.setCustomerId(salesOrderHeader.getCustomerId());
			apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
			apiHeader.setPartnerCode(salesOrderHeader.getCustomerId());
			apiHeader.setPartnerName(salesOrderHeader.getCustomerName());
			apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
			apiHeader.setPickListStatus(salesOrderHeader.getStatus());
			apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
			if (salesOrderHeader.getOrderType() != null) {
				apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
			} else {
				apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID_MT);
			}

			apiHeader.setRefDocumentType("EMPTY CRATE");

			apiHeader.setCustomerType("INVOICE");								//HardCoded
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());

			apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());

			try {
				Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}

			List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (SalesOrderLineV2 soLine : salesOrderLines) {
				String barcodeId = null;
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));
				apiLine.setBrand(soLine.getBrand());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
				apiLine.setManufacturerCode(MFR_NAME_V5);
				apiLine.setManufacturerName(MFR_NAME_V5);
				apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
				apiLine.setStoreID(salesOrderHeader.getStoreID());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setCustomerType("INVOICE");								//HardCoded
				if (salesOrderHeader.getOrderType() != null) {
					apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
				} else {
					apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
				}
//				barcodeId = soLine.getBarcodeId() != null ? soLine.getBarcodeId() : soLine.getSku().trim();
				if(soLine.getBarcodeId() != null) {
					apiLine.setBarcodeId(soLine.getBarcodeId());
				}
				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku().trim());                    // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
				apiLine.setUom(soLine.getUom());                                // ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
				apiLine.setPickListNo(soLine.getPickListNo());

				apiLine.setMiddlewareId(soLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				apiHeader.setExecuted(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Failed: " + createdOrder);
				throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw new BadRequestException("Exception while saving sales Order-PickList - " + e.toString());
		}
		return null;
	}

	//=====================================KNOWELL ==============================================//
	// POST
	public OutboundOrderV2 saveSalesOrderV7(@Valid SalesOrderV2 salesOrder) {
		try {
			SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();
			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));
			if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
			} else {
				Optional<Warehouse> warehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
								salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
								0L);
				// Warehouse ID Validation
//                validateWarehouseId(warehouse.get().getWarehouseId());
				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			}


			apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
			apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
			apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);
			apiHeader.setCustomerId(salesOrderHeader.getCustomerId());
			apiHeader.setCustomerName(salesOrderHeader.getCustomerName());


			if (salesOrderHeader.getPickListNumber() != null && salesOrderHeader.getStoreID() != null) {
				apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
				apiHeader.setPartnerCode(salesOrderHeader.getStoreID());
				apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
				apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
			} else {
				apiHeader.setOrderId(salesOrderHeader.getSalesOrderNumber());
				apiHeader.setPartnerCode(salesOrderHeader.getBranchCode());
				apiHeader.setRefDocumentNo(salesOrderHeader.getSalesOrderNumber());
				apiHeader.setPickListNumber(salesOrderHeader.getSalesOrderNumber());
			}


			apiHeader.setPartnerName(salesOrderHeader.getStoreName());
			apiHeader.setPickListStatus(salesOrderHeader.getStatus());

			if (salesOrderHeader.getOrderType() != null) {
				apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
			} else {
				apiHeader.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
			}
			apiHeader.setRefDocumentType("SALES ORDER");
			apiHeader.setCustomerType("INVOICE");                                //HardCoded
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setSalesOrderNumber(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());

			apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());

			try {
				Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be yyyy-MM-dd");
			}

			List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			String barcodeId = null;
			for (SalesOrderLineV2 soLine : salesOrderLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));

				if (salesOrderHeader.getStoreID() != null) {
					apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				} else {
					apiLine.setSourceBranchCode(salesOrderHeader.getBranchCode());
				}

				apiLine.setBrand(soLine.getBrand());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
				apiLine.setManufacturerCode(soLine.getManufacturerName());
				apiLine.setManufacturerName(soLine.getManufacturerName());
//				apiLine.setManufacturerCode(MFR_NAME_V7);
//				apiLine.setManufacturerName(MFR_NAME_V7);

				apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
				apiLine.setStoreID(salesOrderHeader.getStoreID());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setCustomerType("INVOICE");                                //HardCoded
				if (salesOrderHeader.getOrderType() != null) {
					apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
				} else {
					apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
				}
				barcodeId = soLine.getBarcodeId() != null ? soLine.getBarcodeId() : soLine.getSku();
				apiLine.setBarcodeId(barcodeId);

				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
				apiLine.setPickListNo(soLine.getPickListNo());

				apiLine.setMiddlewareId(soLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

//                if(soLine.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), soLine.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                        apiLine.setNoBags(soLine.getOrderedQty());
////                        double orderQty = getQuantity(soLine.getOrderedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                    }
//                }

				log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + apiHeader.getCompanyCode() + " plantId ----> " + apiHeader.getBranchCode() + " languageId ----> " + apiHeader.getLanguageId() +
						", warehouseId -----> " + apiHeader.getWarehouseID() + "itemCode -----> " + apiLine.getItemCode() + " refDocumentNo -----> " + apiHeader.getRefDocumentNo());

				Double INV_QTY = inventoryV2Repository.getCurrentCaseQtyWithoutBarcodeId(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
						apiHeader.getWarehouseID(), apiLine.getItemCode(), apiHeader.getRefDocumentNo());

				log.info("Inventory Qty for incoming order according to ItemCode INV_QTY ----> {}", INV_QTY);

//				Double ordQty = soLine.getExpectedQtyInPieces() / soLine.getExpectedQtyInCases();// 50 / 2 => 25
				Double ordQty = 0.0;
				if (soLine.getExpectedQtyInPieces() != null && soLine.getExpectedQtyInCases() != null && soLine.getExpectedQtyInCases() != 0) {
					ordQty = soLine.getExpectedQtyInPieces() / soLine.getExpectedQtyInCases();
				} else {
					log.warn("Division by zero or null detected for item: {}", apiLine.getItemCode());
				}
				apiLine.setExpectedQty(soLine.getExpectedQtyInPieces());     // 25
				apiLine.setOrderedQty(soLine.getExpectedQtyInPieces());      // 25
				apiLine.setBagSize(INV_QTY);         // 25
				if (soLine.getNoBags() != null) {
					apiLine.setNoBags(soLine.getNoBags());
				} else {
					apiLine.setNoBags(soLine.getExpectedQtyInCases());
				}

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("SalesOrder Order Failed: " + createdOrder);
				throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw new BadRequestException(e.toString());
		}
		return null;
	}

	/**
	 * @param returnPO
	 * @return
	 */
	public ReturnPOV2 postReturnPOV2(ReturnPOV2 returnPO) throws ParseException {
		log.info("ReturnPOHeader received from External: " + returnPO);
		OutboundOrderV2 savedReturnPOHeader = saveReturnPOV5(returnPO);                    // Without Nongo
		log.info("savedReturnPOHeader: " + savedReturnPOHeader);
		return returnPO;
	}

	/**
	 * @param returnPO
	 * @return
	 */
	private OutboundOrderV2 saveReturnPOV5(ReturnPOV2 returnPO) throws ParseException {
		try {
			ReturnPOHeaderV2 returnPOHeader = returnPO.getReturnPOHeader();
			List<ReturnPOLineV2> returnPOLines = returnPO.getReturnPOLine();
			// Mongo Primary Key
			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			BeanUtils.copyProperties(returnPOHeader, apiHeader, CommonUtils.getNullPropertyNames(returnPOHeader));
			if (returnPOHeader.getWareHouseId() != null && !returnPOHeader.getWareHouseId().isBlank()) {
				apiHeader.setWarehouseID(returnPOHeader.getWareHouseId());
			} else {
				Optional<Warehouse> warehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								returnPOHeader.getCompanyCode(), returnPOHeader.getBranchCode(),
								returnPOHeader.getLanguageId() != null ? returnPOHeader.getLanguageId() : LANG_ID,
								0L);
				// Warehouse ID Validation
//                validateWarehouseId(warehouse.get().getWarehouseId());
				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			}
			apiHeader.setCompanyCode(returnPOHeader.getCompanyCode());
			apiHeader.setBranchCode(returnPOHeader.getBranchCode());
			apiHeader.setLanguageId(returnPOHeader.getLanguageId());
			apiHeader.setWarehouseID(returnPOHeader.getWareHouseId());

			apiHeader.setOrderId(returnPOHeader.getPickListNumber());
			apiHeader.setPartnerCode(returnPOHeader.getStoreID());
			apiHeader.setPartnerName(returnPOHeader.getStoreName());
			apiHeader.setRefDocumentNo(returnPOHeader.getPickListNumber());
			apiHeader.setOutboundOrderTypeID(2L);                            // Hardcoded Value "2"
			apiHeader.setRefDocumentType("PURCHASE RETURN");                        // Hardcoded value "RETURNPO"
			apiHeader.setRefDocumentType(getOutboundOrderTypeDesc(apiHeader.getOutboundOrderTypeID()));
			apiHeader.setOrderReceivedOn(new Date());

			apiHeader.setIsCompleted(returnPOHeader.getIsCompleted());
			apiHeader.setIsCancelled(returnPOHeader.getIsCancelled());
			apiHeader.setUpdatedOn(returnPOHeader.getUpdatedOn());
			apiHeader.setMiddlewareId(returnPOHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(returnPOHeader.getMiddlewareTable());
			apiHeader.setCustomerId(returnPOHeader.getCustomerId());

			try {
				Date date = DateUtils.convertStringToDate2(returnPOHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(date);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be yyyy-MM-dd");
			}

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					returnPOHeader.getCompanyCode(),
					returnPOHeader.getStoreID(),returnPOHeader.getWareHouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}
			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (ReturnPOLineV2 rpoLine : returnPOLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				BeanUtils.copyProperties(rpoLine, apiLine, CommonUtils.getNullPropertyNames(rpoLine));
				apiLine.setBrand(rpoLine.getBrand());
				apiLine.setOrigin(rpoLine.getOrigin());
				apiLine.setPackQty(rpoLine.getPackQty());
				apiLine.setExpectedQty(rpoLine.getExpectedQty());
				apiLine.setSupplierName(rpoLine.getSupplierName());
				apiLine.setSourceBranchCode(returnPOHeader.getStoreID());
				apiLine.setCountryOfOrigin(rpoLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(returnPOHeader.getCompanyCode());
				apiLine.setReturnOrderNo(rpoLine.getReturnOrderNo());
				apiLine.setStoreID(returnPOHeader.getStoreID());
				apiLine.setLineReference(rpoLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(rpoLine.getSku());                            // ITM_CODE
				apiLine.setItemText(rpoLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(rpoLine.getReturnQty());                    // ORD_QTY
				apiLine.setUom(rpoLine.getUom());                                // ORD_UOM
				apiLine.setRefField1ForOrderType(rpoLine.getOrderType());
				apiLine.setOrderedQty(rpoLine.getReturnQty());
				apiLine.setBarcodeId(rpoLine.getBarcodeId());// ORDER_TYPE
//				apiLine.setManufacturerCode(rpoLine.getManufacturerCode());
//				apiLine.setManufacturerName(rpoLine.getManufacturerName());
				if (rpoLine.getManufacturerCode() != null) {
					apiLine.setManufacturerCode(rpoLine.getManufacturerCode());
				} else {
						apiLine.setManufacturerCode(MFR_NAME_V5);
				}
				if (rpoLine.getManufacturerName() != null) {
					apiLine.setManufacturerName(rpoLine.getManufacturerName());
				} else {
						apiLine.setManufacturerName(MFR_NAME_V5);
					}

				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				apiLine.setOutboundOrderTypeID(2L);

				apiLine.setSupplierInvoiceNo(rpoLine.getSupplierInvoiceNo());
				apiLine.setReturnOrderNo(rpoLine.getReturnOrderNo());
				apiLine.setIsCompleted(rpoLine.getIsCompleted());
				apiLine.setIsCancelled(rpoLine.getIsCancelled());
				apiLine.setMiddlewareId(rpoLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(rpoLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(rpoLine.getMiddlewareTable());

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (returnPO.getReturnPOLine() != null && !returnPO.getReturnPOLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("ReturnPO Order Success: " + createdOrder);
				return apiHeader;
			} else if (returnPO.getReturnPOLine() == null || returnPO.getReturnPOLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("ReturnPO Order Failed: " + createdOrder);
				throw new BadRequestException("ReturnPO Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	/**
	 *
	 * @param interWarehouseTransferInV2
	 * @return
	 */
	public OutboundOrderV2 postInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
		OutboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
		log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
		return savedIWHReturnV2;
	}
	// InterWarehouseTransferInV2
	private OutboundOrderV2 saveInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		try {
			InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
			List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
			apiHeader.setTransferRequestType(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
			apiHeader.setOutboundOrderTypeID(1L);                // Hardcoded Value 3
			apiHeader.setRefDocumentType("WMS to WMS");
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setLanguageId("EN");
			apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
			apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
			apiHeader.setRequiredDeliveryDate(interWarehouseTransferInHeaderV2.getTransferOrderDate());
			apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
			apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());
			apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
			apiHeader.setFromBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());
			apiHeader.setPartnerCode(interWarehouseTransferInHeaderV2.getToBranchCode());

			// Get Warehouse
			DataBaseContextHolder.setCurrentDb("KNOWELL");
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							interWarehouseTransferInHeaderV2.getSourceCompanyCode(),
							interWarehouseTransferInHeaderV2.getSourceBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());

			Set<OutboundOrderLineV2> orderLinesV2 = new HashSet<>();
			for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
				apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
				apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
				apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
				apiLine.setSupplierInvoiceNo(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
				apiLine.setManufacturerName(iwhTransferLineV2.getManufacturerName());            // BRND_NM
				apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
				apiLine.setUom(iwhTransferLineV2.getUom());
				apiLine.setPackQty(iwhTransferLineV2.getPackQty());
				apiLine.setOrigin(iwhTransferLineV2.getOrigin());
				apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
				apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
				apiLine.setBrand(iwhTransferLineV2.getBrand());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setOutboundOrderTypeID(1L);

				apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
				apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
				apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

				log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + apiHeader.getCompanyCode() + " plantId ----> " + apiHeader.getBranchCode() + " languageId ----> " + apiHeader.getLanguageId() +
						", warehouseId -----> " + apiHeader.getWarehouseID() + "itemCode -----> " + apiLine.getItemCode() + " refDocumentNo -----> " + apiHeader.getRefDocumentNo());

				Double INV_QTY = inventoryV2Repository.getCurrentCaseQtyWithoutBarcodeId(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
						apiHeader.getWarehouseID(), apiLine.getItemCode(), apiHeader.getRefDocumentNo());

				apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQtyInPieces());     // 25
				apiLine.setOrderedQty(iwhTransferLineV2.getExpectedQtyInPieces());      // 25
				apiLine.setBagSize(INV_QTY);
				apiLine.setNoBags(iwhTransferLineV2.getExpectedQtyInCases());

				orderLinesV2.add(apiLine);

//				// EA_DATE
//				try {
//					ZoneId defaultZoneId = ZoneId.systemDefault();
//					String sdate = iwhTransferLineV2.getExpectedDate();
//					String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
//					String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
//					secondHalf = "/20" + secondHalf;
//					sdate = firstHalf + secondHalf;
//					log.info("sdate--------> : " + sdate);
//
//					LocalDate localDate = DateUtils.dateConv2(sdate);
//					log.info("localDate--------> : " + localDate);
//					Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
//					apiLine.setExpectedDate(date);
//				} catch (Exception e) {
//					e.printStackTrace();
//					throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
//				}
			}
			apiHeader.setLine(orderLinesV2);
			apiHeader.setOrderProcessedOn(new Date());
			if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
					!interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrderV2 = orderService.createOutboundOrdersV3(apiHeader);
				log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
				return createdOrderV2;
			} else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
					interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrderV2 = orderService.createOutboundOrdersV3(apiHeader);
				log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
				throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

}
package com.tekclover.wms.api.transaction.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountLine;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.periodic.PeriodicHeaderV1;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.periodic.PeriodicLineV1;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.perpetual.PerpetualHeaderV1;
import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.perpetual.PerpetualLineV1;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.*;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.transaction.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.controller.exception.InboundOrderRequestException;
import com.tekclover.wms.api.transaction.controller.exception.OutboundOrderRequestException;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASNHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.ASNLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InterWarehouseTransferIn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InterWarehouseTransferInHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.InterWarehouseTransferInLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.SOReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.SOReturnLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.StoreReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.StoreReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.StoreReturnLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransfer;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.Shipment;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService extends BaseService {
	@Autowired
	private IntegrationApiResponseRepository integrationApiResponseRepository;

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

	/**
	 * 
	 * @return
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	/**
	 *
	 * @param asn
	 * @return
	 */
	public InboundOrder postWarehouseASN (ASN asn) {
		log.info("ASNHeader received from External: " + asn);
		InboundOrder savedAsnHeader = saveASN (asn);							// Without Mongo
		log.info("savedAsnHeader: " + savedAsnHeader);
		return savedAsnHeader;
	} 

	/**
	 * 
	 * @param storeReturn
	 * @return
	 */
	public InboundOrder postStoreReturn(StoreReturn storeReturn) {
		log.info("StoreReturnHeader received from External: " + storeReturn);
		InboundOrder savedStoreReturn = saveStoreReturn (storeReturn);
		log.info("savedStoreReturn: " + savedStoreReturn);
		return savedStoreReturn;
	}
	
	/**
	 * 
	 * @param soReturn
	 * @return
	 */
	public InboundOrder postSOReturn(SaleOrderReturn soReturn) {
		log.info("StoreReturnHeader received from External: " + soReturn);
		InboundOrder savedSOReturn = saveSOReturn (soReturn);
		log.info("soReturnHeader: " + savedSOReturn);
		return savedSOReturn;
	}

	/**
	 * 
	 * @param interWarehouseTransferIn
	 * @return
	 */
	public InboundOrder postInterWarehouseTransfer(InterWarehouseTransferIn interWarehouseTransferIn) {
		log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransferIn);
		InboundOrder savedIWHReturn = saveInterWarehouseTransfer (interWarehouseTransferIn);
		log.info("interWarehouseTransferHeader: " + savedIWHReturn);
		return savedIWHReturn;
	}

	/*-------------------------------------OUTBOUND---------------------------------------------------*/
	/**
	 * ShipmentOrder
	 * @param shipmenOrder
	 * @return
	 */
	public ShipmentOrder postSO( ShipmentOrder shipmenOrder, boolean isRerun) {
		log.info("ShipmenOrder received from External: " + shipmenOrder);
		OutboundOrder savedSoHeader = saveSO (shipmenOrder, isRerun);						// Without Nongo
		log.info("savedSoHeader: " + savedSoHeader.getRefDocumentNo());
		return shipmenOrder;
	}
	
	/**
	 * 
	 * @param salesOrder
	 * @return
	 */
	public SalesOrder postSalesOrder(SalesOrder salesOrder) {
		log.info("SalesOrderHeader received from External: " + salesOrder);
		OutboundOrder savedSoHeader = saveSalesOrder (salesOrder);								// Without Nongo
		log.info("salesOrderHeader: " + savedSoHeader);
		return salesOrder;
	}

	/**
	 * 
	 * @param returnPO
	 * @return
	 */
	public ReturnPO postReturnPO( ReturnPO returnPO) {
		log.info("ReturnPOHeader received from External: " + returnPO);
		OutboundOrder savedReturnPOHeader = saveReturnPO (returnPO);					// Without Nongo
		log.info("savedReturnPOHeader: " + savedReturnPOHeader);
		return returnPO;
	}

	/**
	 * 
	 * @param interWarehouseTransfer
	 * @return
	 */
	public InterWarehouseTransferOut postInterWarehouseTransferOutbound(InterWarehouseTransferOut interWarehouseTransfer) {
		log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransfer);
		OutboundOrder savedInterWarehouseTransferHeader = saveIWHTransfer (interWarehouseTransfer);													// Without Nongo
		log.info("savedInterWarehouseTransferHeader: " + savedInterWarehouseTransferHeader);
		return interWarehouseTransfer;
	}
	
	/*----------------------------INBOUND-CONFIRMATION-POST---------------------------------------------*/
	// ASN 
	public AXApiResponse postASNConfirmation (com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASN asn, 
			String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceAsnUrl());
		HttpEntity<?> entity = new HttpEntity<>(asn, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// StoreReturn
	public AXApiResponse postStoreReturnConfirmation (
			com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturn storeReturn,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
		HttpEntity<?> entity = new HttpEntity<>(storeReturn, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// Sale Order Returns
	public AXApiResponse postSOReturnConfirmation (
			com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturn soReturn,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
		HttpEntity<?> entity = new HttpEntity<>(soReturn, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/**
	 *
	 * @param iwhTransfer
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postInterWarehouseTransferConfirmation(InterWarehouseTransfer iwhTransfer,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
		HttpEntity<?> entity = new HttpEntity<>(iwhTransfer, headers);
		ResponseEntity<AXApiResponse> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	//--------------------------------OUTBOUND-----------------------------------------------------------------------

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
			com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.SalesOrder salesOrder,
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
			com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.ReturnPO returnPO,
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
	
	/**
	 * 
	 * @return
	 */
	public static synchronized String getUUID() {
		String uniqueID = UUID.randomUUID().toString();
		return uniqueID;
	}
	
	//================================================Moongo=Removed================================================================================
	//------------------------------------------------INBOUND-ORDERS--------------------------------------------------------------------------------
	// POST ASNHeader
	private InboundOrder saveASN (ASN asn) {
		try {
			ASNHeader asnHeader = asn.getAsnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (asnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundOrder dbApiHeader = orderService.getOrderById(asnHeader.getAsnNumber());
			if (dbApiHeader != null) {
				throw new InboundOrderRequestException("ASN is already posted and it can't be duplicated.");
			}
						
			List<ASNLine> asnLines = asn.getAsnLine();
			InboundOrder apiHeader = new InboundOrder();
			apiHeader.setOrderId(asnHeader.getAsnNumber());
			apiHeader.setRefDocumentNo(asnHeader.getAsnNumber());
			apiHeader.setRefDocumentType("ASN");
			apiHeader.setWarehouseID(asnHeader.getWareHouseId());
			apiHeader.setInboundOrderTypeId(1L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			for (ASNLine asnLine : asnLines) {
				InboundOrderLines apiLine = new InboundOrderLines();
				apiLine.setLineReference(asnLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(asnLine.getSku());							// ITM_CODE
				apiLine.setItemText(asnLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setInvoiceNumber(asnLine.getInvoiceNumber());			// INV_NO
				apiLine.setContainerNumber(asnLine.getContainerNumber());		// CONT_NO
				apiLine.setSupplierCode(asnLine.getSupplierCode());				// PARTNER_CODE
				apiLine.setSupplierPartNumber(asnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
				apiLine.setManufacturerName(asnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(asnLine.getManufacturerPartNo());	// MFR_PART
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(asnLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(asnLine.getExpectedQty());				// ORD_QTY
				apiLine.setUom(asnLine.getUom());								// ORD_UOM
				apiLine.setItemCaseQty(asnLine.getPackQty());					// ITM_CASE_QTY
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			if (asn.getAsnLine() != null && !asn.getAsnLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("ASN Order Success : " + createdOrder);
				return createdOrder;
			} else if (asn.getAsnLine() == null || asn.getAsnLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("ASN Order Failed : " + createdOrder);
				throw new BadRequestException("ASN Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	// STORE RETURN
	private InboundOrder saveStoreReturn (StoreReturn storeReturn) {
		try {
			StoreReturnHeader storeReturnHeader = storeReturn.getStoreReturnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (storeReturnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundOrder dbApiHeader = orderService.getOrderById(storeReturnHeader.getTransferOrderNumber());
			if (dbApiHeader != null) {
				throw new InboundOrderRequestException("StoreReturn is already posted and it can't be duplicated.");
			}
						
			List<StoreReturnLine> storeReturnLines = storeReturn.getStoreReturnLine();
			InboundOrder apiHeader = new InboundOrder();
			apiHeader.setOrderId(storeReturnHeader.getTransferOrderNumber());
			apiHeader.setRefDocumentNo(storeReturnHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(storeReturnHeader.getWareHouseId());
			apiHeader.setRefDocumentType("RETURN");			
			apiHeader.setInboundOrderTypeId(2L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			for (StoreReturnLine storeReturnLine : storeReturnLines) {
				InboundOrderLines apiLine = new InboundOrderLines();
				apiLine.setLineReference(storeReturnLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(storeReturnLine.getSku());							// ITM_CODE
				apiLine.setItemText(storeReturnLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setInvoiceNumber(storeReturnLine.getInvoiceNumber());			// INV_NO
				apiLine.setContainerNumber(storeReturnLine.getContainerNumber());		// CONT_NO
				apiLine.setSupplierCode(storeReturnLine.getStoreID());					// PARTNER_CODE
				apiLine.setSupplierPartNumber(storeReturnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
				apiLine.setManufacturerName(storeReturnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(storeReturnLine.getManufacturerPartNo());	// MFR_PART
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(storeReturnLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(storeReturnLine.getExpectedQty());				// ORD_QTY
				apiLine.setUom(storeReturnLine.getUom());								// ORD_UOM
				apiLine.setItemCaseQty(storeReturnLine.getPackQty());					// ITM_CASE_QTY
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			
			if (storeReturn.getStoreReturnLine() != null && !storeReturn.getStoreReturnLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("StoreReturn Order Success: " + createdOrder);
				return createdOrder;
			} else if (storeReturn.getStoreReturnLine() == null || storeReturn.getStoreReturnLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("StoreReturn Order Failed : " + createdOrder);
				throw new BadRequestException("StoreReturn Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	// SOReturn
	private InboundOrder saveSOReturn (SaleOrderReturn soReturn) {
		try {
			SOReturnHeader soReturnHeader = soReturn.getSoReturnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (soReturnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundOrder dbApiHeader = orderService.getOrderById(soReturnHeader.getReturnOrderReference());
			if (dbApiHeader != null) {
				throw new InboundOrderRequestException("Return Order Reference is already posted and it can't be duplicated.");
			}
						
			List<SOReturnLine> storeReturnLines = soReturn.getSoReturnLine();
			InboundOrder apiHeader = new InboundOrder();
			apiHeader.setOrderId(soReturnHeader.getReturnOrderReference());
			apiHeader.setRefDocumentNo(soReturnHeader.getReturnOrderReference());
			apiHeader.setWarehouseID(soReturnHeader.getWareHouseId());
			apiHeader.setRefDocumentType("RETURN");	
			apiHeader.setInboundOrderTypeId(4L);										// Hardcoded Value 4
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			for (SOReturnLine soReturnLine : storeReturnLines) {
				InboundOrderLines apiLine = new InboundOrderLines();
				apiLine.setLineReference(soReturnLine.getLineReference()); 				// IB_LINE_NO
				apiLine.setItemCode(soReturnLine.getSku());								// ITM_CODE
				apiLine.setItemText(soReturnLine.getSkuDescription()); 					// ITEM_TEXT
				apiLine.setInvoiceNumber(soReturnLine.getInvoiceNumber());				// INV_NO
				apiLine.setContainerNumber(soReturnLine.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(soReturnLine.getStoreID());						// PARTNER_CODE
				apiLine.setSupplierPartNumber(soReturnLine.getSupplierPartNumber());	// PARTNER_ITM_CODE
				apiLine.setManufacturerName(soReturnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(soReturnLine.getManufacturerPartNo());	// MFR_PART
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(soReturnLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(soReturnLine.getExpectedQty());					// ORD_QTY
				apiLine.setUom(soReturnLine.getUom());									// ORD_UOM
				apiLine.setItemCaseQty(soReturnLine.getPackQty());						// ITM_CASE_QTY
				apiLine.setSalesOrderReference(soReturnLine.getSalesOrderReference());	// REF_FIELD_4
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			
			if (soReturn.getSoReturnLine() != null && !soReturn.getSoReturnLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("Return Order Reference Order Success: " + createdOrder);
				return createdOrder;
			} else if (soReturn.getSoReturnLine() == null || soReturn.getSoReturnLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("Return Order Reference Order Failed : " + createdOrder);
				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	// InterWarehouseTransfer
	private InboundOrder saveInterWarehouseTransfer (InterWarehouseTransferIn interWarehouseTransferIn) {
		try {
			InterWarehouseTransferInHeader interWarehouseTransferInHeader = interWarehouseTransferIn.getInterWarehouseTransferInHeader();
			// Warehouse ID Validation
			validateWarehouseId (interWarehouseTransferInHeader.getToWhsId());
			
			// Checking for duplicate RefDocNumber
			InboundOrder dbApiHeader = orderService.getOrderById(interWarehouseTransferInHeader.getTransferOrderNumber());
			if (dbApiHeader != null) {
				throw new InboundOrderRequestException("InterWarehouseTransfer is already posted and it can't be duplicated.");
			}
						
			List<InterWarehouseTransferInLine> interWarehouseTransferInLines = interWarehouseTransferIn.getInterWarehouseTransferInLine();
			InboundOrder apiHeader = new InboundOrder();
			apiHeader.setOrderId(interWarehouseTransferInHeader.getTransferOrderNumber());
			apiHeader.setRefDocumentNo(interWarehouseTransferInHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(interWarehouseTransferInHeader.getToWhsId());
			apiHeader.setRefDocumentType("WH2WH");				// Hardcoded Value "WH to WH"
			apiHeader.setInboundOrderTypeId(3L);				// Hardcoded Value 3
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			for (InterWarehouseTransferInLine iwhTransferLine : interWarehouseTransferInLines) {
				InboundOrderLines apiLine = new InboundOrderLines();
				apiLine.setLineReference(iwhTransferLine.getLineReference()); 				// IB_LINE_NO
				apiLine.setItemCode(iwhTransferLine.getSku());								// ITM_CODE
				apiLine.setItemText(iwhTransferLine.getSkuDescription()); 					// ITEM_TEXT
				apiLine.setInvoiceNumber(iwhTransferLine.getInvoiceNumber());				// INV_NO
				apiLine.setContainerNumber(iwhTransferLine.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(iwhTransferLine.getFromWhsId());					// PARTNER_CODE
				apiLine.setSupplierPartNumber(iwhTransferLine.getSupplierPartNumber());		// PARTNER_ITM_CODE
				apiLine.setManufacturerName(iwhTransferLine.getManufacturerName());			// BRND_NM
				apiLine.setManufacturerPartNo(iwhTransferLine.getManufacturerPartNo());		// MFR_PART
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(iwhTransferLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(iwhTransferLine.getExpectedQty());					// ORD_QTY
				apiLine.setUom(iwhTransferLine.getUom());									// ORD_UOM
				apiLine.setItemCaseQty(iwhTransferLine.getPackQty());						// ITM_CASE_QTY
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			if (interWarehouseTransferIn.getInterWarehouseTransferInLine() != null && 
					!interWarehouseTransferIn.getInterWarehouseTransferInLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("InterWarehouseTransfer Order Success: " + createdOrder);
				return createdOrder;
			} else if (interWarehouseTransferIn.getInterWarehouseTransferInLine() == null || 
					interWarehouseTransferIn.getInterWarehouseTransferInLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrder createdOrder = orderService.createInboundOrders(apiHeader);
				log.info("InterWarehouseTransfer Order Failed : " + createdOrder);
				throw new BadRequestException("InterWarehouseTransfer Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~OUTBOUND~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
	// POST SOHeader
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
	
	// POST 
	private OutboundOrder saveSalesOrder(@Valid SalesOrder salesOrder) {
		try {
			SalesOrderHeader salesOrderHeader = salesOrder.getSalesOrderHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (salesOrderHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			OutboundOrder obOrder = orderService.getOBOrderById(salesOrderHeader.getSalesOrderNumber());
			
			if (obOrder != null) {
				throw new OutboundOrderRequestException("SalesOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<SalesOrderLine> salesOrderLines = salesOrder.getSalesOrderLine();
			OutboundOrder apiHeader = new OutboundOrder();
			apiHeader.setOrderId(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setWarehouseID(salesOrderHeader.getWareHouseId());
			apiHeader.setPartnerCode(salesOrderHeader.getStoreID());
			apiHeader.setPartnerName(salesOrderHeader.getStoreName());
			apiHeader.setRefDocumentNo(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setOutboundOrderTypeID(3L);							// Hardcoded Value "3"
			apiHeader.setRefDocumentType("SaleOrder");						// Hardcoded value "SaleOrder"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			for (SalesOrderLine soLine : salesOrderLines) {
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
			
			if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("SalesOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("SalesOrder Order Failed: " + createdOrder);
				throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 
	 * @param returnPO
	 * @return
	 */
	private OutboundOrder saveReturnPO (ReturnPO returnPO) {
		try {
			ReturnPOHeader returnPOHeader = returnPO.getReturnPOHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (returnPOHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			OutboundOrder obOrder = orderService.getOBOrderById(returnPOHeader.getPoNumber());
			
			if (obOrder != null) {
				throw new OutboundOrderRequestException("PONumber is already posted and it can't be duplicated.");
			}
						
			List<ReturnPOLine> returnPOLines = returnPO.getReturnPOLine();
			
			// Mongo Primary Key
			OutboundOrder apiHeader = new OutboundOrder();
			apiHeader.setOrderId(returnPOHeader.getPoNumber());
			apiHeader.setWarehouseID(returnPOHeader.getWareHouseId());
			apiHeader.setPartnerCode(returnPOHeader.getStoreID());
			apiHeader.setPartnerName(returnPOHeader.getStoreName());
			apiHeader.setRefDocumentNo(returnPOHeader.getPoNumber());
			apiHeader.setOutboundOrderTypeID(2L);							// Hardcoded Value "2"
			apiHeader.setRefDocumentType("RETURNPO");						// Hardcoded value "RETURNPO"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(returnPOHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			for (ReturnPOLine rpoLine : returnPOLines) {
				OutboundOrderLine apiLine = new OutboundOrderLine();
				apiLine.setLineReference(rpoLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(rpoLine.getSku());							// ITM_CODE
				apiLine.setItemText(rpoLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(rpoLine.getReturnQty());					// ORD_QTY
				apiLine.setUom(rpoLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(rpoLine.getOrderType());		// ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			
			if (returnPO.getReturnPOLine() != null && !returnPO.getReturnPOLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("ReturnPO Order Success: " + createdOrder);
				return apiHeader;
			} else if (returnPO.getReturnPOLine() == null || returnPO.getReturnPOLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
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
	 * @param interWarehouseTransfer
	 * @return
	 */
	private OutboundOrder saveIWHTransfer (InterWarehouseTransferOut interWarehouseTransfer) {
		try {
			InterWarehouseTransferOutHeader interWarehouseTransferOutHeader = 
					interWarehouseTransfer.getInterWarehouseTransferOutHeader();
			// Warehouse ID Validation
			validateWarehouseId (interWarehouseTransferOutHeader.getFromWhsID());
			
			// Checking for duplicate RefDocNumber
			OutboundOrder obOrder = orderService.getOBOrderById(interWarehouseTransferOutHeader.getTransferOrderNumber());
			
			if (obOrder != null) {
				throw new OutboundOrderRequestException("TransferOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<InterWarehouseTransferOutLine> interWarehouseTransferOutLines = 
					interWarehouseTransfer.getInterWarehouseTransferOutLine();
			
			// Mongo Primary Key
			OutboundOrder apiHeader = new OutboundOrder();
			apiHeader.setOrderId(interWarehouseTransferOutHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(interWarehouseTransferOutHeader.getFromWhsID());
			apiHeader.setPartnerCode(interWarehouseTransferOutHeader.getToWhsID());
			apiHeader.setPartnerName(interWarehouseTransferOutHeader.getStoreName());
			apiHeader.setRefDocumentNo(interWarehouseTransferOutHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(1L);							// Hardcoded Value "1"
			apiHeader.setRefDocumentType("WH2WH");							// Hardcoded value "WH to WH"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(interWarehouseTransferOutHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			for (InterWarehouseTransferOutLine iwhTransferLine : interWarehouseTransferOutLines) {
				OutboundOrderLine apiLine = new OutboundOrderLine();
				apiLine.setLineReference(iwhTransferLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(iwhTransferLine.getSku());							// ITM_CODE
				apiLine.setItemText(iwhTransferLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(iwhTransferLine.getOrderedQty());					// ORD_QTY
				apiLine.setUom(iwhTransferLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(iwhTransferLine.getOrderType());		// ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				orderLines.add(apiLine);
			}
			apiHeader.setLines(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			
			if (interWarehouseTransfer.getInterWarehouseTransferOutLine() != null && 
					!interWarehouseTransfer.getInterWarehouseTransferOutLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("InterWarehouseTransferOut Order Success: " + createdOrder);
				return apiHeader;
			} else if (interWarehouseTransfer.getInterWarehouseTransferOutLine() == null || 
					interWarehouseTransfer.getInterWarehouseTransferOutLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrder createdOrder = orderService.createOutboundOrders(apiHeader);
				log.info("InterWarehouseTransferOut Order Failed: " + createdOrder);
				throw new BadRequestException("InterWarehouseTransferOut Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

//==================================================Inbound V2===========================================================================
	/**
	 *
	 * @param asnv2
	 * @return
	 */
	public InboundOrderV2 postWarehouseASNV2 (ASNV2 asnv2) {
		log.info("ASNV2Header received from External: " + asnv2);
		InboundOrderV2 savedAsnV2Header = saveASNV2 (asnv2);
		log.info("savedAsnV2Header: " + savedAsnV2Header);
		return savedAsnV2Header;
	}

	// POST ASNV2Header
	private InboundOrderV2 saveASNV2 (ASNV2 asnv2) {
		try {
			ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
			List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();
			InboundOrderV2 apiHeader = new InboundOrderV2();

			apiHeader.setOrderId(asnV2Header.getAsnNumber());
			apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
			apiHeader.setBranchCode(asnV2Header.getBranchCode());
			apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());
			apiHeader.setRefDocumentType("SupplierInvoice");
			apiHeader.setInboundOrderTypeId(1L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
			apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

			apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
			apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
			apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());

			// Get Warehouse
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							asnV2Header.getCompanyCode(),
							asnV2Header.getBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
//			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
			for (ASNLineV2 asnLineV2 : asnLineV2s) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setLineReference(asnLineV2.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(asnLineV2.getSku());							// ITM_CODE
				apiLine.setItemText(asnLineV2.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setContainerNumber(asnLineV2.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(asnLineV2.getSupplierCode());				// PARTNER_CODE
				apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
				apiLine.setManufacturerName(asnLineV2.getManufacturerName());		// BRAND_NM
				apiLine.setManufacturerCode(asnLineV2.getManufacturerCode());
				apiLine.setOrigin(asnLineV2.getOrigin());
				apiLine.setCompanyCode(asnLineV2.getCompanyCode());
				apiLine.setBranchCode(asnLineV2.getBranchCode());
				apiLine.setExpectedQty(asnLineV2.getExpectedQty());
				apiLine.setSupplierName(asnLineV2.getSupplierName());
				apiLine.setBrand(asnLineV2.getBrand());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
				apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
				apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
				apiLine.setInboundOrderTypeId(1L);

				apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
				apiLine.setReceivedBy(asnLineV2.getReceivedBy());
				apiLine.setReceivedQty(asnLineV2.getReceivedQty());
				apiLine.setReceivedDate(asnLineV2.getReceivedDate());
				apiLine.setIsCancelled(asnLineV2.getIsCancelled());
				apiLine.setIsCompleted(asnLineV2.getIsCompleted());

				apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
				apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());

				if (asnLineV2.getExpectedDate() != null) {
					if (asnLineV2.getExpectedDate().contains("-")) {
						// EA_DATE
						try {
							Date reqDelDate = new Date();
							if(asnLineV2.getExpectedDate().length() > 10) {
								reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
							}
							if(asnLineV2.getExpectedDate().length() == 10) {
								reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
							}
							apiLine.setExpectedDate(reqDelDate);
						} catch (Exception e) {
							e.printStackTrace();
							throw new BadRequestException("Date format should be MM-dd-yyyy");
						}
					}
					if (asnLineV2.getExpectedDate().contains("/")) {
						// EA_DATE
						try {
							ZoneId defaultZoneId = ZoneId.systemDefault();
							String sdate = asnLineV2.getExpectedDate();
							String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
							String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
							secondHalf = "/20" + secondHalf;
							sdate = firstHalf + secondHalf;
							log.info("sdate--------> : " + sdate);

							LocalDate localDate = DateUtils.dateConv2(sdate);
							log.info("localDate--------> : " + localDate);
							Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
							apiLine.setExpectedDate(date);
						} catch (Exception e) {
							e.printStackTrace();
							throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
						}
					}
				}

				apiLine.setOrderedQty(asnLineV2.getExpectedQty());				// ORD_QTY
				apiLine.setUom(asnLineV2.getUom());								// ORD_UOM
				apiLine.setPackQty(asnLineV2.getPackQty());					// ITM_CASE_QTY
				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("ASNV2 Order Success : " + createdOrder);
				return createdOrder;
			} else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("ASNV2 Order Failed : " + createdOrder);
				throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public InboundOrderV2 postWarehouseStockReceipt (StockReceiptHeader stockReceipt) {
		log.info("StockReceipt received from External: " + stockReceipt);
		InboundOrderV2 savedStockReceipt = saveStockReceipt (stockReceipt);
		log.info("savedStockReceipt: " + savedStockReceipt);
		return savedStockReceipt;
	}

	// POST StockReceiptHeader
	private InboundOrderV2 saveStockReceipt (StockReceiptHeader stockReceipt) {
		try {
//			StockReceiptHeader stockReceiptHeader = stockReceipt.getStockReceiptHeader();
			List<StockReceiptLine> stockReceiptLines = stockReceipt.getStockReceiptLines();

			InboundOrderV2 apiHeader = new InboundOrderV2();

			apiHeader.setOrderId(stockReceipt.getReceiptNo());
			apiHeader.setCompanyCode(stockReceipt.getCompanyCode());
			apiHeader.setBranchCode(stockReceipt.getBranchCode());
			apiHeader.setRefDocumentNo(stockReceipt.getReceiptNo());

			apiHeader.setIsCompleted(stockReceipt.getIsCompleted());
			apiHeader.setUpdatedOn(stockReceipt.getUpdatedOn());

			apiHeader.setRefDocumentType("DirectReceipt");
			apiHeader.setInboundOrderTypeId(5L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(stockReceipt.getMiddlewareId());
			apiHeader.setMiddlewareTable(stockReceipt.getMiddlewareTable());

			// Get Warehouse
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							stockReceipt.getCompanyCode(),
							stockReceipt.getBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
			for (StockReceiptLine stockReceiptLine : stockReceiptLines) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setLineReference(stockReceiptLine.getLineNoForEachItem()); 			// IB_LINE_NO
				apiLine.setItemCode(stockReceiptLine.getItemCode());							// ITM_CODE
				apiLine.setItemText(stockReceiptLine.getItemDescription()); 				// ITEM_TEXT
//				apiLine.setContainerNumber(stockReceiptLine.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(stockReceiptLine.getSupplierCode());				// PARTNER_CODE
				apiLine.setSupplierPartNumber(stockReceiptLine.getSupplierPartNo());  // PARTNER_ITM_CODE
				apiLine.setManufacturerName(stockReceiptLine.getManufacturerShortName());		// BRAND_NM
				apiLine.setManufacturerCode(stockReceiptLine.getManufacturerCode());
				apiLine.setSupplierName(stockReceiptLine.getSupplierName());
				apiLine.setExpectedQty(stockReceiptLine.getReceiptQty());
				apiLine.setOrderId(stockReceiptLine.getReceiptNo());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setCompanyCode(stockReceiptLine.getCompanyCode());
				apiLine.setBranchCode(stockReceiptLine.getBranchCode());
				apiLine.setManufacturerFullName(stockReceiptLine.getManufacturerFullName());
				apiLine.setIsCompleted(stockReceiptLine.getIsCompleted());
				apiLine.setMiddlewareHeaderId(stockReceiptLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareId(stockReceiptLine.getMiddlewareId());
				apiLine.setMiddlewareTable(stockReceiptLine.getMiddlewareTable());
				apiLine.setInboundOrderTypeId(5L);

				if (stockReceiptLine.getReceiptDate() != null) {
//						 EA_DATE
							apiLine.setExpectedDate(stockReceiptLine.getReceiptDate());
					}

				apiLine.setOrderedQty(stockReceiptLine.getReceiptQty());				// ORD_QTY
				apiLine.setUom(stockReceiptLine.getUnitOfMeasure());								// ORD_UOM
				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			if (stockReceipt.getStockReceiptLines() != null && !stockReceipt.getStockReceiptLines().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("stockReceipt Order Success : " + createdOrder);
				return createdOrder;
			} else if (stockReceipt.getStockReceiptLines() == null || stockReceipt.getStockReceiptLines().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("stockReceipt Order Failed : " + createdOrder);
				throw new BadRequestException("stockReceipt Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * @param soReturnV2
	 * @return
	 */
	public InboundOrderV2 postSOReturnV2(SaleOrderReturnV2 soReturnV2) {
		log.info("StoreReturnHeader received from External: " + soReturnV2);
		InboundOrderV2 savedSOReturn = saveSOReturnV2(soReturnV2);
		log.info("soReturnHeader: " + savedSOReturn);
		return savedSOReturn;
	}

	// SOReturnV2
	private InboundOrderV2 saveSOReturnV2(SaleOrderReturnV2 soReturnV2) {
		try {
			SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
			List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

			InboundOrderV2 apiHeader = new InboundOrderV2();
			apiHeader.setTransferOrderNumber(soReturnHeaderV2.getTransferOrderNumber());
			apiHeader.setRefDocumentNo(soReturnHeaderV2.getTransferOrderNumber());
			apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
			apiHeader.setOrderId(soReturnHeaderV2.getTransferOrderNumber());
			apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
			apiHeader.setRefDocumentType("SalesReturn");
			apiHeader.setInboundOrderTypeId(2L);                                        // Hardcoded Value 2
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
			apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
			apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
			apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
			apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());

			// Get Warehouse
			Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							soReturnHeaderV2.getCompanyCode(),
							soReturnHeaderV2.getBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

			Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
			for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
				apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
				apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
				apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
				apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
				apiLine.setManufacturerName(soReturnLineV2.getManufacturerName());        // BRND_NM
				apiLine.setStoreID(soReturnLineV2.getStoreID());
				apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
				apiLine.setUom(soReturnLineV2.getUom());
				apiLine.setPackQty(soReturnLineV2.getPackQty());
				apiLine.setOrigin(soReturnLineV2.getOrigin());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
				apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
				apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
				apiLine.setInboundOrderTypeId(2L);
				// EA_DATE
				try {
					Date reqDelDate = new Date();
//					if (soReturnLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(soReturnLineV2.getExpectedDate());
//					}
					if (soReturnLineV2.getExpectedDate() != null) {
						reqDelDate = DateUtils.convertStringToDate2(soReturnLineV2.getExpectedDate());
					}
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}

				apiLine.setManufacturerCode(soReturnLineV2.getManufacturerCode());
				apiLine.setBrand(soReturnLineV2.getBrand());
				orderLinesV2.add(apiLine);
			}
			apiHeader.setLine(orderLinesV2);
			apiHeader.setOrderProcessedOn(new Date());

			if (soReturnV2.getSoReturnLine() != null && !soReturnV2.getSoReturnLine().isEmpty()) {

				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("Return Order Reference Order Success: " + createdOrderV2);
				return createdOrderV2;
			} else if (soReturnV2.getSoReturnLine() == null || soReturnV2.getSoReturnLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("Return Order Reference Order Failed : " + createdOrderV2);
				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}


	/**
	 * @param interWarehouseTransferInV2
	 * @return
	 */
	public InboundOrderV2 postInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
		InboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
		log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
		return savedIWHReturnV2;
	}


	// InterWarehouseTransferInV2
	private InboundOrderV2 saveInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		try {
			InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
			List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

			InboundOrderV2 apiHeader = new InboundOrderV2();
			apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
			apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
			apiHeader.setInboundOrderTypeId(4L);                // Hardcoded Value 3
			apiHeader.setRefDocumentType("WMS to WMS");
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
			apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
			apiHeader.setTransferOrderDate(interWarehouseTransferInHeaderV2.getTransferOrderDate());
			apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
			apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());
			apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
			apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());

			// Get Warehouse
			Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							interWarehouseTransferInHeaderV2.getToCompanyCode(),
							interWarehouseTransferInHeaderV2.getToBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

			Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
			for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
				apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
				apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
				apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
				apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
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
				apiLine.setInboundOrderTypeId(4L);

				apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
				apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
				apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

				orderLinesV2.add(apiLine);

				// EA_DATE
				try {
					ZoneId defaultZoneId = ZoneId.systemDefault();
					String sdate = iwhTransferLineV2.getExpectedDate();
					String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
					String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
					secondHalf = "/20" + secondHalf;
					sdate = firstHalf + secondHalf;
					log.info("sdate--------> : " + sdate);

					LocalDate localDate = DateUtils.dateConv2(sdate);
					log.info("localDate--------> : " + localDate);
					Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
					apiLine.setExpectedDate(date);
				} catch (Exception e) {
					e.printStackTrace();
					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
				}
			}
			apiHeader.setLine(orderLinesV2);
			apiHeader.setOrderProcessedOn(new Date());
			if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
					!interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
				return createdOrderV2;
			} else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
					interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
				throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}



	/**
	 * @param
	 * @return
	 */
	public InboundOrderV2 postB2bTransferIn(B2bTransferIn b2bTransferIn) {
		log.info("B2bTransferIn received from External: " + b2bTransferIn);
		InboundOrderV2 savedB2bTransferIn = saveB2BTransferIn(b2bTransferIn);
		log.info("B2bTransferIn: " + savedB2bTransferIn);
		return savedB2bTransferIn;
	}

	// B2bTransferIn
	private InboundOrderV2 saveB2BTransferIn(B2bTransferIn b2bTransferIn) {
		try {
			B2bTransferInHeader b2BTransferInHeader = b2bTransferIn.getB2bTransferInHeader();
			List<B2bTransferInLine> b2bTransferInLines = b2bTransferIn.getB2bTransferLine();

			InboundOrderV2 apiHeader = new InboundOrderV2();
			apiHeader.setTransferOrderNumber(b2BTransferInHeader.getTransferOrderNumber());
			apiHeader.setCompanyCode(b2BTransferInHeader.getCompanyCode());
			apiHeader.setBranchCode(b2BTransferInHeader.getBranchCode());
			apiHeader.setOrderId(b2BTransferInHeader.getTransferOrderNumber());
			apiHeader.setRefDocumentNo(b2BTransferInHeader.getTransferOrderNumber());
			apiHeader.setRefDocumentType("Non-WMS to WMS");
			apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(b2BTransferInHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(b2BTransferInHeader.getMiddlewareTable());
			apiHeader.setSourceBranchCode(b2BTransferInHeader.getSourceBranchCode());
			apiHeader.setSourceCompanyCode(b2BTransferInHeader.getSourceCompanyCode());
			apiHeader.setTransferOrderDate(b2BTransferInHeader.getTransferOrderDate());
			apiHeader.setIsCompleted(b2BTransferInHeader.getIsCompleted());
			apiHeader.setUpdatedOn(b2BTransferInHeader.getUpdatedOn());

			// Get Warehouse
			Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							b2BTransferInHeader.getCompanyCode(),
							b2BTransferInHeader.getBranchCode(),
							"EN",
							0L
					);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
			for (B2bTransferInLine b2bTransferInLine : b2bTransferInLines) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setLineReference(b2bTransferInLine.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(b2bTransferInLine.getSku());                                // ITM_CODE
				apiLine.setItemText(b2bTransferInLine.getSkuDescription());                     // ITEM_TEXT
				apiLine.setSourceBranchCode(b2bTransferInLine.getStoreID());
				apiLine.setSupplierPartNumber(b2bTransferInLine.getSupplierPartNumber());
				apiLine.setManufacturerName(b2bTransferInLine.getManufacturerName());
				apiLine.setExpectedQty(b2bTransferInLine.getExpectedQty());
				apiLine.setCountryOfOrigin(b2bTransferInLine.getOrigin());
				apiLine.setManufacturerCode(b2bTransferInLine.getManufacturerCode());
				apiLine.setBrand(b2bTransferInLine.getBrand());
				apiLine.setSupplierName(b2bTransferInLine.getSupplierName());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setManufacturerFullName(b2bTransferInLine.getManufacturerFullName());
				apiLine.setStoreID(b2bTransferInLine.getStoreID());
				apiLine.setInboundOrderTypeId(3L);

				apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());
				apiLine.setMiddlewareId(b2bTransferInLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(b2bTransferInLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(b2bTransferInLine.getMiddlewareTable());
//				apiLine.setOrigin(b2bTransferInLine.getOrigin());

				apiLine.setIsCompleted(b2bTransferInLine.getIsCompleted());
				apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());

				// EA_DATE
				try {
					Date reqDelDate = new Date();
//					if (b2bTransferInLine.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(b2bTransferInLine.getExpectedDate());
//					}
					if (b2bTransferInLine.getExpectedDate() != null) {
						reqDelDate = DateUtils.convertStringToDate2(b2bTransferInLine.getExpectedDate());
					}
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
				}

				apiLine.setUom(b2bTransferInLine.getUom());                                    // ORD_UOM

				apiLine.setItemCaseQty(Double.valueOf(b2bTransferInLine.getPackQty()));        // ITM_CASE_QTY
				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (b2bTransferIn.getB2bTransferLine() != null && !b2bTransferIn.getB2bTransferLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("Return Order Reference Order Success: " + createdOrder);
				return createdOrder;
			} else if (b2bTransferIn.getB2bTransferLine() == null || b2bTransferIn.getB2bTransferLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
				log.info("Return Order Reference Order Failed : " + createdOrder);
				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

//	private InboundOrderV2 saveB2BTransferIn(B2bTransferIn b2bTransferIn) {
//		try {
//			B2bTransferInHeader b2BTransferInHeader = b2bTransferIn.getB2bTransferInHeader();
//			List<B2bTransferInLine> b2bTransferInLines = b2bTransferIn.getB2bTransferLine();
//
//			InboundOrderV2 apiHeader = new InboundOrderV2();
//			apiHeader.setTransferOrderNumber(b2BTransferInHeader.getTransferOrderNumber());
//			apiHeader.setCompanyCode(b2BTransferInHeader.getCompanyCode());
//			apiHeader.setBranchCode(b2BTransferInHeader.getBranchCode());
//			apiHeader.setOrderId(b2BTransferInHeader.getTransferOrderNumber());
//			apiHeader.setRefDocumentNo(b2BTransferInHeader.getTransferOrderNumber());
//			apiHeader.setRefDocumentType("Non-WMS to WMS");
//			apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
//			apiHeader.setOrderReceivedOn(new Date());
//			apiHeader.setMiddlewareId(b2BTransferInHeader.getMiddlewareId());
//			apiHeader.setMiddlewareTable(b2BTransferInHeader.getMiddlewareTable());
//			apiHeader.setSourceBranchCode(b2BTransferInHeader.getSourceBranchCode());
//			apiHeader.setSourceCompanyCode(b2BTransferInHeader.getSourceCompanyCode());
//			apiHeader.setTransferOrderDate(b2BTransferInHeader.getTransferOrderDate());
//			apiHeader.setIsCompleted(b2BTransferInHeader.getIsCompleted());
//			apiHeader.setUpdatedOn(b2BTransferInHeader.getUpdatedOn());
//
//			// Get Warehouse
//			Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> dbWarehouse =
//					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//							b2BTransferInHeader.getCompanyCode(),
//							b2BTransferInHeader.getBranchCode(),
//							"EN",
//							0L
//					);
//			if(dbWarehouse != null && !dbWarehouse.isEmpty()) {
//				apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
//			}
//
//			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
//			for (B2bTransferInLine b2bTransferInLine : b2bTransferInLines) {
//				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
//				apiLine.setLineReference(b2bTransferInLine.getLineReference());                 // IB_LINE_NO
//				apiLine.setItemCode(b2bTransferInLine.getSku());                                // ITM_CODE
//				apiLine.setItemText(b2bTransferInLine.getSkuDescription());                     // ITEM_TEXT
//				apiLine.setSourceBranchCode(b2bTransferInLine.getStoreID());
//				apiLine.setSupplierPartNumber(b2bTransferInLine.getSupplierPartNumber());
//				apiLine.setManufacturerName(b2bTransferInLine.getManufacturerName());
//				apiLine.setExpectedQty(b2bTransferInLine.getExpectedQty());
//				apiLine.setCountryOfOrigin(b2bTransferInLine.getOrigin());
//				apiLine.setManufacturerCode(b2bTransferInLine.getManufacturerCode());
//				apiLine.setBrand(b2bTransferInLine.getBrand());
//				apiLine.setSupplierName(b2bTransferInLine.getSupplierName());
//				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setManufacturerFullName(b2bTransferInLine.getManufacturerFullName());
//				apiLine.setStoreID(b2bTransferInLine.getStoreID());
//
//				apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());
//				apiLine.setMiddlewareId(b2bTransferInLine.getMiddlewareId());
//				apiLine.setMiddlewareHeaderId(b2bTransferInLine.getMiddlewareHeaderId());
//				apiLine.setMiddlewareTable(b2bTransferInLine.getMiddlewareTable());
////				apiLine.setOrigin(b2bTransferInLine.getOrigin());
//
//				apiLine.setIsCompleted(b2bTransferInLine.getIsCompleted());
//				apiLine.setTransferOrderNumber(b2bTransferInLine.getTransferOrderNo());
//
//				// EA_DATE
//				try {
//					Date reqDelDate = new Date();
////					if (b2bTransferInLine.getExpectedDate().length() > 10) {
////						reqDelDate = DateUtils.convertStringToDateWithTime(b2bTransferInLine.getExpectedDate());
////					}
//					if (b2bTransferInLine.getExpectedDate() != null) {
//						reqDelDate = DateUtils.convertStringToDate2(b2bTransferInLine.getExpectedDate());
//					}
//					apiLine.setExpectedDate(reqDelDate);
//				} catch (Exception e) {
//					throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
//				}
//
//				apiLine.setUom(b2bTransferInLine.getUom());                                    // ORD_UOM
//
//				apiLine.setItemCaseQty(Double.valueOf(b2bTransferInLine.getPackQty()));        // ITM_CASE_QTY
//				orderLines.add(apiLine);
//			}
//			apiHeader.setLine(orderLines);
//			apiHeader.setOrderProcessedOn(new Date());
//
//			if (b2bTransferIn.getB2bTransferLine() != null && !b2bTransferIn.getB2bTransferLine().isEmpty()) {
//				apiHeader.setProcessedStatusId(0L);
//				log.info("apiHeader : " + apiHeader);
//				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
//				log.info("Return Order Reference Order Success: " + createdOrder);
//				return createdOrder;
//			} else if (b2bTransferIn.getB2bTransferLine() == null || b2bTransferIn.getB2bTransferLine().isEmpty()) {
//				// throw the error as Lines are Empty and set the Indicator as '100'
//				apiHeader.setProcessedStatusId(100L);
//				log.info("apiHeader : " + apiHeader);
//				InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
//				log.info("Return Order Reference Order Failed : " + createdOrder);
//				throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return null;
//	}

	/**
	 * @param interWarehouseTransferInV2
	 * @return
	 */
	public InboundOrderV2 postInterWarehouseTransferInV2(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
		InboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2(interWarehouseTransferInV2);
		log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
		return savedIWHReturnV2;
	}

	// InterWarehouseTransferInV2
	private InboundOrderV2 saveInterWarehouseTransferInV2(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
		try {
			InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
			List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

			InboundOrderV2 apiHeader = new InboundOrderV2();
			apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
			apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
			apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
			apiHeader.setInboundOrderTypeId(4L);                // Hardcoded Value 3
			apiHeader.setRefDocumentType("WMS to WMS");
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
			apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());
			apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
			apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
			apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
			apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());

			// Get Warehouse
			Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							interWarehouseTransferInHeaderV2.getToCompanyCode(),
							interWarehouseTransferInHeaderV2.getToBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
			Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
			for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
				apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
				apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
				apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
				apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
				apiLine.setManufacturerName(iwhTransferLineV2.getManufacturerName());            // BRND_NM
				apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
				apiLine.setUom(iwhTransferLineV2.getUom());
				apiLine.setPackQty(iwhTransferLineV2.getPackQty());
				apiLine.setOrigin(iwhTransferLineV2.getOrigin());
				apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
				apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
				apiLine.setInboundOrderTypeId(4L);

				apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
				apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

				// EA_DATE
				try {
					Date reqDelDate = new Date();
//					if (iwhTransferLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(iwhTransferLineV2.getExpectedDate());
//					}
					if (iwhTransferLineV2.getExpectedDate() != null) {
						reqDelDate = DateUtils.convertStringToDate2(iwhTransferLineV2.getExpectedDate());
					}
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}
				apiLine.setBrand(iwhTransferLineV2.getBrand());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				orderLinesV2.add(apiLine);
			}
			apiHeader.setLine(orderLinesV2);
			apiHeader.setOrderProcessedOn(new Date());
			if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
					!interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
				return createdOrderV2;
			} else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
					interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
				throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}


	//-------------------------------------------------------------------------------------------------------------------------------------------------
	// ASN
	public AXApiResponse postASNConfirmationV2 (com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNV2 asn,
											  String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceAsnUrl());
		HttpEntity<?> entity = new HttpEntity<>(asn, headers);
		ResponseEntity<AXApiResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// StoreReturn
	public AXApiResponse postStoreReturnConfirmationV2 (
			com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturn storeReturn,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
		HttpEntity<?> entity = new HttpEntity<>(storeReturn, headers);
		ResponseEntity<AXApiResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// Sale Order Returns
	public AXApiResponse postSOReturnConfirmationV2 (
			com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturn soReturn,
			String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
		HttpEntity<?> entity = new HttpEntity<>(soReturn, headers);
		ResponseEntity<AXApiResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/**
	 *
	 * @param iwhTransfer
	 * @param access_token
	 * @return
	 */
	public AXApiResponse postInterWarehouseTransferConfirmationV2(InterWarehouseTransferInV2 iwhTransfer,
																String access_token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "AX-API Rest service");
		headers.add("Authorization", "Bearer " + access_token);

		UriComponentsBuilder builder =
				UriComponentsBuilder.fromHttpUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
		HttpEntity<?> entity = new HttpEntity<>(iwhTransfer, headers);
		ResponseEntity<AXApiResponse> result =
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AXApiResponse.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	/*---------------------------------CycleCountOrder------------------------------------------*/

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


//====================================================OutBound V2===========================================================================

	/**
	 * ShipmentOrder
	 *
	 * @param shipmenOrder
	 * @return
	 */
	public ShipmentOrderV2 postSOV2(ShipmentOrderV2 shipmenOrder, boolean isRerun) throws ParseException {
		log.info("ShipmenOrder received from External: " + shipmenOrder);
		OutboundOrderV2 savedSoHeader = saveSOV2(shipmenOrder, isRerun);                        // Without Nongo
		log.info("savedSoHeader: " + savedSoHeader.getRefDocumentNo());
		return shipmenOrder;
	}

	/**
	 * @param salesOrder
	 * @return
	 */
	public SalesOrderV2 postSalesOrderV2(SalesOrderV2 salesOrder) throws ParseException {
		log.info("SalesOrderHeader received from External: " + salesOrder);
		OutboundOrderV2 savedSoHeader = saveSalesOrderV2(salesOrder);                                // Without Nongo
		log.info("salesOrderHeader: " + savedSoHeader);
		return salesOrder;
	}

	/**
	 * @param returnPO
	 * @return
	 */
	public ReturnPOV2 postReturnPOV2(ReturnPOV2 returnPO) throws ParseException {
		log.info("ReturnPOHeader received from External: " + returnPO);
		OutboundOrderV2 savedReturnPOHeader = saveReturnPOV2(returnPO);                    // Without Nongo
		log.info("savedReturnPOHeader: " + savedReturnPOHeader);
		return returnPO;
	}

	/**
	 * @param interWarehouseTransfer
	 * @return
	 */
	public InterWarehouseTransferOutV2 postInterWarehouseTransferOutboundV2(InterWarehouseTransferOutV2 interWarehouseTransfer) throws ParseException {
		log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransfer);
		OutboundOrderV2 savedInterWarehouseTransferHeader = saveIWHTransferV2(interWarehouseTransfer);                                                    // Without Nongo
		log.info("savedInterWarehouseTransferHeader: " + savedInterWarehouseTransferHeader);
		return interWarehouseTransfer;
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

	/*---------------------------------------------------------------------------------------------------------------------------------*/

//	// POST ShipmentOrderV2
	private OutboundOrderV2 saveSOV2(ShipmentOrderV2 shipmentOrder, boolean isRerun) throws ParseException {
		try {
			SOHeaderV2 soHeader = shipmentOrder.getSoHeader();

			Optional<Warehouse> warehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							soHeader.getCompanyCode(), soHeader.getBranchCode(), "EN", 0L);
			// Warehouse ID Validation
			validateWarehouseId(warehouse.get().getWarehouseId());

			// Checking for duplicate RefDocNumber
//			OutboundOrderV2 obOrder = orderService.getOBOrderByIdV2(soHeader.getTransferOrderNumber());
//			if (obOrder != null) {
//				throw new OutboundOrderRequestException("TransferOrderNumber is getting duplicated. This order already exists in the System.");
//			}
//
			List<SOLineV2> soLines = shipmentOrder.getSoLine();

			OutboundOrderV2 apiHeader = new OutboundOrderV2();

			apiHeader.setBranchCode(soHeader.getBranchCode());
			apiHeader.setCompanyCode(soHeader.getCompanyCode());
			apiHeader.setWarehouseID(warehouse.get().getWarehouseId());

			apiHeader.setMiddlewareId(soHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(soHeader.getMiddlewareTable());
			apiHeader.setOrderId(soHeader.getTransferOrderNumber());
			apiHeader.setPartnerCode(soHeader.getStoreID());
			apiHeader.setPartnerName(soHeader.getStoreName());
			apiHeader.setRefDocumentNo(soHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(0L);
			apiHeader.setRefDocumentType("WMS to Non-WMS");                        // Hardcoded value "SO"
			apiHeader.setCustomerType("TRANSVERSE");								//HardCoded
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setTargetCompanyCode(soHeader.getTargetCompanyCode());
			apiHeader.setTargetBranchCode(soHeader.getTargetBranchCode());

			try {
				Date date = DateUtils.convertStringToDate2(soHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(date);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					soHeader.getCompanyCode(), soHeader.getBranchCode(), warehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setBranchName(iKeyValuePair.getPlantDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (SOLineV2 soLine : soLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();

				apiLine.setBrand(soLine.getManufacturerFullName());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(apiHeader.getBranchCode());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(apiHeader.getCompanyCode());
				apiLine.setStoreID(soLine.getStoreID());

				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
				apiLine.setUom(soLine.getUom());                                // ORD_UOM
				apiLine.setManufacturerCode(soLine.getManufacturerCode());
				apiLine.setManufacturerName(soLine.getManufacturerName());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				apiLine.setCustomerType("TRANSVERSE");								//HardCoded
				apiLine.setOutboundOrderTypeID(0L);

				apiLine.setTransferOrderNumber(soLine.getTransferOrderNumber());
				apiLine.setMiddlewareId(soLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (shipmentOrder.getSoLine() != null && !shipmentOrder.getSoLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("ShipmentOrder Order Success: " + createdOrder);
				return apiHeader;
			} else if (shipmentOrder.getSoLine() == null || shipmentOrder.getSoLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader: " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("ShipmentOrder Order Failed: " + createdOrder);
				throw new BadRequestException("ShipmentOrder Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	// POST ShipmentOrderV2
//	private OutboundOrderV2 saveSOV2(ShipmentOrderV2 shipmentOrder, boolean isRerun) {
//		try {
//			SOHeaderV2 soHeader = shipmentOrder.getSoHeader();
//
//			Optional<Warehouse> warehouse =
//					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//							soHeader.getCompanyCode(), soHeader.getBranchCode(), "EN", 0L);
//			// Warehouse ID Validation
////			validateWarehouseId(warehouse.get().getWarehouseId());
//
//			// Checking for duplicate RefDocNumber
//			OutboundOrderV2 obOrder = orderService.getOBOrderByIdV2(soHeader.getTransferOrderNumber());
//			if (obOrder != null) {
//				throw new OutboundOrderRequestException("TransferOrderNumber is getting duplicated. This order already exists in the System.");
//			}
//
//			List<SOLineV2> soLines = shipmentOrder.getSoLine();
//
//			OutboundOrderV2 apiHeader = new OutboundOrderV2();
//
//			apiHeader.setBranchCode(soHeader.getBranchCode());
//			apiHeader.setCompanyCode(soHeader.getCompanyCode());
//			if(warehouse != null && !warehouse.isEmpty()) {
//				apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
//			}
//
//			apiHeader.setMiddlewareId(soHeader.getMiddlewareId());
//			apiHeader.setMiddlewareTable(soHeader.getMiddlewareTable());
//			apiHeader.setOrderId(soHeader.getTransferOrderNumber());
//			apiHeader.setPartnerCode(soHeader.getStoreID());
//			apiHeader.setPartnerName(soHeader.getStoreName());
//			apiHeader.setRefDocumentNo(soHeader.getTransferOrderNumber());
//			apiHeader.setOutboundOrderTypeID(0L);
//			apiHeader.setRefDocumentType("WMS to Non-WMS");                        // Hardcoded value "SO"
//			apiHeader.setOrderReceivedOn(new Date());
//			apiHeader.setTargetCompanyCode(soHeader.getTargetCompanyCode());
//			apiHeader.setTargetBranchCode(soHeader.getTargetBranchCode());
//
//			try {
//				Date date = DateUtils.convertStringToDate2(soHeader.getRequiredDeliveryDate());
//				apiHeader.setRequiredDeliveryDate(date);
//			} catch (Exception e) {
//				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
//			}
//
////			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
////					soHeader.getCompanyCode(), soHeader.getBranchCode(), warehouse.get().getWarehouseId());
////			if (iKeyValuePair != null) {
////				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
////				apiHeader.setBranchName(iKeyValuePair.getPlantDesc());
////				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
////			}
//
//			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
//			for (SOLineV2 soLine : soLines) {
//				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
//
//				apiLine.setBrand(soLine.getManufacturerFullName());
//				apiLine.setOrigin(soLine.getOrigin());
//				apiLine.setPackQty(soLine.getPackQty());
//				apiLine.setExpectedQty(soLine.getExpectedQty());
//				apiLine.setSupplierName(soLine.getSupplierName());
//				apiLine.setSourceBranchCode(apiHeader.getBranchCode());
//				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
//				apiLine.setFromCompanyCode(apiHeader.getCompanyCode());
//				apiLine.setStoreID(soLine.getStoreID());
//
//				apiLine.setRefField1ForOrderType(soLine.getOrderType());
//				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
//				apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
//				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
//				apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
//				apiLine.setUom(soLine.getUom());                                // ORD_UOM
//				apiLine.setManufacturerCode(soLine.getManufacturerCode());
//				apiLine.setManufacturerName(soLine.getManufacturerName());
//				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
//				apiLine.setOrderId(apiHeader.getOrderId());
//
//				apiLine.setTransferOrderNumber(soLine.getTransferOrderNumber());
//				apiLine.setMiddlewareId(soLine.getMiddlewareId());
//				apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
//				apiLine.setMiddlewareTable(soLine.getMiddlewareTable());
//
//				orderLines.add(apiLine);
//			}
//			apiHeader.setLine(orderLines);
//			apiHeader.setOrderProcessedOn(new Date());
//
//			if (shipmentOrder.getSoLine() != null && !shipmentOrder.getSoLine().isEmpty()) {
//				apiHeader.setProcessedStatusId(0L);
//				log.info("apiHeader : " + apiHeader);
//				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//				log.info("ShipmentOrder Order Success: " + createdOrder);
//				return apiHeader;
//			} else if (shipmentOrder.getSoLine() == null || shipmentOrder.getSoLine().isEmpty()) {
//				// throw the error as Lines are Empty and set the Indicator as '100'
//				apiHeader.setProcessedStatusId(100L);
//				log.info("apiHeader: " + apiHeader);
//				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
//				log.info("ShipmentOrder Order Failed: " + createdOrder);
//				throw new BadRequestException("ShipmentOrder Order doesn't contain any Lines.");
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return null;
//	}

	// POST
	private OutboundOrderV2 saveSalesOrderV2(@Valid SalesOrderV2 salesOrder) throws ParseException {
		try {
			SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();

			Optional<Warehouse> warehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							salesOrderHeader.getCompanyCode(), salesOrderHeader.getStoreID(),
							"EN", 0L);
			// Warehouse ID Validation
			validateWarehouseId(warehouse.get().getWarehouseId());

			// Checking for duplicate RefDocNumber
//			OutboundOrderV2 obOrder = orderService.getOBOrderByIdV2(salesOrderHeader.getSalesOrderNumber());
//			if (obOrder != null) {
//				throw new OutboundOrderRequestException("SalesOrderNumber is already posted and it can't be duplicated.");
//			}

			OutboundOrderV2 apiHeader = new OutboundOrderV2();
			apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
			apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
			apiHeader.setLanguageId(warehouse.get().getLanguageId());
			apiHeader.setWarehouseID(warehouse.get().getWarehouseId());

			apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
			apiHeader.setPartnerCode(salesOrderHeader.getStoreID());
			apiHeader.setPartnerName(salesOrderHeader.getStoreName());
			apiHeader.setPickListNumber(salesOrderHeader.getPickListNumber());
			apiHeader.setPickListStatus(salesOrderHeader.getStatus());
			apiHeader.setRefDocumentNo(salesOrderHeader.getPickListNumber());
			apiHeader.setOutboundOrderTypeID(3L);                                   // Hardcoded Value "3"
			apiHeader.setRefDocumentType("PICK LIST");                              // Hardcoded value "SaleOrder"
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

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					salesOrderHeader.getCompanyCode(), salesOrderHeader.getStoreID(), warehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (SalesOrderLineV2 soLine : salesOrderLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
				apiLine.setBrand(soLine.getBrand());
				apiLine.setOrigin(soLine.getOrigin());
				apiLine.setPackQty(soLine.getPackQty());
				apiLine.setExpectedQty(soLine.getExpectedQty());
				apiLine.setSupplierName(soLine.getSupplierName());
				apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
				apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());
				apiLine.setManufacturerCode(soLine.getManufacturerCode());
				apiLine.setManufacturerName(soLine.getManufacturerName());
				apiLine.setManufacturerFullName(soLine.getManufacturerFullName());
				apiLine.setStoreID(salesOrderHeader.getStoreID());
				apiLine.setRefField1ForOrderType(soLine.getOrderType());
				apiLine.setCustomerType("INVOICE");								//HardCoded
				apiLine.setOutboundOrderTypeID(3L);

				apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());                            // ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
				apiLine.setUom(soLine.getUom());                                // ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
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
			throw e;
		}
		return null;
	}


	/**
	 * @param returnPO
	 * @return
	 */
	private OutboundOrderV2 saveReturnPOV2(ReturnPOV2 returnPO) throws ParseException {
		try {
			ReturnPOHeaderV2 returnPOHeader = returnPO.getReturnPOHeader();

			Optional<Warehouse> warehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							returnPOHeader.getCompanyCode(), returnPOHeader.getStoreID(),
							"EN", 0L);

			// Warehouse ID Validation
			validateWarehouseId(warehouse.get().getWarehouseId());

			// Checking for duplicate RefDocNumber
//			OutboundOrderV2 obOrder = orderService.getOBOrderByIdV2(returnPOHeader.getPoNumber());
//
//			if (obOrder != null) {
//				throw new OutboundOrderRequestException("PO number is already posted and it can't be duplicated");
//			}

			List<ReturnPOLineV2> returnPOLines = returnPO.getReturnPOLine();

			// Mongo Primary Key
			OutboundOrderV2 apiHeader = new OutboundOrderV2();

			apiHeader.setCompanyCode(returnPOHeader.getCompanyCode());
			apiHeader.setBranchCode(returnPOHeader.getBranchCode());
			apiHeader.setLanguageId(returnPOHeader.getLanguageId());
			apiHeader.setWarehouseID(warehouse.get().getWarehouseId());

			apiHeader.setOrderId(returnPOHeader.getPoNumber());
			apiHeader.setPartnerCode(returnPOHeader.getStoreID());
			apiHeader.setPartnerName(returnPOHeader.getStoreName());
			apiHeader.setRefDocumentNo(returnPOHeader.getPoNumber());
			apiHeader.setOutboundOrderTypeID(2L);                            // Hardcoded Value "2"
			apiHeader.setRefDocumentType("PURCHASE RETURN");                        // Hardcoded value "RETURNPO"
			apiHeader.setOrderReceivedOn(new Date());

			apiHeader.setIsCompleted(returnPOHeader.getIsCompleted());
			apiHeader.setIsCancelled(returnPOHeader.getIsCancelled());
			apiHeader.setUpdatedOn(returnPOHeader.getUpdatedOn());
			apiHeader.setMiddlewareId(returnPOHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(returnPOHeader.getMiddlewareTable());

			try {
				Date date = DateUtils.convertStringToDate2(returnPOHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(date);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					returnPOHeader.getCompanyCode(),
					returnPOHeader.getStoreID(),
					warehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (ReturnPOLineV2 rpoLine : returnPOLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
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
				apiLine.setRefField1ForOrderType(rpoLine.getOrderType());        // ORDER_TYPE
				apiLine.setManufacturerCode(rpoLine.getManufacturerCode());
				apiLine.setManufacturerName(rpoLine.getManufacturerName());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				apiLine.setOutboundOrderTypeID(2L);

				apiLine.setSupplierInvoiceNo(rpoLine.getSupplierInvoiceNo());
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
	 * @param iWhTransferOutV2
	 * @return
	 */
	private OutboundOrderV2 saveIWHTransferV2(InterWarehouseTransferOutV2 iWhTransferOutV2) throws ParseException {
		try {
			InterWarehouseTransferOutHeaderV2 interWhTransferOutHeader =
					iWhTransferOutV2.getInterWarehouseTransferOutHeader();

			Optional<Warehouse> warehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							interWhTransferOutHeader.getFromCompanyCode(), interWhTransferOutHeader.getFromBranchCode(),
							"EN", 0L);

			// Warehouse ID Validation
			validateWarehouseId(warehouse.get().getWarehouseId());

			// Checking for duplicate RefDocNumber
//			OutboundOrderV2 obOrder = orderService.getOBOrderByIdV2(interWhTransferOutHeader.getTransferOrderNumber());
//
//			if (obOrder != null) {
//				throw new OutboundOrderRequestException("TransferOrderNumber is already posted and it can't be duplicated");
//			}

			List<InterWarehouseTransferOutLineV2> interWarehouseTransferOutLines =
					iWhTransferOutV2.getInterWarehouseTransferOutLine();

			// Mongo Primary Key
			OutboundOrderV2 apiHeader = new OutboundOrderV2();

			apiHeader.setBranchCode(interWhTransferOutHeader.getFromBranchCode());
			apiHeader.setCompanyCode(interWhTransferOutHeader.getFromCompanyCode());
			apiHeader.setLanguageId(warehouse.get().getLanguageId());
			apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
			apiHeader.setFromBranchCode(interWhTransferOutHeader.getFromBranchCode());

			apiHeader.setOrderId(interWhTransferOutHeader.getTransferOrderNumber());
//			apiHeader.setWarehouseID(interWhTransferOutHeader.getFromWhsID());
			apiHeader.setPartnerCode(interWhTransferOutHeader.getToBranchCode());
			apiHeader.setPartnerName(interWhTransferOutHeader.getStoreName());

			apiHeader.setSourceCompanyCode(interWhTransferOutHeader.getFromCompanyCode());
			apiHeader.setTargetCompanyCode(interWhTransferOutHeader.getToCompanyCode());
			apiHeader.setTargetBranchCode(interWhTransferOutHeader.getToBranchCode());

			apiHeader.setRefDocumentNo(interWhTransferOutHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(1L);                             // Hardcoded Value "1"
			apiHeader.setRefDocumentType("WMS to WMS");                            // Hardcoded value "WH to WH"
			apiHeader.setCustomerType("TRANSVERSE");								//HardCoded
			apiHeader.setOrderReceivedOn(new Date());

			apiHeader.setMiddlewareId(interWhTransferOutHeader.getMiddlewareId());
			apiHeader.setMiddlewareTable(interWhTransferOutHeader.getMiddlewareTable());

			try {
				Date reqDate = DateUtils.convertStringToDate2(interWhTransferOutHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDate);
			} catch (Exception e) {
				throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
			}

			IKeyValuePair iKeyValuePair = outboundOrderV2Repository.getV2Description(
					interWhTransferOutHeader.getFromCompanyCode(),
					interWhTransferOutHeader.getFromBranchCode(),
					warehouse.get().getWarehouseId());
			if (iKeyValuePair != null) {
				apiHeader.setCompanyName(iKeyValuePair.getCompanyDesc());
				apiHeader.setBranchName(iKeyValuePair.getPlantDesc());
				apiHeader.setWarehouseName(iKeyValuePair.getWarehouseDesc());
			}
//			apiHeader.setOutboundOrderHeaderId(System.currentTimeMillis());
			Set<OutboundOrderLineV2> orderLines = new HashSet<>();
			for (InterWarehouseTransferOutLineV2 iwhTransferLine : interWarehouseTransferOutLines) {
				OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();

				apiLine.setBrand(iwhTransferLine.getBrand());
				apiLine.setOrigin(iwhTransferLine.getOrigin());
				apiLine.setPackQty(iwhTransferLine.getPackQty());
				apiLine.setExpectedQty(iwhTransferLine.getExpectedQty());
				apiLine.setSupplierName(iwhTransferLine.getSupplierName());
				apiLine.setSourceBranchCode(iwhTransferLine.getSourceBranchCode());
				apiLine.setCountryOfOrigin(iwhTransferLine.getCountryOfOrigin());
				apiLine.setFromCompanyCode(iwhTransferLine.getFromCompanyCode());
				apiLine.setStoreID(iwhTransferLine.getStoreID());

				apiLine.setLineReference(iwhTransferLine.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(iwhTransferLine.getSku());                            // ITM_CODE
				apiLine.setItemText(iwhTransferLine.getSkuDescription());                // ITEM_TEXT
				apiLine.setOrderedQty(iwhTransferLine.getOrderedQty());                    // ORD_QTY
				apiLine.setUom(iwhTransferLine.getUom());                                // ORD_UOM
				apiLine.setRefField1ForOrderType(iwhTransferLine.getOrderType());        // ORDER_TYPE
				apiLine.setManufacturerCode(iwhTransferLine.getManufacturerCode());
				apiLine.setManufacturerName(iwhTransferLine.getManufacturerName());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setOutboundOrderHeaderId(apiHeader.getOutboundOrderHeaderId());
				apiLine.setCustomerType("TRANSVERSE");								//HardCoded
				apiLine.setOutboundOrderTypeID(1L);

				apiLine.setMiddlewareId(iwhTransferLine.getMiddlewareId());
				apiLine.setMiddlewareHeaderId(iwhTransferLine.getMiddlewareHeaderId());
				apiLine.setMiddlewareTable(iwhTransferLine.getMiddlewareTable());

				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());

			if (iWhTransferOutV2.getInterWarehouseTransferOutLine() != null &&
					!iWhTransferOutV2.getInterWarehouseTransferOutLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				log.info("apiHeader: " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferOut Order Success: " + createdOrder);
				return apiHeader;
			} else if (iWhTransferOutV2.getInterWarehouseTransferOutLine() == null ||
					iWhTransferOutV2.getInterWarehouseTransferOutLine().isEmpty()) {
				// throw the error as Lines are Empty and set the Indicator as '100'
				apiHeader.setProcessedStatusId(100L);
				log.info("apiHeader : " + apiHeader);
				OutboundOrderV2 createdOrder = orderService.createOutboundOrdersV2(apiHeader);
				log.info("InterWarehouseTransferOut Order Failed: " + createdOrder);
				throw new BadRequestException("InterWarehouseTransferOut Order doesn't contain any Lines.");
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
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

}
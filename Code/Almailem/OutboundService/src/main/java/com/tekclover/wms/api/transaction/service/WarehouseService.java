package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.controller.exception.OutboundOrderRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransfer;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.*;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.Shipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderV2Repository;
import com.tekclover.wms.api.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;

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


	/**
	 * 
	 * @return
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
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

}
package com.tekclover.wms.api.inbound.transaction.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.controller.exception.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.ASN;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.SOReturn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.StoreReturn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.transaction.repository.InboundOrderLinesV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASNHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASNLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrder;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferIn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferInHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InterWarehouseTransferInLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SOReturnHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SOReturnLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.SaleOrderReturn;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturnHeader;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturnLine;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransfer;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

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
	InboundOrderLinesV2Repository inboundOrderLinesV2Repository;


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
	public InboundOrder postWarehouseASN (com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASN asn) {
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
	public InboundOrder postStoreReturn(com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturn storeReturn) {
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

	/*----------------------------INBOUND-CONFIRMATION-POST---------------------------------------------*/
	// ASN 
	public AXApiResponse postASNConfirmation (ASN asn,
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
			StoreReturn storeReturn,
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
			SOReturn soReturn,
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
	 * @param asnv2
	 * @return
	 */
	public InboundOrderV2 postWarehouseASNV2 (ASNV2 asnv2) {
		log.info("ASNV2Header received from External: " + asnv2);
		InboundOrderV2 savedAsnV2Header = saveASNV2 (asnv2);
		log.info("savedAsnV2Header: " + savedAsnV2Header);
		return savedAsnV2Header;
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
	private InboundOrder saveASN (com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.ASN asn) {
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
	private InboundOrder saveStoreReturn (com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.StoreReturn storeReturn) {
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

	/**
	 * @param asnv2
	 * @return
	 */
	public InboundOrderV2 postWarehouseASNV5(ASNV2 asnv2) {
		log.info("ASNV2Header received from External: " + asnv2);
		InboundOrderV2 savedAsnV2Header = saveASNV5(asnv2);
		log.info("savedAsnV2Header: " + savedAsnV2Header);
		return savedAsnV2Header;
	}

	// POST ASNV2Header
	private InboundOrderV2 saveASNV5 (ASNV2 asnv2) {
		try {
			ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
			List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

			//validateBarcodeIds
			huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

			InboundOrderV2 apiHeader = new InboundOrderV2();
			BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));
			apiHeader.setOrderId(asnV2Header.getAsnNumber());
			apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
			apiHeader.setBranchCode(asnV2Header.getBranchCode());
			apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
			apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

			apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
			apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
			apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());

			if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
			} else {
				// Get Warehouse
				Optional<Warehouse> dbWarehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								asnV2Header.getCompanyCode(),
								asnV2Header.getBranchCode(),
								asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
								0L
						);
				log.info("dbWarehouse : " + dbWarehouse);
				apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			}

			if (asnV2Header.getInboundOrderTypeId() != null) {
				apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
			} else {
				apiHeader.setInboundOrderTypeId(1L);                                            //Default
			}
			apiHeader.setRefDocumentType("Supplier Invoice");
//			apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//					LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));


			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
			for (ASNLineV2 asnLineV2 : asnLineV2s) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
				apiLine.setLineReference(asnLineV2.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(asnLineV2.getSku().trim());							// ITM_CODE
				apiLine.setBarcodeId(asnLineV2.getBarcodeId().trim());
				apiLine.setItemText(asnLineV2.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setContainerNumber(asnLineV2.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(asnLineV2.getSupplierCode());				// PARTNER_CODE
				apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
				apiLine.setManufacturerName(MFR_NAME_V5);		// BRAND_NM
				apiLine.setManufacturerCode(MFR_NAME_V5);
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
				if (asnV2Header.getInboundOrderTypeId() != null) {
					apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
				} else {
					apiLine.setInboundOrderTypeId(1L);                                            //Default
				}

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
							throw new BadRequestException("Date format should be yyyy-MM-dd");
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
				apiLine.setNoPairs(asnLineV2.getNoPairs());
				orderLines.add(apiLine);
			}
			apiHeader.setLine(orderLines);
			apiHeader.setOrderProcessedOn(new Date());
			if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
				apiHeader.setProcessedStatusId(0L);
				apiHeader.setExecuted(0L);
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

	/**
	 *
	 * @param asnLines
	 */
	public void huSerialValidation (List<ASNLineV2> asnLines, String refDocNumber) {
		if(asnLines != null && !asnLines.isEmpty()) {
			List<String> barcodeIds = asnLines.stream().filter(n->n.getBarcodeId() != null).map(ASNLineV2::getBarcodeId).collect(Collectors.toList());
			log.info("BarcodeId: " + barcodeIds.size());
			barcodeIdValidation(barcodeIds, refDocNumber);
		}
	}

	/**
	 * @param asnv2
	 * @return
	 */
	public InboundOrderV2 postWarehouseASNV6(ASNV2 asnv2) {
		log.info("ASNV2Header received from External: " + asnv2);
		InboundOrderV2 savedAsnV2Header = saveASNV6(asnv2);
		log.info("savedAsnV2Header: " + savedAsnV2Header);
		return savedAsnV2Header;
	}

	/**
	 * NAMRATHA
	 * @param asnv2
	 * @return
	 */
	private InboundOrderV2 saveASNV6(ASNV2 asnv2) {
		try {
			ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
			List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();
			InboundOrderV2 apiHeader = new InboundOrderV2();
			BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));

			apiHeader.setOrderId(asnV2Header.getAsnNumber());
			apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
			apiHeader.setBranchCode(asnV2Header.getBranchCode());
			apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
			apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

			apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
			apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
			apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
			apiHeader.setRefDocumentType("SUPPLIER INVOICE");

			apiHeader.setLanguageId(asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID);
			if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
			} else {
				// Get Warehouse
				Optional<Warehouse> dbWarehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								asnV2Header.getCompanyCode(),
								asnV2Header.getBranchCode(),
								asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
								0L
						);
				log.info("dbWarehouse : " + dbWarehouse);
				apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			}

			if (asnV2Header.getInboundOrderTypeId() != null) {
				apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
			} else {
				apiHeader.setInboundOrderTypeId(1L);                                            //Default
			}
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                                                                 LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

			Set<InboundOrderLinesV2> orderLines = new HashSet<>();
			for (ASNLineV2 asnLineV2 : asnLineV2s) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));

				//validation for bagSize performing BagUom
//                checkUom(asnLineV2.getUom(), asnLineV2.getAlternateUom(), asnLineV2.getBagSize());

				apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
				apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
				apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
				apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
				if (asnLineV2.getSupplierCode() != null) {
					apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
				} else {
					apiLine.setSupplierCode(asnV2Header.getSupplierCode());
				}
				apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
				apiLine.setManufacturerName(MFR_NAME);
				apiLine.setManufacturerCode(MFR_NAME);
				apiLine.setManufacturerPartNo(MFR_NAME);
				apiLine.setOrigin(asnLineV2.getOrigin());
				apiLine.setCompanyCode(asnLineV2.getCompanyCode());
				apiLine.setBranchCode(asnLineV2.getBranchCode());
//                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
				apiLine.setSupplierName(asnLineV2.getSupplierName());
				apiLine.setBrand(asnLineV2.getBrand());
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
				apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
				apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

				if (asnV2Header.getInboundOrderTypeId() != null) {
					apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
				} else {
					apiLine.setInboundOrderTypeId(1L);                                            //Default
				}

//                if(asnLineV2.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), asnLineV2.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                    apiLine.setNoBags(asnLineV2.getExpectedQty());
////                        double orderQty = getQuantity(asnLineV2.getExpectedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                }
//                }

				if (asnLineV2.getExpectedQtyInCases() != null && asnLineV2.getExpectedQtyInPieces() != null) {
					Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
					apiLine.setExpectedQty(ordQty);     // 25
					apiLine.setOrderedQty(ordQty);      // 25
					apiLine.setBagSize(ordQty);         // 25
				} else {
					Double ordQty = asnLineV2.getExpectedQty() / asnLineV2.getNoBags();  // 50 / 2 => 25
					apiLine.setExpectedQty(ordQty);     // 25
					apiLine.setOrderedQty(ordQty);      // 25
					apiLine.setBagSize(ordQty);         // 25
				}

				apiLine.setNoBags(asnLineV2.getExpectedQtyInCases());
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
							if (asnLineV2.getExpectedDate().length() > 10) {
								reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
							}
							if (asnLineV2.getExpectedDate().length() == 10) {
								reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
							}
							apiLine.setExpectedDate(reqDelDate);
						} catch (Exception e) {
							e.printStackTrace();
							throw new BadRequestException("Date format should be yyyy-MM-dd");
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
							throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
						}
					}
				}

				apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
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
	
	// POST ASNV2Header
	private InboundOrderV2 saveASNV2 (ASNV2 asnv2) {
		try {
			ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
			List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();
			InboundOrderV2 apiHeader = new InboundOrderV2();

			apiHeader.setAMSSupplierInvoiceNo(asnV2Header.getAMSSupplierInvoiceNo());
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
				apiLine.setAMSSupplierInvoiceNo(asnLineV2.getAMSSupplierInvoiceNo());
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
			BeanUtils.copyProperties(soReturnHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(soReturnHeaderV2));
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
			Optional<Warehouse> dbWarehouse =
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
	 * @param soReturnV2
	 * @return
	 */
	public InboundOrderV2 postSOReturnV6(SaleOrderReturnV2 soReturnV2) {
		log.info("StoreReturnHeader received from External: " + soReturnV2);
		InboundOrderV2 savedSOReturn = saveSOReturnV6(soReturnV2);
		log.info("soReturnHeader: " + savedSOReturn);
		return savedSOReturn;
	}

	// SOReturnV2
	private InboundOrderV2 saveSOReturnV6(SaleOrderReturnV2 soReturnV2) {
		try {
			SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
			List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

			InboundOrderV2 apiHeader = new InboundOrderV2();
			BeanUtils.copyProperties(soReturnHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(soReturnHeaderV2));
			apiHeader.setTransferOrderNumber(soReturnHeaderV2.getAsnNumber());
			apiHeader.setOrderId(soReturnHeaderV2.getAsnNumber());
			apiHeader.setRefDocumentNo(soReturnHeaderV2.getTransferOrderNumber());
			apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
			apiHeader.setOrderId(soReturnHeaderV2.getTransferOrderNumber());
			apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
//			apiHeader.setRefDocumentType("SalesReturn");
			if(soReturnHeaderV2.getInboundOrderTypeId() != null) {
				apiHeader.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
			} else {
				apiHeader.setInboundOrderTypeId(2L);                                        // Hardcoded Value 2
			}
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
			apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
			apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
			apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
			apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());
			apiHeader.setRefDocumentType("SALES RETURN");

			if(soReturnHeaderV2.getWarehouseId() != null && !soReturnHeaderV2.getWarehouseId().isBlank()) {
				apiHeader.setWarehouseID(soReturnHeaderV2.getWarehouseId());
			} else {
				// Get Warehouse
				Optional<Warehouse> dbWarehouse =
						warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
								soReturnHeaderV2.getCompanyCode(),
								soReturnHeaderV2.getBranchCode(),
								LANG_ID,
								0L
						);
				log.info("dbWarehouse : " + dbWarehouse);
				apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
			}
			apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
					LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

			Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
			for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
				InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
				BeanUtils.copyProperties(soReturnLineV2, apiLine, CommonUtils.getNullPropertyNames(soReturnLineV2));

				//validation for bagSize performing BagUom
//                checkUom(soReturnLineV2.getUom(), soReturnLineV2.getAlternateUom(), soReturnLineV2.getBagSize());

				apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
				apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
				apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
				apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
				apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
				apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
				apiLine.setStoreID(soReturnLineV2.getStoreID());
				apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
				apiLine.setUom(soReturnLineV2.getUom());
				apiLine.setPackQty(soReturnLineV2.getPackQty());
				apiLine.setOrigin(soReturnLineV2.getOrigin());
				apiLine.setOrderId(apiHeader.getOrderId());
				apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
				apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
				apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
				if(soReturnHeaderV2.getInboundOrderTypeId() != null) {
					apiLine.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
				} else {
					apiLine.setInboundOrderTypeId(2L);
				}
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
					throw new BadRequestException("Date format should be yyyy-MM-dd");
				}

//                if(soReturnLineV2.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), soReturnLineV2.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                    apiLine.setNoBags(soReturnLineV2.getExpectedQty());
////                        double orderQty = getQuantity(soReturnLineV2.getExpectedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                }
//                }

				log.info("SOReturn ExpectedQtyInCases -----------> {}", soReturnLineV2.getExpectedQtyInCases());
				log.info("SOReturn ExpectedQtyInPieces ----------> {}", soReturnLineV2.getExpectedQtyInPieces());
				if (soReturnLineV2.getExpectedQtyInCases() != null && soReturnLineV2.getExpectedQtyInPieces() != null) {
					Double ordQty = soReturnLineV2.getExpectedQtyInPieces() / soReturnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
					apiLine.setExpectedQty(ordQty);     // 25
					apiLine.setOrderedQty(ordQty);      // 25
					apiLine.setBagSize(ordQty);         // 25
				} else {
					Double ordQty = soReturnLineV2.getExpectedQty() / soReturnLineV2.getNoBags();  // 50 / 2 => 25
					apiLine.setExpectedQty(ordQty);     // 25
					apiLine.setOrderedQty(ordQty);      // 25
					apiLine.setBagSize(ordQty);         // 25
				}

				apiLine.setNoBags(soReturnLineV2.getExpectedQtyInCases());
				apiLine.setManufacturerCode(MFR_NAME);
				apiLine.setManufacturerName(MFR_NAME);
				apiLine.setManufacturerPartNo(MFR_NAME);
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
			DataBaseContextHolder.setCurrentDb("KNOWELL");
			Optional<Warehouse> dbWarehouse =
					warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
							interWarehouseTransferInHeaderV2.getToCompanyCode(),
							interWarehouseTransferInHeaderV2.getToBranchCode(),
							"EN",
							0L
					);
			log.info("dbWarehouse : " + dbWarehouse);
			apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
//			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());

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
				apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerName());
				apiLine.setBrand(iwhTransferLineV2.getBrand());
				apiLine.setOrderId(apiHeader.getOrderId());
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
				apiLine.setInboundOrderTypeId(4L);

				apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
				apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
				apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
				apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());
				apiLine.setNoBags(iwhTransferLineV2.getNoBags());

				if (iwhTransferLineV2.getExpectedQtyInCases() > 0.0 && iwhTransferLineV2.getExpectedQtyInPieces() > 0.0) {
					Double ordQty = iwhTransferLineV2.getExpectedQtyInPieces() / iwhTransferLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
					apiLine.setExpectedQty(ordQty);     // 25
					apiLine.setOrderedQty(ordQty);      // 25
					apiLine.setBagSize(ordQty);         // 25
					apiLine.setNoBags(iwhTransferLineV2.getExpectedQtyInCases());
				} else {
					apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQtyInPieces());
					apiLine.setOrderedQty(iwhTransferLineV2.getExpectedQtyInPieces());
					apiLine.setBagSize(iwhTransferLineV2.getExpectedQtyInPieces());
				}

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
			Optional<Warehouse> dbWarehouse =
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
			BeanUtils.copyProperties(interWarehouseTransferInHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(interWarehouseTransferInHeaderV2));
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
			Optional<Warehouse> dbWarehouse =
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
	public AXApiResponse postASNConfirmationV2 (ASNV2 asn,
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
			StoreReturn storeReturn,
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
			SOReturn soReturn,
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
	/**
	 *
	 * @param barcodeIdList
	 * @param refDocNumber
	 */
	public void barcodeIdValidation(List<String> barcodeIdList, String refDocNumber) {
		List<String> result = inboundOrderLinesV2Repository.findAllByBarcodeIdIn(barcodeIdList);
		log.info("Duplicate HU Serial No: " + result);
		if (result.size() > 0) {
			List<String> duplicates = new ArrayList<>(result.size());
			for (String barcode : result) {
				boolean pass = inboundOrderLinesV2Repository.existsByBarcodeIdAndOrderId(barcode, refDocNumber);
				if (pass) {
					duplicates.add(barcode);
				}
			}
			if (duplicates.size() > 0) {
				throw new BadRequestException("HU-SerialNo/BarcodeId already exists for this order number..! " + result + "|" + refDocNumber);
			}
		}
	}


}
package com.tekclover.wms.api.transaction.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

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

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
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
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOut;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOutHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.InterWarehouseTransferOutLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPO;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPOHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ReturnPOLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SOHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SOLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrderHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.SalesOrderLine;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.ShipmentOrder;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.InterWarehouseShipment;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.Shipment;
import com.tekclover.wms.api.transaction.repository.MongoInboundRepository;
import com.tekclover.wms.api.transaction.repository.MongoOutboundRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService extends BaseService {
	
	@Autowired
	MongoInboundRepository mongoInboundRepository;
	
	@Autowired
	MongoOutboundRepository mongoOutboundRepository;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	OrderService ibOrderService;
	
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
	 * @param preInboundHeader
	 * @return
	 */
	public InboundIntegrationHeader postWarehouseASN (ASN asn) {
		log.info("ASNHeader received from External: " + asn);
		InboundIntegrationHeader savedAsnHeader = saveASNInMongo (asn);
		log.info("savedAsnHeader in Mongo: " + savedAsnHeader);
		return savedAsnHeader;
	} 

	/**
	 * 
	 * @param storeReturn
	 * @return
	 */
	public StoreReturn postStoreReturn(StoreReturn storeReturn) {
		log.info("StoreReturnHeader received from External: " + storeReturn);
		InboundIntegrationHeader savedStoreReturn = saveStoreReturnInMongo (storeReturn);
		log.info("savedStoreReturn in Mongo: " + savedStoreReturn);
		return storeReturn;
	}
	
	/**
	 * 
	 * @param soReturn
	 * @return
	 */
	public SaleOrderReturn postSOReturn(SaleOrderReturn soReturn) {
		log.info("StoreReturnHeader received from External: " + soReturn);
		InboundIntegrationHeader savedSOReturn = saveSOReturnInMongo (soReturn);
		log.info("soReturnHeader in Mongo: " + savedSOReturn);
		return soReturn;
	}

	/**
	 * 
	 * @param interWarehouseTransferIn
	 * @return
	 */
	public InterWarehouseTransferIn postInterWarehouseTransfer(InterWarehouseTransferIn interWarehouseTransferIn) {
		log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransferIn);
		InboundIntegrationHeader savedIWHReturn = saveInterWarehouseTransferInMongo (interWarehouseTransferIn);
		log.info("interWarehouseTransferHeader in Mongo: " + savedIWHReturn);
		return interWarehouseTransferIn;
	}

	/*-------------------------------------OUTBOUND---------------------------------------------------*/
	/**
	 * ShipmentOrder
	 * @param shipmenOrder
	 * @return
	 */
	public ShipmentOrder postSO( ShipmentOrder shipmenOrder, boolean isRerun) {
		log.info("ShipmenOrder received from External: " + shipmenOrder);
		OutboundIntegrationHeader savedSoHeader = saveSOInMongo (shipmenOrder, isRerun);
		log.info("savedSoHeader in Mongo: " + savedSoHeader.getRefDocumentNo());
		return shipmenOrder;
	}

	/**
	 * 
	 * @param salesOrder
	 * @return
	 */
	public SalesOrder postSalesOrder(SalesOrder salesOrder) {
		log.info("SalesOrderHeader received from External: " + salesOrder);
		OutboundIntegrationHeader savedSalesOrderHeader = saveSalesOrderInMongo (salesOrder);
		log.info("salesOrderHeader in Mongo: " + savedSalesOrderHeader);
		return salesOrder;
	}

	/**
	 * 
	 * @param returnPO
	 * @return
	 */
	public ReturnPO postReturnPO( ReturnPO returnPO) {
		log.info("ReturnPOHeader received from External: " + returnPO);
		OutboundIntegrationHeader savedReturnPOHeader = saveReturnPOInMongo (returnPO);
		log.info("savedReturnPOHeader in Mongo: " + savedReturnPOHeader);
		return returnPO;
	}

	/**
	 * 
	 * @param interWarehouseTransfer
	 * @return
	 */
	public InterWarehouseTransferOut postInterWarehouseTransferOutbound(
			InterWarehouseTransferOut interWarehouseTransfer) {
		log.info("InterWarehouseTransferHeader received from External: " + interWarehouseTransfer);
		OutboundIntegrationHeader savedInterWarehouseTransferHeader = saveIWHTransferInMongo (interWarehouseTransfer);
		log.info("savedInterWarehouseTransferHeader in Mongo: " + savedInterWarehouseTransferHeader);
		return interWarehouseTransfer;
	}
	
	/*-----------------------MONGO PROCESS---------------------------------------------------------------------*/
	// POST ASNHeader
	private InboundIntegrationHeader saveASNInMongo (ASN asn) {
		try {
			ASNHeader asnHeader = asn.getAsnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (asnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundIntegrationHeader dbApiHeader = mongoInboundRepository.findByRefDocumentNo(asnHeader.getAsnNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("ASN is already posted and it can't be duplicated.");
			}
			
			List<ASNLine> asnLines = asn.getAsnLine();
			
			// Mongo Primary Key
			InboundIntegrationHeader apiHeader = new InboundIntegrationHeader();
			apiHeader.setId(asnHeader.getAsnNumber() + ":" + getUUID());
			apiHeader.setRefDocumentNo(asnHeader.getAsnNumber());
			apiHeader.setRefDocumentType("ASN");
			apiHeader.setWarehouseID(asnHeader.getWareHouseId());
			apiHeader.setInboundOrderTypeId(1L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			List<InboundIntegrationLine> apiLines = new ArrayList<>();
			for (ASNLine asnLine : asnLines) {
				InboundIntegrationLine apiLine = new InboundIntegrationLine();
				apiLine.setLineReference(asnLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(asnLine.getSku());							// ITM_CODE
				apiLine.setItemText(asnLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setInvoiceNumber(asnLine.getInvoiceNumber());			// INV_NO
				apiLine.setContainerNumber(asnLine.getContainerNumber());		// CONT_NO
				apiLine.setSupplierCode(asnLine.getSupplierCode());				// PARTNER_CODE
				apiLine.setSupplierPartNumber(asnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
				apiLine.setManufacturerName(asnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(asnLine.getManufacturerPartNo());	// MFR_PART
				
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
				apiLines.add(apiLine);
				
				//------Lines
				InboundOrderLines lines = new InboundOrderLines();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setInboundIntegrationLine(apiLines);
			InboundIntegrationHeader createdInboundIntegration = mongoInboundRepository.save(apiHeader);
			log.info("createdInboundIntegration : " + createdInboundIntegration);
			
			// Store in SQL DB
			InboundOrder newInboundOrder = new InboundOrder();
			BeanUtils.copyProperties(apiHeader, newInboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newInboundOrder.setOrderId(asnHeader.getAsnNumber());
			newInboundOrder.setOrderProcessedOn(new Date());
			newInboundOrder.setLines(orderLines);
			InboundOrder createdOrder = ibOrderService.createInboundOrders(newInboundOrder);
			log.info("ASN createdOrder in SQL: " + createdOrder);
			return createdInboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param storeReturnHeader
	 * @return
	 */
	private InboundIntegrationHeader saveStoreReturnInMongo(StoreReturn storeReturn) {
		try {
			StoreReturnHeader storeReturnHeader = storeReturn.getStoreReturnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (storeReturnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundIntegrationHeader dbApiHeader = mongoInboundRepository.findByRefDocumentNo(storeReturnHeader.getTransferOrderNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("TransferOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<StoreReturnLine> storeReturnLines = storeReturn.getStoreReturnLine();
			
			// Mongo Primary Key
			InboundIntegrationHeader apiHeader = new InboundIntegrationHeader();
			apiHeader.setId(storeReturnHeader.getTransferOrderNumber() + ":" + getUUID());
			apiHeader.setRefDocumentNo(storeReturnHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(storeReturnHeader.getWareHouseId());
			apiHeader.setRefDocumentType("RETURN");			
			apiHeader.setInboundOrderTypeId(2L);
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			List<InboundIntegrationLine> apiLines = new ArrayList<>();
			for (StoreReturnLine storeReturnLine : storeReturnLines) {
				InboundIntegrationLine apiLine = new InboundIntegrationLine();
				apiLine.setLineReference(storeReturnLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(storeReturnLine.getSku());							// ITM_CODE
				apiLine.setItemText(storeReturnLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setInvoiceNumber(storeReturnLine.getInvoiceNumber());			// INV_NO
				apiLine.setContainerNumber(storeReturnLine.getContainerNumber());		// CONT_NO
				apiLine.setSupplierCode(storeReturnLine.getStoreID());					// PARTNER_CODE
				apiLine.setSupplierPartNumber(storeReturnLine.getSupplierPartNumber()); // PARTNER_ITM_CODE
				apiLine.setManufacturerName(storeReturnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(storeReturnLine.getManufacturerPartNo());	// MFR_PART
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(storeReturnLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(storeReturnLine.getExpectedQty());				// ORD_QTY
				apiLine.setUom(storeReturnLine.getUom());								// ORD_UOM
				apiLine.setItemCaseQty(storeReturnLine.getPackQty());					// ITM_CASE_QTY
				apiLines.add(apiLine);
				
				//------Lines
				InboundOrderLines lines = new InboundOrderLines();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setInboundIntegrationLine(apiLines);
			InboundIntegrationHeader createdInboundIntegration = mongoInboundRepository.save(apiHeader);
			log.info("createdInboundIntegration : " + createdInboundIntegration);
			
			// Store in SQL DB
			InboundOrder newInboundOrder = new InboundOrder();
			BeanUtils.copyProperties(apiHeader, newInboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newInboundOrder.setOrderId(storeReturnHeader.getTransferOrderNumber());
			newInboundOrder.setOrderProcessedOn(new Date());
			newInboundOrder.setLines(orderLines);
			InboundOrder createdOrder = ibOrderService.createInboundOrders(newInboundOrder);
			log.info("StoreReturn - createdOrder in SQL: " + createdOrder);
						
			return createdInboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param soReturn
	 * @return
	 */
	private InboundIntegrationHeader saveSOReturnInMongo(SaleOrderReturn soReturn) {
		try {
			SOReturnHeader soReturnHeader = soReturn.getSoReturnHeader();
			
			// Warehouse ID Validation
			validateWarehouseId (soReturnHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			InboundIntegrationHeader dbApiHeader = mongoInboundRepository.findByRefDocumentNo(soReturnHeader.getReturnOrderReference());
			if (dbApiHeader != null) {
				throw new BadRequestException("Return Order Reference is already posted and it can't be duplicated.");
			}
						
			List<SOReturnLine> storeReturnLines = soReturn.getSoReturnLine();
			
			// Mongo Primary Key
			InboundIntegrationHeader apiHeader = new InboundIntegrationHeader();
			apiHeader.setId(soReturnHeader.getReturnOrderReference() + ":" + getUUID());
			apiHeader.setRefDocumentNo(soReturnHeader.getReturnOrderReference());
			apiHeader.setWarehouseID(soReturnHeader.getWareHouseId());
			apiHeader.setRefDocumentType("RETURN");	
			apiHeader.setInboundOrderTypeId(4L);										// Hardcoded Value 4
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			List<InboundIntegrationLine> apiLines = new ArrayList<>();
			for (SOReturnLine soReturnLine : storeReturnLines) {
				InboundIntegrationLine apiLine = new InboundIntegrationLine();
				apiLine.setLineReference(soReturnLine.getLineReference()); 				// IB_LINE_NO
				apiLine.setItemCode(soReturnLine.getSku());								// ITM_CODE
				apiLine.setItemText(soReturnLine.getSkuDescription()); 					// ITEM_TEXT
				apiLine.setInvoiceNumber(soReturnLine.getInvoiceNumber());				// INV_NO
				apiLine.setContainerNumber(soReturnLine.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(soReturnLine.getStoreID());						// PARTNER_CODE
				apiLine.setSupplierPartNumber(soReturnLine.getSupplierPartNumber());	// PARTNER_ITM_CODE
				apiLine.setManufacturerName(soReturnLine.getManufacturerName());		// BRND_NM
				apiLine.setManufacturerPartNo(soReturnLine.getManufacturerPartNo());	// MFR_PART
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(soReturnLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(soReturnLine.getExpectedQty());					// ORD_QTY
				apiLine.setUom(soReturnLine.getUom());									// ORD_UOM
				apiLine.setItemCaseQty(soReturnLine.getPackQty());						// ITM_CASE_QTY
				apiLine.setSalesOrderReference(soReturnLine.getSalesOrderReference());	// REF_FIELD_4
				apiLines.add(apiLine);
				
				//------Lines
				InboundOrderLines lines = new InboundOrderLines();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setInboundIntegrationLine(apiLines);
			InboundIntegrationHeader createdInboundIntegration = mongoInboundRepository.save(apiHeader);
			log.info("createdInboundIntegration : " + createdInboundIntegration);
			
			// Store in SQL DB
			InboundOrder newInboundOrder = new InboundOrder();
			BeanUtils.copyProperties(apiHeader, newInboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newInboundOrder.setOrderId(soReturnHeader.getReturnOrderReference());
			newInboundOrder.setOrderProcessedOn(new Date());
			newInboundOrder.setLines(orderLines);
			InboundOrder createdOrder = ibOrderService.createInboundOrders(newInboundOrder);
			log.info("SOReturn - createdOrder in SQL: " + createdOrder);
						
			return createdInboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param interWarehouseTransferIn
	 * @return
	 */
	private InboundIntegrationHeader saveInterWarehouseTransferInMongo(
			InterWarehouseTransferIn interWarehouseTransferIn) {
		try {
			InterWarehouseTransferInHeader interWarehouseTransferInHeader = interWarehouseTransferIn.getInterWarehouseTransferInHeader();
			// Warehouse ID Validation
			validateWarehouseId (interWarehouseTransferInHeader.getToWhsId());
			
			// Checking for duplicate RefDocNumber
			InboundIntegrationHeader dbApiHeader = 
					mongoInboundRepository.findByRefDocumentNo(interWarehouseTransferInHeader.getTransferOrderNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("TransferOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<InterWarehouseTransferInLine> interWarehouseTransferInLines = interWarehouseTransferIn.getInterWarehouseTransferInLine();
			
			// Mongo Primary Key
			InboundIntegrationHeader apiHeader = new InboundIntegrationHeader();
			apiHeader.setId(interWarehouseTransferInHeader.getTransferOrderNumber() + ":" + getUUID());
			apiHeader.setRefDocumentNo(interWarehouseTransferInHeader.getTransferOrderNumber());
			apiHeader.setWarehouseID(interWarehouseTransferInHeader.getToWhsId());
			apiHeader.setRefDocumentType("WH2WH");				// Hardcoded Value "WH to WH"
			apiHeader.setInboundOrderTypeId(3L);				// Hardcoded Value 3
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			Set<InboundOrderLines> orderLines = new HashSet<>();
			List<InboundIntegrationLine> apiLines = new ArrayList<>();
			for (InterWarehouseTransferInLine iwhTransferLine : interWarehouseTransferInLines) {
				InboundIntegrationLine apiLine = new InboundIntegrationLine();
				apiLine.setLineReference(iwhTransferLine.getLineReference()); 				// IB_LINE_NO
				apiLine.setItemCode(iwhTransferLine.getSku());								// ITM_CODE
				apiLine.setItemText(iwhTransferLine.getSkuDescription()); 					// ITEM_TEXT
				apiLine.setInvoiceNumber(iwhTransferLine.getInvoiceNumber());				// INV_NO
				apiLine.setContainerNumber(iwhTransferLine.getContainerNumber());			// CONT_NO
				apiLine.setSupplierCode(iwhTransferLine.getFromWhsId());					// PARTNER_CODE
				apiLine.setSupplierPartNumber(iwhTransferLine.getSupplierPartNumber());		// PARTNER_ITM_CODE
				apiLine.setManufacturerName(iwhTransferLine.getManufacturerName());			// BRND_NM
				apiLine.setManufacturerPartNo(iwhTransferLine.getManufacturerPartNo());		// MFR_PART
				
				// EA_DATE
				try {
					Date reqDelDate = DateUtils.convertStringToDate(iwhTransferLine.getExpectedDate());
					apiLine.setExpectedDate(reqDelDate);
				} catch (Exception e) {
					throw new BadRequestException("Date format should be MM-dd-yyyy");
				}
				
				apiLine.setOrderedQty(iwhTransferLine.getExpectedQty());					// ORD_QTY
				apiLine.setUom(iwhTransferLine.getUom());									// ORD_UOM
				apiLine.setItemCaseQty(iwhTransferLine.getPackQty());						// ITM_CASE_QTY
				apiLines.add(apiLine);
				
				//------Lines
				InboundOrderLines lines = new InboundOrderLines();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setInboundIntegrationLine(apiLines);
			InboundIntegrationHeader createdInboundIntegration = mongoInboundRepository.save(apiHeader);
			log.info("createdInboundIntegration : " + createdInboundIntegration);
			
			// Store in SQL DB
			InboundOrder newInboundOrder = new InboundOrder();
			BeanUtils.copyProperties(apiHeader, newInboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newInboundOrder.setOrderId(interWarehouseTransferInHeader.getTransferOrderNumber());
			newInboundOrder.setOrderProcessedOn(new Date());
			newInboundOrder.setLines(orderLines);
			InboundOrder createdOrder = ibOrderService.createInboundOrders(newInboundOrder);
			log.info("InterWarehouseTransfer - createdOrder in SQL: " + createdOrder);
			return createdInboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*-----------------------------------OUTBOUND----------------------------------------------------------------------*/
	
	// POST SOHeader
	private OutboundIntegrationHeader saveSOInMongo (ShipmentOrder shipmenOrder, boolean isRerun) {
		try {
			SOHeader soHeader = shipmenOrder.getSoHeader();
			// Warehouse ID Validation
			validateWarehouseId (soHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			OutboundIntegrationHeader dbApiHeader = 
					mongoOutboundRepository.findByRefDocumentNo(soHeader.getTransferOrderNumber());
			
			if (dbApiHeader != null) {
				throw new BadRequestException("TransferOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<SOLine> soLines = shipmenOrder.getSoLine();
			
			// Mongo Primary Key
			OutboundIntegrationHeader apiHeader = new OutboundIntegrationHeader();
			apiHeader.setId(soHeader.getTransferOrderNumber() + ":" + getUUID());
			apiHeader.setWarehouseID(soHeader.getWareHouseId());
			apiHeader.setPartnerCode(soHeader.getStoreID());
			apiHeader.setPartnerName(soHeader.getStoreName());
			apiHeader.setRefDocumentNo(soHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(0L);
			apiHeader.setRefDocumentType("SO");						// Hardcoded value "SO"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(soHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new BadRequestException("Date format should be MM-dd-yyyy");
			}
			
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			List<OutboundIntegrationLine> apiLines = new ArrayList<>();
			for (SOLine soLine : soLines) {
				OutboundIntegrationLine apiLine = new OutboundIntegrationLine();
				apiLine.setLineReference(soLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());							// ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());					// ORD_QTY
				apiLine.setUom(soLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());		// ORDER_TYPE
				apiLines.add(apiLine);
				
				//------Lines
				OutboundOrderLine lines = new OutboundOrderLine();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setOutboundIntegrationLine(apiLines);
			
			OutboundIntegrationHeader createdOutboundIntegration = mongoOutboundRepository.save(apiHeader);
			log.info("createdOutboundIntegration : " + createdOutboundIntegration);
			
			if (!isRerun) {
				// Store in SQL DB
				OutboundOrder newOutboundOrder = new OutboundOrder();
				BeanUtils.copyProperties(apiHeader, newOutboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
				newOutboundOrder.setOrderId(soHeader.getTransferOrderNumber());
				newOutboundOrder.setOrderProcessedOn(new Date());
				newOutboundOrder.setLines(orderLines);
				OutboundOrder createdOrder = ibOrderService.createOutboundOrders(newOutboundOrder);
				log.info("ShipmentOrder - createdOrder in SQL: " + createdOrder);
			}
						
			return createdOutboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param salesOrderHeader
	 * @return
	 */
	private OutboundIntegrationHeader saveSalesOrderInMongo(@Valid SalesOrder salesOrder) {
		try {
			SalesOrderHeader salesOrderHeader = salesOrder.getSalesOrderHeader();
			// Warehouse ID Validation
			validateWarehouseId (salesOrderHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			OutboundIntegrationHeader dbApiHeader = 
					mongoOutboundRepository.findByRefDocumentNo(salesOrderHeader.getSalesOrderNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("SalesOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<SalesOrderLine> salesOrderLines = salesOrder.getSalesOrderLine();
			
			// Mongo Primary Key
			OutboundIntegrationHeader apiHeader = new OutboundIntegrationHeader();
			apiHeader.setId(salesOrderHeader.getSalesOrderNumber() + ":" + getUUID());
			apiHeader.setWarehouseID(salesOrderHeader.getWareHouseId());
			apiHeader.setPartnerCode(salesOrderHeader.getStoreID());
			apiHeader.setPartnerName(salesOrderHeader.getStoreName());
			apiHeader.setRefDocumentNo(salesOrderHeader.getSalesOrderNumber());
			apiHeader.setOutboundOrderTypeID(3L);							// Hardcoded Value "3"
			apiHeader.setRefDocumentType("SaleOrder");						// Hardcoded value "SaleOrder"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(salesOrderHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new BadRequestException("Date format should be MM-dd-yyyy");
			}
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			List<OutboundIntegrationLine> apiLines = new ArrayList<>();
			for (SalesOrderLine soLine : salesOrderLines) {
				OutboundIntegrationLine apiLine = new OutboundIntegrationLine();
				apiLine.setLineReference(soLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(soLine.getSku());							// ITM_CODE
				apiLine.setItemText(soLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(soLine.getOrderedQty());					// ORD_QTY
				apiLine.setUom(soLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(soLine.getOrderType());		// ORDER_TYPE
				apiLines.add(apiLine);
				
				//------Lines
				OutboundOrderLine lines = new OutboundOrderLine();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setOutboundIntegrationLine(apiLines);
			OutboundIntegrationHeader createdOutboundIntegration = mongoOutboundRepository.save(apiHeader);
			log.info("createdOutboundIntegration : " + createdOutboundIntegration);
			
			// Store in SQL DB
			OutboundOrder newOutboundOrder = new OutboundOrder();
			BeanUtils.copyProperties(apiHeader, newOutboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newOutboundOrder.setOrderId(salesOrderHeader.getSalesOrderNumber());
			newOutboundOrder.setOrderProcessedOn(new Date());
			newOutboundOrder.setLines(orderLines);
			OutboundOrder createdOrder = ibOrderService.createOutboundOrders(newOutboundOrder);
			log.info("ShipmentOrder - createdOrder in SQL: " + createdOrder);
						
			return createdOutboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param returnPO
	 * @return
	 */
	private OutboundIntegrationHeader saveReturnPOInMongo(@Valid ReturnPO returnPO) {
		try {
			ReturnPOHeader returnPOHeader = returnPO.getReturnPOHeader();
			// Warehouse ID Validation
			validateWarehouseId (returnPOHeader.getWareHouseId());
			
			// Checking for duplicate RefDocNumber
			OutboundIntegrationHeader dbApiHeader = 
					mongoOutboundRepository.findByRefDocumentNo(returnPOHeader.getPoNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("PONumber is already posted and it can't be duplicated.");
			}
						
			List<ReturnPOLine> returnPOLines = returnPO.getReturnPOLine();
			
			// Mongo Primary Key
			OutboundIntegrationHeader apiHeader = new OutboundIntegrationHeader();
			apiHeader.setId(returnPOHeader.getPoNumber() + ":" + getUUID());
			apiHeader.setWarehouseID(returnPOHeader.getWareHouseId());
			apiHeader.setPartnerCode(returnPOHeader.getStoreID());
			apiHeader.setPartnerName(returnPOHeader.getStoreName());
			apiHeader.setRefDocumentNo(returnPOHeader.getPoNumber());
			apiHeader.setOutboundOrderTypeID(2L);							// Hardcoded Value "2"
			apiHeader.setRefDocumentType("RETURNPO");						// Hardcoded value "RETURNPO"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(returnPOHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new BadRequestException("Date format should be MM-dd-yyyy");
			}
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			List<OutboundIntegrationLine> apiLines = new ArrayList<>();
			for (ReturnPOLine rpoLine : returnPOLines) {
				OutboundIntegrationLine apiLine = new OutboundIntegrationLine();
				apiLine.setLineReference(rpoLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(rpoLine.getSku());							// ITM_CODE
				apiLine.setItemText(rpoLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(rpoLine.getReturnQty());					// ORD_QTY
				apiLine.setUom(rpoLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(rpoLine.getOrderType());		// ORDER_TYPE
				apiLines.add(apiLine);
				
				//------Lines
				OutboundOrderLine lines = new OutboundOrderLine();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setOutboundIntegrationLine(apiLines);
			OutboundIntegrationHeader createdOutboundIntegration = mongoOutboundRepository.save(apiHeader);
			log.info("createdOutboundIntegration : " + createdOutboundIntegration);
			
			// Store in SQL DB
			OutboundOrder newOutboundOrder = new OutboundOrder();
			BeanUtils.copyProperties(apiHeader, newOutboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newOutboundOrder.setOrderId(returnPOHeader.getPoNumber());
			newOutboundOrder.setOrderProcessedOn(new Date());
			newOutboundOrder.setLines(orderLines);
			OutboundOrder createdOrder = ibOrderService.createOutboundOrders(newOutboundOrder);
			log.info("ShipmentOrder - createdOrder in SQL: " + createdOrder);
						
			return createdOutboundIntegration;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param interWarehouseTransfer
	 * @return
	 */
	private OutboundIntegrationHeader saveIWHTransferInMongo(InterWarehouseTransferOut interWarehouseTransfer) {
		try {
			InterWarehouseTransferOutHeader interWarehouseTransferOutHeader = 
					interWarehouseTransfer.getInterWarehouseTransferOutHeader();
			// Warehouse ID Validation
			validateWarehouseId (interWarehouseTransferOutHeader.getFromWhsID());
			
			// Checking for duplicate RefDocNumber
			OutboundIntegrationHeader dbApiHeader = 
					mongoOutboundRepository.findByRefDocumentNo(interWarehouseTransferOutHeader.getTransferOrderNumber());
			if (dbApiHeader != null) {
				throw new BadRequestException("TransferOrderNumber is already posted and it can't be duplicated.");
			}
						
			List<InterWarehouseTransferOutLine> interWarehouseTransferOutLines = 
					interWarehouseTransfer.getInterWarehouseTransferOutLine();
			
			// Mongo Primary Key
			OutboundIntegrationHeader apiHeader = new OutboundIntegrationHeader();
			apiHeader.setId(interWarehouseTransferOutHeader.getTransferOrderNumber() + ":" + getUUID());
			apiHeader.setWarehouseID(interWarehouseTransferOutHeader.getFromWhsID());
			apiHeader.setPartnerCode(interWarehouseTransferOutHeader.getToWhsID());
			apiHeader.setPartnerName(interWarehouseTransferOutHeader.getStoreName());
			apiHeader.setRefDocumentNo(interWarehouseTransferOutHeader.getTransferOrderNumber());
			apiHeader.setOutboundOrderTypeID(1L);							// Hardcoded Value "1"
			apiHeader.setRefDocumentType("WH2WH");							// Hardcoded value "WH to WH"
			apiHeader.setOrderProcessedOn(new Date());
			apiHeader.setOrderReceivedOn(new Date());
			apiHeader.setProcessedStatusId(0L);
			
			try {
				Date reqDelDate = DateUtils.convertStringToDate(interWarehouseTransferOutHeader.getRequiredDeliveryDate());
				apiHeader.setRequiredDeliveryDate(reqDelDate);
			} catch (Exception e) {
				throw new BadRequestException("Date format should be MM-dd-yyyy");
			}
			Set<OutboundOrderLine> orderLines = new HashSet<>();
			List<OutboundIntegrationLine> apiLines = new ArrayList<>();
			for (InterWarehouseTransferOutLine iwhTransferLine : interWarehouseTransferOutLines) {
				OutboundIntegrationLine apiLine = new OutboundIntegrationLine();
				apiLine.setLineReference(iwhTransferLine.getLineReference()); 			// IB_LINE_NO
				apiLine.setItemCode(iwhTransferLine.getSku());						// ITM_CODE
				apiLine.setItemText(iwhTransferLine.getSkuDescription()); 				// ITEM_TEXT
				apiLine.setOrderedQty(iwhTransferLine.getOrderedQty());					// ORD_QTY
				apiLine.setUom(iwhTransferLine.getUom()); 								// ORD_UOM
				apiLine.setRefField1ForOrderType(iwhTransferLine.getOrderType());		// ORDER_TYPE
				apiLines.add(apiLine);
				
				//------Lines
				OutboundOrderLine lines = new OutboundOrderLine();
				BeanUtils.copyProperties(apiLine, lines, CommonUtils.getNullPropertyNames(apiLine));
				orderLines.add(lines);
			}
			apiHeader.setOutboundIntegrationLine(apiLines);
			OutboundIntegrationHeader createdOutboundIntegration = mongoOutboundRepository.save(apiHeader);
			log.info("createdOutboundIntegration : " + createdOutboundIntegration);
			
			// Store in SQL DB
			OutboundOrder newOutboundOrder = new OutboundOrder();
			BeanUtils.copyProperties(apiHeader, newOutboundOrder, CommonUtils.getNullPropertyNames(apiHeader));
			newOutboundOrder.setOrderId(interWarehouseTransferOutHeader.getTransferOrderNumber());
			newOutboundOrder.setOrderProcessedOn(new Date());
			newOutboundOrder.setLines(orderLines);
			OutboundOrder createdOrder = ibOrderService.createOutboundOrders(newOutboundOrder);
			log.info("ShipmentOrder - createdOrder in SQL: " + createdOrder);
			return createdOutboundIntegration;
		} catch (Exception e) {
			throw e;
		}
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
	 * InterWarehouseTransfer API
	 * @param iwhTransferHeader
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
	 * @param shipmentHeader
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
	 * @param iwhShipmentHeader
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
	 * @param salesOrderHeader
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
	 * @param returnPOHeader
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
		if (wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_110) || wareHouseId.equalsIgnoreCase(WAREHOUSE_ID_111)) {
			log.info("wareHouseId:------------> " + wareHouseId);
			return true;
		} else {
			throw new BadRequestException("Warehouse Id must be either 110 or 111");
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
	
//	public static void main(String[] args) {
//		System.out.println(getUUID());
//	}
}

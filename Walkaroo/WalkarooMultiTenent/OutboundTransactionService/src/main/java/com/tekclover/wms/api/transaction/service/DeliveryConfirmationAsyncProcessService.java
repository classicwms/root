package com.tekclover.wms.api.transaction.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundHeader;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationLineV3;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationSAP;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationV3;
import com.tekclover.wms.api.transaction.repository.DeliveryConfirmationRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeliveryConfirmationAsyncProcessService extends BaseService {

	 @Autowired
	 PreOutboundHeaderService preOutboundHeaderService;
	 
	 @Autowired
	 DeliveryConfirmationRepository deliveryConfirmationRepository;

	 @Async ("asyncExecutor")
	 public void postDeliveryConfirmationV4(List<DeliveryConfirmationSAP> deliveryConfirmationSAPList) throws Exception {
		 try {
			 log.info("DeliveryConfirmation ----SAP Input's" + deliveryConfirmationSAPList);
			 String db = getDataBase(deliveryConfirmationSAPList.get(0).getBranchCode());
			 DataBaseContextHolder.clear();
			 DataBaseContextHolder.setCurrentDb(db);
			 DeliveryConfirmationV3 deliveryConfirmationV3 = prepDeliveryConfirmationV3(deliveryConfirmationSAPList);
			 log.info("DeliveryConfirmationV4 received from SAP External: " + deliveryConfirmationV3);

			 saveDeliveryConfirmationV4(deliveryConfirmationV3);
			 log.info("DeliveryConfirmationV4 SAP : " + deliveryConfirmationV3);
		 } finally {
			 DataBaseContextHolder.clear();
		 }
	 }
	
	/**
     * 
     * @param deliveryConfirmationSAPList
     * @return
     */
    public DeliveryConfirmationV3 prepDeliveryConfirmationV3(List<DeliveryConfirmationSAP> deliveryConfirmationSAPList) {
        log.info("deliveryConfirmationSAPList -----> {}", deliveryConfirmationSAPList);
		DeliveryConfirmationV3 deliveryConfirmationV3 = new DeliveryConfirmationV3();
		List<DeliveryConfirmationLineV3> deliveryLines = new ArrayList<>();
		String languageId = null;
		String companyCode = null;
		String plantId = null;
		String warehouseId = null;
		
		for (DeliveryConfirmationSAP sapData : deliveryConfirmationSAPList) {
			SearchPreOutboundHeader searchPreOutboundHeader = new SearchPreOutboundHeader();
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.add(sapData.getSalesOrderNumber());
			searchPreOutboundHeader.setSoNumber(searchList);
			
			try {
				List<PreOutboundHeader> preOutboundHeaderArr = 
						preOutboundHeaderService.findPreOutboundHeader(searchPreOutboundHeader);
				if (preOutboundHeaderArr != null && preOutboundHeaderArr.size() > 0) {
					companyCode = preOutboundHeaderArr.get(0).getCompanyCodeId();
					warehouseId = preOutboundHeaderArr.get(0).getWarehouseId();
					languageId = preOutboundHeaderArr.get(0).getLanguageId();
					log.info("companyCode -> {}, warehouseId -> {}, languageId ->{} ", companyCode, warehouseId, languageId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			plantId = sapData.getBranchCode();
			
			DeliveryConfirmationLineV3 line = new DeliveryConfirmationLineV3();
			line.setOutbound(sapData.getSalesOrderNumber());
			line.setHuSerialNo(sapData.getBarcodeId());
			line.setMaterial(sapData.getMaterialNo());
			line.setPriceSegement(sapData.getPriceSegement());
			line.setPlant(sapData.getBranchCode());
			line.setSkuCode(sapData.getSku());
			line.setPickedQty(1D);
			deliveryLines.add(line);
		}
		deliveryConfirmationV3.setCompanyCodeId(companyCode);
		deliveryConfirmationV3.setPlantId(plantId);
		deliveryConfirmationV3.setLanguageId(languageId);
		deliveryConfirmationV3.setWarehouseId(warehouseId);
		deliveryConfirmationV3.setLines(deliveryLines);
		return deliveryConfirmationV3;
	}
    
    /**
	 * Modified for SAP orders - 30/06/2025
	 * Aakash Vinayak
	 *
	 * @param deliveryConfirmationV3
	 * @throws Exception
	 */
	private void saveDeliveryConfirmationV4(@Valid DeliveryConfirmationV3 deliveryConfirmationV3) throws Exception {
		try {
			if(deliveryConfirmationV3 != null && deliveryConfirmationV3.getLines() != null && !deliveryConfirmationV3.getLines().isEmpty()) {
				log.info("Delivery template post SAP validation Initiated..!");
				List<String> orderList = deliveryConfirmationV3.getLines().stream().filter(n -> n.getOutbound() != null).map(DeliveryConfirmationLineV3::getOutbound).distinct().collect(Collectors.toList());
				List<String> validateDeliveryOrderNumber = deliveryConfirmationRepository.validateDeliveryConfirmation(orderList);
				List<String> validateOrderNumber = deliveryConfirmationRepository.validateDeliveryOrders(orderList);
				log.info("Validate OrderNumber From SAP : " + orderList.size() + "|" + validateOrderNumber.size()+ "|" + validateDeliveryOrderNumber.size());

				List<DeliveryConfirmation> deliveryConfirmations = deliveryConfirmationV3.getLines().stream().map(deliveryLine -> {
					DeliveryConfirmation newDeliveryConfirmation = new DeliveryConfirmation();
					BeanUtils.copyProperties(deliveryLine, newDeliveryConfirmation, CommonUtils.getNullPropertyNames(deliveryLine));
					newDeliveryConfirmation.setCompanyCodeId(deliveryConfirmationV3.getCompanyCodeId());
					newDeliveryConfirmation.setPlantId(deliveryConfirmationV3.getPlantId());
					newDeliveryConfirmation.setLanguageId(deliveryConfirmationV3.getLanguageId());
					newDeliveryConfirmation.setWarehouseId(deliveryConfirmationV3.getWarehouseId());
					newDeliveryConfirmation.setLoginUserId(deliveryConfirmationV3.getLoginUserId());
					newDeliveryConfirmation.setPriceSegment(deliveryLine.getPriceSegement());
					newDeliveryConfirmation.setOrderReceivedOn(new Date());
					newDeliveryConfirmation.setProcessedStatusId(9L);	// For SAP Delivery confirm orders
					return newDeliveryConfirmation;
				}).collect(toList());

				saveDeliveryConfirmation(deliveryConfirmations);
				log.info("DeliveryConfirmation saved from SAP ----> {}", deliveryConfirmations);
			}
		} catch (Exception e) {
			log.info("Exception while delivery template validate...!");
			throw e;
		}
	}
	
	/**
	 *
	 * @param deliveryConfirmationList
	 */
	private void saveDeliveryConfirmation(@Valid List<DeliveryConfirmation> deliveryConfirmationList) throws Exception {
       try {
           deliveryConfirmationRepository.saveAll(deliveryConfirmationList);
		} catch (Exception e) {
			log.error("Exception while saving delivery template..!");
			throw e;
		}
	}
}
package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityLine;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.QualityLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.QualityLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityLineService extends BaseService {

	@Autowired
	private QualityLineRepository qualityLineRepository;

	@Autowired
	private OutboundLineRepository outboundLineRepository;

	@Autowired
	private QualityHeaderService qualityHeaderService;

	@Autowired
	private OutboundHeaderService outboundHeaderService;

	@Autowired
	private OutboundLineService outboundLineService;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private MastersService mastersService;

	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private InventoryMovementService inventoryMovementService;

	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;

	/**
	 * getQualityLines
	 * 
	 * @return
	 */
	public List<QualityLine> getQualityLines() {
		List<QualityLine> qualityLineList = qualityLineRepository.findAll();
		qualityLineList = qualityLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return qualityLineList;
	}

	/**
	 * getQualityLine
	 * 
	 * @return
	 */
	public QualityLine getQualityLine(String partnerCode) {
		QualityLine qualityLine = qualityLineRepository.findByPartnerCode(partnerCode).orElse(null);
		if (qualityLine != null && qualityLine.getDeletionIndicator() == 0) {
			return qualityLine;
		} else {
			throw new BadRequestException("The given QualityLine ID : " + partnerCode + " doesn't exist.");
		}
	}

	/**
	 * 
	 * // Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public QualityLine getQualityLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			Long lineNumber, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		throw new BadRequestException("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
				+ preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
				+ lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public QualityLine getQualityLineValidated(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public List<QualityLine> getQualityLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode) {
		List<QualityLine> qualityLine = qualityLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public QualityLine getQualityLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
				+ ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
				+ ",itemCode:" + itemCode + " doesn't exist.");
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public List<QualityLine> getQualityLineForUpdateForDeliverConformation(String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
		List<QualityLine> qualityLine = qualityLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
				+ ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
				+ ",itemCode:" + itemCode + " doesn't exist.");
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumbers
	 * @param itemCodes
	 * @return
	 */
	public List<QualityLine> getQualityLine (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
		List<QualityLine> qualityLine = qualityLineRepository
				.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param qualityInspectionNo
	 * @param itemCode
	 * @return
	 */
	private QualityLine getQualityLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
						itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		throw new BadRequestException(
				"The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
						+ ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
						+ ",qualityInspectionNo:" + qualityInspectionNo + ",itemCode:" + itemCode + " doesn't exist.");
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param qualityInspectionNo
	 * @param itemCode
	 * @return
	 */
	private QualityLine findDuplicateRecord(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
						itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param qualityInspectionNo
	 * @param itemCode
	 * @return
	 */
	private QualityLine findQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
		QualityLine qualityLine = qualityLineRepository
				.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
						itemCode, 0L);
		if (qualityLine != null) {
			return qualityLine;
		}
		return null;
	}

	/**
	 * 
	 * @param searchQualityLine
	 * @return
	 * @throws ParseException
	 */
	public List<QualityLine> findQualityLine(SearchQualityLine searchQualityLine) throws ParseException {
		QualityLineSpecification spec = new QualityLineSpecification(searchQualityLine);
		List<QualityLine> results = qualityLineRepository.findAll(spec);
		return results;
	}
	
	/**
	 * 
	 * @param personList
	 * @return
	 */
	public static List<AddQualityLine> getDuplicates(List<AddQualityLine> qualityLineList) {
		return getDuplicatesMap(qualityLineList).values().stream()
	      .filter(duplicates -> duplicates.size() > 1)
	      .flatMap(Collection::stream)
	      .collect(Collectors.toList());
	}

	/**
	 * 
	 * @param personList
	 * @return
	 */
	private static Map<String, List<AddQualityLine>> getDuplicatesMap(List<AddQualityLine> addQualityLineList) {
	  return addQualityLineList.stream().collect(Collectors.groupingBy(AddQualityLine::uniqueAttributes));
	}
	
	/**
	 * createQualityLine
	 * 
	 * @param newQualityLine
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<QualityLine> createQualityLine(List<AddQualityLine> newQualityLines, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			log.info("-------createQualityLine--------called-------> " + new Date());
			
			List<AddQualityLine> dupQualityLines = getDuplicates (newQualityLines);
			log.info("-------dupQualityLines--------> " + dupQualityLines);
			if (dupQualityLines != null && !dupQualityLines.isEmpty()) {
				newQualityLines.removeAll(dupQualityLines);
				newQualityLines.add(dupQualityLines.get(0));
				log.info("-------newQualityLines---removed-dupQualityLines-----> " + newQualityLines);
			}
			
			/*
			 * The below flag helps to avoid duplicate request and updating of outboundline
			 * table
			 */
			List<QualityLine> createdQualityLineList = new ArrayList<>();
			for (AddQualityLine newQualityLine : newQualityLines) {
				log.info("Input from UI:  " + newQualityLine);

				QualityLine dbQualityLine = new QualityLine();
				BeanUtils.copyProperties(newQualityLine, dbQualityLine,
						CommonUtils.getNullPropertyNames(newQualityLine));

				// STATUS_ID - HardCoded Value "55"
				dbQualityLine.setStatusId(55L);
				dbQualityLine.setDeletionIndicator(0L);
				dbQualityLine.setQualityCreatedBy(loginUserID);
				dbQualityLine.setQualityUpdatedBy(loginUserID);
				dbQualityLine.setQualityCreatedOn(new Date());
				dbQualityLine.setQualityUpdatedOn(new Date());

				/*
				 * String warehouseId, String preOutboundNo, String refDocNumber, String
				 * partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode
				 */
				QualityLine existingQualityLine = findDuplicateRecord(newQualityLine.getWarehouseId(),
						newQualityLine.getPreOutboundNo(), newQualityLine.getRefDocNumber(),
						newQualityLine.getPartnerCode(), newQualityLine.getLineNumber(),
						newQualityLine.getQualityInspectionNo(), newQualityLine.getItemCode());
				log.info("existingQualityLine record status : " + existingQualityLine);

				/*
				 * Checking whether the record already exists (created) or not. If it is not
				 * created then only the rest of the logic has been carry forward
				 */
				if (existingQualityLine == null) {
					QualityLine createdQualityLine = qualityLineRepository.save(dbQualityLine);
					log.info("createdQualityLine: " + createdQualityLine);
					createdQualityLineList.add(createdQualityLine);
				}
			}

			/*
			 * Based on created QualityLine List, updating respective tables
			 */
			AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
			for (QualityLine dbQualityLine : createdQualityLineList) {
				/*-----------------STATUS updates in QualityHeader-----------------------*/
				try {
					UpdateQualityHeader updateQualityHeader = new UpdateQualityHeader();
					updateQualityHeader.setStatusId(55L);
					StatusId idStatus = idmasterService.getStatus(55L, dbQualityLine.getWarehouseId(), authTokenForIDService.getAccess_token());
					updateQualityHeader.setReferenceField10(idStatus.getStatus());
					
					QualityHeader qualityHeader = qualityHeaderService.updateQualityHeader(					
							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
							dbQualityLine.getRefDocNumber(), dbQualityLine.getQualityInspectionNo(),
							dbQualityLine.getActualHeNo(), loginUserID, updateQualityHeader);
					log.info("qualityHeader updated : " + qualityHeader);
				} catch (Exception e1) {
					e1.printStackTrace();
					log.info("qualityHeader updated Error : " + e1.toString());
				}
				
				/*-------------OTUBOUNDHEADER/OUTBOUNDLINE table updates------------------------------*/
				/*
				 * DLV_ORD_NO
				 * -----------------------------------------------------------------------------
				 * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 12 in
				 * NUMBERRANGE table and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR
				 * and add +1 and insert
				 */
				Long NUM_RAN_CODE = 12L;
				String DLV_ORD_NO = getNextRangeNumber(NUM_RAN_CODE, dbQualityLine.getWarehouseId());

				/*-------------------OUTBOUNDLINE------Update---------------------------*/
				/*
				 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE /OB_LINE_NO/_ITM_CODE values in
				 * QUALITYILINE table and fetch QC_QTY values and pass the same values in
				 * OUTBOUNDLINE table and update DLV_QTY
				 * 
				 * Pass Unique keys in OUTBOUNDLINE table and update STATUS_ID as "57"
				 */
				try {
					//---------------Update-Lock-Applied---------------------------------------------------------
					OutboundLine outboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
							dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
							dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
							dbQualityLine.getItemCode());
					log.info("DB outboundLine : " + outboundLine);
					if (outboundLine != null) {
						outboundLine.setDeliveryOrderNo(DLV_ORD_NO);
						outboundLine.setStatusId(57L);

						Double exisitingDelQty = 0D;
						if (outboundLine.getDeliveryQty() != null) {
							exisitingDelQty = outboundLine.getDeliveryQty();
						} else {
							exisitingDelQty = 0D;
						}
						log.info("DB queried outboundLine existingDelQty : " + exisitingDelQty);
						outboundLine.setDeliveryQty(exisitingDelQty + dbQualityLine.getQualityQty());
						
						log.info("DB after outboundLine existingDelQty : " + exisitingDelQty);
						outboundLine = outboundLineRepository.save(outboundLine);
						log.info("outboundLine updated : " + outboundLine);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					log.info("outboundLine updated error: " + e1.toString());
				}
					
				try {
					/*-------------------OUTBOUNDHEADER------Update---------------------------*/
					boolean isStatus57 = false;
					List<OutboundLine> outboundLines = outboundLineService.getOutboundLine(
							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
							dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode());
					outboundLines = outboundLines.stream().filter(o -> o.getStatusId() == 57L)
							.collect(Collectors.toList());
					if (outboundLines != null) {
						isStatus57 = true;
					}

					UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
					updateOutboundHeader.setDeliveryOrderNo(DLV_ORD_NO);
					if (isStatus57) { // If Status if 57 then update OutboundHeader with Status 57.
						updateOutboundHeader.setStatusId(57L);
					}

					OutboundHeader outboundHeader = outboundHeaderService.updateOutboundHeader(
							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
							dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), updateOutboundHeader,
							loginUserID);
					log.info("outboundHeader updated : " + outboundHeader);
				} catch (Exception e1) {
					e1.printStackTrace();
					log.info("outboundHeader updated error: " + e1.toString());
				}
				
				/*-----------------Inventory Updates--------------------------------------*/
				// Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE in INVENTORY table
				AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
				Long BIN_CLASS_ID = 4L;
				StorageBin storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
						authTokenForMastersService.getAccess_token());
				Warehouse warehouse = getWarehouse(dbQualityLine.getWarehouseId());
				Inventory inventory = null;
				try {
					inventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
							dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
							storageBin.getStorageBin());
					log.info("inventory---BIN_CLASS_ID-4----> : " + inventory);

					if (inventory != null) {
						Double INV_QTY = inventory.getInventoryQuantity() - dbQualityLine.getQualityQty();
						log.info("Calculated inventory INV_QTY: " + INV_QTY);
						inventory.setInventoryQuantity(INV_QTY);

						// INV_QTY > 0 then, update Inventory Table
						inventory = inventoryRepository.save(inventory);
						log.info("inventory updated : " + inventory);

						if (INV_QTY == 0) {
							log.info("inventory INV_QTY: " + INV_QTY);
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				/*-------------------Inserting record in InventoryMovement-------------------------------------*/
				Long subMvtTypeId = 2L;
				String movementDocumentNo = dbQualityLine.getQualityInspectionNo();
				String stBin = storageBin.getStorageBin();
				String movementQtyValue = "N";
				
				QualityLine qualityLine = findQualityLine(dbQualityLine.getWarehouseId(),
						dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(),
						dbQualityLine.getLineNumber(), dbQualityLine.getQualityInspectionNo(), dbQualityLine.getItemCode());
				
				InventoryMovement inventoryMovement = createInventoryMovement(qualityLine, subMvtTypeId,
						movementDocumentNo, stBin, movementQtyValue, loginUserID);
				log.info("InventoryMovement created : " + inventoryMovement);

				/*--------------------------------------------------------------------------*/
				// 2.Insert a new record in INVENTORY table as below
				// Fetch from QUALITYLINE table and insert WH_ID/ITM_CODE/ST_BIN= (ST_BIN value
				// of BIN_CLASS_ID=5
				// from STORAGEBIN table)/PACK_BARCODE/INV_QTY = QC_QTY - INVENTORY UPDATE 2
				BIN_CLASS_ID = 5L;
				storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
						authTokenForMastersService.getAccess_token());
				warehouse = getWarehouse(dbQualityLine.getWarehouseId());

				/*
				 * Checking Inventory table before creating new record inventory
				 */
				// Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=5 /PACK_BARCODE
				Inventory existingInventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
						dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(), storageBin.getStorageBin());
				log.info("existingInventory : " + existingInventory);
				if (existingInventory != null) {
					Double INV_QTY = existingInventory.getInventoryQuantity() + dbQualityLine.getQualityQty();
					UpdateInventory updateInventory = new UpdateInventory();
					updateInventory.setInventoryQuantity(INV_QTY);
					try {
						Inventory updatedInventory = inventoryService.updateInventory(dbQualityLine.getWarehouseId(),
								dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
								storageBin.getStorageBin(), 1L, 1L, updateInventory, loginUserID);
						log.info("updatedInventory----------> : " + updatedInventory);
					} catch (Exception e) {
						e.printStackTrace();
						log.info("updatedInventory error----------> : " + e.toString());
					}
				} else {
					log.info("AddInventory========>");
					AddInventory newInventory = new AddInventory();
					newInventory.setLanguageId(warehouse.getLanguageId());
					newInventory.setCompanyCodeId(warehouse.getCompanyCode());
					newInventory.setPlantId(warehouse.getPlantId());
					newInventory.setStockTypeId(inventory.getStockTypeId());
					newInventory.setBinClassId(BIN_CLASS_ID);
					newInventory.setWarehouseId(dbQualityLine.getWarehouseId());
					newInventory.setPackBarcodes(dbQualityLine.getPickPackBarCode());
					newInventory.setItemCode(dbQualityLine.getItemCode());
					newInventory.setStorageBin(storageBin.getStorageBin());
					newInventory.setInventoryQuantity(dbQualityLine.getQualityQty());
					newInventory.setSpecialStockIndicatorId(1L);

					List<IImbasicData1> imbasicdata1 = imbasicdata1Repository
							.findByItemCode(newInventory.getItemCode());
					if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
						newInventory.setReferenceField8(imbasicdata1.get(0).getDescription());
						newInventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
					}
					if (storageBin != null) {
						newInventory.setReferenceField10(storageBin.getStorageSectionId());
						newInventory.setReferenceField5(storageBin.getAisleNumber());
						newInventory.setReferenceField6(storageBin.getShelfId());
						newInventory.setReferenceField7(storageBin.getRowId());
					}

					Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
					log.info("newInventory created : " + createdInventory);
				}
				
				/*-----------------------InventoryMovement----------------------------------*/
				// Inserting record in InventoryMovement
				subMvtTypeId = 2L;
				movementDocumentNo = DLV_ORD_NO;
				stBin = storageBin.getStorageBin();
				movementQtyValue = "P";
				inventoryMovement = createInventoryMovement(qualityLine, subMvtTypeId, movementDocumentNo, stBin,
						movementQtyValue, loginUserID);
				log.info("InventoryMovement created for update2: " + inventoryMovement);
			}
			return createdQualityLineList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * @param dbQualityLine
	 * @param subMvtTypeId
	 * @param movementDocumentNo
	 * @param storageBin
	 * @param movementQtyValue
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private InventoryMovement createInventoryMovement(QualityLine dbQualityLine, Long subMvtTypeId,
			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
		BeanUtils.copyProperties(dbQualityLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbQualityLine));

		// MVT_TYP_ID
		inventoryMovement.setMovementType(3L);

		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(subMvtTypeId);

		// VAR_ID
		inventoryMovement.setVariantCode(1L);

		// VAR_SUB_ID
		inventoryMovement.setVariantSubCode("1");

		// STR_MTD
		inventoryMovement.setStorageMethod("1");

		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");

		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(movementDocumentNo);

		// ST_BIN
		inventoryMovement.setStorageBin(storageBin);

		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue(movementQtyValue);

		// PACK_BAR_CODE
		inventoryMovement.setPackBarcodes(dbQualityLine.getPickPackBarCode());

		// MVT_QTY
		inventoryMovement.setMovementQty(dbQualityLine.getPickConfirmQty());

		// MVT_UOM
		inventoryMovement.setInventoryUom(dbQualityLine.getQualityConfirmUom());

		// IM_CTD_BY
		inventoryMovement.setCreatedBy(dbQualityLine.getQualityConfirmedBy());

		// IM_CTD_ON
		inventoryMovement.setCreatedOn(dbQualityLine.getQualityCreatedOn());

		InventoryMovement createdInventoryMovement = inventoryMovementService.createInventoryMovement(inventoryMovement,
				loginUserID);
		return createdInventoryMovement;
	}

	/**
	 * updateQualityLine
	 * 
	 * @param loginUserId
	 * @param partnerCode
	 * @param loginUserID2
	 * @param updateQualityLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<QualityLine> updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID,
			UpdateQualityLine updateQualityLine) throws IllegalAccessException, InvocationTargetException {
		List<QualityLine> dbQualityLine = getQualityLineForUpdateForDeliverConformation(warehouseId, preOutboundNo,
				refDocNumber, partnerCode, lineNumber, itemCode);
		if (dbQualityLine != null) {
			dbQualityLine.forEach(data -> {
				BeanUtils.copyProperties(updateQualityLine, data, CommonUtils.getNullPropertyNames(updateQualityLine));
				data.setQualityUpdatedBy(loginUserID);
				data.setQualityUpdatedOn(new Date());
			});
			return qualityLineRepository.saveAll(dbQualityLine);
		}
		return null;
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param qualityInspectionNo
	 * @param itemCode
	 * @param loginUserID
	 * @param updateQualityLine
	 * @return
	 */
	public QualityLine updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID,
			@Valid UpdateQualityLine updateQualityLine) {
		QualityLine dbQualityLine = getQualityLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, qualityInspectionNo, itemCode);
		if (dbQualityLine != null) {
			BeanUtils.copyProperties(updateQualityLine, dbQualityLine,
					CommonUtils.getNullPropertyNames(updateQualityLine));
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		}
		return null;
	}

	/**
	 * deleteQualityLine
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public QualityLine deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		QualityLine dbQualityLine = getQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
				itemCode);
		if (dbQualityLine != null) {
			dbQualityLine.setDeletionIndicator(1L);
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}

	public List<QualityLine> deleteQualityLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<QualityLine> dbQualityLine = getQualityLineForReversal(warehouseId, preOutboundNo, refDocNumber,
				partnerCode, lineNumber, itemCode);
		if (dbQualityLine != null && !dbQualityLine.isEmpty()) {
			List<QualityLine> qualityLineList = new ArrayList<>();
			dbQualityLine.forEach(data -> {
				data.setDeletionIndicator(1L);
				data.setQualityUpdatedBy(loginUserID);
				data.setQualityUpdatedOn(new Date());
				qualityLineList.add(data);
			});
			return qualityLineRepository.saveAll(qualityLineList);
		} else {
			return null;
		}
	}

	public QualityLine deleteQualityLineValidated(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		QualityLine dbQualityLine = getQualityLineValidated(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, itemCode);
		if (dbQualityLine != null) {
			dbQualityLine.setDeletionIndicator(1L);
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param lineNumber
	 * @param qualityInspectionNo
	 * @param itemCode
	 * @param loginUserID
	 * @return
	 */
	public QualityLine deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID) {
		QualityLine dbQualityLine = getQualityLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				lineNumber, qualityInspectionNo, itemCode);
		if (dbQualityLine != null) {
			dbQualityLine.setDeletionIndicator(1L);
			dbQualityLine.setQualityUpdatedBy(loginUserID);
			dbQualityLine.setQualityUpdatedOn(new Date());
			return qualityLineRepository.save(dbQualityLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
		}
	}
}

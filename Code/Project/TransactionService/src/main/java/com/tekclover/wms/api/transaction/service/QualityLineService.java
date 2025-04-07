package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.outbound.quality.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLineInterim;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineInterimRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.QualityHeaderRepository;
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
	private QualityHeaderRepository qualityHeaderRepository;
	
	@Autowired
	private OutboundLineRepository outboundLineRepository;
	
	@Autowired
	private PreOutboundLineRepository preOutboundLineRepository;

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
	
	@Autowired
	private OutboundLineInterimRepository outboundLineInterimRepository;
	
	@Autowired
	private TransactionErrorService transactionErrorService;
	
	@Autowired
	private PickupLineRepository pickupLineRepository;
	
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
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
			Long BIN_CLASS_ID = 4L;
			StorageBin storageBin = null;
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
					
					// createOutboundLineInterim
					createOutboundLineInterim (createdQualityLine);
					createdQualityLineList.add(createdQualityLine);
					log.info("createdQualityLineList------>: " + createdQualityLineList);
					
					/*
					 * QualityHeader Status Update
					 */
					StatusId idStatus = idmasterService.getStatus(55L, dbQualityLine.getWarehouseId(), authTokenForIDService.getAccess_token());
					qualityHeaderRepository.updateQualityHeader(idStatus.getStatus(), dbQualityLine.getQualityInspectionNo());
					log.info("-----updateQualityHeader--updated---->: " + idStatus.getStatus());
					
					storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
					log.info("storageBin------>: " + storageBin);
					
//					// Inventory Update
//					Inventory inventory = null;
//					try {
//						storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
//								authTokenForMastersService.getAccess_token());
//						inventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
//								dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(), storageBin.getStorageBin());
//						log.info("inventory---BIN_CLASS_ID-4----> : " + inventory);
//
//						if (inventory != null) {
//							Double INV_QTY = inventory.getInventoryQuantity() - dbQualityLine.getQualityQty();
//							log.info("Calculated inventory INV_QTY: " + INV_QTY);
//							inventory.setInventoryQuantity(INV_QTY);
//
//							// INV_QTY > 0 then, update Inventory Table
//							inventory = inventoryRepository.save(inventory);
//							log.info("inventory updated : " + inventory);
//
//							if (INV_QTY == 0) {
//								log.info("inventory INV_QTY: " + INV_QTY);
//							}
//						}
//					} catch (Exception e1) {
//						String objectData = (dbQualityLine.getWarehouseId() + "|" + BIN_CLASS_ID + "|" + 
//								dbQualityLine.getPickPackBarCode() + "|" + dbQualityLine.getItemCode() + "|" + storageBin.getStorageBin());
//						transactionErrorService.createTransactionError("INVENTORY", 
//								"createdQualityLine | Inventory Update | existingInventory is null", 
//								e1.getMessage(), e1.getLocalizedMessage(), objectData, loginUserID);	
//						log.error("createdQualityLine | Inventory Update Error : " + e1.toString());;
//						e1.printStackTrace();
//					}
				}
			}

			/*
			 * Based on created QualityLine List, updating respective tables
			 */
			for (QualityLine dbQualityLine : createdQualityLineList) {
				/*-----------------STATUS updates in QualityHeader-----------------------*/
//				Optional<QualityHeader> qualityHeaderOpt = 
//						qualityHeaderRepository.findByQualityInspectionNo(dbQualityLine.getQualityInspectionNo());
				Optional<QualityHeader> qualityHeaderOpt = 
						qualityHeaderRepository.findByQualityInspectionNoAndRefDocNumberAndReferenceField4 (dbQualityLine.getQualityInspectionNo(),
								dbQualityLine.getRefDocNumber(), dbQualityLine.getItemCode());
				QualityHeader qualityHeader = qualityHeaderOpt.get();
//				try {
//					UpdateQualityHeader updateQualityHeader = new UpdateQualityHeader();
//					updateQualityHeader.setStatusId(55L);
//					StatusId idStatus = idmasterService.getStatus(55L, dbQualityLine.getWarehouseId(), authTokenForIDService.getAccess_token());
//					updateQualityHeader.setReferenceField10(idStatus.getStatus());
//					qualityHeader = qualityHeaderService.updateQualityHeader(					
//							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
//							dbQualityLine.getRefDocNumber(), dbQualityLine.getQualityInspectionNo(),
//							dbQualityLine.getActualHeNo(), loginUserID, updateQualityHeader);
//					log.info("qualityHeader updated : " + qualityHeader);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					log.error("UpdateQualityHeader error: " + e1.toString());
//				}
				
				Long NUM_RAN_CODE = 12L;
				String DLV_ORD_NO = getNextRangeNumber(NUM_RAN_CODE, dbQualityLine.getWarehouseId());
				
//				try {
					/*-------------------OUTBOUNDLINE------Update---------------------------*/
//					updateOutboundLine (dbQualityLine, DLV_ORD_NO);
					
					/*-------------------OUTBOUNDHEADER------Update---------------------------*/
//					outboundHeaderService.updateOutboundHeaderByProcedure (dbQualityLine.getWarehouseId(), 
//							dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), loginUserID);
					try {
						UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
						updateOutboundHeader.setStatusId(57L);
						outboundHeaderService.updateOutboundHeader(dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(), 
								dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), updateOutboundHeader, loginUserID);
						log.info("------outboundHeader updated as 57---------");
					} catch (Exception e1) {
						e1.printStackTrace();
						log.error("UpdateOutboundHeader error: " + e1.toString());
					}
					
					/*------------------------------------------------------------------------*/
					
//					boolean isStatus57 = false;
//					List<OutboundLine> outboundLines = outboundLineService.getOutboundLine(
//							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
//							dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode());
//					outboundLines = outboundLines.stream().filter(o -> o.getStatusId() == 57L)
//							.collect(Collectors.toList());
//					if (outboundLines != null) {
//						isStatus57 = true;
//					}
//
//					UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
//					updateOutboundHeader.setDeliveryOrderNo(DLV_ORD_NO);
//					if (isStatus57) { // If Status if 57 then update OutboundHeader with Status 57.
//						updateOutboundHeader.setStatusId(57L);
//					}
//
//					OutboundHeader outboundHeader = outboundHeaderService.updateOutboundHeader(
//							dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
//							dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), updateOutboundHeader,
//							loginUserID);
//					log.info("outboundHeader updated : " + outboundHeader);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					log.info("outboundHeader updated error: " + e1.toString());
//				}
				
				/*-----------------Inventory Updates--------------------------------------*/
				// Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE in INVENTORY table
//				Warehouse warehouse = getWarehouse(dbQualityLine.getWarehouseId());
//				Inventory inventory = null;
//				try {
//					inventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
//							dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
//							storageBin.getStorageBin());
//					log.info("inventory---BIN_CLASS_ID-4----> : " + inventory);
//
//					if (inventory != null) {
//						Double INV_QTY = inventory.getInventoryQuantity() - dbQualityLine.getQualityQty();
//						log.info("Calculated inventory INV_QTY: " + INV_QTY);
//						inventory.setInventoryQuantity(INV_QTY);
//
//						// INV_QTY > 0 then, update Inventory Table
//						inventory = inventoryRepository.save(inventory);
//						log.info("inventory updated : " + inventory);
//
//						if (INV_QTY == 0) {
//							log.info("inventory INV_QTY: " + INV_QTY);
//						}
//					}
//				} catch (Exception e1) {
//					String objectData = (dbQualityLine.getWarehouseId() + "|" + BIN_CLASS_ID + "|" + 
//							dbQualityLine.getPickPackBarCode() + "|" + dbQualityLine.getItemCode() + "|" + storageBin.getStorageBin());
//					transactionErrorService.createTransactionError("INVENTORY", 
//							"createPickupLine | Inventory Update | existingInventory is not null", 
//							e1.getMessage(), e1.getLocalizedMessage(), objectData, loginUserID);	
//					e1.printStackTrace();
//				}
				
				/*-------------------Inserting record in InventoryMovement-------------------------------------*/
				Long subMvtTypeId = 2L;
				String movementDocumentNo = dbQualityLine.getQualityInspectionNo();
				String stBin = storageBin.getStorageBin();
				String movementQtyValue = "N";
				InventoryMovement inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId,
						movementDocumentNo, stBin, movementQtyValue, loginUserID);
				log.info("InventoryMovement created : " + inventoryMovement);

				/*--------------------------------------------------------------------------*/
				// 2.Insert a new record in INVENTORY table as below
				// Fetch from QUALITYLINE table and insert WH_ID/ITM_CODE/ST_BIN= (ST_BIN value
				// of BIN_CLASS_ID=5
//				// from STORAGEBIN table)/PACK_BARCODE/INV_QTY = QC_QTY - INVENTORY UPDATE 2
//				BIN_CLASS_ID = 5L;
//				storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
//						authTokenForMastersService.getAccess_token());
//				Warehouse warehouse = getWarehouse(dbQualityLine.getWarehouseId());
//
//				/*
//				 * Checking Inventory table before creating new record inventory
//				 */
//				// Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=5 /PACK_BARCODE
//				Inventory existingInventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
//						dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(), storageBin.getStorageBin());
//				log.info("existingInventory : " + existingInventory);
//				if (existingInventory != null) {
//					Double INV_QTY = existingInventory.getInventoryQuantity() + dbQualityLine.getQualityQty();
//					UpdateInventory updateInventory = new UpdateInventory();
//					updateInventory.setInventoryQuantity(INV_QTY);
//					try {
//						Inventory updatedInventory = inventoryService.updateInventory(dbQualityLine.getWarehouseId(),
//								dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
//								storageBin.getStorageBin(), 1L, 1L, updateInventory, loginUserID);
//						log.info("updatedInventory----------> : " + updatedInventory);
//					} catch (Exception e) {
//						String objectData = (dbQualityLine.getWarehouseId() + "|" + BIN_CLASS_ID + "|" + 
//								dbQualityLine.getPickPackBarCode() + "|" + dbQualityLine.getItemCode() + "|" + storageBin.getStorageBin());
//						transactionErrorService.createTransactionError("INVENTORY", 
//								"createdQualityLine | Inventory Update | existingInventory is not null", 
//								e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);	
//						e.printStackTrace();
//						log.info("updatedInventory error----------> : " + e.toString());
//					}
//				} else {
//					log.info("AddInventory========>");
//					AddInventory newInventory = new AddInventory();
//					newInventory.setLanguageId(warehouse.getLanguageId());
//					newInventory.setCompanyCodeId(warehouse.getCompanyCode());
//					newInventory.setPlantId(warehouse.getPlantId());
////					newInventory.setStockTypeId(inventory.getStockTypeId());
//					newInventory.setStockTypeId(1L);
//					newInventory.setBinClassId(BIN_CLASS_ID);
//					newInventory.setWarehouseId(dbQualityLine.getWarehouseId());
//					newInventory.setPackBarcodes(dbQualityLine.getPickPackBarCode());
//					newInventory.setItemCode(dbQualityLine.getItemCode());
//					newInventory.setStorageBin(storageBin.getStorageBin());
//					newInventory.setInventoryQuantity(dbQualityLine.getQualityQty());
//					newInventory.setSpecialStockIndicatorId(1L);
//					newInventory.setCreatedOn(new Date());			
//					newInventory.setCreatedBy(loginUserID);
//					
//					List<IImbasicData1> imbasicdata1 = imbasicdata1Repository
//							.findByItemCode(newInventory.getItemCode());
//					if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
//						newInventory.setReferenceField8(imbasicdata1.get(0).getDescription());
//						newInventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
//					}
//					if (storageBin != null) {
//						newInventory.setReferenceField10(storageBin.getStorageSectionId());
//						newInventory.setReferenceField5(storageBin.getAisleNumber());
//						newInventory.setReferenceField6(storageBin.getShelfId());
//						newInventory.setReferenceField7(storageBin.getRowId());
//					}
//
//					Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
//					log.info("newInventory created : " + createdInventory);
//				}
				
				/*-----------------------InventoryMovement----------------------------------*/
				// Inserting record in InventoryMovement
				subMvtTypeId = 2L;
				movementDocumentNo = DLV_ORD_NO;
				stBin = storageBin.getStorageBin();
				movementQtyValue = "P";
				inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId, movementDocumentNo, stBin,
						movementQtyValue, loginUserID);
				log.info("InventoryMovement created for update2: " + inventoryMovement);
				PickupLine pickupLine = pickupLineRepository.findByPickupNumber (qualityHeader.getPickupNumber());
				
				// Creating new Inventory for Rejection of Material
				if ((dbQualityLine.getQualityQty() < dbQualityLine.getPickConfirmQty()) || 
						(dbQualityLine.getQualityQty() > dbQualityLine.getPickConfirmQty())) {
					try {
						AddInventory newInventory = new AddInventory();
						newInventory.setLanguageId(getLanguageId());
						newInventory.setCompanyCodeId(getCompanyCode());
						newInventory.setPlantId(getPlantId());
						newInventory.setBinClassId(BIN_CLASS_ID);
						newInventory.setStockTypeId(1L); 				// Hardcoding as 1L for Stock Tyope ID
						newInventory.setWarehouseId(dbQualityLine.getWarehouseId());
						newInventory.setPackBarcodes(dbQualityLine.getPickPackBarCode());
						newInventory.setItemCode(dbQualityLine.getItemCode());
						newInventory.setStorageBin(storageBin.getStorageBin());
						newInventory.setInventoryQuantity(Math.abs(pickupLine.getPickConfirmQty() - dbQualityLine.getQualityQty()));
						newInventory.setSpecialStockIndicatorId(1L); 	// Hardcoding as 1L for Stock Tyope ID
						newInventory.setCreatedOn(new Date());			
						newInventory.setCreatedBy(loginUserID);
						
						List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(newInventory.getItemCode());
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
					} catch (Exception e) {
						log.error("newInventory create Error :" + e.toString());
						e.printStackTrace();
					}
				
					/*
					 * Inventory Update 
					 */
					Inventory inventory = inventoryService.getInventory (dbQualityLine.getWarehouseId(),
							dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(), pickupLine.getPickedStorageBin());
					log.info("inventory record queried: " + inventory);
					if (inventory != null) {
						if (pickupLine.getAllocatedQty() > 0D) {
							try {
								Double ALLOC_QTY = inventory.getAllocatedQuantity() - (pickupLine.getPickConfirmQty() - dbQualityLine.getQualityQty());
								log.info("inventory ALLOC_QTY: " + ALLOC_QTY);
								log.info("Inventory: inventory.getAllocatedQuantity() ---> " + inventory.getAllocatedQuantity());
								log.info("inventory: (pickupLine.getPickConfirmQty() - dbQualityLine.getQualityQty())--->: " + 
										(pickupLine.getPickConfirmQty() - dbQualityLine.getQualityQty()));
								
								if (ALLOC_QTY < 0D) {
									ALLOC_QTY = 0D;
								}
		
								inventory.setAllocatedQuantity(ALLOC_QTY);
		
								// INV_QTY > 0 then, update Inventory Table
								inventory = inventoryRepository.save(inventory);
								log.info("inventory updated : " + inventory);
							} catch (Exception e) {
								String objectData = dbQualityLine.getPickPackBarCode() + "|" + dbQualityLine.getItemCode() + "|" + pickupLine.getPickedStorageBin();
								transactionErrorService.createTransactionError("INVENTORY", "createPickupLine | Inventory Update", e.getMessage(), e.getLocalizedMessage(), objectData, loginUserID);	
								log.error("Inventory Update Error:" + e.toString());
								e.printStackTrace();
							}
						}
					} // End of Inventory Update
				}
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
	 */
	private void createOutboundLineInterim (QualityLine dbQualityLine) {
		PreOutboundLine preOutboundLine = preOutboundLineRepository.findByWarehouseIdAndRefDocNumberAndItemCodeAndLineNumberAndDeletionIndicator (dbQualityLine.getWarehouseId(),
				dbQualityLine.getRefDocNumber(), dbQualityLine.getItemCode(), dbQualityLine.getLineNumber(), 0L);
		OutboundLineInterim outboundLineInterim = new OutboundLineInterim();
		BeanUtils.copyProperties(dbQualityLine, outboundLineInterim, CommonUtils.getNullPropertyNames(dbQualityLine));
		if (dbQualityLine.getReferenceField2() != null && dbQualityLine.getReferenceField2().isEmpty()) {
			outboundLineInterim.setReferenceField2(null);
		}
		outboundLineInterim.setDeletionIndicator(0L);
		outboundLineInterim.setOrderQty(preOutboundLine.getOrderQty());
		outboundLineInterim.setDeliveryQty(dbQualityLine.getQualityQty());
		outboundLineInterim.setCreatedBy(dbQualityLine.getQualityCreatedBy());
		outboundLineInterim.setCreatedOn(new Date());
		OutboundLineInterim createdOutboundLine = outboundLineInterimRepository.saveAndFlush(outboundLineInterim);
		log.info("outboundLineInterim created ----------->: " + createdOutboundLine);
	}
	
	/**
	 * 
	 * @param dbQualityLine
	 * @param DLV_ORD_NO
	 */
//	@Transactional(isolation = Isolation.READ_COMMITTED)
//	private void updateOutboundLine (QualityLine dbQualityLine, String DLV_ORD_NO) {
//		try {
//			//---------------Update-Lock-Applied---------------------------------------------------------
//			OutboundLine outboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
//					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
//					dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//					dbQualityLine.getItemCode());
//			log.info("DB outboundLine : " + outboundLine);
//			if (outboundLine != null) {
//				Double exisitingDelQty = 0D;
//				if (outboundLine.getDeliveryQty() != null) {
//					exisitingDelQty = outboundLine.getDeliveryQty();
//				} else {
//					exisitingDelQty = 0D;
//				}
//				exisitingDelQty = exisitingDelQty + dbQualityLine.getQualityQty();
//				log.info("DB after outboundLine existingDelQty : " + exisitingDelQty);
//				outboundLineRepository.updateOutboundLine(dbQualityLine.getWarehouseId(),
//						dbQualityLine.getRefDocNumber(), dbQualityLine.getPreOutboundNo(),
//						dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//						dbQualityLine.getItemCode(), DLV_ORD_NO, 57L, exisitingDelQty);
//				log.info("outboundLine updated.");
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			log.info("outboundLine updated error: " + e1.toString());
//		}
//	}
	
	/**
	 * 
	 * @param dbQualityLine
	 * @param DLV_ORD_NO
	 */
	private void updateOutboundLine (QualityLine dbQualityLine, String DLV_ORD_NO) {
		try {
			Double deliveryQty = outboundLineInterimRepository.getSumOfDeliveryLine (dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
				dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
				dbQualityLine.getItemCode());
			log.info("=======updateOutboundLine==========>: " + deliveryQty);
			
			// WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);
			outboundLineService.updateOutboundLineByQLCreateProc(dbQualityLine.getWarehouseId(), 
					dbQualityLine.getPreOutboundNo(), 
					dbQualityLine.getRefDocNumber(), 
					dbQualityLine.getPartnerCode(), 
					dbQualityLine.getLineNumber(), 
					dbQualityLine.getItemCode(), 
					deliveryQty,
					DLV_ORD_NO,
					57L);
			log.info("----------updateOutboundLineByQLCreateProc updated as StatusID = 57----------->");
			
//			// Get Existing Record
//			OutboundLine existingOutboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
//					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
//					dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//					dbQualityLine.getItemCode());
			
//			// Insert
//			OutboundLine outboundLine = new OutboundLine();
//			BeanUtils.copyProperties(existingOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(existingOutboundLine));
//			outboundLine.setDeliveryQty(deliveryQty);
//			outboundLine.setDeliveryOrderNo(DLV_ORD_NO);
//			outboundLine.setStatusId(57L);
//			outboundLine.setDeletionIndicator(0L);
//						
//			// Delete
//			outboundLineRepository.delete(existingOutboundLine);
////			outboundLineRepository.deleteOutboundLineMain(dbQualityLine.getWarehouseId(),
////					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
////					dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
////					dbQualityLine.getItemCode());
//			
//			OutboundLine createdOutboundLineNewly = outboundLineRepository.save(outboundLine);
//			log.info("createdOutboundLineNewly created ----------->: " + createdOutboundLineNewly);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.info("outboundLine updated error: " + e1.toString());
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

	/**
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
	public List<QualityLine> deleteQualityLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<QualityLine> dbQualityLine = getQualityLineForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
		
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
	
	/**
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
	public List<OutboundLineInterim> deleteOutboundLineInterimForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		List<OutboundLineInterim> listOutboundLineInterim = outboundLineInterimRepository.
				findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(warehouseId, 
						preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
		if (listOutboundLineInterim != null && !listOutboundLineInterim.isEmpty()) {
			listOutboundLineInterim.forEach(data -> {
				data.setDeletionIndicator(1L);
				data.setUpdatedBy(loginUserID);
				data.setUpdatedOn(new Date());
			});
			return outboundLineInterimRepository.saveAll(listOutboundLineInterim);
		}
		return listOutboundLineInterim;
	}

	/**
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

	/**
	 *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preOutboundNo
	 * @param itemCode
	 * @param partnerCode
	 * @param lineNumber
	 * @param loginUserID
	 * @throws Exception
	 */
	public void qualityLineReversal(String companyCodeId, String plantId, String languageId, String warehouseId,
									String refDocNumber, String preOutboundNo, String itemCode, String partnerCode,
									Long lineNumber, Long statusId, Long statusId50, String statusDescription, String loginUserID) throws Exception {
		try {

			List<QualityLine> qualityLines = getQualityLineForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
			log.info("QualityLine--------for cancel-------> : " + qualityLines.size());
			if (qualityLines != null && !qualityLines.isEmpty()) {
				for(QualityLine qualityLine : qualityLines) {
					qualityHeaderRepository.updateQualityHeaderReversal(statusId, statusDescription, loginUserID, qualityLine.getQualityInspectionNo());
					outboundLineRepository.updateOutboundLineStatus (warehouseId, refDocNumber, statusId50, lineNumber);
					qualityLineRepository.delete(qualityLine);
				}
			}
			log.info("Quality Line reversal finished !");
		} catch (Exception e) {
			log.error("Exception while quality line reversal process : " + e.getMessage());
			throw e;
		}
	}

	/**
	 *
	 * @param qualityReversalInputList
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	public void batchQualityReversalV2(List<ReversalInput> qualityReversalInputList, String loginUserID) throws Exception {
		log.info("OutboundReversal Input: " + qualityReversalInputList);
		if(qualityReversalInputList != null && !qualityReversalInputList.isEmpty()) {
			AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
			Long STATUS_ID = 55L;
			Long STATUS_ID_50 = 50L;
			for (ReversalInput outboundReversalInput : qualityReversalInputList){
				StatusId idStatus = idmasterService.getStatus(STATUS_ID, outboundReversalInput.getWarehouseId(), authTokenForIDService.getAccess_token());
				String statusDescription = idStatus.getStatus();
				qualityLineReversal(outboundReversalInput.getCompanyCodeId(), outboundReversalInput.getPlantId(), outboundReversalInput.getLanguageId(), outboundReversalInput.getWarehouseId(),
						outboundReversalInput.getRefDocNumber(), outboundReversalInput.getPreOutboundNo(), outboundReversalInput.getItemCode(), outboundReversalInput.getPartnerCode(),
						outboundReversalInput.getLineReference(), STATUS_ID, STATUS_ID_50, statusDescription, loginUserID);
			}
		}
	}
}

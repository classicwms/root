package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.GrLine;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.putaway.AddPutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayHeader;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PutAwayHeaderService extends BaseService {
	
	@Autowired
	private PutAwayHeaderRepository putAwayHeaderRepository;
	
	@Autowired
	private PutAwayLineRepository putAwayLineRepository;
	
	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;
	
	@Autowired
	private InboundLineRepository inboundLineRepository;
	
	@Autowired
	private PutAwayLineService putAwayLineService;
	
	@Autowired
	private InboundLineService inboundLineService;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private GrLineService grLineService;
	
	/**
	 * getPutAwayHeaders
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeaders () {
		List<PutAwayHeader> putAwayHeaderList =  putAwayHeaderRepository.findAll();
		putAwayHeaderList = putAwayHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return putAwayHeaderList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param putAwayNumber
	 * @param proposedStorageBin
	 * @return
	 */
	public PutAwayHeader getPutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin) {
		Optional<PutAwayHeader> putAwayHeader = 
				putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					warehouseId, 
					preInboundNo, 
					refDocNumber, 
					goodsReceiptNo, 
					palletCode, 
					caseCode, 
					packBarcodes, 
					putAwayNumber,
					proposedStorageBin,
					0L
					);
			if (putAwayHeader.isEmpty()) {
				throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
						",refDocNumber: " + refDocNumber + "," +
						",preInboundNo: " + preInboundNo + "," +
						",goodsReceiptNo: " + goodsReceiptNo + 
						",palletCode: " + palletCode + 
						",caseCode: " + caseCode + 
						",packBarcodes: " + packBarcodes + 
						",putAwayNumber: " + putAwayNumber + 
						",proposedStorageBin: " + proposedStorageBin +
						" doesn't exist.");
			} 
		return putAwayHeader.get();
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param putAwayNumber
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String putAwayNumber) {
		List<PutAwayHeader> putAwayHeader = 
				putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator (
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					warehouseId, 
					preInboundNo, 
					refDocNumber, 
					putAwayNumber,
					0L
					);
			if (putAwayHeader.isEmpty()) {
				throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
						",refDocNumber: " + refDocNumber + 
						",preInboundNo: " + preInboundNo + 
						",putAwayNumber: " + putAwayNumber + 
						" doesn't exist.");
			} 
		return putAwayHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber) {
		List<PutAwayHeader> putAwayHeader = 
				putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator (
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					warehouseId, 
					preInboundNo, 
					refDocNumber, 
					0L
					);
			if (putAwayHeader.isEmpty()) {
				throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
						",refDocNumber: " + refDocNumber + "," +
						",preInboundNo: " + preInboundNo + "," +
						" doesn't exist.");
			} 
		return putAwayHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @return
	 */
	public long getPutawayHeaderByStatusId (String warehouseId, String preInboundNo, String refDocNumber) {
		long putAwayHeaderStatusIdCount = putAwayHeaderRepository.getPutawayHeaderCountByStatusId(getCompanyCode(), getPlantId(), warehouseId, preInboundNo, refDocNumber);
		return putAwayHeaderStatusIdCount;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param packBarcodes
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeader (String refDocNumber, String packBarcodes) {
		List<PutAwayHeader> putAwayHeader = 
				putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator (
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					refDocNumber, 
					packBarcodes,
					0L
					);
			if (putAwayHeader.isEmpty()) {
				throw new BadRequestException("The given values: " + 
						",refDocNumber: " + refDocNumber + "," +
						",packBarcodes: " + packBarcodes + "," +
						" doesn't exist.");
			} 
		return putAwayHeader;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeader (String refDocNumber) {
		List<Long> statusIds = Arrays.asList(19L, 20L);
		List<PutAwayHeader> putAwayHeader = 
				putAwayHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator (
					getLanguageId(),
					getCompanyCode(),
					getPlantId(),
					refDocNumber, 
					statusIds,
					0L
					);
			if (putAwayHeader.isEmpty()) {
				throw new BadRequestException("The given values: " + 
						",refDocNumber: " + refDocNumber + "," +
						" doesn't exist.");
			} 
		return putAwayHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<PutAwayHeader> getPutAwayHeaderCount (String warehouseId, List<Long> orderTypeId) {
		List<PutAwayHeader> header = 
				putAwayHeaderRepository.findByWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator (
						warehouseId, 19L, orderTypeId, 0L);
		return header;
	}
	
	/**
	 * 
	 * @param searchPutAwayHeader
	 * @return
	 * @throws Exception
	 */
	public List<PutAwayHeader> findPutAwayHeader(SearchPutAwayHeader searchPutAwayHeader) 
			throws Exception {
		if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(), 
					searchPutAwayHeader.getEndCreatedOn());
			searchPutAwayHeader.setStartCreatedOn(dates[0]);
			searchPutAwayHeader.setEndCreatedOn(dates[1]);
		}
		
		PutAwayHeaderSpecification spec = new PutAwayHeaderSpecification(searchPutAwayHeader);
		List<PutAwayHeader> results = putAwayHeaderRepository.findAll(spec);
		return results;
	}

	/**
	 *
	 * @param searchPutAwayHeader
	 * @return
	 * @throws Exception
	 */
	//Stream
	public Stream<PutAwayHeader> findPutAwayHeaderNew(SearchPutAwayHeader searchPutAwayHeader)
			throws Exception {
		if (searchPutAwayHeader.getStartCreatedOn() != null && searchPutAwayHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayHeader.getStartCreatedOn(),
					searchPutAwayHeader.getEndCreatedOn());
			searchPutAwayHeader.setStartCreatedOn(dates[0]);
			searchPutAwayHeader.setEndCreatedOn(dates[1]);
		}

		PutAwayHeaderSpecification spec = new PutAwayHeaderSpecification(searchPutAwayHeader);
		Stream<PutAwayHeader> results = putAwayHeaderRepository.stream(spec, PutAwayHeader.class).parallel();
		return results;
	}

	/**
	 * createPutAwayHeader
	 * @param newPutAwayHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PutAwayHeader createPutAwayHeader (AddPutAwayHeader newPutAwayHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader dbPutAwayHeader = new PutAwayHeader();
		log.info("newPutAwayHeader : " + newPutAwayHeader);
		BeanUtils.copyProperties(newPutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(newPutAwayHeader));
		dbPutAwayHeader.setDeletionIndicator(0L);
		dbPutAwayHeader.setCreatedBy(loginUserID);
		dbPutAwayHeader.setUpdatedBy(loginUserID);
		dbPutAwayHeader.setCreatedOn(new Date());
		dbPutAwayHeader.setUpdatedOn(new Date());
		return putAwayHeaderRepository.save(dbPutAwayHeader);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param putAwayNumber
	 * @param proposedStorageBin
	 * @param loginUserID
	 * @param updatePutAwayHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PutAwayHeader updatePutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, 
			String loginUserID, UpdatePutAwayHeader updatePutAwayHeader) 
			throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader dbPutAwayHeader = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, 
				palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
		BeanUtils.copyProperties(updatePutAwayHeader, dbPutAwayHeader, CommonUtils.getNullPropertyNames(updatePutAwayHeader));
		dbPutAwayHeader.setUpdatedBy(loginUserID);
		dbPutAwayHeader.setUpdatedOn(new Date());
		return putAwayHeaderRepository.save(dbPutAwayHeader);
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<PutAwayHeader> putAwayHeaders = getPutAwayHeaders();
		putAwayHeaders.forEach(p -> p.setReferenceField1(asnNumber));
		putAwayHeaderRepository.saveAll(putAwayHeaders);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PutAwayHeader> updatePutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, Long statusId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<PutAwayHeader> dbPutAwayHeaderList = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber);
		List<PutAwayHeader> updatedPutAwayHeaderList = new ArrayList<>();
		for (PutAwayHeader dbPutAwayHeader : dbPutAwayHeaderList) {
			dbPutAwayHeader.setStatusId(statusId);
			dbPutAwayHeader.setUpdatedBy(loginUserID);
			dbPutAwayHeader.setUpdatedOn(new Date());
			updatedPutAwayHeaderList.add(putAwayHeaderRepository.save(dbPutAwayHeader));
		}
		return updatedPutAwayHeaderList;
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @param packBarcodes
	 * @param loginUserID
	 * @return 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public List<PutAwayHeader> updatePutAwayHeader(String refDocNumber, String packBarcodes, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		String warehouseId = null;
		String caseCode = null;
		String palletCode = null;
		String storageBin = null;
		
		/*
		 * Pass WH_ID/REF_DOC_NO/PACK_BARCODE values in PUTAWAYHEADER table and fetch STATUS_ID value and PA_NO
		 * 1. If STATUS_ID=20, then
		 */
		List<PutAwayHeader> putAwayHeaderList = getPutAwayHeader (refDocNumber, packBarcodes);
		List<GrLine> grLineList = grLineService.getGrLine(refDocNumber, packBarcodes);
		
		// Fetching Item Code
		String itemCode = null;
		if (grLineList != null) {
			itemCode = grLineList.get(0).getItemCode();
		}
		
		for (PutAwayHeader dbPutAwayHeader : putAwayHeaderList) {
			warehouseId = dbPutAwayHeader.getWarehouseId();
			palletCode = dbPutAwayHeader.getPalletCode();
			caseCode = dbPutAwayHeader.getCaseCode();
			storageBin = dbPutAwayHeader.getProposedStorageBin();
			
			log.info("dbPutAwayHeader---------> : " + dbPutAwayHeader.getWarehouseId() + "," + refDocNumber + "," + dbPutAwayHeader.getPutAwayNumber());	
			if (dbPutAwayHeader.getStatusId() == 20L) {
				/*
				 * Checking whether Line Items have updated with STATUS_ID = 22.
				 */
				long STATUS_ID_22_COUNT = putAwayLineService.getPutAwayLineByStatusId(warehouseId, dbPutAwayHeader.getPutAwayNumber(), refDocNumber, 22L);
				log.info("putAwayLine---STATUS_ID_22_COUNT---> : " + STATUS_ID_22_COUNT);
				if (STATUS_ID_22_COUNT > 0) {
					throw new BadRequestException("Pallet_ID : " + dbPutAwayHeader.getPalletCode() + " is already reversed.");
				}
				
				/*
				 * Pass WH_ID/REF_DOC_NO/PA_NO values in PUTAWAYLINE table and fetch PA_CNF_QTY values and QTY_TYPE values and 
				 * update Status ID as 22, PA_UTD_BY = USR_ID and PA_UTD_ON=Server time 
				 */
				List<PutAwayLine> putAwayLineList = 
						putAwayLineService.getPutAwayLine(dbPutAwayHeader.getWarehouseId(), refDocNumber, dbPutAwayHeader.getPutAwayNumber());
				log.info("putAwayLineList : " + putAwayLineList);
				for (PutAwayLine dbPutAwayLine : putAwayLineList) {
					log.info("dbPutAwayLine---------> : " + dbPutAwayLine);
							
					itemCode = dbPutAwayLine.getItemCode();
					
					/*
					 * On Successful reversal, update INVENTORY table as below
					 * Pass WH_ID/PACK_BARCODE/ITM_CODE values in Inventory table and delete the records
					 */
					boolean isDeleted = inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode);
					log.info("deleteInventory deleted.." + isDeleted);
					
					if (isDeleted) {
						dbPutAwayLine.setStatusId(22L);
						dbPutAwayLine.setConfirmedBy(loginUserID);
						dbPutAwayLine.setUpdatedBy(loginUserID);
						dbPutAwayLine.setConfirmedOn(new Date());
						dbPutAwayLine.setUpdatedOn(new Date());
						dbPutAwayLine = putAwayLineRepository.save(dbPutAwayLine);
						log.info("dbPutAwayLine updated: " + dbPutAwayLine);
						
						// warehouseId, refDocNumber, preInboundNo, lineNo, itemCode
						InboundLine inboundLine = 
								inboundLineService.getInboundLine(warehouseId, refDocNumber, dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getLineNo(), itemCode);
						inboundLine.setStatusId(22L);
						inboundLine.setUpdatedBy(loginUserID);
						inboundLine.setUpdatedOn(new Date());
						inboundLineRepository.save(inboundLine);
						log.info("InboundLine updated : " + inboundLine);
					}
				
					/*
					 * Pass WH_ID/REF_DOC_NO/IB_LINE_NO/ ITM_CODE values in Inboundline table and update
					 * If QTY_TYPE = A, update ACCEPT_QTY as (ACCEPT_QTY-PA_CNF_QTY) 
					 * if QTY_TYPE= D, update DAMAGE_QTY as (DAMAGE_QTY-PA_CNF_QTY)
					 */
					PutAwayLine dbPutAwayLineForPAConfirmedQty = 
							putAwayLineService.getPutAwayLine(dbPutAwayHeader.getWarehouseId(), refDocNumber, dbPutAwayHeader.getPutAwayNumber(), dbPutAwayHeader.getPackBarcodes());
					log.info("----------dbPutAwayLineForPAConfirmedQty---- > : " + dbPutAwayLineForPAConfirmedQty);	
					
					InboundLine inboundLine = inboundLineService.getInboundLine(dbPutAwayHeader.getWarehouseId(), refDocNumber, dbPutAwayHeader.getPreInboundNo(), 
							dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode());
					if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
						Double acceptedQty = inboundLine.getAcceptedQty() - dbPutAwayLineForPAConfirmedQty.getPutawayConfirmedQty();
						inboundLine.setAcceptedQty(acceptedQty);
					}
					
					if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
						Double damageQty = inboundLine.getDamageQty() - dbPutAwayLineForPAConfirmedQty.getPutawayConfirmedQty();
						inboundLine.setAcceptedQty(damageQty);
					}
					
					if (isDeleted) {
						// Updating InboundLine only if Inventory got deleted
						InboundLine updatedInboundLine = inboundLineRepository.save(inboundLine);
						log.info("updatedInboundLine : " + updatedInboundLine);
					}
				}
			}
			
			/*
			 * 3. For STATUS_ID=19 and 20 , below tables to be updated
			 * Pass the selected REF_DOC_NO/PACK_BARCODE values  and PUTAWAYHEADER tables and update Status ID as 22 and 
			 * PA_UTD_BY = USR_ID and PA_UTD_ON=Server time and fetch CASE_CODE
			 */
			if (dbPutAwayHeader.getStatusId() == 19L) {
				log.info("---#---deleteInventory: " + warehouseId + "," + packBarcodes + "," + itemCode);
				boolean isDeleted = inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode);
				log.info("---#---deleteInventory deleted.." + isDeleted);
				
				if (isDeleted) {
					dbPutAwayHeader.setStatusId(22L);
					dbPutAwayHeader.setUpdatedBy(loginUserID);
					dbPutAwayHeader.setUpdatedOn(new Date());
					PutAwayHeader updatedPutAwayHeader = putAwayHeaderRepository.save(dbPutAwayHeader);
					log.info("updatedPutAwayHeader : " + updatedPutAwayHeader);
				}
			}
		}
		
		// Insert a record into INVENTORYMOVEMENT table as below
		for (GrLine grLine : grLineList) {
			createInventoryMovement(grLine, caseCode, palletCode, storageBin);
		}
		
		return putAwayHeaderList;
	}
	
	/**
	 * 
	 * @param grLine
	 * @param caseCode 
	 * @param storageBin 
	 */
	private void createInventoryMovement(GrLine grLine, String caseCode, String palletCode, String storageBin) {
		InventoryMovement inventoryMovement = new InventoryMovement();
		BeanUtils.copyProperties(grLine, inventoryMovement, CommonUtils.getNullPropertyNames(grLine));
		
		inventoryMovement.setCompanyCodeId(grLine.getCompanyCode());
		
		// CASE_CODE
		inventoryMovement.setCaseCode(caseCode);
				
		// PAL_CODE
		inventoryMovement.setPalletCode(palletCode);
		
		// MVT_TYP_ID	
		inventoryMovement.setMovementType(1L);
		
		// SUB_MVT_TYP_ID
		inventoryMovement.setSubmovementType(3L);
		
		// VAR_ID
		inventoryMovement.setVariantCode(1L);
		
		// VAR_SUB_ID
		inventoryMovement.setVariantSubCode("1");
		
		// STR_MTD
		inventoryMovement.setStorageMethod("1");
		
		// STR_NO
		inventoryMovement.setBatchSerialNumber("1");
		
		// MVT_DOC_NO
		inventoryMovement.setMovementDocumentNo(grLine.getRefDocNumber());
		
		// ST_BIN
		inventoryMovement.setStorageBin(storageBin);
		
		// MVT_QTY
		inventoryMovement.setMovementQty(grLine.getGoodReceiptQty());
		
		// MVT_QTY_VAL
		inventoryMovement.setMovementQtyValue("N");
		
		// BAL_OH_QTY
		// PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
		List<Inventory> inventoryList = inventoryService.getInventory (grLine.getWarehouseId(), grLine.getItemCode(), 1L);
		double sumOfInvQty = inventoryList.stream().mapToDouble(a->a.getInventoryQuantity()).sum();
		inventoryMovement.setBalanceOHQty(sumOfInvQty);
		
		// MVT_UOM
		inventoryMovement.setInventoryUom(grLine.getGrUom());
		
		// PACK_BARCODES
		inventoryMovement.setPackBarcodes(grLine.getPackBarcodes());
		
		// ITEM_CODE
		inventoryMovement.setItemCode(grLine.getItemCode());
		
		// IM_CTD_BY
		inventoryMovement.setCreatedBy(grLine.getCreatedBy());
		
		// IM_CTD_ON
		inventoryMovement.setCreatedOn(grLine.getCreatedOn());
		inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
		log.info("inventoryMovement created: " + inventoryMovement);
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param lineNo
	 * @param itemCode
	 * @param statusId
	 * @param loginUserID
	 */
	public void updatePutAwayHeader(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
		String itemCode, Long statusId, String loginUserID) {
		List<PutAwayHeader> putAwayHeaderList = getPutAwayHeader (warehouseId, preInboundNo, refDocNumber);
		for (PutAwayHeader dbPutAwayHeader : putAwayHeaderList) {
			dbPutAwayHeader.setStatusId(statusId);
			dbPutAwayHeader.setUpdatedBy(loginUserID);
			dbPutAwayHeader.setUpdatedOn(new Date());
			putAwayHeaderRepository.save(dbPutAwayHeader);
		}
		
		// Line
		List<PutAwayLine> putAwayLineList = 
				putAwayLineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, lineNo, itemCode);
		for (PutAwayLine dbPutAwayLine : putAwayLineList) {
			dbPutAwayLine.setStatusId(statusId);
			dbPutAwayLine.setUpdatedBy(loginUserID);
			dbPutAwayLine.setUpdatedOn(new Date());
			putAwayLineRepository.save(dbPutAwayLine);
		}
		log.info("PutAwayHeader & Line updated..");
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param putAwayNumber
	 * @param proposedStorageBin
	 * @param loginUserID
	 */
	public void deletePutAwayHeader (String warehouseId, String preInboundNo, String refDocNumber, String goodsReceiptNo, 
			String palletCode, String caseCode, String packBarcodes, String putAwayNumber, String proposedStorageBin, String loginUserID) {
		PutAwayHeader putAwayHeader = getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, 
				palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin);
		if ( putAwayHeader != null) {
			putAwayHeader.setDeletionIndicator(1L);
			putAwayHeader.setUpdatedBy(loginUserID);
			putAwayHeaderRepository.save(putAwayHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + putAwayNumber);
		}
	}
}

package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.model.auditlog.AddAuditLog;
import com.tekclover.wms.api.masters.model.auditlog.AuditLog;
import com.tekclover.wms.api.masters.model.impartner.ImPartnerInput;
import com.tekclover.wms.api.masters.model.impartner.SearchImPartner;
import com.tekclover.wms.api.masters.repository.specification.ImPartnerSpecification;
import com.tekclover.wms.api.masters.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.impartner.AddImPartner;
import com.tekclover.wms.api.masters.model.impartner.ImPartner;
import com.tekclover.wms.api.masters.repository.ImPartnerRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImPartnerService {

	@Autowired
	private ImPartnerRepository impartnerRepository;

	@Autowired
	private AuditLogService auditLogService;

	/**
	 *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param itemCode
	 * @return
	 */
	public List<ImPartner> getImPartner (String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String partnerItemBarcode ) {
		List<ImPartner> impartner =
				impartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						languageId,
						itemCode,
						manufacturerName,
						partnerItemBarcode,
						0L);
		if(impartner.isEmpty()) {
			throw new BadRequestException("The given values: " +
					" plantId " + plantId +
					" itemCode " + itemCode +
					" manufacturerName " + manufacturerName +
					" warehouse " + warehouseId +
					" language Id " + languageId +
					" companyCodeId " + companyCodeId +" doesn't exist.");
		}
		return impartner;
	}

	public List<ImPartner> getImPartnerV2 (ImPartnerInput imPartnerInput) {
		List<ImPartner> impartner =
				impartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
						imPartnerInput.getCompanyCodeId(),
						imPartnerInput.getPlantId(),
						imPartnerInput.getWarehouseId(),
						imPartnerInput.getLanguageId(),
						imPartnerInput.getItemCode(),
						imPartnerInput.getManufacturerName(),
						imPartnerInput.getPartnerItemBarcode(),
						0L);
		if(impartner.isEmpty()) {
			throw new BadRequestException("The given values: " +
					" plantId " + imPartnerInput.getPlantId() +
					" itemCode " + imPartnerInput.getItemCode() +
					" manufacturerName " + imPartnerInput.getManufacturerName() +
					" warehouse " + imPartnerInput.getWarehouseId() +
					" language Id " + imPartnerInput.getLanguageId() +
					" companyCodeId " + imPartnerInput.getCompanyCodeId() +" doesn't exist.");
		}
		return impartner;
	}

	/**
	 *
	 * @param searchImPartner
	 * @return
	 * @throws ParseException
	 */
	public List<ImPartner> findImPartnerV8(SearchImPartner searchImPartner) throws ParseException {
		log.info("SearchImpartner Input: " + searchImPartner);
		ImPartnerSpecification spec = new ImPartnerSpecification(searchImPartner);
		List<ImPartner> results = impartnerRepository.findAll(spec);
		log.info("results: " + results.size());
		return results;
	}

	/**
	 *
	 * @param newImPartner
	 * @param loginUserID
	 * @return
	 */
	public List<ImPartner> createImPartner (List<AddImPartner> newImPartner, String loginUserID) {
		log.info("Impartner Input: " + newImPartner);
		try {
			List<ImPartner>imPartnerList=new ArrayList<>();

			for(AddImPartner addImPartner:newImPartner) {

				if(addImPartner.getPartnerItemBarcode().equalsIgnoreCase("")){
					throw new BadRequestException("Barcode is Empty");
				}
				//Duplicate Barcode Validation
				List<ImPartner> duplicateBarcodeCheck = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
						addImPartner.getCompanyCodeId(), addImPartner.getPlantId(), addImPartner.getLanguageId(), addImPartner.getWarehouseId(),
						addImPartner.getPartnerItemBarcode(), 0L);
				log.info("Duplicate BarcodeId : " + duplicateBarcodeCheck);
				if(duplicateBarcodeCheck != null && !duplicateBarcodeCheck.isEmpty()) {
					for(ImPartner dbImPartner : duplicateBarcodeCheck) {
						String dbItemCode = dbImPartner.getItemCode();
						String dbManufacturerName = dbImPartner.getManufacturerName();
						String dbItmMfrName = dbItemCode+dbManufacturerName;
						String newItemCode = addImPartner.getItemCode();
						String newManufacturerName = addImPartner.getManufacturerName();
						String newItmMfrName = newItemCode+newManufacturerName;
						log.info("dbItmMfrName, newItmMfrName : " + dbItmMfrName + ", " + newItmMfrName);
						if(!dbItmMfrName.equalsIgnoreCase(newItmMfrName)) {
							throw new BadRequestException("BarcodeId already exist: " + addImPartner.getPartnerItemBarcode());
						}
					}
				}

				List<ImPartner> existingBarcodeId = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
						addImPartner.getCompanyCodeId(), addImPartner.getPlantId(), addImPartner.getLanguageId(), addImPartner.getWarehouseId(),
						addImPartner.getItemCode(), addImPartner.getManufacturerName(), addImPartner.getPartnerItemBarcode(), 0L);
				log.info("Existing BarcodeId : " + existingBarcodeId);

				if(existingBarcodeId != null && !existingBarcodeId.isEmpty()) {
					return existingBarcodeId;
				}

				ImPartner imPartner = new ImPartner();
				BeanUtils.copyProperties(addImPartner, imPartner, CommonUtils.getNullPropertyNames(addImPartner));
				imPartner.setDeletionIndicator(0L);
				imPartner.setCreatedBy(loginUserID);
				imPartner.setUpdatedBy(loginUserID);
				imPartner.setCreatedOn(new Date());
				imPartner.setUpdatedOn(new Date());
				ImPartner savedImPartner = impartnerRepository.save(imPartner);
				log.info("Impartner Created: " + savedImPartner);
				imPartnerList.add(savedImPartner);
			}
			return imPartnerList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param itemCode
	 * @param updateImPartner
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
//	 */

//	public List<ImPartner> updateImPartner (String companyCodeId, String plantId, String languageId, String warehouseId,
//											String itemCode, String manufacturerName, List<AddImPartner> updateImPartner,
//											String loginUserID)
//			throws IllegalAccessException, InvocationTargetException, ParseException {
//
//		List<ImPartner> dbImPartner =
//				impartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
//						companyCodeId, plantId, warehouseId,languageId, itemCode, manufacturerName,0L);
//
//		if(dbImPartner!=null) {
//			for (ImPartner newImPartner : dbImPartner) {
//				newImPartner.setUpdatedBy(loginUserID);
//				newImPartner.setUpdatedOn((new Date()));
//				newImPartner.setDeletionIndicator(1L);
//				impartnerRepository.save(newImPartner);
//			}
//		}
//	 else {
//		throw new EntityNotFoundException("The Given Values of companyId " + companyCodeId +
//				" plantId " + plantId +
//				" manufacturerName " + manufacturerName +
//				" warehouseId " + warehouseId +
//				" languageId " + languageId +
//				" itemCode " + itemCode + "doesn't exists");
//	}
//
//	 List<ImPartner>createImPartner=createImPartner(updateImPartner,loginUserID);
//	 return createImPartner;
//	}
public List<ImPartner> updateImPartner (String companyCodeId, String plantId, String languageId, String warehouseId,
										String itemCode, String manufacturerName, List<AddImPartner> updateImPartner,
										String loginUserID) throws ParseException, InvocationTargetException, IllegalAccessException {

			List<ImPartner> updatedImpartnerList = new ArrayList<>();
			for (AddImPartner newImPartner : updateImPartner) {
				ImPartner dbImpartner = impartnerRepository.findByBusinessPartnerCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndBusinessPartnerTypeAndPartnerItemBarcodeAndManufacturerNameAndDeletionIndicator(
						newImPartner.getBusinessPartnerCode(), companyCodeId, plantId, warehouseId, languageId, newImPartner.getItemCode(),
						newImPartner.getBusinessPartnerType(), newImPartner.getOldPartnerItemBarcode(), newImPartner.getManufacturerName(), 0L);

				//Duplicate Barcode Validation
				List<ImPartner> duplicateBarcodeCheck = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
						companyCodeId, plantId, languageId, warehouseId, newImPartner.getPartnerItemBarcode(), 0L);
				log.info("Duplicate BarcodeId : " + duplicateBarcodeCheck);
				if(duplicateBarcodeCheck != null && !duplicateBarcodeCheck.isEmpty()) {
					for(ImPartner dbImPartner : duplicateBarcodeCheck) {
						String dbItemCode = dbImPartner.getItemCode();
						String dbManufacturerName = dbImPartner.getManufacturerName();
						String dbItmMfrName = dbItemCode+dbManufacturerName;
						String newItemCode = newImPartner.getItemCode();
						String newManufacturerName = newImPartner.getManufacturerName();
						String newItmMfrName = newItemCode+newManufacturerName;
						log.info("dbItmMfrName, newItmMfrName : " + dbItmMfrName + ", " + newItmMfrName);
						if(!dbItmMfrName.equalsIgnoreCase(newItmMfrName)) {
							throw new BadRequestException("BarcodeId already exist: " + newImPartner.getPartnerItemBarcode());
						}
					}
				}

				if (dbImpartner != null) {
					//AuditLog
					createAuditLogRecord(companyCodeId, plantId, languageId, warehouseId, loginUserID, "tblimpartner",
							"Impartner", "partner_itm_bar", newImPartner.getOldPartnerItemBarcode(),
							newImPartner.getPartnerItemBarcode(), newImPartner.getItemCode(),
							newImPartner.getManufacturerName(), newImPartner.getOldPartnerItemBarcode());
					//delete Record
					impartnerRepository.delete(dbImpartner);
					log.info("Impartner Updated: " + dbImpartner);
				}
					ImPartner imPartner = new ImPartner();
					BeanUtils.copyProperties(newImPartner, imPartner, CommonUtils.getNullPropertyNames(newImPartner));
					imPartner.setDeletionIndicator(0L);
					imPartner.setCreatedBy(loginUserID);
					imPartner.setUpdatedBy(loginUserID);
					imPartner.setCreatedOn(new Date());
					imPartner.setUpdatedOn(new Date());
					impartnerRepository.save(imPartner);
					updatedImpartnerList.add(imPartner);
					log.info("Created Impartner: " + imPartner);

			}
	 	return updatedImpartnerList;
	}

	/**
	 *
	 * @param updateImPartner
	 * @param loginUserID
	 * @return
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<ImPartner> updateImPartnerV2 (List<AddImPartner> updateImPartner, String loginUserID) throws ParseException, InvocationTargetException, IllegalAccessException {

			List<ImPartner> updatedImpartnerList = new ArrayList<>();
			for (AddImPartner newImPartner : updateImPartner) {
				ImPartner dbImpartner = impartnerRepository.findByBusinessPartnerCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndBusinessPartnerTypeAndPartnerItemBarcodeAndManufacturerNameAndDeletionIndicator(
						newImPartner.getBusinessPartnerCode(), newImPartner.getCompanyCodeId(), newImPartner.getPlantId(),
						newImPartner.getWarehouseId(), newImPartner.getLanguageId(), newImPartner.getItemCode(),
						newImPartner.getBusinessPartnerType(), newImPartner.getOldPartnerItemBarcode(), newImPartner.getManufacturerName(), 0L);

				//Duplicate Barcode Validation
				List<ImPartner> duplicateBarcodeCheck = impartnerRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
						newImPartner.getCompanyCodeId(), newImPartner.getPlantId(), newImPartner.getLanguageId(), newImPartner.getWarehouseId(), newImPartner.getPartnerItemBarcode(), 0L);
				log.info("Duplicate BarcodeId : " + duplicateBarcodeCheck);
				if(duplicateBarcodeCheck != null && !duplicateBarcodeCheck.isEmpty()) {
					for(ImPartner dbImPartner : duplicateBarcodeCheck) {
						String dbItemCode = dbImPartner.getItemCode();
						String dbManufacturerName = dbImPartner.getManufacturerName();
						String dbItmMfrName = dbItemCode+dbManufacturerName;
						String newItemCode = newImPartner.getItemCode();
						String newManufacturerName = newImPartner.getManufacturerName();
						String newItmMfrName = newItemCode+newManufacturerName;
						log.info("dbItmMfrName, newItmMfrName : " + dbItmMfrName + ", " + newItmMfrName);
						if(!dbItmMfrName.equalsIgnoreCase(newItmMfrName)) {
							throw new BadRequestException("BarcodeId already exist: " + newImPartner.getPartnerItemBarcode());
						}
					}
				}

				if (dbImpartner != null) {
					//AuditLog
					createAuditLogRecord(newImPartner.getCompanyCodeId(), newImPartner.getPlantId(), newImPartner.getLanguageId(),
							newImPartner.getWarehouseId(), loginUserID, "tblimpartner",
							"Impartner", "partner_itm_bar", newImPartner.getOldPartnerItemBarcode(),
							newImPartner.getPartnerItemBarcode(), newImPartner.getItemCode(),
							newImPartner.getManufacturerName(), newImPartner.getOldPartnerItemBarcode());
					//delete Record
					impartnerRepository.delete(dbImpartner);
					log.info("Impartner Updated: " + dbImpartner);
				}
					ImPartner imPartner = new ImPartner();
					BeanUtils.copyProperties(newImPartner, imPartner, CommonUtils.getNullPropertyNames(newImPartner));
					imPartner.setDeletionIndicator(0L);
					imPartner.setCreatedBy(loginUserID);
					imPartner.setUpdatedBy(loginUserID);
					imPartner.setCreatedOn(new Date());
					imPartner.setUpdatedOn(new Date());
					impartnerRepository.save(imPartner);
					updatedImpartnerList.add(imPartner);
					log.info("Created Impartner: " + imPartner);

			}
	 	return updatedImpartnerList;
	}


	/**
	 *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param itemCode
	 * @param loginUserID
	 */
	public void deleteImPartner (String companyCodeId, String plantId, String languageId, String warehouseId,
									 String itemCode, String manufacturerName, String partnerItemBarcode, String loginUserID) throws ParseException, InvocationTargetException, IllegalAccessException {

		List<ImPartner> impartner =
				impartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						languageId,
						itemCode,
						manufacturerName,
						partnerItemBarcode,
						0L);

		if ( impartner != null) {
			for(ImPartner dbImpartner:impartner) {
				createAuditLogRecord(companyCodeId, plantId, languageId, warehouseId, loginUserID, "tblimpartner",
						"Impartner", "partner_itm_bar",partnerItemBarcode,"Deleted", itemCode, manufacturerName, partnerItemBarcode);
				impartnerRepository.delete(dbImpartner);
//				dbImpartner.setDeletionIndicator(1L);
//				dbImpartner.setUpdatedBy(loginUserID);
//				dbImpartner.setUpdatedOn(new Date());
//				impartnerRepository.save(dbImpartner);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + itemCode);
		}
	}

	/**
	 *
	 * @param imPartnerInputList
	 * @param loginUserID
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void deleteImPartnerV2 (List<ImPartnerInput> imPartnerInputList, String loginUserID) throws ParseException, InvocationTargetException, IllegalAccessException {

		if(imPartnerInputList != null && !imPartnerInputList.isEmpty()) {
			for (ImPartnerInput dbimPartner : imPartnerInputList) {
				List<ImPartner> impartner =
						impartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
								dbimPartner.getCompanyCodeId(),
								dbimPartner.getPlantId(),
								dbimPartner.getWarehouseId(),
								dbimPartner.getLanguageId(),
								dbimPartner.getItemCode(),
								dbimPartner.getManufacturerName(),
								dbimPartner.getPartnerItemBarcode(),
								0L);

				if (impartner != null) {
					for (ImPartner dbImpartner : impartner) {
						createAuditLogRecord(dbimPartner.getCompanyCodeId(), dbimPartner.getPlantId(), dbimPartner.getLanguageId(), dbimPartner.getWarehouseId(), loginUserID, "tblimpartner",
								"Impartner", "partner_itm_bar", dbimPartner.getPartnerItemBarcode(), "Deleted", dbimPartner.getItemCode(), dbimPartner.getManufacturerName(), dbimPartner.getPartnerItemBarcode());
						impartnerRepository.delete(dbImpartner);
					}
				} else {
					throw new EntityNotFoundException("Error in deleting Id:" + dbimPartner.getItemCode());
				}
			}
		}
	}

	public void createAuditLogRecord(String companyCodeId, String plantId, String languageId, String warehouseId,
									 String loginUserID, String tableName, String objectName,
									 String modifiedField, String oldValue, String newValue,
									 String itemCode, String manufacturerName, String partnerItemBarcode)
			throws InvocationTargetException, IllegalAccessException, ParseException {

		AddAuditLog auditLog = new AddAuditLog();

		auditLog.setCompanyCodeId(companyCodeId);

		auditLog.setPlantId(plantId);

		auditLog.setWarehouseId(warehouseId);
		auditLog.setLanguageId(languageId);

		auditLog.setFinancialYear(DateUtils.getCurrentYear());

		auditLog.setObjectName(objectName);

		auditLog.setModifiedTableName(tableName);

		// MOD_FIELD
		auditLog.setModifiedField(modifiedField);

		// OLD_VL
		auditLog.setOldValue(oldValue);

		// NEW_VL
		auditLog.setNewValue(newValue);

		// CTD_BY
		auditLog.setCreatedBy(loginUserID);

		auditLog.setReferenceField1(itemCode);
		auditLog.setReferenceField2(manufacturerName);
		auditLog.setReferenceField3(partnerItemBarcode);
		auditLog.setReferenceField10("MasterService");

		auditLogService.createAuditLog(auditLog, loginUserID);
	}

}

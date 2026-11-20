package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.model.impacking.SearchImPacking;
import com.tekclover.wms.api.masters.repository.specification.ImPackingSpecification;
import com.tekclover.wms.api.masters.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.impacking.AddImPacking;
import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import com.tekclover.wms.api.masters.model.impacking.UpdateImPacking;
import com.tekclover.wms.api.masters.repository.ImPackingRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImPackingService {

	@Autowired
	private ImPackingRepository impackingRepository;

	/**
	 * getImPackings
	 * @return
	 */
	public List<ImPacking> getImPackings () {
		List<ImPacking> impackingList = impackingRepository.findAll();
		log.info("impackingList : " + impackingList);
		impackingList = impackingList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return impackingList;
	}

	/**
	 * getImPacking
	 * @param packingMaterialNo
	 * @return
	 */
	public ImPacking getImPacking (String packingMaterialNo,String companyCodeId,String plantId,String languageId,String warehouseId,String itemCode) {
		Optional<ImPacking> impacking = impackingRepository.findByPackingMaterialNoAndCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
				packingMaterialNo,
				companyCodeId,
				languageId,
				plantId,
				warehouseId,
				itemCode,
				0L);
		if(impacking.isEmpty()) {
			throw new BadRequestException("The given values :" +
					"packingMaterialNo"+packingMaterialNo +
					"companyCodeId"+companyCodeId+
					"plantId"+plantId+
					"itemCode"+itemCode+"doesn't exist.");
		}
		return impacking.get();
	}

	/**
	 * findImPacking
	 * @param searchImPacking
	 * @return
	 * @throws ParseException
	 */
	public List<ImPacking> findImPacking(SearchImPacking searchImPacking) throws ParseException {

		ImPackingSpecification spec = new ImPackingSpecification(searchImPacking);
		List<ImPacking> results = impackingRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

	/**
	 * createImPacking
	 * @param newImPacking
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPacking createImPacking (AddImPacking newImPacking, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ImPacking dbImPacking = new ImPacking();
		Optional<ImPacking> duplicateImPacking = impackingRepository.findByPackingMaterialNoAndCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(newImPacking.getPackingMaterialNo(), newImPacking.getCompanyCodeId(), newImPacking.getLanguageId(), newImPacking.getPlantId(), newImPacking.getWarehouseId(), newImPacking.getItemCode(), 0L);
		if (!duplicateImPacking.isEmpty()) {
			throw new BadRequestException("Record is Getting Duplicate");
		} else {
			BeanUtils.copyProperties(newImPacking, dbImPacking, CommonUtils.getNullPropertyNames(newImPacking));
			dbImPacking.setDeletionIndicator(0L);
			dbImPacking.setCreatedBy(loginUserID);
			dbImPacking.setUpdatedBy(loginUserID);
			dbImPacking.setCreatedOn(new Date());
			dbImPacking.setUpdatedOn(new Date());
			return impackingRepository.save(dbImPacking);
		}
	}

	/**
	 * updateImPacking
	 * @param packingMaterialNo
	 * @param updateImPacking
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPacking updateImPacking (String packingMaterialNo,String companyCodeId,String plantId,String languageId,String warehouseId,String itemCode,UpdateImPacking updateImPacking, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ImPacking dbImPacking = getImPacking(packingMaterialNo,companyCodeId,plantId,languageId,warehouseId,itemCode);
		BeanUtils.copyProperties(updateImPacking, dbImPacking, CommonUtils.getNullPropertyNames(updateImPacking));
		dbImPacking.setUpdatedBy(loginUserID);
		dbImPacking.setUpdatedOn(new Date());
		return impackingRepository.save(dbImPacking);
	}

	/**
	 * deleteImPacking
	 * @param packingMaterialNo
	 */
	public void deleteImPacking (String packingMaterialNo,String companyCodeId,String plantId,String languageId,String warehouseId,String itemCode,String loginUserID) throws ParseException {
		ImPacking impacking = getImPacking(packingMaterialNo,companyCodeId,plantId,languageId,warehouseId,itemCode);
		if ( impacking != null) {
			impacking.setDeletionIndicator (1L);
			impacking.setUpdatedBy(loginUserID);
			impacking.setUpdatedOn(new Date());
			impackingRepository.save(impacking);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + packingMaterialNo);
		}
	}
}

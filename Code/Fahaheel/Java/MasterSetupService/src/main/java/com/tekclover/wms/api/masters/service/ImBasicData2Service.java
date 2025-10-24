package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.model.imbasicdata2.SearchImBasicData2;
import com.tekclover.wms.api.masters.repository.specification.ImBasicData2Specification;
import com.tekclover.wms.api.masters.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imbasicdata2.AddImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.UpdateImBasicData2;
import com.tekclover.wms.api.masters.repository.ImBasicData2Repository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImBasicData2Service {

	@Autowired
	private ImBasicData2Repository imbasicdata2Repository;


	/**
	 * getImBasicData2
	 * @param itemCode
	 * @return
	 */
	public ImBasicData2 getImBasicData2 (String itemCode,String companyCodeId,String plantId,String warehouseId,String languageId) {
		Optional<ImBasicData2> imbasicdata2 = imbasicdata2Repository.findByItemCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
				itemCode,
				companyCodeId,
				plantId,
				warehouseId,
				languageId,
				0L);
		if(imbasicdata2.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"itemCode"+itemCode+
					"companyCodeId"+companyCodeId+
					"plantId"+plantId+
					"doesn't exist.");
		}
		return imbasicdata2.get();
	}

	/**
	 * findImBasicData2
	 * @param searchImBasicData2
	 * @return
	 * @throws ParseException
	 */
	public List<ImBasicData2> findImBasicData2(SearchImBasicData2 searchImBasicData2) throws ParseException {

		ImBasicData2Specification spec = new ImBasicData2Specification(searchImBasicData2);
		List<ImBasicData2> results = imbasicdata2Repository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}

	/**
	 * createImBasicData2
	 * @param newImBasicData2
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData2 createImBasicData2 (AddImBasicData2 newImBasicData2, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ImBasicData2 dbImBasicData2 = new ImBasicData2();
		Optional<ImBasicData2> duplicateImBasicData2 = imbasicdata2Repository.findByItemCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(newImBasicData2.getItemCode(), newImBasicData2.getCompanyCodeId(), newImBasicData2.getPlantId(), newImBasicData2.getWarehouseId(), newImBasicData2.getLanguageId(), 0L);
		if (!duplicateImBasicData2.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			BeanUtils.copyProperties(newImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(newImBasicData2));
			dbImBasicData2.setDeletionIndicator(0L);
			dbImBasicData2.setCreatedBy(loginUserID);
			dbImBasicData2.setUpdatedBy(loginUserID);
			dbImBasicData2.setCreatedOn(new Date());
			dbImBasicData2.setUpdatedOn(new Date());
			return imbasicdata2Repository.save(dbImBasicData2);
		}
	}

	/**
	 *
	 * @param itemCode
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param languageId
	 * @param updateImBasicData2
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData2 updateImBasicData2 (String itemCode,String companyCodeId,String plantId,String warehouseId,String languageId, UpdateImBasicData2 updateImBasicData2, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ImBasicData2 dbImBasicData2 = getImBasicData2(itemCode,companyCodeId,plantId,warehouseId,languageId);
		BeanUtils.copyProperties(updateImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(updateImBasicData2));
		dbImBasicData2.setUpdatedBy(loginUserID);
		dbImBasicData2.setUpdatedOn(new Date());
		return imbasicdata2Repository.save(dbImBasicData2);
	}

	/**
	 * deleteImBasicData2
//	 * @param imbasicdata2
	 */
	public void deleteImBasicData2 (String itemCode,String companyCodeId,String plantId,String warehouseId,String languageId,String loginUserID) throws ParseException {
		ImBasicData2 imbasicdata2 = getImBasicData2(itemCode,companyCodeId,plantId,warehouseId,languageId);
		if ( imbasicdata2 != null) {
			imbasicdata2.setDeletionIndicator (1L);
			imbasicdata2.setUpdatedBy(loginUserID);
			imbasicdata2.setUpdatedOn(new Date());
			imbasicdata2Repository.save(imbasicdata2);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + itemCode);
		}
	}
}

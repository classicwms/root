package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.model.imalternateuom.SearchImAlternateUom;
import com.tekclover.wms.api.masters.repository.specification.ImAlternateUomSpecification;
import com.tekclover.wms.api.masters.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imalternateuom.AddImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.UpdateImAlternateUom;
import com.tekclover.wms.api.masters.repository.ImAlternateUomRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImAlternateUomService {

	@Autowired
	private ImAlternateUomRepository imalternateuomRepository;

	/**
	 * getImAlternateUoms
	 * @return
	 */
	public List<ImAlternateUom> getImAlternateUoms () {
		List<ImAlternateUom> imalternateuomList = imalternateuomRepository.findAll();
//		log.info("imalternateuomList : " + imalternateuomList);
		imalternateuomList = imalternateuomList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return imalternateuomList;
	}

	/**
	 * getImAlternateUom
	 * @param alternateUom
	 * @return
	 */
	public List<ImAlternateUom> getImAlternateUom (String alternateUom,String companyCodeId,String plantId,String warehouseId,String itemCode,String uomId,String languageId) {
		List<ImAlternateUom> imalternateuom = imalternateuomRepository.
				findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
						alternateUom,
						companyCodeId,
						plantId,
						warehouseId,
						itemCode,
						uomId,
						languageId,
						0L);
		if(imalternateuom.isEmpty()) {
			throw new BadRequestException("The given values:" +
					"imAlternateUom " + alternateUom +
					"itemCode"+itemCode+
					"plantId"+plantId+
					"companyCodeId"+companyCodeId+" doesn't exist.");
		}
		return imalternateuom;
	}

	/**
	 * findImAlternateUom
	 * @param searchImAlternateUom
	 * @return
	 * @throws ParseException
	 */
	public List<ImAlternateUom> findImAlternateUom(SearchImAlternateUom searchImAlternateUom) throws ParseException {
		ImAlternateUomSpecification spec = new ImAlternateUomSpecification(searchImAlternateUom);
		List<ImAlternateUom> results = imalternateuomRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}

	/**
	 * createImAlternateUom
	 * @param newImAlternateUom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ImAlternateUom> createImAlternateUom (List<AddImAlternateUom> newImAlternateUom, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		List<ImAlternateUom> dbImAlternateUomList = new ArrayList<>();

//		String uomId = imalternateuomRepository.getUomId();

		for (AddImAlternateUom newAlternateUom : newImAlternateUom) {

			List<ImAlternateUom> duplicateImAlternateUom = imalternateuomRepository.
					findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
							newAlternateUom.getAlternateUom(), newAlternateUom.getCompanyCodeId(),
							newAlternateUom.getPlantId(), newAlternateUom.getWarehouseId(),
							newAlternateUom.getItemCode(), newAlternateUom.getUomId(),
							newAlternateUom.getLanguageId(), 0L);

			if (!duplicateImAlternateUom.isEmpty()) {

				throw new EntityNotFoundException("Record is Getting Duplicated");

			} else {

				ImAlternateUom dbImAlternateUom = new ImAlternateUom();
				Long id = imalternateuomRepository.getId();

				BeanUtils.copyProperties(newAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newAlternateUom));

//				if(uomId != null) {
//
//					dbImAlternateUom.setUomId(uomId);
//
//				}else {
//
//					dbImAlternateUom.setUomId("1");
//				}
				if(id != null) {

					dbImAlternateUom.setId(id);

				}else {

					dbImAlternateUom.setId(1L);
				}
				dbImAlternateUom.setDeletionIndicator(0L);
				dbImAlternateUom.setCreatedBy(loginUserID);
				dbImAlternateUom.setUpdatedBy(loginUserID);
				dbImAlternateUom.setCreatedOn(new Date());
				dbImAlternateUom.setUpdatedOn(new Date());
				ImAlternateUom savedImAlternate = imalternateuomRepository.save(dbImAlternateUom);
				dbImAlternateUomList.add(savedImAlternate);
			}
		}
		return dbImAlternateUomList;
	}

//	}	 */
//	public List<ImAlternateUom> createImAlternateUom (List<AddImAlternateUom> newImAlternateUom, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//			Optional<ImAlternateUom> duplicateImAlternateUom = imalternateuomRepository.findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(newImAlternateUom.getAlternateUom(), newImAlternateUom.getCompanyCodeId(), newImAlternateUom.getPlantId(), newImAlternateUom.getWarehouseId(), newImAlternateUom.getItemCode(), newImAlternateUom.getUomId(), newImAlternateUom.getLanguageId(), 0L);
//			ImAlternateUom imAlternateUom=new ImAlternateUom();
//			if (!duplicateImAlternateUom.isEmpty()) {
//				throw new EntityNotFoundException("Record is Getting Duplicated");
//			} else {
//				ImAlternateUom dbImAlternateUom=new ImAlternateUom();
//				BeanUtils.copyProperties(newImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newImAlternateUom));
//				dbImAlternateUom.setDeletionIndicator(0L);
//				dbImAlternateUom.setCreatedBy(loginUserID);
//				dbImAlternateUom.setUpdatedBy(loginUserID);
//				dbImAlternateUom.setCreatedOn(new Date());
//				dbImAlternateUom.setUpdatedOn(new Date());
//				//return imalternateuomRepository.save(dbImAlternateUom);
////				ImAlternateUom savedImAlternateUom=imalternateuomRepository.save(dbImAlternateUom);
//
//			}
//
//
//	}

	/**
	 * updateImAlternateUom
	 * @param alternateUom
	 * @param updateImAlternateUom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ImAlternateUom> updateImAlternateUom (String alternateUom,String companyCodeId,String plantId,
													  String warehouseId,String itemCode,String uomId,String languageId,
													  List<UpdateImAlternateUom> updateImAlternateUom, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<ImAlternateUom> dbImAlternateUomList = new ArrayList<>();

		for (UpdateImAlternateUom newUpdateImAlternateUom : updateImAlternateUom) {

			if(newUpdateImAlternateUom.getId() != null) {
				ImAlternateUom dbImAlternateUom = imalternateuomRepository.
						findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndIdAndDeletionIndicator(
								newUpdateImAlternateUom.getAlternateUom(), companyCodeId, plantId,
								warehouseId, itemCode, newUpdateImAlternateUom.getUomId(),
								languageId, newUpdateImAlternateUom.getId(), 0L);


				if(dbImAlternateUom != null) {

					BeanUtils.copyProperties(newUpdateImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newUpdateImAlternateUom));
					dbImAlternateUom.setUpdatedBy(loginUserID);
					dbImAlternateUom.setUpdatedOn(new Date());
					dbImAlternateUomList.add(imalternateuomRepository.save(dbImAlternateUom));

				}
			} else {
				Long id = imalternateuomRepository.getId();
				ImAlternateUom newImAlternateUom = new ImAlternateUom();

				List<ImAlternateUom> duplicateImAlternateUom = imalternateuomRepository.
						findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
								newUpdateImAlternateUom.getAlternateUom(), newUpdateImAlternateUom.getCompanyCodeId(),
								newUpdateImAlternateUom.getPlantId(), newUpdateImAlternateUom.getWarehouseId(),
								newUpdateImAlternateUom.getItemCode(), newUpdateImAlternateUom.getUomId(),
								newUpdateImAlternateUom.getLanguageId(), 0L);

				if (!duplicateImAlternateUom.isEmpty()) {

					throw new EntityNotFoundException("Record is Getting Duplicated");

				} else {

					BeanUtils.copyProperties(newUpdateImAlternateUom, newImAlternateUom, CommonUtils.getNullPropertyNames(newUpdateImAlternateUom));

					newImAlternateUom.setUomId(uomId);
					newImAlternateUom.setId(id);
					newImAlternateUom.setDeletionIndicator(0L);
					newImAlternateUom.setCreatedBy(loginUserID);
					newImAlternateUom.setUpdatedBy(loginUserID);
					newImAlternateUom.setCreatedOn(new Date());
					newImAlternateUom.setUpdatedOn(new Date());

					dbImAlternateUomList.add(imalternateuomRepository.save(newImAlternateUom));
				}
			}
		}
		return dbImAlternateUomList;
	}

	/**
	 //		 * updateImAlternateUom
	 //		 * @param alternateUom
	 //		 * @param updateImAlternateUom
	 //		 * @return
	 //		 * @throws IllegalAccessException
	 //		 * @throws InvocationTargetException
	 //		 */
//		public ImAlternateUom updateImAlternateUom (String alternateUom,String companyCodeId,String plantId,String warehouseId,String itemCode,String uomId,String languageId, UpdateImAlternateUom updateImAlternateUom, String loginUserID)
//            throws IllegalAccessException, InvocationTargetException {
//				ImAlternateUom dbImAlternateUom = getImAlternateUom(alternateUom, companyCodeId, plantId, warehouseId, itemCode, uomId, languageId);
//				BeanUtils.copyProperties(updateImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(updateImAlternateUom));
//				dbImAlternateUom.setUpdatedBy(loginUserID);
//				dbImAlternateUom.setUpdatedOn(new Date());
//				return imalternateuomRepository.save(dbImAlternateUom);
//		}

	/**
	 * deleteImAlternateUom
	 * @param alternateUom
	 */
	public void deleteImAlternateUom (String alternateUom,String companyCodeId,String plantId,String warehouseId,String itemCode,String uomId,String languageId, String loginUserID) throws ParseException {
		List<ImAlternateUom> imalternateuom = getImAlternateUom(alternateUom,companyCodeId,plantId,
				warehouseId,itemCode,uomId,languageId);
		if ( imalternateuom != null) {
			for (ImAlternateUom dbImAlternateUom : imalternateuom) {
				dbImAlternateUom.setDeletionIndicator(1L);
				dbImAlternateUom.setUpdatedBy(loginUserID);
				dbImAlternateUom.setUpdatedOn(new Date());
				imalternateuomRepository.save(dbImAlternateUom);
			}
		} else{
			throw new EntityNotFoundException("Error in deleting Id:" + uomId);
		}
	}
}

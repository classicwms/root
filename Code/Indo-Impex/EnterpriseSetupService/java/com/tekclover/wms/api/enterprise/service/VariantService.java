package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerial;
import com.tekclover.wms.api.enterprise.model.variant.*;
import com.tekclover.wms.api.enterprise.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.repository.specification.VariantSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VariantService {
	@Autowired
	private BatchSerialRepository batchSerialRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

//	@Autowired
//	private LevelReferenceVariantService levelReferenceVariantService;

//	@Autowired
//	private LevelReferenceVariantRepository levelReferenceVariantRepository;
@Autowired
private VariantRepository variantRepository;

	/**
	 * getVariants
	 *
	 * @return
	 */
	public List<Variant> getVariants() {
		List<Variant> variantList = variantRepository.findAll();
		log.info("variantList : " + variantList);
		variantList = variantList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());

		String companyCodeId = null;
		String plantId = null;
		String warehouseId = null;
		String variantId = null;
		Long levelId = null;
		IkeyValuePair ikeyValuePair = null;

		List<Variant> newVariantList =new ArrayList<>();

		for(Variant dbVariant:variantList) {

			Variant newVariantOutput = new Variant();

			if(companyCodeId != dbVariant.getCompanyId() && plantId != dbVariant.getPlantId() &&
					warehouseId != dbVariant.getWarehouseId() && variantId != dbVariant.getVariantId()) {

				companyCodeId = dbVariant.getCompanyId();
				plantId = dbVariant.getPlantId();
				warehouseId = dbVariant.getWarehouseId();
				variantId = dbVariant.getVariantId();
				levelId = dbVariant.getLevelId();

				ikeyValuePair = variantRepository.getDescription(
						companyCodeId,
						plantId,
						warehouseId,
						dbVariant.getLanguageId(),
						levelId,
						variantId);
			}
			if(ikeyValuePair != null) {
				dbVariant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId()+"-"+ikeyValuePair.getCompanyDescription());
				dbVariant.setPlantIdAndDescription(ikeyValuePair.getPlantId()+"-"+ikeyValuePair.getPlantDescription());
				dbVariant.setWarehouseIdAndDescription(ikeyValuePair.getWarehouseId()+"-"+ikeyValuePair.getWarehouseDescription());
				dbVariant.setLevelIdAndDescription(ikeyValuePair.getLevelId()+"-"+ikeyValuePair.getLevelDescription());
				dbVariant.setDescription(ikeyValuePair.getVariantDescription());
			}
//			BeanUtils.copyProperties(dbVariant, newVariantOutput, CommonUtils.getNullPropertyNames(dbVariant));
//
//			List<String> newLevelReferenceVariantList = new ArrayList<>();
//			if(dbVariant.getLevelReferenceVariants() != null) {
//				for (LevelReferenceVariant dbLevelReferenceVariant : dbVariant.getLevelReferenceVariants()) {
//					newLevelReferenceVariantList.add(dbLevelReferenceVariant.getLevelReference());
//				}
//			}
//			newVariantOutput.setLevelReferenceVariant(newLevelReferenceVariantList);
//
			newVariantList.add(dbVariant);

		}
		return newVariantList;
	}

	/**
	 * getVariant
	 *
	 * @param variantId
	 * @return
	 */
	public Variant getVariant(String variantId,String companyId,String languageId,String plantId,String warehouseId,Long levelId,String variantSubId) {

		List<Variant> variant =
				variantRepository.findByVariantIdAndLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndLevelIdAndVariantSubIdAndDeletionIndicator(
						variantId, languageId, companyId, plantId, warehouseId, levelId, variantSubId, 0L);
		if (variant.isEmpty()) {
			throw new BadRequestException("The given Values of " + "Variant ID : " + variantId +
					" companyId " + companyId +
					" plantId " + plantId +
					" warehouseId " + warehouseId +
					" languageId " + languageId +
					" levelId " + levelId + " doesn't exist.");
		}
		return (Variant) variant;
	}
//		Variant newVariant = new Variant();
//		BeanUtils.copyProperties(variant.get(), newVariant, CommonUtils.getNullPropertyNames(variant));
//		IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(newVariant.getCompanyId(), newVariant.getLanguageId());
//		IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(newVariant.getPlantId(), newVariant.getLanguageId(), newVariant.getCompanyId());
//		IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newVariant.getWarehouseId(), newVariant.getLanguageId(), newVariant.getCompanyId(), newVariant.getPlantId());
//		IkeyValuePair ikeyValuePair3 = variantRepository.getVariantIdAndDescription(newVariant.getVariantId(), newVariant.getLanguageId(), newVariant.getCompanyId(), newVariant.getPlantId(), newVariant.getWarehouseId());
//		IkeyValuePair ikeyValuePair4 = batchSerialRepository.getLevelIdAndDescription(String.valueOf(newVariant.getLevelId()), newVariant.getLanguageId(), newVariant.getCompanyId(), newVariant.getPlantId(), newVariant.getWarehouseId());
//		if(iKeyValuePair!=null) {
//			newVariant.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//		}
//		if(iKeyValuePair1!=null) {
//			newVariant.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//		}
//		if(iKeyValuePair2!=null) {
//			newVariant.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//		}
//		if(ikeyValuePair4!=null) {
//			newVariant.setLevelIdAndDescription(ikeyValuePair4.getLevelId() + "-" + ikeyValuePair4.getDescription());
//		}
//		if(ikeyValuePair3!=null) {
//			newVariant.setDescription(ikeyValuePair3.getDescription());
//		}
//		return newVariant;


	public List<Variant> getVariantOutput(String variantId,String companyId,String languageId,
										  String plantId,String warehouseId,Long levelId,String variantSubId) {

		List<Variant> variantList = new ArrayList<>();
		List<Variant> variant =
				variantRepository.findByVariantIdAndLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndLevelIdAndVariantSubIdAndDeletionIndicator(
						variantId, languageId, companyId, plantId, warehouseId, levelId, variantSubId, 0L);

		if (variant.isEmpty()) {
			throw new BadRequestException("The given Values of " + "Variant ID : " + variantId +
					"companyId" + companyId +
					"plantId" + plantId +
					"warehouseId" + warehouseId +
					"languageId" + languageId +
					"levelId" + levelId +
					"variantSubId" + variantSubId + " doesn't exist.");
		} else {
			for (Variant dbVariant : variant) {
				IkeyValuePair ikeyValuePair = variantRepository.getDescription(
						companyId,
						plantId,
						warehouseId,
						languageId,
						levelId,
						variantId);

				if (ikeyValuePair != null) {
					dbVariant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getCompanyDescription());
					dbVariant.setPlantIdAndDescription(ikeyValuePair.getPlantId() + "-" + ikeyValuePair.getPlantDescription());
					dbVariant.setWarehouseIdAndDescription(ikeyValuePair.getWarehouseId() + "-" + ikeyValuePair.getWarehouseDescription());
					dbVariant.setLevelIdAndDescription(ikeyValuePair.getLevelId() + "-" + ikeyValuePair.getLevelDescription());
					dbVariant.setDescription(ikeyValuePair.getVariantDescription());
				}
//		BeanUtils.copyProperties(variant.get(), newVariantOutput, CommonUtils.getNullPropertyNames(variant.get()));
//
//		List<String> newLevelReferenceVariantList = new ArrayList<>();
//		if(variant.get().getLevelReferenceVariants() != null) {
//			for (LevelReferenceVariant dbLevelReferenceVariant : variant.get().getLevelReferenceVariants()) {
//				newLevelReferenceVariantList.add(dbLevelReferenceVariant.getLevelReference());
//			}
//		}
//		newVariantOutput.setLevelReferenceVariant(newLevelReferenceVariantList);
				variantList.add(dbVariant);
			}
			return variantList;
		}
	}

	/**
	 * findVariant
	 *
	 * @param searchVariant
	 * @return
	 */
	public List<Variant> findVariant(SearchVariant searchVariant) throws Exception {
		if (searchVariant.getStartCreatedOn() != null && searchVariant.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchVariant.getStartCreatedOn(), searchVariant.getEndCreatedOn());
			searchVariant.setStartCreatedOn(dates[0]);
			searchVariant.setEndCreatedOn(dates[1]);
		}

		VariantSpecification spec = new VariantSpecification(searchVariant);
		List<Variant> results = variantRepository.findAll(spec);
		log.info("results: " + results);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

		String companyCodeId = null;
		String plantId = null;
		String warehouseId = null;
		String variantId = null;
		Long levelId = null;
		String variantSubId = null;
		IkeyValuePair ikeyValuePair = null;

//		List<VariantOutput> newVariantList =new ArrayList<>();
		List<Variant> newVariantList =new ArrayList<>();

		for(Variant dbVariant:results) {


			if(companyCodeId != dbVariant.getCompanyId() && plantId != dbVariant.getPlantId() &&
					warehouseId != dbVariant.getWarehouseId() && variantId != dbVariant.getVariantId()) {

				companyCodeId = dbVariant.getCompanyId();
				plantId = dbVariant.getPlantId();
				warehouseId = dbVariant.getWarehouseId();
				variantId = dbVariant.getVariantId();
				levelId = dbVariant.getLevelId();

				ikeyValuePair = variantRepository.getDescription(
						companyCodeId,
						plantId,
						warehouseId,
						dbVariant.getLanguageId(),
						levelId,
						variantId);
			}
			if(ikeyValuePair != null) {
				dbVariant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId()+"-"+ikeyValuePair.getCompanyDescription());
				dbVariant.setPlantIdAndDescription(ikeyValuePair.getPlantId()+"-"+ikeyValuePair.getPlantDescription());
				dbVariant.setWarehouseIdAndDescription(ikeyValuePair.getWarehouseId()+"-"+ikeyValuePair.getWarehouseDescription());
				dbVariant.setLevelIdAndDescription(ikeyValuePair.getLevelId()+"-"+ikeyValuePair.getLevelDescription());
				dbVariant.setDescription(ikeyValuePair.getVariantDescription());
			}
//			BeanUtils.copyProperties(dbVariant, newVariantOutput, CommonUtils.getNullPropertyNames(dbVariant));
//
//			List<String> newLevelReferenceVariantList = new ArrayList<>();
//			if(dbVariant.getLevelReferenceVariants() != null) {
//				for (LevelReferenceVariant dbLevelReferenceVariant : dbVariant.getLevelReferenceVariants()) {
//					newLevelReferenceVariantList.add(dbLevelReferenceVariant.getLevelReference());
//				}
//			}
//			newVariantOutput.setLevelReferenceVariant(newLevelReferenceVariantList);
//
//
//			newVariantList.add(newVariantOutput);
			newVariantList.add(dbVariant);
		}
		return newVariantList;

	}

	/**
	 * createVariant
	 *
	 * @param newVariant
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Variant> createVariant(List<AddVariant> newVariant, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<Variant> variantList = new ArrayList<>();

		for (AddVariant addVariant : newVariant) {

			List<Variant> duplicateVariant = variantRepository.findByVariantIdAndLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndLevelIdAndVariantSubIdAndLevelReferenceAndDeletionIndicator(
					addVariant.getVariantId(), addVariant.getLanguageId(), addVariant.getCompanyId(),
					addVariant.getPlantId(), addVariant.getWarehouseId(), addVariant.getLevelId(),
					addVariant.getVariantSubId(), addVariant.getLevelReference(), 0L);

			if (!duplicateVariant.isEmpty()) {

				throw new EntityNotFoundException("Record is Getting Duplicated");

			} else {
				IkeyValuePair ikeyValuePair =
						companyRepository.getCompanyIdAndDescription(addVariant.getCompanyId(), addVariant.getLanguageId());

				IkeyValuePair ikeyValuePair1 =
						plantRepository.getPlantIdAndDescription(addVariant.getPlantId(), addVariant.getLanguageId(), addVariant.getCompanyId());

				IkeyValuePair ikeyValuePair2 =
						warehouseRepository.getWarehouseIdAndDescription(addVariant.getWarehouseId(), addVariant.getLanguageId(),
								addVariant.getCompanyId(), addVariant.getPlantId());

				IkeyValuePair ikeyValuePair3 =
						batchSerialRepository.getLevelIdAndDescription(String.valueOf(addVariant.getLevelId()), addVariant.getLanguageId(),
								addVariant.getCompanyId(), addVariant.getPlantId(), addVariant.getWarehouseId());

				IkeyValuePair ikeyValuePair4 = variantRepository.getVariantIdAndDescription(addVariant.getVariantId(),
						addVariant.getLanguageId(), addVariant.getCompanyId(), addVariant.getPlantId(), addVariant.getWarehouseId(), addVariant.getVariantSubId());


				Variant dbVariant = new Variant();
				Long id = variantRepository.getId();
				BeanUtils.copyProperties(addVariant, dbVariant, CommonUtils.getNullPropertyNames(addVariant));

				if (id != null) {

					dbVariant.setId(id);

				} else {

					dbVariant.setId(1L);
				}

				if (ikeyValuePair != null && ikeyValuePair1 != null && ikeyValuePair2 != null &&
						ikeyValuePair3 != null && ikeyValuePair4 != null) {

					dbVariant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
					dbVariant.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
					dbVariant.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
					dbVariant.setLevelIdAndDescription(ikeyValuePair3.getLevelId() + "-" + ikeyValuePair3.getDescription());
					dbVariant.setDescription(ikeyValuePair4.getDescription());
					dbVariant.setVariantId(ikeyValuePair4.getVariantId());
				} else {
					throw new BadRequestException("The given values of Company Id "
							+ addVariant.getCompanyId() + " Plant Id "
							+ addVariant.getPlantId() + " Warehouse Id "
							+ addVariant.getWarehouseId() + " Level Id "
							+ addVariant.getLevelId() + " Variant Id "
							+ addVariant.getVariantId() + " doesn't exist");
				}

				dbVariant.setDeletionIndicator(0L);
				dbVariant.setCreatedBy(loginUserID);
				dbVariant.setUpdatedBy(loginUserID);
				dbVariant.setCreatedOn(new Date());
				dbVariant.setUpdatedOn(new Date());
				Variant savedVariant = variantRepository.save(dbVariant);

//			savedVariant.setLevelReferenceVariants(new HashSet<>());
//			if (newVariant.getLevelReferenceVariants() != null) {
//				for (LevelReferenceVariant newLevelReference : newVariant.getLevelReferenceVariants()) {
//					LevelReferenceVariant dblevelReferenceVariant = new LevelReferenceVariant();
//					BeanUtils.copyProperties(newLevelReference, dblevelReferenceVariant, CommonUtils.getNullPropertyNames(newLevelReference));
//					dblevelReferenceVariant.setDeletionIndicator(0L);
//					dblevelReferenceVariant.setCreatedBy(loginUserID);
//					dblevelReferenceVariant.setUpdatedBy(loginUserID);

//					dblevelReferenceVariant.setCreatedOn(new Date());
//					dblevelReferenceVariant.setUpdatedOn(new Date());
//					dblevelReferenceVariant.setVariantId(savedVariant.getVariantId());
//					LevelReferenceVariant savedLevelReferenceVariant = levelReferenceVariantRepository.save(dblevelReferenceVariant);
//					savedVariant.getLevelReferenceVariants().add(savedLevelReferenceVariant);
//				}

//			}
				variantList.add(savedVariant);
			}
		}
		return variantList;
	}


//	}public List<Variant> createVariant (List<AddVariant> newVariant, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		Variant dbVariant = new Variant();
//		Optional<Variant> duplicateVariant = variantRepository.findByVariantId(dbVariant.getVariantId());
//		if (!duplicateVariant.isEmpty()) {
//			throw new EntityNotFoundException("Record is Getting Duplicated");
//		} else {
//			BeanUtils.copyProperties(newVariant, dbVariant, CommonUtils.getNullPropertyNames(newVariant));
//			dbVariant.setDeletionIndicator(0L);
//			IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newVariant.getCompanyId(), newVariant.getLanguageId());
//			IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newVariant.getPlantId(), newVariant.getLanguageId(), newVariant.getCompanyId());
//			IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newVariant.getWarehouseId(), newVariant.getLanguageId(), newVariant.getCompanyId(), newVariant.getPlantId());
//			IkeyValuePair ikeyValuePair3= batchSerialRepository.getLevelIdAndDescription(String.valueOf(newVariant.getLevelId()), newVariant.getLanguageId());
//			dbVariant.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
//			dbVariant.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
//			dbVariant.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
//			dbVariant.setLevelIdAndDescription(ikeyValuePair3.getLevelId()+"-"+ikeyValuePair3.getDescription());
//			dbVariant.setCreatedBy(loginUserID);
//			dbVariant.setUpdatedBy(loginUserID);
//			dbVariant.setCreatedOn(new Date());
//			dbVariant.setUpdatedOn(new Date()
//			);
//			return variantRepository.save(dbVariant);
//		}
//	}

	/**
	 *
	 * @param variantId
	 * @param companyId
	 * @param languageId
	 * @param plantId
	 * @param variantSubId
	 * @param warehouseId
	 * @param levelId
	 * @param updateVariant
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Variant> updateVariant(String variantId,String companyId,String languageId,String plantId,String variantSubId,
									   String warehouseId,Long levelId,List<UpdateVariant> updateVariant, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<Variant>variantList=new ArrayList<>();


		for(UpdateVariant newUpdateVariant:updateVariant) {
			if (newUpdateVariant.getId() != null) {
				Variant dbVariant = variantRepository.findByVariantIdAndLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndLevelIdAndVariantSubIdAndIdAndDeletionIndicator(
						variantId,
						languageId,
						companyId,
						plantId,
						warehouseId,
						levelId,
						variantSubId,
						newUpdateVariant.getId(),
						0L);

				if (dbVariant != null) {
					BeanUtils.copyProperties(newUpdateVariant, dbVariant, CommonUtils.getNullPropertyNames(newUpdateVariant));
					dbVariant.setUpdatedBy(loginUserID);
					dbVariant.setUpdatedOn(new Date());
					variantList.add(variantRepository.save(dbVariant));
				}
				else {
					throw new EntityNotFoundException(" The given values of companyCodeId " + companyId +
							" plantId " + plantId +
							" warehouseId " + warehouseId +
							" languageId " + languageId +
							" levelId " + levelId +
							" variantId " + variantId +
							" variantSubId " + variantSubId + " doesn't exists");
				}
			}else {

					Long id = variantRepository.getId();
					Variant newVariant = new Variant();

					List<Variant> duplicateVariants = variantRepository.findByVariantIdAndLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndLevelIdAndVariantSubIdAndLevelReferenceAndDeletionIndicator(
							newUpdateVariant.getVariantId(), newUpdateVariant.getLanguageId(), newUpdateVariant.getCompanyId(), newUpdateVariant.getPlantId(), newUpdateVariant.getWarehouseId(),
							newUpdateVariant.getLevelId(), newUpdateVariant.getVariantSubId(), newUpdateVariant.getLevelReference(), 0L);

					if (!duplicateVariants.isEmpty()) {
						throw new EntityNotFoundException("Record is Getting Duplicate");
					} else {

						BeanUtils.copyProperties(newUpdateVariant, newVariant, CommonUtils.getNullPropertyNames(newUpdateVariant));
						newVariant.setId(id);
						newVariant.setDeletionIndicator(0L);
						newVariant.setCreatedBy(loginUserID);
						newVariant.setUpdatedBy(loginUserID);
						newVariant.setCreatedOn(new Date());
						newVariant.setUpdatedOn(new Date());
						variantList.add(variantRepository.save(newVariant));
					}
				}
			}

		return variantList;
	}


	/**
	 *
	 * @param variantId
	 * @param companyId
	 * @param languageId
	 * @param plantId
	 * @param variantSubId
	 * @param warehouseId
	 * @param levelId
	 * @param loginUserID
	 */
	public void deleteVariant(String variantId,String companyId,String languageId,String plantId,String variantSubId,
							  String warehouseId,Long levelId,String loginUserID) throws ParseException {
		List<Variant> variant = getVariantOutput(variantId,companyId,languageId,plantId,warehouseId,levelId,variantSubId);
		if (variant != null) {
			for (Variant dbVariant:variant){
				dbVariant.setDeletionIndicator(1L);
				dbVariant.setUpdatedBy(loginUserID);
				dbVariant.setUpdatedOn(new Date());
				variantRepository.save(dbVariant);
				//batchSerialRepository.save(batchSerial);
//				if (levelReferenceVariantService.getLevelReferenceVariant(variantId) != null) {
//					levelReferenceVariantService.deleteLevelReferences(variantId, loginUserID);
			}
		}else {
			throw new EntityNotFoundException(variantId);
		}
	}
}

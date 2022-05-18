package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.packingmaterial.AddPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.SearchPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.UpdatePackingMaterial;
import com.tekclover.wms.api.masters.repository.PackingMaterialRepository;
import com.tekclover.wms.api.masters.repository.specification.PackingMaterialSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PackingMaterialService {
	
	@Autowired
	private PackingMaterialRepository packingmaterialRepository;
	
	/**
	 * getPackingMaterials
	 * @return
	 */
	public List<PackingMaterial> getPackingMaterials () {
		List<PackingMaterial> packingmaterialList = packingmaterialRepository.findAll();
		log.info("packingmaterialList : " + packingmaterialList);
		packingmaterialList = packingmaterialList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return packingmaterialList;
	}
	
	/**
	 * getPackingMaterial
	 * @param packingMaterialNo
	 * @return
	 */
	public PackingMaterial getPackingMaterial (String packingMaterialNo) {
		PackingMaterial packingmaterial = packingmaterialRepository.findByPackingMaterialNo(packingMaterialNo).orElse(null);
		if (packingmaterial != null && packingmaterial.getDeletionIndicator() != null && packingmaterial.getDeletionIndicator() == 0) {
			return packingmaterial;
		} else {
			throw new BadRequestException("The given PackingMaterial ID : " + packingMaterialNo + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchPackingMaterial
	 * @return
	 * @throws Exception
	 */
	public List<PackingMaterial> findPackingMaterial(SearchPackingMaterial searchPackingMaterial)
			throws Exception {
		if (searchPackingMaterial.getStartCreatedOn() != null && searchPackingMaterial.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPackingMaterial.getStartCreatedOn(), searchPackingMaterial.getEndCreatedOn());
			searchPackingMaterial.setStartCreatedOn(dates[0]);
			searchPackingMaterial.setEndCreatedOn(dates[1]);
		}
		
		if (searchPackingMaterial.getStartUpdatedOn() != null && searchPackingMaterial.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPackingMaterial.getStartUpdatedOn(), searchPackingMaterial.getEndUpdatedOn());
			searchPackingMaterial.setStartUpdatedOn(dates[0]);
			searchPackingMaterial.setEndUpdatedOn(dates[1]);
		}
		
		PackingMaterialSpecification spec = new PackingMaterialSpecification(searchPackingMaterial);
		List<PackingMaterial> results = packingmaterialRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPackingMaterial
	 * @param newPackingMaterial
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PackingMaterial createPackingMaterial (AddPackingMaterial newPackingMaterial, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PackingMaterial dbPackingMaterial = new PackingMaterial();
		BeanUtils.copyProperties(newPackingMaterial, dbPackingMaterial, CommonUtils.getNullPropertyNames(newPackingMaterial));
		dbPackingMaterial.setDeletionIndicator(0L);
		dbPackingMaterial.setCreatedBy(loginUserID);
		dbPackingMaterial.setUpdatedBy(loginUserID);
		dbPackingMaterial.setCreatedOn(new Date());
		dbPackingMaterial.setUpdatedOn(new Date());
		return packingmaterialRepository.save(dbPackingMaterial);
	}
	
	/**
	 * updatePackingMaterial
	 * @param packingmaterial
	 * @param updatePackingMaterial
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PackingMaterial updatePackingMaterial (String packingMaterialNo, UpdatePackingMaterial updatePackingMaterial, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PackingMaterial dbPackingMaterial = getPackingMaterial(packingMaterialNo);
		BeanUtils.copyProperties(updatePackingMaterial, dbPackingMaterial, CommonUtils.getNullPropertyNames(updatePackingMaterial));
		dbPackingMaterial.setUpdatedBy(loginUserID);
		dbPackingMaterial.setUpdatedOn(new Date());
		return packingmaterialRepository.save(dbPackingMaterial);
	}
	
	/**
	 * deletePackingMaterial
	 * @param packingmaterial
	 */
	public void deletePackingMaterial (String packingMaterialNo, String loginUserID) {
		PackingMaterial packingmaterial = getPackingMaterial(packingMaterialNo);
		if ( packingmaterial != null) {
			packingmaterial.setDeletionIndicator (1L);
			packingmaterial.setUpdatedBy(loginUserID);
			packingmaterial.setUpdatedOn(new Date());
			packingmaterialRepository.save(packingmaterial);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + packingMaterialNo);
		}
	}
}

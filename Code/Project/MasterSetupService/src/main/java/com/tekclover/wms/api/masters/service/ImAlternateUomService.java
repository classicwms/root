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
		log.info("imalternateuomList : " + imalternateuomList);
		imalternateuomList = imalternateuomList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return imalternateuomList;
	}
	
	/**
	 * getImAlternateUom
	 * @param alternateUom
	 * @return
	 */
	public ImAlternateUom getImAlternateUom (String alternateUom) {
		ImAlternateUom imalternateuom = imalternateuomRepository.findByAlternateUom(alternateUom).orElse(null);
		if (imalternateuom != null && imalternateuom.getDeletionIndicator() != null && imalternateuom.getDeletionIndicator() == 0) {
			return imalternateuom;
		} else {
			throw new BadRequestException("The given ImAlternateUom ID : " + alternateUom + " doesn't exist.");
		}
	}
	
	/**
	 * findImAlternateUom
	 * @param searchImAlternateUom
	 * @return
	 * @throws ParseException
	 */
//	public List<ImAlternateUom> findImAlternateUom(SearchImAlternateUom searchImAlternateUom) throws ParseException {
//		if (searchImAlternateUom.getStartCreatedOn() != null && searchImAlternateUom.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImAlternateUom.getStartCreatedOn(), searchImAlternateUom.getEndCreatedOn());
//			searchImAlternateUom.setStartCreatedOn(dates[0]);
//			searchImAlternateUom.setEndCreatedOn(dates[1]);
//		}
//		
//		ImAlternateUomSpecification spec = new ImAlternateUomSpecification(searchImAlternateUom);
//		List<ImAlternateUom> results = imalternateuomRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createImAlternateUom
	 * @param newImAlternateUom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImAlternateUom createImAlternateUom (AddImAlternateUom newImAlternateUom, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImAlternateUom dbImAlternateUom = new ImAlternateUom();
		BeanUtils.copyProperties(newImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(newImAlternateUom));
		dbImAlternateUom.setDeletionIndicator(0L);
		dbImAlternateUom.setCreatedBy(loginUserID);
		dbImAlternateUom.setUpdatedBy(loginUserID);
		dbImAlternateUom.setCreatedOn(new Date());
		dbImAlternateUom.setUpdatedOn(new Date());
		return imalternateuomRepository.save(dbImAlternateUom);
	}
	
	/**
	 * updateImAlternateUom
	 * @param imalternateuom
	 * @param updateImAlternateUom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImAlternateUom updateImAlternateUom (String alternateUom, UpdateImAlternateUom updateImAlternateUom, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImAlternateUom dbImAlternateUom = getImAlternateUom(alternateUom);
		BeanUtils.copyProperties(updateImAlternateUom, dbImAlternateUom, CommonUtils.getNullPropertyNames(updateImAlternateUom));
		dbImAlternateUom.setUpdatedBy(loginUserID);
		dbImAlternateUom.setUpdatedOn(new Date());
		return imalternateuomRepository.save(dbImAlternateUom);
	}
	
	/**
	 * deleteImAlternateUom
	 * @param imalternateuom
	 */
	public void deleteImAlternateUom (String alternateUom, String loginUserID) {
		ImAlternateUom imalternateuom = getImAlternateUom(alternateUom);
		if ( imalternateuom != null) {
			imalternateuom.setDeletionIndicator (1L);
			imalternateuom.setUpdatedBy(loginUserID);
			imalternateuom.setUpdatedOn(new Date());
			imalternateuomRepository.save(imalternateuom);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + alternateUom);
		}
	}
}

package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.AddPreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.UpdatePreOutboundLine;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundLineSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreOutboundLineService extends BaseService {
	
	@Autowired
	private PreOutboundLineRepository preOutboundLineRepository;
	
	/**
	 * getPreOutboundLines
	 * @return
	 */
	public List<PreOutboundLine> getPreOutboundLines () {
		List<PreOutboundLine> preOutboundLineList =  preOutboundLineRepository.findAll();
		preOutboundLineList = preOutboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return preOutboundLineList;
	}
	
	/**
	 * getPreOutboundLine
	 * @param lineNumber
	 * @return
	 */
	public PreOutboundLine getPreOutboundLine (String languageId, String companyCodeId, String plantId, String warehouseId, 
			String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode) {
		Optional<PreOutboundLine> preOutboundLine = 
				preOutboundLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator (
						languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, 
						lineNumber, itemCode, 0L);
		if (!preOutboundLine.isEmpty()) {
			return preOutboundLine.get();
		} 

		return null;
	}
	
	/**
	 * 
	 * @param lineNumber
	 * @return
	 */
	private List<PreOutboundLine> getPreOutboundLine(String preOutboundNo) {
		List<PreOutboundLine> preOutboundLine = preOutboundLineRepository.findByPreOutboundNo(preOutboundNo);
		if (preOutboundLine != null) {
			return preOutboundLine;
		} else {
			throw new BadRequestException("The given PreOutboundLine ID : " + preOutboundNo + " doesn't exist.");
		}
	}

	
	/**
	 * 
	 * @param searchPreOutboundLine
	 * @return
	 * @throws ParseException
	 */
	public List<PreOutboundLine> findPreOutboundLine(SearchPreOutboundLine searchPreOutboundLine) 
			throws ParseException {
		PreOutboundLineSpecification spec = new PreOutboundLineSpecification(searchPreOutboundLine);
		List<PreOutboundLine> results = preOutboundLineRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPreOutboundLine
	 * @param newPreOutboundLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreOutboundLine createPreOutboundLine (AddPreOutboundLine newPreOutboundLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundLine dbPreOutboundLine = new PreOutboundLine();
		log.info("newPreOutboundLine : " + newPreOutboundLine);
		BeanUtils.copyProperties(newPreOutboundLine, dbPreOutboundLine);
		dbPreOutboundLine.setDeletionIndicator(0L);
		dbPreOutboundLine.setCreatedBy(loginUserID);
		dbPreOutboundLine.setUpdatedBy(loginUserID);
		dbPreOutboundLine.setCreatedOn(new Date());
		dbPreOutboundLine.setUpdatedOn(new Date());
		return preOutboundLineRepository.save(dbPreOutboundLine);
	}
	
	/**
	 * updatePreOutboundLine
	 * @param loginUserId 
	 * @param lineNumber
	 * @param updatePreOutboundLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PreOutboundLine updatePreOutboundLine (String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode, 
			String loginUserID, UpdatePreOutboundLine updatePreOutboundLine) 
			throws IllegalAccessException, InvocationTargetException {
		PreOutboundLine dbPreOutboundLine = getPreOutboundLine (getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, 
				refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode);
		if (dbPreOutboundLine != null) {
			BeanUtils.copyProperties(updatePreOutboundLine, dbPreOutboundLine, CommonUtils.getNullPropertyNames(updatePreOutboundLine));
			dbPreOutboundLine.setUpdatedBy(loginUserID);
			dbPreOutboundLine.setUpdatedOn(new Date());
			return preOutboundLineRepository.save(dbPreOutboundLine);
		}
		return null;
	}
	
	/**
	 * deletePreOutboundLine
	 * @param loginUserID 
	 * @param lineNumber
	 */
//	public void deletePreOutboundLine (String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, Long lineNumber, String itemCode, String loginUserID) {
//		PreOutboundLine preOutboundLine = getPreOutboundLine(lineNumber);
//		if ( preOutboundLine != null) {
//			preOutboundLine.setDeletionIndicator(1L);
//			preOutboundLine.setUpdatedBy(loginUserID);
//			preOutboundLineRepository.save(preOutboundLine);
//		} else {
//			throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
//		}
//	}
}

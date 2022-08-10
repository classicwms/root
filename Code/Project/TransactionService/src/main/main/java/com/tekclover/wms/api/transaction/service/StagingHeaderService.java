package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.inbound.staging.AddStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.SearchStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.staging.UpdateStagingHeader;
import com.tekclover.wms.api.transaction.repository.StagingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.StagingHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StagingHeaderService extends BaseService {
	
	@Autowired
	private StagingHeaderRepository stagingHeaderRepository;
	
	@Autowired
	private StagingLineRepository stagingLineRepository;
	
	@Autowired
	private StagingLineService stagingLineService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	/**
	 * getStagingHeaders
	 * @return
	 */
	public List<StagingHeader> getStagingHeaders () {
		List<StagingHeader> stagingHeaderList =  stagingHeaderRepository.findAll();
		stagingHeaderList = stagingHeaderList.stream()
				.filter(n -> n.getDeletionIndicator() != null && 
				n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return stagingHeaderList;
	}
	
	/**
	 * getStagingHeader
	 * @param stagingHeaderNo
	 * @return
	 */
	public StagingHeader getStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo) {
		log.info("Staging Header value : " + getLanguageId() + "," + getCompanyCode()
		+ "," + getPlantId() + "," + warehouseId + "," + refDocNumber + "," + preInboundNo + "," + stagingNo);
		
		Optional<StagingHeader> stagingHeader = 
				stagingHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						preInboundNo,
						refDocNumber,
						stagingNo,
						0L);
		log.info("stagingHeader : " + stagingHeader);
		if (stagingHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					",stagingNo: " + stagingNo + " doesn't exist.");
		} 
		
		return stagingHeader.get();
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @return
	 */
	public List<StagingHeader> getStagingHeader (String warehouseId, String preInboundNo, String refDocNumber) {
		List<StagingHeader> stagingHeader = 
				stagingHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						preInboundNo,
						refDocNumber,
						0L);
		log.info("stagingHeader : " + stagingHeader);
		if (stagingHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",refDocNumber: " + refDocNumber + 
					",preInboundNo: " + preInboundNo + 
					" doesn't exist.");
		} 
		return stagingHeader;
	}
	
	/**
	 * 
	 * @param numberOfCases
	 * @param warehouseId
	 * @return
	 */
	public List<String> generateNumberRanges(Long numberOfCases, String warehouseId) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		List<String> numberRanges = new ArrayList<>();
		for (int i = 0; i < numberOfCases; i++) {
			String nextRangeNumber = getNextRangeNumber(4, warehouseId, authTokenForIDMasterService.getAccess_token());
			numberRanges.add(nextRangeNumber);
		}
		return numberRanges;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<StagingHeader> getStagingHeaderCount (String warehouseId) {
		return stagingHeaderRepository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 12L, 0L);
	}
	
	/**
	 * 
	 * @param searchStagingHeader
	 * @return
	 * @throws ParseException
	 */
	public List<StagingHeader> findStagingHeader(SearchStagingHeader searchStagingHeader) 
			throws Exception {
		if (searchStagingHeader.getStartCreatedOn() != null && searchStagingHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStagingHeader.getStartCreatedOn(), searchStagingHeader.getEndCreatedOn());
			searchStagingHeader.setStartCreatedOn(dates[0]);
			searchStagingHeader.setEndCreatedOn(dates[1]);
		}
		
		StagingHeaderSpecification spec = new StagingHeaderSpecification(searchStagingHeader);
		List<StagingHeader> results = stagingHeaderRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStagingHeader
	 * @param newStagingHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StagingHeader createStagingHeader (AddStagingHeader newStagingHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StagingHeader dbStagingHeader = new StagingHeader();
		log.info("newStagingHeader : " + newStagingHeader);
		BeanUtils.copyProperties(newStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(newStagingHeader));
		dbStagingHeader.setDeletionIndicator(0L);
		dbStagingHeader.setCreatedBy(loginUserID);
		dbStagingHeader.setUpdatedBy(loginUserID);
		dbStagingHeader.setCreatedOn(new Date());
		dbStagingHeader.setUpdatedOn(new Date());
		return stagingHeaderRepository.save(dbStagingHeader);
	}
	
	/**
	 * updateStagingHeader
	 * @param loginUserId 
	 * @param stagingHeaderNo
	 * @param updateStagingHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StagingHeader updateStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, 
			String loginUserID, UpdateStagingHeader updateStagingHeader) 
			throws IllegalAccessException, InvocationTargetException {
		StagingHeader dbStagingHeader = getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo);
		BeanUtils.copyProperties(updateStagingHeader, dbStagingHeader, CommonUtils.getNullPropertyNames(updateStagingHeader));
		dbStagingHeader.setUpdatedBy(loginUserID);
		dbStagingHeader.setUpdatedOn(new Date());
		return stagingHeaderRepository.save(dbStagingHeader);
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
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void updateStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, Long lineNo,
			String itemCode, Long statusId, String loginUserID) throws IllegalAccessException, InvocationTargetException {
		List<StagingHeader> dbStagingHeaderList = getStagingHeader(warehouseId, preInboundNo, refDocNumber);
		for (StagingHeader dbStagingHeader : dbStagingHeaderList) {
			dbStagingHeader.setStatusId(statusId);
			dbStagingHeader.setUpdatedBy(loginUserID);
			dbStagingHeader.setUpdatedOn(new Date());
			dbStagingHeader = stagingHeaderRepository.save(dbStagingHeader);
			log.info("dbStagingHeader : " + dbStagingHeader);
		}
		
		// Line
		List<StagingLineEntity> stagingLineEntityList = stagingLineService.getStagingLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode);
		for (StagingLineEntity stagingLineEntity : stagingLineEntityList) {
			stagingLineEntity.setStatusId(statusId);
			stagingLineEntity.setUpdatedBy(loginUserID);
			stagingLineEntity.setUpdatedOn(new Date());
			stagingLineEntity = stagingLineRepository.save(stagingLineEntity);
			log.info("stagingLineEntity : " + stagingLineEntity);
		}
		log.info("GRHeader & Line updated..");
	}
	
	/**
	 * 
	 * @param asnNumber
	 */
	public void updateASN (String asnNumber) {
		List<StagingHeader> stagingHeaders = getStagingHeaders();
		stagingHeaders.forEach(stagHeaders -> stagHeaders.setReferenceField1(asnNumber));
		stagingHeaderRepository.saveAll(stagingHeaders);
	}
	
	/**
	 * deleteStagingHeader
	 * @param loginUserID 
	 * @param stagingHeaderNo
	 */
	public void deleteStagingHeader (String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String loginUserID) {
		StagingHeader stagingHeader = getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo);
		if ( stagingHeader != null) {
			stagingHeader.setDeletionIndicator(1L);
			stagingHeader.setUpdatedBy(loginUserID);
			stagingHeaderRepository.save(stagingHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: warehouseId: " + warehouseId + 
					",refDocNumber: " + refDocNumber + "," +
					",preInboundNo: " + preInboundNo + "," +
					",stagingNo: " + stagingNo + " doesn't exist.");
		}
	}
}

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
import com.tekclover.wms.api.masters.model.auditlog.AddAuditLog;
import com.tekclover.wms.api.masters.model.auditlog.AuditLog;
import com.tekclover.wms.api.masters.model.auditlog.UpdateAuditLog;
import com.tekclover.wms.api.masters.repository.AuditLogRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogService {
	
	@Autowired
	private AuditLogRepository auditlogRepository;
	
	/**
	 * getAuditLogs
	 * @return
	 */
	public List<AuditLog> getAuditLogs () {
		List<AuditLog> auditlogList = auditlogRepository.findAll();
		log.info("auditlogList : " + auditlogList);
		auditlogList = auditlogList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return auditlogList;
	}
	
	/**
	 * getAuditLog
	 * @param auditFileNumber
	 * @return
	 */
	public AuditLog getAuditLog (String auditFileNumber) {
		AuditLog auditlog = auditlogRepository.findByAuditFileNumber(auditFileNumber);
		if (auditlog != null && auditlog.getDeletionIndicator() != null && auditlog.getDeletionIndicator() == 0) {
			return auditlog;
		} else {
			throw new BadRequestException("The given AuditLog ID : " + auditFileNumber + " doesn't exist.");
		}
	}
	
	/**
	 * findAuditLog
	 * @param searchAuditLog
	 * @return
	 * @throws ParseException
	 */
//	public List<AuditLog> findAuditLog(SearchAuditLog searchAuditLog) throws ParseException {
//		if (searchAuditLog.getStartCreatedOn() != null && searchAuditLog.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchAuditLog.getStartCreatedOn(), searchAuditLog.getEndCreatedOn());
//			searchAuditLog.setStartCreatedOn(dates[0]);
//			searchAuditLog.setEndCreatedOn(dates[1]);
//		}
//		
//		AuditLogSpecification spec = new AuditLogSpecification(searchAuditLog);
//		List<AuditLog> results = auditlogRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createAuditLog
	 * @param newAuditLog
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AuditLog createAuditLog (AddAuditLog newAuditLog, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		AuditLog dbAuditLog = new AuditLog();
		BeanUtils.copyProperties(newAuditLog, dbAuditLog, CommonUtils.getNullPropertyNames(newAuditLog));
		dbAuditLog.setDeletionIndicator(0L);
		dbAuditLog.setCreatedBy(loginUserID);
		dbAuditLog.setUpdatedBy(loginUserID);
		dbAuditLog.setCreatedOn(new Date());
		dbAuditLog.setUpdatedOn(new Date());
		return auditlogRepository.save(dbAuditLog);
	}
	
	/**
	 * updateAuditLog
	 * @param auditlog
	 * @param updateAuditLog
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AuditLog updateAuditLog (String auditFileNumber, UpdateAuditLog updateAuditLog, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		AuditLog dbAuditLog = getAuditLog(auditFileNumber);
		BeanUtils.copyProperties(updateAuditLog, dbAuditLog, CommonUtils.getNullPropertyNames(updateAuditLog));
		dbAuditLog.setUpdatedBy(loginUserID);
		dbAuditLog.setUpdatedOn(new Date());
		return auditlogRepository.save(dbAuditLog);
	}
	
	/**
	 * deleteAuditLog
	 * @param auditlog
	 */
	public void deleteAuditLog (String auditFileNumber, String loginUserID) {
		AuditLog auditlog = getAuditLog(auditFileNumber);
		if ( auditlog != null) {
			auditlog.setDeletionIndicator (1L);
			auditlog.setUpdatedBy(loginUserID);
			auditlog.setUpdatedOn(new Date());
			auditlogRepository.save(auditlog);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + auditFileNumber);
		}
	}
}

package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.model.auditlog.AuditLog;
import com.tekclover.wms.api.idmaster.repository.AuditLogRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogService {
	
	@Autowired
	private AuditLogRepository auditLogRepository;
	
	public List<AuditLog> getAuditLogs () {
		return auditLogRepository.findAll();
	}
	
	/**
	 * getAuditLog
	 * @param auditlogId
	 * @return
	 */
	public AuditLog getAuditLog (String auditLogNumber) {
		return auditLogRepository.findByAuditLogNumber(auditLogNumber);
	}
	
	/**
	 * createAuditLog
	 * @param newAuditLog
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AuditLog createAuditLog (AuditLog newAuditLog, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		
		AuditLog dbAuditLog = new AuditLog();
		BeanUtils.copyProperties(newAuditLog, dbAuditLog, CommonUtils.getNullPropertyNames(newAuditLog));
		dbAuditLog.setCreatedBy(loginUserID);
		dbAuditLog.setUpdatedBy(loginUserID);
		dbAuditLog.setCreatedOn(new Date());
		dbAuditLog.setUpdatedOn(new Date());
		log.info("dbAuditLog----------> : " + dbAuditLog);
		return auditLogRepository.save(dbAuditLog);
	}
	
	/**
	 * deleteAuditLog
	 * @param auditlogCode
	 */
	public void deleteAuditLog (String auditlogNumber) {
		AuditLog auditlog = getAuditLog(auditlogNumber);
		if ( auditlog != null) {
			auditLogRepository.delete(auditlog);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + auditlogNumber);
		}
	}
}

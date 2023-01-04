package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.documentstatus.AddDocumentStatus;
import com.ustorage.api.master.model.documentstatus.DocumentStatus;
import com.ustorage.api.master.model.documentstatus.UpdateDocumentStatus;
import com.ustorage.api.master.repository.DocumentStatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentStatusService {
	
	@Autowired
	private DocumentStatusRepository documentStatusRepository;
	
	public List<DocumentStatus> getDocumentStatus () {
		List<DocumentStatus> documentStatusList =  documentStatusRepository.findAll();
		documentStatusList = documentStatusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return documentStatusList;
	}
	
	/**
	 * getDocumentStatus
	 * @param documentStatusId
	 * @return
	 */
	public DocumentStatus getDocumentStatus (String documentStatusId) {
		Optional<DocumentStatus> documentStatus = documentStatusRepository.findByCodeAndDeletionIndicator(documentStatusId, 0L);
		if (documentStatus.isEmpty()) {
			return null;
		}
		return documentStatus.get();
	}
	
	/**
	 * createDocumentStatus
	 * @param newDocumentStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DocumentStatus createDocumentStatus (AddDocumentStatus newDocumentStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		DocumentStatus dbDocumentStatus = new DocumentStatus();
		BeanUtils.copyProperties(newDocumentStatus, dbDocumentStatus, CommonUtils.getNullPropertyNames(newDocumentStatus));
		dbDocumentStatus.setDeletionIndicator(0L);
		dbDocumentStatus.setCreatedBy(loginUserId);
		dbDocumentStatus.setUpdatedBy(loginUserId);
		dbDocumentStatus.setCreatedOn(new Date());
		dbDocumentStatus.setUpdatedOn(new Date());
		return documentStatusRepository.save(dbDocumentStatus);
	}
	
	/**
	 * updateDocumentStatus
	 * @param documentStatusId
	 * @param loginUserId 
	 * @param updateDocumentStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DocumentStatus updateDocumentStatus (String code, String loginUserId, UpdateDocumentStatus updateDocumentStatus)
			throws IllegalAccessException, InvocationTargetException {
		DocumentStatus dbDocumentStatus = getDocumentStatus(code);
		BeanUtils.copyProperties(updateDocumentStatus, dbDocumentStatus, CommonUtils.getNullPropertyNames(updateDocumentStatus));
		dbDocumentStatus.setUpdatedBy(loginUserId);
		dbDocumentStatus.setUpdatedOn(new Date());
		return documentStatusRepository.save(dbDocumentStatus);
	}
	
	/**
	 * deleteDocumentStatus
	 * @param loginUserID 
	 * @param documentstatusCode
	 */
	public void deleteDocumentStatus (String documentstatusModuleId, String loginUserID) {
		DocumentStatus documentstatus = getDocumentStatus(documentstatusModuleId);
		if (documentstatus != null) {
			documentstatus.setDeletionIndicator(1L);
			documentstatus.setUpdatedBy(loginUserID);
			documentstatus.setUpdatedOn(new Date());
			documentStatusRepository.save(documentstatus);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + documentstatusModuleId);
		}
	}
}

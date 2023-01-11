package com.ustorage.api.master.service;

import com.ustorage.api.master.model.documentstorage.*;
import com.ustorage.api.master.repository.DocumentStorageRepository;
import com.ustorage.api.master.repository.Specification.DocumentStorageSpecification;
import com.ustorage.api.master.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentStorageService {
	
	@Autowired
	private DocumentStorageRepository documentStorageRepository;

	/**
	 * getDocumentStorage
	 * @param documentNumber
	 * @return
	 */
	public DocumentStorage getDocumentStorage (String documentNumber) {
		Optional<DocumentStorage> documentStorage = documentStorageRepository.findByDocumentNumberAndDeletionIndicator(documentNumber, 0L);
		if (documentStorage.isEmpty()) {
			return null;
		}
		return documentStorage.get();
	}
	
	/**
	 * createDocumentStorage
	 * @param newDocumentStorage
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DocumentStorage createDocumentStorage (AddDocumentStorage newDocumentStorage, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		DocumentStorage dbDocumentStorage = new DocumentStorage();

		BeanUtils.copyProperties(newDocumentStorage, dbDocumentStorage, CommonUtils.getNullPropertyNames(newDocumentStorage));
		dbDocumentStorage.setDeletionIndicator(0L);
		dbDocumentStorage.setCreatedBy(loginUserId);
		dbDocumentStorage.setUpdatedBy(loginUserId);
		dbDocumentStorage.setUploadedBy(loginUserId);
		dbDocumentStorage.setCreatedOn(new Date());
		dbDocumentStorage.setUpdatedOn(new Date());
		dbDocumentStorage.setUploadedOn(new Date());
		return documentStorageRepository.save(dbDocumentStorage);
	}

	/**
	 * deleteDocumentStorage
	 * @param loginUserID 
	 * @param documentNumber
	 */
	public void deleteDocumentStorage (String documentNumber, String loginUserID) {
		DocumentStorage documentstorage = getDocumentStorage(documentNumber);
		if (documentstorage != null) {
			documentstorage.setDeletionIndicator(1L);
			documentstorage.setUpdatedBy(loginUserID);
			documentstorage.setUpdatedOn(new Date());
			documentStorageRepository.save(documentstorage);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + documentNumber);
		}
	}

	//Find DocumentStorage

	public List<DocumentStorage> findDocumentStorage(FindDocumentStorage findDocumentStorage) throws ParseException {

		DocumentStorageSpecification spec = new DocumentStorageSpecification(findDocumentStorage);
		List<DocumentStorage> results = documentStorageRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}

package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.model.agreement.*;

import com.ustorage.api.trans.model.storenumber.*;
import com.ustorage.api.trans.repository.Specification.AgreementSpecification;
import com.ustorage.api.trans.repository.StoreNumberRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.repository.AgreementRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AgreementService {

	@Autowired
	private AgreementRepository agreementRepository;

	@Autowired
	private StoreNumberService storeNumberService;

	@Autowired
	private StoreNumberRepository storeNumberRepository;

	public List<Agreement> getAgreement() {
		List<Agreement> agreementList = agreementRepository.findAll();
		agreementList = agreementList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return agreementList;
	}

	/**
	 * getAgreement
	 *
	 * @param agreementNumber
	 * @return
	 */
	public GAgreement getAgreement(String agreementNumber) {
		Optional<Agreement> agreement = agreementRepository.findByAgreementNumberAndDeletionIndicator(agreementNumber, 0L);
		if (agreement.isEmpty()) {
			return null;
		}
		GAgreement gAgreement = new GAgreement();
		BeanUtils.copyProperties(agreement.get(),gAgreement, CommonUtils.getNullPropertyNames(agreement));
		gAgreement.setReferenceField4(agreementRepository.getStoreNumber(gAgreement.getAgreementNumber()));
		gAgreement.setReferenceField5(agreementRepository.getStoreSize(gAgreement.getAgreementNumber()));
		gAgreement.setReferenceField6(agreementRepository.getStoreLocation(gAgreement.getAgreementNumber()));
		gAgreement.setStoreNumbers(new ArrayList<>());
		for(StoreNumber newStoreNumber : agreement.get().getStoreNumbers()){
			gAgreement.getStoreNumbers().add(newStoreNumber);
		}
		return gAgreement;
	}

	public Agreement getAgreemnt (String agreementNumber) {
		Optional<Agreement> agreement = agreementRepository.findByAgreementNumberAndDeletionIndicator(agreementNumber, 0L);
		if (agreement.isEmpty()) {
			return null;
		}
		return agreement.get();
	}

	/**
	 * createAgreement
	 *
	 * @param newAgreement
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Agreement createAgreement(AddAgreement newAgreement, String loginUserId)
			throws IllegalAccessException, InvocationTargetException, Exception {
		Agreement dbAgreement = new Agreement();
		BeanUtils.copyProperties(newAgreement, dbAgreement, CommonUtils.getNullPropertyNames(newAgreement));
		dbAgreement.setDeletionIndicator(0L);
		dbAgreement.setCreatedBy(loginUserId);
		dbAgreement.setUpdatedBy(loginUserId);
		dbAgreement.setCreatedOn(new Date());
		dbAgreement.setUpdatedOn(new Date());

		Agreement savedAgreement = agreementRepository.save(dbAgreement);

		savedAgreement.setStoreNumbers(new HashSet<>());
			if(newAgreement.getStoreNumbers()!=null) {
				for (StoreNumber newStoreNumber : newAgreement.getStoreNumbers()) {
					StoreNumber dbStoreNumber = new StoreNumber();
					BeanUtils.copyProperties(newStoreNumber, dbStoreNumber, CommonUtils.getNullPropertyNames(newStoreNumber));
					dbStoreNumber.setDeletionIndicator(0L);
					dbStoreNumber.setCreatedBy(loginUserId);
					dbStoreNumber.setUpdatedBy(loginUserId);
					dbStoreNumber.setCreatedOn(new Date());
					dbStoreNumber.setUpdatedOn(new Date());
					dbStoreNumber.setAgreementNumber(savedAgreement.getAgreementNumber());
					//dbStoreNumber.setStoreNumber(newStoreNumber);
					StoreNumber savedStoreNumber = storeNumberRepository.save(dbStoreNumber);
					savedAgreement.getStoreNumbers().add(savedStoreNumber);
				}
			}

		return savedAgreement;
	}

	/**
	 * updateAgreement
	 *
	 * @param agreementNumber
	 * @param loginUserId
	 * @param updateAgreement
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Agreement updateAgreement(String agreementNumber, String loginUserId, UpdateAgreement updateAgreement)
			throws IllegalAccessException, InvocationTargetException {
		Agreement dbAgreement = getAgreemnt(agreementNumber);

		BeanUtils.copyProperties(updateAgreement, dbAgreement, CommonUtils.getNullPropertyNames(updateAgreement));
		dbAgreement.setUpdatedBy(loginUserId);
		dbAgreement.setUpdatedOn(new Date());

		Agreement savedAgreement = agreementRepository.save(dbAgreement);

		if(updateAgreement.getStoreNumbers()!=null){
			if(storeNumberService.getStoreNumber(agreementNumber)!=null) {
				storeNumberService.deleteStoreNumber(agreementNumber, loginUserId);
			}

			for (StoreNumber newStoreNumber : updateAgreement.getStoreNumbers()) {
				StoreNumber dbStoreNumber = new StoreNumber();
				BeanUtils.copyProperties(newStoreNumber, dbStoreNumber, CommonUtils.getNullPropertyNames(newStoreNumber));
				dbStoreNumber.setDeletionIndicator(0L);
				dbStoreNumber.setCreatedOn(new Date());
				dbStoreNumber.setCreatedBy(loginUserId);
				dbStoreNumber.setUpdatedBy(loginUserId);
				dbStoreNumber.setUpdatedOn(new Date());
				dbStoreNumber.setAgreementNumber(savedAgreement.getAgreementNumber());
				//dbStoreNumber.setStoreNumber(newStoreNumber);
				StoreNumber savedStoreNumber = storeNumberRepository.save(dbStoreNumber);
				savedAgreement.getStoreNumbers().add(savedStoreNumber);
			}
		}

		return savedAgreement;
	}

	/**
	 * deleteAgreement
	 *
	 * @param loginUserID
	 * @param agreementNumber
	 */
	public void deleteAgreement(String agreementNumber, String loginUserID) {
		Agreement agreement = getAgreemnt(agreementNumber);
		if (agreement != null) {
			agreement.setDeletionIndicator(1L);
			agreement.setUpdatedBy(loginUserID);
			agreement.setUpdatedOn(new Date());
			agreementRepository.save(agreement);
			if(storeNumberService.getStoreNumber(agreementNumber)!=null) {
				storeNumberService.deleteStoreNumber(agreementNumber,loginUserID);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + agreementNumber);
		}
	}

	//find
	public List<GAgreement> findAgreement(FindAgreement findAgreement) throws ParseException {

		AgreementSpecification spec = new AgreementSpecification(findAgreement);
		List<Agreement> results = agreementRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);

		List<GAgreement> gAgreement = new ArrayList<>();
		for (Agreement dbAgreement : results) {
			GAgreement newGAgreement = new GAgreement();
			BeanUtils.copyProperties(dbAgreement, newGAgreement, CommonUtils.getNullPropertyNames(dbAgreement));
			newGAgreement.setReferenceField4(agreementRepository.getStoreNumber(newGAgreement.getAgreementNumber()));
			newGAgreement.setReferenceField5(agreementRepository.getStoreSize(newGAgreement.getAgreementNumber()));
			newGAgreement.setReferenceField6(agreementRepository.getStoreLocation(newGAgreement.getAgreementNumber()));
			newGAgreement.setStoreNumbers(new ArrayList<>());
			for (StoreNumber newstoreNumber : dbAgreement.getStoreNumbers()) {
				//newGAgreement.getStoreNumbers().add(newstoreNumber.getStoreNumber());
				newGAgreement.getStoreNumbers().add(newstoreNumber);
			}
			gAgreement.add(newGAgreement);
		}
		return gAgreement;
	}

}

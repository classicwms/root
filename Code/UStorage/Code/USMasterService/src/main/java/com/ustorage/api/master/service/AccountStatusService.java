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

import com.ustorage.api.master.model.accountstatus.AddAccountStatus;
import com.ustorage.api.master.model.accountstatus.AccountStatus;
import com.ustorage.api.master.model.accountstatus.UpdateAccountStatus;
import com.ustorage.api.master.repository.AccountStatusRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountStatusService {
	
	@Autowired
	private AccountStatusRepository accountStatusRepository;
	
	public List<AccountStatus> getAccountStatus () {
		List<AccountStatus> accountStatusList =  accountStatusRepository.findAll();
		accountStatusList = accountStatusList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return accountStatusList;
	}
	
	/**
	 * getAccountStatus
	 * @param accountStatusId
	 * @return
	 */
	public AccountStatus getAccountStatus (String accountStatusId) {
		Optional<AccountStatus> accountStatus = accountStatusRepository.findByCodeAndDeletionIndicator(accountStatusId, 0L);
		if (accountStatus.isEmpty()) {
			return null;
		}
		return accountStatus.get();
	}
	
	/**
	 * createAccountStatus
	 * @param newAccountStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AccountStatus createAccountStatus (AddAccountStatus newAccountStatus, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		AccountStatus dbAccountStatus = new AccountStatus();
		BeanUtils.copyProperties(newAccountStatus, dbAccountStatus, CommonUtils.getNullPropertyNames(newAccountStatus));
		dbAccountStatus.setDeletionIndicator(0L);
		dbAccountStatus.setCreatedBy(loginUserId);
		dbAccountStatus.setUpdatedBy(loginUserId);
		dbAccountStatus.setCreatedOn(new Date());
		dbAccountStatus.setUpdatedOn(new Date());
		return accountStatusRepository.save(dbAccountStatus);
	}
	
	/**
	 * updateAccountStatus
	 * @param accountStatusId
	 * @param loginUserId 
	 * @param updateAccountStatus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AccountStatus updateAccountStatus (String code, String loginUserId, UpdateAccountStatus updateAccountStatus)
			throws IllegalAccessException, InvocationTargetException {
		AccountStatus dbAccountStatus = getAccountStatus(code);
		BeanUtils.copyProperties(updateAccountStatus, dbAccountStatus, CommonUtils.getNullPropertyNames(updateAccountStatus));
		dbAccountStatus.setUpdatedBy(loginUserId);
		dbAccountStatus.setUpdatedOn(new Date());
		return accountStatusRepository.save(dbAccountStatus);
	}
	
	/**
	 * deleteAccountStatus
	 * @param loginUserID 
	 * @param accountstatusModuleId
	 */
	public void deleteAccountStatus (String accountstatusModuleId, String loginUserID) {
		AccountStatus accountstatus = getAccountStatus(accountstatusModuleId);
		if (accountstatus != null) {
			accountstatus.setDeletionIndicator(1L);
			accountstatus.setUpdatedBy(loginUserID);
			accountstatus.setUpdatedOn(new Date());
			accountStatusRepository.save(accountstatus);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + accountstatusModuleId);
		}
	}
}

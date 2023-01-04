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

import com.ustorage.api.master.model.storenumbersize.AddStoreNumberSize;
import com.ustorage.api.master.model.storenumbersize.StoreNumberSize;
import com.ustorage.api.master.model.storenumbersize.UpdateStoreNumberSize;
import com.ustorage.api.master.repository.StoreNumberSizeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreNumberSizeService {
	
	@Autowired
	private StoreNumberSizeRepository storeNumberSizeRepository;
	
	public List<StoreNumberSize> getStoreNumberSize () {
		List<StoreNumberSize> storeNumberSizeList =  storeNumberSizeRepository.findAll();
		storeNumberSizeList = storeNumberSizeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storeNumberSizeList;
	}
	
	/**
	 * getStoreNumberSize
	 * @param storeNumberSizeId
	 * @return
	 */
	public StoreNumberSize getStoreNumberSize (String storeNumberSizeId) {
		Optional<StoreNumberSize> storeNumberSize = storeNumberSizeRepository.findByCodeAndDeletionIndicator(storeNumberSizeId, 0L);
		if (storeNumberSize.isEmpty()) {
			return null;
		}
		return storeNumberSize.get();
	}
	
	/**
	 * createStoreNumberSize
	 * @param newStoreNumberSize
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StoreNumberSize createStoreNumberSize (AddStoreNumberSize newStoreNumberSize, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		StoreNumberSize dbStoreNumberSize = new StoreNumberSize();
		BeanUtils.copyProperties(newStoreNumberSize, dbStoreNumberSize, CommonUtils.getNullPropertyNames(newStoreNumberSize));
		dbStoreNumberSize.setDeletionIndicator(0L);
		dbStoreNumberSize.setCreatedBy(loginUserId);
		dbStoreNumberSize.setUpdatedBy(loginUserId);
		dbStoreNumberSize.setCreatedOn(new Date());
		dbStoreNumberSize.setUpdatedOn(new Date());
		return storeNumberSizeRepository.save(dbStoreNumberSize);
	}
	
	/**
	 * updateStoreNumberSize
	 * @param storeNumberSizeId
	 * @param loginUserId 
	 * @param updateStoreNumberSize
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StoreNumberSize updateStoreNumberSize (String code, String loginUserId, UpdateStoreNumberSize updateStoreNumberSize)
			throws IllegalAccessException, InvocationTargetException {
		StoreNumberSize dbStoreNumberSize = getStoreNumberSize(code);
		BeanUtils.copyProperties(updateStoreNumberSize, dbStoreNumberSize, CommonUtils.getNullPropertyNames(updateStoreNumberSize));
		dbStoreNumberSize.setUpdatedBy(loginUserId);
		dbStoreNumberSize.setUpdatedOn(new Date());
		return storeNumberSizeRepository.save(dbStoreNumberSize);
	}
	
	/**
	 * deleteStoreNumberSize
	 * @param loginUserID 
	 * @param storenumbersizeCode
	 */
	public void deleteStoreNumberSize (String storenumbersizeModuleId, String loginUserID) {
		StoreNumberSize storenumbersize = getStoreNumberSize(storenumbersizeModuleId);
		if (storenumbersize != null) {
			storenumbersize.setDeletionIndicator(1L);
			storenumbersize.setUpdatedBy(loginUserID);
			storenumbersize.setUpdatedOn(new Date());
			storeNumberSizeRepository.save(storenumbersize);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storenumbersizeModuleId);
		}
	}
}

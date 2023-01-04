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

import com.ustorage.api.master.model.storesize.AddStoreSize;
import com.ustorage.api.master.model.storesize.StoreSize;
import com.ustorage.api.master.model.storesize.UpdateStoreSize;
import com.ustorage.api.master.repository.StoreSizeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreSizeService {
	
	@Autowired
	private StoreSizeRepository storeSizeRepository;
	
	public List<StoreSize> getStoreSize () {
		List<StoreSize> storeSizeList =  storeSizeRepository.findAll();
		storeSizeList = storeSizeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return storeSizeList;
	}
	
	/**
	 * getStoreSize
	 * @param storeSizeId
	 * @return
	 */
	public StoreSize getStoreSize (String storeSizeId) {
		Optional<StoreSize> storeSize = storeSizeRepository.findByCodeAndDeletionIndicator(storeSizeId, 0L);
		if (storeSize.isEmpty()) {
			return null;
		}
		return storeSize.get();
	}
	
	/**
	 * createStoreSize
	 * @param newStoreSize
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StoreSize createStoreSize (AddStoreSize newStoreSize, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		StoreSize dbStoreSize = new StoreSize();
		BeanUtils.copyProperties(newStoreSize, dbStoreSize, CommonUtils.getNullPropertyNames(newStoreSize));
		dbStoreSize.setDeletionIndicator(0L);
		dbStoreSize.setCreatedBy(loginUserId);
		dbStoreSize.setUpdatedBy(loginUserId);
		dbStoreSize.setCreatedOn(new Date());
		dbStoreSize.setUpdatedOn(new Date());
		return storeSizeRepository.save(dbStoreSize);
	}
	
	/**
	 * updateStoreSize
	 * @param storeSizeId
	 * @param loginUserId 
	 * @param updateStoreSize
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StoreSize updateStoreSize (String code, String loginUserId, UpdateStoreSize updateStoreSize)
			throws IllegalAccessException, InvocationTargetException {
		StoreSize dbStoreSize = getStoreSize(code);
		BeanUtils.copyProperties(updateStoreSize, dbStoreSize, CommonUtils.getNullPropertyNames(updateStoreSize));
		dbStoreSize.setUpdatedBy(loginUserId);
		dbStoreSize.setUpdatedOn(new Date());
		return storeSizeRepository.save(dbStoreSize);
	}
	
	/**
	 * deleteStoreSize
	 * @param loginUserID 
	 * @param storesizeCode
	 */
	public void deleteStoreSize (String storesizeModuleId, String loginUserID) {
		StoreSize storesize = getStoreSize(storesizeModuleId);
		if (storesize != null) {
			storesize.setDeletionIndicator(1L);
			storesize.setUpdatedBy(loginUserID);
			storesize.setUpdatedOn(new Date());
			storeSizeRepository.save(storesize);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + storesizeModuleId);
		}
	}
}

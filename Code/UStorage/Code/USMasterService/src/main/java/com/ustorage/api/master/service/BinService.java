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

import com.ustorage.api.master.model.bin.AddBin;
import com.ustorage.api.master.model.bin.Bin;
import com.ustorage.api.master.model.bin.UpdateBin;
import com.ustorage.api.master.repository.BinRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BinService {
	
	@Autowired
	private BinRepository binRepository;
	
	public List<Bin> getBin () {
		List<Bin> binList =  binRepository.findAll();
		binList = binList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return binList;
	}
	
	/**
	 * getBin
	 * @param binId
	 * @return
	 */
	public Bin getBin (String binId) {
		Optional<Bin> bin = binRepository.findByCodeAndDeletionIndicator(binId, 0L);
		if (bin.isEmpty()) {
			return null;
		}
		return bin.get();
	}
	
	/**
	 * createBin
	 * @param newBin
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Bin createBin (AddBin newBin, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Bin dbBin = new Bin();
		BeanUtils.copyProperties(newBin, dbBin, CommonUtils.getNullPropertyNames(newBin));
		dbBin.setDeletionIndicator(0L);
		dbBin.setCreatedBy(loginUserId);
		dbBin.setUpdatedBy(loginUserId);
		dbBin.setCreatedOn(new Date());
		dbBin.setUpdatedOn(new Date());
		return binRepository.save(dbBin);
	}
	
	/**
	 * updateBin
	 * @param binId
	 * @param loginUserId 
	 * @param updateBin
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Bin updateBin (String code, String loginUserId, UpdateBin updateBin)
			throws IllegalAccessException, InvocationTargetException {
		Bin dbBin = getBin(code);
		BeanUtils.copyProperties(updateBin, dbBin, CommonUtils.getNullPropertyNames(updateBin));
		dbBin.setUpdatedBy(loginUserId);
		dbBin.setUpdatedOn(new Date());
		return binRepository.save(dbBin);
	}
	
	/**
	 * deleteBin
	 * @param loginUserID 
	 * @param binCode
	 */
	public void deleteBin (String binModuleId, String loginUserID) {
		Bin bin = getBin(binModuleId);
		if (bin != null) {
			bin.setDeletionIndicator(1L);
			bin.setUpdatedBy(loginUserID);
			bin.setUpdatedOn(new Date());
			binRepository.save(bin);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + binModuleId);
		}
	}
}

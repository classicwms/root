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

import com.ustorage.api.master.model.rentperiod.AddRentPeriod;
import com.ustorage.api.master.model.rentperiod.RentPeriod;
import com.ustorage.api.master.model.rentperiod.UpdateRentPeriod;
import com.ustorage.api.master.repository.RentPeriodRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RentPeriodService {
	
	@Autowired
	private RentPeriodRepository rentPeriodRepository;
	
	public List<RentPeriod> getRentPeriod () {
		List<RentPeriod> rentPeriodList =  rentPeriodRepository.findAll();
		rentPeriodList = rentPeriodList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return rentPeriodList;
	}
	
	/**
	 * getRentPeriod
	 * @param rentPeriodId
	 * @return
	 */
	public RentPeriod getRentPeriod (String rentPeriodId) {
		Optional<RentPeriod> rentPeriod = rentPeriodRepository.findByCodeAndDeletionIndicator(rentPeriodId, 0L);
		if (rentPeriod.isEmpty()) {
			return null;
		}
		return rentPeriod.get();
	}
	
	/**
	 * createRentPeriod
	 * @param newRentPeriod
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RentPeriod createRentPeriod (AddRentPeriod newRentPeriod, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		RentPeriod dbRentPeriod = new RentPeriod();
		BeanUtils.copyProperties(newRentPeriod, dbRentPeriod, CommonUtils.getNullPropertyNames(newRentPeriod));
		dbRentPeriod.setDeletionIndicator(0L);
		dbRentPeriod.setCreatedBy(loginUserId);
		dbRentPeriod.setUpdatedBy(loginUserId);
		dbRentPeriod.setCreatedOn(new Date());
		dbRentPeriod.setUpdatedOn(new Date());
		return rentPeriodRepository.save(dbRentPeriod);
	}
	
	/**
	 * updateRentPeriod
	 * @param rentPeriodId
	 * @param loginUserId 
	 * @param updateRentPeriod
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RentPeriod updateRentPeriod (String code, String loginUserId, UpdateRentPeriod updateRentPeriod)
			throws IllegalAccessException, InvocationTargetException {
		RentPeriod dbRentPeriod = getRentPeriod(code);
		BeanUtils.copyProperties(updateRentPeriod, dbRentPeriod, CommonUtils.getNullPropertyNames(updateRentPeriod));
		dbRentPeriod.setUpdatedBy(loginUserId);
		dbRentPeriod.setUpdatedOn(new Date());
		return rentPeriodRepository.save(dbRentPeriod);
	}
	
	/**
	 * deleteRentPeriod
	 * @param loginUserID 
	 * @param rentperiodCode
	 */
	public void deleteRentPeriod (String rentperiodModuleId, String loginUserID) {
		RentPeriod rentperiod = getRentPeriod(rentperiodModuleId);
		if (rentperiod != null) {
			rentperiod.setDeletionIndicator(1L);
			rentperiod.setUpdatedBy(loginUserID);
			rentperiod.setUpdatedOn(new Date());
			rentPeriodRepository.save(rentperiod);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + rentperiodModuleId);
		}
	}
}

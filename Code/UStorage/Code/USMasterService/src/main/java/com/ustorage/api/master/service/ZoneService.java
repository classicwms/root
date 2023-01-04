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

import com.ustorage.api.master.model.zone.AddZone;
import com.ustorage.api.master.model.zone.Zone;
import com.ustorage.api.master.model.zone.UpdateZone;
import com.ustorage.api.master.repository.ZoneRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZoneService {
	
	@Autowired
	private ZoneRepository zoneRepository;
	
	public List<Zone> getZone () {
		List<Zone> zoneList =  zoneRepository.findAll();
		zoneList = zoneList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return zoneList;
	}
	
	/**
	 * getZone
	 * @param zoneId
	 * @return
	 */
	public Zone getZone (String zoneId) {
		Optional<Zone> zone = zoneRepository.findByCodeAndDeletionIndicator(zoneId, 0L);
		if (zone.isEmpty()) {
			return null;
		}
		return zone.get();
	}
	
	/**
	 * createZone
	 * @param newZone
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Zone createZone (AddZone newZone, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Zone dbZone = new Zone();
		BeanUtils.copyProperties(newZone, dbZone, CommonUtils.getNullPropertyNames(newZone));
		dbZone.setDeletionIndicator(0L);
		dbZone.setCreatedBy(loginUserId);
		dbZone.setUpdatedBy(loginUserId);
		dbZone.setCreatedOn(new Date());
		dbZone.setUpdatedOn(new Date());
		return zoneRepository.save(dbZone);
	}
	
	/**
	 * updateZone
	 * @param zoneId
	 * @param loginUserId 
	 * @param updateZone
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Zone updateZone (String code, String loginUserId, UpdateZone updateZone)
			throws IllegalAccessException, InvocationTargetException {
		Zone dbZone = getZone(code);
		BeanUtils.copyProperties(updateZone, dbZone, CommonUtils.getNullPropertyNames(updateZone));
		dbZone.setUpdatedBy(loginUserId);
		dbZone.setUpdatedOn(new Date());
		return zoneRepository.save(dbZone);
	}
	
	/**
	 * deleteZone
	 * @param loginUserID 
	 * @param zoneCode
	 */
	public void deleteZone (String zoneModuleId, String loginUserID) {
		Zone zone = getZone(zoneModuleId);
		if (zone != null) {
			zone.setDeletionIndicator(1L);
			zone.setUpdatedBy(loginUserID);
			zone.setUpdatedOn(new Date());
			zoneRepository.save(zone);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + zoneModuleId);
		}
	}
}

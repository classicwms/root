package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.dockid.AddDockId;
import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.dockid.UpdateDockId;
import com.tekclover.wms.api.idmaster.repository.DockIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DockIdService extends BaseService {
	
	@Autowired
	private DockIdRepository dockIdRepository;
	
	/**
	 * getDockIds
	 * @return
	 */
	public List<DockId> getDockIds () {
		List<DockId> dockIdList =  dockIdRepository.findAll();
		dockIdList = dockIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return dockIdList;
	}
	
	/**
	 * getDockId
	 * @param dockId
	 * @return
	 */
	public DockId getDockId (String warehouseId, String dockId) {
		Optional<DockId> dbDockId = 
				dockIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDockIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								dockId,
								getLanguageId(),
								0L
								);
		if (dbDockId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"dockId - " + dockId +
						" doesn't exist.");
			
		} 
		return dbDockId.get();
	}
	
	/**
	 * createDockId
	 * @param newDockId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DockId createDockId (AddDockId newDockId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		DockId dbDockId = new DockId();
		log.info("newDockId : " + newDockId);
		BeanUtils.copyProperties(newDockId, dbDockId, CommonUtils.getNullPropertyNames(newDockId));
		dbDockId.setCompanyCodeId(getCompanyCode());
		dbDockId.setPlantId(getPlantId());
		dbDockId.setDeletionIndicator(0L);
		dbDockId.setCreatedBy(loginUserID);
		dbDockId.setUpdatedBy(loginUserID);
		dbDockId.setCreatedOn(new Date());
		dbDockId.setUpdatedOn(new Date());
		return dockIdRepository.save(dbDockId);
	}
	
	/**
	 * updateDockId
	 * @param loginUserID
	 * @param dockId
	 * @param updateDockId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DockId updateDockId (String warehouseId, String dockId, String loginUserID,
			UpdateDockId updateDockId) 
			throws IllegalAccessException, InvocationTargetException {
		DockId dbDockId = getDockId( warehouseId, dockId);
		BeanUtils.copyProperties(updateDockId, dbDockId, CommonUtils.getNullPropertyNames(updateDockId));
		dbDockId.setUpdatedBy(loginUserID);
		dbDockId.setUpdatedOn(new Date());
		return dockIdRepository.save(dbDockId);
	}
	
	/**
	 * deleteDockId
	 * @param loginUserID 
	 * @param dockId
	 */
	public void deleteDockId (String warehouseId, String dockId, String loginUserID) {
		DockId dbDockId = getDockId( warehouseId, dockId);
		if ( dbDockId != null) {
			dbDockId.setDeletionIndicator(1L);
			dbDockId.setUpdatedBy(loginUserID);
			dockIdRepository.save(dbDockId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dockId);
		}
	}
}

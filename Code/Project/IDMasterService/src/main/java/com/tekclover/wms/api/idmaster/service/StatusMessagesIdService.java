package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.AddStatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.StatusMessagesId;
import com.tekclover.wms.api.idmaster.model.statusmessagesid.UpdateStatusMessagesId;
import com.tekclover.wms.api.idmaster.repository.StatusMessagesIdRepository;
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
public class StatusMessagesIdService extends BaseService {
	
	@Autowired
	private StatusMessagesIdRepository statusMessagesIdRepository;
	
	/**
	 * getStatusMessagesIds
	 * @return
	 */
	public List<StatusMessagesId> getStatusMessagesIds () {
		List<StatusMessagesId> statusMessagesIdList =  statusMessagesIdRepository.findAll();
		statusMessagesIdList = statusMessagesIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return statusMessagesIdList;
	}
	
	/**
	 * getStatusMessagesId
	 * @param messagesId
	 * @return
	 */
	public StatusMessagesId getStatusMessagesId (String messagesId, String languageId, String messageType) {
		Optional<StatusMessagesId> dbStatusMessagesId = 
				statusMessagesIdRepository.findByMessageIdAndLanguageIdAndMessageTypeAndDeletionIndicator(
								messagesId,
								languageId,
								messageType,
								0L
								);
		if (dbStatusMessagesId.isEmpty()) {
			throw new BadRequestException("The given values : " +
						"messagesId - " + messagesId +
						"languageId - " + languageId +
						"messagesType - " + messageType +
						" doesn't exist.");
			
		} 
		return dbStatusMessagesId.get();
	}
	
	/**
	 * createStatusMessagesId
	 * @param newStatusMessagesId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StatusMessagesId createStatusMessagesId (AddStatusMessagesId newStatusMessagesId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		StatusMessagesId dbStatusMessagesId = new StatusMessagesId();
		log.info("newStatusMessagesId : " + newStatusMessagesId);
		BeanUtils.copyProperties(newStatusMessagesId, dbStatusMessagesId, CommonUtils.getNullPropertyNames(newStatusMessagesId));
		dbStatusMessagesId.setDeletionIndicator(0L);
		dbStatusMessagesId.setCreatedBy(loginUserID);
		dbStatusMessagesId.setUpdatedBy(loginUserID);
		dbStatusMessagesId.setCreatedOn(new Date());
		dbStatusMessagesId.setUpdatedOn(new Date());
		return statusMessagesIdRepository.save(dbStatusMessagesId);
	}
	
	/**
	 * updateStatusMessagesId
	 * @param loginUserID
	 * @param messagesId
	 * @param updateStatusMessagesId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StatusMessagesId updateStatusMessagesId (String messagesId, String languageId, String messageType, String loginUserID,
			UpdateStatusMessagesId updateStatusMessagesId) 
			throws IllegalAccessException, InvocationTargetException {
		StatusMessagesId dbStatusMessagesId = getStatusMessagesId(messagesId, languageId, messageType);
		BeanUtils.copyProperties(updateStatusMessagesId, dbStatusMessagesId, CommonUtils.getNullPropertyNames(updateStatusMessagesId));
		dbStatusMessagesId.setUpdatedBy(loginUserID);
		dbStatusMessagesId.setUpdatedOn(new Date());
		return statusMessagesIdRepository.save(dbStatusMessagesId);
	}
	
	/**
	 * deleteStatusMessagesId
	 * @param loginUserID 
	 * @param messagesId
	 */
	public void deleteStatusMessagesId (String messagesId, String languageId, String messageType, String loginUserID) {
		StatusMessagesId dbStatusMessagesId = getStatusMessagesId(messagesId, languageId, messageType);
		if ( dbStatusMessagesId != null) {
			dbStatusMessagesId.setDeletionIndicator(1L);
			dbStatusMessagesId.setUpdatedBy(loginUserID);
			statusMessagesIdRepository.save(dbStatusMessagesId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + messagesId);
		}
	}
}

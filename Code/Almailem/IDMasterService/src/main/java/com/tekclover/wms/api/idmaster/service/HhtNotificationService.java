package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.dockid.AddDockId;
import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotificationToken;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.HhtNotificationRepository;
import com.tekclover.wms.api.idmaster.repository.HhtNotificationTokenRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
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

@Slf4j
@Service
public class HhtNotificationService {

	@Autowired
	private HhtNotificationRepository hhtNotificationRepository;

	@Autowired
	private HhtNotificationTokenRepository hhtNotificationTokenRepository;

	public HhtNotification createHhtNotification (HhtNotification newHhtNotification, String loginUserID) {

		HhtNotification dbHhtNotification =
				hhtNotificationRepository.findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
						newHhtNotification.getCompanyId(),
						newHhtNotification.getPlantId(),
						newHhtNotification.getWarehouseId(),
						newHhtNotification.getLanguageId(),
						newHhtNotification.getDeviceId(),
						newHhtNotification.getUserId(),
						newHhtNotification.getTokenId(),
						0L
				);

		if(dbHhtNotification != null) {
			dbHhtNotification.setDeletionIndicator(1L);
			dbHhtNotification.setUpdatedOn(new Date());
			dbHhtNotification.setUpdatedBy(loginUserID);
			hhtNotificationRepository.save(dbHhtNotification);
			return dbHhtNotification;
//			throw new BadRequestException (newHhtNotification.getTokenId() + " Deleted Successfully");
		}else {
			newHhtNotification.setDeletionIndicator(0L);
			newHhtNotification.setCreatedBy(loginUserID);
			newHhtNotification.setUpdatedBy(loginUserID);
			newHhtNotification.setCreatedOn(new Date());
			newHhtNotification.setUpdatedOn(new Date());
			newHhtNotification.setNotificationHeaderId(System.currentTimeMillis());

//		if(newHhtNotification.getHhtNotificationTokens() != null && !newHhtNotification.getHhtNotificationTokens().isEmpty()){
//			for(HhtNotificationToken hhtNotificationToken : newHhtNotification.getHhtNotificationTokens()) {
//				hhtNotificationToken.setNotificationHeaderId(newHhtNotification.getNotificationHeaderId());
//				hhtNotificationToken.setDeletionIndicator(0L);
//				hhtNotificationToken.setCreatedBy(loginUserID);
//				hhtNotificationToken.setUpdatedBy(loginUserID);
//				hhtNotificationToken.setCreatedOn(new Date());
//				hhtNotificationToken.setUpdatedOn(new Date());
//			}
//		}

			return hhtNotificationRepository.save(newHhtNotification);
		}

	}

	public HhtNotification getHhtNotification (String warehouseId, String companyId,String languageId,String plantId, String deviceId, String userId,String tokenId ) {
		HhtNotification dbHhtNotification =
				hhtNotificationRepository.findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
						companyId,
						plantId,
						warehouseId,
						languageId,
						deviceId,
						userId,
						tokenId,
						0L
				);
		if(dbHhtNotification != null){
			return dbHhtNotification;
		}else {
			throw new BadRequestException("No User Found");
		}
	}





}

package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import com.tekclover.wms.api.idmaster.repository.HhtNotificationRepository;
import com.tekclover.wms.api.idmaster.repository.HhtNotificationTokenRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class HhtNotificationService {

	@Autowired
	private HhtNotificationRepository hhtNotificationRepository;
	@Autowired
	private HhtNotificationTokenRepository hhtNotificationTokenRepository;


	//Create HhtNotification
	public HhtNotification createHhtNotification (HhtNotification newHhtNotification, String loginUserID) {

        HhtNotification newHht = new HhtNotification();
        Optional<HhtNotification> optionalDbHhtNotification =
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

        if (optionalDbHhtNotification.isPresent()) {
            HhtNotification dbHhtNotification = optionalDbHhtNotification.get();
			dbHhtNotification.setDeletionIndicator(1L);
			dbHhtNotification.setUpdatedOn(new Date());
			dbHhtNotification.setUpdatedBy(loginUserID);
			hhtNotificationRepository.save(dbHhtNotification);

            if (!newHhtNotification.getIsLoggedIn()) {
			return dbHhtNotification;
		}
        }
        BeanUtils.copyProperties(newHhtNotification, newHht, CommonUtils.getNullPropertyNames(newHhtNotification));
        newHht.setDeletionIndicator(0L);
        newHht.setCreatedBy(loginUserID);
        newHht.setUpdatedBy(loginUserID);
        newHht.setCreatedOn(new Date());
        newHht.setUpdatedOn(new Date());
        newHht.setNotificationHeaderId(System.currentTimeMillis());
        return hhtNotificationRepository.save(newHht);
	}

    // Get HhtNotification
	public HhtNotification getHhtNotification (String warehouseId, String companyId,String languageId,String plantId, String deviceId, String userId,String tokenId ) {
        Optional<HhtNotification> dbHhtNotification =
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
        if (dbHhtNotification.isPresent()) {
            return dbHhtNotification.get();
		}else {
			throw new BadRequestException("No User Found");
		}
	}


}

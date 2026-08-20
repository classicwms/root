package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.repository.PickupLineV2Repository;
import com.tekclover.wms.api.outbound.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.outbound.transaction.repository.StorageBinV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

	@Autowired
	StagingLineV2Repository stagingLineV2Repository;

	@Autowired
	StorageBinV2Repository storageBinRepository;

	@Autowired
	PickupLineV2Repository pickupLineV2Repository;



	@Cacheable(
			value = "statusDescription",
			key = "#statusId + '_' + #languageId"
	)
	public String getStatusDescription(Long statusId, String languageId) {
		log.info("Fetching status description for statusId: {} and languageId: {} from database", statusId, languageId);
		return stagingLineV2Repository.getStatusDescription(statusId, languageId);
	}

}
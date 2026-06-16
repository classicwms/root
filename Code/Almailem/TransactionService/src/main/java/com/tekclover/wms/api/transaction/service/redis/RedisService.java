package com.tekclover.wms.api.transaction.service.redis;

import com.tekclover.wms.api.transaction.model.DescriptionDTO;
import com.tekclover.wms.api.transaction.model.IDescriptionProjection;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

	@Autowired
	StagingLineV2Repository stagingLineV2Repository;


	@Cacheable(value = "statusDescription", key = "#statusId + '_' + #languageId")
	public String getStatusDescription(Long statusId, String languageId) {
		log.info("Fetching status description for statusId: {} and languageId: {} from database", statusId, languageId);
		return stagingLineV2Repository.getStatusDescription(statusId, languageId);
	}

	@Cacheable(
			value = "descriptionCache",
			key = "#companyId + '-' + #languageId + '-' + #plantId + '-' + #warehouseId")
	public DescriptionDTO getDescription(String companyId, String languageId, String plantId, String warehouseId) {

		log.info(" CACHE MISS - Fetching from DB: {}-{}-{}-{}",
				companyId, languageId, plantId, warehouseId);

		IDescriptionProjection p = stagingLineV2Repository.getDescriptionRedis(companyId, languageId, plantId, warehouseId);
		return new DescriptionDTO(
				p.getCompanyDesc(),
				p.getPlantDesc(),
				p.getWarehouseDesc()
		);
	}
}
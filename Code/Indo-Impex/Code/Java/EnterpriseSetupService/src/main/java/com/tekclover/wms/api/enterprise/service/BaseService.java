package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.repository.BatchSerialRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
	
	protected static final String WAREHOUSE_ID = "110";
	
	/**
	 * 
	 * @return
	 */
	protected String getLanguageId () {
		return "EN";
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getCompanyCode () {
		return "1000";
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getPlantId () {
		return "1001";
	}

	@Autowired
	BatchSerialRepository batchSerialRepository;

	/**
	 *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @return
	 */
	protected IkeyValuePair getDescription(String companyCodeId, String plantId, String languageId, String warehouseId, Long levelId) {
        return batchSerialRepository.getIdDescription(companyCodeId, plantId, languageId, warehouseId, levelId);
	}
}
package com.tekclover.wms.api.idmaster.service;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
	
	@Autowired
	private NumberRangeService numberRangeService;
	
	@Autowired
	private WarehouseService warehouseService;
	
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
	
	/**
	 * 
	 * @param NUM_RAN_CODE
	 * @param warehouseId
	 * @param accessToken
	 * @return
	 */
	protected String getNextRangeNumber (long NUM_RAN_CODE, String warehouseId,
										 String companyCodeId,String plantId,
										 String languageId,String accessToken) {
		long FISCALYEAR = Year.now().getValue();		
		String nextNumberRange = numberRangeService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, companyCodeId, plantId, languageId);
		return nextNumberRange;
	}
}

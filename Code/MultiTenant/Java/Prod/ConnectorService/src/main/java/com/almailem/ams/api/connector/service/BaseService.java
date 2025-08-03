package com.almailem.ams.api.connector.service;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;

import com.almailem.ams.api.connector.model.auth.AuthToken;

public class BaseService {
	
	protected static final String WAREHOUSE_ID_110 = "110";
	protected static final String WAREHOUSE_ID_111 = "111";
	
	@Autowired
	protected IDMasterService idmasterService;
	
	@Autowired
	protected AuthTokenService authTokenService;
	
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
	protected String getNextRangeNumber (long NUM_RAN_CODE, String warehouseId, String accessToken) {
		int FISCALYEAR = Year.now().getValue();		
		String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, accessToken);
		return nextNumberRange;
	}
	
	/**
	 * 
	 * @param NUM_RAN_CODE
	 * @param warehouseId
	 * @return
	 */
	protected String getNextRangeNumber (long NUM_RAN_CODE, String warehouseId) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		int FISCALYEAR = Year.now().getValue();		
		String nextNumberRange = 
				idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, authTokenForIDMasterService.getAccess_token());
		return nextNumberRange;
	}
	
	//--------------------------------------------------------------------------------------------------------
	/**
	 *
	 * @param NUM_RAN_CODE
	 * @param warehouseId
	 * @param accessToken
	 * @return
	 */
	protected String getNextRangeNumber (Long NUM_RAN_CODE, String companyCodeId, String plantId,
										 String languageId, String warehouseId, String accessToken) {
		String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, accessToken);
		return nextNumberRange;
	}
}

package com.tekclover.wms.api.masters.service;

import java.time.Year;

import com.tekclover.wms.api.masters.model.warehouse.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;

import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.dto.UserManagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BaseService {
	
	@Autowired
	private IDMasterService idmasterService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
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
	protected String getNextRangeNumber (Long NUM_RAN_CODE, String warehouseId,
										 String companyCodeId,String plantId,
										 String languageId, String accessToken) {
		int FISCALYEAR = Year.now().getValue();
		String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, companyCodeId, plantId, languageId, accessToken);
		return nextNumberRange;
	}
	
	/**
	 * 
	 * @param NUM_RAN_CODE
	 * @param warehouseId
	 * @return
	 */
	protected String getNextRangeNumber (Long NUM_RAN_CODE, String warehouseId,
										 String companyCodeId, String plantId,
										 String languageId) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		int FISCALYEAR = Year.now().getValue();
		log.info ("year : " + FISCALYEAR);
		String nextNumberRange = 
				idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId,companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
		return nextNumberRange;
	}
	
	/**
	 * 
	 * @param loginUserID
	 * @param accessToken
	 * @return
	 */
	protected UserManagement getUserManagement (String loginUserID, String accessToken) {
		UserManagement userManagement = idmasterService.getUserManagement(loginUserID, accessToken);
		return userManagement;
	}
	
	/**
	 * 
	 * @param loginUserID
	 * @return
	 */
	protected UserManagement getUserManagement (String loginUserID) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		UserManagement userManagement = 
				idmasterService.getUserManagement(loginUserID, authTokenForIDMasterService.getAccess_token());
		return userManagement;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	protected Warehouse getWarehouse (String warehouseId) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		return idmasterService.getWarehouse(warehouseId, authTokenForIDMasterService.getAccess_token());
	}

	/**
	 *
	 * @param plantId plantId
	 * @return
	 */
	public String getDataBase(String plantId) {

		if (plantId.equalsIgnoreCase("1207")) {
			return "MDU";
		} else if (plantId.equalsIgnoreCase("1204")) {
			return "CMP";
		} else if (plantId.equalsIgnoreCase("1203")) {
			return "CHN";
		} else {
			return "WK";
		}
	}
}

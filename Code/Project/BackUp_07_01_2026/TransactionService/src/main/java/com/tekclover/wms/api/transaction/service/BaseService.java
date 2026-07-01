package com.tekclover.wms.api.transaction.service;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;

import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;

public class BaseService {
	
	protected static final String WAREHOUSE_ID_110 = "110";
	protected static final String WAREHOUSE_ID_111 = "111";
	
	protected static final String OUT_OF_STOCK = "Out of Stock";
	protected static final String OUTOF_STOCK = "Outof Stock";
	protected static final String OUTOFSTOCK = "OutofStock";
	protected static final String SHORT_QTY = "Short Qty";
	protected static final String DAMAGE = "Damage";
	protected static final String AISLE_BLOCK = "Aisle Block";
	protected static final String NON_PACK_QTY = "Non-Pack Qty";

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
	
	/**
	 * 
	 * @param loginUserID
	 * @param accessToken
	 * @return
	 */
	protected UserManagement getUserManagement (String loginUserID, String warehouseId, String accessToken) {
		UserManagement userManagement = idmasterService.getUserManagement(loginUserID, warehouseId, accessToken);
		return userManagement;
	}
	
	/**
	 * 
	 * @param loginUserID
	 * @return
	 */
	protected UserManagement getUserManagement (String loginUserID, String warehouseId) {
		AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
		UserManagement userManagement = 
				idmasterService.getUserManagement(loginUserID, warehouseId, authTokenForIDMasterService.getAccess_token());
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
}

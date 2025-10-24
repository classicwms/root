package com.almailem.ams.api.connector.service;

import java.time.Year;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import com.almailem.ams.api.connector.model.auth.AuthToken;

@Slf4j
public class BaseService {
	
	protected static final String WAREHOUSE_ID_100 = "100";
	protected static final String WAREHOUSE_ID_200 = "200";
	
	@Autowired
	protected IDMasterService idmasterService;
	
	@Autowired
	protected AuthTokenService authTokenService;
	
	@Autowired
	PropertiesConfig propertiesConfig;

	String almailemPrimaryCompanyCode = "21";
	String almailemPrimaryPlant = "222";
	String almailemSecondaryCompanyCode = "23";
	String almailemSecondaryPlant = "125";

	String amgharaPrimaryCompanyCode = "21";
	String amgharaPrimaryPlant = "212";
	String amgharaSecondaryCompanyCode = "23";
	String amgharaSecondaryPlant = "115";

	public String getAmgharaTransactionServiceApiUrl() { 
		return propertiesConfig.getAmgharaTransactionServiceUrl(); 
	}
	public String getAmgharaMasterServiceApiUrl() {
		return propertiesConfig.getAmgharaMastersServiceUrl();
	}

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

	//=============================================================================================================

	/**
	 *
	 * @param companyCode
	 * @param plantId
	 * @return
	 */
	public String validateWarehouse(String companyCode, String plantId) {
		log.info("CompanyCode, PlantId : " + companyCode + "|" + plantId);
        try {
            if ((almailemPrimaryCompanyCode.equalsIgnoreCase(companyCode) && almailemPrimaryPlant.equalsIgnoreCase(plantId)) ||
                    (almailemSecondaryCompanyCode.equalsIgnoreCase(companyCode) && almailemSecondaryPlant.equalsIgnoreCase(plantId))) {
				log.info("WH: " + WAREHOUSE_ID_200);
                return WAREHOUSE_ID_200;
            }
            if ((amgharaPrimaryCompanyCode.equalsIgnoreCase(companyCode) && amgharaPrimaryPlant.equalsIgnoreCase(plantId)) ||
                    (amgharaSecondaryCompanyCode.equalsIgnoreCase(companyCode) && amgharaSecondaryPlant.equalsIgnoreCase(plantId))) {
				log.info("WH: " + WAREHOUSE_ID_100);
                return WAREHOUSE_ID_100;
            }
            return null;
        } catch (Exception e) {
            throw new BadRequestException("get warehouse exception " + e.getLocalizedMessage());
        }
    }
}
package com.tekclover.wms.api.masters.enterprise.service;

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
}

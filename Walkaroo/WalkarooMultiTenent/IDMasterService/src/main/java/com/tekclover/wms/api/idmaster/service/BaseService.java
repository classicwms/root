package com.tekclover.wms.api.idmaster.service;

import java.time.Year;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {

	@Autowired
	NumberRangeService numberRangeService;

	@Autowired
	WarehouseService warehouseService;

	@Autowired
	CompanyIdRepository companyIdRepository;

	IKeyValuePair description = null;

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
		} else {
			return "WK";
		}
	}

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    protected IKeyValuePair getDescription (String companyCodeId,String plantId, String languageId, String warehouseId) {
        return companyIdRepository.getDescription(companyCodeId, plantId, languageId, warehouseId);
    }
}

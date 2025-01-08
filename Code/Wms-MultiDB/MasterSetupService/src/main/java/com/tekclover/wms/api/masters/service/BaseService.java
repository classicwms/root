package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.dto.UserManagement;
import com.tekclover.wms.api.masters.model.dto.Warehouse;
import com.tekclover.wms.api.masters.repository.ImBasicData1V2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;

@Slf4j
public class BaseService {

    @Autowired
    IDMasterService idmasterService;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    protected IKeyValuePair description = null;

    /**
     * @return
     */
    protected String getLanguageId() {
        return "EN";
    }

    /**
     * @return
     */
    protected String getCompanyCode() {
        return "1000";
    }

    /**
     * @return
     */
    protected String getPlantId() {
        return "1001";
    }

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @param accessToken
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String warehouseId,
                                        String companyCodeId, String plantId,
                                        String languageId, String accessToken) {
        int FISCALYEAR = Year.now().getValue();
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, companyCodeId, plantId, languageId, accessToken);
        return nextNumberRange;
    }

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String warehouseId,
                                        String companyCodeId, String plantId,
                                        String languageId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        int FISCALYEAR = Year.now().getValue();
        log.info("year : " + FISCALYEAR);
        String nextNumberRange =
                idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
        return nextNumberRange;
    }

    /**
     * @param loginUserID
     * @param accessToken
     * @return
     */
    protected UserManagement getUserManagement(String loginUserID, String accessToken) {
        UserManagement userManagement = idmasterService.getUserManagement(loginUserID, accessToken);
        return userManagement;
    }

    /**
     * @param loginUserID
     * @return
     */
    protected UserManagement getUserManagement(String loginUserID) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        UserManagement userManagement =
                idmasterService.getUserManagement(loginUserID, authTokenForIDMasterService.getAccess_token());
        return userManagement;
    }

    /**
     * @param warehouseId
     * @return
     */
    protected Warehouse getWarehouse(String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        return idmasterService.getWarehouse(warehouseId, authTokenForIDMasterService.getAccess_token());
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemTypeId
     * @return
     */
    public String getItemTypeDesc(String companyCodeId, String plantId, String languageId, String warehouseId, Long itemTypeId) {
        return imBasicData1V2Repository.getItemTypeDescription(companyCodeId, plantId, languageId, warehouseId, itemTypeId);
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemGroupId
     * @return
     */
    public String getItemGroupDesc(String companyCodeId, String plantId, String languageId, String warehouseId, Long itemGroupId) {
        return imBasicData1V2Repository.getItemGroupDescription(companyCodeId, plantId, languageId, warehouseId, itemGroupId);
    }

    /**
     * Get company, plant, warehouse description
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public IKeyValuePair getDescription(String companyCodeId, String plantId, String languageId, String warehouseId) {
        return imBasicData1V2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
    }
}
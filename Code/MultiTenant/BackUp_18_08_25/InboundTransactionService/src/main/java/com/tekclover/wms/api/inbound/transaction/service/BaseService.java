package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.dto.AlternateUomImpl;
import com.tekclover.wms.api.inbound.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.inbound.transaction.model.dto.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

public class BaseService {

    protected Long NUMBER_RANGE_CODE = 0L;

    protected String numberRangeId = null;
    protected IKeyValuePair description = null;
    protected String MW_AMS = "MW_AMS";
    protected String statusDescription = null;
    protected static final String LANG_ID = "EN";
    protected static final String MFR_NAME = "NAMRATHA";
    protected static final String MFR_NAME_V5 = "REEFERON";
    protected static final String ACTIVE = "Active";
    protected static final String IN_ACTIVE = "InActive";
    protected String stockTypeDesc = null;
    protected static final String WAREHOUSE_ID_110 = "110";
    protected static final String WAREHOUSE_ID_111 = "111";
    //V2
    protected static final String WAREHOUSE_ID_100 = "100";
    protected static final String WAREHOUSE_ID_200 = "200";


    protected static final String PACK_BARCODE = "99999";

    @Autowired
    protected IDMasterService idmasterService;

    @Autowired
    protected AuthTokenService authTokenService;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

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

    protected String getMasterAuthToken() {
        return authTokenService.getMastersServiceAuthToken().getAccess_token();
    }

    protected String getIdMasterAuthToken() {
        return authTokenService.getIDMasterServiceAuthToken().getAccess_token();
    }


    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public IKeyValuePair getDescription(String companyCodeId, String plantId, String languageId, String warehouseId) {
        return stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
    }

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @param accessToken
     * @return
     */
    protected String getNextRangeNumber(long NUM_RAN_CODE, String warehouseId, String accessToken) {
        int FISCALYEAR = Year.now().getValue();
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, accessToken);
        return nextNumberRange;
    }

    /**
     *
     * @param status
     * @return
     */
    public String getStatus(Long status) {
        return status != null ? status == 0 ? ACTIVE : IN_ACTIVE : null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param uomText
     * @return
     */
    public AlternateUomImpl getUom(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String uomText) {
        uomText = uomText.trim().toUpperCase().replaceAll("\\s+", "");
        return stagingLineV2Repository.getAlternateUomQuantity(companyCodeId, plantId, languageId, warehouseId, itemCode, uomText);
    }

    /**
     *
     * @param value
     * @return
     */
    protected static double round(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    /**
     *
     * @param value
     * @return
     */
    protected static double round1(Double value) {
        if(value == null) {
            return 0.0;
        }
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public IKeyValuePair getItemTypeAndDesc(String companyCodeId, String plantId, String languageId,
                                            String warehouseId, String itemCode) {
        return stagingLineV2Repository.getItemTypeAndDescription(companyCodeId, plantId, languageId, warehouseId, itemCode);
    }

    /**
     *
     * @param value
     * @return
     */
    protected static double roundUp(Double value) {
        return Math.ceil(value);
    }
    /**
     * @param statusId
     * @param languageId
     * @return
     */
    public String getStatusDescription(Long statusId, String languageId) {
        return stagingLineV2Repository.getStatusDescription(statusId, languageId);
    }
    /**
     * ID Master AuthToken
     * @return
     */
    protected String getIDMasterAuthToken() {
        return authTokenService.getIDMasterServiceAuthToken().getAccess_token();
    }


    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @return
     */
    protected String getNextRangeNumber(long NUM_RAN_CODE, String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        int FISCALYEAR = Year.now().getValue();
        String nextNumberRange =
                idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, authTokenForIDMasterService.getAccess_token());
        return nextNumberRange;
    }

    /**
     * @param loginUserID
     * @param accessToken
     * @return
     */
    protected UserManagement getUserManagement(String loginUserID, String warehouseId, String accessToken) {
        UserManagement userManagement = idmasterService.getUserManagement(loginUserID, warehouseId, accessToken);
        return userManagement;
    }

    /**
     * @param loginUserID
     * @return
     */
    protected UserManagement getUserManagement(String loginUserID, String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        UserManagement userManagement =
                idmasterService.getUserManagement(loginUserID, warehouseId, authTokenForIDMasterService.getAccess_token());
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

    //--------------------------------------------------------------------------------------------------------

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @param accessToken
     * @return
     */
    protected synchronized String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
                                        String languageId, String warehouseId, String accessToken) {
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, accessToken);
        return nextNumberRange;
    }

    /**
     * @param NUM_RAN_CODE
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
                                        String languageId, String warehouseId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
        return nextNumberRange;
    }

    /**
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    protected Warehouse getWarehouse(String warehouseId, String companyCodeId, String plantId, String languageId) {
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        return idmasterService.getWarehouse(warehouseId, companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeDesc(String companyCode, String plantId, String languageId, String warehouseId, Long referenceDocumentTypeId) {
        String referenceDocumentType = stagingLineV2Repository.getInboundOrderTypeDescription(referenceDocumentTypeId,companyCode, plantId, languageId, warehouseId);
        return referenceDocumentType;
    }

    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeDesc(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "SupplierInvoice";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "SalesReturn"; //sale return -7(Bin Class Id)
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "Non-WMS to WMS"; //b2b
        }
        if (referenceDocumentTypeId == 4) {
            referenceDocumentType = "WMS to WMS"; //iwt
        }
        if (referenceDocumentTypeId == 5) {
            referenceDocumentType = "DirectReceipt";
        }

        return referenceDocumentType;
    }

    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getOutboundOrderTypeDesc(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 0) {
            referenceDocumentType = "WMS to Non-WMS";
        }
        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "WMS to WMS";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "PURCHASE RETURN";
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "PICK LIST";
        }

        return referenceDocumentType;
    }
    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeTable(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "SUPPLIERINVOICEHEADER";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "SALESRETURNHEADER"; //sale return -7(Bin Class Id)
        }
        if (referenceDocumentTypeId == 3 || referenceDocumentTypeId == 4) {
            referenceDocumentType = "TRANSFERINHEADER"; //b2b
        }
        if (referenceDocumentTypeId == 5) {
            referenceDocumentType = "STOCKRECEIPTHEADER";
        }

        return referenceDocumentType;
    }

    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getOutboundOrderTypeTable(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 0 || referenceDocumentTypeId == 1) {
            referenceDocumentType = "TRANSFEROUTHEADER";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "PURCHASERETURNHEADER";
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "PICKLISTHEADER";
        }

        return referenceDocumentType;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param stockTypeId
     * @return
     */
    public String getStockTypeDesc(String companyCodeId, String plantId, String languageId,
                                   String warehouseId, Long stockTypeId) {

        String stockTypeDesc = stagingLineV2Repository.getStockTypeDescription(companyCodeId, plantId, languageId, warehouseId, stockTypeId);


        return stockTypeDesc;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param customerCode
     * @return
     */
    public String getCustomerName(String companyCodeId, String plantId, String languageId, String warehouseId, String customerCode) {
        return stagingLineV2Repository.getCustomerName(companyCodeId, plantId, languageId, warehouseId, customerCode);
    }
}
package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.AlternateUomImpl;
import com.tekclover.wms.api.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseService {

    protected Long NUMBER_RANGE_CODE = 0L;
    protected String numberRangeId = null;
    protected IKeyValuePair description = null;
    protected String MW_AMS = "MW_AMS";
    protected String statusDescription = null;
    protected String stockTypeDesc = null;
    protected static final String PACK_BARCODE = "99999";
    protected static final String LANG_ID = "EN";
    protected static final String MFR_NAME = "IMPEX";
    protected static final String PICK_HE_NO = "HE-01";
    protected static final String BASE_UOM = "ECH";
    protected static final String ALTERNATE_UOM = "BAG";
    protected static final String WAREHOUSE_ID_110 = "110";
    protected static final String WAREHOUSE_ID_111 = "111";
    //V2
    protected static final String WAREHOUSE_ID_100 = "100";
    protected static final String WAREHOUSE_ID_200 = "200";

    protected static final String COMPANY_CODE = "1001";        //Indus mega food company code
    protected static final String WK_COMPANY_CODE = "2000";        //Indus mega food company code
    protected static final Long IB_QH_ORD_TYP_ID = 6L;          //Indus quality header creation
    protected static final Long IB_FG_ORD_TYP_ID = 7L;         //Indus Inventory bin to bin create
    protected static final Long IB_SFG_ORD_TYP_ID = 8L;         //Indus Inventory bin to bin create
    protected static final Long IB_PO_ORD_TYP_ID = 5L;          //Production Order creation
    protected static final String OB_PL_ORD_TYP = "3";          //Production Order creation
    protected static final String OB_IPL_ORD_TYP_SFG = "5";          //Production Order create - semi-finished goods Picklist
    protected static final String OB_IPL_ORD_TYP_FG = "6";          //Production Order create-finished goods picklist
    protected static final Long OB_PL_ORD_TYP_ID = 3L;          //Production Order creation
    protected static final Long OB_IPL_ORD_TYP_ID_SFG = 5L;          //Production Order create - semi-finished goods Picklist
    protected static final Long OB_IPL_ORD_TYP_ID_FG = 6L;          //Production Order create-finished goods picklist

    protected static final String ST_SEC_ID_CS = "2";         //Indus raw material cold store storage section id
    protected static final String ST_SEC_ID_DS = "3";         //Indus raw material dry store storage section id
    protected static final String ST_SEC_ID_SFG = "2";         //Indus semi Finished goods storage section id   previous "4" now changed to "2" - cold store
    protected static final String ST_SEC_ID_FG = "5";         //Indus Finished goods storage section id
    protected static final String ST_SEC_ID_PSFG = "6";         //Indus semi Finished goods production storage section id
    protected static final String ST_SEC_ID_PFG = "7";         //Indus Finished goods production storage section id

    protected static final List<String> storageSectionIds  = new ArrayList<>(Arrays.asList("6","7"));         //Indus storage section id propose not in

    @Autowired
    protected IDMasterService idmasterService;

    @Autowired
    protected AuthTokenService authTokenService;

    @Autowired
    protected StagingLineV2Repository stagingLineV2Repository;

    /**
     * @return
     */
    protected String getLanguageId() {
        return LANG_ID;
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
     *
     * @return
     */
    protected String getMfrName(String companyCode) {
        if(companyCode.equalsIgnoreCase(COMPANY_CODE)) {
            return "1001";
        }
        return null;
    }

    /**
     * ID Master AuthToken
     * @return
     */
    protected String getIDMasterAuthToken() {
        return authTokenService.getIDMasterServiceAuthToken().getAccess_token();
    }

    /**
     * Master AuthToken
     * @return
     */
    protected String getMasterAuthToken() {
        return authTokenService.getMastersServiceAuthToken().getAccess_token();
    }

    /**
     * Enterprise AuthToken
     * @return
     */
    protected String getEnterpriseAuthToken() {
        return authTokenService.getEnterpriseSetupServiceAuthToken().getAccess_token();
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
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
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
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, getIDMasterAuthToken());
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
     * @param warehouseId
     * @return
     */
    public String getTransferNoV2(String companyCode, String plantId, String languageId, String warehouseId, String authToken) {
        String nextRangeNumber = getNextRangeNumber(8L, companyCode, plantId, languageId, warehouseId, authToken);
        return nextRangeNumber;
    }

    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeDesc(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
//            referenceDocumentType = "SupplierInvoice";
            referenceDocumentType = "Production Order";
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
//            referenceDocumentType = "DirectReceipt";
            referenceDocumentType = "Sub Contract";
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
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentTypeId
     * @return
     */
    public String getOutboundOrderTypeDesc(String companyCode, String plantId, String languageId, String warehouseId, Long referenceDocumentTypeId) {
        String referenceDocumentType = stagingLineV2Repository.getOutboundOrderTypeDescription(
                referenceDocumentTypeId,companyCode, plantId, languageId, warehouseId );
        return referenceDocumentType;
    }

    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeTable(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
//            referenceDocumentType = "SUPPLIERINVOICEHEADER";
            referenceDocumentType = "Production Order";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "SALESRETURNHEADER"; //sale return -7(Bin Class Id)
        }
        if (referenceDocumentTypeId == 3 || referenceDocumentTypeId == 4) {
            referenceDocumentType = "TRANSFERINHEADER"; //b2b
        }
        if (referenceDocumentTypeId == 5) {
//            referenceDocumentType = "STOCKRECEIPTHEADER";
            referenceDocumentType = "Sub Contract";
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
        if (referenceDocumentTypeId == 4) {
            referenceDocumentType = "SALESINVOICE";
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
        return stagingLineV2Repository.getStockTypeDescription(companyCodeId, plantId, languageId, warehouseId, stockTypeId);
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
        return stagingLineV2Repository.getItemTypeDescription(companyCodeId, plantId, languageId, warehouseId, itemTypeId);
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
        return stagingLineV2Repository.getItemGroupDescription(companyCodeId, plantId, languageId, warehouseId, itemGroupId);
    }

    /**
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
    protected static double round(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
     * Get company, plant, warehouse description
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
     * @param statusId
     * @param languageId
     * @return
     */
    public String getStatusDescription(Long statusId, String languageId) {
        return stagingLineV2Repository.getStatusDescription(statusId, languageId);
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<String> getBarCodeId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName) {
        return stagingLineV2Repository.getPartnerItemBarcode(itemCode, companyCodeId, plantId, warehouseId, manufacturerName, languageId);
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
     * @param orderQty
     * @param uomQty
     * @return
     */
    public Double getQuantity(Double orderQty, Double uomQty) {
        if(orderQty != null && uomQty != null) {
            return orderQty * uomQty;
        }
        throw new BadRequestException("Quantity cannot be null");
    }

    /**
     *
     * @param itemCode
     * @param partBarCode
     * @return
     */
    public String generateBarCodeId (String itemCode, String partBarCode) {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(partBarCode);
        return stringBuilder.toString();
    }

    /**
     *
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeId (String refDocNumber) {
        String orderNoLastThreeDigit = refDocNumber;
        if (refDocNumber != null && refDocNumber.length() >= 3) {
            orderNoLastThreeDigit = refDocNumber.substring(refDocNumber.length() - 3);
        } else {
            throw new IllegalArgumentException("refDocNumber must be at least 3 characters long");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(orderNoLastThreeDigit);
        stringBuilder.append(DateUtils.getDateMonth());
        return stringBuilder.toString();
    }

    /**
     *
     * @param itemCode
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeIdV4 (String itemCode, String refDocNumber) {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        String orderNoLastThreeDigit = refDocNumber;
        if (refDocNumber != null && refDocNumber.length() >= 3) {
            orderNoLastThreeDigit = refDocNumber.substring(refDocNumber.length() - 3);
        } else {
            throw new IllegalArgumentException("refDocNumber must be at least 3 characters long");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(orderNoLastThreeDigit);
        stringBuilder.append(DateUtils.getDateMonth());
        return stringBuilder.toString();
    }
}
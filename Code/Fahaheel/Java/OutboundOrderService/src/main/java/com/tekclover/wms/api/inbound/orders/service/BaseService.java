package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.BusinessPartnerV2;
import com.tekclover.wms.api.inbound.orders.model.dto.UserManagement;
import com.tekclover.wms.api.inbound.orders.model.dto.Warehouse;
import com.tekclover.wms.api.inbound.orders.repository.StagingLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.List;

@Slf4j
public class BaseService {

    protected Long NUMBER_RANGE_CODE = 0L;
    protected String numberRangeId = null;
    protected IKeyValuePair description = null;
    protected String MW_AMS = "WK_ADMIN";
    protected String MFR_NAME_V7 = "KNOWELL";
    protected String statusDescription = null;
    protected String stockTypeDesc = null;
    protected static final String PACK_BARCODE = "99999";
    protected static final String LANG_ID = "EN";
    protected static final String ACTIVE = "Active";
    protected static final String IN_ACTIVE = "InActive";
    protected static final Long RESERVE_BIN_CLASS_ID = 2L;
    protected static final Long STAGING_BIN_CLASS_ID = 3L;
    protected static final String DIRECT_STOCK_BIN = "REC-AL-B2";
    protected static final String PICK_HE_NO = "HE-01";
    protected static final String UOM_ID = "BOX";
    protected static final Long OB_PL_ORD_TYP_ID = 3L;          //Production Order creation
    protected static final String WAREHOUSE_ID_110 = "110";
    protected static final String WAREHOUSE_ID_111 = "111";
    //V2
    protected static final String WAREHOUSE_ID_100 = "100";
    protected static final String WAREHOUSE_ID_200 = "200";
    protected static final String WAREHOUSE_ID_300 = "300";
    protected static final Long FAILED_PROCESS_STATUS_ID = 100L;

    protected static final String COMPANY_CODE = "1001";        //Indus mega food company code
    protected static final String MFR_NAME_V4 = "NAMRATHA";              //Walkaroo Mfr code
    protected static final String MFR_NAME_V6 = "BP";
    @Autowired
    protected IDMasterService idmasterService;

    @Autowired
    protected AuthTokenService authTokenService;

    @Autowired
    protected StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    protected MastersService mastersService;

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
     * @return
     */
    protected String getMfrName(String companyCode) {
        if (companyCode.equalsIgnoreCase(COMPANY_CODE)) {
            return "1001";
        }
        return null;
    }

    /**
     * ID Master AuthToken
     *
     * @return
     */
    protected String getIDMasterAuthToken() {
        return authTokenService.getIDMasterServiceAuthToken().getAccess_token();
    }

    /**
     * Master AuthToken
     *
     * @return
     */
    protected String getMasterAuthToken() {
        return authTokenService.getMastersServiceAuthToken().getAccess_token();
    }

    public String getPreOutboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param statusId
     * @param languageId
     * @return
     */
    public String getStatusDescriptionV5(Long statusId, String languageId) {
        return stagingLineV2Repository.getStatusDescription(statusId, languageId);
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
        if (referenceDocumentTypeId == 4) {
            referenceDocumentType = "SALESINVOICE";
        }

        return referenceDocumentType;
    }

    /**
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemTypeId
     * @return
     */
    public String getItemTypeDesc(String companyCodeId, String plantId, String languageId,
                                  String warehouseId, Long itemTypeId) {
        return stagingLineV2Repository.getItemTypeDescription(companyCodeId, plantId, languageId, warehouseId, itemTypeId);
    }

    /**
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
     * @param value
     * @return
     */
    protected static double round(Double value) {
        if (value == null) {
            return 0.0;
        }
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param value
     * @return
     */
    protected static double roundUp(Double value) {
        return Math.ceil(value);
    }

    /**
     * Get company, plant, warehouse description
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
     * @param statusId
     * @param languageId
     * @return
     */
    public String getStatusDescription(Long statusId, String languageId) {
        return stagingLineV2Repository.getStatusDescription(statusId, languageId);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public IKeyValuePair getnoOfCaseTolerance(String companyCodeId, String plantId, String languageId, String warehouseId) {
        return stagingLineV2Repository.getNoOfCaseAndTolerance(companyCodeId, plantId, languageId, warehouseId);
    }

    /**
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
     * @param quantity
     * @return
     */
    public Double getQuantity(Double quantity) {
        return quantity != null ? quantity : 0;
    }

    /**
     * @param status
     * @return
     */
    public String getStatus(Long status) {
        return status != null ? status == 0 ? ACTIVE : IN_ACTIVE : null;
    }

    /**
     * orderManagementLine inventory calculation (Allocate)
     *
     * @param allocatedQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] allocateInventory(Double allocatedQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("1.INV_QTY, ALLOC_QTY, ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + allocatedQty);

        double INV_QTY = getQuantity(inventoryQty) - allocatedQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty) + allocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * orderManagementLine inventory calculation (Allocate)
     *
     * @param allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] allocateInventory(Double allocatedQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + allocatedQty);
        bagSize = bagSize != null ? bagSize : 0;       // 6.0               0.0                         6.0

//        double actualAllocatedQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);    // 30

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - allocatedQty;                // 6 - 30 = -24
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) + allocatedQty;        // 0 + 6  = 30

        double ALLOC_QTY = allocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);      //  0
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);       //  6

        double TOT_QTY = INV_QTY + ALLOC_QTY;          //  6

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }


    /**
     * @param orderQty
     * @param uomQty
     * @return
     */
    public Double getQuantity(Double orderQty, Double uomQty) {
        if (orderQty != null && uomQty != null) {
            return orderQty * uomQty;
        }
        throw new BadRequestException("Quantity cannot be null");
    }


    /**
     * pickupline inventory calculation (unAllocate)
     *
     * @param pickCnfQty/allocatedQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUnAllocate(Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("2.INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);

        double INV_QTY = getQuantity(inventoryQty) + pickCnfQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty) - pickCnfQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * reversal/pickupLine
     *
     * @param pickCnfQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] inventoryUnAllocate(Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("3.INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);

        double INV_QTY = getQuantity(inventoryQty) + pickCnfQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * pickupline inventory calculation
     *
     * @param allocatedQty
     * @param pickCnfQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventory(Double allocatedQty, Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("4.INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);

        allocatedQty = getQuantity(allocatedQty);
        pickCnfQty = getQuantity(pickCnfQty);

        double INV_QTY = getQuantity(inventoryQty) + allocatedQty - pickCnfQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty) - allocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * @param pickCnfQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventory(Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("5.INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);

        pickCnfQty = getQuantity(pickCnfQty);

        double INV_QTY = getQuantity(inventoryQty);
        double ALLOC_QTY = getQuantity(invAllocatedQty);

        ALLOC_QTY = INV_QTY == 0 ? (ALLOC_QTY - pickCnfQty) : ALLOC_QTY;
        INV_QTY = INV_QTY == 0 ? INV_QTY : (INV_QTY - pickCnfQty);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * @param pickCnfQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] rollbackInventory(Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("6.INV_QTY, ALLOC_QTY, PICK_CNF_QTY, PICK_ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        pickCnfQty = getQuantity(pickCnfQty);
        double INV_QTY = getQuantity(inventoryQty) + pickCnfQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * @param pickCnfQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] rollbackInventoryV3(Double pickCnfQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("7.INV_QTY, ALLOC_QTY, PICK_CNF_QTY, PICK_ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        pickCnfQty = getQuantity(pickCnfQty);
        double INV_QTY = getQuantity(inventoryQty);
        double ALLOC_QTY = getQuantity(invAllocatedQty) + pickCnfQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * orderManagementLine inventory calculation (Allocate)
     *
     * @param allocatedQty
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] allocateInventoryV5(Double allocatedQty, Double inventoryQty, Double invAllocatedQty) {
        log.info("1.INV_QTY, ALLOC_QTY, ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + allocatedQty);

        double INV_QTY = getQuantity(inventoryQty) - allocatedQty;
        double ALLOC_QTY = getQuantity(invAllocatedQty) + allocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }
//    /**
//     *
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param referenceDocumentTypeId
//     * @return
//     */
//    public String getOutboundOrderTypeDescV5(String companyCode, String plantId, String languageId, String warehouseId, Long referenceDocumentTypeId) {
//        String referenceDocumentType = stagingLineV2Repository.getOutboundOrderTypeDescription(
//                referenceDocumentTypeId, companyCode, plantId, languageId, warehouseId);
//        return referenceDocumentType;
//    }


}
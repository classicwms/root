package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.outbound.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.outbound.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.outbound.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.outbound.transaction.repository.StagingLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;


@Slf4j
public class BaseService {

    protected Long NUMBER_RANGE_CODE = 0L;
    protected static final String WAREHOUSE_ID_110 = "110";
    protected static final String WAREHOUSE_ID_111 = "111";
    //V2
    protected static final String MFR_NAME = "NAMRATHA";
    protected static final String MFR_NAME_V5 = "REEFERON";
    protected static final String MFR_NAME_V7 = "KNOWELL";
    protected static final String WAREHOUSE_ID_100 = "100";
    protected static final String WAREHOUSE_ID_200 = "200";
    protected static final Long OB_PL_ORD_TYP_ID = 3L;          //Production Order creation
    protected static final Long OB_PL_ORD_TYP_ID_MT = 11L;          //Production Order creation
    protected static final Long OB_IPL_ORD_TYP_ID_SFG = 5L;          //Production Order create - semi-finished goods Picklist
    protected static final Long OB_IPL_ORD_TYP_ID_FG = 6L;
    protected static final String LANG_ID = "EN";
    protected IKeyValuePair description = null;
    protected String statusDescription = null;
    protected static final String PACK_BARCODE = "99999";

    @Autowired
    protected IDMasterService idmasterService;

    @Autowired
    protected AuthTokenService authTokenService;

    @Autowired
    private PickupLineRepository pickupLineRepository;

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
     * @return
     */
    public IKeyValuePair getDescription(String companyCodeId, String plantId, String languageId, String warehouseId) {
        return stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
    }

//    /**
//     * pickupline inventory calculation (unAllocate)
//     * @param pickCnfQty/allocatedQty
//     * @param bagSize
//     * @param inventoryQty
//     * @param invAllocatedQty
//     * @return
//     */
//    public double[] calculateInventoryUnAllocate (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
//        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
//        bagSize = bagSize != null ? bagSize : 0;
//
//        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);
//
//        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + actualPickConfirmQty;
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualPickConfirmQty;
//
//        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
//        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);
//
//        double TOT_QTY = INV_QTY + ALLOC_QTY;
//        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;
//
//        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
//        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
//    }

    /**
     * Modified for Namratha 16-06-2025
     * Aakash Vinayak
     *
     * pickupline inventory calculation (unAllocate)
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUnAllocateV6 (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualPickConfirmQty = pickCnfQty;

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0);
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualPickConfirmQty;
        double ALLOC_QTY = 0.0;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     *
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryAllocate (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

//        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - pickCnfQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }


    /**
     * pickupline inventory calculation (Allocate)
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUOMAllocate (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - getQuantity(pickCnfQty);
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     *
     * @param allocatedQty
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateUOMInventory (Double allocatedQty, Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);

        bagSize = bagSize != null ? bagSize : 0;
        allocatedQty = getQuantity(allocatedQty);

        double INV_QTY = ((inventoryQty != null ? inventoryQty : 0) + allocatedQty) - getQuantity(pickCnfQty);
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - allocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * pickupline inventory calculation
     * @param allocatedQty
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
//    public double[] calculateInventory (Double allocatedQty, Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
//        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
//        bagSize = bagSize != null ? bagSize : 0;
//
//        double actualAllocationQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);
//        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);
//
//        double INV_QTY = ((inventoryQty != null ? inventoryQty : 0) + actualAllocationQty) - actualPickConfirmQty;
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualAllocationQty;
//
//        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
//        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);
//
//        double TOT_QTY = INV_QTY + ALLOC_QTY;
//        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;
//
//        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
//        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
//    }

    /**
     * Modified for Namratha 16-06-2025
     * Aakash Vinayak
     *
     * pickupline inventory calculation
     * @param allocatedQty
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryV6 (Double allocatedQty, Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualAllocationQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);
        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = ((inventoryQty != null ? inventoryQty : 0) + invAllocatedQty) - actualPickConfirmQty;
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualAllocationQty;
        double ALLOC_QTY = 0.0;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     *
     * @param quantity
     * @return
     */
    public Double getQuantity(Double quantity) {
        return quantity != null ? quantity : 0;
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
     * @param value
     * @return
     */
    protected static double roundUp(Double value) {
        return Math.ceil(value);
    }
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
        if (referenceDocumentTypeId == 11) {
            referenceDocumentType = "EMPTY CRATE";
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

        String stockTypeDesc = pickupLineRepository.getStockTypeDescription(companyCodeId, plantId, languageId, warehouseId, stockTypeId);


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
        return pickupLineRepository.getCustomerName(companyCodeId, plantId, languageId, warehouseId, customerCode);
    }

    /**
     * pickupline inventory calculation (unAllocate)
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
//    public double[] calculateInventoryUOMUnAllocate (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
//        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
//        bagSize = bagSize != null ? bagSize : 0;
//        pickCnfQty = getQuantity(pickCnfQty);
//
//        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + pickCnfQty;
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - pickCnfQty;
//
//        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
//        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);
//
//        double TOT_QTY = INV_QTY + ALLOC_QTY;
//        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;
//
//        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
//        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
//    }

    /**
     * pickupline inventory calculation (unAllocate)
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUOMUnAllocateV6 (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;
        pickCnfQty = getQuantity(pickCnfQty);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - pickCnfQty;
//        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - pickCnfQty;
        double ALLOC_QTY = 0.0;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
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
//    public String getOutboundOrderTypeDescV5(String companyCode, String plantId, String languageId, String warehouseId, Long referenceDocumentTypeId) {
//        String referenceDocumentType = stagingLineV2Repository.getOutboundOrderTypeDescription(
//                referenceDocumentTypeId, companyCode, plantId, languageId, warehouseId);
//        return referenceDocumentType;
//    }
}
package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.confirmation.SalesOrder;
import com.tekclover.wms.api.inbound.orders.repository.StagingLineV2Repository;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class BaseService {

    protected Long NUMBER_RANGE_CODE = 0L;
    protected String numberRangeId = null;
    protected IKeyValuePair description = null;
    protected String MW_AMS = "MW_AMS";
    protected String statusDescription = null;
    protected String stockTypeDesc = null;
    protected static final String PACK_BARCODE = "99999";
    protected static final String LANG_ID = "EN";
    protected static final String MFR_NAME = "NAMRATHA";
    protected static final String MFR_NAME_V5 = "REEFERON";
    protected static final String MRF_NAME_V7 = "KNOWELL";
    protected static final String MRF_NAME_V6 = "BP";
    protected static final String PICK_HE_NO = "HE-01";
    protected static final String BASE_UOM = "ECH";
    protected static final String ALTERNATE_UOM = "BAG";
    protected static final String UOM_ID = "BOX";
    protected static final String WAREHOUSE_ID_110 = "110";
    protected static final String WAREHOUSE_ID_111 = "111";
    //V2
    protected static final String WAREHOUSE_ID_100 = "100";
    protected static final String WAREHOUSE_ID_200 = "200";

    protected static final String COMPANY_CODE = "1001";        //Indus mega food company code
    protected static final String WK_COMPANY_CODE = "2000";        //Indus mega food company code
    protected static final String KW_COMPANY_CODE = "2000";
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

    protected static final List<String> storageSectionIds = new ArrayList<>(Arrays.asList("6", "7"));         //Indus storage section id propose not in


    @Autowired
    protected AuthTokenService authTokenService;

    @Autowired
    protected StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    MastersService mastersService;

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

    //--------------------------------------------------------------------------------------------------------

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @param accessToken
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
                                        String languageId, String warehouseId, String accessToken) {
        log.info("The Given Values : CompanyCodeId --> " + companyCodeId + " and PlantId --> " + plantId + " and LanguageId --> " + languageId + " and WareHouseId --> " + warehouseId);
        String nextNumberRange = mastersService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, accessToken);
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
        log.info("The Given Values : CompanyCodeId --> " + companyCodeId + " and PlantId --> " + plantId + " and LanguageId --> " + languageId + " and WareHouseId --> " + warehouseId);
        String nextNumberRange = mastersService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, getIDMasterAuthToken());
        return nextNumberRange;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeDesc(String companyCode, String plantId, String languageId, String warehouseId, Long referenceDocumentTypeId) {
        String referenceDocumentType = stagingLineV2Repository.getInboundOrderTypeDescription(referenceDocumentTypeId, companyCode, plantId, languageId, warehouseId);
        return referenceDocumentType;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @return
     */
    public String getUomId(String companyCodeId, String plantId, String languageId, String warehouseId) {
        String uomId = stagingLineV2Repository.getUomId(companyCodeId, plantId, languageId, warehouseId);
        log.info("UOMID : " + uomId);
        return uomId != null ? uomId : UOM_ID;
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
//            referenceDocumentType = "DirectReceipt";
            referenceDocumentType = "Sub Contract";
        }
        if (referenceDocumentTypeId == 11) {
//            referenceDocumentType = "DirectReceipt";
            referenceDocumentType = "Empty Crate";
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
    public String getItemTypeDesc(String companyCodeId, String plantId, String languageId, String warehouseId, Long itemTypeId) {
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
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<String> getBarCodeId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName) {
        return stagingLineV2Repository.getPartnerItemBarcode(itemCode, companyCodeId, plantId, warehouseId, manufacturerName, languageId);
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
     * @param quantity
     * @return
     */
    public Double getQuantity(Double quantity) {
        return quantity != null ? quantity : 0;
    }

    /**
     * @param itemCode
     * @param partBarCode
     * @return
     */
    public String generateBarCodeId(String itemCode, String partBarCode) {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(partBarCode);
        return stringBuilder.toString();
    }

    /**
     * @param itemCode
     * @param partBarCode
     * @param bagNumber
     * @return
     */
    public String generateBarCodeId(String itemCode, String partBarCode, long bagNumber) throws ParseException {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(partBarCode);
        stringBuilder.append(bagNumber);

        Date currentDate = DateUtils.getCurrentDateTimeAsDate();
        Date[] dates = DateUtils.addTimeToDatesForSearch(currentDate, currentDate);
        Date fromDate = dates[0];
        Date toDate = dates[1];
        log.info("Barcode Generate ---> Logic ---> Started");
        Long lineNo = stagingLineV2Repository.getStagingLineCount(itemCode, fromDate, toDate);
        stringBuilder.append(lineNo);
        return stringBuilder.toString();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param idMasterAuthToken
     * @return
     */
    public String generateKnowellBarCodeId(String companyCodeId, String plantId, String languageId, String warehouseId, String idMasterAuthToken) {
        String caseBarcode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
        return caseBarcode;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeId(String refDocNumber) {
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
     * @param itemCode
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeIdV4(String itemCode, String refDocNumber) {
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


    /**
     * pickupline inventory calculation
     *
     * @param allocatedQty
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventory(Double allocatedQty, Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualAllocationQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);
        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = ((inventoryQty != null ? inventoryQty : 0) + actualAllocationQty) - actualPickConfirmQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualAllocationQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * @param allocatedQty
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateUOMInventory(Double allocatedQty, Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
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
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * pickupline inventory calculation (unAllocate)
     *
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUnAllocate(Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + actualPickConfirmQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualPickConfirmQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * pickupline inventory calculation (unAllocate)
     *
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUOMUnAllocate(Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;
        pickCnfQty = getQuantity(pickCnfQty);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + pickCnfQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - pickCnfQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * pickupline inventory calculation (Allocate)
     *
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryAllocate(Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - actualPickConfirmQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * pickupline inventory calculation (Allocate)
     *
     * @param pickCnfQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUOMAllocate(Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - getQuantity(pickCnfQty);
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
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
        bagSize = bagSize != null ? bagSize : 0;

        double actualAllocatedQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - actualAllocatedQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) + actualAllocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * @param transferQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateTransferInventory(Double transferQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, TFR_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + transferQty);
        bagSize = bagSize != null ? bagSize : 0;

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + transferQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("Tfr INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * @param varianceQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateStockAdjustmentInventory(Double varianceQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, TFR_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + varianceQty);
        bagSize = bagSize != null ? bagSize : 0;
        double actualVarianceQty = getQuantity((varianceQty != null ? varianceQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + actualVarianceQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0);

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("Tfr INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[]{INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
    }

    /**
     * @param expectedDate
     * @return
     */
    public Date getExpectedDate(String expectedDate) {
        Date reqDelDate = null;
        if (expectedDate != null) {
            if (expectedDate.contains("-")) {
                // EA_DATE
                try {
                    reqDelDate = new Date();
                    if (expectedDate.length() > 10) {
                        reqDelDate = DateUtils.convertStringToDateWithTime(expectedDate);
                    }
                    if (expectedDate.length() == 10) {
                        reqDelDate = DateUtils.convertStringToDate2(expectedDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }
            }
        }
        return reqDelDate;
    }

    /**
     *
     * @param refDocNo
     * @param itemCode
     * @param lineNo
     * @return
     */
    public String generateBarcodeId(String refDocNo, String itemCode, int lineNo){
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        String orderNoLastThreeDigit = refDocNo;
        if (refDocNo != null && refDocNo.length() >= 3) {
            orderNoLastThreeDigit = refDocNo.substring(refDocNo.length() - 3);
        } else {
            throw new IllegalArgumentException("refDocNumber must be at least 3 characters long");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(orderNoLastThreeDigit);
        stringBuilder.append(lineNo);
        return stringBuilder.toString();
    }
}
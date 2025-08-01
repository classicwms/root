package com.tekclover.wms.api.outbound.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.outbound.transaction.model.inbound.gr.GrLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class GrLineV2 extends GrLine {

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "CBM")
    private Double cbm;

    @Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
    private String cbmUnit;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(255)")
    private String brand;

    @Column(name = "REJ_TYPE", columnDefinition = "nvarchar(255)")
    private String rejectType;

    @Column(name = "REJ_REASON", columnDefinition = "nvarchar(255)")
    private String rejectReason;

    @Column(name = "CBM_QTY")
    private Double cbmQuantity;                                        //CBM per Quantity

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "ST_BIN_INTM", columnDefinition = "nvarchar(100)")
    private String interimStorageBin;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
    private String middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    /*----------------Walkaroo changes------------------------------------------------------*/
    @Column(name = "MATERIAL_NO", columnDefinition = "nvarchar(50)")
    private String materialNo;

    @Column(name = "PRICE_SEGMENT", columnDefinition = "nvarchar(50)")
    private String priceSegment;

    @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
    private String articleNo;

    @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
    private String gender;

    @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
    private String color;

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    @Column(name = "NO_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;

    @Column(name = "STG_NO", columnDefinition = "nvarchar(50)")
    private String stagingNo;

    /*--------------------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "AMS_SUP_INV", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    @Column(name = "IS_PA_HEADER_CTD")
    private Long isPutAwayHeaderCreated;

    @Column(name = "ST_SEC_ID")
    private String storageSectionId;;

    // 3PL
    @Column(name = "TPL_CBM")
    private Double threePLCbm;

    @Column(name = "TPL_UOM")
    private String threePLUom;

    @Column(name = "TPL_BILL_STATUS", columnDefinition = "nvarchar(50)")
    private String threePLBillStatus;

    @Column(name = "TPL_LENGTH")
    private Double threePLLength;

    @Column(name = "TPL_WIDTH")
    private Double threePLHeight;

    @Column(name = "TPL_HEIGHT")
    private Double threePLWidth;

    @Column(name = "ACCEPT_TOT_CBM")
    private Double acceptTotalCbm;

    @Column(name = "RECEIVER_NAME", columnDefinition = "nvarchar(100)")
    private String receiverName;

    @Column(name = "UNLOADER_NAME", columnDefinition = "nvarchar(100)")
    private String unLoaderName;

    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;

    @Column(name = "ITM_TYP", columnDefinition = "nvarchar(100)")
    private String itemType;

    @Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
    private String itemGroup;

    @Column(name = "PA_NO", columnDefinition = "nvarchar(100)")
    private String putAwayNumber;

    @Column(name = "CROSS_DOCK")
    private Boolean crossDock;

}
package com.tekclover.wms.api.transaction.model.threepl.stockmovement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblstockmovement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MVT_DOC_NO")
    private Long movementDocNo;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(5)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(50)" )
    private String itemCode;

    @Column(name = "REF_ORD_NO")
    private String referenceOrderNo;

    @Column(name = "DOC_DATE")
    private Date documentDate = new Date();

    @Column(name = "OPEN_STOCK")
    private Double openingStock;

    @Column(name = "IB_QTY")
    private Double inboundQty;

    @Column(name = "OB_QTY")
    private Double outboundQty;

    @Column(name = "CLOSE_STOCK")
    private Double closingStock;

    @Column(name = "PROFORMA_INVOICE_NO")
    private Long proformaInvoiceNo = 0L;

    @Column(name = "STOCK_UOM")
    private Double stockUom;

    @Column(name = "PARTNER_CODE")
    private String partnerCode;

    @Column(name = "TEXT", columnDefinition = "nvarchar(200)")
    private String description;

    @Column(name = "C_DESC", columnDefinition = "nvarchar(500)")
    private String companyDescription;

    @Column(name = "PLANT_DESC",columnDefinition = "nvarchar(500)")
    private String plantDescription;

    @Column(name = "WAREHOUSE_DESC",columnDefinition = "nvarchar(500)")
    private String warehouseDescription;

    @Column(name = "STATUS_DESC",columnDefinition = "nvarchar(500)")
    private String statusDescription;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(200)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(200)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(200)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(200)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(200)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(200)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(200)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(200)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(200)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(200)")
    private String referenceField10;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "PAL_CODE")
    private String palletCode;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "PACK_BARCODE")
    private String packBarcodes;

    @Column(name = "VAR_ID")
    private Long variantCode;

    @Column(name = "VAR_SUB_ID")
    private String variantSubCode;

    @Column(name = "STR_NO")
    private String batchSerialNumber;

    @Column(name = "ST_BIN")
    private String storageBin;

    @Column(name = "STCK_TYP_ID")
    private Long stockTypeId;

    @Column(name = "SP_ST_IND_ID")
    private Long specialStockIndicatorId;

    @Column(name = "STR_MTD")
    private String storageMethod;

    @Column(name = "BIN_CL_ID")
    private Long binClassId;

    @Column(name = "ALLOC_QTY")
    private Double allocatedQuantity;

    @Column(name = "INV_UOM")
    private String inventoryUom;

    @Column(name = "MFR_DATE")
    private Date manufacturerDate;

    @Column(name = "EXP_DATE")
    private Date expiryDate;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "CBM", columnDefinition = "nvarchar(255)")
    private String cbm;

    @Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
    private String cbmUnit;

    @Column(name = "CBM_PER_QTY", columnDefinition = "nvarchar(255)")
    private String cbmPerQuantity;
//    @Id
//    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
//    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
    private String levelId;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(255)")
    private String brand;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(255)")
    private String referenceDocumentNo;

    @Column(name = "STCK_TYP_TEXT", columnDefinition = "nvarchar(255)")
    private String stockTypeDescription;

    //3PL
    @Column(name = "TPL_ST_IND")
    private Long threePLStockIndicator;

    @Column(name = "TPL_PARTNER_ID", columnDefinition = "nvarchar(50)")
    private String threePLPartnerId;

    @Column(name = "TPL_GR_DATE")
    private Date threePLGrDate = new Date();

    @Column(name = "TPL_CBM")
    private Double threePLCbm = 0D;

    @Column(name = "TPL_UOM", columnDefinition = "nvarchar(50)")
    private String threePLUom;

    @Column(name = "TPL_CBM_PER_QTY")
    private Double threePLCbmPerQty = 0D;

    @Column(name = "TPL_RATE_PER_QTY")
    private Double threePLRatePerQty = 0D;

    @Column(name = "RATE")
    private Double rate = 0D;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "TOTAL_TPL_CBM")
    private Double totalThreePLCbm;

    @Column(name = "TOTAL_RATE")
    private Double totalRate;

    @Column(name = "TPL_BILL_STATUS", columnDefinition = "nvarchar(50)")
    private String threePLBillStatus;

    @Column(name = "TPL_LENGTH")
    private Double threePLLength;

    @Column(name = "TPL_WIDTH")
    private Double threePLHeight;

    @Column(name = "TPL_HEIGHT")
    private Double threePLWidth;

    @Column(name = "STOCK_INVOICE")
    private Long stockInvoice =0L;

}

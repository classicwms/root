package com.tekclover.wms.api.transaction.model.cyclecount.stockadjustment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblstockadjustment")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StockAdjustmentId")
    private Long stockAdjustmentId;

    @Column(name = "STOCK_ADJUSTMENT_KEY")
    private Long stockAdjustmentKey;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCode;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(100)")
    private String branchName;

    @Column(name = "DATE_OF_ADJUSTMENT")
    private Date dateOfAdjustment;

    @Column(name = "IS_CYCLECOUNT", columnDefinition = "nvarchar(10)")
    private String isCycleCount;

    @Column(name = "IS_DAMAGE", columnDefinition = "nvarchar(10)")
    private String isDamage;

    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(50)")
    private String itemCode;

    @Column(name = "ITM_TEXT", columnDefinition = "nvarchar(255)")
    private String itemDescription;

    @Column(name = "ADJUSTMENT_QTY")
    private Double adjustmentQty;

    @Column(name = "UNIT_OF_MEASURE", columnDefinition = "nvarchar(50)")
    private String unitOfMeasure;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(100)")
    private String manufacturerCode;

    @Column(name = "REMARKS", columnDefinition = "nvarchar(500)")
    private String remarks;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "AMS_REF_NO", columnDefinition = "nvarchar(50)")
    private String amsReferenceNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "PAL_CODE")
    private String palletCode;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
    private String packBarcodes;

    @Column(name = "ST_BIN")
    private String storageBin;

    @Column(name = "STCK_TYP_ID")
    private Long stockTypeId;

    @Column(name = "SP_ST_IND_ID")
    private Long specialStockIndicatorId;

    @Column(name = "REF_ORD_NO")
    private String referenceOrderNo;

    @Column(name = "STR_MTD")
    private String storageMethod;

    @Column(name = "BIN_CL_ID")
    private Long binClassId;

    @Column(name = "UPDATED_ON")
    private Date saUpdatedOn;

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(100)")
    private String barcodeId;

    @Column(name = "CBM", columnDefinition = "nvarchar(255)")
    private String cbm;

    @Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
    private String cbmUnit;

    @Column(name = "CBM_PER_QTY", columnDefinition = "nvarchar(255)")
    private String cbmPerQuantity;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
    private String levelId;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(255)")
    private String referenceDocumentNo;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "ALLOC_QTY")
    private Double allocatedQuantity;

    @Column(name = "INV_UOM")
    private String inventoryUom;

    @Column(name = "MFR_DATE")
    private Date manufacturerDate;

    @Column(name = "EXP_DATE")
    private Date expiryDate;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REF_FIELD_1")
    private String referenceField1;

    @Column(name = "REF_FIELD_2")
    private String referenceField2;

    @Column(name = "REF_FIELD_3")
    private String referenceField3;

    @Column(name = "REF_FIELD_4")
    private Double referenceField4;

    @Column(name = "BEFORE_ADJUSTMENT")
    private Double beforeAdjustment;

    @Column(name = "AFTER_ADJUSTMENT")
    private Double afterAdjustment;

    @Column(name = "REF_FIELD_5")
    private String referenceField5;

    @Column(name = "REF_FIELD_6")
    private String referenceField6;

    @Column(name = "REF_FIELD_7")
    private String referenceField7;

    @Column(name = "REF_FIELD_8")
    private String referenceField8;

    @Column(name = "REF_FIELD_9")
    private String referenceField9;

    @Column(name = "REF_FIELD_10")
    private String referenceField10;

    @Column(name = "IV_CTD_BY")
    private String createdBy;

    @Column(name = "IV_CTD_ON")
    private Date createdOn;

    @Column(name = "IV_UTD_BY")
    private String updatedBy;

    @Column(name = "IV_UTD_ON")
    private Date updatedOn;

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "STCK_TYP_TEXT", columnDefinition = "nvarchar(255)")
    private String stockTypeDescription;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;

    @Column(name = "STR_NO")
    private String batchSerialNumber;

    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;
}
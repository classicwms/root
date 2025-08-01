package com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class PutAwayLineV2 extends PutAwayLine {

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "MFR_DATE")
    private Date manufacturerDate;

    @Column(name = "EXP_DATE")
    private Date expiryDate;

    @Column(name = "ST_SEC_ID")
    private String storageSectionId;

    @Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String parentProductionOrderNo;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "ORD_QTY")
    private Double orderQty;

    @Column(name = "CBM", columnDefinition = "nvarchar(255)")
    private String cbm;

    @Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
    private String cbmUnit;

    @Column(name = "CBM_QTY")
    private Double cbmQuantity;

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

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

    /*--------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
    private String businessPartnerCode;

    @Column(name = "LEVEL_ID", columnDefinition = "nvarchar(25)")
    private String levelId;
    
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

    @Column(name = "ACT_INV_QTY")
    private Double actualInventoryQty;

    @Column(name = "PAL_ID", columnDefinition = "nvarchar(100)")
    private String palletId;

    @Column(name = "PA_ASGN_ON")
    private Date assignedOn;

    @Column(name = "AMS_SUP_INV", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    @Column(name = "PIECE_QTY")
    private Double pieceQty;

    @Column(name = "CASE_QTY")
    private Double caseQty;

    @Column(name = "QTY_IN_CASE")
    private Double qtyInCase;

    @Column(name = "QTY_IN_PIECE")
    private Double qtyInPiece;

    @Column(name = "QTY_IN_CREATE")
    private Double qtyInCreate;

    @Column(name = "VEHICLE_NO", columnDefinition = "nvarchar(50)")
    private String vehicleNo;

    @Column(name = "VEHICLE_REPORTING_DATE")
    private Date vehicleReportingDate;

    @Column(name = "VEHICLE_UNLOADING_DATE")
    private Date vehicleUnloadingDate;

    @Column(name = "RECEIVINGVARIANCE")
    private String receivingVariance;
}

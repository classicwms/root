package com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.v2;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.Inventory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class InventoryV2 extends Inventory {

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

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "STCK_TYP_TEXT", columnDefinition = "nvarchar(255)")
    private String stockTypeDescription;

    @Column(name = "TPL_CBM")
    private Double threePLCbm;

    @Column(name = "TPL_UOM", columnDefinition = "nvarchar(50)")
    private String threePLUom;

    @Column(name = "TPL_CBM_PER_QTY")
    private Double threePLCbmPerQty;

    @Column(name = "TPL_RATE_PER_QTY")
    private Double threePLRatePerQty;

    @Column(name = "RATE")
    private Double rate;

    @Column(name = "CURRENCY")
    private String currency;
}
package com.tekclover.wms.api.transaction.model.inbound.inventory.v2;

import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
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
public class InventoryV2 extends Inventory {

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(100)")
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

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
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

    //3PL
    @Column(name = "TPL_ST_IND")
    private Long threePLStockIndicator;

    @Column(name = "TPL_PARTNER_ID", columnDefinition = "nvarchar(50)")
    private String threePLPartnerId;

    @Column(name = "TPL_GR_DATE")
    private Date threePLGrDate = new Date();

    @Column(name = "TPL_CBM")
    private Double threePLCbm;

    @Column(name = "TPL_UOM")
    private String threePLUom;

    @Column(name = "TPL_CBM_PER_QTY")
    private Double threePLCbmPerQty;

    @Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
    private String businessPartnerCode;

    @Column(name = "BATCH_DATE")
    private Date batchDate;

    @Column(name = "ITM_TYP_ID")
    private Long itemType;

    @Column(name = "ITM_TYP_TXT", columnDefinition = "nvarchar(50)")
    private String itemTypeDescription;
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

    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;

    @Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
    private String itemGroup;
}
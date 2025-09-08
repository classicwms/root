package com.tekclover.wms.api.transaction.model.inbound.staging.v2;

import com.tekclover.wms.api.transaction.model.inbound.staging.StagingLineEntity;
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
public class StagingLineEntityV2 extends StagingLineEntity {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ST_SEC_ID")
    private String storageSectionId;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "BRAND", columnDefinition = "nvarchar(255)")
    private String brand;

    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(500)")
    private String barcodeId;

    @Column(name = "REC_ACCEPT_QTY")
    private Double rec_accept_qty;

    @Column(name = "REC_DAMAGE_QTY")
    private Double rec_damage_qty;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
    private String middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String parentProductionOrderNo;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    /*--------------------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;
    
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

    @Column(name = "TOTAL_COUNT")
    private Long totalCount;

    @Column(name = "TOTAL_BARCODE_STATUS_COUNT")
    private Long totalBarcodeStatusCount;

    @Column(name = "VEHICLE_NO", columnDefinition = "nvarchar(50)")
    private String vehicleNumber;

    @Column(name = "VEHICLE_TOTAL_HU_NUMBER")
    private Long vehicleTotalHuNumber;

    @Column(name = "VEHICLE_SCAN_HU_NUMBER")
    private Long vehicleScanHuNumber;

    @Column(name = "PALLET_ID", columnDefinition = "nvarchar(250)")
    private String palletId;

    @Column(name = "GRLINE_FLAG", columnDefinition = "nvarchar(50)")
    private String grLineFlag;

    @Column(name = "SAP_FALG", columnDefinition = "nvarchar(50)")
    private String sapFlag;

    @Column(name = "SAP_TYPE", columnDefinition = "nvarchar(50)")
    private String sapType;

    @Column(name = "SAP_MESSAGE", columnDefinition = "nvarchar(200)")
    private String sapMessage;

    @Column(name = "SAP_DOCUMENT_NO", columnDefinition = "nvarchar(200)")
    private String sapDocumentNo;

    @Column(name = "MATERIAL_DOC_DATE", columnDefinition = "nvarchar(200)")
    private Date materialDocDate;

    @Column(name = "MTO_NUMBER", columnDefinition = "nvarchar(200)")
    private String mtoNumber;

    @Column(name = "PUT_AWAY_QUANTITY")
    private Double putAwayQuantity;

    @Column(name = "PUT_AWAY_DATE", columnDefinition = "nvarchar(200)")
    private Date putAwayDate;

}

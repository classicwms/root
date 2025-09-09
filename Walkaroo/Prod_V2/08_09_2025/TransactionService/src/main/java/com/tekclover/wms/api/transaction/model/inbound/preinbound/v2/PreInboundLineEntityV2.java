package com.tekclover.wms.api.transaction.model.inbound.preinbound.v2;

import com.tekclover.wms.api.transaction.model.inbound.preinbound.PreInboundLineEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class PreInboundLineEntityV2 extends PreInboundLineEntity {

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ST_SEC_ID")
    private String storageSectionId;

    @Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
    private String origin;

    @Column(name = "SUPPLIER_NAME", columnDefinition = "nvarchar(255)")
    private String supplierName;

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
    private String middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
    private String middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String purchaseOrderNumber;

    @Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String parentProductionOrderNo;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    /*--------------------------------------------------------------------------------------------------------*/
    @Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;
    
    @Column(name = "STR_NO")
    private String batchSerialNumber;
    
    /*----------------Walkaroo changes------------------------------------------------------*/
    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(100)")
    private String barcodeId;

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

    @Column(name = "MTO_NUMBER", columnDefinition = "nvarchar(200)")
    private String mtoNumber;
}
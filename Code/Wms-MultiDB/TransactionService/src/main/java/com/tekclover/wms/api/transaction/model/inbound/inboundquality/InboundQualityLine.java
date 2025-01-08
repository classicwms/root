package com.tekclover.wms.api.transaction.model.inbound.inboundquality;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `C_ID`,`PLANT_ID`, `WH_ID`, `LANG_ID`
 */
@Table(
        name = "tblinboundqualityline",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_inboundqualityline",
                        columnNames = {"C_ID", "PLANT_ID", "LANG_ID", "WH_ID", "REF_DOC_NO", "PRE_IB_NO", "IB_QC_NO", "ITM_CODE", "IB_QL_LINE_NO"})
        }
)
@IdClass(InboundQualityLineCompositeKey.class)
public class InboundQualityLine {

    @Id
    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Id
    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Id
    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Id
    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
    private String refDocNumber;

    @Id
    @Column(name = "PRE_IB_NO", columnDefinition = "nvarchar(100)")
    private String preInboundNo;

    @Id
    @Column(name = "IB_QC_NO", columnDefinition = "nvarchar(100)")
    private String inboundQualityNumber;

    @Id
    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(200)")
    private String itemCode;

    @Id
    @Column(name = "IB_QL_LINE_NO")
    private Long lineNo;

    @Column(name = "ITM_TEXT", columnDefinition = "nvarchar(300)")
    private String itemDescription;

    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "BATCH_NO", columnDefinition = "nvarchar(100)")
    private String batchSerialNumber;

    @Column(name = "IB_ORD_TYP_ID")
    private Long inboundOrderTypeId;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "RECEIVED_QTY")
    private Double receivedQuantity;

    @Column(name = "SAMPLE_QTY")
    private Double sampleQuantity;

    @Column(name = "IMPURITIES", columnDefinition = "nvarchar(255)")
    private String impurities;

    @Column(name = "ANALYSIS", columnDefinition = "nvarchar(255)")
    private String analysis;

    @Column(name = "ST_SEC_ID", columnDefinition = "nvarchar(50)")
    private String storageSectionId;

    @Column(name = "REMARK", columnDefinition = "nvarchar(255)")
    private String remarks;

    @Column(name = "STATUS_ID", columnDefinition = "bigint default'0'")
    private Long statusId;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "IS_DELETED", columnDefinition = "bigint default'0'")
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

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(200)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(200)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(200)")
    private String warehouseDescription;

    @Column(name = "PACK_BARCODE")
    private String packBarcodes;

    @Column(name = "PAR_PROD_ORD_NO", columnDefinition = "nvarchar(100)")
    private String parentProductionOrderNo;

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

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
    private String brand;

}
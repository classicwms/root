package com.tekclover.wms.api.transaction.model.mnc;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `TR_NO`,`SRCE_ITM_CODE`
 */
@Table(
        name = "tblinhousetransferline",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_inhousetransferline",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "TR_NO", "SRCE_ITM_CODE"})
        }
)
@IdClass(InhouseTransferLineCompositeKey.class)
public class InhouseTransferLine {

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Id
    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Id
    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Id
    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Id
    @Column(name = "TR_NO")
    private String transferNumber;

    @Id
    @Column(name = "SRCE_ITM_CODE")
    private String sourceItemCode;

    @Column(name = "SRCE_STCK_TYP_ID")
    private Long sourceStockTypeId;

    @Column(name = "SRCE_ST_BIN")
    private String sourceStorageBin;

    @Column(name = "TGT_ITM_CODE")
    private String targetItemCode;

    @Column(name = "TGT_STCK_TYP_ID")
    private Long targetStockTypeId;

    @Column(name = "TGT_ST_BIN")
    private String targetStorageBin;

    @Column(name = "TR_ORD_QTY")
    private Double transferOrderQty;

    @Column(name = "TR_CNF_QTY")
    private Double transferConfirmedQty;

    @Column(name = "TR_UOM")
    private String transferUom;

    @Column(name = "PAL_CODE")
    private String palletCode;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
    private String packBarcodes;

    @Column(name = "SP_ST_IND_ID")
    private Long specialStockIndicatorId;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "BARCODE_ID")
    private String sourceBarcodeId;

    @Column(name = "REF_FIELD_1")
    private String referenceField1;

    @Column(name = "REF_FIELD_2")
    private String referenceField2;

    @Column(name = "REF_FIELD_3")
    private String referenceField3;

    @Column(name = "REF_FIELD_4")
    private String referenceField4;

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

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REMARK")
    private String remarks;

    @Column(name = "IT_CTD_BY")
    private String createdBy;

    @Column(name = "IT_CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "IT_CNF_BY")
    private String confirmedBy;

    @Column(name = "IT_CNF_ON")
    private Date confirmedOn = new Date();

    @Column(name = "IT_UTD_BY")
    private String updatedBy;

    @Column(name = "IT_UTD_ON")
    private Date updatedOn = new Date();

    //Almailem

    @Column(name = "C_TEXT")
    private String companyDescription;

    @Column(name = "PLANT_TEXT")
    private String plantDescription;

    @Column(name = "WH_TEXT")
    private String warehouseDescription;

    @Column(name = "SRCE_STCK_TYP_TEXT")
    private String sourceStockTypeDescription;

    @Column(name = "TGT_STCK_TYP_TEXT")
    private String targetStockTypeDescription;

    @Column(name = "MFR_NAME")
    private String manufacturerName;

    @Column(name = "SRCE_ST_SEC_ID")
    private String sourceStorageSectionId;

    @Column(name = "TGT_ST_SEC_ID")
    private String targetStorageSectionId;

    @Column(name = "TGT_BARCODE_ID")
    private String targetBarcodeId;

    @Column(name = "STATUS_TEXT")
    private String statusDescription;

    @Column(name = "STR_NO")
    private String batchSerialNumber;

    @Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(25)")
    private String partnerCode;

    @Transient
    private Date manufacturerDate;

    @Transient
    private Date expiryDate;
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
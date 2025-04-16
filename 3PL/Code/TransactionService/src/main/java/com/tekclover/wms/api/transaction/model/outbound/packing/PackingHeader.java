package com.tekclover.wms.api.transaction.model.outbound.packing;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `QC_NO`, `PACK_NO`
 */
@Table(
        name = "tblpackingheader",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_packingheader",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", "QC_NO", "PACK_NO"}
                )
        }
)
@IdClass(PackingHeaderCompositeKey.class)
public class PackingHeader {

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Id
    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Id
    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Id
    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Id
    @Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)")
    private String preOutboundNo;

    @Id
    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(50)")
    private String refDocNumber;

    @Id
    @Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(50)")
    private String partnerCode;

    @Id
    @Column(name = "QC_NO", columnDefinition = "nvarchar(50)")
    private String qualityInspectionNo;

    @Id
    @Column(name = "PACK_NO", columnDefinition = "nvarchar(50)")
    private String packingNo;

    @Column(name = "OB_ORD_TYP_ID")
    private Long outboundOrderTypeId;

    @Column(name = "QC_QTY")
    private Double qualityConfirmQty;

    @Column(name = "ITM_CODE")
    private String itemCode;

    @Column(name = "QC_UOM", columnDefinition = "nvarchar(50)")
    private String qualityConfirmUom;

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

    @Column(name = "PACK_CBM")
    private Double packCbm;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(50)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(50)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(50)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(50)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(50)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(50)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(50)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(50)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(50)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(50)")
    private String referenceField10;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "PICK_CNF_QTY")
    private Double pickConfirmQty;

    @Column(name = "ALLOC_QTY")
    private Double allocatedQty;

    @Column(name = "PICK_UOM")
    private String pickUom;

    @Column(name = "STCK_TYP_ID")
    private Long stockTypeId;

    @Column(name = "SP_ST_IND_ID")
    private Long specialStockIndicatorId;

    @Column(name = "REMARK", columnDefinition = "nvarchar(255)")
    private String remark;  // Changed from "remarks" to match the expected DDL

    @Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
    private String packBarcode;

    @Column(name = "PACK_CNF_BY", columnDefinition = "nvarchar(50)")
    private String packConfirmedBy;

    @Column(name = "PACK_CNF_ON", columnDefinition = "nvarchar(50)")
    private Date packConfirmedOn = new Date();

    @Column(name = "PACK_UTD_BY", columnDefinition = "nvarchar(50)")
    private String packUpdatedBy;

    @Column(name = "PACK_UTD_ON", columnDefinition = "nvarchar(50)")
    private Date packUpdatedOn = new Date();

    @Column(name = "PACK_REV_BY", columnDefinition = "nvarchar(50)")
    private String packingReversedBy;

    @Column(name = "PACK_REV_ON")
    private Date packingReversedOn = new Date();

    // 3PL
    @Column(name = "TPL_CBM")
    private Double threePLCbm;

    @Column(name = "TPL_UOM", columnDefinition = "nvarchar(50)")
    private String threePLUom;

    @Column(name = "TPL_BILL_STATUS", columnDefinition = "nvarchar(50)")
    private String threePLBillStatus;

    @Column(name = "TPL_LENGTH")
    private Double threePLLength;

    @Column(name = "TPL_WIDTH")
    private Double threePLHeight;

    @Column(name = "TPL_HEIGHT")
    private Double threePLWidth;

    @Column(name = "RATE")
    private Double rate;

    @Column(name = "CURRENCY")
    private String currency;

}

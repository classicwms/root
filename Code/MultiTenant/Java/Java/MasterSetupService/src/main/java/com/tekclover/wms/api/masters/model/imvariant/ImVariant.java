package com.tekclover.wms.api.masters.model.imvariant;

import com.tekclover.wms.api.masters.model.imstrategies.ImStrategiesCompositeKey;
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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `STR_TYP_ID`, `SEQ_IND`
 */
@Table(
        name = "tblimvariant",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_imvariant",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE", "VAR_ID", "VAR_TYPE"})
        }
)
@IdClass(ImVariantCompositeKey.class)
public class ImVariant {

    @Id
    @Column(name = "LANG_ID",columnDefinition = "nvarchar(10)")
    private String languageId;

    @Id
    @Column(name = "C_ID",columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Id
    @Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
    private String plantId;

    @Id
    @Column(name = "WH_ID",columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Id
    @Column(name = "ITM_CODE",columnDefinition = "nvarchar(50)")
    private String itemCode;

    @Id
    @Column(name="VAR_ID",columnDefinition = "nvarchar(50)")
    private String variantCode;

    @Id
    @Column(name="VAR_TYPE",columnDefinition = "nvarchar(50)")
    private String variantType;

    @Column(name="VAR_SUB_CODE",columnDefinition = "nvarchar(50)")
    private String variantSubCode;

    @Column(name="VAR_IND")
    private Boolean variantIndicator;

    @Column(name = "SPEC_FROM",columnDefinition = "nvarchar(50)")
    private String specificationFrom;

    @Column(name="SPEC_TO",columnDefinition = "nvarchar(50)")
    private String specificationTo;

    @Column(name="SPEC_UOM",columnDefinition = "nvarchar(50)")
    private String specificationUom;

    @Column(name="VAR_BAR_CODE",columnDefinition = "nvarchar(50)")
    private String variantBarcode;

    @Column(name="STATUS_ID",columnDefinition = "nvarchar(50)")
    private String statusId;

    @Column(name = "REF_FIELD_1",columnDefinition = "nvarchar(200)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2",columnDefinition = "nvarchar(200)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3",columnDefinition = "nvarchar(200)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4",columnDefinition = "nvarchar(200)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5",columnDefinition = "nvarchar(200)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6",columnDefinition = "nvarchar(200)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7",columnDefinition = "nvarchar(200)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8",columnDefinition = "nvarchar(200)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9",columnDefinition = "nvarchar(200)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10",columnDefinition = "nvarchar(200)")
    private String referenceField10;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();
}

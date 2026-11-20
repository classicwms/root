package com.tekclover.wms.api.masters.model.impalletization;

import com.tekclover.wms.api.masters.model.impacking.ImPackingCompositeKey;
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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `PACK_MAT_NO`
 */
@Table(
        name = "tblimpalletization",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_impalletization",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","ITM_CODE", "PAL_LVL"})
        }
)
@IdClass(ImPalletizationCompositeKey.class)
public class ImPalletization {

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
        @Column(name="PAL_LVL",columnDefinition = "nvarchar(50)")
        private String palletizationLevel;

        @Column(name="PAL_IND")
        private Boolean palletizationIndicator;

        @Column(name="PAL_LEN")
        private Double palletLength;

        @Column(name="PAL_WID")
        private Double palletWidth;

        @Column(name="PAL_HGT")
        private Double palletHeight;

        @Column(name="PAL_DIM_UOM",columnDefinition = "nvarchar(50)")
        private String palletDimensionUom;

        @Column(name="ITM_PAL_QTY")
        private Double itemPerPalletQuantity;

        @Column(name="CASE_LEN")
        private Double caseLength;

        @Column(name="CASE_WID")
        private Double caseWidth;

        @Column(name="CASE_HGT")
        private Double caseHeight;

        @Column(name="CASE_DIM_UOM",columnDefinition = "nvarchar(50)")
        private String caseDimensionUom;

        @Column(name="ITM_CASE_QTY")
        private Double itemCaseQuantity;

        @Column(name="CASE_PAL_QTY")
        private Double casesPerPallet;

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

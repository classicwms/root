package com.tekclover.wms.api.masters.model.threepl.billing;

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
 *`LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`PARTNER_CODE`,`MOD_ID`'
 */
@Table(
        name = "tblbilling",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_billing",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","PARTNER_CODE", "MOD_ID"})
        }
)
@IdClass(BillingCompositeKey.class)
public class Billing {

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(5)")
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
    @Column(name = "PARTNER_CODE",columnDefinition = "nvarchar(50)")
    private String partnerCode;
    @Id
    @Column(name = "MOD_ID",columnDefinition = "nvarchar(50)")
    private String moduleId;

    @Column(name="BILL_MODE_ID")
    private Long billModeId;

    @Column(name="BILL_FREQ_ID")
    private Long billFrequencyId;

    @Column(name="PAYMENT_TERM_ID")
    private Long paymentTermId;

    @Column(name="PAYMENT_MTD")
    private Long paymentModeId;

    @Column(name="BILL_GEN_IND",columnDefinition = "nvarchar(50)")
    private String billGenerationIndicator;

    @Column(name="STATUS_ID")
    private Long statusId;

    @Column(name="MOD_ID_DESC",columnDefinition = "nvarchar(200)")
    private String moduleIdAndDescription;

    @Column(name="BILL_MODE_ID_DESC",columnDefinition = "nvarchar(200)")
    private String billModeIdAndDescription;

    @Column(name="COMP_ID_DESC",columnDefinition = "nvarchar(200)")
    private String companyIdAndDescription;

    @Column(name="PLANT_ID_DESC",columnDefinition = "nvarchar(200)")
    private String plantIdAndDescription;

    @Column(name="WH_ID_DESC",columnDefinition = "nvarchar(200)")
    private String warehouseIdAndDescription;

    @Column(name="BILL_FREQ_ID_DESC",columnDefinition = "nvarchar(200)")
    private String billFrequencyIdAndDescription;

    @Column(name="PAYMENT_TERM_ID_DESC",columnDefinition = "nvarchar(200)")
    private String paymentTermIdAndDescription;

    @Column(name="PAYMENT_MTD_ID_DESC",columnDefinition = "nvarchar(200)")
    private String paymentModeIdAndDescription;

    @Column(name="IS_DELETED")
    private Long deletionIndicator;
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
    @Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
    private String createdBy;
    @Column(name = "CTD_ON")
    private Date createdOn = new Date();
    @Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;
    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();


}

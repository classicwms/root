package com.tekclover.wms.api.idmaster.model.threepl.servicetypeid;

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
 * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,'MOD_ID',`SER_TYP_ID`
 */
@Table(
        name = "tblservicetypeid",
        uniqueConstraints = {
                @UniqueConstraint (
                        name = "unique_key_servicetypeid",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","MOD_ID","SER_TYP_ID"})
        }
)
@IdClass(ServiceTypeIdCompositeKey.class)
public class ServiceTypeId {

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
    @Column(name = "WH_ID",columnDefinition = "nvarchar(50)")
    private String warehouseId;
    @Id
    @Column(name="MOD_ID")
    private Long moduleId;
    @Id
    @Column(name="SER_TYP_ID")
    private Long serviceTypeId;
    @Column(name="SER_TYP_TEXT",columnDefinition = "nvarchar(200)")
    private String serviceTypeDescription;
    @Column(name="STATUS_ID")
    private Long statusId;
    @Column(name = "IS_DELETED")
    private Long deletionIndicator;
    @Column(name="REF_FIELD_1",columnDefinition = "nvarchar(200)")
    private String referenceField1;
    @Column(name="REF_FIELD_2",columnDefinition = "nvarchar(200)")
    private String referenceField2;
    @Column(name="REF_FIELD_3",columnDefinition = "nvarchar(200)")
    private String referenceField3;
    @Column(name="REF_FIELD_4",columnDefinition = "nvarchar(200)")
    private String referenceField4;
    @Column(name="REF_FIELD_5",columnDefinition = "nvarchar(200)")
    private String referenceField5;
    @Column(name="REF_FIELD_6",columnDefinition = "nvarchar(200)")
    private String referenceField6;
    @Column(name="REF_FIELD_7",columnDefinition = "nvarchar(200)")
    private String referenceField7;
    @Column(name="REF_FIELD_8",columnDefinition = "nvarchar(200)")
    private String referenceField8;
    @Column(name="REF_FIELD_9",columnDefinition = "nvarchar(200)")
    private String referenceField9;
    @Column(name="REF_FIELD_10",columnDefinition = "nvarchar(200)")
    private String referenceField10;
    @Column(name="CTD_BY",columnDefinition = "nvarchar(50)")
    private String createdBy;
    @Column(name="CTD_ON")
    private Date createdOn = new Date();
    @Column(name="UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;
    @Column(name="UTD_ON")
    private Date updatedOn = new Date();



}
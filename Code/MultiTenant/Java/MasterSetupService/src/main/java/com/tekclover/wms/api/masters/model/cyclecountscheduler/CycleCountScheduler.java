package com.tekclover.wms.api.masters.model.cyclecountscheduler;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `CC_TYP_ID','SCHE_NO','LEVEL_ID'
 */
@Table(
        name = "tblcyclecountscheduler",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_cyclecountscheduler",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "CC_TYP_ID", "SCHE_NO","LEVEL_ID"})
        }
)
@IdClass(CycleCountSchedulerCompositeKey.class)
public class CycleCountScheduler {

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
    @Column(name="CC_TYP_ID")
    private Long cycleCountTypeId;

    @Id
    @Column(name="SCHE_NO",columnDefinition = "nvarchar(50)")
    private String schedulerNumber;

    @Id
    @Column(name="LEVEL_ID")
    private Long levelId;

    @Column(name="LVL_REF",columnDefinition = "nvarchar(50)")
    private String levelReference;

    @Column(name="SUB_LVL_REF",columnDefinition = "nvarchar(50)")
    private String SubLevelReference;

    @Column(name="CNT_FREQ",columnDefinition = "nvarchar(50)")
    private String countFrequency;

    @Column(name = "COUNT_DAY",columnDefinition = "nvarchar(50)")
    private String dayOfCount;

    @Column(name="COUNT_DATE")
    private Date countDate=new Date();

    @Column(name="STATUS_ID",columnDefinition = "nvarchar(50)")
    private String statusId;

    @Column(name = "IS_DELETED")
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

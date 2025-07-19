package com.tekclover.wms.api.inbound.transaction.model.dto;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`,'ST_MTD'
 */
@Table(
        name = "tblimbatchserial",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_imbatchserial",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE","ST_MTD"})
        }
)
@IdClass(ImBatchSerialCompositeKey.class)
public class ImBatchSerial {

    @Id
    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
    private String itemCode;

    @Id
    @Column(name="LANG_ID")
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
    @Column(name="ST_MTD")
    private String storageMethod;

    @Column(name="MAINT")
    private String maintenance;

    @Column(name="RGE_FROM")
    private String rangeFrom;

    @Column(name="RGE_TO")
    private String rangeTo;

    @Column(name="CUR_NO")
    private String currentNo;

    @Column(name="SHL_LIF_IND")
    private Boolean shelfLifeIndicator;

    @Column(name="TOT_SHL_LIF")
    private Double totalShelfLife;

    @Column(name="SHL_LIF_PERIOD")
    private String shelfLifePeriod;

    @Column(name="SHL_LIF_NOT")
    private Double shelfLifeNotification;

    @Column(name="SHL_LIF_NOT_PERIOD")
    private String shelfLifeNotificationPeriod;

    @Column(name = "STATUS_ID")
    private String statusId;

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

    @Column(name = "CTD_BY")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();

    @Column(name = "SEQ_IND")
    private Long sequenceIndicator;

}
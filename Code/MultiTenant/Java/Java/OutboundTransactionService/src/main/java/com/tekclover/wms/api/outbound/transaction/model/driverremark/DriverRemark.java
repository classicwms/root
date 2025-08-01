package com.tekclover.wms.api.outbound.transaction.model.driverremark;

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
 * `LANG_ID', 'C_ID', 'PLANT_ID', 'WH_ID', 'DLV_NO`
 */
@Table(
        name = "tbldriverremark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_driverremark",
                        columnNames = {"DRIVER_REMARK_NO", "PRE_OB_NO", "REF_DOC_NO"})
        }
)
@IdClass(DriverRemarkCompositeKey.class)
public class DriverRemark {

    @Id
    @Column(name = "DRIVER_REMARK_NO")
    private String driverRemarkNo;

    @Id
    @Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(25)")
    private String preOutboundNo;

    @Id
    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(25)")
    private String refDocNumber;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Column(name = "VEHICLE_NO")
    private String vehicleNO;

    @Column(name = "DRIVER_NAME")
    private String driverName;

    @Column(name = "REMARKS", columnDefinition = "nvarchar(2000)")
    private String remarks;

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

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "CTD_BY")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

}

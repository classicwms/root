package com.tekclover.wms.api.masters.model.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DRIVER_ID`
 */
@Table(
        name = "tbldriver",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_driver",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "DRIVER_ID"})
        }
)

@IdClass(DriverCompositeKey.class)
public class Driver {

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
    @TableGenerator(name = "driver_id", initialValue = 10000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "driver_id")
    @Column(name="DRIVER_ID")
    private Long driverId;

    @Column(name="DRIVER_NM",columnDefinition = "nvarchar(255)")
    private String driverName;

    @Column(name="PH_NO",columnDefinition = "nvarchar(50)")
    private String phoneNUmber;

    @Column(name="EMAIL_ID",columnDefinition = "nvarchar(50)")
    private String eMailId;

    @Column(name="ADDRESS_1",columnDefinition = "nvarchar(255)")
    private String address1;

    @Column(name="ADDRESS_2",columnDefinition = "nvarchar(255)")
    private String address2;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name="CONFIRM_PASSWORD")
    private String confirmPassword;

    @Column(name="RESET_PASSWORD")
    private Boolean resetPassword;

    @Column(name = "NEW_DELIVERY")
    private Boolean newDelivery;

    @Column(name = "IN_TRANSIT")
    private Boolean inTransit;

    @Column(name = "COMPLETED")
    private Boolean completed;

    @Column(name="RE_ATTEMPT")
    private Boolean reAttempt;

    @Column(name = "RETURNS")
    private Boolean returns;

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

package com.tekclover.wms.api.masters.model.drivervehicleassignment;

import com.tekclover.wms.api.masters.model.driver.DriverCompositeKey;
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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DRIVER_ID','VEHICLE_ID','ROUTE_ID'
 */
@Table(
        name = "tbldrivervehicleassignment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_drivervehicleassignment",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "DRIVER_ID","VEHICLE_ID","ROUTE_ID"})
        }
)
@IdClass(DriverVehicleAssignmentCompositeKey.class)
public class DriverVehicleAssignment {

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
    @Column(name="DRIVER_ID",columnDefinition = "nvarchar(50)")
    private Long driverId;

    @Id
    @Column(name="VEHICLE_ID",columnDefinition = "nvarchar(50)")
    private String vehicleNumber;

    @Id
    @Column(name="ROUTE_ID",columnDefinition = "nvarchar(50)")
    private Long routeId;

    @Column(name="DRIVER_NM",columnDefinition = "nvarchar(255)")
    private String driverName;

    @Column(name="ROUTE_NM",columnDefinition = "nvarchar(255)")
    private String routeName;

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

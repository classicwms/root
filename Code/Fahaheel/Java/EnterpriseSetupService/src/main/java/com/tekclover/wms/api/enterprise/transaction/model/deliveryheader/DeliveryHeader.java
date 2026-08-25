package com.tekclover.wms.api.enterprise.transaction.model.deliveryheader;

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
        name = "tbldeliveryheader",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_deliveryheader",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "DLV_NO"})
        }
)
@IdClass(DeliveryHeaderCompositeKey.class)
public class DeliveryHeader {

    @Id
    @Column(name = "DLV_NO")
    private Long deliveryNo;

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

    @Column(name = "VEHICLE_ID", columnDefinition = "nvarchar(50)")
    private String vehicleNo;

    @Column(name = "DRIVER_ID", columnDefinition = "nvarchar(50)")
    private String driverId;

    @Column(name = "DRIVER_NM", columnDefinition = "nvarchar(255)")
    private String driverName;

    @Column(name = "ROUTE_ID", columnDefinition = "nvarchar(50)")
    private String routeId;

    @Column(name = "DLV_FAIL_REASON", columnDefinition = "nvarchar(500)")
    private String deliveryFailureReason;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "NO_OF_ATTEND", columnDefinition = "nvarchar(50)")
    private String noOfAttend;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(50)")
    private String refDocNumber;

    @Column(name = "REMARKS",columnDefinition = "nvarchar(50)")
    private String remarks;

    @Column(name = "C_DESC", columnDefinition = "nvarchar(500)")
    private String companyDescription;

    @Column(name = "PLANT_DESC",columnDefinition = "nvarchar(500)")
    private String plantDescription;

    @Column(name = "WAREHOUSE_DESC",columnDefinition = "nvarchar(500)")
    private String warehouseDescription;

    @Column(name = "STATUS_DESC",columnDefinition = "nvarchar(500)")
    private String statusDescription;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(200)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(200)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(200)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(200)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(200)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(200)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(200)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(200)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(200)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(200)")
    private String referenceField10;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

}
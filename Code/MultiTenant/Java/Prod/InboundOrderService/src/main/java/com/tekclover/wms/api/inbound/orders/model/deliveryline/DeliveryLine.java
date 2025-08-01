package com.tekclover.wms.api.inbound.orders.model.deliveryline;

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
 * `LANG_ID', 'C_ID', 'PLANT_ID', 'WH_ID', 'DLV_NO`,'ITM_CODE','OB_LINE_NO'
 */
@Table(
        name = "tbldeliveryline",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_deliveryline",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "DLV_NO","ITM_CODE","OB_LINE_NO","INVOICE_NO","REF_DOC_NO"})
        }
)
@IdClass(DeliveryLineCompositeKey.class)
public class DeliveryLine {

    @Id
    @Column(name = "LANG_ID",columnDefinition = "nvarchar(5)")
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
    @Column(name="DLV_NO")
    private Long deliveryNo;

    @Id
    @Column(name="ITM_CODE",columnDefinition = "nvarchar(50)")
    private String itemCode;

    @Id
    @Column(name="OB_LINE_NO")
    private Long lineNumber;

    @Id
    @Column(name="INVOICE_NO",columnDefinition = "nvarchar(50)")
    private String invoiceNumber;

    @Id
    @Column(name="REF_DOC_NO",columnDefinition = "nvarchar(50)")
    private String refDocNumber;

    @Column(name = "VEHICLE_ID", columnDefinition = "nvarchar(50)")
    private String vehicleNo;

    @Column(name = "DRIVER_ID", columnDefinition = "nvarchar(50)")
    private String driverId;

    @Column(name = "DRIVER_NM", columnDefinition = "nvarchar(255)")
    private String driverName;

    @Column(name = "ROUTE_ID", columnDefinition = "nvarchar(50)")
    private String routeId;

    @Column(name = "REMARKS",columnDefinition = "nvarchar(50)")
    private String remarks;

    @Column(name = "PICKED")
    private Boolean picked;

    @Column(name = "DELIVERED")
    private Boolean delivered;

    @Column(name="PARTNER_CODE",columnDefinition = "nvarchar(50)")
    private String partnerCode;

    @Column(name="OB_ORD_TYP_ID")
    private Long outboundOrderTypeId;

    @Column(name = "C_DESC", columnDefinition = "nvarchar(500)")
    private String companyDescription;

    @Column(name = "PLANT_DESC",columnDefinition = "nvarchar(500)")
    private String plantDescription;

    @Column(name = "WAREHOUSE_DESC",columnDefinition = "nvarchar(500)")
    private String warehouseDescription;

    @Column(name = "STATUS_DESC",columnDefinition = "nvarchar(500)")
    private String statusDescription;

    @Column(name="ADD_1",columnDefinition = "nvarchar(500)")
    private String address1;

    @Column(name="ADD_2",columnDefinition = "nvarchar(500)")
    private String address2;

    @Column(name="ZONE",columnDefinition = "nvarchar(500)")
    private String zone;

    @Column(name="COUNTRY",columnDefinition = "nvarchar(50)")
    private String country;

    @Column(name="STATE",columnDefinition = "nvarchar(50)")
    private String state;

    @Column(name="PH_NO",columnDefinition = "nvarchar(500)")
    private String phoneNumber;

    @Column(name="MAIL_ID",columnDefinition = "nvarchar(500)")
    private String eMailId;

    @Column(name="TEXT",columnDefinition = "nvarchar(500)")
    private String description;

    @Column(name="MFR_CODE",columnDefinition = "nvarchar(200)")
    private String manufacturerCode;

    @Column(name="DLV_CNF_QTY",columnDefinition = "nvarchar(50)")
    private String deliveryQty;

    @Column(name="DLV_UOM",columnDefinition = "nvarchar(50)")
    private String deliveryUom;

    @Column(name = "NO_ATTEMPT")
    private Long noOfAttempts;

    @Column(name="DIG_SIGN")
    private String digitalSignature;

    @Column(name="COMP_REASON",columnDefinition = "nvarchar(500)")
    private String complaintReason;

    @Column(name="DLV_FAIL_REASON",columnDefinition = "nvarchar(500)")
    private String deliveryFailiureReason;

    @Column(name="PAYMENT_MODE",columnDefinition = "nvarchar(50)")
    private String paymentMode;

    @Column(name = "RE_DELIVERY")
    private Boolean reDelivered;

    @Column(name="STATUS_ID")
    private Long statusId;

    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

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

    @Column(name="DLV_CNF_BY",columnDefinition = "nvarchar(50)")
    private String deliveryConfirmedBy;

    @Column(name="DLV_CNF_ON")
    private Date deliveryConfirmedOn;

    @Column(name = "DLV_CTD_BY",columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "DLV_CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "DLV_UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "DLV_UTD_ON")
    private Date updatedOn;

}


package com.tekclover.wms.api.inbound.transaction.model.threepl.proformainvoiceline;


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
 * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`PROFORMA_BILL_NO`,`PARTNER_CODE`,'LINE_NO'
 */
@Table(
        name = "tblproformainvoiceline",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_proformainvoiceline",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","PROFORMA_BILL_NO", "PARTNER_CODE","LINE_NO"})
        }
)
@IdClass(ProformaInvoiceLineCompositeKey.class)
public class ProformaInvoiceLine {
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
    @Column(name="PROFORMA_BILL_NO",columnDefinition = "nvarchar(50)")
    private String proformaBillNo;
    @Id
    @Column(name="PARTNER_CODE",columnDefinition = "nvarchar(50)")
    private String partnerCode;
    @Id
    @Column(name="LINE_NO")
    private Long lineNumber;
    @Column(name="LINE_TEXT",columnDefinition = "nvarchar(200)")
    private String description;
    @Column(name="PROFORMA_BILL_AMT_LINE")
    private Double proformaBillAmountLine;
    @Column(name="BILL_UNIT",columnDefinition ="nvarchar(50)")
    private String billUnit;
    @Column(name="BILL_QTY")
    private Double billQuantity;
    @Column(name="PRICE_UNIT",columnDefinition = "nvarchar(50)")
    private String priceUnit;
    @Column(name="PROFORMA_BILL_DATE")
    private Date invoiceDate;
    @Column(name="STATUS_ID")
    private Long statusId;

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

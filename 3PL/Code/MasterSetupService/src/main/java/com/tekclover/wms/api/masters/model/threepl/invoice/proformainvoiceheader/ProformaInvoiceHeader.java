package com.tekclover.wms.api.masters.model.threepl.invoice.proformainvoiceheader;

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
 *`LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`PROFORMA_BILL_NO`,`PARTNER_CODE`'
 */
@Table(
        name = "tblproformainvoiceheader",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_proformainvoiceheader",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","PROFORMA_BILL_NO", "PARTNER_CODE"})
        }
)
@IdClass(ProformaInvoiceHeaderCompositeKey.class)
public class ProformaInvoiceHeader {

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
    @Column(name="PROFORMA_BILL_AMT")
    private Double proformaBillAmount;
    @Column(name="BILL_UNIT",columnDefinition = "nvarchar(50)")
    private String billUnit;
    @Column(name="BILL_QTY")
    private Double billQuantity;
    @Column(name="PRICE_UNIT",columnDefinition = "nvarchar(50)")
    private String priceUnit;
    @Column(name="BILL_DATE_FROM")
    private Date billDateFrom;
    @Column(name="BILL_DATE_TO")
    private Date billDateTo;
    @Column(name="PROFORMA_BILL_DATE")
    private Date proformaBillDate;
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
    @Column(name = "TOTAL_CBM")
    private Double totalCbm;
}

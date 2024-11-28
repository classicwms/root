package com.tekclover.wms.api.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblbillingtransactionreportresult")
public class BillingTransactionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BTR_ID")
    private Long btrId;
    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;
    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;
    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;
    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;
    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;
    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;
    @Column(name = "SKU", columnDefinition = "nvarchar(255)")
    private String sku;
    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(200)")
    private String mfrName;
    @Column(name = "DESCRIPTION", columnDefinition = "nvarchar(255)")
    private String description;
    @Column(name = "CUSTOMER", columnDefinition = "nvarchar(255)")
    private String customer;
    @Column(name = "MODULE", columnDefinition = "nvarchar(100)")
    private String module;
    @Column(name = "ORDER_NO", columnDefinition = "nvarchar(50)")
    private String orderNo;
    @Column(name = "TRANSACTION_QTY")
    private Double transactionQty;
    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;
    @Column(name = "CBM_PER_QTY")
    private Double cbmPerQty;
    @Column(name = "CHARGE_VALUE")
    private Double chargeValue;
    @Column(name = "RATE_UNIT")
    private Double rateUnit;
    @Column(name = "TOTAL_VALUE")
    private Double totalValue;
    @Column(name = "CHARGE_UNIT")
    private String chargeUnit;
    @Column(name = "SERVICE_TYPE")
    private String serviceType;
    @Column(name = "CURRENCY")
    private String currency;

}
package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class BillingTransactionReport {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String sku;
    private String mfrName;
    private String description;
    private String customer;
    private String module;
    private String orderNo;
    private Double transactionQty;
    private Date transactionDate;
    private Double cbmPerQty;
    private Double chargeValue;
    private Double rateUnit;
    private Double totalValue;
    private String chargeUnit;
    private String serviceType;
    private String currency;
}
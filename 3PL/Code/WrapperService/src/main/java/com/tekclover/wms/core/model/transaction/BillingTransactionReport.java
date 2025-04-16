package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class BillingTransactionReport {

    private Long btrId;
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
    private String amount;
    private String partnerCode;
    private Double transactionQty;
    private Date transactionDate;
    private Double cbmPerQty;
    private Double chargeValue;
    private Double rateUnit;
    private Double totalValue;
    private String chargeUnit;
    private String serviceTypeId;
    private String currency;
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    private Double rate;
    private Date fromDate;
    private Date toDate;
    private String itemCode;
    private String referenceOrderNo;
    private String refDocNumber;
}

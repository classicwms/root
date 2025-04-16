package com.tekclover.wms.api.transaction.model.report;

import java.util.Date;

public interface BillingTransactionReportImpl {
    public String getLanguageId();
    public String getCompanyCodeId();
    public String getPlantId();
    public String getWarehouseId();
    public String getPartnerCode();
    public String getServiceTypeId();
    public Date getCreatedOn();
    public String getThreePLUom();
    public Double getThreePLCbm();
    public String getCurrency();
    public Double getRate();
    public String getPartnerName();
    public String getDescription();
    public String getCompanyDescription();
    public String getPlantDescription();
    public String getWarehouseDescription();
    public Double getOrderQty();
    public Double getPickConfirmQty();
    public Double getInventoryQuantity();
    public String getItemCode();
    String getReferenceOrderNo();
    String getRefDocNumber();
    Date getFromDate();
    Date getToDate();
    String getBusinessPartnerCode();

}

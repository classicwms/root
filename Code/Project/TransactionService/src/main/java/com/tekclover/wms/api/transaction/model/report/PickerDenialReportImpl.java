package com.tekclover.wms.api.transaction.model.report;

import java.util.Date;

public interface PickerDenialReportImpl {

    String getCompanyCodeId();

    String getPlantId();

    String getLanguageId();

    String getWarehouseId();

    String getPreOutboundNo();

    String getRefDocNumber();

    String getPartnerCode();

    Long getLineNumber();

    String getPickupNumber();

    String getItemCode();

    String getActualHeNo();

    String getPickedStorageBin();

    String getPickedPackCode();

    Long getOutboundOrderTypeId();

    Double getPickConfirmQty();

    Double getAllocatedQty();

    String getPickUom();

    String getDescription();

    String getAssignedPickerId();

    String getPickupCreatedBy();

    String getPickupConfirmedBy();

    Date getDenialDate();
    Date getDeliverConfirmedOn();

    String getSDeliveryDate();
    String getSDenialDate();
    String getRemarks();
    String getPartnerName();
    String getOrderType();
    Long getDenialCount();
    Long getSkuDenied();
    Double getPercentageShipped();
    Long getOrderedQty();
    Long getShippedQty();
    Long getSkuOrdered();
    Long getSkuShipped();
    String getType();
}
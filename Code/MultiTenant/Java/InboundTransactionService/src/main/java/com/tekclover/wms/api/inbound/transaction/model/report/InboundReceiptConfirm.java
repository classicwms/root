package com.tekclover.wms.api.inbound.transaction.model.report;

import java.util.Date;

public interface InboundReceiptConfirm {

    Double getOrderQty();

    String getItemCode();

    String getManufacturerName();
    Double getAcceptedQty();

    Double getDamageQty();

    Double getNoBags();

    String getDescription();

    Double getMissingQty();

     Date getManufacturerDate();

     Date getExpiryDate();
     String getBarcodeId();

}

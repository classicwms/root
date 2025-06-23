package com.tekclover.wms.api.inbound.orders.model.deliveryline;


import lombok.Data;

@Data
public class FindDeliveryLineCount {

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String driverId;
}

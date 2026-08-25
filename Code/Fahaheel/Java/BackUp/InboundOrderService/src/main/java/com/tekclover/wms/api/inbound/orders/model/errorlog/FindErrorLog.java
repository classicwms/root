package com.tekclover.wms.api.inbound.orders.model.errorlog;

import lombok.Data;

import java.util.List;

@Data
public class FindErrorLog {

    List<String> companyCodeId;
    List<String> languageId;
    List<String> warehouseId;
    List<String> plantId;
    List<Long> orderId;
}

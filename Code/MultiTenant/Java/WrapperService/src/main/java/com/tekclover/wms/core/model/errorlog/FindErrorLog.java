package com.tekclover.wms.core.model.errorlog;

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

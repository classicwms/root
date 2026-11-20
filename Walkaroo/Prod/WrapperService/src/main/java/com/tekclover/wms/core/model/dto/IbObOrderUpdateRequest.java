package com.tekclover.wms.core.model.dto;


import lombok.Data;

import java.util.List;

@Data
public class IbObOrderUpdateRequest {

    private String companyCode;
    private String languageId;
    private String branchCode;
    private String warehouseID;
    private List<String> orderId;
}

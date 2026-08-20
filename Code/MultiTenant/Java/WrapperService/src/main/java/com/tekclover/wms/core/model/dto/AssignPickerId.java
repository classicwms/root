package com.tekclover.wms.core.model.dto;
import lombok.Data;

@Data
public class AssignPickerId {

    private String companyId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNumber;
    private String preOutboundNo;
    private String itemCode;
    private String barcode;
    private Long lineNo;
    private String proposedStorageBin;
    private String palletCode;
    private Double orderQty;
    private String partnerCode;
    private String proposedPackCode;
}
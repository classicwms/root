package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
public class DeliveryConfirmationPdfInput {

    private String warehouseId;
    private String fromDeliveryDate;
    private String toDeliveryDate;
    private String storeCode;
    private List<String> soType;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String loginUserID;
    private Boolean webPortal;
}

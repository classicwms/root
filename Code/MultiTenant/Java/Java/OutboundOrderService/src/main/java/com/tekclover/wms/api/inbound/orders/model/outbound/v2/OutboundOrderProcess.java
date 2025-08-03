package com.tekclover.wms.api.inbound.orders.model.outbound.v2;

import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.OutboundIntegrationLog;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import lombok.Data;

import java.util.List;

@Data
public class OutboundOrderProcess {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNumber;
    private Long outboundOrderTypeId;
    private String loginUserId;

    //Header
    OutboundIntegrationHeaderV2 outboundIntegrationHeader;
    List<OutboundIntegrationLineV2> outboundIntegrationLines;
    OutboundIntegrationLog outboundIntegrationLog;

    PreOutboundHeaderV2 preOutboundHeader;
    OutboundHeaderV2 outboundHeader;
    OrderManagementHeaderV2 orderManagementHeader;

    //Lines
    List<PreOutboundLineV2> preOutboundLines;
    List<OutboundLineV2> outboundLines;
    List<OrderManagementLineV2> orderManagementLines;
}
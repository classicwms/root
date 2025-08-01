package com.tekclover.wms.api.inbound.orders.model.dto;

import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import lombok.Data;

import java.util.List;

@Data
public class InboundOrderProcess {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNumber;
    private Long inboundOrderTypeId;
    private String loginUserId;

    //Header
    InboundIntegrationHeader inboundIntegrationHeader;

    List<InboundIntegrationLine> inboundIntegrationLines;
    PreInboundHeaderEntityV2 preInboundHeader;
    InboundHeaderV2 inboundHeader;
    StagingHeaderV2 stagingHeader;
    GrHeaderV2 grHeader;
    List<PutAwayHeaderV2> putAwayHeaders;
    InboundIntegrationLog inboundIntegrationLog;
    //Lines
    List<PreInboundLineEntityV2> preInboundLines;
    List<InboundLineV2> inboundLines;
    List<StagingLineEntityV2> stagingLines;
    List<GrLineV2> grLines;
    List<PutAwayLineV2> putAwayLines;
    List<ImPartner> imPartnerList;
}
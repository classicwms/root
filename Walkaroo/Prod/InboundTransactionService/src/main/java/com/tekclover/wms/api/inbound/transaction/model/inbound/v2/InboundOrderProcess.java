package com.tekclover.wms.api.inbound.transaction.model.inbound.v2;

import com.tekclover.wms.api.inbound.transaction.model.dto.ImPartner;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
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
    InboundIntegrationLog inboundIntegrationLog;

    PreInboundHeaderEntityV2 preInboundHeader;
    InboundHeaderV2 inboundHeader;
    StagingHeaderV2 stagingHeader;
    GrHeaderV2 grHeader;
    List<PutAwayHeaderV2> putAwayHeaders;

    //Lines
    List<PreInboundLineEntityV2> preInboundLines;
    List<InboundLineV2> inboundLines;
    List<StagingLineEntityV2> stagingLines;
    List<GrLineV2> grLines;
    List<PutAwayLineV2> putAwayLines;
    List<ImPartner> imPartnerList;
}
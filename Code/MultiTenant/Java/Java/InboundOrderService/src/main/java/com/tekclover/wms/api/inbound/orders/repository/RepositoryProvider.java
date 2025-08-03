package com.tekclover.wms.api.inbound.orders.repository;


import com.tekclover.wms.api.inbound.orders.service.AuthTokenService;
import com.tekclover.wms.api.inbound.orders.service.MastersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryProvider {

    public final AuthTokenService authTokenService;
    public final MastersService mastersService;

    public final PreInboundLineV2Repository preInboundLineV2Repository;
    public final InboundLineV2Repository inboundLineV2Repository;
    public final StagingLineV2Repository stagingLineV2Repository;
    public final GrHeaderV2Repository grHeaderV2Repository;
    public final StagingHeaderV2Repository stagingHeaderV2Repository;
    public final PutAwayHeaderV2Repository putAwayHeaderV2Repository;
    public final PreInboundHeaderV2Repository preInboundHeaderV2Repository;
    public final WarehouseRepository warehouseRepository;
    public final ImBasicData1V2Repository imBasicData1V2Repository;
    public final InboundHeaderV2Repository inboundHeaderV2Repository;
    public final ImBasicData1Repository imBasicData1Repository;
    public final ImPartnerRepository imPartnerRepository;
    public final OrderManagementLineV2Repository orderManagementLineV2Repository;

}

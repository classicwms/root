package com.tekclover.wms.api.inbound.orders.repository;


import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.service.MastersService;
import com.tekclover.wms.api.inbound.orders.service.OrderManagementLineService;
import com.tekclover.wms.api.inbound.orders.service.OrderService;
import com.tekclover.wms.api.inbound.orders.service.StrategiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoryProvider {

    public final MastersService mastersService;
    public final OrderService orderService;
    public final OrderManagementLineService orderManagementLineService;

    public final ImBasicData1Repository imBasicData1Repository;
    public final WarehouseRepository warehouseRepository;
    public final PreOutboundLineV2Repository preOutboundLineV2Repository;
    public final PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    public final OutboundHeaderV2Repository outboundHeaderV2Repository;
    public final OutboundLineV2Repository outboundLineV2Repository;
    public final OrderManagementLineV2Repository orderManagementLineV2Repository;
    public final OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
    public final ImBasicData1V2Repository imBasicData1V2Repository;
    public final PropertiesConfig propertiesConfig;
    public final InventoryV2Repository inventoryV2Repository;
    public final StrategiesService strategiesService;
}

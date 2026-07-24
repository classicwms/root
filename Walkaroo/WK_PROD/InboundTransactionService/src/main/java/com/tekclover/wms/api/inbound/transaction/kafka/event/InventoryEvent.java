package com.tekclover.wms.api.inbound.transaction.kafka.event;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class InventoryEvent {

    private InventoryV2 inventoryV2;
}

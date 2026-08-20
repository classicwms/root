package com.tekclover.wms.api.outbound.transaction.kafka.event;


import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.trans.InventoryTrans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySaveEvent {

    private InventoryV2 inventoryV2;

}

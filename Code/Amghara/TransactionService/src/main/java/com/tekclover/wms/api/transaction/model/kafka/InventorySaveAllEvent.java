package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySaveAllEvent {

    List<InventoryV2> inventory = new ArrayList<>();

}

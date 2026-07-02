package com.tekclover.wms.api.inbound.transaction.model.pickup;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import lombok.Data;

import java.util.List;

@Data
public class AdditionalBin {
	private List<Inventory> additionalBins;
}

package com.tekclover.wms.api.transaction.model.outbound.pickup;

import java.util.List;

import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;

import lombok.Data;

@Data
public class AdditionalBin {
	private List<Inventory> additionalBins;
}

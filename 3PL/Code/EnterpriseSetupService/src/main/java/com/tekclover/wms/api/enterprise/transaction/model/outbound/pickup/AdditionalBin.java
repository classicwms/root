package com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup;

import java.util.List;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.inventory.Inventory;

import lombok.Data;

@Data
public class AdditionalBin {
	private List<Inventory> additionalBins;
}
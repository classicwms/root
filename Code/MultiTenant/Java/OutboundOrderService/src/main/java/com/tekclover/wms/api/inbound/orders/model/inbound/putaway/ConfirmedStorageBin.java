package com.tekclover.wms.api.inbound.orders.model.inbound.putaway;

import lombok.Data;

@Data
public class ConfirmedStorageBin {
	private String storageBin;
	private Double putawayConfirmedQty;
}

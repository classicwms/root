package com.tekclover.wms.api.outbound.transaction.model.inbound.putaway;

import lombok.Data;

@Data
public class ConfirmedStorageBin {
	private String storageBin;
	private Double putawayConfirmedQty;
}

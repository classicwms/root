package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class ConfirmedStorageBin {
	private String storageBin;
	private Double putawayConfirmedQty;
}

package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class ShipmentDispatch {

	private ShipmentDispatchHeader header;
	private List<ShipmentDispatchList> shipmentDispatchList;
}


package com.tekclover.wms.api.transaction.model.dto;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.DeliveryConfirmationLineV3;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryConfirmationImplV3 {

	List<IKeyValuePair> proposedBarcodes;
	List<DeliveryConfirmationLineV3> deliveryConfirmationLines;
}
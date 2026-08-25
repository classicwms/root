package com.tekclover.wms.api.outbound.transaction.model.outbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import lombok.Data;

import java.util.List;

@Data
public class PickListCancellation {

	private OutboundHeaderV2 oldOutboundHeader;
	private List<OutboundLineV2> oldOutboundLineList;
	private List<PickupLineV2> oldPickupLineList;
	private List<PickupLineV2> newPickupLineList;
	private List<OrderManagementLineV2> oldOrderManagementLineList;
	private List<QualityLineV2> oldQualityLineList;
	private String oldPickListNumber;
	private String newPickListNumber;

}
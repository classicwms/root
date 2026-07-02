package com.tekclover.wms.api.transaction.model.outbound.v2;

import java.util.List;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;

import lombok.Data;

@Data
public class PickListCancellation {

	private OutboundHeaderV2 oldOutboundHeader;
	private List<OutboundLineV2> oldOutboundLineList;
	private List<PickupLineV2> oldPickupLineList;
	private List<PickupLineV2> newPickupLineList;
	private List<OrderManagementLineV2> oldOrderManagementLineList;
	private List<QualityLineV2> oldQualityLineList;
	private String oldSalesOrderNumber;
	private String newSalesOrderNumber;
	public String oldPickListNumber;
	public String newPickListNumber;
	
	//
	public String oldRefDocNumber;
	public String newRefDocNumber;
}
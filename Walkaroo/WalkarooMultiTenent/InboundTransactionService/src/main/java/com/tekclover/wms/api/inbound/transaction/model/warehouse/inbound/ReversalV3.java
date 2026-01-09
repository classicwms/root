package com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound;

import lombok.Data;

import java.util.List;

@Data
public class ReversalV3 {
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String loginUserId;
	
    private List<ReversalLineV3> lines;
}

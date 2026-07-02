package com.tekclover.wms.core.model.warehouse.inbound.walkaroo;

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

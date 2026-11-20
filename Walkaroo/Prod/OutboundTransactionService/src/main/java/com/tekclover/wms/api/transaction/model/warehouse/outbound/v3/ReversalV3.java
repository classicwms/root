package com.tekclover.wms.api.transaction.model.warehouse.outbound.v3;

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

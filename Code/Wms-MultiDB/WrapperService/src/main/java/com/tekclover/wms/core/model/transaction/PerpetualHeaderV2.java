package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PerpetualHeaderV2 extends PerpetualHeader {

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
	private String middlewareId;
	private String middlewareTable;
	private String referenceDocumentType;
	private String referenceCycleCountNo;

}
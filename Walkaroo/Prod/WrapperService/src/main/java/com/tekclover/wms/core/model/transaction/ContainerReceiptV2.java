package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContainerReceiptV2 extends ContainerReceipt {

	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
}

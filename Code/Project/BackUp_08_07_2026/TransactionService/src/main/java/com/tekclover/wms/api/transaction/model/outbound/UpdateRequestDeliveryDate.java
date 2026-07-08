package com.tekclover.wms.api.transaction.model.outbound;

import java.util.Date;
import lombok.Data;

@Data
public class UpdateRequestDeliveryDate {

	private String warehouseId;
	private String refDocNumber;
	private Date requiredDeliveryDate;
}

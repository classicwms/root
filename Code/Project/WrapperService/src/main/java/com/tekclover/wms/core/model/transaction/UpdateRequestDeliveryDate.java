package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import lombok.Data;

@Data
public class UpdateRequestDeliveryDate {

	private String warehouseId;
	private String refDocNumber;
	private Date requiredDeliveryDate;
}

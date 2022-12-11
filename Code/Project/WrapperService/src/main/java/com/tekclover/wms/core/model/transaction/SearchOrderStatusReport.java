package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class SearchOrderStatusReport {
	 
	private String warehouseId;
	@NotNull
	@NotEmpty
	private String fromDeliveryDate;
	@NotNull
	@NotEmpty
	private String toDeliveryDate;
	private List<String> customerCode;
	private List<String> orderNumber;
	private List<String> orderType;
	private List<Long> statusId;
	}

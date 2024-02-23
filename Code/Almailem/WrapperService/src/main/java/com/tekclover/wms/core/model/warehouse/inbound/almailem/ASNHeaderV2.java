package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ASNHeaderV2  {

	@NotBlank(message = "To Branch Code is mandatory")
	private String branchCode;

	@NotBlank(message = "To CompanyCode is mandatory")
	private String companyCode;

	@NotBlank(message = "ASN number is mandatory")
	private String asnNumber;

	//almailem fields
	private String purchaseOrderNumber;
}

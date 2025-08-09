package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOrderStatusReport {

	/*
	 * LANG_ID          - Single
	 * C_ID             - Single
	 * PLANT_ID         - Single
	 * WH_ID			- Single
	 * DLV_CNF_ON
	 * PARTNER_CODE		- Multi select (Optional)
	 * REF_DOC_NO		- Multi select (Optional)
	 * REF_FIELD_1		- Multi select (Optional)
	 * STATUS_ID		- Multi select (Optional)
	 */

	// For Reports
	private String languageId;          // LANG_ID
	private String companyCodeId;       // C_ID
	private String plantId;             // PLANT_ID
	private String warehouseId;         // WH_ID
	private Date fromDeliveryDate;      // DLV_CNF_ON
	private Date toDeliveryDate;        // DLV_CNF_ON
	private List<String> partnerCode;   // PARTNER_CODE
	private List<String> refDocNumber;  // REF_DOC_NO
	private List<String> orderType;     // REF_FIELD_1
	private List<Long> statusId;        // STATUS_ID

	private List<String> customerCode;
	private List<String> orderNumber;

	private String itemCode;

}

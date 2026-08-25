package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AssignPickerV2 extends AssignPicker {

	private String languageId;
	private String companyCodeId;
	private String plantId;

}
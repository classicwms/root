package com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2;

import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.AssignPicker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AssignPickerV2 extends AssignPicker {

	private String languageId;
	private String companyCodeId;
	private String plantId;

}
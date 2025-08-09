package com.tekclover.wms.api.outbound.transaction.model.inbound.containerreceipt.v2;

import com.tekclover.wms.api.outbound.transaction.model.inbound.containerreceipt.SearchContainerReceipt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchContainerReceiptV2 extends SearchContainerReceipt {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> refDocNumber;
}
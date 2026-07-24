package com.tekclover.wms.api.inbound.transaction.model.mnc;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InhouseTransferUpload {

	@Valid
	private InhouseTransferHeader inhouseTransferHeader;

	@Valid
	private List<InhouseTransferLine> inhouseTransferLine;
}

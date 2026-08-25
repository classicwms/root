package com.tekclover.wms.api.outbound.transaction.model.mnc;

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

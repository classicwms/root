package com.tekclover.wms.core.model.transaction;

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

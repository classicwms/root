package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class PerpetualUpdateResponse {
	private List<PerpetualLine> perpetualLines;
	private PerpetualHeader perpetualHeader;
}

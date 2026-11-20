package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PerpetualUpdateResponseV2 {
	private List<PerpetualLineV2> perpetualLines;
	private PerpetualHeaderV2 perpetualHeader;
}

package com.tekclover.wms.api.transaction.model.inbound.inboundquality;

import lombok.Data;

import java.io.Serializable;

@Data
public class InboundQualityLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String languageId;
	private String refDocNumber;
	private String preInboundNo;
	private String inboundQualityNumber;
	private String itemCode;
	private Long lineNo;
}
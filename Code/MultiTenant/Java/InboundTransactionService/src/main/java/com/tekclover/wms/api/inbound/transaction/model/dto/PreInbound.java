package com.tekclover.wms.api.inbound.transaction.model.dto;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundHeaderEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundLineEntity;

import lombok.Data;

@Data
public class PreInbound {

	private PreInboundHeaderEntity preInboundHeader;
	private PreInboundLineEntity preInboundLine;
}

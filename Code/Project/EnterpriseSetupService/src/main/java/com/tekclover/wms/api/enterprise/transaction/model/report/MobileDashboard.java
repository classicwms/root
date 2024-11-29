package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

@Data
public class MobileDashboard {
	private InboundCount inboundCount;
	private OutboundCount outboundCount;
	private StockCount stockCount;
	
	@Data
	public class InboundCount {
		private Long cases;
		private Long putaway;
		private Long reversals;
	}
	
	@Data
	public class OutboundCount {
		private Long picking;
		private Long quality;
		private Long reversals;
	}
	
	@Data
	public class StockCount {
		private Long perpertual;
		private Long periodic;
	}
}
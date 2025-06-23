package com.tekclover.wms.api.inbound.orders.model.report;

import lombok.Data;

@Data
public class Dashboard {
	private Day day;
	private Month month;
	private	BinStatus binStatus;
	
	@Data
	public class Day {
		private Receipts receipts;
		private Shipping shipping;
		
		@Data
		public class Receipts {
			private Long awaitingASN;
			private Long containerReceived;
			private Long itemReceived;
		}
		
		@Data
		public class Shipping {
			private Long shippedLine;
			private Long normal;
			private Long special;
		}
	}

	@Data
	public class Month {
		private Receipts receipts;
		private Shipping shipping;
		
		@Data
		public class Receipts {
			private Long awaitingASN;
			private Long containerReceived;
			private Long itemReceived;
		}
		
		@Data
		public class Shipping {
			private Long shippedLine;
			private Long normal;
			private Long special;
		}
	}
	
	@Data
	public class BinStatus {
		private Long statusEqualToZeroCount;
		private Long statusNotEqualToZeroCount;
	}
}
package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FastSlowMovingDashboard {
	private Day day;
	private Month month;
	
	@Data
	public class Day {
		private List<ItemData> itemData = new ArrayList<>();
	}

	@Data
	public class Month {
		private List<ItemData> itemData = new ArrayList<>();
	}


	@Data
	public static class ItemData {
		private String itemCode;
		private String itemText;
		private Double deliveryQuantity;
		private String type;
	}
}
package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class FastSlowMovingDashboard {
	private Day day;
	private Month month;
	
	@Data
	@Getter
	@Setter
	public class Day {
		private List<ItemData> itemData = new ArrayList<>();
	}

	@Data
	@Getter
	@Setter
	public class Month {
		private List<ItemData> itemData = new ArrayList<>();
	}


	@Data
	@Getter
	@Setter
	public static class ItemData {
		private String itemCode;
		private String itemText;
		private Double deliveryQuantity;
		private String type;
	}

	public interface ItemDataImpl {
		String getItemCode();
		String getItemText();
		Double getDeliveryQuantity();
	}
}
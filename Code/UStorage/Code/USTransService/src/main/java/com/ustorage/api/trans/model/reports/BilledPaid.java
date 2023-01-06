package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class BilledPaid {

	private List<Float> ustorageBilled;
	private List<Float> ustoragePaid;
	private List<Float> ulogisticsBilled;
	private List<Float> ulogisticsPaid;

}

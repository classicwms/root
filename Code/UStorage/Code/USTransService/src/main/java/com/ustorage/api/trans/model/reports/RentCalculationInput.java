package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class RentCalculationInput {
	private String period;
	private long rent;
	private Date startDate;
	private Date endDate;

}

package com.ustorage.core.model.trans;

import lombok.Data;

@Data
public class Rent {

	private String rentPeriod;
	private long period;
	private long days;
	private double totalRent;
}

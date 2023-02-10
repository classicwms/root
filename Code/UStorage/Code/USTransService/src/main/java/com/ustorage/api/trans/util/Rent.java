package com.ustorage.api.trans.util;

import lombok.Data;

@Data
public class Rent {

	private String rentPeriod;
	private long period;
	private long days;
	private double totalRent;
}

package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class LeadAndCustomer {

	private List<Integer> lead;
	private List<Integer> customer;

	private List<Integer> ullead;
	private List<Integer> ulCustomer;

}

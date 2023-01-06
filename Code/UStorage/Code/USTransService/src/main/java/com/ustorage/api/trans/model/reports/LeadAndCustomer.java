package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class LeadAndCustomer {

	private List<Integer> lead;
	private List<Integer> Customer;

}

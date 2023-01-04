package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class Dropdown {

	private List<KeyValuePair> customer;
	private List<KeyValue> agreement;
	private List<KeyValue> invoice;
	private List<KeyValue> payment;
	private List<KeyValue> workorder;
	private List<KeyValue> quote;
	private List<KeyValue> Inquiry;

}

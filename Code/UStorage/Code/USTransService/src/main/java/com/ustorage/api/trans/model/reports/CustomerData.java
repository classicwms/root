package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class CustomerData {

	private List<String> customerCode;

	private List<String> customerName;
}

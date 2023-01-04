package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class ContractRenewalStatus {

	private List<String> phase;
	private List<String> storeNumber;
	private List<String> storageType;
}

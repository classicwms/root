package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class ContractRenewalStatus {

	private List<String> phase;
	private List<String> storeNumber;
	private List<String> storageType;
}

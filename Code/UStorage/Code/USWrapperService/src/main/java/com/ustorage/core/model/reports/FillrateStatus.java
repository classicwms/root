package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class FillrateStatus {

	private List<String> phase;
	private List<String> storeNumber;
	private List<String> storageType;
	private List<String> status;
}

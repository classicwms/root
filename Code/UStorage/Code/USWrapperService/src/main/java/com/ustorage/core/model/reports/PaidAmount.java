package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class PaidAmount {

	private List<String> ulogistics;
	private List<String> ustorage;

}

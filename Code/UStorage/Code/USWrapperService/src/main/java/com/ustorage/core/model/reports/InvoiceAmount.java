package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceAmount {

	private List<String> ulogistics;
	private List<String> ustorage;

}

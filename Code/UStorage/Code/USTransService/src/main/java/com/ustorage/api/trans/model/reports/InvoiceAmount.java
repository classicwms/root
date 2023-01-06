package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceAmount {

	private List<Float> billed;

}

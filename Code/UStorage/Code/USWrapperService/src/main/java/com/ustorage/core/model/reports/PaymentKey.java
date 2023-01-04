package com.ustorage.core.model.reports;

import lombok.Data;

@Data
public class PaymentKey {

	private String voucherId;
	private String voucherAmount;
	private String voucherStatus;
	private String remarks;

}

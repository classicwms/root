package com.ustorage.api.trans.model.reports;

import java.util.Date;

public interface IPaymentDue {

	String getDueStatus();
	String getDueAmount();
	Date getDueDate();
	String getDueDays();
	String getModeOfPayment();
	Date getStartDate();
	Date getEndDate();
	Date getLastPaidDate();
	String getRentPeriod();
	String getLastPaidVoucherAmount();
}

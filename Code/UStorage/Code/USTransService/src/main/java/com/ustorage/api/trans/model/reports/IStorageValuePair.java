package com.ustorage.api.trans.model.reports;

import java.util.Date;

public interface IStorageValuePair {

	String getStoreNumber();
	String getSize();
	String getStorageType();
	String getPhase();

	String getLastPaidVoucherId();
	String getAgreementNumber();
	String getCustomerCode();

}

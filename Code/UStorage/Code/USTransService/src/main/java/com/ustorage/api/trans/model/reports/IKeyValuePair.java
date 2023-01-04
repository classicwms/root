package com.ustorage.api.trans.model.reports;

import java.util.Date;

public interface IKeyValuePair {

	String getDocumentNumber();
	String getTotal();
	String getStatus();
	String getNotes();
	Date getDocumentDate();


}

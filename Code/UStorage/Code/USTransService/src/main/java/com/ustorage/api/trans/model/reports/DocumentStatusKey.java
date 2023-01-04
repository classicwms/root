package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentStatusKey {

	private String DocumentNumber;
	private  String CivilId;
	private String Code;

	private String CustomerId;
	private String CustomerName;
	private String Cgroup;
	private String Email;
	private String Mobile;
	private String Phone;
	private String Status;
	private String ServiceType;
	private String StoreNumber;
	private String Amount;
	private String Remark;
	private String Location;

	private String DocumentType;

	private String Note;
	private Date DocumentDate;
	private Date StartDate;
	private Date EndDate;

}

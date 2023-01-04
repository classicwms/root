package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentStatusKey {

	 String DocumentNumber;
	 String CivilId;
	 String Code;

	 String CustomerId;
	 String CustomerName;
	 String Cgroup;
	 String Email;
	 String Mobile;
	 String Phone;
	 String Status;
	 String ServiceType;
	 String StoreNumber;
	 String Amount;
	 String Remark;
	 String Location;

	 String DocumentType;

	 String Note;
	 Date DocumentDate;
	 Date StartDate;
	 Date EndDate;

}

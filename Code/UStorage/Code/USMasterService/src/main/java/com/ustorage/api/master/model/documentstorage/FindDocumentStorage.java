package com.ustorage.api.master.model.documentstorage;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindDocumentStorage {
	private List<String> documentNumber;
    private List<String> customerName;
    private List<String> fileDescription;
    private List<String> uploadedBy;
    private Date startDate;
    private Date endDate;


}

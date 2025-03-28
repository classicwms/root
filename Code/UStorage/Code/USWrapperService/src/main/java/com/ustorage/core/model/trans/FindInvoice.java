package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindInvoice {

	private List<String> invoiceNumber;

    private List<String> codeId;

    private List<String> customerId;

    private List<String> documentNumber;

    private List<String> status;

    private Date documentStartDate;

    private Date documentEndDate;

    //private Boolean isActive;
}

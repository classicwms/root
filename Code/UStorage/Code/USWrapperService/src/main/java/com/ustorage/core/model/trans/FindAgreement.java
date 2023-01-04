package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindAgreement {

	private List<String> agreementNumber;

    private List<String> codeId;

    private List<String> quoteNumber;

    private String customerName;

    private List<String> nationality;

    private String email;
    private List<String> agreementType;
    private List<String> deposit;

    private List<String> status;

    private Date startDate;
    private Date endDate;

}

package com.ustorage.core.model.trans;

import com.ustorage.core.Enum.LeadCustomerTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class FindLeadCustomer {

	private List<String> customerCode;

    private List<String> codeId;

    private List<String> civilId;

    private List<String> customerName;

    private List<String> type;

    private List<String> status;

    private List<String> mobileNumber;

    private List<String> phoneNumber;

    //private Boolean isActive;
}

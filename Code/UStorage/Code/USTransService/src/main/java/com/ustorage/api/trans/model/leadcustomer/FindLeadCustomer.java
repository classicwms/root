package com.ustorage.api.trans.model.leadcustomer;

import com.ustorage.api.trans.Enum.LeadCustomerTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
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

    private Boolean isActive;
}

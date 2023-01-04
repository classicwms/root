package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPaymentVoucher {

	private List<String> voucherId;

    private List<String> codeId;

    private List<String> serviceType;

    private List<String> storeNumber;

    private String customerName;

    private List<String> contractNumber;

    private List<String> modeOfPayment;

    private List<String> status;

    private Date startDate;

    private Date endDate;

    private List<String> voucherStatus;

    //private Boolean isActive;
}

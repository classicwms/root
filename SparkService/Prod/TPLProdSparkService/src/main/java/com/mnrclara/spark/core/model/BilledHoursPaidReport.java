package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class BilledHoursPaidReport {
    private String matterNumber;
    private String matterText;
    private String attorney;
    private String invoiceNumber;
    private Date   dateOfBill;
    private Double amountBilled;
    private Double hoursBilled;
    private Double approxHoursPaid;
    private Double approxAmountReceived;
}

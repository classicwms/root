package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
@Data
public class ContractRenewalStatusReport {

 Date AgreementRenewalDate;
 String AgreementNumber;
 String CustomerName;
 String PhoneNumber;
 String SecondaryNumber;
 String StoreNumber;
 String Size;
 String StorageType;
 String Phase;
 String TotalRent;
 String FillrateStatus;
}

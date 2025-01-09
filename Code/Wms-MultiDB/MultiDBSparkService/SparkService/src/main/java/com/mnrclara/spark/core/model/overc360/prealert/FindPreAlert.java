package com.mnrclara.spark.core.model.overc360.prealert;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPreAlert {

    private List<String> companyId;
    private List<String> languageId;
    private List<String> partnerMasterAirwayBill;
    private List<String> partnerHouseAirwayBill;
    private List<String> partnerId;
    private List<String> subCustomerId;
    private List<String> hsCode;
    private List<String> ddpInvoiceNo;
    private String invoice;
    private Date fromDate;
    private Date toDate;

}

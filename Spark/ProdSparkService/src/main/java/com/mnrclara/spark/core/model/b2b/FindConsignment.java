package com.mnrclara.spark.core.model.b2b;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindConsignment {

    private List<Long> consignmentId;
    private List<String> reference_number;
    private List<String> customer_code;
    private List<String> service_type_id;
    private List<String> consignment_type;
    private List<String> customer_reference_number;
    private List<String> customer_civil_id;
    private List<String> receiver_civil_id;
    private List<String> awb_3rd_Party;
    private List<String> scanType_3rd_Party;
    private List<String> hubCode_3rd_Party;
    private List<String> orderType;
    private List<String> jntPushStatus;
    private List<String> boutiqaatPushStatus;
    private List<String> scanType;
    private List<String> hubCode;

    private Date startDate;
    private Date EndDate;
}

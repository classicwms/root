package com.mnrclara.spark.core.model.b2b;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class JntHubCode {
    private String reference_number;
    private String customer_reference_number;
    private String status_description;
    private String awb_3rd_Party;
    private String order_Type;
    private Boolean is_awb_printed;
    private String customer_code;
    private String scan_Type;
    private String printStatus;
    private Timestamp created_at;

}

package com.mnrclara.spark.core.model.b2b;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class QPWebhook {

    private String  reference_number;
    private String  customer_reference_number;
    private String  status_description;
    private Timestamp  created_at;
    private Boolean  is_awb_printed;
    private String  action_time;
    private String  order_type;
    private String  customer_code;
    private String  qp_wh_status;
    private String item_action_name;
}

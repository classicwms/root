package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OutboundHeaderV3 {

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String target_branch_code;
    private String ref_doc_typ;
    private String sales_order_number;
    private String pre_ob_no;
    private String ref_doc_no;
    private Timestamp ref_doc_date;
    private Timestamp dlv_cnf_on;

}

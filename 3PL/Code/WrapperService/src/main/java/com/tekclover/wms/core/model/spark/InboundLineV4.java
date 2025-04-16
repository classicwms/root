package com.tekclover.wms.core.model.spark;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboundLineV4 {

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String source_branch_code;
    private String ref_doc_no;
    private Long ib_line_no;
    private String ref_doc_type;
    private String mfr_name;
    private String itm_code;
    private String text;
    private Double ord_qty;
    private Double accept_qty;
    private Double damage_qty;
    private Double var_qty;
    private Timestamp ctd_on;
    private Timestamp ib_cnf_on;


}

package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboundLineNewV3 {
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
    private String material_no;
    private String price_segment;
    private String article_no;
    private String gender;
    private String color;
    private String size;
    private String no_pairs;

}

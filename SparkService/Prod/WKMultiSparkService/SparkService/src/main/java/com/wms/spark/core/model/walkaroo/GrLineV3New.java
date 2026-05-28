package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GrLineV3New {

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String mfr_name;
    private String itm_code;
    private String barcode_id;
    private String ref_doc_no;
    private String ref_doc_type;
    private Double ord_qty;
    private Double accept_qty;
    private Double damage_qty;
    private String ref_field_10;
    private Timestamp gr_cnf_on;
    private Timestamp gr_ctd_on;
    private String qty_type;
    private String gr_ctd_by;
    private String material_no;
    private String price_segment;
    private String article_no;
    private String gender;
    private String color;
    private String size;
    private String no_pairs;
}

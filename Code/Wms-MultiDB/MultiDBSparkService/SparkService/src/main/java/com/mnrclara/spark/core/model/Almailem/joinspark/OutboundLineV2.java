package com.mnrclara.spark.core.model.Almailem.joinspark;


import lombok.Data;
import java.sql.Timestamp;

@Data
public class OutboundLineV2 {

    private String plant_text;
    private String wh_text;
    private String ref_doc_no;
    private String ref_doc_type;
    private String sales_order_number;
    private String target_branch_code;
    private String mfr_name;
    private String itm_code;
    private String item_text;
    private Double ord_qty;
    private String referenceField9;
    private String referenceField10;
    private Double dlv_qty;
    private String status_text;
    private Timestamp dlv_ctd_on;

}

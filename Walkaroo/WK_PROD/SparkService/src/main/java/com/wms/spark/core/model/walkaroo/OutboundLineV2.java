package com.wms.spark.core.model.walkaroo;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class OutboundLineV2 {

    private String plant_text;
    private String wh_text;
    private String ref_doc_no;
    private String ref_doc_type;
    private String sales_order_number;
    private String sales_invoice_number;
    private String target_branch_code;
    private String mfr_name;
    private String itm_code;
    private String item_text;
    private Double ord_qty;
    private Double PICK_CNF_QTY;
    private Double QC_QTY;
    private Double dlv_qty;
    private String status_text;
    private Timestamp dlv_ctd_on;
    private Integer IMS_SALE_TYP_CODE;

}
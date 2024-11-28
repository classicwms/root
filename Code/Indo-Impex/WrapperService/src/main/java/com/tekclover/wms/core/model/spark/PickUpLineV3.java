package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PickUpLineV3 {

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String ref_doc_type;
    private String ref_doc_no;
    private String pu_no;
    private Long ob_line_no;
    private String mfr_name;
    private String itm_code;
    private String item_text;
    private String partner_item_barcode;
    private String pick_st_bin;
    private String level_id;
    private String ass_picker_id;
    private String partner_code;
    private Double alloc_qty;
    private Double pick_cnf_qty;
    private Double var_qty;
    private Timestamp pick_ctd_on;
    private Timestamp pick_utd_on;
    private String alt_uom;
    private String no_bags;
    private String bag_size;

}
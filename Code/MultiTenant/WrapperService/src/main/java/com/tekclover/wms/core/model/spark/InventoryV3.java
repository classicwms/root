package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;


@Data
public class InventoryV3 {

    private Long inv_id;
    private String lang_id;
    private String c_id;
    private String plant_id;
    private String wh_id;
    private String pal_code;
    private String case_code;
    private String pack_barcode;
    private String itm_code;
    private Long var_id;
    private String var_sub_id;
    private String str_no;
    private Long stck_typ_id;
    private Long sp_st_ind_id;
    private String ref_ord_no;
    private String str_mtd;
    private Long bin_cl_id;
    private String text;
    private Double inv_qty;
    private Double alloc_qty;
    private String inv_uom;
    private Timestamp mfr_date;
    private Timestamp exp_date;
    private Long is_deleted;
    private String ref_field_1;
    private String ref_field_2;
    private String ref_field_3;
    private Double ref_field_4;
    private String ref_field_5;
    private String ref_field_6;
    private String ref_field_7;
    private String ref_field_8;
    private String ref_field_9;
    private String ref_field_10;
    private String iu_ctd_by;
    private Timestamp iu_ctd_on;
    private String utd_by;
    private Timestamp utd_on;
    private String mfr_code;
    private String barcode_id;
    private String cbm;
    private String cbm_unit;
    private String level_id;
    private String origin;
    private String brand;
    private String ref_doc_no;
    private String c_text;
    private String plant_text;
    private String wh_text;
    private String stck_typ_text;
    private String st_bin;
    private String status_text;
    private String partner_code;
    private Long itm_typ_id;
    private String itm_typ_txt;
    private Timestamp batch_date;
    private String alt_uom;
    private Double no_bags;
    private Double bag_size;
    private String cbm_per_qty;
    private String mfr_name;
    private Boolean loose_pack;
    private Double case_qty;
    private Double piece_qty;
    private Double crate_qty;
    private Double qty_in_case;
    private Double qty_in_create;
    private Double qty_in_piece;
    private String vehicle_no;
    private Timestamp vehicle_reporting_date;
    private Timestamp vehicle_unloading_date;
    private String self_life;
    private String remaining_days;
    private Long remaining_self_life_percentage;
    private String receivingVariance;
}
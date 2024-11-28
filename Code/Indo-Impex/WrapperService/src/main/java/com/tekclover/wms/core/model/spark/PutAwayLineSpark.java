package com.tekclover.wms.core.model.spark;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PutAwayLineSpark {

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String mfr_name;
    private String itm_code;
    private String ref_doc_no;
    private String ref_doc_type;
    private String prop_st_bin;
    private String cnf_st_bin;
    private String barcode_id;
    private Double pa_qty;
    private Double pa_cnf_qty;
    private String pa_cnf_by;
    private Timestamp pa_ctd_on;
    private Timestamp pa_cnf_on;
    private String ref_field_1;

}

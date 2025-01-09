package com.mnrclara.spark.core.model.overc360;

import lombok.Data;

import java.util.List;

@Data
public class Consignment {

    private Long consignment_id;
    private String lang_id;
    private String lang_text;
    private String c_id;
    private String c_name;
    private String status_id;
    private String status_text;
    private String partner_id;
    private String partner_type;
    private String partner_name;
    private String master_airway_bill;
    private String no_of_piece_hawb;
    private String partner_master_ab;
    //    private String partner_house_ab;
//    private String product_id;
//    private String product_name;
//    private String sub_product_id;
//    private String sub_product_name;
//    private String service_type_id;
//    private String service_type_text;
//    private String shipper_id;
//    private String shipper_name;
//    private String no_of_package_mawb;
//    //consignment_info
//    private String load_type;
//    private String description;
//    private String notes;
//    private String cod_amount;
//    private String cod_favor_of;
//    private String cod_collection_mode;
//    private String declared_value_without_tax;
//    //consignment_ref
//    private String pack_details;
//    private String ref_number;
//    private String storage_location;
//    private String is_exchange;
//    private String reverse_reason;

    private DestinationDetails destinationDetails;
    private OriginDetails originDetails;
    private ReturnDetails returnDetails;

    private List<PieceDetails> pieceDetails;


}

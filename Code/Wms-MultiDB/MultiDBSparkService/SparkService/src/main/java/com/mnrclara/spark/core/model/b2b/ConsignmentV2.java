package com.mnrclara.spark.core.model.b2b;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ConsignmentV2 {

        private Long consignmentId;
        private String referenceNumber;
        private String tracking_no;
        private String item_action_name;
        private String cod_amount;
        private String cod_collection_mode;
        private String service_type_id;
        private String consignment_type;
        private String load_type;
        private String description;
        private String cod_favor_of;
        private String dimension_unit;
        private String length;
        private String width;
        private String height;
        private String weight_unit;
        private Double weight;
        private Double declared_value;
        private Long num_pieces;
        private String notes;
        private String customer_reference_number;
        private Boolean is_risk_surcharge_applicable;
        private Timestamp created_at;
        private String status_description;
        private String customer_civil_id;
        private String receiver_civil_id;
        private String currency;
        private String awb_3rd_Party;
        private String scanType_3rd_Party;
        private String hubCode_3rd_Party;
        private String orderType;
        //        private String jntPushStatus;
//        private String boutiqaatPushStatus;
        private Boolean is_awb_printed;
}

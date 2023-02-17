package com.iweb2b.core.model.integration.asyad;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Data;

/*
 *  {
        "consignments": [{
               "customer_code": "ASD",
               "reference_number": "",
               "service_type_id": "PREMIUM",
               "consignment_type": "forward",
               "load_type": "DOCUMENT",
               "description": "Sports Nutrition",
               "cod_favor_of": "",
               "cod_collection_mode": "dd",
               "dimension_unit": "cm",
               "length": "",
               "width": "",
               "height": "",
               "weight_unit": "kg",
               "weight": 2.63,
               "declared_value": 146,
               "cod_amount": "0.000",
               "num_pieces": 1,
               "notes": "Sports Nutrition",
               "customer_reference_number": "296163854",
               "is_risk_surcharge_applicable": false,
               "origin_details": {
                       "name": "The Hut Group",
                       "phone": "97101606811863",
                       "alternate_phone": "",
                       "address_line_1": "Mohebi Logistics; Plot WT01 & WT04",
                       "address_line_2": "LF Logistics/Mohebi Logistics, Plot",
                       "pincode": "8005",
                       "city": "Dubai",
                       "state": "",
                       "country": "AE"
               },

               "destination_details": {
                       "name": "Mohammad Alsmaim",
                       "phone": "96597990399",
                       "alternate_phone": "",
                       "address_line_1": "house 61 Block 5, street 517",
                       "address_line_2": "",
                       "pincode": "91710",
                       "city": "Jaber Alahmad",
                       "state": "",
                       "country": "KW"
               },

               "pieces_detail": [{
                       "description": "Sports Nutrition",
                       "declared_value": 0,
                       "weight": 2.63,
                       "height": 18,
                       "length": 11,
                       "width": 13
               }]
        }]
	}
 */
@Data
public class Consignment {
	
	private String customerCode;
	private String referenceNumber;
	private String serviceTypeId;
	private String consignmentType;
	private String loadType;
	private String description;
	private String codFavorOf;
	private String codCollectionMode;
	private String dimensionUnit;
	private String length;
    private String width;
    private String height;
    private String weightUnit;
    private String weight;
    private String declaredValue;
    private String codAmount;
    private String numPieces;
    private String notes;
    private String customerReferenceNumber;
    private Boolean isRiskSurchargeApplicable;
    
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<Origin_Details> originDetails;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<Destination_Details> destinationDetails;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<Pieces_Details> piecesDetail;
}

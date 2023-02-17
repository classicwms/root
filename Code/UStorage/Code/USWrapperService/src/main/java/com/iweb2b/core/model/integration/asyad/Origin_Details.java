package com.iweb2b.core.model.integration.asyad;

import lombok.Data;

@Data
public class Origin_Details {
	
	/*
	 * "origin_details": {
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
	 */

    private String name;
    private String phone;
    private String alternate_phone;
    private String address_line_1;
    private String address_line_2;
    private String pincode;
    private String city;
    private String state;
    private String country;
}

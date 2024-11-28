package com.tekclover.wms.api.enterprise.model.warehouse;
import lombok.Data;

@Data
public class AddWarehouse {

    private String languageId;
    private String companyId;
    private String plantId;
    private String warehouseId;
    private String modeOfImplementation;
    private Long warehouseTypeId;
    private Boolean inboundQaCheck;
    private String description;
    private Boolean outboundQaCheck;
    private String zone;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
    private String contactName;
    private String desigination;
    private String phoneNumber;
    private String emailId;
    private Double length;
    private Double width;
    private Double totalArea;
    private String uom;
    private Long noAisles;
    private Double lattitude;
    private Double longitude;
    private String storageMethod;
    private Long deletionIndicator;
}

package com.tekclover.wms.core.batch.dto;

import lombok.Data;

import java.util.Date;
@Data
public class PutAwayStrategy {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String brand;
    private String article;
    private String category;
    private String proposedBin;
//    private String companyIdAndDescription;
//    private String plantIdAndDescription;
//    private String warehouseIdAndDescription;
    private String capacity;
    private String gender;
    private String location;
    private String createdBy;
    private String maxCapacity;
    private String superMaxCapacity;

    /**
     *
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param brand
     * @param article
     * @param category
     * @param proposedBin
     * @param capacity
     * @param gender
     * @param location
     */
    public PutAwayStrategy(String languageId, String companyCodeId, String plantId, String warehouseId, String brand,
                           String article, String category, String proposedBin, String capacity, String gender, String location, String createdBy , String maxCapacity , String superMaxCapacity){

        this.languageId = languageId;
        this.companyCodeId = companyCodeId;
        this.plantId = plantId;
        this.warehouseId = warehouseId;
        this.brand = brand;
        this.article = article;
        this.category = category;
        this.proposedBin = proposedBin;
        this.capacity = capacity;
        this.gender = gender;
        this.location = location;
        this.createdBy = createdBy;
        this.maxCapacity = maxCapacity;
        this.superMaxCapacity = superMaxCapacity;

    }

}

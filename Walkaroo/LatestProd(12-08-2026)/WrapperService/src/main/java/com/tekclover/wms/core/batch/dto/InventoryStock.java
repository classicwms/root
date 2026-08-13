package com.tekclover.wms.core.batch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class InventoryStock {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String palletCode;
    private String caseCode;
    private String itemCode;
    private String packBarcodes;
    private Long variantCode;
    private String variantSubCode;
    private String batchSerialNumber;
    private String storageBin;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private String storageMethod;
    private Long binClassId;
    private String description;
    private Double allocatedQuantity;
    private Double inventoryQuantity;
    private String inventoryUom;
    private Date manufacturerDate;
    private Date expiryDate;
    private String barcodeId;
    private String cbm;
    private String cbmUnit;
    private String cbmPerQuantity;
    private String manufacturerName;
    private String stockTypeDescription;
    private Long deletionIndicator;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private String createdBy;


    /**
     *
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param palletCode
     * @param caseCode
     * @param itemCode
     * @param packBarcodes
     * @param variantCode
     * @param variantSubCode
     * @param batchSerialNumber
     * @param storageBin
     * @param stockTypeId
     * @param specialStockIndicatorId
     * @param referenceOrderNo
     * @param storageMethod
     * @param binClassId
     * @param description
     * @param allocatedQuantity
     * @param inventoryQuantity
     * @param inventoryUom
     * @param manufacturerDate
     * @param expiryDate
     * @param barcodeId
     * @param cbm
     * @param cbmUnit
     * @param cbmPerQuantity
     * @param manufacturerName
     * @param stockTypeDescription
     * @param deletionIndicator
     * @param referenceField1
     * @param referenceField2
     * @param referenceField3
     * @param referenceField4
     * @param referenceField5
     * @param referenceField6
     * @param referenceField7
     * @param referenceField8
     * @param referenceField9
     * @param referenceField10
     * @param dType
     * @param createdBy
     */
    public InventoryStock(String languageId, String companyCodeId, String plantId, String warehouseId, String palletCode, String caseCode,
                          String itemCode, String packBarcodes, Long variantCode, String variantSubCode, String batchSerialNumber,
                          String storageBin, Long stockTypeId, Long specialStockIndicatorId, String referenceOrderNo, String storageMethod,
                          Long binClassId, String description, Double allocatedQuantity, Double inventoryQuantity, String inventoryUom,
                          Date manufacturerDate, Date expiryDate, String barcodeId, String cbm, String cbmUnit, String cbmPerQuantity,
                          String manufacturerName, String stockTypeDescription, Long deletionIndicator, String referenceField1,
                          String referenceField2, String referenceField3, String referenceField4, String referenceField5, String referenceField6,
                          String referenceField7, String referenceField8, String referenceField9, String referenceField10, String createdBy) {

        this.languageId = languageId;
        this.companyCodeId = companyCodeId;
        this.plantId = plantId;
        this.warehouseId = warehouseId;
        this.palletCode = palletCode;
        this.caseCode = caseCode;
        this.itemCode = itemCode;
        this.packBarcodes = packBarcodes;
        this.variantCode = variantCode;
        this.variantSubCode = variantSubCode;
        this.batchSerialNumber = batchSerialNumber;
        this.storageBin = storageBin;
        this.stockTypeId = stockTypeId;
        this.specialStockIndicatorId = specialStockIndicatorId;
        this.referenceOrderNo = referenceOrderNo;
        this.storageMethod = storageMethod;
        this.binClassId = binClassId;
        this.description = description;
        this.allocatedQuantity = allocatedQuantity;
        this.inventoryQuantity = inventoryQuantity;
        this.inventoryUom = inventoryUom;
        this.manufacturerDate = manufacturerDate;
        this.expiryDate = expiryDate;
        this.barcodeId = barcodeId;
        this.cbm = cbm;
        this.cbmUnit = cbmUnit;
        this.cbmPerQuantity = cbmPerQuantity;
        this.manufacturerName = manufacturerName;
        this.stockTypeDescription = stockTypeDescription;
        this.deletionIndicator = deletionIndicator;
        this.referenceField1 = referenceField1;
        this.referenceField2 = referenceField2;
        this.referenceField3 = referenceField3;
        this.referenceField4 = referenceField4;
        this.referenceField5 = referenceField5;
        this.referenceField6 = referenceField6;
        this.referenceField7 = referenceField7;
        this.referenceField8 = referenceField8;
        this.referenceField9 = referenceField9;
        this.referenceField10 = referenceField10;
        this.createdBy = createdBy;
    }


}

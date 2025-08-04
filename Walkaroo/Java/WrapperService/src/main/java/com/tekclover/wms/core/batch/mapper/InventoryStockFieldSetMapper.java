package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.InventoryStock;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryStockFieldSetMapper implements FieldSetMapper<InventoryStock> {

    @Override
    public InventoryStock mapFieldSet(FieldSet fieldSet) throws BindException {
        return new InventoryStock(
                fieldSet.readString("languageId"),
                fieldSet.readString("companyCodeId"),
                fieldSet.readString("plantId"),
                fieldSet.readString("warehouseId"),
                fieldSet.readString("palletCode"),
                fieldSet.readString("caseCode"),
                fieldSet.readString("itemCode"),
                fieldSet.readString("packBarcodes"),
                parseLong(fieldSet.readString("variantCode")),
                fieldSet.readString("variantSubCode"),
                fieldSet.readString("batchSerialNumber"),
                fieldSet.readString("storageBin"),
                parseLong(fieldSet.readString("stockTypeId")),
                parseLong(fieldSet.readString("specialStockIndicatorId")),
                fieldSet.readString("referenceOrderNo"),
                fieldSet.readString("storageMethod"),
                parseLong(fieldSet.readString("binClassId")),
                fieldSet.readString("description"),
                parseDouble(fieldSet.readString("allocatedQuantity")),
                parseDouble(fieldSet.readString("inventoryQuantity")),
                fieldSet.readString("inventoryUom"),
                parseDate(fieldSet.readString("manufacturerDate")),
                parseDate(fieldSet.readString("expiryDate")),
                fieldSet.readString("barcodeId"),
                fieldSet.readString("cbm"),
                fieldSet.readString("cbmUnit"),
                fieldSet.readString("cbmPerQuantity"),
                fieldSet.readString("manufacturerName"),
                fieldSet.readString("stockTypeDescription"),
                parseLong(fieldSet.readString("deletionIndicator")),
                fieldSet.readString("referenceField1"),
                fieldSet.readString("referenceField2"),
                fieldSet.readString("referenceField3"),
                fieldSet.readString("referenceField4"),
                fieldSet.readString("referenceField5"),
                fieldSet.readString("referenceField6"),
                fieldSet.readString("referenceField7"),
                fieldSet.readString("referenceField8"),
                fieldSet.readString("referenceField9"),
                fieldSet.readString("referenceField10"),
                fieldSet.readString("createdBy")
        );
    }

    //Long DataType
    private Long parseLong(String value){
        return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
    }

    //Double DataType
    private Double parseDouble(String value){
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
    }

    //Date Format
    private Date parseDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing date: " + value, e);
        }
    }
}

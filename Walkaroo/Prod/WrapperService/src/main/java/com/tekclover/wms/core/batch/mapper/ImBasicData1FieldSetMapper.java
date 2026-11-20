package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.ImBasicData1;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class ImBasicData1FieldSetMapper implements FieldSetMapper<ImBasicData1> {


    // Field Set ImBasicData1
    @Override
    public ImBasicData1 mapFieldSet(FieldSet fieldSet) {
        return new ImBasicData1(
                fieldSet.readString("uomId"),
                fieldSet.readString("languageId"),
                fieldSet.readString("companyCodeId"),
                fieldSet.readString("plantId"),
                fieldSet.readString("warehouseId"),
                fieldSet.readString("itemCode"),
                fieldSet.readString("manufacturerPartNo"),
                fieldSet.readString("description"),
                fieldSet.readString("model"),
                fieldSet.readString("specifications1"),
                fieldSet.readString("specifications2"),
                fieldSet.readString("eanUpcNo"),
                fieldSet.readString("hsnCode"),
                parseLong(fieldSet.readString("itemType")),
                parseLong(fieldSet.readString("itemGroup")),
                parseLong(fieldSet.readString("subItemGroup")),
                fieldSet.readString("storageSectionId"),
                parseDouble(fieldSet.readString("totalStock")),
                parseDouble(fieldSet.readString("minimumStock")),
                parseDouble(fieldSet.readString("maximumStock")),
                parseDouble(fieldSet.readString("reorderLevel")),
                parseBoolean(fieldSet.readString("capacityCheck")),
                parseDouble(fieldSet.readString("replenishmentQty")),
                parseDouble(fieldSet.readString("safetyStock")),
                fieldSet.readString("capacityUnit"),
                fieldSet.readString("capacityUom"),
                fieldSet.readString("quantity"),
                parseDouble(fieldSet.readString("weight")),
                parseBoolean(fieldSet.readString("shelfLifeIndicator")),
                parseLong(fieldSet.readString("statusId")),
                parseDouble(fieldSet.readString("length")),
                parseDouble(fieldSet.readString("width")),
                parseDouble(fieldSet.readString("height")),
                fieldSet.readString("dimensionUom"),
                parseDouble(fieldSet.readString("volume")),
                fieldSet.readString("manufacturerName"),
                fieldSet.readString("manufacturerFullName"),
                fieldSet.readString("manufacturerCode"),
                fieldSet.readString("brand"),
                fieldSet.readString("supplierPartNumber"),
                fieldSet.readString("remarks"),
                fieldSet.readString("dType"),
                parseLong(fieldSet.readString("deletionIndicator")),
                fieldSet.readString("createdBy"),
                fieldSet.readString("barcodeId"),
                fieldSet.readString("materialNo"),
                fieldSet.readString("priceSegment"),
                fieldSet.readString("articleNo"),
                fieldSet.readString("gender"),
                fieldSet.readString("color"),
                fieldSet.readString("size"),
                fieldSet.readString("noPairs")
        );
    }

    //Long DataType
    private Long parseLong(String value) {
        return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
    }

    //Double DataType
    private Double parseDouble(String value) {
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
    }

    //Boolean DataType
    private Boolean parseBoolean(String value) {
        return value != null && !value.isEmpty() ? Boolean.parseBoolean(value) : null;
    }
}
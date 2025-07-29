package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.IMPartner;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class IMPartnerFieldSetMapper implements FieldSetMapper<IMPartner> {

    //	@Override
//	public IMPartner mapFieldSet(FieldSet fieldSet) {
//		return new IMPartner(
//				fieldSet.readString("businessPartnerCode"),
//				fieldSet.readString("languageId"),
//				fieldSet.readString("companyCodeId"),
//				fieldSet.readString("plantId"),
//				fieldSet.readString("warehouseId"),
//				fieldSet.readString("itemCode"),
//				fieldSet.readString("businessPartnerType"),
//				fieldSet.readString("partnerItemBarcode"),
//				fieldSet.readString("manufacturerCode"),
//				fieldSet.readString("manufacturerName"),
//				fieldSet.readString("brandName"),
//				fieldSet.readString("partnerName"),
//				fieldSet.readString("partnerItemNo"),
//				fieldSet.readString("vendorItemBarcode"),
//				fieldSet.readString("mfrBarcode"),
//				fieldSet.readDouble("stock"),
//				fieldSet.readString("stockUom"),
//				fieldSet.readLong("statusId"),
//				fieldSet.readString("referenceField1"),
//				fieldSet.readString("referenceField2"),
//				fieldSet.readString("referenceField3"),
//				fieldSet.readLong("deletionIndicator"),
//				fieldSet.readString("createdBy")
//		);
//	}

    // Field Set IMPartner
    @Override
    public IMPartner mapFieldSet(FieldSet fieldSet) {

        return new IMPartner(
                fieldSet.readString("businessPartnerCode"),
                fieldSet.readString("languageId"),
                fieldSet.readString("companyCodeId"),
                fieldSet.readString("plantId"),
                fieldSet.readString("warehouseId"),
                fieldSet.readString("itemCode"),
                fieldSet.readString("businessPartnerType"),
                fieldSet.readString("partnerItemBarcode"),
                fieldSet.readString("manufacturerCode"),
                fieldSet.readString("manufacturerName"),
                fieldSet.readString("brandName"),
                fieldSet.readString("partnerName"),
                fieldSet.readString("partnerItemNo"),
                fieldSet.readString("vendorItemBarcode"),
                fieldSet.readString("mfrBarcode"),
                parseDouble(fieldSet.readString("stock")),
                fieldSet.readString("stockUom"),
                parseLong(fieldSet.readString("statusId")),
                fieldSet.readString("referenceField1"),
                fieldSet.readString("referenceField2"),
                fieldSet.readString("referenceField3"),
                parseLong(fieldSet.readString("deletionIndicator")),
                fieldSet.readString("createdBy")
        );
    }

    // Long DataType
    private Long parseLong(String value) {
        return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
    }

    //Double DataType
    private Double parseDouble(String value) {
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
    }


}
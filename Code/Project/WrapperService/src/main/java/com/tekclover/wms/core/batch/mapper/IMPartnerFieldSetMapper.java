package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.BusinessPartner;
import com.tekclover.wms.core.batch.dto.IMPartner;

public class IMPartnerFieldSetMapper implements FieldSetMapper<IMPartner> {

	@Override
	public IMPartner mapFieldSet(FieldSet fieldSet) {
		return new IMPartner(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("itemCode"),
				fieldSet.readString("businessParnterType"),
				fieldSet.readString("businessPartnerCode"),
				fieldSet.readString("partnerItemNo"),
				fieldSet.readString("mfrBarcode"),
				fieldSet.readString("brandName"),
				fieldSet.readLong("statusId"),
				fieldSet.readString("referenceField1"),
				fieldSet.readString("referenceField2"),
				fieldSet.readString("referenceField3"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
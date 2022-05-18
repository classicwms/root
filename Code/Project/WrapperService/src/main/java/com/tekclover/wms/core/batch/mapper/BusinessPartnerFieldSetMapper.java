package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.BusinessPartner;

public class BusinessPartnerFieldSetMapper implements FieldSetMapper<BusinessPartner> {

	@Override
	public BusinessPartner mapFieldSet(FieldSet fieldSet) {
		return new BusinessPartner(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("businessParnterType"),
				fieldSet.readString("partnerCode"),
				fieldSet.readString("partnerName"),
				fieldSet.readString("address1"),
				fieldSet.readString("address2"),
				fieldSet.readString("zone"),
				fieldSet.readString("country"),
				fieldSet.readString("state"),
				fieldSet.readString("phoneNumber"),
				fieldSet.readString("faxNumber"),
				fieldSet.readString("emailId"),
				fieldSet.readString("referenceText"),
				fieldSet.readString("location"),
				fieldSet.readString("lattitude"),
				fieldSet.readString("longitude"),
				fieldSet.readString("storageTypeId"),
				fieldSet.readString("storageBin"),
				fieldSet.readString("statusId"),
				fieldSet.readString("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.ImBasicData1;

public class ImBasicData1FieldSetMapper implements FieldSetMapper<ImBasicData1> {

	@Override
	public ImBasicData1 mapFieldSet(FieldSet fieldSet) {
		return new ImBasicData1(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("itemCode"),
				fieldSet.readString("uomId"),
				fieldSet.readString("description"),
				fieldSet.readString("manufacturerPartNo"),
				fieldSet.readLong("itemType"),
				fieldSet.readLong("itemGroup"),
				fieldSet.readLong("subItemGroup"),
				fieldSet.readLong("storageSectionId"),
				fieldSet.readLong("statusId"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
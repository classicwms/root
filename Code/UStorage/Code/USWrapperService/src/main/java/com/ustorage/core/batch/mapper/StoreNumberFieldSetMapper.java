package com.ustorage.core.batch.mapper;

import com.ustorage.core.batch.dto.StoreNumber;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class StoreNumberFieldSetMapper implements FieldSetMapper<StoreNumber> {

	@Override
	public StoreNumber mapFieldSet(FieldSet fieldSet) {
		return new StoreNumber(
				fieldSet.readString("storeNumber"),
				fieldSet.readString("agreementNumber"),
				fieldSet.readString("description"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy"),
				fieldSet.readDate("createdOn"),
				fieldSet.readString("updatedBy"),
				fieldSet.readDate("updatedOn")
		);
	}
}
package com.ustorage.core.batch.mapper;

import com.ustorage.core.batch.dto.StorageUnit;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class StorageUnitFieldSetMapper implements FieldSetMapper<StorageUnit> {

	@Override
	public StorageUnit mapFieldSet(FieldSet fieldSet) {
		return new StorageUnit(
				fieldSet.readString("itemCode"),
				fieldSet.readString("codeId"),
				fieldSet.readString("description"),
				fieldSet.readString("itemType"),
				fieldSet.readString("itemGroup"),
				fieldSet.readString("doorType"),
				fieldSet.readString("storageType"),
				fieldSet.readString("phase"),
				fieldSet.readString("zone"),
				fieldSet.readString("room"),
				fieldSet.readString("rack"),
				fieldSet.readString("bin"),
				fieldSet.readString("priceMeterSquare"),
				fieldSet.readString("weekly"),
				fieldSet.readString("monthly"),
				fieldSet.readString("quarterly"),
				fieldSet.readString("halfYearly"),
				fieldSet.readString("yearly"),
				fieldSet.readString("length"),
				fieldSet.readString("width"),
				fieldSet.readString("originalDimention"),
				fieldSet.readString("roundedDimention"),
				fieldSet.readString("availability"),
				fieldSet.readDate("occupiedFrom"),
				fieldSet.readDate("availableAfter"),
				fieldSet.readString("linkedAgreement"),
				fieldSet.readString("status"),
				fieldSet.readString("storeSizeMeterSquare"),
				fieldSet.readString("aisle"),

				fieldSet.readLong("deletionIndicator"),
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
				fieldSet.readString("createdBy"),
				fieldSet.readDate("createdOn"),
				fieldSet.readString("updatedBy"),
				fieldSet.readDate("updatedOn")
		);
	}
}
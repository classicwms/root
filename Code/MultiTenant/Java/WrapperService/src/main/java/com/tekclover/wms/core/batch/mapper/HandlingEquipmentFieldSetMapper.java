package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.HandlingEquipment;

public class HandlingEquipmentFieldSetMapper implements FieldSetMapper<HandlingEquipment> {

	@Override
	public HandlingEquipment mapFieldSet(FieldSet fieldSet) {
		return new HandlingEquipment(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("handlingEquipmentId"),
				fieldSet.readString("category"),
				fieldSet.readString("handlingUnit"),
				fieldSet.readString("acquistionDate"),
				fieldSet.readString("acquistionValue"),
				fieldSet.readString("currencyId"),
				fieldSet.readString("modelNo"),
				fieldSet.readString("manufacturerPartNo"),
				fieldSet.readString("countryOfOrigin"),
				fieldSet.readString("heBarcode"),
				fieldSet.readLong("statusId"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
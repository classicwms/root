package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.BomLine;

public class BomLineFieldSetMapper implements FieldSetMapper<BomLine> {

	@Override
	public BomLine mapFieldSet(FieldSet fieldSet) {
		return new BomLine(
			fieldSet.readString("languageId"),
			fieldSet.readString("companyCodeId"),
			fieldSet.readString("plantId"),
			fieldSet.readString("warehouseId"),
			fieldSet.readLong("bomNumber"),
			fieldSet.readString("childItemCode"),
			fieldSet.readDouble("childItemQuantity"),
			fieldSet.readLong("statusId"),
			fieldSet.readLong("deletionIndicator"),
			fieldSet.readString("createdBy")
		);
	}
}
package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.BomHeader;

public class BomHeaderFieldSetMapper implements FieldSetMapper<BomHeader> {

	@Override
	public BomHeader mapFieldSet(FieldSet fieldSet) {
		return new BomHeader(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCode"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("parentItemCode"),
				fieldSet.readLong("bomNumber"),
				fieldSet.readDouble("parentItemQuantity"),
				fieldSet.readLong("statusId"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy"),
				fieldSet.readDate("createdOn")
			);
	}
}
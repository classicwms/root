package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.BinLocation;

public class BinLocationFieldSetMapper implements FieldSetMapper<BinLocation> {

	@Override
	public BinLocation mapFieldSet(FieldSet fieldSet) {
		return new BinLocation(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("storageBin"),
				fieldSet.readLong("floorId"),
				fieldSet.readString("storageSectionId"),
				fieldSet.readString("rowId"),
				fieldSet.readString("aisleNumber"),
				fieldSet.readString("spanId"),
				fieldSet.readString("shelfId"),
				fieldSet.readString("binSectionId"),
				fieldSet.readLong("storageTypeId"),
				fieldSet.readLong("binClassId"),
				fieldSet.readString("description"),
				fieldSet.readString("binBarcode"),
				fieldSet.readBoolean("putawayBlock"),
				fieldSet.readBoolean("pickingBlock"),
				fieldSet.readString("blockReason"),
				fieldSet.readLong("statusId"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
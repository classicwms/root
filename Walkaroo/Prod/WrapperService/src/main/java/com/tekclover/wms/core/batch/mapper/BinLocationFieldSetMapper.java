package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.BinLocation;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class BinLocationFieldSetMapper implements FieldSetMapper<BinLocation> {

	//MapFieldSet BinLocation
	@Override
	public BinLocation mapFieldSet(FieldSet fieldSet) {
		return new BinLocation(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("storageBin"),
				parseLong(fieldSet.readString("floorId")),
				fieldSet.readString("storageSectionId"),
				fieldSet.readString("rowId"),
				fieldSet.readString("aisleNumber"),
				fieldSet.readString("spanId"),
				fieldSet.readString("shelfId"),
				fieldSet.readString("binSectionId"),
				parseLong(fieldSet.readString("storageTypeId")),
				parseLong(fieldSet.readString("binClassId")),
				fieldSet.readString("description"),
				fieldSet.readString("binBarcode"),
				parseLong(fieldSet.readString("putawayBlock")),
				parseLong(fieldSet.readString("pickingBlock")),
				fieldSet.readString("blockReason"),
				parseLong(fieldSet.readString("statusId")),
				fieldSet.readString("occupiedVolume"),
				fieldSet.readString("occupiedWeight"),
				fieldSet.readString("occupiedQuantity"),
				fieldSet.readString("remainingVolume"),
				fieldSet.readString("remainingWeight"),
				fieldSet.readString("remainingQuantity"),
				fieldSet.readString("totalVolume"),
				fieldSet.readString("totalWeight"),
				fieldSet.readString("totalQuantity"),
				parseBoolean(fieldSet.readString("capacityCheck")),
				parseDouble(fieldSet.readString("allocatedVolume")),
				parseLong(fieldSet.readString("deletionIndicator")),
				fieldSet.readString("dType"),
				fieldSet.readString("createdBy")
		);
	}

	//Long DataType
	private Long parseLong(String value){
		return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
	}

	//Boolean DataType
	private Boolean parseBoolean(String value) {
		return value != null && !value.isEmpty() ? Boolean.parseBoolean(value) : null;
	}

	//Double DataType
	private Double parseDouble(String value) {
		return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
	}
}

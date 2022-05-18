package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.Inventory;

public class InventoryFieldSetMapper implements FieldSetMapper<Inventory> {

	@Override
	public Inventory mapFieldSet(FieldSet fieldSet) {
		return new Inventory(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("itemCode"),
				fieldSet.readString("packBarcode"),
				fieldSet.readString("storageBin"),
				fieldSet.readLong("stockTypeId"),
				fieldSet.readLong("specialStockIndicatorId"),
				fieldSet.readLong("binClassId"),
				fieldSet.readString("description"),
				fieldSet.readDouble("inventoryQuantity"),
				fieldSet.readString("inventoryUom"),
				fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy")
			);
	}
}
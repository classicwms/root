package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.Inventory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryFieldSetMapper implements FieldSetMapper<Inventory> {


//	Long inventoryId = System.currentTimeMillis();

	//Inventory FieldSet
	@Override
	public Inventory mapFieldSet(FieldSet fieldSet) {
		return new Inventory(
//				inventoryId,
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCodeId"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("palletCode"),
				fieldSet.readString("caseCode"),
				fieldSet.readString("packBarcodes"),
				fieldSet.readString("itemCode"),
				parseLong(fieldSet.readString("variantCode")),
				fieldSet.readString("variantSubCode"),
				fieldSet.readString("batchSerialNumber"),
				fieldSet.readString("storageBin"),
				parseLong(fieldSet.readString("stockTypeId")),
				parseLong(fieldSet.readString("specialStockIndicatorId")),
				fieldSet.readString("referenceOrderNo"),
				fieldSet.readString("storageMethod"),
				parseLong(fieldSet.readString("binClassId")),
				fieldSet.readString("description"),
				parseDouble(fieldSet.readString("inventoryQuantity")),
				parseDouble(fieldSet.readString("allocatedQuantity")),
				fieldSet.readString("inventoryUom"),
				fieldSet.readString("manufacturerCode"),
				parseDate(fieldSet.readString("manufacturerDate")),
				parseDate(fieldSet.readString("expiryDate")),
				parseLong(fieldSet.readString("deletionIndicator")),
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
				fieldSet.readString("dType"),
				fieldSet.readString("createdBy"),

				fieldSet.readString("barcodeId"),
				fieldSet.readString("cbm"),
				fieldSet.readString("cbmUnit"),
				fieldSet.readString("cbmPerQuantity"),
				fieldSet.readString("manufacturerName"),
				fieldSet.readString("origin"),
				fieldSet.readString("levelId"),
				fieldSet.readString("brand"),
				fieldSet.readString("referenceDocumentNo"),
				fieldSet.readString("companyDescription"),
				fieldSet.readString("plantDescription"),
				fieldSet.readString("warehouseDescription"),
				fieldSet.readString("statusDescription"),
				fieldSet.readString("stockTypeDescription"),

				fieldSet.readString("alternateUom"),
				parseDouble(fieldSet.readString("noBags")),
				parseDouble(fieldSet.readString("bagSize"))
		);
	}

	//Long DataType
	private Long parseLong(String value){
		return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
	}

	//Double DataType
	private Double parseDouble(String value){
		return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
	}

	//Date Format
	private Date parseDate(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Error parsing date: " + value, e);
		}
	}

}
package com.tekclover.wms.core.batch.mapper;

import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.model.transaction.PeriodicLine;
import com.tekclover.wms.core.util.DateUtils;

public class PeriodicLineMapper implements FieldSetMapper<PeriodicLine> {
	@Override
	public PeriodicLine mapFieldSet(FieldSet fieldSet) {
		Long variantCode = (fieldSet.readString("variantCode") != null ? Long.valueOf(fieldSet.readString("variantCode")) : 0L);
		Long stockTypeId = (fieldSet.readString("stockTypeId") != null ? Long.valueOf(fieldSet.readString("stockTypeId")) : 0L);
		Long statusId = (fieldSet.readString("statusId") != null ? Long.valueOf(fieldSet.readString("statusId")) : 0L);
		Long approvalProcessId = (fieldSet.readString("approvalProcessId") != null ? Long.valueOf(fieldSet.readString("approvalProcessId")) : 0L);
		Long deletionIndicator = (fieldSet.readString("deletionIndicator") != null ? Long.valueOf(fieldSet.readString("deletionIndicator")) : 0L);
		Double inventoryQuantity = (fieldSet.readString("inventoryQuantity") != null ? Double.valueOf(fieldSet.readString("inventoryQuantity")) : 0D);
		Double countedQty = (fieldSet.readString("countedQty") != null ? Double.valueOf(fieldSet.readString("countedQty")) : 0D);
		Double varianceQty = (fieldSet.readString("varianceQty") != null ? Double.valueOf(fieldSet.readString("varianceQty")) : 0D);
		
		String sCtdOn = fieldSet.readString("createdOn");
		String sCountedOn = fieldSet.readString("countedOn");
		String sConfirmedOn = fieldSet.readString("confirmedOn");
		
		Date createdOn = DateUtils.convertStringToDate2(sCtdOn);
		Date countedOn = DateUtils.convertStringToDate2(sCountedOn);
		Date confirmedOn = DateUtils.convertStringToDate2(sConfirmedOn);
		
		return new PeriodicLine(
				fieldSet.readString("languageId"),
				fieldSet.readString("companyCode"),
				fieldSet.readString("plantId"),
				fieldSet.readString("warehouseId"),
				fieldSet.readString("cycleCountNo"),
				fieldSet.readString("storageBin"),
				fieldSet.readString("itemCode"),
				fieldSet.readString("packBarcodes"),
				variantCode, //fieldSet.readLong("variantCode"),
				fieldSet.readString("variantSubCode"),
				fieldSet.readString("batchSerialNumber"),
				stockTypeId, //fieldSet.readLong("stockTypeId"),
				fieldSet.readString("specialStockIndicator"),
				inventoryQuantity, //fieldSet.readDouble("inventoryQuantity"),
				fieldSet.readString("inventoryUom"),
				countedQty, //fieldSet.readDouble("countedQty"),
				varianceQty, //fieldSet.readDouble("varianceQty"),
				fieldSet.readString("cycleCounterId"),
				fieldSet.readString("cycleCounterName"),
				statusId, //fieldSet.readLong("statusId"),
				fieldSet.readString("cycleCountAction"),
				fieldSet.readString("referenceNo"),
				approvalProcessId, //fieldSet.readLong("approvalProcessId"),
				fieldSet.readString("approvalLevel"),
				fieldSet.readString("approverCode"),
				fieldSet.readString("approvalStatus"),
				fieldSet.readString("remarks"),
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
				deletionIndicator, //fieldSet.readLong("deletionIndicator"),
				fieldSet.readString("createdBy"),
				createdOn, //fieldSet.readDate("createdOn"),
				fieldSet.readString("countedBy"),
				countedOn, //fieldSet.readDate("countedOn"),
				fieldSet.readString("confirmedBy"),
				confirmedOn //fieldSet.readDate("confirmedOn")
			);
	}
}
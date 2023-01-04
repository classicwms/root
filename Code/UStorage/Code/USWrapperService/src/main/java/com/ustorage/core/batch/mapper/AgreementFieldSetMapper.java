package com.ustorage.core.batch.mapper;

import com.ustorage.core.batch.dto.Agreement;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class AgreementFieldSetMapper implements FieldSetMapper<Agreement> {

	@Override
	public Agreement mapFieldSet(FieldSet fieldSet) {
		return new Agreement(
				fieldSet.readString("agreementNumber"),
				fieldSet.readString("codeId"),
				fieldSet.readString("quoteNumber"),
				fieldSet.readString("customerName"),
				fieldSet.readString("civilIDNumber"),
				fieldSet.readString("nationality"),
				fieldSet.readString("address"),
				fieldSet.readString("phoneNumber"),
				fieldSet.readString("secondaryNumber"),
				fieldSet.readString("faxNumber"),
				fieldSet.readString("itemsToBeStored"),
				fieldSet.readDate("startDate"),
				fieldSet.readDate("endDate"),
				fieldSet.readString("location"),
				fieldSet.readString("size"),
				fieldSet.readString("insurance"),
				fieldSet.readString("rent"),
				fieldSet.readString("rentPeriod"),
				fieldSet.readString("totalRent"),
				fieldSet.readString("paymentTerms"),
				fieldSet.readString("agreementDiscount"),
				fieldSet.readString("totalAfterDiscount"),
				fieldSet.readString("notes"),
				fieldSet.readString("email"),
				fieldSet.readString("agreementType"),
				fieldSet.readString("deposit"),
				fieldSet.readString("others"),
				fieldSet.readString("status"),
				fieldSet.readString("itemsToBeStored1"),
				fieldSet.readString("itemsToBeStored2"),
				fieldSet.readString("itemsToBeStored3"),
				fieldSet.readString("itemsToBeStored4"),
				fieldSet.readString("itemsToBeStored5"),
				fieldSet.readString("itemsToBeStored6"),
				fieldSet.readString("itemsToBeStored7"),
				fieldSet.readString("itemsToBeStored8"),
				fieldSet.readString("itemsToBeStored9"),
				fieldSet.readString("itemsToBeStored10"),
				fieldSet.readString("itemsToBeStored11"),
				fieldSet.readString("itemsToBeStored12"),
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
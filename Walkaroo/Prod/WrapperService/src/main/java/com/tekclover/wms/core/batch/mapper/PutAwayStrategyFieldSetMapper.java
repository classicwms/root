package com.tekclover.wms.core.batch.mapper;

import com.tekclover.wms.core.batch.dto.PutAwayStrategy;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class PutAwayStrategyFieldSetMapper implements FieldSetMapper<PutAwayStrategy> {

    //Fields Set PutAwayStrategy
    @Override
    public PutAwayStrategy mapFieldSet(FieldSet fieldSet){
        return new PutAwayStrategy(
                fieldSet.readString("languageId"),
                fieldSet.readString("companyCodeId"),
                fieldSet.readString("plantId"),
                fieldSet.readString("warehouseId"),
                fieldSet.readString("brand"),
                fieldSet.readString("article"),
                fieldSet.readString("category"),
                fieldSet.readString("proposedBin"),
                fieldSet.readString("capacity"),
                fieldSet.readString("gender"),
                fieldSet.readString("location"),
                fieldSet.readString("createdBy"),
                fieldSet.readString("maxCapacity"),
                fieldSet.readString("superMaxCapacity")
        );
    }

}

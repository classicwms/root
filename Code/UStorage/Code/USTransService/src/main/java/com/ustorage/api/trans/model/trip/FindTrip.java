package com.ustorage.api.trans.model.trip;

import lombok.Data;

import java.util.List;

@Data
public class FindTrip {

	private List<String> itemCode;

    private List<String> codeId;

    private List<String> itemType;

    private List<String> itemGroup;

    private List<String> saleUnitOfMeasure;

    private List<String> status;

    private Boolean isActive;
}

package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.List;

@Data
public class FindFlRent {

	private List<String> itemCode;

    private List<String> codeId;

    private List<String> itemType;

    private List<String> itemGroup;

    private List<String> saleUnitOfMeasure;

    private List<String> status;

    //private Boolean isActive;
}

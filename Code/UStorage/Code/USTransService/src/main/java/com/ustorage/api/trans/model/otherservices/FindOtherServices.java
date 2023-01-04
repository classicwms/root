package com.ustorage.api.trans.model.otherservices;

import lombok.Data;

import java.util.List;

@Data
public class FindOtherServices {

	private List<String> itemCode;

    private List<String> codeId;

    private String itemType;

    private String itemGroup;

    private String saleUnitOfMeasure;

    private String status;

    private Boolean isActive;
}

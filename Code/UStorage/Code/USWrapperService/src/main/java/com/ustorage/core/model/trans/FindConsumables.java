package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.List;

@Data
public class FindConsumables {

	private List<String> itemCode;

    private List<String> codeId;

    private List<String> itemType;

    private List<String> itemGroup;

    private List<String> warehouse;

    private String status;

    private List<String> inStock;

    private List<String> lastReceipt;

    //private Boolean isActive;
}

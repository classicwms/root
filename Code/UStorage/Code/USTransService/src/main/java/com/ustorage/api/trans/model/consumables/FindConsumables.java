package com.ustorage.api.trans.model.consumables;

import lombok.Data;

import java.util.List;

@Data
public class FindConsumables {

	private List<String> itemCode;

    private List<String> codeId;

    private List<String> itemType;

    private List<String> itemGroup;

    private List<String> warehouse;

    private List<String> status;

    private List<String> inStock;

    private List<String> lastReceipt;

    private Boolean isActive;
}

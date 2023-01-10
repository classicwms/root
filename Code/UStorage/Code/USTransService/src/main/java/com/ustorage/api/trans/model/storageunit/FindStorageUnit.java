package com.ustorage.api.trans.model.storageunit;

import lombok.Data;

import java.util.List;

@Data
public class FindStorageUnit {

	private List<String> itemCode;

    private List<String> codeId;

    private List<String> itemType;

    private List<String> itemGroup;

    private List<String> doorType;

    private List<String> storageType;

    private List<String> storeSizeMeterSquare;

    private List<String> bin;

    private List<String> availability;

    private List<String> priceMeterSquare;

    private List<String> phase;

    private List<String> status;

    private Boolean isActive;
}

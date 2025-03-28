package com.ustorage.core.Enum;

public enum LeadCustomerTypeEnum {

    LEAD("lead"),
    CUSTOMER("customer"),
    VENDOR("vendor");

    private String value;

    LeadCustomerTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.ustorage.api.trans.model.storenumber;

import lombok.Data;

import java.util.Date;

@Data
public class AddStoreNumber {

    private String storeNumber;
    private String agreementNumber;
    //private String description;
    //private Long deletionIndicator = 0L;
}

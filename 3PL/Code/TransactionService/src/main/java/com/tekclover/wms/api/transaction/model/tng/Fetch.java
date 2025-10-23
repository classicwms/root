package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

import java.util.List;

@Data
public class Fetch {

    private List<String> skus;
    private String storerKey;

}

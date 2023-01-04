package com.ustorage.api.trans.model.workorder;

import lombok.Data;

@Data
public class AddWoProcessedBy {

    private String processedBy;
    private String workOrderId;
}

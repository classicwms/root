package com.ustorage.api.trans.model.workorder;

import lombok.Data;

@Data
public class UpdateWoProcessedBy {

    private String processedBy;
    private String workOrderId;
}
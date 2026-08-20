package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class MergingPalletStatusReport {

    private Long thresholdLimited;
    private Double maximumAllowed;
    private String currentPending;
    private Double pendency;
    private String mergingDoneYDay;
}

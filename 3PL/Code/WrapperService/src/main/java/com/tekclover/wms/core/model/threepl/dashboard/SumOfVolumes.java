package com.tekclover.wms.core.model.threepl.dashboard;

import lombok.Data;

@Data
public class SumOfVolumes {

    private Double sumOfTotalVolumes;
    private Double sumOfOccupiedVolumes;
    private Double sumOfRemainingVolumes;

}

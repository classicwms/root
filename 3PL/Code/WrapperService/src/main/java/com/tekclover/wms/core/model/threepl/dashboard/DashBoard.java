package com.tekclover.wms.core.model.threepl.dashboard;

import lombok.Data;

import java.util.List;
@Data
public class DashBoard {

    private SumOfVolumes sumOfVolumes;
    private List<ListOfTotalVolumes> listOfTotalVolumes;

}

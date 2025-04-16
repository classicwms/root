package com.tekclover.wms.api.transaction.model.threepl.dashboard;

import lombok.Data;

import java.util.List;
@Data
public class DashBoard {

    private SumOfVolumes sumOfVolumes;
    private List<ListOfTotalVolumes> listOfTotalVolumes;

}

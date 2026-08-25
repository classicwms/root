package com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2;


import lombok.Data;

import java.util.List;

@Data
public class PickUpHeaderReport {


    private List<PickerReport> pickerReportList;

}

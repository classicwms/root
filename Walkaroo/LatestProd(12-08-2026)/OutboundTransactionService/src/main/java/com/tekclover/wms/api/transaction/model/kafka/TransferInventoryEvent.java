package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.mnc.NewAddInhouseTransferLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferInventoryEvent {

    private String companyCodeId;
    private String languageId;
    private String plantId;
    private String warehouseId;
    private String loginUserID;
    private List<NewAddInhouseTransferLine> inhouseTransferLine;

}

package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.mnc.AddInhouseTransferHeader;
import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InhouseTransferHeaderEvent {

    private AddInhouseTransferHeader inhouseTransferHeader;
    private String loginUserID;

}

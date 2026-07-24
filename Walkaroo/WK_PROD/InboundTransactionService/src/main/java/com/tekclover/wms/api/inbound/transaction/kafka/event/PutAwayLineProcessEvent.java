package com.tekclover.wms.api.inbound.transaction.kafka.event;

import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutAwayLineProcessEvent {

    private String loginUserID;
    private List<PutAwayLineV2> putAwayLines;
}
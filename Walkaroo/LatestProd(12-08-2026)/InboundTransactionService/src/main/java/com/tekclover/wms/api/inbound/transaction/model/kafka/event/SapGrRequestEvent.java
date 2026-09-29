package com.tekclover.wms.api.inbound.transaction.model.kafka.event;

import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SapGrRequestEvent {

    List<StagingLineEntityV2> stagingLineEntityV2List = new ArrayList<>();
}

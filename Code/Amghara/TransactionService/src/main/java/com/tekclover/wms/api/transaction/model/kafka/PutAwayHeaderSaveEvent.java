package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutAwayHeaderSaveEvent {

    List<PutAwayHeaderV2> putAwayHeader = new ArrayList<>();
    List<GrLineV2> grLine = new ArrayList<>();
}

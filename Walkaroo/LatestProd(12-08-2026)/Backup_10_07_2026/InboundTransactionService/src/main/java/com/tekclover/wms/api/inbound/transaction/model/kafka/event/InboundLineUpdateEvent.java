package com.tekclover.wms.api.inbound.transaction.model.kafka.event;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class InboundLineUpdateEvent {

    private PutAwayLine putAwayLine;
}

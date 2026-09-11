package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.OutboundLineInterim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundLineInterimSaveEvent {

    private List<OutboundLineInterim> outboundLineInterimList = new ArrayList<>();
}

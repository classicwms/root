package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrLineSaveEvent {

    List<GrLineV2> grLine = new ArrayList<>();
}

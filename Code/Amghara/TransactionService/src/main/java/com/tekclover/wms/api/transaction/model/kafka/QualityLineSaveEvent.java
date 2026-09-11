package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityLineSaveEvent {

    private List<QualityLineV2> qualityLineV2List = new ArrayList<>();
}

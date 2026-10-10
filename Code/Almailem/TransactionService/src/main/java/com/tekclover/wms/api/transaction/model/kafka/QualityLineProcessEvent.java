package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityLineProcessEvent {

    private List<AddQualityLineV2> newQualityLines;
    private String loginUserID;
}

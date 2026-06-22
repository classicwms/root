package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityLineCreateEvent {

    private List<AddQualityLineV2> qualityLineV2s = new ArrayList<>();
    private String loginUserID;
}

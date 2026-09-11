package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.AssignPickerV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignPickerEvent {

    List<AssignPickerV2> assignPickers;
    private String assignedPickerId;
    private String loginUserID;
}

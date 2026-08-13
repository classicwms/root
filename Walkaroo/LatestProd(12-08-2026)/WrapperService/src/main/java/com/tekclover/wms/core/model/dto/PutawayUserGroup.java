package com.tekclover.wms.core.model.dto;

import lombok.Data;

@Data
public class PutawayUserGroup {

    private String assUserId;
    private Double totalQty;
    private Long count;
    private Double qtyPerMember;
    private Double durationInMin;
    private Double qtyPerUserPerMin;
    private Double minPerQtyPerUser;
}

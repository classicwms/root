package com.tekclover.wms.api.transaction.model.dto;


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

    public PutawayUserGroup(String assUserId, Double totalQty, Long count,
                               Double qtyPerMember, Double durationInMin,
                               Double qtyPerUserPerMin, Double minPerQtyPerUser) {
        this.assUserId = assUserId;
        this.totalQty = totalQty;
        this.count = count;
        this.qtyPerMember = qtyPerMember;
        this.durationInMin = durationInMin;
        this.qtyPerUserPerMin = qtyPerUserPerMin;
        this.minPerQtyPerUser = minPerQtyPerUser;


    }
}

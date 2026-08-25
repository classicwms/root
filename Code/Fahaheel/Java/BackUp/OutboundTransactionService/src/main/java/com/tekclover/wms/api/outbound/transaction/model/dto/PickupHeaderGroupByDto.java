package com.tekclover.wms.api.outbound.transaction.model.dto;

import java.util.Date;

public interface PickupHeaderGroupByDto {

    String getRefDocNumber();
    String getItemCode();
    String getReferenceField5();
    String getBagSize();
    Double getPickToQty();
    String getProposedStorageBin();
    String getPartnerCode();
    Date getPickupCreatedOn();
    Double getTotalPickToQty(); // sum of grouped pickToQty
    Long getTotalBags();
    Double getMrp();
}

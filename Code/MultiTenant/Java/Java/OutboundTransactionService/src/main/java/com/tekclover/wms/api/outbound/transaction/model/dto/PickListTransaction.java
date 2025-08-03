package com.tekclover.wms.api.outbound.transaction.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PickListTransaction {

    List<PickupHeaderGroupByItemCode> pickupHeaderGroupByItemCodeList;
    List<PickListLoosePack> pickListLoosePackList;
}

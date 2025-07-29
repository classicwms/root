package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PickListTransaction {

    List<PickupHeaderGroupByItemCode> pickupHeaderGroupByItemCodeList;
    List<PickListLoosePack> pickListLoosePackList;
}

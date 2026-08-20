package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class InventoryOwner {

    private String customerName;
    private List<MergingPalletStatusReport> mergingPalletStatusReportList;
}

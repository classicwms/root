package com.tekclover.wms.api.transaction.model.inbound.inventory;


public interface InventoryStockLevel {
     String getWarehouseId();
     String getCustomerId();
     String getSku();
     Double getStockQuantity();
     String getStockStatus();

}

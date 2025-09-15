package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FastSlowMovingDashboard {
    private String itemCode;
    private String itemText;
    private Double deliveryQuantity;
    private String type;

    public interface FastSlowMovingDashboardImpl {
        String getItemCode();

        String getItemText();

        Double getDeliveryQuantity();
    }
}
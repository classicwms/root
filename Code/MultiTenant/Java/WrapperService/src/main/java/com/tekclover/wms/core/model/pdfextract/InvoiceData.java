package com.tekclover.wms.core.model.pdfextract;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InvoiceData {

    private String invoiceNo;
    private String requiredDeliveryDate;
    private String customerName;
    private String deliveryTo;
    private List<ItemLine> items = new ArrayList<>();

    public static class ItemLine {
        private String itemCode;
        private int quantity;

        public ItemLine(String itemCode, int quantity) {
            this.itemCode = itemCode;
            this.quantity = quantity;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

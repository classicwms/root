package com.tekclover.wms.api.transaction.model.threepl.invoiceheader;

import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import lombok.Data;

import java.util.List;

@Data
public class Invoice {
    private InvoiceHeader invoiceHeader;
    private List<InvoiceLine> invoiceLine;
}

package com.tekclover.wms.core.model.threepl;

import com.tekclover.wms.core.model.transaction.InvoiceHeader;
import com.tekclover.wms.core.model.transaction.InvoiceLine;
import lombok.Data;

import java.util.List;

@Data
public class Invoice {
    private InvoiceHeader invoiceHeader;
    private List<InvoiceLine> invoiceLine;
}

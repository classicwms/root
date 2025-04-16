package com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import lombok.Data;

import java.util.List;

@Data
public class ProformaInvoice {

    private ProformaInvoiceHeader proformaInvoiceHeader;
    private List<ProformaInvoiceLine> proformaInvoiceLineList;
}

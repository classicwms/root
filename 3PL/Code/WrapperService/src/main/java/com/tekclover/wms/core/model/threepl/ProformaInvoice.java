package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class ProformaInvoice {

    private ProformaInvoiceHeader proformaInvoiceHeader;
    private List<ProformaInvoiceLine> proformaInvoiceLineList;
}

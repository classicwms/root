package com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader;

import lombok.Data;
import java.util.List;

@Data
public class FindProformaInvoiceHeader {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> proformaBillNo;
    private List<String> partnerCode;
    private List<Long> statusId;

}

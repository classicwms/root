package com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader;

import lombok.Data;
import java.util.List;

@Data
public class FindProformaInvoiceHeader {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> proformaBillNo;
    private List<String> partnerCode;
    private List<Long> statusId;

}

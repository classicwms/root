package com.tekclover.wms.api.transaction.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryConfirmEvent {

   private String companyCodeId;
   private String plantId;
   private String languageId;
   private String warehouseId;
   private String preOutboundNo;
   private String refDocNumber;
   private String partnerCode;
   private String loginUserID;
   private List<Long> lineNumbers;
}


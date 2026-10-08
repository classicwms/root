package com.tekclover.wms.api.transaction.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferOrderEvent {

   private String companyCode;
   private String plantId;
   private String languageId;
   private String warehouseId;
   private String loginUserID;
   private List<TransferLines> transferLinesList;
}

package com.tekclover.wms.api.outbound.transaction.model.mnc;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class NewAddInhouseTransferLine {

    private String sourceBarcodeId;

    private String targetStorageBin;

}

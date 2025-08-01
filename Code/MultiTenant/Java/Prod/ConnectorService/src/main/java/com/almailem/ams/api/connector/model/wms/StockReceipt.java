package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;


@Data
public class StockReceipt {

    @Valid
    private StockReceiptHeader stockReceiptHeader;

    @Valid
    private List<StockReceiptLine> stockReceiptLines;
}

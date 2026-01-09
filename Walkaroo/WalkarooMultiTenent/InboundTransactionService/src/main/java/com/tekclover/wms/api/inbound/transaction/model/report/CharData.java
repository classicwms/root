package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class CharData {

    private List<SeriesData> series;
    private List<String> categories;
}

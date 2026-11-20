package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class CharData {

    private List<SeriesData> series;
    private List<String> categories;
}

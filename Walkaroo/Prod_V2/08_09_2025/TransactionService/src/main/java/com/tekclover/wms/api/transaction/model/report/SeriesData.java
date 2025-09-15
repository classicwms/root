package com.tekclover.wms.api.transaction.model.report;


import lombok.Data;

import java.util.List;

@Data
public class SeriesData {

        private String name;
        private List<Integer> data;
}

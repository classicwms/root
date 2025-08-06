package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.List;

@Data
public class SeriesData {

        private String name;
        private List<Integer> data;
}

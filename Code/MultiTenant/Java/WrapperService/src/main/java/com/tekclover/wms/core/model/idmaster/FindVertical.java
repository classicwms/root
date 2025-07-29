package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindVertical {
    private List<Long> verticalId;
    private List<String> verticalName;
    private List<String> languageId;
    private List<String> remark;
}

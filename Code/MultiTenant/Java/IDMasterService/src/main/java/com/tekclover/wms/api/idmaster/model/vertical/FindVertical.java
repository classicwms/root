package com.tekclover.wms.api.idmaster.model.vertical;
import lombok.Data;

import java.util.List;

@Data
public class FindVertical {
    private List<Long> verticalId;
    private List<String> languageId;
    private List<String> verticalName;
    private List<String> remark;
}

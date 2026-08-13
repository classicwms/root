package com.tekclover.wms.api.idmaster.model.vertical;
import lombok.Data;

import java.io.Serializable;
@Data
public class VerticalIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `VERT_ID`, 'LANG_ID`
     */
    private Long verticalId;
    private String languageId;
}

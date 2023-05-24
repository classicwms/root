package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindStatusId {
    private List<Long> statusId;
    private String status;
    private List<String> languageId;
}

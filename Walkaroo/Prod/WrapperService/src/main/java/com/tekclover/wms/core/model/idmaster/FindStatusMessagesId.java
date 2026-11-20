package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindStatusMessagesId {
    private List<String> messageId;
    private List<String> messageType;
    private List<String> languageId;
}

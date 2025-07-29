package com.tekclover.wms.api.idmaster.model.statusmessagesid;

import lombok.Data;

import java.util.List;

@Data
public class FindStatusMessagesId {
    private List<String> messageId;
    private List<String> messageType;
    private List<String> languageId;
}

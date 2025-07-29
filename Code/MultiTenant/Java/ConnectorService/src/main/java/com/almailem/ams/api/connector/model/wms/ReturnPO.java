package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ReturnPO {

    @Valid
    private ReturnPOHeader returnPOHeader;

    @Valid
    private List<ReturnPOLine> returnPOLine;
}

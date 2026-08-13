package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2.OutboundHeader;
import com.tekclover.wms.api.inbound.transaction.repository.OutboundHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OutboundHeaderService {


    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;

    /**
     * @param refDocNumber
     * @return
     */
    public OutboundHeader getOutboundHeader(String refDocNumber, String warehouseId) {
        OutboundHeader outboundHeader = outboundHeaderRepository.findByRefDocNumberAndWarehouseIdAndDeletionIndicator(refDocNumber, warehouseId, 0L);
        return outboundHeader;
    }
}

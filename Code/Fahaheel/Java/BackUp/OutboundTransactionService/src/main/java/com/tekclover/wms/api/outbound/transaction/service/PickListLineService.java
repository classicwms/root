package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PickListLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.SearchPickListLine;
import com.tekclover.wms.api.outbound.transaction.repository.PickListLineRepository;
import com.tekclover.wms.api.outbound.transaction.repository.specification.PickListLineSpecification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class PickListLineService extends BaseService {

    @Autowired
    PickListLineRepository pickListLineRepository;

    public Stream<PickListLine> findPickListLine(SearchPickListLine searchPickListLine) throws ParseException, java.text.ParseException {

        if (searchPickListLine.getFromDeliveryDate() != null && searchPickListLine.getToDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickListLine.getFromDeliveryDate(),
                    searchPickListLine.getToDeliveryDate());
            searchPickListLine.setFromDeliveryDate(dates[0]);
            searchPickListLine.setToDeliveryDate(dates[1]);
        }

        PickListLineSpecification spec = new PickListLineSpecification(searchPickListLine);
        Stream<PickListLine> pickListLineStream = pickListLineRepository.stream(spec, PickListLine.class);

        return pickListLineStream;
    }

//    /**
//     *
//     * @param pickListLine
//     * @param loginUserID
//     * @return
//     */
//    public PickListLine createPickListLine(PickListLine pickListLine, String loginUserID) {
//
//        PickListLine dbPickListLine = new PickListLine();
//        log.info("newPickListLine : " + pickListLine);
//        BeanUtils.copyProperties(pickListLine, dbPickListLine, CommonUtils.getNullPropertyNames(pickListLine));
//
//        dbPickListLine.setDeletionIndicator(0L);
//        dbPickListLine.setCreatedBy(loginUserID);
//        dbPickListLine.setUpdatedBy(loginUserID);
//        dbPickListLine.setCreatedOn(new Date());
//        dbPickListLine.setUpdatedOn(new Date());
//        dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
//        return pickListLineRepository.save(dbPickListLine);
//    }
}
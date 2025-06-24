package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.SearchPickListHeader;
import com.tekclover.wms.api.outbound.transaction.repository.specification.PickListHeaderSpecification;
import com.tekclover.wms.api.outbound.transaction.repository.PickListHeaderRepository;
import com.tekclover.wms.api.outbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class PickListHeaderService {

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    public Stream<PickListHeader> findPickListHeader(SearchPickListHeader searchPickListHeader)
            throws ParseException, java.text.ParseException {

        log.info("searchPickListHeader: " + searchPickListHeader);

        if (searchPickListHeader.getStartRequiredDeliveryDate() != null && searchPickListHeader.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickListHeader.getStartRequiredDeliveryDate(), searchPickListHeader.getEndRequiredDeliveryDate());
            searchPickListHeader.setStartRequiredDeliveryDate(dates[0]);
            searchPickListHeader.setEndRequiredDeliveryDate(dates[1]);
        } else {
            searchPickListHeader.setStartRequiredDeliveryDate(null);
            searchPickListHeader.setEndRequiredDeliveryDate(null);
        }

        if (searchPickListHeader.getStartDeliveryConfirmedOn() != null && searchPickListHeader.getEndDeliveryConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickListHeader.getStartDeliveryConfirmedOn(), searchPickListHeader.getEndDeliveryConfirmedOn());
            searchPickListHeader.setStartDeliveryConfirmedOn(dates[0]);
            searchPickListHeader.setEndDeliveryConfirmedOn(dates[1]);
        } else {
            searchPickListHeader.setStartDeliveryConfirmedOn(null);
            searchPickListHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchPickListHeader.getStartOrderDate() != null && searchPickListHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickListHeader.getStartOrderDate(), searchPickListHeader.getEndOrderDate());
            searchPickListHeader.setStartOrderDate(dates[0]);
            searchPickListHeader.setEndOrderDate(dates[1]);
        } else {
            searchPickListHeader.setStartOrderDate(null);
            searchPickListHeader.setEndOrderDate(null);
        }

        PickListHeaderSpecification spec = new PickListHeaderSpecification(searchPickListHeader);
        Stream<PickListHeader> headerSearchResults = pickListHeaderRepository.stream(spec, PickListHeader.class);

        return headerSearchResults;
    }

    /**
     *
     * @param dbPickListHeader
     * @param loginUserID
     * @return
     */
    public PickListHeader createPickListHeader(PickListHeader dbPickListHeader, String loginUserID) {

//        PickListHeader dbPickListHeader = new PickListHeader();
        log.info("newPickListHeader : " + dbPickListHeader);
//        BeanUtils.copyProperties(pickListHeader, dbPickListHeader, CommonUtils.getNullPropertyNames(pickListHeader));

        dbPickListHeader.setDeletionIndicator(0L);
        dbPickListHeader.setCreatedBy(loginUserID);
        dbPickListHeader.setUpdatedBy(loginUserID);
        dbPickListHeader.setCreatedOn(new Date());
        dbPickListHeader.setUpdatedOn(new Date());
//        dbPickListHeader.setPickListCancelHeaderId(System.currentTimeMillis());
//        List<PickListLine> createdPickListLineList = new ArrayList<>();
//        if(pickListHeader.getLine() != null){
//            for (PickListLine pickListLine : pickListHeader.getLine()) {
//                pickListLine.setPickListCancelHeaderId(dbPickListHeader.getPickListCancelHeaderId());
//                PickListLine createdPickListLine = pickListLineService.createPickListLine(pickListLine, loginUserID);
//                createdPickListLineList.add(createdPickListLine);
//            }
//        }
//        dbPickListHeader.setLine(createdPickListLineList);
        PickListHeader createdPickListHeader = pickListHeaderRepository.save(dbPickListHeader);
//        log.info("CreatedPickListCancel Record : " + createdPickListHeader);
        return createdPickListHeader;
    }

}
package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderProcessV4;
import com.tekclover.wms.api.inbound.transaction.repository.InboundOrderProcessRepository;
import com.tekclover.wms.api.inbound.transaction.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class OrderPreparationService {

    @Autowired
    InboundOrderProcessRepository inboundOrderProcessRepository;

    /**
     *
     * @param inboundOrderProcessList
     */
    public List<InboundOrderProcessV4> saveInboundProcess(List<InboundOrderProcessV4> inboundOrderProcessList) {
        if (inboundOrderProcessList != null && !inboundOrderProcessList.isEmpty()) {
            try {
                List<InboundOrderProcessV4> createInboundOrderProcessList = inboundOrderProcessList.stream().map(inboundOrderProcess -> {
                    InboundOrderProcessV4 inboundLine = new InboundOrderProcessV4();
                    BeanUtils.copyProperties(inboundOrderProcess, inboundLine, CommonUtils.getNullPropertyNames(inboundOrderProcess));
                    return inboundLine;
                }).collect(toList());
                return inboundOrderProcessRepository.saveAll(createInboundOrderProcessList).stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Exception while InboundProcess Create : " + e);
                throw e;
            }
        }
        return null;
    }


    //=======================================================INBOUND===============================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<ASNV2> prepAsnMultipleDataV4(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<InboundOrderProcessV4> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        long lineReference = 1;
        ASNHeaderV2 header = null;
        List<ASNLineV2> lisAsnLine = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getAsnNumber());
            }

            if (!isSameOrder) {
                ASNV2 orders = new ASNV2();
                orders.setAsnHeader(header);
                orders.setAsnLine(lisAsnLine);
                orderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                lisAsnLine = new ArrayList<>();
                lineReference = 1;
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getAsnNumber();
                if (oneTimeAllow) {
                    header = new ASNHeaderV2();

                    header.setBranchCode(plantId);
                    header.setCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                line.setBranchCode(plantId);
                line.setCompanyCode(companyCodeId);
                line.setLineReference(lineReference);
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                lineReference++;
                lisAsnLine.add(line);
            }
            if (allRowsList.size() == i) {
                ASNV2 orders = new ASNV2();
                orders.setAsnHeader(header);
                orders.setAsnLine(lisAsnLine);
                orderList.add(orders);
            }
            i++;
        }
        return orderList;
    }

    }
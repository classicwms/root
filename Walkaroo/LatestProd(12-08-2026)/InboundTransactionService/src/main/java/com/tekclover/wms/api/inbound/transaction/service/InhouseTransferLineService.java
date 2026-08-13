package com.tekclover.wms.api.inbound.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.mnc.AddInhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.model.mnc.InhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.model.mnc.SearchInhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.repository.InhouseTransferLineRepository;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InhouseTransferLineSpecification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InhouseTransferLineService extends BaseService {

    @Autowired
    private InhouseTransferLineRepository inhouseTransferLineRepository;

    /**
     * getInhouseTransferLines
     * @return
     */
    public List<InhouseTransferLine> getInhouseTransferLines () {
        List<InhouseTransferLine> InhouseTransferLineList = inhouseTransferLineRepository.findAll();
        InhouseTransferLineList = InhouseTransferLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return InhouseTransferLineList;
    }

    /**
     * getInhouseTransferLine
     * @param transferNumber
     * @return
     */
    public InhouseTransferLine getInhouseTransferLine (String warehouseId, String transferNumber, String sourceItemCode) {
        Optional<InhouseTransferLine> InhouseTransferLine =
                inhouseTransferLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferNumberAndSourceItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        transferNumber,
                        sourceItemCode,
                        0L);
        if (InhouseTransferLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",transferNumber: " + transferNumber +
                    " doesn't exist.");
        }
        return InhouseTransferLine.get();
    }

    /**
     * Find InhouseTransferLine
     *
     *
     * @param searchInhouseTransferLine
     * @return
     * @throws Exception
     */
    public List<InhouseTransferLine> findInhouseTransferLine(SearchInhouseTransferLine searchInhouseTransferLine)
            throws Exception {
        if (searchInhouseTransferLine.getStartCreatedOn() != null && searchInhouseTransferLine.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInhouseTransferLine.getStartCreatedOn(), searchInhouseTransferLine.getEndCreatedOn());
            searchInhouseTransferLine.setStartCreatedOn(dates[0]);
            searchInhouseTransferLine.setEndCreatedOn(dates[1]);
        }
        if (searchInhouseTransferLine.getStartConfirmedOn() != null && searchInhouseTransferLine.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInhouseTransferLine.getStartConfirmedOn(), searchInhouseTransferLine.getEndConfirmedOn());
            searchInhouseTransferLine.setStartConfirmedOn(dates[0]);
            searchInhouseTransferLine.setEndConfirmedOn(dates[1]);
        }
        InhouseTransferLineSpecification spec = new InhouseTransferLineSpecification(searchInhouseTransferLine);
        List<InhouseTransferLine> results = inhouseTransferLineRepository.findAll(spec);
        log.info("results: " + results);

        List<InhouseTransferLine> inhouseTransferLineList = new ArrayList<>();
        for (InhouseTransferLine dbInhouseTransferLine : results) {
            IKeyValuePair iKeyValuePair = inhouseTransferLineRepository.getDescription(
                    dbInhouseTransferLine.getLanguageId(), dbInhouseTransferLine.getCompanyCodeId(),
                    dbInhouseTransferLine.getPlantId(), dbInhouseTransferLine.getWarehouseId());

            if (iKeyValuePair != null) {
                dbInhouseTransferLine.setCompanyDescription(iKeyValuePair.getCompanyDesc());
                dbInhouseTransferLine.setPlantDescription(iKeyValuePair.getPlantDesc());
                dbInhouseTransferLine.setWarehouseDescription(iKeyValuePair.getWarehouseDesc());
            }
            inhouseTransferLineList.add(dbInhouseTransferLine);
        }
        log.info("inhouseTransferLineList: " + inhouseTransferLineList);
        return inhouseTransferLineList;
    }



    /**
     * createInhouseTransferLine
     * @param newInhouseTransferLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InhouseTransferLine createInhouseTransferLine (AddInhouseTransferLine newInhouseTransferLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Optional<InhouseTransferLine> InhouseTransferLine =
                inhouseTransferLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferNumberAndSourceItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        newInhouseTransferLine.getWarehouseId(),
                        newInhouseTransferLine.getTransferNumber(),
                        newInhouseTransferLine.getSourceItemCode(),
                        0L);
        if (!InhouseTransferLine.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        InhouseTransferLine dbInhouseTransferLine = new InhouseTransferLine();
        log.info("newInhouseTransferLine : " + newInhouseTransferLine);
        BeanUtils.copyProperties(newInhouseTransferLine, dbInhouseTransferLine, CommonUtils.getNullPropertyNames(newInhouseTransferLine));
        dbInhouseTransferLine.setDeletionIndicator(0L);
        dbInhouseTransferLine.setCreatedBy(loginUserID);
        dbInhouseTransferLine.setUpdatedBy(loginUserID);
        dbInhouseTransferLine.setCreatedOn(new Date());
        dbInhouseTransferLine.setUpdatedOn(new Date());
        return inhouseTransferLineRepository.save(dbInhouseTransferLine);
    }
}

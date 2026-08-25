package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.enterprise.transaction.model.deliveryheader.DeliveryHeader;
import com.tekclover.wms.api.enterprise.transaction.model.deliveryline.*;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.enterprise.transaction.repository.*;
import com.tekclover.wms.api.enterprise.transaction.repository.specification.DeliveryLineSpecification;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeliveryLineService {
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    private DeliveryHeaderRepository deliveryHeaderRepository;

    @Autowired
    private DeliveryLineRepository deliveryLineRepository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private DeliveryHeaderService deliveryHeaderService;

    @Autowired
    OutboundHeaderService outboundHeaderService;

    @Autowired
    OutboundLineService outboundLineService;

    String statusDescription = null;

    /**
     * getAllDeliveryLine
     *
     * @return
     */
    public List<DeliveryLine> getAllDeliveryLine() {
        List<DeliveryLine> deliveryLineList = deliveryLineRepository.findAll();
        deliveryLineList = deliveryLineList
                .stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return deliveryLineList;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param invoiceNumber
     * @param refDocNumber
     * @param languageId
     * @param deliveryNo
     * @param itemCode
     * @param lineNumber
     * @return
     */
    public DeliveryLine getDeliveryLine(String companyCodeId, String plantId, String warehouseId,
                                        String invoiceNumber, String refDocNumber, String languageId,
                                        Long deliveryNo, String itemCode, Long lineNumber) {
        Optional<DeliveryLine> dbDeliveryLine =
                deliveryLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeliveryNoAndItemCodeAndLineNumberAndInvoiceNumberAndRefDocNumberAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        languageId,
                        deliveryNo,
                        itemCode,
                        lineNumber,
                        invoiceNumber,
                        refDocNumber,
                        0L
                );
        if (dbDeliveryLine.isEmpty()) {
            throw new BadRequestException("The given values CompanyCodeId -"
                    + companyCodeId + " PlantId "
                    + plantId + " WarehouseId "
                    + warehouseId + " languageId "
                    + languageId + " DeliveryNo "
                    + deliveryNo + " Item Code "
                    + itemCode + " Line No "
                    + lineNumber + " Invoice Number "
                    + invoiceNumber + " Ref Doc Number "
                    + refDocNumber + " doesn't exist.");

        }
        return dbDeliveryLine.get();
    }

    public DeliveryLine getDeliveryLineForUpdate(String companyCodeId, String plantId, String warehouseId,
                                                 String invoiceNumber, String refDocNumber, String languageId,
                                                 Long deliveryNo, String itemCode, Long lineNumber) {
        Optional<DeliveryLine> dbDeliveryLine =
                deliveryLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeliveryNoAndItemCodeAndLineNumberAndInvoiceNumberAndRefDocNumberAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        languageId,
                        deliveryNo,
                        itemCode,
                        lineNumber,
                        invoiceNumber,
                        refDocNumber,
                        0L
                );
        return dbDeliveryLine.get();
    }


    /**
     * @param newDeliveryLineList
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<DeliveryLine> createDeliveryLine(List<AddDeliveryLine> newDeliveryLineList, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<DeliveryLine> dbDeliveryLineList = new ArrayList<>();

        for (AddDeliveryLine addDeliveryLine : newDeliveryLineList) {
            List<DeliveryLine> duplicateDeliveryLine =
                    deliveryLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndItemCodeAndLineNumberAndInvoiceNumberAndRefDocNumberAndDeletionIndicator(
                            addDeliveryLine.getLanguageId(), addDeliveryLine.getCompanyCodeId(), addDeliveryLine.getPlantId(),
                            addDeliveryLine.getWarehouseId(), addDeliveryLine.getDeliveryNo(), addDeliveryLine.getItemCode(),
                            addDeliveryLine.getLineNumber(), addDeliveryLine.getInvoiceNumber(), addDeliveryLine.getRefDocNumber(), 0L);
            if (!duplicateDeliveryLine.isEmpty()) {
                throw new BadRequestException("Record is getting duplicated with the given values");
            } else {
                DeliveryLine dbDeliveryLine = new DeliveryLine();
                BeanUtils.copyProperties(addDeliveryLine, dbDeliveryLine, CommonUtils.getNullPropertyNames(addDeliveryLine));

                //V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(dbDeliveryLine.getCompanyCodeId(),
                        dbDeliveryLine.getLanguageId(),
                        dbDeliveryLine.getPlantId(),
                        dbDeliveryLine.getWarehouseId());

                dbDeliveryLine.setCompanyDescription(description.getCompanyDesc());
                dbDeliveryLine.setPlantDescription(description.getPlantDesc());
                dbDeliveryLine.setWarehouseDescription(description.getWarehouseDesc());

                if (dbDeliveryLine.getStatusId() != null) {
                    statusDescription = stagingLineV2Repository.getStatusDescription(dbDeliveryLine.getStatusId(), dbDeliveryLine.getLanguageId());
                    dbDeliveryLine.setStatusDescription(statusDescription);
                }
                dbDeliveryLine.setDeletionIndicator(0L);
                dbDeliveryLine.setCreatedBy(loginUserID);
                dbDeliveryLine.setUpdatedBy(loginUserID);
                dbDeliveryLine.setCreatedOn(new Date());
                dbDeliveryLine.setUpdatedOn(new Date());
                DeliveryLine savedDeliveryLine = deliveryLineRepository.save(dbDeliveryLine);
                dbDeliveryLineList.add(savedDeliveryLine);
            }
        }
        log.info("DeliveryLine Create Successfully" + dbDeliveryLineList );
        return dbDeliveryLineList;
    }


//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param warehouseId
//     * @param deliveryNo
//     * @param languageId
//     * @param itemCode
//     * @param lineNumber
//     * @param invoiceNumber
//     * @param refDocNumber
//     * @param updateDeliveryLine
//     * @param loginUserId
//     * @return
//     * @throws IllegalAccessException
//     * @throws InvocationTargetException
//     */
//    public List<DeliveryLine> updateDeliveryLine(String companyCodeId, String plantId, String warehouseId, Long deliveryNo,
//                                                 String languageId, String itemCode, Long lineNumber, String invoiceNumber,
//                                                 String refDocNumber, List<UpdateDeliveryLine> updateDeliveryLine, String loginUserId)
//            throws IllegalAccessException, InvocationTargetException {
//
//        List<DeliveryLine> deliveryLineList = new ArrayList<>();
//        for (UpdateDeliveryLine deliveryLine : updateDeliveryLine) {
//
//            DeliveryLine dbDeliveryLine = getDeliveryLine(companyCodeId, plantId, warehouseId, invoiceNumber,
//                    refDocNumber, languageId, deliveryNo, itemCode, lineNumber);
//
//            if (dbDeliveryLine != null) {
//                BeanUtils.copyProperties(deliveryLine, dbDeliveryLine, CommonUtils.getNullPropertyNames(deliveryLine));
//                dbDeliveryLine.setUpdatedBy(loginUserId);
//                dbDeliveryLine.setUpdatedOn(new Date());
//                DeliveryLine updatedDeliveryLine = deliveryLineRepository.save(dbDeliveryLine);
//                deliveryLineList.add(updatedDeliveryLine);
//            } else {
//                throw new BadRequestException("DeliveryLine not found for parameters: " +
//                        "companyCodeId=" + companyCodeId +
//                        ", plantId=" + plantId +
//                        ", warehouseId=" + warehouseId +
//                        ", languageId=" + languageId +
//                        ", itemCode=" + itemCode +
//                        ", lineNumber=" + lineNumber);
//            }
//        }
//        return deliveryLineList;
//    }

    public List<DeliveryLine> updateDeliveryLine(List<UpdateDeliveryLine> updateDeliveryLine, String loginUserId)
            throws IllegalAccessException, InvocationTargetException {

        List<DeliveryLine> deliveryLineList = new ArrayList<>();
        List<AddDeliveryLine> createDeliveryLineList = new ArrayList<>();

        for (UpdateDeliveryLine deliveryLine : updateDeliveryLine) {

            DeliveryLine dbDeliveryLine = getDeliveryLineForUpdate(deliveryLine.getCompanyCodeId(), deliveryLine.getPlantId(),
                    deliveryLine.getWarehouseId(), deliveryLine.getInvoiceNumber(),
                    deliveryLine.getRefDocNumber(), deliveryLine.getLanguageId(), deliveryLine.getDeliveryNo(),
                    deliveryLine.getItemCode(), deliveryLine.getLineNumber());

            log.info("GetDeliveryLine " + dbDeliveryLine);
            if (dbDeliveryLine != null) {
                BeanUtils.copyProperties(deliveryLine, dbDeliveryLine, CommonUtils.getNullPropertyNames(deliveryLine));
                statusDescription = stagingLineV2Repository.getStatusDescription(dbDeliveryLine.getStatusId(), dbDeliveryLine.getLanguageId());
                dbDeliveryLine.setStatusDescription(statusDescription);
                dbDeliveryLine.setUpdatedBy(loginUserId);
                dbDeliveryLine.setUpdatedOn(new Date());
                DeliveryLine updatedDeliveryLine = deliveryLineRepository.save(dbDeliveryLine);
                deliveryLineList.add(updatedDeliveryLine);
            } else {
                //Insert New Line if existing line not present
                AddDeliveryLine addDeliveryLine = new AddDeliveryLine();
                BeanUtils.copyProperties(deliveryLine, addDeliveryLine, CommonUtils.getNullPropertyNames(deliveryLine));
                createDeliveryLineList.add(addDeliveryLine);
            }
        }
        //Calling Create Delivery Line
        if(createDeliveryLineList != null && !createDeliveryLineList.isEmpty()) {
            createDeliveryLine(createDeliveryLineList,loginUserId);
        }

        //Update Delivery Line, Delivery Header, Outbound Line and Outbound Header Status update if all lines updated as same status ID
        List<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                updateDeliveryLine.get(0).getCompanyCodeId(),
                updateDeliveryLine.get(0).getPlantId(),
                updateDeliveryLine.get(0).getLanguageId(),
                updateDeliveryLine.get(0).getWarehouseId(),
                updateDeliveryLine.get(0).getRefDocNumber(),
                0L);
        log.info("Delivery Lines : " + deliveryLines);

        List<DeliveryHeader> deliveryHeaderList = deliveryHeaderService.getDeliveryHeaderList(
                updateDeliveryLine.get(0).getCompanyCodeId(),
                updateDeliveryLine.get(0).getPlantId(),
                updateDeliveryLine.get(0).getLanguageId(),
                updateDeliveryLine.get(0).getWarehouseId(),
                updateDeliveryLine.get(0).getRefDocNumber());
        log.info("Delivery Header: " + deliveryHeaderList);

        OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(
                updateDeliveryLine.get(0).getCompanyCodeId(),
                updateDeliveryLine.get(0).getPlantId(),
                updateDeliveryLine.get(0).getLanguageId(),
                updateDeliveryLine.get(0).getRefDocNumber(),
                updateDeliveryLine.get(0).getWarehouseId());
        log.info("Outbound Header: " + outboundHeader);

        List<OutboundLineV2> outboundLineList = outboundLineService.getOutboundLineV2(
                updateDeliveryLine.get(0).getCompanyCodeId(),
                updateDeliveryLine.get(0).getPlantId(),
                updateDeliveryLine.get(0).getLanguageId(),
                updateDeliveryLine.get(0).getWarehouseId(),
                updateDeliveryLine.get(0).getRefDocNumber());
        log.info("Outbound Line : " + outboundLineList);

        Long deliveryLineCount = deliveryLines.stream().count();
        log.info("Delivery Lines Count: " + deliveryLineCount);

        Long deliveryLineCountStatus90 = 0L;
        List<DeliveryLine> deliveryLinesStatus90 = null;
        Long deliveryLineCountStatus91 = 0L;
        List<DeliveryLine> deliveryLinesStatus91 = null;
        Long deliveryLineCountStatus92 = 0L;
        List<DeliveryLine> deliveryLinesStatus92 = null;

        if(deliveryLines != null && !deliveryLines.isEmpty()) {
           deliveryLinesStatus90 = deliveryLines.stream().filter(a -> a.getStatusId() == 90L).collect(Collectors.toList());
           if(deliveryLinesStatus90 != null && !deliveryLinesStatus90.isEmpty()) {
               deliveryLineCountStatus90 = deliveryLinesStatus90.stream().count();
               log.info("deliveryLineCountStatus90: " + deliveryLineCountStatus90);
           }
        }
        if(deliveryLines != null && !deliveryLines.isEmpty()) {
           deliveryLinesStatus91 = deliveryLines.stream().filter(a -> a.getStatusId() == 91L).collect(Collectors.toList());
           if(deliveryLinesStatus91 != null && !deliveryLinesStatus91.isEmpty()) {
               deliveryLineCountStatus91 = deliveryLinesStatus91.stream().count();
               log.info("deliveryLineCountStatus91: " + deliveryLineCountStatus91);
           }
        }
        if(deliveryLines != null && !deliveryLines.isEmpty()) {
           deliveryLinesStatus92 = deliveryLines.stream().filter(a -> a.getStatusId() == 92L).collect(Collectors.toList());
           if(deliveryLinesStatus92 != null && !deliveryLinesStatus92.isEmpty()) {
               deliveryLineCountStatus92 = deliveryLinesStatus92.stream().count();
               log.info("deliveryLineCountStatus92: " + deliveryLineCountStatus92);
           }
        }
        if(deliveryLineCount.equals(deliveryLineCountStatus90)) {
            if(deliveryHeaderList != null && !deliveryHeaderList.isEmpty()){
                statusDescription = stagingLineV2Repository.getStatusDescription(90L, updateDeliveryLine.get(0).getLanguageId());
                for(DeliveryHeader deliveryHeader : deliveryHeaderList) {
                    deliveryHeader.setStatusId(90L);
                    deliveryHeader.setStatusDescription(statusDescription);
                    deliveryHeaderRepository.save(deliveryHeader);
                    log.info("Delivery Header updated to status 90 : " + deliveryHeader);
                }
            }

        }
        if(deliveryLineCount.equals(deliveryLineCountStatus91)) {
            if(deliveryHeaderList != null && !deliveryHeaderList.isEmpty()){
                statusDescription = stagingLineV2Repository.getStatusDescription(91L, updateDeliveryLine.get(0).getLanguageId());
                for(DeliveryHeader deliveryHeader : deliveryHeaderList) {
                    deliveryHeader.setStatusId(91L);
                    deliveryHeader.setStatusDescription(statusDescription);
                    deliveryHeaderRepository.save(deliveryHeader);
                    log.info("Delivery Header updated to status 91 : " + deliveryHeader);
                }
            }
            if(outboundHeader != null) {
                outboundHeader.setStatusId(91L);
                outboundHeader.setStatusDescription(statusDescription);
                outboundHeaderV2Repository.save(outboundHeader);
                log.info("Outbound Header updated to status 91 : " + outboundHeader);
            }
            if(outboundLineList != null && !outboundLineList.isEmpty()){
                for (OutboundLineV2 outboundLine : outboundLineList){
                    outboundLine.setStatusId(91L);
                    outboundLine.setStatusDescription(statusDescription);
                    outboundLineV2Repository.save(outboundLine);
                    log.info("Outbound Line updated to status 91 : " + outboundLine);
                }
            }
        }
        if(deliveryLineCount.equals(deliveryLineCountStatus92)) {
            if(deliveryHeaderList != null && !deliveryHeaderList.isEmpty()){
                statusDescription = stagingLineV2Repository.getStatusDescription(92L, updateDeliveryLine.get(0).getLanguageId());
                for(DeliveryHeader deliveryHeader : deliveryHeaderList) {
                    deliveryHeader.setStatusId(92L);
                    deliveryHeader.setStatusDescription(statusDescription);
                    deliveryHeaderRepository.save(deliveryHeader);
                    log.info("Delivery Header updated to status 92 : " + deliveryHeader);
                }
            }
            if(outboundHeader != null) {
                outboundHeader.setStatusId(92L);
                outboundHeader.setStatusDescription(statusDescription);
                outboundHeaderV2Repository.save(outboundHeader);
                log.info("Outbound Header updated to status 92 : " + outboundHeader);
            }
            if(outboundLineList != null && !outboundLineList.isEmpty()){
                for (OutboundLineV2 outboundLine : outboundLineList){
                    outboundLine.setStatusId(92L);
                    outboundLine.setStatusDescription(statusDescription);
                    outboundLineV2Repository.save(outboundLine);
                    log.info("Outbound Line updated to status 92 : " + outboundLine);
                }
            }
        }

        log.info("Update DeliveryLine SuccessFully " + deliveryLineList);
        return deliveryLineList;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param deliveryNo
     * @param refDocNumber
     * @param invoiceNumber
     * @param itemCode
     * @param lineNumber
     * @param languageId
     * @param loginUserID
     */
    public void deleteDeliveryLine(String companyCodeId, String plantId, String warehouseId, Long deliveryNo,
                                   String refDocNumber, String invoiceNumber, String itemCode,
                                   Long lineNumber, String languageId, String loginUserID) {

        DeliveryLine deliveryLine = getDeliveryLine(companyCodeId, plantId, warehouseId, invoiceNumber,
                refDocNumber, languageId, deliveryNo, itemCode, lineNumber);
        if (deliveryLine != null) {
            deliveryLine.setDeletionIndicator(1L);
            deliveryLine.setUpdatedBy(loginUserID);
            deliveryLineRepository.save(deliveryLine);
            log.info("Delete DeliveryLine SuccessFully " + deliveryLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + deliveryNo);
        }
    }

    /**
     * @param searchDeliveryLine
     * @return
     * @throws ParseException
     */
    public List<DeliveryLine> findDeliveryLine(SearchDeliveryLine searchDeliveryLine) throws ParseException {

        DeliveryLineSpecification spec = new DeliveryLineSpecification(searchDeliveryLine);
        log.info("Input value " + searchDeliveryLine);
        List<DeliveryLine> results = deliveryLineRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }


    //Delivery Line Count
    public DeliveryLineCount getDeliveryLineCount(String companyCodeId, String languageId, String plantId, String warehouseId, String driverId){

        DeliveryLineCount deliveryLineCount = new DeliveryLineCount();

        //new
        List<Long> newDeliveryLineCount = deliveryLineRepository.getNewRecordCount(companyCodeId, plantId, warehouseId, languageId, driverId, 90L);
        Long newLineCount = newDeliveryLineCount.stream().count();
        deliveryLineCount.setNewCount(newLineCount);

        //InTransit
        List<Long> inTransitLineCount = deliveryLineRepository.getNewRecordCount(companyCodeId, plantId, warehouseId, languageId, driverId, 91L);
        Long transitCount = inTransitLineCount.stream().count();
        deliveryLineCount.setInTransitCount(transitCount);

        //Completed
        List<Long> completedLineCount = deliveryLineRepository.getNewRecordCount(companyCodeId, plantId, warehouseId, languageId, driverId, 92L);
        Long completedCount = completedLineCount.stream().count();
        deliveryLineCount.setCompletedCount(completedCount);

        //ReDelivery
        List<Long> reDeliveryLineCount = deliveryLineRepository.getReDeliveryLineCount(companyCodeId, plantId, warehouseId, languageId, driverId, 92L,true);
        Long reDeliveryCount = reDeliveryLineCount.stream().count();
        deliveryLineCount.setRedeliveryCount(reDeliveryCount);

        return deliveryLineCount;
    }


    /**
     *
     * @param findDeliveryLineCount
     * @return
     * @throws Exception
     */
    public DeliveryLineCount findDeliveryLineCount(FindDeliveryLineCount findDeliveryLineCount) throws Exception{

        DeliveryLineCount deliveryLineCount = new DeliveryLineCount();

        //new
        List<Long> newDeliveryLineCount = deliveryLineRepository.getNewDeliveryLineCount(findDeliveryLineCount.getCompanyCodeId(),
                findDeliveryLineCount.getPlantId(), findDeliveryLineCount.getWarehouseId(), findDeliveryLineCount.getLanguageId(),
                findDeliveryLineCount.getDriverId(),  90L);

        Long newLineCount = newDeliveryLineCount.stream().count();
        deliveryLineCount.setNewCount(newLineCount);

        //InTransit
        List<Long> inTransitLineCount = deliveryLineRepository.getCountOfDeliveryLine(findDeliveryLineCount.getCompanyCodeId(),
                findDeliveryLineCount.getPlantId(), findDeliveryLineCount.getWarehouseId(), findDeliveryLineCount.getLanguageId(),
                findDeliveryLineCount.getDriverId(),  91L,false);

        Long transitCount = inTransitLineCount.stream().count();
        deliveryLineCount.setInTransitCount(transitCount);

        //Completed
        List<Long> completedLineCount = deliveryLineRepository.getCountOfDeliveryLine(findDeliveryLineCount.getCompanyCodeId(),
                findDeliveryLineCount.getPlantId(), findDeliveryLineCount.getWarehouseId(), findDeliveryLineCount.getLanguageId(),
                findDeliveryLineCount.getDriverId(),  92L);

        Long completedCount = completedLineCount.stream().count();
        deliveryLineCount.setCompletedCount(completedCount);

        //ReDelivery
        List<Long> reDeliveryLineCount = deliveryLineRepository.getCountOfDeliveryLine(findDeliveryLineCount.getCompanyCodeId(),
                findDeliveryLineCount.getPlantId(), findDeliveryLineCount.getWarehouseId(), findDeliveryLineCount.getLanguageId(),
                findDeliveryLineCount.getDriverId(),  91L,true);

        Long reDeliveryCount = reDeliveryLineCount.stream().count();
        deliveryLineCount.setRedeliveryCount(reDeliveryCount);

        return deliveryLineCount;

    }

}
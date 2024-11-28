package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.SearchBomLine;
import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.masters.repository.BomLineRepository;
import com.tekclover.wms.api.masters.repository.ExceptionLogRepository;
import com.tekclover.wms.api.masters.repository.specification.BomLineSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BomLineService {

    @Autowired
    private BomLineRepository bomLineRepository;

    @Autowired
    private ExceptionLogRepository exceptionLogRepo;

    /**
     * getBomLines
     *
     * @return
     */
    public List<BomLine> getBomLines() {
        try {
            List<BomLine> bomLineList = bomLineRepository.findAll();
            bomLineList = bomLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return bomLineList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param bomNumber
     * @return
     */
    public List<BomLine> getBomLine(String warehouseId, Long bomNumber, String companyCode, String languageId, String plantId) {
        try {
            List<BomLine> bomLine = bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndDeletionIndicator(
                    languageId,
                    companyCode,
                    plantId,
                    warehouseId,
                    bomNumber, 0L);
            return bomLine;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getBomLine
     *
     * @param bomNumber
     * @return
     */
    public BomLine getBomLine(String warehouseId, Long bomNumber, String childItemCode, String companyCode, String plantId, String languageId) {
        try {
            Optional<BomLine> bomLine =
                    bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            bomNumber,
                            childItemCode,
                            0L);
            if (bomLine.isEmpty()) {
                // Exception Log
                createBomLineLog(bomNumber, languageId, companyCode, plantId, warehouseId,
                        "Bom Line with given values and bomNumber-" + bomNumber + " doesn't exists.");
                throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                        ",bomNumber: " + bomNumber +
                        ",childItemCode: " + childItemCode +
                        " doesn't exist.");
            }
            return bomLine.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createBomLine
     *
     * @param newBomLine
     * @param loginUserID
     * @return
     */
    public BomLine createBomLine(BomLine newBomLine, String loginUserID) {
        try {
            BomLine dbBomLine = new BomLine();
            Optional<BomLine> duplicateBomLine = bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(newBomLine.getLanguageId(), newBomLine.getCompanyCode(), newBomLine.getPlantId(), newBomLine.getWarehouseId(), newBomLine.getBomNumber(), newBomLine.getChildItemCode(), 0L);
            if (!duplicateBomLine.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting duplicated");
            } else {
                log.info("newBomLine : " + newBomLine);
                BeanUtils.copyProperties(newBomLine, dbBomLine, CommonUtils.getNullPropertyNames(newBomLine));
                dbBomLine.setDeletionIndicator(0L);
                dbBomLine.setCreatedBy(loginUserID);
                dbBomLine.setUpdatedBy(loginUserID);
                dbBomLine.setCreatedOn(new Date());
                dbBomLine.setUpdatedOn(new Date());
                return bomLineRepository.save(dbBomLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateBomLine
     *
     * @param loginUserID
     * @param bomNumber
     * @param updateBomLine
     * @return
     */
    public BomLine updateBomLine(String warehouseId, Long bomNumber, String companyCode,
                                 String languageId, String plantId, String childItemCode,
                                 String loginUserID, BomLine updateBomLine) {
        try {
            BomLine dbBomLine = getBomLine(warehouseId, bomNumber, childItemCode, companyCode, plantId, languageId);
            BeanUtils.copyProperties(updateBomLine, dbBomLine, CommonUtils.getNullPropertyNames(updateBomLine));
            dbBomLine.setUpdatedBy(loginUserID);
            dbBomLine.setUpdatedOn(new Date());
            return bomLineRepository.save(dbBomLine);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param bomNumber
     * @param loginUserID
     * @param updateBomLines
     * @return
     */
    public List<BomLine> updateBomLines(String companyCode, String plantId, String languageId, String warehouseId,
                                        Long bomNumber, String loginUserID, List<BomLine> updateBomLines) {
        try {
            List<BomLine> updatedBomLines = new ArrayList<>();
            String createdBy = null;
            Date createdOn = null;
            if (updateBomLines != null && !updateBomLines.isEmpty()) {
                List<BomLine> bomLines = getBomLine(warehouseId, bomNumber, companyCode, languageId, plantId);
                if (bomLines != null && !bomLines.isEmpty()) {
                    createdOn = bomLines.get(0).getCreatedOn();
                    createdBy = bomLines.get(0).getCreatedBy();
                    bomLineRepository.deleteAll(bomLines);
                }
                for (BomLine bomLine : updateBomLines) {
                    log.info("Update BOM Line Initiated: " + bomLine);
                    BomLine updateBomLine = new BomLine();
                    BeanUtils.copyProperties(bomLine, updateBomLine, CommonUtils.getNullPropertyNames(bomLine));
                    updateBomLine.setUpdatedBy(loginUserID);
                    updateBomLine.setUpdatedOn(new Date());
                    if (createdBy != null) {
                        updateBomLine.setCreatedBy(createdBy);
                    } else {
                        updateBomLine.setCreatedBy(loginUserID);
                    }

                    updateBomLine.setCreatedOn(Objects.requireNonNullElseGet(createdOn, Date::new));
                    updateBomLine.setBomNumber(bomNumber);
                    updateBomLine.setDeletionIndicator(0L);
                    BomLine updatedBomline = bomLineRepository.save(updateBomLine);
                    updatedBomLines.add(updatedBomline);
                }
            }
            return updatedBomLines;
        } catch (Exception e) {
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteBomLine
     *
     * @param loginUserID
     * @param bomNumber
     */
    public void deleteBomLine(String warehouseId, Long bomNumber, String childItemCode, String companyCode,
                              String languageId, String plantId, String loginUserID) {
        try {
            BomLine bomLine = getBomLine(warehouseId, bomNumber, childItemCode, companyCode, plantId, languageId);
            if (bomLine != null) {
                bomLine.setDeletionIndicator(1L);
                bomLine.setUpdatedBy(loginUserID);
                bomLineRepository.save(bomLine);
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + bomNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * @param searchBomLine
     * @return
     * @throws Exception
     */
    public List<BomLine> findBomLine(SearchBomLine searchBomLine) {
        try {
            BomLineSpecification spec = new BomLineSpecification(searchBomLine);
            List<BomLine> results = bomLineRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //============================================BomLine_ExceptionLog=================================================
    private void createBomLineLog(Long lineNo, String languageId, String companyCodeId,
                                  String plantId, String warehouseId, String error) {

        try {
            ExceptionLog dbExceptionLog = new ExceptionLog();
            dbExceptionLog.setOrderTypeId(String.valueOf(lineNo));
            dbExceptionLog.setOrderDate(new Date());
            dbExceptionLog.setLanguageId(languageId);
            dbExceptionLog.setCompanyCodeId(companyCodeId);
            dbExceptionLog.setPlantId(plantId);
            dbExceptionLog.setWarehouseId(warehouseId);
            dbExceptionLog.setReferenceField1(String.valueOf(lineNo));
            dbExceptionLog.setErrorMessage(error);
            dbExceptionLog.setCreatedBy("MSD_API");
            dbExceptionLog.setCreatedOn(new Date());
            exceptionLogRepo.save(dbExceptionLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

}
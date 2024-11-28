package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.bom.*;
import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.masters.repository.BomHeaderRepository;
import com.tekclover.wms.api.masters.repository.BomLineRepository;
import com.tekclover.wms.api.masters.repository.ExceptionLogRepository;
import com.tekclover.wms.api.masters.repository.specification.BomHeaderSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BomHeaderService extends BaseService {

    @Autowired
    private BomHeaderRepository bomHeaderRepository;

    @Autowired
    private BomLineRepository bomLineRepository;

    @Autowired
    private BomLineService bomLineService;

    @Autowired
    private ExceptionLogRepository exceptionLogRepo;

    /**
     * getBomHeaders
     *
     * @return
     */
    public List<AddBomHeader> getBomHeaders() {
        try {
            List<BomHeader> bomHeaderList = bomHeaderRepository.findAll();
            bomHeaderList = bomHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

            List<AddBomHeader> addBomHeaders = new ArrayList<>();
//		for (BomHeader bomHeader : bomHeaderList) {
//			List<BomLine> bomLines = bomLineService.getBomLine (bomHeader.getWarehouseId(), bomHeader.getBomNumber(), bomHeader.getCompanyCode(), bomHeader.getLanguageId(), bomHeader.getPlantId());
//
//			List<AddBomLine> addBomLines = new ArrayList<>();
//			for (BomLine bomLine : bomLines) {
//				AddBomLine addBomLine = new AddBomLine();
//				BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
//				addBomLines.add(addBomLine);
//			}
//
//			AddBomHeader addBomHeader = new AddBomHeader();
//			BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
//			addBomHeader.setBomLines(addBomLines);
//			addBomHeaders.add(addBomHeader);
//		}

            bomHeaderList.stream().forEach(bomHeader -> {
                List<BomLine> bomLines = bomLineService.getBomLine(bomHeader.getWarehouseId(),
                        bomHeader.getBomNumber(),
                        bomHeader.getCompanyCode(),
                        bomHeader.getLanguageId(),
                        bomHeader.getPlantId());

                AddBomHeader addBomHeader = new AddBomHeader();
                BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
                bomLines.stream().forEach(bomLine -> {
                    bomLine.setReferenceField1(bomHeader.getParentItemCode());
                    bomLine.setReferenceField3(String.valueOf(bomHeader.getParentItemQuantity()));
                    bomLine.setReferenceField4(bomHeader.getCreatedBy());
                    bomLine.setReferenceField5(String.valueOf(bomHeader.getCreatedOn()));
                });
                addBomHeader.setBomLines(bomLines);
                addBomHeaders.add(addBomHeader);
            });

            return addBomHeaders;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getBomHeader
     *
     * @param parentItemCode
     * @return
     */
    public AddBomHeader getBomHeader(String warehouseId, String parentItemCode, String languageId, String companyCode, String plantId) {
        try {
            Optional<BomHeader> optBomHeader =
                    bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            parentItemCode,
                            0L);
            if (optBomHeader.isEmpty()) {
                // Exception Log
                createBomHeaderLog(parentItemCode, languageId, companyCode, plantId, warehouseId,
                        "Bom Header with given values and parentItemCode-" + parentItemCode + " doesn't exists.");
                throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                        "parentItemCode: " + parentItemCode +
                        "companyCode:" + companyCode +
                        " doesn't exist.");
            }

            BomHeader bomHeader = optBomHeader.get();
            List<BomLine> bomLines = bomLineService.getBomLine(bomHeader.getWarehouseId(), bomHeader.getBomNumber(), bomHeader.getCompanyCode(), bomHeader.getLanguageId(), bomHeader.getPlantId());
            bomLines.stream().forEach(bomLine -> {
                bomLine.setReferenceField1(bomHeader.getParentItemCode());
                bomLine.setReferenceField3(String.valueOf(bomHeader.getParentItemQuantity()));
                bomLine.setReferenceField4(bomHeader.getCreatedBy());
                bomLine.setReferenceField5(String.valueOf(bomHeader.getCreatedOn()));
            });
//		List<AddBomLine> addBomLines = new ArrayList<>();
//		for (BomLine bomLine : bomLines) {
//			AddBomLine addBomLine = new AddBomLine();
//			BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
//			addBomLines.add(addBomLine);
//		}

            AddBomHeader addBomHeader = new AddBomHeader();
            BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
            addBomHeader.setBomLines(bomLines);
            return addBomHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param parentItemCode
     * @return
     */
    public BomHeader getBomHeaderByEntity(String warehouseId, String parentItemCode, String languageId, String companyCode, String plantId) {
        try {
            Optional<BomHeader> optBomHeader =
                    bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            parentItemCode,
                            0L);
            if (optBomHeader.isEmpty()) {
                throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                        ",parentItemCode: " + parentItemCode +
                        " doesn't exist.");
            }

            return optBomHeader.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchBomHeader
     * @return
     */
    public List<AddBomHeader> findBomHeader(SearchBomHeader searchBomHeader) {
        try {
            if (searchBomHeader.getStartCreatedOn() != null && searchBomHeader.getStartCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchBomHeader.getStartCreatedOn(), searchBomHeader.getEndCreatedOn());
                searchBomHeader.setStartCreatedOn(dates[0]);
                searchBomHeader.setEndCreatedOn(dates[1]);
            }
            if (searchBomHeader.getStartUpdatedOn() != null && searchBomHeader.getStartUpdatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchBomHeader.getStartUpdatedOn(), searchBomHeader.getEndUpdatedOn());
                searchBomHeader.setStartUpdatedOn(dates[0]);
                searchBomHeader.setEndUpdatedOn(dates[1]);
            }
            BomHeaderSpecification spec = new BomHeaderSpecification(searchBomHeader);
            List<BomHeader> bomHeaderSearchResults = bomHeaderRepository.findAll(spec);
//		log.info("Search results: " + bomHeaderSearchResults);

            /* pulling out child records */
            List<AddBomHeader> addBomHeaders = new ArrayList<>();
//		for (BomHeader bomHeader : bomHeaderSearchResults) {
//			List<BomLine> bomLines = bomLineService.getBomLine (bomHeader.getWarehouseId(), bomHeader.getBomNumber(), bomHeader.getCompanyCode(), bomHeader.getLanguageId(), bomHeader.getPlantId());
//
//			List<AddBomLine> addBomLines = new ArrayList<>();
//			for (BomLine bomLine : bomLines) {
//				AddBomLine addBomLine = new AddBomLine();
//				BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
//				addBomLines.add(addBomLine);
//			}
//
//			AddBomHeader addBomHeader = new AddBomHeader();
//			BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
//			addBomHeader.setBomLines(addBomLines);
//			addBomHeaders.add(addBomHeader);
//		}

            bomHeaderSearchResults.forEach(bomHeader -> {
                List<BomLine> bomLines = bomLineService.getBomLine(bomHeader.getWarehouseId(),
                        bomHeader.getBomNumber(),
                        bomHeader.getCompanyCode(),
                        bomHeader.getLanguageId(),
                        bomHeader.getPlantId());
                bomLines.forEach(bomLine -> {
                    bomLine.setReferenceField1(bomHeader.getParentItemCode());
                    bomLine.setReferenceField3(String.valueOf(bomHeader.getParentItemQuantity()));
                    bomLine.setReferenceField4(bomHeader.getCreatedBy());
                    bomLine.setReferenceField5(String.valueOf(bomHeader.getCreatedOn()));
                });
                AddBomHeader addBomHeader = new AddBomHeader();
                BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
                addBomHeader.setBomLines(bomLines);
                addBomHeaders.add(addBomHeader);
            });
            return addBomHeaders;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createBomHeader
     *
     * @param newBomHeader
     * @param loginUserID
     * @return
     */
    public AddBomHeader createBomHeader(AddBomHeader newBomHeader, String loginUserID) {
        /*
         * Creating BOM Number
         * ------------------------
         * On Clicking save, Pass the selected WH_ID, NUM_RAN_OBJ=90,FISCALYEAR= CURRENT CALENDAR YEAR in NUMBERRANGE table
         * and fetch NUM_RAN_CURRENT values and add +1 and insert this no as BOM_NO in BOMHEADER and BOMLINE tables
         */
        try {
            Long NUM_RAN_CODE = 90L;
            String BOMNUMBER = getNextRangeNumber(NUM_RAN_CODE,
                    newBomHeader.getWarehouseId(),
                    newBomHeader.getCompanyCode(),
                    newBomHeader.getPlantId(),
                    newBomHeader.getLanguageId());

            Optional<BomHeader> bomHeader =
                    bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
                            newBomHeader.getLanguageId(), newBomHeader.getCompanyCode(), newBomHeader.getPlantId(), newBomHeader.getWarehouseId(),
                            newBomHeader.getParentItemCode(), 0L);
            if (!bomHeader.isEmpty()) {
                throw new BadRequestException("The given values: warehouseId:" + newBomHeader.getWarehouseId() +
                        ",parentItemCode: " + newBomHeader.getParentItemCode() +
                        " already existing.");
            }

            BomHeader dbBomHeader = new BomHeader();
            log.info("newBomHeader : " + newBomHeader);

            List<BomLine> createdBomLines = new ArrayList<>();

            Long sequenceNo = 1L;
            for (BomLine newBomLine : newBomHeader.getBomLines()) {
                newBomLine.setBomNumber(Long.valueOf(BOMNUMBER));
                newBomLine.setSequenceNo(sequenceNo);
                sequenceNo = sequenceNo + 1;
                BomLine bomLine = bomLineService.createBomLine(newBomLine, loginUserID);

                //			AddBomLine createdBomLine = new AddBomLine();
                //			BeanUtils.copyProperties(bomLine, createdBomLine, CommonUtils.getNullPropertyNames(bomLine));
                createdBomLines.add(bomLine);
            }

            BeanUtils.copyProperties(newBomHeader, dbBomHeader, CommonUtils.getNullPropertyNames(newBomHeader));
            dbBomHeader.setBomNumber(Long.valueOf(BOMNUMBER));
            dbBomHeader.setDeletionIndicator(0L);
            dbBomHeader.setCreatedBy(loginUserID);
            dbBomHeader.setUpdatedBy(loginUserID);
            dbBomHeader.setCreatedOn(new Date());
            dbBomHeader.setUpdatedOn(new Date());
            dbBomHeader = bomHeaderRepository.save(dbBomHeader);

            AddBomHeader createdBomHeader = new AddBomHeader();
            BeanUtils.copyProperties(dbBomHeader, createdBomHeader, CommonUtils.getNullPropertyNames(dbBomHeader));
            createdBomHeader.setBomLines(createdBomLines);

            return createdBomHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateBomHeader
     *
     * @param loginUserID
     * @param parentItemCode
     * @param updateBomHeader
     * @return
     */
    public UpdateBomHeader updateBomHeader(String warehouseId, String parentItemCode, String languageId,
                                           String companyCode, String plantId,
                                           String loginUserID, UpdateBomHeader updateBomHeader) {
        try {
            log.info("Update BOM Header : " + updateBomHeader);
            BomHeader dbBomHeader = getBomHeaderByEntity(warehouseId, parentItemCode, languageId, companyCode, plantId);
            BeanUtils.copyProperties(updateBomHeader, dbBomHeader, CommonUtils.getNullPropertyNames(updateBomHeader));
            dbBomHeader.setUpdatedBy(loginUserID);
            dbBomHeader.setUpdatedOn(new Date());
            dbBomHeader = bomHeaderRepository.save(dbBomHeader);

//		List<BomLine> updateBomLines = new ArrayList<>();
//		for (BomLine updateBomLine : updateBomHeader.getBomLines()) {
//			if(updateBomLine.getBomNumber() != null) {
//				BomLine bomLine = bomLineService.updateBomLine(warehouseId, updateBomLine.getBomNumber(),
//						updateBomLine.getCompanyCode(),updateBomLine .getLanguageId(),updateBomLine.getPlantId(),updateBomLine.getChildItemCode(),loginUserID, updateBomLine);
//
////				UpdateBomLine updatedBomLine = new UpdateBomLine ();
////				BeanUtils.copyProperties(bomLine, updatedBomLine, CommonUtils.getNullPropertyNames(bomLine));
//				updateBomLines.add(bomLine);
//			} else {
////				AddBomLine newBomLine = new AddBomLine();
////				BeanUtils.copyProperties(updateBomLine, newBomLine, CommonUtils.getNullPropertyNames(updateBomLine));
//				updateBomLine.setBomNumber(dbBomHeader.getBomNumber());
//				BomLine bomLine = bomLineService.createBomLine(updateBomLine, loginUserID);
//
////				UpdateBomLine updatedBomLine = new UpdateBomLine ();
////				BeanUtils.copyProperties(bomLine, updatedBomLine, CommonUtils.getNullPropertyNames(bomLine));
//				updateBomLines.add(bomLine);
//			}
//
//		}

            List<BomLine> updateBomLines = bomLineService.updateBomLines(companyCode, plantId, languageId, warehouseId,
                    updateBomHeader.getBomNumber(), loginUserID, updateBomHeader.getBomLines());

            UpdateBomHeader updatedBomHeader = new UpdateBomHeader();
            BeanUtils.copyProperties(dbBomHeader, updatedBomHeader, CommonUtils.getNullPropertyNames(dbBomHeader));
            updatedBomHeader.setBomLines(updateBomLines);
            return updatedBomHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteBomHeader
     *
     * @param loginUserID
     * @param parentItemCode
     */
    public void deleteBomHeader(String warehouseId, String parentItemCode, String companyCode,
                                String languageId, String plantId, String loginUserID) {
        try {
            BomHeader bomHeader = getBomHeaderByEntity(warehouseId, parentItemCode, languageId, companyCode, plantId);
            if (bomHeader != null) {
                bomHeader.setDeletionIndicator(1L);
                bomHeader.setUpdatedBy(loginUserID);
                bomHeaderRepository.save(bomHeader);
                List<BomLine> bomLines = bomLineService.getBomLine(warehouseId, bomHeader.getBomNumber(), bomHeader.getCompanyCode(), bomHeader.getLanguageId(), bomHeader.getPlantId());

                for (BomLine bomLine : bomLines) {
                    bomLineService.deleteBomLine(warehouseId, bomHeader.getBomNumber(),
                            bomLine.getChildItemCode(), bomLine.getCompanyCode(), bomLine.getLanguageId(), bomLine.getPlantId(), loginUserID);
                }
            } else {
                throw new EntityNotFoundException("Error in deleting Id: " + parentItemCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //===========================================BomHeader_ExceptionLog================================================
    private void createBomHeaderLog(String itemCode, String languageId, String companyCodeId,
                                    String plantId, String warehouseId, String error) {

        try {
            ExceptionLog exceptionLog = new ExceptionLog();
            exceptionLog.setOrderTypeId(itemCode);
            exceptionLog.setOrderDate(new Date());
            exceptionLog.setLanguageId(languageId);
            exceptionLog.setCompanyCodeId(companyCodeId);
            exceptionLog.setPlantId(plantId);
            exceptionLog.setWarehouseId(warehouseId);
            exceptionLog.setReferenceField1(itemCode);
            exceptionLog.setErrorMessage(error);
            exceptionLog.setCreatedBy("MSD_API");
            exceptionLog.setCreatedOn(new Date());
            exceptionLogRepo.save(exceptionLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

}
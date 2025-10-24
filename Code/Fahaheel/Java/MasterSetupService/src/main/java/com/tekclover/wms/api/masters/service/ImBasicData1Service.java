package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.dto.LikeSearchInput;
import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import com.tekclover.wms.api.masters.model.imbasicdata1.AddImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.UpdateImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.repository.ExceptionLogRepository;
import com.tekclover.wms.api.masters.repository.ImBasicData1Repository;
import com.tekclover.wms.api.masters.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.masters.repository.specification.ImBasicData1Specification;
import com.tekclover.wms.api.masters.repository.specification.ImBasicData1V2Specification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class ImBasicData1Service {
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    private IDMasterService idMasterService;

    @Autowired
    private ExceptionLogRepository exceptionLogRepo;

    /**
     * @param itemCode
     * @return
     */
    public ImBasicData1 getImBasicData1(String itemCode, String warehouseId, String companyCodeId, String plantId, String uomId, String manufacturerPartNo, String languageId) {
        Optional<ImBasicData1> imbasicdata1 = imbasicdata1Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
                companyCodeId,
                plantId,
                warehouseId,
                itemCode,
                uomId,
                manufacturerPartNo,
                languageId,
                0L);
        if (!imbasicdata1.isEmpty()) {
            return imbasicdata1.get();
        }
        // Exception Log
        createImBasicData1Log3(itemCode, languageId, companyCodeId, plantId, warehouseId, uomId, manufacturerPartNo,
                "ImBasicData1 with given values and itemCode-" + itemCode + " doesn't exists.");
        return null;
    }

    /**
     *
     * @param itemCode
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param uomId
     * @param manufacturerPartNo
     * @param languageId
     * @return
     */
    public ImBasicData1V2 getaImBasicData1V2(String itemCode, String warehouseId, String companyCodeId, String plantId, String uomId, String manufacturerPartNo, String languageId) {
        Optional<ImBasicData1V2> imbasicdata1 = imBasicData1V2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
                companyCodeId,
                plantId,
                warehouseId,
                itemCode,
                uomId,
                manufacturerPartNo,
                languageId,
                0L);
        if (!imbasicdata1.isEmpty()) {
            return imbasicdata1.get();
        }
        // Exception Log
        createImBasicData1Log3(itemCode, languageId, companyCodeId, plantId, warehouseId, uomId, manufacturerPartNo,
                "ImBasicData1 with given values and itemCode-" + itemCode + " doesn't exists.");
        return null;
    }


    /**
     * @param searchImBasicData1
     * @param sortBy
     * @param pageSize
     * @param pageNo
     * @return
     * @throws Exception
     */
    public Page<ImBasicData1> findImBasicData1(SearchImBasicData1 searchImBasicData1,
                                               Integer pageNo, Integer pageSize, String sortBy)
            throws Exception {
        if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
            searchImBasicData1.setStartCreatedOn(dates[0]);
            searchImBasicData1.setEndCreatedOn(dates[1]);
        }

        if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            searchImBasicData1.setStartUpdatedOn(dates[0]);
            searchImBasicData1.setEndUpdatedOn(dates[1]);
        }

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
        Page<ImBasicData1> results = imbasicdata1Repository.findAll(spec, paging);
//		log.info("results: " + results);
        return results;
    }

    /**
     * @param searchImBasicData1
     * @return
     * @throws Exception
     */
    public List<ImBasicData1> findImBasicData1(SearchImBasicData1 searchImBasicData1)
            throws Exception {
        log.info("ImbasicData1---->" +searchImBasicData1);
        if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
            searchImBasicData1.setStartCreatedOn(dates[0]);
            searchImBasicData1.setEndCreatedOn(dates[1]);
        }

        if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            searchImBasicData1.setStartUpdatedOn(dates[0]);
            searchImBasicData1.setEndUpdatedOn(dates[1]);
        }

        ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
        List<ImBasicData1> results = imbasicdata1Repository.findAll(spec);
		log.info("results: " + results);
        return results;
    }

    //Streaming Changed to List for Fahaheel MT
    public List<ImBasicData1> findImBasicData1StreamToList(SearchImBasicData1 searchImBasicData1)
            throws Exception {
        if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
            searchImBasicData1.setStartCreatedOn(dates[0]);
            searchImBasicData1.setEndCreatedOn(dates[1]);
        }

        if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            searchImBasicData1.setStartUpdatedOn(dates[0]);
            searchImBasicData1.setEndUpdatedOn(dates[1]);
        }

        ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
        List<ImBasicData1> results = imbasicdata1Repository.findAll(spec);
		log.info("results: " + results);
        return results;
    }


    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    public List<ItemListImpl> findImBasicData1LikeSearchNew(String likeSearchByItemCodeNDesc, String companyCodeId,
                                                            String plantId, String languageId, String warehouseId) {
        if (likeSearchByItemCodeNDesc != null && !likeSearchByItemCodeNDesc.trim().isEmpty()) {
            List<ItemListImpl> data = imbasicdata1Repository.getItemListBySearchNew(likeSearchByItemCodeNDesc.trim(),
                    likeSearchByItemCodeNDesc.trim(),
                    likeSearchByItemCodeNDesc.trim(),
                    companyCodeId,
                    plantId,
                    languageId,
                    warehouseId);
            return data;
        } else {
            throw new BadRequestException("Search string must not be empty");
        }
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    public List<ItemListImpl> findImBasicData1LikeSearchV2(LikeSearchInput likeSearchInput) {

        if (likeSearchInput.getLikeSearchByDesc() != null && !likeSearchInput.getLikeSearchByDesc().trim().isEmpty()) {

            log.info("searchText1 -----> {}", likeSearchInput.getLikeSearchByDesc().trim());
            log.info("searchText2 -----> {}", likeSearchInput.getLikeSearchByDesc().trim());
            log.info("searchText3 -----> {}", likeSearchInput.getLikeSearchByDesc().trim());
            log.info("LikeSearchInput -----> {}", likeSearchInput);
            List<ItemListImpl> data = imbasicdata1Repository.getItemListBySearchV2(
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getLikeSearchByDesc().trim(),
                    likeSearchInput.getCompanyCodeId(),
                    likeSearchInput.getPlantId(),
                    likeSearchInput.getLanguageId(),
                    likeSearchInput.getWarehouseId());
            log.info("Like Search ImBasicDate size ----> {}", data.size());
            return data;
        } else {
            throw new BadRequestException("Search string must not be empty");
        }
    }


    /**
     * createImBasicData1
     *
     * @param newImBasicData1
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ImBasicData1 createImBasicData1(AddImBasicData1 newImBasicData1, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImBasicData1 dbImBasicData1 = new ImBasicData1();
        Optional<ImBasicData1> duplicateImBasicData1 = imbasicdata1Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(newImBasicData1.getCompanyCodeId(),
                newImBasicData1.getPlantId(), newImBasicData1.getWarehouseId(), newImBasicData1.getItemCode(),
                newImBasicData1.getUomId(), newImBasicData1.getManufacturerPartNo(), newImBasicData1.getLanguageId(), 0L);

        if (!duplicateImBasicData1.isEmpty()) {
            // Exception Log
            createImBasicData1Log1(newImBasicData1, "Record is getting Duplicated.");
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(newImBasicData1));
            if(newImBasicData1.getCapacityCheck() != null) {
                dbImBasicData1.setCapacityCheck(newImBasicData1.getCapacityCheck());
            }
            if(newImBasicData1.getCapacityCheck() == null) {
                dbImBasicData1.setCapacityCheck(false);
            }
            if(newImBasicData1.getItemType() == null) {
                dbImBasicData1.setItemType(1L);
            }
            dbImBasicData1.setDeletionIndicator(0L);
            dbImBasicData1.setCreatedBy(loginUserID);
            dbImBasicData1.setUpdatedBy(loginUserID);
            dbImBasicData1.setCreatedOn(new Date());
            dbImBasicData1.setUpdatedOn(new Date());
            return imbasicdata1Repository.save(dbImBasicData1);
        }
    }

    /**
     *
     * @param newImBasicData1
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public ImBasicData1V2 createImBasicData1V2(ImBasicData1V2 newImBasicData1, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImBasicData1V2 dbImBasicData1 = new ImBasicData1V2();
        Optional<ImBasicData1V2> duplicateImBasicData1 = imBasicData1V2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(newImBasicData1.getCompanyCodeId(),
                newImBasicData1.getPlantId(), newImBasicData1.getWarehouseId(), newImBasicData1.getItemCode(),
                newImBasicData1.getUomId(), newImBasicData1.getManufacturerPartNo(), newImBasicData1.getLanguageId(), 0L);

        if (!duplicateImBasicData1.isEmpty()) {
            // Exception Log
            createImBasicData1Log2(newImBasicData1, "Record is getting Duplicated.");
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(newImBasicData1));
            if(newImBasicData1.getCapacityCheck() != null) {
                dbImBasicData1.setCapacityCheck(newImBasicData1.getCapacityCheck());
            }
            if(newImBasicData1.getCapacityCheck() == null) {
                dbImBasicData1.setCapacityCheck(false);
            }
            if(newImBasicData1.getItemType() == null) {
                dbImBasicData1.setItemType(1L);
            }
            if(newImBasicData1.getManufacturerName() == null) {
                dbImBasicData1.setManufacturerName(newImBasicData1.getManufacturerPartNo());
            }
            if(newImBasicData1.getManufacturerCode() == null) {
                dbImBasicData1.setManufacturerCode(newImBasicData1.getManufacturerPartNo());
            }
            if(newImBasicData1.getManufacturerFullName() == null) {
                dbImBasicData1.setManufacturerFullName(newImBasicData1.getManufacturerPartNo());
            }

            log.info("Id: " + newImBasicData1.getCompanyCodeId() + ", " + newImBasicData1.getPlantId() + ", " + newImBasicData1.getWarehouseId() + ", " + newImBasicData1.getLanguageId());
            IKeyValuePair description = imBasicData1V2Repository.getDescription(newImBasicData1.getCompanyCodeId(),
                    newImBasicData1.getLanguageId(),
                    newImBasicData1.getPlantId(),
                    newImBasicData1.getWarehouseId());
            log.info("Description: " + description);
            if(description != null) {
                log.info("Description: " + description.getCompanyDesc() + ", " + description.getPlantDesc() + ", " + description.getWarehouseDesc());
                dbImBasicData1.setCompanyDescription(description.getCompanyDesc());
                dbImBasicData1.setPlantDescription(description.getPlantDesc());
                dbImBasicData1.setWarehouseDescription(description.getWarehouseDesc());
            }

            dbImBasicData1.setDeletionIndicator(0L);
            dbImBasicData1.setCreatedBy(loginUserID);
            dbImBasicData1.setUpdatedBy(loginUserID);
            dbImBasicData1.setCreatedOn(new Date());
            dbImBasicData1.setUpdatedOn(new Date());
            return imBasicData1V2Repository.save(dbImBasicData1);
        }
    }

    /**
     * @param itemCode
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param uomId
     * @param warehouseId
     * @param manufacturerPartNo
     * @param updateImBasicData1
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ImBasicData1 updateImBasicData1(String itemCode, String companyCodeId, String plantId, String languageId, String uomId,
                                           String warehouseId, String manufacturerPartNo, UpdateImBasicData1 updateImBasicData1, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImBasicData1 dbImBasicData1 =
                getImBasicData1(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        BeanUtils.copyProperties(updateImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(updateImBasicData1));
        dbImBasicData1.setUpdatedBy(loginUserID);
        dbImBasicData1.setUpdatedOn(new Date());
        return imbasicdata1Repository.save(dbImBasicData1);
    }
    public ImBasicData1V2 updateImBasicData1V2(String itemCode, String companyCodeId, String plantId, String languageId, String uomId,
                                               String warehouseId, String manufacturerPartNo, ImBasicData1V2 updateImBasicData1, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImBasicData1V2 dbImBasicData1 =
                getaImBasicData1V2(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        BeanUtils.copyProperties(updateImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(updateImBasicData1));
        dbImBasicData1.setUpdatedBy(loginUserID);
        dbImBasicData1.setUpdatedOn(new Date());
        imBasicData1V2Repository.delete(dbImBasicData1);
        return imBasicData1V2Repository.save(dbImBasicData1);
    }

    /**
     * @param itemCode
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param uomId
     * @param manufacturerPartNo
     * @param warehouseId
     * @param loginUserID
     */
    public void deleteImBasicData1(String itemCode, String companyCodeId, String plantId, String languageId, String uomId, String manufacturerPartNo, String warehouseId, String loginUserID) throws ParseException {
        ImBasicData1 imbasicdata1 = getImBasicData1(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        if (imbasicdata1 != null) {
            imbasicdata1.setDeletionIndicator(1L);
            imbasicdata1.setUpdatedBy(loginUserID);
            imbasicdata1.setUpdatedOn(new Date());
            imbasicdata1Repository.save(imbasicdata1);
        } else {
            throw new EntityNotFoundException("Error in deleting itemCode Id:" + itemCode);
        }
    }

    public void deleteImBasicData1V2(String itemCode, String companyCodeId, String plantId, String languageId, String uomId, String manufacturerPartNo, String warehouseId, String loginUserID) throws ParseException {
        ImBasicData1V2 imbasicdata1 = getaImBasicData1V2(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        if (imbasicdata1 != null) {
            imbasicdata1.setDeletionIndicator(1L);
            imbasicdata1.setUpdatedBy(loginUserID);
            imbasicdata1.setUpdatedOn(new Date());
            imBasicData1V2Repository.save(imbasicdata1);
        } else {
            throw new EntityNotFoundException("Error in deleting itemCode Id:" + itemCode);
        }
    }

    public ImBasicData1V2 getImBasicData1V2(ImBasicData imBasicData) {
        try {
            log.info("ImBasicData :" + imBasicData);
            Optional<ImBasicData1V2> imbasicdata1 = imBasicData1V2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
                    imBasicData.getCompanyCodeId(),
                    imBasicData.getPlantId(),
                    imBasicData.getWarehouseId(),
                    imBasicData.getItemCode(),
                    imBasicData.getManufacturerName(),
                    imBasicData.getLanguageId(),
                    0L);
            if (!imbasicdata1.isEmpty()) {
                return imbasicdata1.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Exception Log
            log.info("ImBasicData1 with given values Not Available!");
        }
        return null;
    }

    //Streaming
    public Stream<ImBasicData1V2> findImBasicData1V2Stream(SearchImBasicData1 searchImBasicData1)
            throws Exception {
        if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
            searchImBasicData1.setStartCreatedOn(dates[0]);
            searchImBasicData1.setEndCreatedOn(dates[1]);
        }

        if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            searchImBasicData1.setStartUpdatedOn(dates[0]);
            searchImBasicData1.setEndUpdatedOn(dates[1]);
        }

        ImBasicData1V2Specification spec = new ImBasicData1V2Specification(searchImBasicData1);
        Stream<ImBasicData1V2> results = imBasicData1V2Repository.stream(spec, ImBasicData1V2.class);
//		log.info("results: " + results);
        return results;
    }

    /**
     *
     * @param itemCode
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param manufacturerPartNo
     * @param warehouseId
     * @return
     */
    public Boolean deleteImBasicData1V2(String itemCode, String companyCodeId, String plantId, String languageId,
                                        String manufacturerPartNo, String warehouseId) {
        Optional<ImBasicData1V2> imbasicdata1 = imBasicData1V2Repository.
                findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerPartNo, 0L);
        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
            imBasicData1V2Repository.delete(imbasicdata1.get());
            return true;
        } else {
            throw new EntityNotFoundException("Error in deleting itemCode Id:" + itemCode);
        }
    }

    //========================================ImBasicData1_ExceptionLog================================================

    private void createImBasicData1Log1(AddImBasicData1 addImBasicData1, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(addImBasicData1.getItemCode());
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setLanguageId(addImBasicData1.getLanguageId());
        exceptionLog.setCompanyCodeId(addImBasicData1.getCompanyCodeId());
        exceptionLog.setPlantId(addImBasicData1.getPlantId());
        exceptionLog.setWarehouseId(addImBasicData1.getWarehouseId());
        exceptionLog.setReferenceField1(addImBasicData1.getItemCode());
        exceptionLog.setReferenceField2(addImBasicData1.getUomId());
        exceptionLog.setReferenceField3(addImBasicData1.getManufacturerPartNo());
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    private void createImBasicData1Log2(ImBasicData1 imBasicData1, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(imBasicData1.getItemCode());
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setLanguageId(imBasicData1.getLanguageId());
        exceptionLog.setCompanyCodeId(imBasicData1.getCompanyCodeId());
        exceptionLog.setPlantId(imBasicData1.getPlantId());
        exceptionLog.setWarehouseId(imBasicData1.getWarehouseId());
        exceptionLog.setReferenceField1(imBasicData1.getItemCode());
        exceptionLog.setReferenceField2(imBasicData1.getUomId());
        exceptionLog.setReferenceField3(imBasicData1.getManufacturerPartNo());
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    private void createImBasicData1Log3(String itemCode, String languageId, String companyCodeId, String plantId,
                                        String warehouseId, String uomId, String manufacturerPartNo, String error) {

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setOrderTypeId(itemCode);
        exceptionLog.setOrderDate(new Date());
        exceptionLog.setLanguageId(languageId);
        exceptionLog.setCompanyCodeId(companyCodeId);
        exceptionLog.setPlantId(plantId);
        exceptionLog.setWarehouseId(warehouseId);
        exceptionLog.setReferenceField1(itemCode);
        exceptionLog.setReferenceField2(uomId);
        exceptionLog.setReferenceField3(manufacturerPartNo);
        exceptionLog.setErrorMessage(error);
        exceptionLog.setCreatedBy("MSD_API");
        exceptionLog.setCreatedOn(new Date());
        exceptionLogRepo.save(exceptionLog);
    }

    public List<ImBasicData1V2> imBasicData1Upload(List<ImBasicData1V2> imBasicDataList) {

        List<ImBasicData1V2> saveImBasicData1 = new ArrayList<>();
        for (ImBasicData1V2 imData1 : imBasicDataList) {
            ImBasicData1V2 dbImBasicData1 = new ImBasicData1V2();
            Optional<ImBasicData1V2> duplicateImBasicData1 = imBasicData1V2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(imData1.getCompanyCodeId(),
                    imData1.getPlantId(), imData1.getWarehouseId(), imData1.getItemCode(),
                    imData1.getUomId(), imData1.getManufacturerPartNo(), imData1.getLanguageId(), 0L);

            log.info("duplicateImBasicData1 ----> ", duplicateImBasicData1.get());

            if (duplicateImBasicData1.isPresent()) {
                throw new BadRequestException("Record is Getting Duplicate");
            } else {
                BeanUtils.copyProperties(imData1, dbImBasicData1, CommonUtils.getNullPropertyNames(imData1));
                dbImBasicData1.setDeletionIndicator(0L);
                dbImBasicData1.setCreatedOn(new Date());
                dbImBasicData1.setUpdatedOn(new Date());
                imBasicData1V2Repository.save(dbImBasicData1);
                saveImBasicData1.add(dbImBasicData1);
            }
        }
        return saveImBasicData1;
    }

}

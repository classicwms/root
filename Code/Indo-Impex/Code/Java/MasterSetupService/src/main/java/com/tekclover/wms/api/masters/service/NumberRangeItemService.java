package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.numberrangeitem.AddNumberRangeItem;
import com.tekclover.wms.api.masters.model.numberrangeitem.NumberRangeItem;
import com.tekclover.wms.api.masters.model.numberrangeitem.SearchNumberRangeItem;
import com.tekclover.wms.api.masters.model.numberrangeitem.UpdateNumberRangeItem;
import com.tekclover.wms.api.masters.repository.NumberRangeItemRepository;
import com.tekclover.wms.api.masters.repository.specification.NumberRangeItemSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NumberRangeItemService {

    @Autowired
    private NumberRangeItemRepository numberRangeItemRepository;

    /**
     * getAllNumberRangeItem
     *
     * @return
     */
    public List<NumberRangeItem> getAllNumberRangeItem() {
        try {
            List<NumberRangeItem> numberRangeItemList = numberRangeItemRepository.findAll();
            log.info("numberRangeItem : " + numberRangeItemList);
            numberRangeItemList = numberRangeItemList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return numberRangeItemList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getDock
     *
     * @param itemTypeId
     * @param sequenceNo
     * @return
     */
    public NumberRangeItem getNumberRangeItem(String warehouseId, String companyCodeId, String languageId, String plantId, Long itemTypeId, Long sequenceNo) {
        try {
            Optional<NumberRangeItem> dbNumberRange =
                    numberRangeItemRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemTypeIdAndSequenceNoAndDeletionIndicator(
                            companyCodeId,
                            plantId,
                            languageId,
                            warehouseId,
                            itemTypeId,
                            sequenceNo,
                            0L);
            if (dbNumberRange.isEmpty()) {
                throw new BadRequestException("The given values : " +
                        "warehouseId - " + warehouseId +
                        "companyCodeId - " + companyCodeId +
                        "plantId - " + plantId +
                        "itemTypeId - " + itemTypeId +
                        "sequenceNo - " + sequenceNo +
                        "doesn't exist.");

            }
            return dbNumberRange.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchNumberRangeItem
     * @return
     * @throws Exception
     */
    public List<NumberRangeItem> findNumberRangeItem(SearchNumberRangeItem searchNumberRangeItem) {
        try {
            NumberRangeItemSpecification spec = new NumberRangeItemSpecification(searchNumberRangeItem);
            List<NumberRangeItem> results = numberRangeItemRepository.findAll(spec);
            log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * createNumberRangeItem
     *
     * @param newNumberRangeItem
     * @return
     */
    public NumberRangeItem createNumberRangeItem(AddNumberRangeItem newNumberRangeItem, String loginUserID) {
        try {
            NumberRangeItem dbNUmberRangeItem = new NumberRangeItem();
            Optional<NumberRangeItem> duplicateNumberRange = numberRangeItemRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemTypeIdAndSequenceNoAndDeletionIndicator(
                    newNumberRangeItem.getCompanyCodeId(),
                    newNumberRangeItem.getLanguageId(),
                    newNumberRangeItem.getPlantId(),
                    newNumberRangeItem.getWarehouseId(),
                    newNumberRangeItem.getItemTypeId(),
                    newNumberRangeItem.getSequenceNo(),
                    0L);
            if (!duplicateNumberRange.isEmpty()) {
                throw new BadRequestException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newNumberRangeItem, dbNUmberRangeItem, CommonUtils.getNullPropertyNames(newNumberRangeItem));
                dbNUmberRangeItem.setDeletionIndicator(0L);
                dbNUmberRangeItem.setCreatedBy(loginUserID);
                dbNUmberRangeItem.setUpdatedBy(loginUserID);
                dbNUmberRangeItem.setCreatedOn(new Date());
                dbNUmberRangeItem.setUpdatedOn(new Date());
                return numberRangeItemRepository.save(dbNUmberRangeItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateNumberRange
     *
     * @param sequenceNo
     * @param itemTypeId
     * @return
     */
    public NumberRangeItem updateNumberRangeItem(String companyCodeId, String plantId, String warehouseId, String languageId, Long itemTypeId,
                                                 Long sequenceNo, UpdateNumberRangeItem updateNumberRangeItem, String loginUserID) {
        try {
            NumberRangeItem dbNumberRangeItem = getNumberRangeItem(warehouseId, companyCodeId, languageId, plantId, itemTypeId, sequenceNo);
            BeanUtils.copyProperties(updateNumberRangeItem, dbNumberRangeItem, CommonUtils.getNullPropertyNames(updateNumberRangeItem));
            dbNumberRangeItem.setUpdatedBy(loginUserID);
            dbNumberRangeItem.setUpdatedOn(new Date());
            return numberRangeItemRepository.save(dbNumberRangeItem);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteNumberRange
     *
     * @param sequenceNo
     * @param itemTypeId
     */
    public void deleteNumberRangeItem(String companyCodeId, String languageId, String plantId,
                                      String warehouseId, Long sequenceNo, Long itemTypeId, String loginUserID) {
        try {
            NumberRangeItem numberRangeItem = getNumberRangeItem(warehouseId, companyCodeId, languageId, plantId, itemTypeId, sequenceNo);
            if (numberRangeItem != null) {
                numberRangeItem.setDeletionIndicator(1L);
                numberRangeItem.setUpdatedBy(loginUserID);
                numberRangeItem.setUpdatedOn(new Date());
                numberRangeItemRepository.save(numberRangeItem);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + itemTypeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}
package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.statusid.FindStatusId;
import com.tekclover.wms.api.idmaster.repository.Specification.StatusIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.statusid.AddStatusId;
import com.tekclover.wms.api.idmaster.model.statusid.StatusId;
import com.tekclover.wms.api.idmaster.model.statusid.UpdateStatusId;
import com.tekclover.wms.api.idmaster.repository.StatusIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatusIdService {

    @Autowired
    private StatusIdRepository statusIdRepository;

    @Autowired
    private LanguageIdService languageIdService;

    /**
     * getStatusIds
     *
     * @return
     */
    public List<StatusId> getStatusIds() {
        List<StatusId> statusIdList = statusIdRepository.findAll();
        statusIdList = statusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return statusIdList;
    }

    /**
     * getStatusId
     *
     * @param statusId
     * @return
     */
    public StatusId getStatusId(Long statusId, String languageId) {
        Optional<StatusId> dbStatusId =
                statusIdRepository.findByLanguageIdAndStatusIdAndDeletionIndicator(
                        languageId,
                        statusId,
                        0L
                );
        if (dbStatusId.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "statusId - " + statusId +
                    " doesn't exist.");

        }
        return dbStatusId.get();
    }

    /**
     * @param statusId
     * @param languageId
     * @param warehouseId
     * @return
     */
//    public StatusId getStatusId(Long statusId, String languageId, String warehouseId) {
//        Optional<StatusId> dbStatusId =
//                statusIdRepository.findByLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
//                        languageId,
//                        warehouseId,
//                        statusId,
//                        0L
//                );
//        if (dbStatusId.isEmpty()) {
//            throw new BadRequestException("The given values : " +
//                    "statusId - " + statusId +
//                    " doesn't exist.");
//
//        }
//        return dbStatusId.get();
//    }

//	/**
//	 * 
//	 * @param searchStatusId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StatusId> findStatusId(SearchStatusId searchStatusId) 
//			throws ParseException {
//		StatusIdSpecification spec = new StatusIdSpecification(searchStatusId);
//		List<StatusId> results = statusIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

    /**
     * createStatusId
     *
     * @param newStatusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StatusId createStatusId(AddStatusId newStatusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StatusId dbStatusId = new StatusId();
        Optional<StatusId> duplicateStatusId = statusIdRepository.findByLanguageIdAndStatusIdAndDeletionIndicator(newStatusId.getLanguageId(), newStatusId.getStatusId(), 0L);
        if (!duplicateStatusId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            LanguageId dbLanguageId = languageIdService.getLanguageId(newStatusId.getLanguageId());
            log.info("newStatusId : " + newStatusId);
            BeanUtils.copyProperties(newStatusId, dbStatusId, CommonUtils.getNullPropertyNames(newStatusId));
            dbStatusId.setDeletionIndicator(0L);
            dbStatusId.setCreatedBy(loginUserID);
            dbStatusId.setUpdatedBy(loginUserID);
            dbStatusId.setCreatedOn(new Date());
            dbStatusId.setUpdatedOn(new Date());
            return statusIdRepository.save(dbStatusId);
        }
    }

    /**
     * updateStatusId
     *
     * @param loginUserID
     * @param statusId
     * @param updateStatusId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StatusId updateStatusId(Long statusId, String languageId, String loginUserID,
                                   UpdateStatusId updateStatusId)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        StatusId dbStatusId = getStatusId(statusId, languageId);
        if(dbStatusId.getStatus().equalsIgnoreCase(updateStatusId.getStatus())) {
        BeanUtils.copyProperties(updateStatusId, dbStatusId, CommonUtils.getNullPropertyNames(updateStatusId));
        dbStatusId.setUpdatedBy(loginUserID);
        dbStatusId.setUpdatedOn(new Date());
        return statusIdRepository.save(dbStatusId);
        }
        if(!dbStatusId.getStatus().equalsIgnoreCase(updateStatusId.getStatus())) {
            String oldStatusText = dbStatusId.getStatus();
            BeanUtils.copyProperties(updateStatusId, dbStatusId, CommonUtils.getNullPropertyNames(updateStatusId));
            dbStatusId.setUpdatedBy(loginUserID);
            dbStatusId.setUpdatedOn(new Date());
            StatusId savedStatusId =statusIdRepository.save(dbStatusId);

            //update transaction table status description using stored procedure
            statusIdRepository.updateStatusDescriptionProc(languageId, statusId, updateStatusId.getStatus(), oldStatusText);

            return savedStatusId;
        }
        return dbStatusId;
    }

    /**
     * deleteStatusId
     *
     * @param loginUserID
     * @param statusId
     */
    public void deleteStatusId(Long statusId, String languageId, String loginUserID) {
        StatusId dbStatusId = getStatusId(statusId, languageId);
        if (dbStatusId != null) {
            dbStatusId.setDeletionIndicator(1L);
            dbStatusId.setUpdatedBy(loginUserID);
            statusIdRepository.save(dbStatusId);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + statusId);
        }
    }

    //Find StatusId
    public List<StatusId> findStatusId(FindStatusId findStatusId) throws ParseException {

        StatusIdSpecification spec = new StatusIdSpecification(findStatusId);
        List<StatusId> results = statusIdRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        return results;
    }
}

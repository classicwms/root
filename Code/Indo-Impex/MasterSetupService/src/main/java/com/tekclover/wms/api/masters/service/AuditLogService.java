package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.auditlog.AddAuditLog;
import com.tekclover.wms.api.masters.model.auditlog.AuditLog;
import com.tekclover.wms.api.masters.model.auditlog.SearchAuditLog;
import com.tekclover.wms.api.masters.model.auditlog.UpdateAuditLog;
import com.tekclover.wms.api.masters.repository.AuditLogRepository;
import com.tekclover.wms.api.masters.repository.specification.AuditLogSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditlogRepository;

    /**
     * getAuditLogs
     *
     * @return
     */
    public List<AuditLog> getAuditLogs() {
        try {
            List<AuditLog> auditlogList = auditlogRepository.findAll();
            log.info("auditlogList : " + auditlogList);
            auditlogList = auditlogList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return auditlogList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getAuditLog
     *
     * @param auditFileNumber
     * @return
     */
    public AuditLog getAuditLog(String auditFileNumber) {
        try {
            AuditLog auditlog = auditlogRepository.findByAuditFileNumberAndDeletionIndicator(auditFileNumber, 0L);
            if (auditlog != null && auditlog.getDeletionIndicator() != null && auditlog.getDeletionIndicator() == 0) {
                return auditlog;
            } else {
                throw new BadRequestException("The given AuditLog ID : " + auditFileNumber + " doesn't exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * findAuditLog
     *
     * @param searchAuditLog
     * @return
     */
    public Stream<AuditLog> findAuditLog(SearchAuditLog searchAuditLog) {
        try {
            if (searchAuditLog.getStartCreatedOn() != null && searchAuditLog.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchAuditLog.getStartCreatedOn(), searchAuditLog.getEndCreatedOn());
                searchAuditLog.setStartCreatedOn(dates[0]);
                searchAuditLog.setEndCreatedOn(dates[1]);
            }
            AuditLogSpecification spec = new AuditLogSpecification(searchAuditLog);
            Stream<AuditLog> results = auditlogRepository.stream(spec, AuditLog.class);
//		log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createAuditLog
     *
     * @param newAuditLog
     * @return
     */
    public AuditLog createAuditLog(AddAuditLog newAuditLog, String loginUserID) {
        try {
            AuditLog dbAuditLog = new AuditLog();
//		AuditLog duplicateAuditLog = auditlogRepository.findByAuditFileNumberAndDeletionIndicator(newAuditLog.getAuditFileNumber(), 0L);
//		if (duplicateAuditLog != null) {
//			throw new EntityNotFoundException("Record is Getting duplicated");
//		} else {
            BeanUtils.copyProperties(newAuditLog, dbAuditLog, CommonUtils.getNullPropertyNames(newAuditLog));
            dbAuditLog.setAuditLogNumber(System.currentTimeMillis());
            dbAuditLog.setAuditFileNumber(String.valueOf(dbAuditLog.getAuditLogNumber()));
            dbAuditLog.setDeletionIndicator(0L);
            dbAuditLog.setCreatedBy(loginUserID);
            dbAuditLog.setUpdatedBy(loginUserID);
            dbAuditLog.setCreatedOn(new Date());
            dbAuditLog.setUpdatedOn(new Date());
            return auditlogRepository.save(dbAuditLog);
//		}
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * updateAuditLog
     *
     * @param auditFileNumber
     * @param updateAuditLog
     * @return
     */
    public AuditLog updateAuditLog(String auditFileNumber, UpdateAuditLog updateAuditLog, String loginUserID) {
        try {
            AuditLog dbAuditLog = getAuditLog(auditFileNumber);
            BeanUtils.copyProperties(updateAuditLog, dbAuditLog, CommonUtils.getNullPropertyNames(updateAuditLog));
            dbAuditLog.setUpdatedBy(loginUserID);
            dbAuditLog.setUpdatedOn(new Date());
            return auditlogRepository.save(dbAuditLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deleteAuditLog
     *
     * @param auditFileNumber
     */
    public void deleteAuditLog(String auditFileNumber, String loginUserID) {
        try {
            AuditLog auditlog = getAuditLog(auditFileNumber);
            if (auditlog != null) {
                auditlog.setDeletionIndicator(1L);
                auditlog.setUpdatedBy(loginUserID);
                auditlog.setUpdatedOn(new Date());
                auditlogRepository.save(auditlog);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + auditFileNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}
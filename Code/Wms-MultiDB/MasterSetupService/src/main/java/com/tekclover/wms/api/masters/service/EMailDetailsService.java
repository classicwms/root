package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.email.EMailDetails;
import com.tekclover.wms.api.masters.model.email.FindEmailDetails;
import com.tekclover.wms.api.masters.repository.EMailDetailsRepository;
import com.tekclover.wms.api.masters.repository.specification.EmailDetailsSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class EMailDetailsService {

    @Autowired
    private EMailDetailsRepository eMailDetailsRepository;

    /**
     * @param newEMailDetails
     * @return
     */
    //Create Mail
    public EMailDetails createEMailDetails(EMailDetails newEMailDetails, String loginUserId) {
        try {
            newEMailDetails.setDeletionIndicator(0L);
            newEMailDetails.setCreatedBy(loginUserId);
            newEMailDetails.setCreatedOn(new Date());
            return eMailDetailsRepository.save(newEMailDetails);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param emailId
     * @param updateEMailDetails
     * @return
     */
    //Update Mail
    public EMailDetails updateEMailDetails(Long emailId, EMailDetails updateEMailDetails, String loginUserId) {
        try {
            EMailDetails dbEMailDetails = getEMailDetails(emailId);
            BeanUtils.copyProperties(updateEMailDetails, dbEMailDetails, CommonUtils.getNullPropertyNames(updateEMailDetails));
            dbEMailDetails.setUpdatedBy(loginUserId);
            dbEMailDetails.setUpdatedOn(new Date());
            return eMailDetailsRepository.save(dbEMailDetails);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param emailId
     * @return
     */
    //get Mail
    public EMailDetails getEMailDetails(Long emailId) {

        try {
            Optional<EMailDetails> dbEMailDetails = eMailDetailsRepository.findByEmailIdAndDeletionIndicator(emailId, 0L);
            if (dbEMailDetails != null) {
                return dbEMailDetails.get();
            } else {
                throw new BadRequestException("The given emailId doesn't exist : " + emailId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //get all
    public List<EMailDetails> getEMailDetailsList() {
        try {
            List<EMailDetails> EMailDetailsList = eMailDetailsRepository.findAll();
            EMailDetailsList = EMailDetailsList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
            return EMailDetailsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param emailId
     */
    //Delete Mail
    public void deleteEMailDetails(Long emailId, String loginUserId) {
        try {
            EMailDetails dbEMailDetails = getEMailDetails(emailId);
            if (dbEMailDetails != null) {
                dbEMailDetails.setDeletionIndicator(1L);
                dbEMailDetails.setUpdatedBy(loginUserId);
                dbEMailDetails.setUpdatedOn(new Date());
                eMailDetailsRepository.save(dbEMailDetails);
            } else {
                throw new BadRequestException("The given emailId doesn't exist : " + emailId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param emailId
     * @return
     */
    public EMailDetails undeleteEMailDetails(Long emailId, String loginUserId) {
        try {
            Optional<EMailDetails> dbEMailDetails = eMailDetailsRepository.findByEmailIdAndDeletionIndicator(emailId, 1L);
            if (!dbEMailDetails.isEmpty()) {
                dbEMailDetails.get().setDeletionIndicator(0L);
                dbEMailDetails.get().setUpdatedBy(loginUserId);
                dbEMailDetails.get().setUpdatedOn(new Date());
                eMailDetailsRepository.save(dbEMailDetails.get());
                return dbEMailDetails.get();
            } else {
                throw new BadRequestException("The given emailId doesn't exist : " + emailId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //Streaming
    public Stream<EMailDetails> findEmailDetails(FindEmailDetails findEmailDetails) {

        try {
            if (findEmailDetails.getStartDate() != null && findEmailDetails.getEndDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(findEmailDetails.getStartDate(), findEmailDetails.getEndDate());
                findEmailDetails.setStartDate(dates[0]);
                findEmailDetails.setEndDate(dates[1]);
            }
            log.info("FindEmailDeatils : " + findEmailDetails);
            EmailDetailsSpecification spec = new EmailDetailsSpecification(findEmailDetails);
            Stream<EMailDetails> results = eMailDetailsRepository.stream(spec, EMailDetails.class);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}
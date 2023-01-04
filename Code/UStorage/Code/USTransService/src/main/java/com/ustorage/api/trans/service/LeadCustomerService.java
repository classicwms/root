package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.Enum.LeadCustomerTypeEnum;
import com.ustorage.api.trans.controller.exception.BadRequestException;

import com.ustorage.api.trans.model.leadcustomer.*;

import com.ustorage.api.trans.repository.Specification.LeadCustomerSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.repository.LeadCustomerRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LeadCustomerService {

    @Autowired
    private LeadCustomerRepository leadCustomerRepository;

    public List<LeadCustomer> getLeadCustomer() {
        List<LeadCustomer> leadCustomerList = leadCustomerRepository.findAll();
        leadCustomerList = leadCustomerList.stream().filter(n -> n.getDeletionIndicator() == 0L).collect(Collectors.toList());
        return leadCustomerList;
    }

    /**
     * getLeadCustomer
     *
     * @param leadCustomerId
     * @return
     */
    public LeadCustomer getLeadCustomer(String leadCustomerId) {
        return leadCustomerRepository.findByCustomerCode(leadCustomerId).orElse(null);
    }

    /**
     * createLeadCustomer
     *
     * @param newLeadCustomer
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public LeadCustomer createLeadCustomer(AddLeadCustomer newLeadCustomer, String loginUserId)
            throws IllegalAccessException, InvocationTargetException, Exception {
        LeadCustomer dbLeadCustomer = new LeadCustomer();

        BeanUtils.copyProperties(newLeadCustomer, dbLeadCustomer, CommonUtils.getNullPropertyNames(newLeadCustomer));
        dbLeadCustomer.setDeletionIndicator(0L);
        dbLeadCustomer.setCreatedBy(loginUserId);
        dbLeadCustomer.setUpdatedBy(loginUserId);
        dbLeadCustomer.setCreatedOn(new Date());
        dbLeadCustomer.setUpdatedOn(new Date());
        return leadCustomerRepository.save(dbLeadCustomer);
    }

    /**
     * updateLeadCustomer
     *
     * @param customerCode
     * @param loginUserId
     * @param updateLeadCustomer
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public LeadCustomer updateLeadCustomer (String customerCode, String loginUserId, UpdateLeadCustomer updateLeadCustomer)
            throws IllegalAccessException, InvocationTargetException {
        LeadCustomer dbLeadCustomer = getLeadCustomer(customerCode);
        BeanUtils.copyProperties(updateLeadCustomer, dbLeadCustomer, CommonUtils.getNullPropertyNames(updateLeadCustomer));
        dbLeadCustomer.setUpdatedBy(loginUserId);
        dbLeadCustomer.setUpdatedOn(new Date());
        return leadCustomerRepository.save(dbLeadCustomer);
    }
 /*   @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public LeadCustomer updateLeadCustomer(String customerCode, String loginUserId, UpdateLeadCustomer updateLeadCustomer)
            throws Exception {
        LeadCustomer dbLeadCustomer = getLeadCustomer(customerCode);
        if (dbLeadCustomer == null) {
            throw new EntityNotFoundException("Error in deleting Id: " + customerCode);
        }
        BeanUtils.copyProperties(updateLeadCustomer, dbLeadCustomer, CommonUtils.getNullPropertyNames(updateLeadCustomer));
        dbLeadCustomer.setUpdatedBy(loginUserId);
        dbLeadCustomer.setUpdatedOn(new Date());
        if (dbLeadCustomer.getType().equals(LeadCustomerTypeEnum.CUSTOMER)) {
            dbLeadCustomer.setIsActive(true);
            dbLeadCustomer.setType(LeadCustomerTypeEnum.LEAD);
            leadCustomerRepository.save(dbLeadCustomer);
            AddLeadCustomer createCustomer = new AddLeadCustomer();
            BeanUtils.copyProperties(updateLeadCustomer, createCustomer, CommonUtils.getNullPropertyNames(updateLeadCustomer));
            return createLeadCustomer(createCustomer,loginUserId);
        } else {
            return leadCustomerRepository.save(dbLeadCustomer);
        }
    }*/

    public List<LeadCustomer> findLeadCustomer(FindLeadCustomer findLeadCustomer) throws ParseException {

        LeadCustomerSpecification spec = new LeadCustomerSpecification(findLeadCustomer);
        List<LeadCustomer> results = leadCustomerRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        return results;
    }

    /**
     * deleteLeadCustomer
     *
     * @param loginUserID
     * @param leadcustomerModuleId
     */
    public void deleteLeadCustomer(String leadcustomerModuleId, String loginUserID) {
        LeadCustomer leadcustomer = getLeadCustomer(leadcustomerModuleId);
        if (leadcustomer != null) {
            leadcustomer.setDeletionIndicator(1L);
            leadcustomer.setUpdatedBy(loginUserID);
            leadcustomer.setUpdatedOn(new Date());
            leadCustomerRepository.save(leadcustomer);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + leadcustomerModuleId);
        }
    }
}

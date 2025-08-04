package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.masters.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CustomerMasterRepository extends JpaRepository<Customer, String> {


    List<Customer> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    Customer findByPartnerCode(String partnerCode);

    Customer findTopByCompanyCodeAndBranchCodeAndPartnerCodeOrderByOrderReceivedOnDesc(
            String companyCodeId, String plantId, String partnerCode);

    Customer findTopByCompanyCodeAndBranchCodeAndPartnerCodeAndProcessedStatusIdOrderByOrderReceivedOnDesc(
            String companyCodeId, String plantId, String partnerCode, long l);

    Customer findTopByCompanyCodeAndPartnerCodeAndProcessedStatusIdOrderByOrderReceivedOnDesc(
            String companyCodeId, String partnerCode, long l);

    Customer findTopByCompanyCodeAndPartnerCodeOrderByOrderReceivedOnDesc(
            String companyCodeId, String partnerCode);

    Customer findTopByCompanyCodeAndBranchCodeAndPartnerCodeAndProcessedStatusIdOrderByOrderReceivedOn(
            String companyCodeId, String plantId, String partnerCode, long l);

    Customer findTopByCompanyCodeAndPartnerCodeOrderByOrderReceivedOn(String companyCodeId, String partnerCode);
}




package com.almailem.ams.api.connector.repository;


import com.almailem.ams.api.connector.model.master.CustomerMaster;
import com.almailem.ams.api.connector.model.master.ItemMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, String>, JpaSpecificationExecutor<CustomerMaster> {

    CustomerMaster findByCustomerCode(String customerCode);

    List<CustomerMaster> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE CUSTOMERMASTER set processedStatusId = 10, orderProcessedOn = :date  \r\n"
            + " WHERE Customercode = :customerCode ", nativeQuery = true)
    public void updateProcessStatusIdWithDate (
            @Param(value = "customerCode") String customerCode,
            @Param(value = "date") Date date);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE CUSTOMERMASTER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE customerMasterId = :customerMasterId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "customerMasterId") Long customerMasterId,
            @Param(value = "processedStatusId")  Long processedStatusId);

    List<CustomerMaster> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    CustomerMaster findTopByCompanyCodeAndBranchCodeAndCustomerCodeOrderByOrderReceivedOnDesc(String companyCode, String branchCode, String partnerCode);

    CustomerMaster findTopByCustomerMasterIdAndCompanyCodeAndBranchCodeAndCustomerCodeOrderByOrderReceivedOnDesc(
            Long customerMasterId, String companyCode, String branchCode, String partnerCode);

    CustomerMaster findTopByCustomerMasterIdAndCompanyCodeAndBranchCodeAndCustomerCodeOrderByOrderReceivedOn(
            Long customerMasterId, String companyCode, String branchCode, String partnerCode);

    @Query(value = "SELECT * FROM CUSTOMERMASTER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<CustomerMaster> findByCustomerMaster(@Param("statusId") Long statusId,
                                      @Param("branchCode") List<String> branchCode);
}

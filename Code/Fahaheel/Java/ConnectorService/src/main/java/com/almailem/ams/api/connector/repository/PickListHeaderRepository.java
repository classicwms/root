package com.almailem.ams.api.connector.repository;


import java.util.List;
import java.util.Optional;

import com.almailem.ams.api.connector.model.salesinvoice.SalesInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almailem.ams.api.connector.model.picklist.PickListHeader;

@Repository
@Transactional
public interface PickListHeaderRepository extends JpaRepository<PickListHeader, String>, JpaSpecificationExecutor<PickListHeader> {
    List<PickListHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
    List<PickListHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);
    Optional<PickListHeader> findByPickListNo(String asnNumber);

    @Query(value = "select * \n" +
            "from PICKLISTHEADER where Picklistheaderid = :pickListHeaderId ",nativeQuery = true)
    public PickListHeader getPickListHeader(@Param(value = "pickListHeaderId") Long pickListHeaderId);

    PickListHeader findTopByPickListNoOrderByOrderReceivedOnDesc(String asnNumber);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE PICKLISTHEADER set processedStatusId = 10, orderProcessedOn = :date  \r\n"
//            + " WHERE PickListNo = :pickListNo ", nativeQuery = true)
//    public void updateProcessStatusId (
//            @Param(value = "pickListNo") String pickListNo,
//            @Param(value = "date") Date date);
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PICKLISTHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE PickListHeaderId = :pickListHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "pickListHeaderId") Long PickListHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId);

    PickListHeader findTopByPickListHeaderIdAndCompanyCodeAndBranchCodeAndPickListNoOrderByOrderReceivedOnDesc(
            Long pickListHeaderId, String companyCode, String branchCode, String pickListNo);

    @Query(value = "SELECT * FROM PICKLISTHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<PickListHeader> findByPickList(@Param("statusId") Long statusId,
                                          @Param("branchCode") List<String> branchCode);
}

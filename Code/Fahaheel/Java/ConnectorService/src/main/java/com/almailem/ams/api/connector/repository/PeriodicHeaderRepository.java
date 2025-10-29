package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.master.ItemMaster;
import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PeriodicHeaderRepository extends JpaRepository<PeriodicHeader, String>, JpaSpecificationExecutor<PeriodicHeader> {

    List<PeriodicHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long l);

    PeriodicHeader findTopByCycleCountNoOrderByOrderReceivedOnDesc(String cycleCountNo);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE PERIODICHEADER set processedStatusId = 10, orderProcessedOn = :date \r\n"
//            + " WHERE CycleCountNo = :cycleCountNo ", nativeQuery = true)
//    void updateProcessStatusId(@Param(value = "cycleCountNo") String cycleCountNo,
//                               @Param(value = "date") Date date);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PERIODICHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE PeriodicHeaderId = :periodicHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "periodicHeaderId") Long periodicHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId);

    @Query(value = "select * \n" +
            "from PERIODICHEADER where Periodicheaderid = :periodicHeaderId ",nativeQuery = true)
    public PeriodicHeader getPeriodicHeader(@Param(value = "periodicHeaderId") Long periodicHeaderId);

    PeriodicHeader findTopByPeriodicHeaderIdAndCompanyCodeAndBranchCodeAndCycleCountNoOrderByOrderReceivedOnDesc(
            Long periodicHeaderId, String companyCode, String branchCode, String cycleCountNo);

    List<PeriodicHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    @Query(value = "SELECT * FROM PERIODICHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<PeriodicHeader> findByPERIODICHEADER(@Param("statusId") Long statusId,
                                      @Param("branchCode") List<String> branchCode);

    Optional<PeriodicHeader> findByCycleCountNo(String cycleCountNo);
}

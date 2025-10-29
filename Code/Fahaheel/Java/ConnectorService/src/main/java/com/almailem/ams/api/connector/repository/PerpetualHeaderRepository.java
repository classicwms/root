package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.master.ItemMaster;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
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
public interface PerpetualHeaderRepository extends JpaRepository<PerpetualHeader, String>, JpaSpecificationExecutor<PerpetualHeader> {
    List<PerpetualHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    PerpetualHeader findTopByCycleCountNoOrderByOrderReceivedOnDesc(String cycleCountNo);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE PERPETUALHEADER set processedStatusId = 10, orderProcessedOn = :date \r\n"
//            + " WHERE CycleCountNo = :cycleCountNo ", nativeQuery = true)
//    void updateProcessStatusId(@Param(value = "cycleCountNo") String cycleCountNo,
//                               @Param(value = "date") Date date);

    @Query(value = "select * \n" +
            "from PERPETUALHEADER where Perpetualheaderid = :perpetualHeaderId ",nativeQuery = true)
    public PerpetualHeader getPerpetualHeader(@Param(value = "perpetualHeaderId") Long perpetualHeaderId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PERPETUALHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE PerpetualHeaderId = :perpetualHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "perpetualHeaderId") Long perpetualHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId);

    PerpetualHeader findTopByPerpetualHeaderIdAndCompanyCodeAndBranchCodeAndCycleCountNoOrderByOrderReceivedOnDesc(
            Long perpetualHeaderId, String companyCode, String branchCode, String cycleCountNo);

    List<PerpetualHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    @Query(value = "SELECT * FROM PERPETUALHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<PerpetualHeader> findByPerpetual(@Param("statusId") Long statusId,
                                      @Param("branchCode") List<String> branchCode);

    Optional<PerpetualHeader> findByCycleCountNo(String cycleCountNo);
}
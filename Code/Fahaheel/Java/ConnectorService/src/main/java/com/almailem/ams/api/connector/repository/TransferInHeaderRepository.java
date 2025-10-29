package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TransferInHeaderRepository extends JpaRepository<TransferInHeader, String>,
        JpaSpecificationExecutor<TransferInHeader> {


    Optional<TransferInHeader> findByTransferOrderNo(String asnNumber);

    List<TransferInHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE TRANSFERINHEADER set processedStatusId = 10, orderProcessedOn = :date  \r\n"
//            + " WHERE TransferOrderNo = :transferOrderNo ", nativeQuery = true)
//    public void updateProcessStatusId (
//            @Param(value = "transferOrderNo") String transferOrderNo,
//            @Param(value = "date") Date date);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE TRANSFERINHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE TransferInHeaderId = :transferInHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "transferInHeaderId") Long transferInHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId );

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE TRANSFERINHEADER set processedStatusId = 100, orderProcessedOn = getdate()  \r\n"
//            + " WHERE TransferOrderNo = :transferOrderNo ", nativeQuery = true)
//    public void updatefailureProcessStatusId (
//            @Param(value = "transferOrderNo") String transferOrderNo);

    TransferInHeader findTopByTransferOrderNoOrderByOrderReceivedOnDesc(String asnNumber);

    TransferInHeader findTopByTransferInHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNoAndProcessedStatusIdOrderByOrderReceivedOn(
            Long transferInHeaderId, String sourceCompanyCode, String sourceBranchCode, String transferOrderNo , Long processedStatusId);

    @Query(value = "select * \n" +
            "from TRANSFERINHEADER where Transferinheaderid = :transferInHeaderId ",nativeQuery = true)
    public TransferInHeader getTransferInHeader(@Param(value = "transferInHeaderId") Long transferInHeaderId);

    List<TransferInHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);


    @Query(value = "SELECT * FROM TRANSFERINHEADER WHERE Processedstatusid = :statusId and Targetbranchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<TransferInHeader> findByTransferIn(@Param("statusId") Long statusId,
                                              @Param("branchCode") List<String> branchCode);

}

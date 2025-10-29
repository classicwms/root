package com.almailem.ams.api.connector.repository;


import com.almailem.ams.api.connector.model.purchasereturn.PurchaseReturnHeader;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
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
public interface TransferOutHeaderRepository extends JpaRepository<TransferOutHeader, String>,
        JpaSpecificationExecutor<TransferOutHeader> {

    List<TransferOutHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    Optional<TransferOutHeader> findByTransferOrderNumber(String transferOrderNumber);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE TRANSFEROUTHEADER set processedStatusId = 10, orderProcessedOn = :date \r\n"
//            + "WHERE TransferOrdernumber = :transferOrderNumber", nativeQuery = true)
//    public void updateProcessStatusId(@Param(value = "transferOrderNumber") String transferOrderNumber,
//                                      @Param(value = "date") Date date);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE TRANSFEROUTHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE TransferOutHeaderId = :transferOutHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "transferOutHeaderId") Long transferOutHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE TRANSFEROUTHEADER set processedStatusId = 100, orderProcessedOn = getdate() \r\n"
//            + "WHERE TransferOrdernumber = :transferOrderNumber", nativeQuery = true)
//    public void updatefailureProcessStatusId(@Param(value = "transferOrderNumber") String transferOrderNumber);

    TransferOutHeader findTopByTransferOrderNumberOrderByOrderReceivedOnDesc(String transferOrderNumber);

    List<TransferOutHeader> findTopByProcessedStatusIdOrderByOrderReceivedOnDesc(long l);

    TransferOutHeader findTopByTransferOutHeaderIdAndSourceCompanyCodeAndSourceBranchCodeAndTransferOrderNumberOrderByOrderReceivedOnDesc(
            Long transferOutHeaderId, String sourceCompanyCode, String sourceBranchCode, String transferOrderNumber);

    @Query(value = "select * \n" +
            "from TRANSFEROUTHEADER where Transferoutheaderid = :transferOutHeaderId ",nativeQuery = true)
    public TransferOutHeader getTransferOutHeader(@Param(value = "transferOutHeaderId") Long transferOutHeaderId);

    List<TransferOutHeader> findByProcessedStatusIdOrderByOrderReceivedOnDesc(long l);

    @Query(value = "SELECT * FROM TRANSFEROUTHEADER WHERE Processedstatusid = :statusId and Sourcebranchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<TransferOutHeader> findByTransferOut(@Param("statusId") Long statusId,
                                                    @Param("branchCode") List<String> branchCode);
}

package com.almailem.ams.api.connector.repository;


import com.almailem.ams.api.connector.model.salesreturn.SalesReturnHeader;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SalesReturnHeaderRepository extends JpaRepository<SalesReturnHeader, String>, JpaSpecificationExecutor<SalesReturnHeader> {
    SalesReturnHeader findByReturnOrderNo(String returnOrderNo);

    List<SalesReturnHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    SalesReturnHeader findTopBySalesReturnHeaderIdAndCompanyCodeAndBranchCodeAndReturnOrderNoOrderByOrderReceivedOnDesc(
            Long salesReturnHeaderId, String companyCode, String branchCode, String returnOrderNo);

    @Query(value = "select * \n" +
            "from SALESRETURNHEADER where Salesreturnheaderid = :salesReturnHeaderId ",nativeQuery = true)
    public SalesReturnHeader getSalesReturnHeader(@Param(value = "salesReturnHeaderId") Long salesReturnHeaderId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE SALESRETURNHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE SalesReturnHeaderId = :salesReturnHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "salesReturnHeaderId") Long salesReturnHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId );


    List<SalesReturnHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);


    @Query(value = "SELECT * FROM SALESRETURNHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<SalesReturnHeader> findBySalesReturn(@Param("statusId") Long statusId,
                                                @Param("branchCode") List<String> branchCode);
}

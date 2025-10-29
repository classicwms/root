package com.almailem.ams.api.connector.repository;

import java.util.List;
import java.util.Optional;

import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;

@Repository
@Transactional
public interface StockReceiptHeaderRepository extends JpaRepository<StockReceiptHeader, String>,
        JpaSpecificationExecutor<StockReceiptHeader> {

    Optional<StockReceiptHeader> findByReceiptNo(String asnNumber);

    List<StockReceiptHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
    
    List<StockReceiptHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    StockReceiptHeader findTopByStockReceiptHeaderIdAndCompanyCodeAndBranchCodeAndReceiptNoOrderByOrderReceivedOnDesc(
            Long stockReceiptHeaderId, String companyCode, String branchCode, String receiptNumber);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE STOCKRECEIPTHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE StockReceiptHeaderId = :stockReceiptHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "stockReceiptHeaderId") Long stockReceiptHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId );

    @Query(value = "select * \n" +
            "from STOCKRECEIPTHEADER where Stockreceiptheaderid = :stockReceiptHeaderId ",nativeQuery = true)
    public StockReceiptHeader getStockReceiptHeader(@Param(value = "stockReceiptHeaderId") Long stockReceiptHeaderId);

    @Query(value = "SELECT * FROM STOCKRECEIPTHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<StockReceiptHeader> findByStockReceipt(@Param("statusId") Long statusId,
                                                      @Param("branchCode") List<String> branchCode);
}

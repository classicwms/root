package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.salesinvoice.SalesInvoice;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
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
public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, String>, JpaSpecificationExecutor<SalesInvoice> {

    List<SalesInvoice> findTopByProcessedStatusIdOrderByOrderReceivedOn(Long l);

    SalesInvoice findBySalesInvoiceNumber(String asnNumber);

    SalesInvoice findTopBySalesInvoiceNumberOrderByOrderReceivedOnDesc(String asnNumber);

//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE SALESINVOICE set processedStatusId = 10, orderProcessedOn = :date  \r\n"
//            + " WHERE salesInvoiceNumber = :salesInvoiceNumber ", nativeQuery = true)
//    public void updateProcessStatusId (
//            @Param(value = "salesInvoiceNumber") String salesInvoiceNumber,
//            @Param(value = "date") Date date);

    @Query(value = "select * \n" +
            "from SALESINVOICE where Salesinvoiceid = :salesInvoiceId ",nativeQuery = true)
    public SalesInvoice getSalesInvoice(@Param(value = "salesInvoiceId") Long salesInvoiceId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE SALESINVOICE set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE Salesinvoiceid = :salesInvoiceId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "salesInvoiceId") Long salesInvoiceId,
            @Param(value = "processedStatusId") Long processedStatusId);

    SalesInvoice findTopBySalesInvoiceIdAndCompanyCodeAndBranchCodeAndSalesInvoiceNumberOrderByOrderReceivedOnDesc(
            Long salesInvoiceId, String companyCode, String branchCode, String salesInvoiceNumber);

    List<SalesInvoice> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    @Query(value = "SELECT * FROM SALESINVOICE WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<SalesInvoice> findBySalesInvoice(@Param("statusId") Long statusId,
                                            @Param("branchCode") List<String> branchCode);
}

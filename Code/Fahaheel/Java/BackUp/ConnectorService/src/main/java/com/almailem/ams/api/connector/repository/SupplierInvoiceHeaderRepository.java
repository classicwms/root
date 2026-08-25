package com.almailem.ams.api.connector.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almailem.ams.api.connector.model.supplierinvoice.SupplierInvoiceHeader;

@Repository
@Transactional
public interface SupplierInvoiceHeaderRepository extends JpaRepository<SupplierInvoiceHeader, String>, JpaSpecificationExecutor<SupplierInvoiceHeader> {

    List<SupplierInvoiceHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
    List<SupplierInvoiceHeader> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    Optional<SupplierInvoiceHeader> findBySupplierInvoiceNo(String asnNumber);

    SupplierInvoiceHeader findTopBySupplierInvoiceHeaderIdAndCompanyCodeAndBranchCodeAndSupplierInvoiceNoOrderByOrderReceivedOnDesc(
            Long supplierInvoiceHeaderId, String companyCode, String branchCode, String supplierInvoiceNo);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE SUPPLIERINVOICEHEADER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE SupplierInvoiceHeaderId = :supplierInvoiceHeaderId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "supplierInvoiceHeaderId") Long supplierInvoiceHeaderId,
            @Param(value = "processedStatusId") Long processedStatusId );

    @Query(value = "select * \n" +
            "from SUPPLIERINVOICEHEADER where Supplierinvoiceheaderid = :supplierInvoiceHeaderId ",nativeQuery = true)
    public SupplierInvoiceHeader getSupplierInvoiceHeader(@Param(value = "supplierInvoiceHeaderId") Long supplierInvoiceHeaderId);

    @Query(value = "SELECT * FROM SUPPLIERINVOICEHEADER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<SupplierInvoiceHeader> findBySupplierInvoice(@Param("statusId") Long statusId,
                                                      @Param("branchCode") List<String> branchCode);
}

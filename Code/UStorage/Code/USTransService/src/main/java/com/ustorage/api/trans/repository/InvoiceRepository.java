package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.invoice.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>,
		JpaSpecificationExecutor<Invoice> {

	public List<Invoice> findAll();

	public Optional<Invoice> findByInvoiceNumberAndDeletionIndicator(String invoiceId, long l);

	@Query(value = "SELECT tblleadcustomer.TYPE \r\n"
			+ "FROM tblleadcustomer \r\n"
			+ "JOIN tblinvoice ON tblinvoice.CUSTOMER_ID=tblleadcustomer.CUSTOMER_CODE and \r\n"
			+ "(COALESCE(:invoiceNumber,null) IS NULL OR (tblinvoice.INVOICE_NUMBER IN (:invoiceNumber))) and \r\n"
			+ "tblleadcustomer.IS_DELETED=0 AND tblinvoice.IS_DELETED=0", nativeQuery = true)
	public String getCustomerType(@Param("invoiceNumber") String invoiceNumber);
}
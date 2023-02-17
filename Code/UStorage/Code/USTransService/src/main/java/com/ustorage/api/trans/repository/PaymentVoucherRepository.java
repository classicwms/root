package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.paymentvoucher.PaymentVoucher;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long>,
		JpaSpecificationExecutor<PaymentVoucher> {

	public List<PaymentVoucher> findAll();

	public Optional<PaymentVoucher> findByVoucherIdAndDeletionIndicator(String paymentVoucherId, long l);

	@Query(value = "select code_id \n" +
			"from tblstorageunit tsu \n" +
			" where \n" +
			"(COALESCE(:storeNumber,null) IS NULL OR (tsu.ITEM_CODE IN (:storeNumber))) and\n"+
			"tsu.is_deleted=0 ",nativeQuery = true)
	public String getStoreNumber(@Param("storeNumber") String storeNumber);
}
package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.paymentvoucher.PaymentVoucher;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long>,
		JpaSpecificationExecutor<PaymentVoucher> {

	public List<PaymentVoucher> findAll();

	public Optional<PaymentVoucher> findByVoucherIdAndDeletionIndicator(String paymentVoucherId, long l);
}
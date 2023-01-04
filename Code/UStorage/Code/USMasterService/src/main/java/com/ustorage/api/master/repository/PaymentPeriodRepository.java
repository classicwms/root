package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.paymentperiod.PaymentPeriod;

@Repository
public interface PaymentPeriodRepository extends JpaRepository<PaymentPeriod, Long>{

	public List<PaymentPeriod> findAll();

	public Optional<PaymentPeriod> findByCodeAndDeletionIndicator(String paymentPeriodId, long l);
}
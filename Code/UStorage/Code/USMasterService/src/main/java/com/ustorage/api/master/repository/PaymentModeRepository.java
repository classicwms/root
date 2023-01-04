package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.paymentmode.PaymentMode;

@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode, Long>{

	public List<PaymentMode> findAll();

	public Optional<PaymentMode> findByCodeAndDeletionIndicator(String paymentModeId, long l);
}
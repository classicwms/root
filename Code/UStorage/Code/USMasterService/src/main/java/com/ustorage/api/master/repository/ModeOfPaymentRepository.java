package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.modeofpayment.ModeOfPayment;

@Repository
public interface ModeOfPaymentRepository extends JpaRepository<ModeOfPayment, Long>{

	public List<ModeOfPayment> findAll();

	public Optional<ModeOfPayment> findByCodeAndDeletionIndicator(String modeOfPaymentId, long l);
}
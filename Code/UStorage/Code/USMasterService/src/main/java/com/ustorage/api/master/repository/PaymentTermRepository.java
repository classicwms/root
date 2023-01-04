package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.paymentterm.PaymentTerm;

@Repository
public interface PaymentTermRepository extends JpaRepository<PaymentTerm, Long>{

	public List<PaymentTerm> findAll();

	public Optional<PaymentTerm> findByCodeAndDeletionIndicator(String paymentTermId, long l);
}
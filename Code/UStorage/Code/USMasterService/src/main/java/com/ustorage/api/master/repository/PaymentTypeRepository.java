package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.paymenttype.PaymentType;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long>{

	public List<PaymentType> findAll();

	public Optional<PaymentType> findByCodeAndDeletionIndicator(String paymentTypeId, long l);
}
package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.invoicecurrency.InvoiceCurrency;

@Repository
public interface InvoiceCurrencyRepository extends JpaRepository<InvoiceCurrency, Long>{

	public List<InvoiceCurrency> findAll();

	public Optional<InvoiceCurrency> findByCodeAndDeletionIndicator(String invoiceCurrencyId, long l);
}
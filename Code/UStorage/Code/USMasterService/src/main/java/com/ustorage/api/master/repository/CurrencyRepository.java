package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.currency.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>{

	public List<Currency> findAll();

	public Optional<Currency> findByCodeAndDeletionIndicator(String currencyId, long l);
}
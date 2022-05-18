package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.currency.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>{

	public List<Currency> findAll();
	Optional<Currency> findByCurrencyId (Long currencyId);
}
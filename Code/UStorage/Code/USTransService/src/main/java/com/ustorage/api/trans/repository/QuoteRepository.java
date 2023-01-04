package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.quote.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long>,
		JpaSpecificationExecutor<Quote> {

	public List<Quote> findAll();

	public Optional<Quote> findByQuoteIdAndDeletionIndicator(String quoteId, long l);
}
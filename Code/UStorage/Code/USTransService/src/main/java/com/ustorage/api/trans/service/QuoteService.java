package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.ustorage.api.trans.model.quote.Quote;
import com.ustorage.api.trans.model.quote.FindQuote;
import com.ustorage.api.trans.repository.Specification.QuoteSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.quote.AddQuote;
import com.ustorage.api.trans.model.quote.Quote;
import com.ustorage.api.trans.model.quote.UpdateQuote;
import com.ustorage.api.trans.repository.QuoteRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuoteService {
	
	@Autowired
	private QuoteRepository quoteRepository;
	
	public List<Quote> getQuote () {
		List<Quote> quoteList =  quoteRepository.findAll();
		quoteList = quoteList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return quoteList;
	}
	
	/**
	 * getQuote
	 * @param quoteId
	 * @return
	 */
	public Quote getQuote (String quoteId) {
		Optional<Quote> quote = quoteRepository.findByQuoteIdAndDeletionIndicator(quoteId, 0L);
		if (quote.isEmpty()) {
			return null;
		}
		return quote.get();
	}
	
	/**
	 * createQuote
	 * @param newQuote
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Quote createQuote (AddQuote newQuote, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Quote dbQuote = new Quote();
		BeanUtils.copyProperties(newQuote, dbQuote, CommonUtils.getNullPropertyNames(newQuote));
		dbQuote.setDeletionIndicator(0L);
		dbQuote.setCreatedBy(loginUserId);
		dbQuote.setUpdatedBy(loginUserId);
		dbQuote.setCreatedOn(new Date());
		dbQuote.setUpdatedOn(new Date());
		return quoteRepository.save(dbQuote);
	}
	
	/**
	 * updateQuote
	 * @param quoteId
	 * @param loginUserId 
	 * @param updateQuote
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Quote updateQuote (String quoteId, String loginUserId, UpdateQuote updateQuote)
			throws IllegalAccessException, InvocationTargetException {
		Quote dbQuote = getQuote(quoteId);
		BeanUtils.copyProperties(updateQuote, dbQuote, CommonUtils.getNullPropertyNames(updateQuote));
		dbQuote.setUpdatedBy(loginUserId);
		dbQuote.setUpdatedOn(new Date());
		return quoteRepository.save(dbQuote);
	}
	
	/**
	 * deleteQuote
	 * @param loginUserID 
	 * @param quoteModuleId
	 */
	public void deleteQuote (String quoteModuleId, String loginUserID) {
		Quote quote = getQuote(quoteModuleId);
		if (quote != null) {
			quote.setDeletionIndicator(1L);
			quote.setUpdatedBy(loginUserID);
			quote.setUpdatedOn(new Date());
			quoteRepository.save(quote);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + quoteModuleId);
		}
	}

	//find
	public List<Quote> findQuote(FindQuote findQuote) throws ParseException {

		QuoteSpecification spec = new QuoteSpecification(findQuote);
		List<Quote> results = quoteRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
	
}

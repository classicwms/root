package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.SearchOutboundReversal;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.enterprise.transaction.repository.OutboundReversalRepository;
import com.tekclover.wms.api.enterprise.transaction.repository.specification.OutboundReversalSpecification;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import com.tekclover.wms.api.enterprise.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OutboundReversalService {
	
	@Autowired
	private OutboundReversalRepository outboundReversalRepository;
	
	/**
	 * getOutboundReversals
	 * @return
	 */
	public List<OutboundReversal> getOutboundReversals () {
		List<OutboundReversal> outboundReversalList =  outboundReversalRepository.findAll();
		outboundReversalList = outboundReversalList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return outboundReversalList;
	}
	
	/**
	 * getOutboundReversal
	 * @return
	 */
	public OutboundReversal getOutboundReversal (String outboundReversalNo) {
		OutboundReversal outboundReversal = outboundReversalRepository.findByOutboundReversalNo(outboundReversalNo).orElse(null);
		if (outboundReversal != null && outboundReversal.getDeletionIndicator() == 0) {
			return outboundReversal;
		} else {
			throw new BadRequestException("The given OutboundReversal ID : " + outboundReversalNo + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchOutboundReversal
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<OutboundReversal> findOutboundReversal(SearchOutboundReversal searchOutboundReversal)
			throws ParseException, java.text.ParseException {
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getStartReversedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn());
			searchOutboundReversal.setStartReversedOn(dates[0]);
			searchOutboundReversal.setEndReversedOn(dates[1]);
		}		
		OutboundReversalSpecification spec = new OutboundReversalSpecification(searchOutboundReversal);
		List<OutboundReversal> results = outboundReversalRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}

	//Stream
	public Stream<OutboundReversal> findOutboundReversalNew(SearchOutboundReversal searchOutboundReversal)
			throws ParseException, java.text.ParseException {
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getStartReversedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn());
			searchOutboundReversal.setStartReversedOn(dates[0]);
			searchOutboundReversal.setEndReversedOn(dates[1]);
		}
		OutboundReversalSpecification spec = new OutboundReversalSpecification(searchOutboundReversal);
		Stream<OutboundReversal> results = outboundReversalRepository.stream(spec, OutboundReversal.class);

		return results;
	}
	
	/**
	 * createOutboundReversal
	 * @param newOutboundReversal
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundReversal createOutboundReversal (AddOutboundReversal newOutboundReversal,
													String loginUserID) throws IllegalAccessException, InvocationTargetException {
		OutboundReversal dbOutboundReversal = new OutboundReversal();
		log.info("newOutboundReversal : " + newOutboundReversal);
		BeanUtils.copyProperties(newOutboundReversal, dbOutboundReversal, CommonUtils.getNullPropertyNames(newOutboundReversal));
		
		// OB_REV_BY
		dbOutboundReversal.setReversedBy(loginUserID);
		dbOutboundReversal.setReversedOn(new Date());
		dbOutboundReversal.setDeletionIndicator(0L);
		return outboundReversalRepository.save(dbOutboundReversal);
	}
}
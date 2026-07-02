package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.SearchOutboundReversalV2;
import com.tekclover.wms.api.transaction.repository.OutboundReversalV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.OutboundReversalV2Specification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.repository.OutboundReversalRepository;
import com.tekclover.wms.api.transaction.repository.specification.OutboundReversalSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundReversalService {

	@Autowired
	private OutboundReversalRepository outboundReversalRepository;
	//------------------------------------------------------------------------------------------------------
	@Autowired
	private OutboundReversalV2Repository outboundReversalV2Repository;
	@Autowired
	private StagingLineV2Repository stagingLineV2Repository;
	String statusDescription = null;
	//------------------------------------------------------------------------------------------------------
	
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
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn());
			searchOutboundReversal.setStartReversedOn(dates[0]);
			searchOutboundReversal.setEndReversedOn(dates[1]);
		}		
		OutboundReversalSpecification spec = new OutboundReversalSpecification(searchOutboundReversal);
		List<OutboundReversal> results = outboundReversalRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

	//Stream
	public Stream<OutboundReversal> findOutboundReversalNew(SearchOutboundReversal searchOutboundReversal)
			throws ParseException, java.text.ParseException {
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
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

//==========================================================================V2==============================================================================

	/**
	 * getOutboundReversals
	 * @return
	 */
	public List<OutboundReversalV2> getOutboundReversalsV2 () {
		List<OutboundReversalV2> outboundReversalList =  outboundReversalV2Repository.findAll();
		outboundReversalList = outboundReversalList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return outboundReversalList;
	}

	/**
	 * getOutboundReversal
	 * @return
	 */
	public OutboundReversalV2 getOutboundReversalV2 (String outboundReversalNo) {
		OutboundReversalV2 outboundReversal = outboundReversalV2Repository.findByOutboundReversalNo(outboundReversalNo);
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
	public List<OutboundReversalV2> findOutboundReversalV2(SearchOutboundReversalV2 searchOutboundReversal)
			throws ParseException, java.text.ParseException {
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn());
			searchOutboundReversal.setStartReversedOn(dates[0]);
			searchOutboundReversal.setEndReversedOn(dates[1]);
		}
		OutboundReversalV2Specification spec = new OutboundReversalV2Specification(searchOutboundReversal);
		List<OutboundReversalV2> results = outboundReversalV2Repository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

	//Stream
	public Stream<OutboundReversalV2> findOutboundReversalNewV2(SearchOutboundReversalV2 searchOutboundReversal)
			throws ParseException, java.text.ParseException {
		if (searchOutboundReversal.getStartReversedOn() != null && searchOutboundReversal.getEndReversedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundReversal.getStartReversedOn(), searchOutboundReversal.getEndReversedOn());
			searchOutboundReversal.setStartReversedOn(dates[0]);
			searchOutboundReversal.setEndReversedOn(dates[1]);
		}
		OutboundReversalV2Specification spec = new OutboundReversalV2Specification(searchOutboundReversal);
		Stream<OutboundReversalV2> results = outboundReversalV2Repository.stream(spec, OutboundReversalV2.class);

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
	public OutboundReversalV2 createOutboundReversalV2 (OutboundReversalV2 newOutboundReversal,
													String loginUserID) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
		OutboundReversalV2 dbOutboundReversal = new OutboundReversalV2();
		log.info("newOutboundReversal : " + newOutboundReversal);
		BeanUtils.copyProperties(newOutboundReversal, dbOutboundReversal, CommonUtils.getNullPropertyNames(newOutboundReversal));

		// OB_REV_BY
		dbOutboundReversal.setReversedBy(loginUserID);
		dbOutboundReversal.setReversedOn(new Date());
		dbOutboundReversal.setDeletionIndicator(0L);
		return outboundReversalV2Repository.save(dbOutboundReversal);
	}
}

package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.trip.*;

import com.ustorage.api.trans.repository.TripRepository;
import com.ustorage.api.trans.repository.Specification.TripSpecification;
import com.ustorage.api.trans.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TripService {
	
	@Autowired
	private TripRepository tripRepository;
	
	public List<Trip> getTrip () {
		List<Trip> tripList =  tripRepository.findAll();
		tripList = tripList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return tripList;
	}
	
	/**
	 * getTrip
	 * @param tripId
	 * @return
	 */
	public Trip getTrip (String tripId) {
		Optional<Trip> trip = tripRepository.findByItemCodeAndDeletionIndicator(tripId, 0L);
		if (trip.isEmpty()) {
			return null;
		}
		return trip.get();
	}
	
	/**
	 * createTrip
	 * @param newTrip
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Trip createTrip (AddTrip newTrip, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Trip dbTrip = new Trip();
		BeanUtils.copyProperties(newTrip, dbTrip, CommonUtils.getNullPropertyNames(newTrip));
		dbTrip.setDeletionIndicator(0L);
		dbTrip.setCreatedBy(loginUserId);
		dbTrip.setUpdatedBy(loginUserId);
		dbTrip.setCreatedOn(new Date());
		dbTrip.setUpdatedOn(new Date());
		return tripRepository.save(dbTrip);
	}
	
	/**
	 * updateTrip
	 * @param itemCode
	 * @param loginUserId 
	 * @param updateTrip
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Trip updateTrip (String itemCode, String loginUserId, UpdateTrip updateTrip)
			throws IllegalAccessException, InvocationTargetException {
		Trip dbTrip = getTrip(itemCode);
		BeanUtils.copyProperties(updateTrip, dbTrip, CommonUtils.getNullPropertyNames(updateTrip));
		dbTrip.setUpdatedBy(loginUserId);
		dbTrip.setUpdatedOn(new Date());
		return tripRepository.save(dbTrip);
	}
	
	/**
	 * deleteTrip
	 * @param loginUserID 
	 * @param tripModuleId
	 */
	public void deleteTrip (String tripModuleId, String loginUserID) {
		Trip trip = getTrip(tripModuleId);
		if (trip != null) {
			trip.setDeletionIndicator(1L);
			trip.setUpdatedBy(loginUserID);
			trip.setUpdatedOn(new Date());
			tripRepository.save(trip);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + tripModuleId);
		}
	}

	//Find Trip

	public List<Trip> findTrip(FindTrip findTrip) throws ParseException {

		TripSpecification spec = new TripSpecification(findTrip);
		List<Trip> results = tripRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		return results;
	}
}

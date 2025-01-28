package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.email.*;

import com.tekclover.wms.api.idmaster.repository.EMailDetailsRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EMailDetailsService {
	
	@Autowired
	private EMailDetailsRepository eMailDetailsRepository;

	//Create Mail
	public EMailDetails createEMailDetails(AddEMailDetails newEMailDetails) {
		EMailDetails dbEMailDetails = new EMailDetails();
		BeanUtils.copyProperties(newEMailDetails, dbEMailDetails, CommonUtils.getNullPropertyNames(newEMailDetails));
		dbEMailDetails.setDeletionIndicator(0L);
		return eMailDetailsRepository.save(dbEMailDetails);

	}
	//Update Mail
	public EMailDetails updateEMailDetails(Long id,AddEMailDetails updateEMailDetails) {
		EMailDetails dbEMailDetails = getEMailDetails(id);
		BeanUtils.copyProperties(updateEMailDetails, dbEMailDetails, CommonUtils.getNullPropertyNames(updateEMailDetails));

		return eMailDetailsRepository.save(dbEMailDetails);
	}
	//get all
	public List<EMailDetails> getEMailDetailsList() {
		List<EMailDetails> EMailDetailsList = eMailDetailsRepository.findAll();
		EMailDetailsList = EMailDetailsList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return EMailDetailsList;
	}

	//get all
	public List<EMailDetails> getDailyReportEMailDetailsList() {
		List<EMailDetails> EMailDetailsList = eMailDetailsRepository.findAll();
		EMailDetailsList = EMailDetailsList.stream().filter(n -> n.getDeletionIndicator() == 0).filter(n -> n.getGroupBy().equalsIgnoreCase("1")).collect(Collectors.toList());
		return EMailDetailsList;
	}

	//get all
	public List<EMailDetails> getReportEMailDetailsList() {
		List<EMailDetails> EMailDetailsList = eMailDetailsRepository.findAll();
		EMailDetailsList = EMailDetailsList.stream().filter(n -> n.getDeletionIndicator() == 0).filter(n -> n.getGroupBy().equalsIgnoreCase("2")).collect(Collectors.toList());
		return EMailDetailsList;
	}

	//get Mail
	public EMailDetails getEMailDetails(Long id) {

		Optional<EMailDetails> dbEMailDetails = eMailDetailsRepository.findByIdAndDeletionIndicator(id,0L);
		if(dbEMailDetails!=null){
			return dbEMailDetails.get();
		}else{
			throw new BadRequestException("The given ID doesn't exist : " + id);
		}
	}
	//Delete Mail
	public void deleteEMailDetails(Long id) {
		EMailDetails dbEMailDetails = getEMailDetails(id);
		if(dbEMailDetails!=null){
			dbEMailDetails.setDeletionIndicator(1L);
			eMailDetailsRepository.save(dbEMailDetails);
		}else{
			throw new BadRequestException("The given ID doesn't exist : " + id);
		}
	}

	public EMailDetails undeleteEMailDetails(Long id) {
		Optional<EMailDetails> dbEMailDetails = eMailDetailsRepository.findById(id);
		if(dbEMailDetails!=null){
			dbEMailDetails.get().setDeletionIndicator(0L);
			eMailDetailsRepository.save(dbEMailDetails.get());
			return dbEMailDetails.get();
		}else{
			throw new BadRequestException("The given ID doesn't exist : " + id);
		}
	}
}

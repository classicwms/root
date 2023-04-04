package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.email.FileNameForEmail;
import com.tekclover.wms.api.idmaster.model.email.FindFileNameForEmail;
import com.tekclover.wms.api.idmaster.repository.FileNameForEmailRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.FileNameForEmailSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileNameForEmailService {

	@Autowired
	private FileNameForEmailRepository fileNameForEmailRepository;

	//Create - Update Mail
	public FileNameForEmail updateFileNameForEmail( FileNameForEmail updateFileNameForEmail) throws ParseException {
		FileNameForEmail fileNameForEmail = new FileNameForEmail();
		String reportDate = updateFileNameForEmail.getReportDate();
		FindFileNameForEmail findFileNameForEmail = new FindFileNameForEmail();
		findFileNameForEmail.setReportDate(updateFileNameForEmail.getReportDate());
		Optional<FileNameForEmail> newFileNameForEmail = fileNameForEmailRepository.findByReportDate(reportDate);
		if(!newFileNameForEmail.isEmpty()){
			Long fileNameId = newFileNameForEmail.get().getFileNameId();
			FileNameForEmail dbFileNameForEmail = getFileNameForEmail(fileNameId);

			if(dbFileNameForEmail.getDelivery110()!= updateFileNameForEmail.getDelivery110()&&
					updateFileNameForEmail.getDelivery110()!=null){
				dbFileNameForEmail.setDelivery110(updateFileNameForEmail.getDelivery110());
				dbFileNameForEmail.setDeletionIndicator(0L);
			}
			if(dbFileNameForEmail.getDelivery111()!= updateFileNameForEmail.getDelivery111()&
					updateFileNameForEmail.getDelivery111()!=null){
				dbFileNameForEmail.setDelivery111(updateFileNameForEmail.getDelivery111());
				dbFileNameForEmail.setDeletionIndicator(0L);
			}
			if(dbFileNameForEmail.getDispatch110()!= updateFileNameForEmail.getDispatch110()&
					updateFileNameForEmail.getDispatch110()!=null){
				dbFileNameForEmail.setDispatch110(updateFileNameForEmail.getDispatch110());
				dbFileNameForEmail.setDeletionIndicator(0L);
			}
			if(dbFileNameForEmail.getDispatch111()!= updateFileNameForEmail.getDispatch111()&
					updateFileNameForEmail.getDispatch111()!=null){
				dbFileNameForEmail.setDispatch111(updateFileNameForEmail.getDispatch111());
				dbFileNameForEmail.setDeletionIndicator(0L);
			}
			fileNameForEmail = fileNameForEmailRepository.save(dbFileNameForEmail);
		}else{
			FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
			BeanUtils.copyProperties(updateFileNameForEmail, dbFileNameForEmail, CommonUtils.getNullPropertyNames(updateFileNameForEmail));
			fileNameForEmail =fileNameForEmailRepository.save(dbFileNameForEmail);
		}
		return fileNameForEmail;
	}

	//get all
	public List<FileNameForEmail> getFileNameForEmailList() {
		List<FileNameForEmail> FileNameForEmailList = fileNameForEmailRepository.findAll();
		FileNameForEmailList = FileNameForEmailList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return FileNameForEmailList;
	}

	//get Mail
	public FileNameForEmail getFileNameForEmail(Long fileNameId) {

		Optional<FileNameForEmail> dbFileNameForEmail = fileNameForEmailRepository.findByFileNameId(fileNameId);
		if(dbFileNameForEmail!=null){
			return dbFileNameForEmail.get();
		}else{
			throw new BadRequestException("The given ID doesn't exist : " + fileNameId);
		}
	}

	//get Mail
	public FileNameForEmail getFileNameForEmailByDate(String fileNameDate) {

		Optional<FileNameForEmail> dbFileNameForEmail = fileNameForEmailRepository.findByReportDate(fileNameDate);
		if(dbFileNameForEmail!=null){
			return dbFileNameForEmail.get();
		}else{
			throw new BadRequestException("The record for given date doesn't exist : " + fileNameDate);
		}
	}

	//Delete Mail
	public void deleteFileNameForEmail(Long fileNameId) {
		FileNameForEmail dbFileNameForEmail = getFileNameForEmail(fileNameId);
		if(dbFileNameForEmail!=null){
			fileNameForEmailRepository.delete(dbFileNameForEmail);
//			dbFileNameForEmail.setDeletionIndicator(1L);
//			fileNameForEmailRepository.save(dbFileNameForEmail);
		}else{
			throw new BadRequestException("The given ID doesn't exist : " + fileNameId);
		}
	}
	//find
//	public FileNameForEmail findFileNameForEmail(FindFileNameForEmail findFileNameForEmail) throws ParseException {
//		FileNameForEmailSpecification spec = new FileNameForEmailSpecification(findFileNameForEmail);
//		FileNameForEmail results = fileNameForEmailRepository.find(spec);
//		return results;
//	}
}

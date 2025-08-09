package com.tekclover.wms.core.batch.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobListener implements JobExecutionListener {
	private JobExecution activeJob;

	@Autowired
	private JobOperator jobOperator;
	
	@Autowired
	PropertiesConfig propertiesConfig;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		final String jobName = jobExecution.getJobInstance().getJobName();
		final BatchStatus batchStatus = jobExecution.getStatus();

		log.info("EmployeeJobListener beforeJob with job {} and status {}", jobName, batchStatus.isRunning());

		synchronized (jobExecution) {
			if (activeJob != null && activeJob.isRunning()) {
				log.info("EmployeeJobListener beforeJob isRunning with job {} and status {}", jobName,
						batchStatus.isRunning());
				try {
					jobOperator.stop(jobExecution.getId());
				} catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
					e.printStackTrace();
				}
			} else {
				activeJob = jobExecution;
			}
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		final String jobName = jobExecution.getJobInstance().getJobName();
		log.info("jobName : " + jobName);
		
		final BatchStatus jobExecutionStatus = jobExecution.getStatus();
		synchronized (jobExecution) {
			if (jobExecution == activeJob) {
				activeJob = null;
			}
			
			log.info("jobName@@@ : " + jobExecutionStatus.getBatchStatus().name());
			if (jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("FAILED")) {
				log.error("jobName@@@ : " + jobExecutionStatus.getBatchStatus().name());
			}
			
			// jobBomHeader
			if (jobName.equalsIgnoreCase("jobBomHeader") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "bomheader" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/bomheader.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobBomLine
			if (jobName.equalsIgnoreCase("jobBomLine") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "bomLine" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/bomLine.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobBinLocation
			if (jobName.equalsIgnoreCase("jobBinLocation") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "binLocation" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/binLocation.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobBusinessPartner
			if (jobName.equalsIgnoreCase("jobBusinessPartner") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "businessPartner" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/businessPartner.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobHandlingEquipment
			if (jobName.equalsIgnoreCase("jobHandlingEquipment") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "handlingEquipment" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/handlingEquipment.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobInventory
			if (jobName.equalsIgnoreCase("jobInventory") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "inventory" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/inventory.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobImBasicData1
			if (jobName.equalsIgnoreCase("jobImBasicData1") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "imBasicData1" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/imBasicData1.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobImBasicData1WhId111
			if (jobName.equalsIgnoreCase("jobImBasicData1WhId111") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "imBasicData1WhId111" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/imBasicData1WhId111.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobIMPartner
			if (jobName.equalsIgnoreCase("jobIMPartner") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "imPartner" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/imPartner.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// jobIMPartnerWhId111
			if (jobName.equalsIgnoreCase("jobIMPartnerWhId111") && jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "imPartnerWhId111" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/imPartnerWhId111.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

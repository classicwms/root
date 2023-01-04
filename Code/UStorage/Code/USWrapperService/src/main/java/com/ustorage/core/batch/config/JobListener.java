package com.ustorage.core.batch.config;

import com.ustorage.core.config.PropertiesConfig;
import com.ustorage.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

		log.info("JobListener beforeJob with job {} and status {}", jobName, batchStatus.isRunning());

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
			
			// jobAgreement
			if (jobName.equalsIgnoreCase("jobAgreement") &&
					jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				 Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "agreement" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							 	Paths.get(propertiesConfig.getFileUploadDir() + "/agreement.csv"),
						        Paths.get(renamedFile.getAbsolutePath())
					        );
					 log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// jobStorageUnit
			if (jobName.equalsIgnoreCase("jobStorageUnit") &&
					jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "storageunit" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							Paths.get(propertiesConfig.getFileUploadDir() + "/storageunit.csv"),
							Paths.get(renamedFile.getAbsolutePath())
					);
					log.info("temp : " + temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// jobStorageNumber-InsideAgreement
			if (jobName.equalsIgnoreCase("jobStoreNumber") &&
					jobExecutionStatus.getBatchStatus().name().equalsIgnoreCase("COMPLETED")) {
				Path temp;
				try {
					String time = DateUtils.getCurrentTimestamp();
					String fileName = propertiesConfig.getFileMoveToDir() + "storenumber" + "_" + time + ".csv";
					File renamedFile = new File (fileName);
					temp = Files.move (
							Paths.get(propertiesConfig.getFileUploadDir() + "/storenumber.csv"),
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

package com.ustorage.core.batch.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchJobScheduler {
	
	@Autowired
	private Job jobAgreement;

	@Autowired
	private Job jobStorageUnit;

	@Autowired
	private Job jobStoreNumber;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	//-----------------------------------------------------------------------------------
	
	/**
	 * 
	 * @throws Exception
	 */
	public void runJobAgreement() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobAgreement, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void runJobStorageUnit() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobStorageUnit, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void runJobStoreNumber() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobStoreNumber, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
package com.tekclover.wms.core.batch.scheduler;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchJobScheduler {
	
	@Autowired
	private Job jobBomHeader;
	
	@Autowired
	private Job jobBinLocation;
	
	@Autowired
	private Job jobBomLine;
	
	@Autowired
	private Job jobBusinessPartner;
	
	@Autowired
	private Job jobHandlingEquipment;
	
	@Autowired
	private Job jobInventory;
	
	@Autowired
	private Job jobInventoryStock;
	
	@Autowired
	private Job jobImBasicData1;
	
//	@Autowired
//	private Job jobImBasicData1WhId111;
//

	@Autowired
	private Job jobIMPartner;
	
//	@Autowired
//	private Job jobIMPartnerWhId111;
//
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job dbToCsvJob;
	
	@Autowired
	private Job jobInventoryMovement;
	
	@Autowired
	private Job periodicJob;

	@Autowired
	private Job imBasicData1Job;

	@Autowired
	private Job imPartnerJob;

	@Autowired
	private Job binlocationJob;

	@Autowired
	private Job inventoryJob;


	/**
	 * jobBomHeader
	 * @throws Exception 
	 */
	public void runJobBomHeader() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobBomHeader, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void runJobBomLine() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobBomLine, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * jobBinLocation
	 */
	public void runJobBinLocation() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobBinLocation, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jobBinLocation
	 */
	public void runJobBusinessPartner() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobBusinessPartner, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jobHandlingEquipment
	 */
	public void runJobHandlingEquipment() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobHandlingEquipment, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jobInventory
	 */
	public void runJobInventory() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobInventory, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jobInventory
	 */
	public void runJobInventoryStock() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobInventoryStock, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jobImBasicData1
	 */
	public void runJobImBasicData1() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobImBasicData1, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * jobImBasicData1WhId111
//	 */
//	public void runJobImBasicData1WhId111() {
//		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
//				.toJobParameters();
//		try {
//			jobLauncher.run(jobImBasicData1WhId111, params);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * jobImBasicData1WhId111
	 */
	public void runJobIMPartner() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobIMPartner, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * jobImBasicData1WhId111
//	 */
//	public void runJobIMPartnerWhId111() {
//		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
//				.toJobParameters();
//		try {
//			jobLauncher.run(jobIMPartnerWhId111, params);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void runJobdbToCsvJob() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(dbToCsvJob, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void runJobInventoryMovement() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobInventoryMovement, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void runJobPeriodic() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(periodicJob, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * jobImBasicData1
	 */
	public void runJobImBasicData1Patch() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(imBasicData1Job, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * jobImPartner
	 */
	public void runIMPartnerPatchJob() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(imPartnerJob, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * jobBinLocation
	 */
	public void runBinLocationPatchJob() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(binlocationJob, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * jobBinLocation
	 */
	public void runInventoryJob() {
		JobParameters params = new JobParametersBuilder().addLong("jobId", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(inventoryJob, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
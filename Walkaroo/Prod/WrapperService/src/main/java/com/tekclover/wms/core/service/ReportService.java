package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.transaction.ReceiptConfimationReport;
import com.tekclover.wms.core.model.transaction.ShipmentDeliveryReport;
import com.tekclover.wms.core.model.transaction.ShipmentDeliverySummaryReport;
import com.tekclover.wms.core.model.transaction.ShipmentDispatchSummaryReport;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
@Service
public class ReportService {

	@Autowired
	private MastersService mastersService;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	

	/**
	 * 
	 * @param reportFormat
	 * @return
	 */
	public String exportBom (String reportFormat) {
		try {
			AuthToken authTokenForMasterService = authTokenService.getMastersServiceAuthToken();
			
			com.tekclover.wms.core.model.masters.BomHeader[] bomHeader = 
					mastersService.getBomHeaders(authTokenForMasterService.getAccess_token());
			
			int i = 300;
			for (com.tekclover.wms.core.model.masters.BomHeader bom : bomHeader) {
				List<String> lines = new ArrayList<>();
				lines.add("One Apple 123" + (i+=1000));
				lines.add("One Apple 234" + (i+=1000));
				lines.add("One Apple 345" + (i+=1000));
				bom.setLines(lines);
			}
			
			File file = ResourceUtils.getFile("classpath:bom.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(Arrays.asList(bomHeader));
			
			Map<String, Object> params = new HashMap<>();
			params.put ("createdBy", "Muru");
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
			
			if (reportFormat.equalsIgnoreCase("html")) {
				JasperExportManager.exportReportToHtmlFile(jasperPrint, "bom.html");
			}
			
			if (reportFormat.equalsIgnoreCase("pdf")) {
				JasperExportManager.exportReportToPdfFile(jasperPrint, "bom.pdf");
			}
			return reportFormat;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
		}
		return reportFormat;
	}

	/**
	 * ShipmentDelivery
	 * @param shipmentDeliveryList
	 * @param reportFormat
	 * @return
	 */
	public String exportShipmentDelivery(ShipmentDeliveryReport[] shipmentDeliveryList, String reportFormat) {
		try {
			String reportPath = propertiesConfig.getReportPath();
			String customerRef = shipmentDeliveryList[0].getCustomerRef();
			String fileName = "/shipmentDelivery_" + customerRef + "_" + DateUtils.getCurrentTimestamp();
			
			File file = ResourceUtils.getFile("classpath:shipment_delivery.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(Arrays.asList(shipmentDeliveryList));
			
			Map<String, Object> params = new HashMap<>();
			params.put ("createdBy", "IWExpress");
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
			
			if (reportFormat.equalsIgnoreCase("html")) {
				fileName = reportPath + fileName + ".html";
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
			}
			
			if (reportFormat.equalsIgnoreCase("pdf")) {
				fileName = reportPath + fileName + ".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			}
			log.info("Done..........");
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return reportFormat;
	}

	/**
	 * 
	 * @param shipmentDeliverySummaryReport
	 * @param reportFormat
	 * @return
	 */
	public String exportShipmentDeliverySummary(ShipmentDeliverySummaryReport[] shipmentDeliverySummaryReport,
			String reportFormat) {
		try {
			String reportPath = propertiesConfig.getReportPath();
			String fileName = "/shipmentDeliverySummaryReport_" + DateUtils.getCurrentTimestamp();
			File file = ResourceUtils.getFile("classpath:shipment_delivery_summary.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource datasource = 
					new JRBeanCollectionDataSource(Arrays.asList(shipmentDeliverySummaryReport));
			
			Map<String, Object> params = new HashMap<>();
			params.put ("createdBy", "IWExpress");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
			
			if (reportFormat.equalsIgnoreCase("html")) {
				fileName = reportPath + fileName + ".html";
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
			}
			
			if (reportFormat.equalsIgnoreCase("pdf")) {
				fileName = reportPath + fileName + ".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			}
			log.info("Done..........");
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return reportFormat;
	}

	/**
	 * 
	 * @param shipmentDispatchSummary
	 * @param reportFormat
	 * @return
	 */
	public String exportShipmentDispatchSummary(ShipmentDispatchSummaryReport shipmentDispatchSummary, String reportFormat) {
		try {
			String reportPath = propertiesConfig.getReportPath();
			String fileName = "/shipmentDispatchSummaryReport_" + DateUtils.getCurrentTimestamp();
			File file = ResourceUtils.getFile("classpath:shipment_dispatch_summary.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource datasource = 
					new JRBeanCollectionDataSource(Arrays.asList(shipmentDispatchSummary));
			
			Map<String, Object> params = new HashMap<>();
			params.put ("createdBy", "IWExpress");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
			
			if (reportFormat.equalsIgnoreCase("html")) {
				fileName = reportPath + fileName + ".html";
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
			}
			
			if (reportFormat.equalsIgnoreCase("pdf")) {
				fileName = reportPath + fileName + ".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			}
			log.info("Done..........");
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return reportFormat;
	}

	/**
	 * 
	 * @param receiptConfimationReport
	 * @param reportFormat
	 * @return
	 */
	public String exportReceiptConfimationReport(ReceiptConfimationReport receiptConfimationReport, String reportFormat) {
		try {
			String reportPath = propertiesConfig.getReportPath();
			String fileName = "/shipmentDispatchSummaryReport_" + DateUtils.getCurrentTimestamp();
			File file = ResourceUtils.getFile("classpath:receiptConfimation.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource datasource = 
					new JRBeanCollectionDataSource(Arrays.asList(receiptConfimationReport));
			
			Map<String, Object> params = new HashMap<>();
			params.put ("createdBy", "IWExpress");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
			
			if (reportFormat.equalsIgnoreCase("html")) {
				fileName = reportPath + fileName + ".html";
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
			}
			
			if (reportFormat.equalsIgnoreCase("pdf")) {
				fileName = reportPath + fileName + ".pdf";
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			}
			log.info("Done..........");
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		return reportFormat;
	}
}

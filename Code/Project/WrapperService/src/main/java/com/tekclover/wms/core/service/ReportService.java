package com.tekclover.wms.core.service;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.tekclover.wms.core.model.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.repository.MongoTransactionRepository;
import com.tekclover.wms.core.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Service
public class ReportService {

	@Autowired
	MongoTransactionRepository mongoInboundRepository;
	
	@Autowired
	private MastersService mastersService;
	
	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	/**
	 * 
	 * @param warehouseID
	 * @param statusId
	 * @param orderDate
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getOrderDetails (String warehouseID, Long statusId, String orderDate) throws ParseException {
		Date localDate = null;
		List<InboundIntegrationHeader> inboundOrders = null;
		if (orderDate != null) {
			try {
				Date date = DateUtils.convertStringToDate(orderDate);
				localDate = DateUtils.addTimeToDate(date);
				
				inboundOrders = mongoInboundRepository.findAllByWarehouseIDAndProcessedStatusIdAndOrderReceivedOn(
						warehouseID, statusId, localDate);
				log.info("inboundOrders : " + inboundOrders);
			} catch (Exception e) {
				throw new BadRequestException("Date format should be MM-dd-yyyy");
			}
		} else {
			inboundOrders = mongoInboundRepository.findAllByWarehouseIDAndProcessedStatusId(
					warehouseID, statusId);
			log.info("inboundOrders : " + inboundOrders);
		}
			
		long newOrders = inboundOrders.stream().filter(a -> a.getProcessedStatusId() == 0).count();
		long processedOrders = inboundOrders.stream().filter(a -> a.getProcessedStatusId() == 10).count();
		
		Map<String, Object> map = new HashMap <>();
		map.put("newOrders", newOrders);
		map.put("processedOrders", processedOrders);
		map.put("orders", inboundOrders);
		return map;
	}
	
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
		} catch (ParseException e) {
			throw new RuntimeException(e);
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

	//Email PDF Generate
	public void exportEmail(OutputStream response, PreOutboundHeader[] preOutboundHeaders) throws IOException, DocumentException, ParseException {

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response);

		document.open();
		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(15);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(12);

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(105f);
		table.setWidths(new float[] {2.0f, 1.5f, 1.5f, 2.0f, 1.5f, 3.0f});
		table.setSpacingBefore(10);

		Paragraph paragraph = new Paragraph("Shipment Dispatch Summary Report", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		paragraph.setSpacingAfter(3.0f);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(8);

		Paragraph paragraph2 = new Paragraph("Selection Date: 01-01-2025 : 02:00 - 01-04-2025 : 01:59", fontParagraph);
		paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
		Paragraph paragraph3 = new Paragraph("Run Date: 01-08-2025 05:07", fontParagraph);
		paragraph3.setAlignment(Paragraph.ALIGN_RIGHT);

		// Creating an ImageData object
//		String imageFile = "C:/Users/Shadow/Downloads/logo.png";
		String imageFile = propertiesConfig.getLogo();
		Image image = Image.getInstance(imageFile);

		image.scaleToFit(100,1000);

		image.setAbsolutePosition(30,780);
		document.add(image);
		document.add(paragraph2);
		document.add(paragraph3);
		document.add(paragraph);

		writeTableHeader(table);
		writeTableData(table, preOutboundHeaders, document, image);

		document.add(table);

		document.close();
	}

	//PDF Generate
	public void export(HttpServletResponse response, PreOutboundHeader[] preOutboundHeaders, SearchPreOutboundHeader searchPreOutboundHeader) throws IOException, DocumentException, ParseException {

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(15);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(12);

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(105f);
		table.setWidths(new float[] {2.0f, 1.5f, 1.5f, 2.0f, 1.5f, 3.0f});
		table.setSpacingBefore(10);

		Paragraph paragraph = new Paragraph("Shipment Dispatch Summary Report", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		paragraph.setSpacingAfter(3.0f);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(8);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy h:mm a");

		String formattedStartDate = searchPreOutboundHeader.getStartOrderDate() != null ? dateFormatter.format(searchPreOutboundHeader.getStartOrderDate()) : "N/A";
		String formattedEndDate = searchPreOutboundHeader.getEndOrderDate() != null ? dateFormatter.format(searchPreOutboundHeader.getEndOrderDate()) : "N/A";
		String formattedRunDate = searchPreOutboundHeader.getRunDate() != null ? dateFormatter.format(searchPreOutboundHeader.getRunDate()) : "N/A";

		Paragraph paragraph2 = new Paragraph("Selection Date: " + formattedStartDate + " - "  + formattedEndDate , fontParagraph);
		paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
		Paragraph paragraph3 = new Paragraph("Run Date: " + formattedRunDate, fontParagraph);
		paragraph3.setAlignment(Paragraph.ALIGN_RIGHT);

		// Creating an ImageData object
		String imageFile = propertiesConfig.getLogo();
		Image image = Image.getInstance(imageFile);

		image.scaleToFit(100,1000);

		image.setAbsolutePosition(30,780);
		document.add(image);
		document.add(paragraph2);
		document.add(paragraph3);
		document.add(paragraph);

		writeTableHeader(table);
		writeTableData(table, preOutboundHeaders, document, image);

		document.add(table);

		document.close();
	}

	private void writeTableHeader(PdfPTable table) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setPaddingBottom(5);
		cell.setBorderWidthBottom(1);

		// Load Arabic font from resources
//		String fontPath = "src/main/resources/font/NotoNaskhArabic-VariableFont_wght.ttf";
//		BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//		Font arabicFont = new Font(bf, 12, Font.BOLD);
//		arabicFont.setSize(9);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(9);

		cell.setPhrase(new Phrase("Order Received date" , fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("TO Number", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Showroom ", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Order Type", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Required Date", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Order Status", fontSubTitle));
		table.addCell(cell);

	}

	private void writeTableData(PdfPTable table, PreOutboundHeader[] preOutboundHeaders, Document document, Image image) throws ParseException, DocumentException, IOException {

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(8.5f);
		font.setStyle(Font.NORMAL);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

		int rowCount = 0; // Track the number of rows
		int maxRowsPerPage = 30; // Define the maximum number of rows per page


		for (PreOutboundHeader header : preOutboundHeaders) {

			PdfPCell cell = new PdfPCell();
			cell.setBorder(0);
			cell.setPaddingBottom(10);
			cell.setBorderWidthBottom(1);

			String formattedOrderDate = dateFormatter.format(header.getRefDocDate());
			String formattedReceivedDate = dateFormatter.format(header.getRequiredDeliveryDate());

			cell.setPhrase(new Phrase(formattedOrderDate, font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getRefDocNumber(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getWarehouseId(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getReferenceField1(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(formattedReceivedDate, font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getReferenceField10(), font));
			table.addCell(cell);

			rowCount++;

			// Check if we've reached the max rows per page
			if (rowCount >= maxRowsPerPage) {
				document.add(table); // Add the current table to the document

				// Add a new page
				document.newPage();

				// Add the logo and header
				image.setAbsolutePosition(30, 780);
				document.add(image);
				addPageHeader(document);

				// Reset the table for the next page
				table.deleteBodyRows();

				writeTableHeader(table);
				rowCount = 0;
			}
		}

//		// Add remaining rows if any
//		if (rowCount > 0) {
//			document.add(table);
//		}

	}

	private void addPageHeader(Document document) throws DocumentException, IOException {
		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(15);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(8);

		Paragraph paragraph = new Paragraph("Shipment Dispatch Summary Report", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		Paragraph paragraph2 = new Paragraph("Selection Date: 01-01-2025 : 02:00 - 01-04-2025 : 01:59", fontParagraph);
		paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
		Paragraph paragraph3 = new Paragraph("Run Date: 01-08-2025 05:07", fontParagraph);
		paragraph3.setAlignment(Paragraph.ALIGN_RIGHT);

		document.add(paragraph2);
		document.add(paragraph3);
		document.add(paragraph);
	}


	//===========================================PickerDenial Report Pdf ===================================//

	Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

	/**
	 * @param response
	 * @param pickerDenialReport
	 * @throws Exception
	 */
	public void export(HttpServletResponse response, PickerDenialReport pickerDenialReport, SearchPickupLine searchPickupLine) throws Exception {

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		addPageHeaderPD(document, searchPickupLine);
		if (pickerDenialReport.getHeaders() != null && !pickerDenialReport.getHeaders().isEmpty()) {
			writeTableData(pickerDenialReport.getHeaders(), document);
		} else {
			noDataFound(document);
		}

		writeReportSummaryTitle(document, searchPickupLine);
		if (pickerDenialReport.getSummaryList() != null && !pickerDenialReport.getSummaryList().isEmpty()) {
			writeSummaryTableData(pickerDenialReport, document);
		} else {
			noDataFound(document);
		}

		document.close();
	}

	/**
	 * @param headers
	 * @param document
	 * @throws Exception
	 */
	private void writeTableData(List<PickerDenialHeader> headers, Document document) throws Exception {

		int pageLines = 25;
		int maxPageLines = 30;
		int headerLineSize = 0;
		int headerSize = headers.size();

		Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);
		List<PickerDenialHeader> sortedHeaders = headers.stream().sorted(Comparator.comparing(PickerDenialHeader::getPartnerCode)
				.thenComparing(PickerDenialHeader::getRefDocNumber)).collect(Collectors.toList());

		String partnerCodeChange = null;
		for (int j = 0; j < headerSize; j++) {
			PickerDenialHeader header = sortedHeaders.get(j);
			List<PickerDenialReportImpl> lines = header.getLines();

			int i = 0;
			String partnerCode = header.getPartnerCode() + " - " + header.getPartnerName();
			log.info("PartnerCode : " + partnerCode);

			if (partnerCodeChange == null || !partnerCodeChange.equalsIgnoreCase(header.getPartnerCode())) {

				partnerCodeChange = header.getPartnerCode();
				addPartnerCodeHeader(document, partnerCode);
				writeTableHeader(document);

				i = 2;
			}

			reportHeaderData(document, header, font);

			if (lines != null && !lines.isEmpty()) {
				writeTableLineHeader(document);
				reportLines(document, lines, font);
			}

			//addNewPageLogic
			headerLineSize = headerLineSize + 2 + i + lines.size();
//            if (headerLineSize >= pageLines) {
			if (j < headerSize - 1) {
				PickerDenialHeader next = sortedHeaders.get(j + 1);
				int k = 0;
				if (!partnerCodeChange.equalsIgnoreCase(next.getPartnerCode())) {
					k = 2;
				}
				int temp = headerLineSize + 2 + k + next.getLines().size();
				if (temp > maxPageLines) {
					addNewPage(document);
					if(k == 0) {
						writeTableHeader(document);
					}
					headerLineSize = 0;
				}
			}
//            }
		}
	}

	/**
	 * @param document
	 * @param header
	 * @param font
	 */
	private void reportHeaderData(Document document, PickerDenialHeader header, Font font) throws Exception {
		PdfPTable tableHeaderData = new PdfPTable(11); // Adjusted to 8 columns
		tableHeaderData.setWidths(new float[]{2.0f, 3.5f, 1.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
		tableHeaderData.setWidthPercentage(105f);
		tableHeaderData.setSpacingBefore(5f);

//		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // Adjust format if needed
//		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//		LocalDate date = LocalDate.parse(header.getSDeliveryDate(), inputFormatter);
//		String formattedDate = date.format(outputFormatter);

//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//		String formattedDate = dateFormat.format(header.getSDeliveryDate());
//		addHeaderCellWithoutSideBorders(tableHeaderData, formattedDate, font);

		//Print Header Data in tableHeader (Without PreOutboundNo)**
		addHeaderCellWithoutSideBorders(tableHeaderData, header.getSDenialDate(), font);   // Denial Date
		addHeaderCellWithoutSideBorders(tableHeaderData, header.getSDeliveryDate(), font); // Delivery Date/Time
		addHeaderCellWithoutSideBorders(tableHeaderData, header.getPartnerCode(), font);   // Branch Code
		addHeaderCellWithoutSideBorders(tableHeaderData, header.getOrderType(), font);     // Order Type
		addHeaderCellWithoutSideBorders(tableHeaderData, header.getRefDocNumber(), font);     // Order Type
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getSkuOrdered()), font);  // # SKUs Ordered
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getSkuShipped()), font);  // # SKUs Shipped
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getSkuDenied()), font);   // # SKU Denied
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getOrderedQty()), font);  // Ordered Qty
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getShippedQty()), font);  // Shipped Qty
		addHeaderCellWithoutSideBorders(tableHeaderData, String.valueOf(header.getPercentageShipped()), font); // % Shipped

		//Add **Header Table** to Document
		document.add(tableHeaderData);
	}

	/**
	 *
	 * @param document
	 * @throws Exception
	 */
	private void writeTableHeader(Document document) throws Exception {

		PdfPTable tableHeader = new PdfPTable(11);
		tableHeader.setWidthPercentage(105f);
		tableHeader.setWidths(new float[]{2.0f, 3.5f, 1.5f, 1.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
		tableHeader.setSpacingBefore(10);

		font.setSize(9);
		addHeaderCellWithoutSideBorders(tableHeader, "Denial Date", font);
		addHeaderCellWithoutSideBorders(tableHeader, "Delivery Date\nTime", font);
		addHeaderCellWithoutSideBorders(tableHeader, "Branch\nCode", font);
		addHeaderCellWithoutSideBorders(tableHeader, "Order\nType", font);
		addHeaderCellWithoutSideBorders(tableHeader, "# S.O", font);
		addHeaderCellWithoutSideBorders(tableHeader, "# SKUs\nOrdered", font);
		addHeaderCellWithoutSideBorders(tableHeader, "# SKUs\nShipped", font);
		addHeaderCellWithoutSideBorders(tableHeader, "# SKU\nDenied", font);
		addHeaderCellWithoutSideBorders(tableHeader, "Ordered\nQty", font);
		addHeaderCellWithoutSideBorders(tableHeader, "Shipped\nQty", font);
		addHeaderCellWithoutSideBorders(tableHeader, "%\nShipped", font);
		document.add(tableHeader);
	}

	/**
	 * @param document
	 * @param partnerCode
	 * @throws Exception
	 */
	private void addPartnerCodeHeader(Document document, String partnerCode) throws Exception {

		font.setSize(12);

		// Dynamic Subtitle with Partner Code
		Paragraph subtitle = new Paragraph(partnerCode, font);
		subtitle.setAlignment(Paragraph.ALIGN_CENTER);
		subtitle.setSpacingBefore(10);
		subtitle.setSpacingAfter(10);

		// Add to Document
		document.add(subtitle);
	}

	/**
	 * @param document
	 * @param lines
	 * @param lines
	 * @param font
	 */
	private void reportLines(Document document, List<PickerDenialReportImpl> lines, Font font) throws Exception {
		List<PickerDenialReportImpl> sortedList = lines.stream().sorted(Comparator.comparing(PickerDenialReportImpl::getLineNumber)).collect(Collectors.toList());
		// Create Line-Level Table (Adjusted Description Position)
		PdfPTable tableLines = new PdfPTable(7); // Adjusted to 7 columns
		tableLines.setWidths(new float[]{1.0f, 2.0f, 6.0f, 2.5f, 1.5f, 1.5f, 2.5f});
		tableLines.setWidthPercentage(105f);
		tableLines.setSpacingBefore(5f);

		int lineSize = sortedList.size();
		for (PickerDenialReportImpl line : sortedList) {
			addCellWithoutSideBorders(tableLines, getCount(line.getLineNumber()), font);
			addCellWithoutSideBorders(tableLines, line.getItemCode(), font);
			addCellWithoutSideBorders(tableLines, line.getDescription(), font);
			addCellWithoutSideBorders(tableLines, line.getAssignedPickerId(), font);
			addCellWithoutSideBorders(tableLines, getCount(line.getOrderedQty()), font);
			addCellWithoutSideBorders(tableLines, getCount(line.getShippedQty()), font);
			addCellWithoutSideBorders(tableLines, line.getRemarks() != null ? line.getRemarks() : "", font);

			lineSize--;
		}
		document.add(tableLines);
		if (lineSize == 0) {
			addLine(document);
		}
	}

	/**
	 * @param document
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void writeTableLineHeader(Document document) throws Exception {

		PdfPTable table = new PdfPTable(7); // Adjusted to 7 columns
		table.setWidths(new float[]{1.0f, 2.0f, 6.0f, 2.5f, 1.5f, 2.0f, 2.5f});
		table.setWidthPercentage(105f);
		table.setSpacingBefore(5f);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(8);

		addCellWithoutSideBorders(table, "LineNo", fontSubTitle);
		addCellWithoutSideBorders(table, "ItemCode", fontSubTitle);
		addCellWithoutSideBorders(table, "Description", fontSubTitle);
		addCellWithoutSideBorders(table, "PickerID", fontSubTitle);
		addCellWithoutSideBorders(table, "OrderQty", fontSubTitle);
		addCellWithoutSideBorders(table, "ShippedQty", fontSubTitle);
		addCellWithoutSideBorders(table, "Remark", fontSubTitle);

		document.add(table);
	}

	/**
	 * @param document
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void writeReportSummaryTitle(Document document, SearchPickupLine searchPickupLine) throws Exception {

		document.newPage();
		font.setSize(12);
		addPageHeaderPD(document, searchPickupLine);

		// Adding Report Summary Header
		Paragraph reportSummary = new Paragraph("Report Summary", font);
		reportSummary.setAlignment(Paragraph.ALIGN_LEFT);
		reportSummary.setSpacingBefore(10);
		document.add(reportSummary);
	}

	/**
	 * @param report
	 * @param document
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void writeSummaryTableData(PickerDenialReport report, Document document) throws Exception {

		try {

			PdfPTable summaryTableHeader = new PdfPTable(8);
			summaryTableHeader.setWidthPercentage(100f);
			summaryTableHeader.setWidths(new float[]{5.5f, 3.0f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f});
			summaryTableHeader.setSpacingBefore(10);

			Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

			// Ensure the summary list is not empty
			if (report.getSummaryList().isEmpty()) {
				log.info("No summary data found!");
				return;
			}

			log.info("Summary Data Found: " + report.getSummaryList().size());

			// Write table header
			writeSummaryTableHeader(summaryTableHeader);

			List<PickerDenialSummary> sortedList = report.getSummaryList().stream().sorted(Comparator.comparing(PickerDenialSummary::getPartnerCode)).collect(Collectors.toList());

			for (PickerDenialSummary summary : sortedList) {
				summaryDataBind(summaryTableHeader, summary, font);
			}

			// Add summary table to document
			document.add(summaryTableHeader);
		} catch (Exception e) {
			log.error("Exception while creating table summary : " + e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * @param table2
	 * @param summary
	 * @param font
	 */
	private void summaryDataBind(PdfPTable table2, PickerDenialSummary summary, Font font) throws Exception {
		// Extract summary details
		String partnerCode = summary.getPartnerCode();
		String partnerName = summary.getPartnerName();
		String orderType = summary.getOrderType();

		table2.addCell(createBorderedCell(partnerCode + " - " + partnerName, font));    // Location (Partner Name)
		table2.addCell(createBorderedCell(orderType, font));                                    // Type (Order Type)
		table2.addCell(createBorderedCell(getCount(summary.getOutOfStock()), font));
		table2.addCell(createBorderedCell(getCount(summary.getShortQty()), font));
		table2.addCell(createBorderedCell(getCount(summary.getDamage()), font));
		table2.addCell(createBorderedCell(getCount(summary.getAisleBlock()), font));
		table2.addCell(createBorderedCell(getCount(summary.getNonPackQty()), font));
		table2.addCell(createBorderedCell(getCount(summary.getTotalSKU()), font));
	}

	/**
	 * @param table2
	 * @throws Exception
	 */
	private void writeSummaryTableHeader(PdfPTable table2) throws Exception {

		font.setSize(8.5f);
		font.setStyle(Font.BOLD);

		Font font2 = FontFactory.getFont(FontFactory.HELVETICA);
		font2.setSize(8.5f);

		table2.addCell(createBorderedCell("Location", font));
		table2.addCell(createBorderedCell("Type", font));
		table2.addCell(createBorderedCell("# SKU Out \nof Stock", font2));
		table2.addCell(createBorderedCell("# SKU \nShort Qty", font2));
		table2.addCell(createBorderedCell("# SKU \nDamage", font2));
		table2.addCell(createBorderedCell("# SKU Aisle \nBlock", font2));
		table2.addCell(createBorderedCell("# SKU Non - \nPack Qty", font2));
		table2.addCell(createBorderedCell("Total SKU", font2));

	}

	/**
	 * @param document
	 * @throws Exception
	 */
	private void addPageHeaderPD(Document document, SearchPickupLine searchPickupLine) throws Exception {

		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(15);

		Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubtitle.setSize(12);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(10);

		Image image = getLogo();
		image.scaleToFit(100, 1000);
		image.setAbsolutePosition(30, 780);
		document.add(image);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy h:mm a");

		String formattedStartDate = searchPickupLine.getFromPickConfirmedOn() != null ? dateFormatter.format(searchPickupLine.getFromPickConfirmedOn()) : "N/A";
		String formattedEndDate = searchPickupLine.getToPickConfirmedOn() != null ? dateFormatter.format(searchPickupLine.getToPickConfirmedOn()) : "N/A";

		// Selection Date
//		String formattedStartDate = getFormatDate(getStartDateTime());
//		String formattedEndDate = getFormatDate(getEndDateTime());

		Paragraph dateInfo = new Paragraph("Selection Date: \n" + formattedStartDate + " - " + formattedEndDate, fontParagraph);
		dateInfo.setAlignment(Paragraph.ALIGN_RIGHT);
		document.add(dateInfo);

		// Report Title
		Paragraph denialReport = new Paragraph("Denial Report", fontTitle);
		denialReport.setAlignment(Paragraph.ALIGN_CENTER);
		denialReport.setSpacingBefore(5);

		document.add(denialReport);
	}

	/**
	 * Creates a table cell with borders and height adjustments.
	 */
	private PdfPCell createBorderedCell(String content, Font font) throws Exception {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
//        cell.setPadding(2);  // Adjusts padding for spacing
		cell.setBorder(Rectangle.BOX); // Full border
		cell.setMinimumHeight(25f); // Adjust height to ensure uniformity
		return cell;
	}

	/**
	 * Adds a cell to the table with only a bottom border (removes left and right borders).
	 */
	private void addCellWithoutSideBorders(PdfPTable table, String text, Font font) throws Exception {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBorderWidthLeft(0);  // Remove left border
		cell.setBorderWidthRight(0); // Remove right border
		cell.setBorder(PdfPCell.BOTTOM); // Keep only bottom border
		cell.setPaddingBottom(5);
		table.addCell(cell);
	}

	/**
	 * Adds a cell to the table with only a bottom border (removes left and right borders).
	 */
	private void addHeaderCellWithoutSideBorders(PdfPTable table, String text, Font font) throws Exception {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBorderWidthLeft(0);  // Remove left border
		cell.setBorderWidthRight(0); // Remove right border
		cell.setBorder(PdfPCell.BOTTOM); // Keep only bottom border
		cell.setPaddingBottom(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}

	/**
	 * Adds a cell to the table with only a bottom border (removes left and right borders).
	 */
	private void addLine(Document document) throws Exception {
		LineSeparator line = new LineSeparator();
		line.setLineColor(Color.BLACK);
		line.setLineWidth(1);
		line.setPercentage(105);
		document.add(line);
	}

	/**
	 * @param document
	 */
	private void addNewPage(Document document) throws Exception {
		try {
			document.newPage();
			addPageHeader(document);
		} catch (Exception e) {
			throw new BadRequestException("Exception while creating new Page : " + e.getLocalizedMessage());
		}
	}

	/**
	 * @return
	 */
	private Image getLogo() throws Exception {
		try {
			// Creating an ImageData object
			String imageFile = propertiesConfig.getLogo();
			return Image.getInstance(imageFile);
		} catch (Exception e) {
			log.error("Exception while fetching Image : " + e.getLocalizedMessage());
			throw new BadRequestException("Image Fetch Exception : " + e.getLocalizedMessage());
		}
	}

	/**
	 * @return
	 */
	private SearchPickupLine getSearchPickupLine() throws Exception {

		SearchPickupLine searchPickupLine = new SearchPickupLine();

		searchPickupLine.setFromPickConfirmedOn(getStartDateTime());
		searchPickupLine.setToPickConfirmedOn(getEndDateTime());

		log.info("StartCreatedOn ------> {}", searchPickupLine.getFromPickConfirmedOn());
		log.info("EndCreatedOn ------> {}", searchPickupLine.getFromPickConfirmedOn());

		searchPickupLine.setWarehouseId(Collections.singletonList("110"));

		return searchPickupLine;
	}

	/**
	 * @return
	 */
	private Date getStartDateTime() throws Exception {
		// Set startOrderDate to 12:00 AM
		Calendar calendar = getPreviousDate();
//        calendar.set(Calendar.YEAR, 2024);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * @return
	 */
	private Date getEndDateTime() throws Exception {
		// Set startOrderDate to 23:59 PM
		Calendar calendar = getPreviousDate();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * @return
	 */
	private Calendar getPreviousDate() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0);
		return calendar;
	}

	/**
	 * @param dateTime
	 * @return
	 */
	private String getFormatDate(Date dateTime) throws Exception {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		return dateFormatter.format(dateTime);
	}

	/**
	 * @param count
	 * @return
	 */
	private String getCount(Long count) {
		return count != null ? String.valueOf(count) : "0";
	}

	/**
	 *
	 * @param document
	 */
	private void noDataFound(Document document) throws DocumentException {
		font.setSize(10);
		Paragraph noDataFound = new Paragraph("No Data Found", font);
		noDataFound.setAlignment(Paragraph.ALIGN_CENTER);
		noDataFound.setSpacingBefore(5);
		document.add(noDataFound);
	}

	private void noDataFoundDeliverConfPdf(Document document) throws DocumentException {
		font.setSize(10);
		Paragraph noDataFound = new Paragraph("No Data Found \n Order might not be processed Completely ", font);
		noDataFound.setAlignment(Paragraph.ALIGN_CENTER);
		noDataFound.setSpacingBefore(5);
		document.add(noDataFound);
	}


	/*=============================================DeliveryConfirmation===============================*/

	public void exportShipmentDeliveryReport(HttpServletResponse response, ShipmentDeliveryReport[] shipmentDeliveryReports) throws IOException, DocumentException, ParseException {

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(12);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(12);

		Font fontSubHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubHeader.setSize(9);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(105f);
		table.setWidths(new float[] {1.0f, 1.0f, 2.0f, 1.0f, 0.5f});
		table.setSpacingBefore(30);

		Paragraph paragraph = new Paragraph("Shipment Delivery Sheet", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
//		paragraph.setSpacingAfter(3.0f);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(9);

		// Convert array to list
        List<ShipmentDeliveryReport> reportList = null;
        try {
			reportList = Arrays.stream(shipmentDeliveryReports)
					.filter(r -> r.getQuantity() != null && r.getQuantity() > 0)
					.collect(Collectors.toList());

			SimpleDateFormat dateFormatter1 = new SimpleDateFormat("d-MMM-yyyy h:mm");
			String date1 = dateFormatter1.format(new Date());

			SimpleDateFormat dateFormatter2 = new SimpleDateFormat("d-MMM-yyyy");
			String date2 = dateFormatter2.format(reportList.get(0).getDeliveryDate());

			Paragraph delivery = new Paragraph("Delivery To: " + reportList.get(0).getDeliveryTo() + " - " + reportList.get(0).getPartnerName(), fontSubHeader);
			delivery.setAlignment(Paragraph.ALIGN_LEFT);
			delivery.setSpacingBefore(12.0f);
			Paragraph order = new Paragraph("Order Type : " + reportList.get(0).getOrderType(), fontSubHeader);
			order.setAlignment(Paragraph.ALIGN_LEFT);

			Paragraph printedDate = new Paragraph("Printed Date: " + date1 , fontSubHeader);
			printedDate.setAlignment(Paragraph.ALIGN_RIGHT);
//		printedDate.setSpacingBefore(9.0f);
			Paragraph deliveryDate = new Paragraph("Delivery Date: " + date2 , fontSubHeader);
			deliveryDate.setAlignment(Paragraph.ALIGN_RIGHT);


			// Creating an ImageData object
			String imageFile = propertiesConfig.getLogo();
			Image image = Image.getInstance(imageFile);

			image.scaleToFit(100,1000);

			image.setAbsolutePosition(30,780);
			document.add(paragraph);
			document.add(image);
			document.add(delivery);
			document.add(printedDate);
			document.add(order);
			document.add(deliveryDate);

			writeTableHeaderSd(table);
			writeTableDataSd(table, document, image, reportList);

			writeTableDataTotal(table, shipmentDeliveryReports);

			document.add(table);

			tableFooterSd(document, fontSubHeader);

        } catch (Exception e) {
			log.info("No Data Found");
			noDataFoundDeliverConfPdf(document);
        }
		document.close();
	}

	private void tableFooterSd(Document document, Font fontSubHeader) throws DocumentException, IOException{

		Paragraph specialInstruction = new Paragraph("Special Instruction: ", fontSubHeader);
		specialInstruction.setAlignment(Paragraph.ALIGN_LEFT);
		specialInstruction.setSpacingBefore(12.0f);

		Paragraph innerWorks = new Paragraph("InnerWorks Name & Signature: ", fontSubHeader);
		innerWorks.setAlignment(Paragraph.ALIGN_LEFT);

		Paragraph date = new Paragraph("Date: ", fontSubHeader);
		date.setAlignment(Paragraph.ALIGN_LEFT);

		Paragraph customerSignature = new Paragraph("Customer Name & Signature: 	", fontSubHeader);
		customerSignature.setAlignment(Paragraph.ALIGN_RIGHT);
		customerSignature.setIndentationRight(50f);

		Paragraph goodsReceived = new Paragraph("Goods Received in Good Condition: ", fontSubHeader);
		goodsReceived.setAlignment(Paragraph.ALIGN_RIGHT);
		goodsReceived.setIndentationRight(50f);


		document.add(specialInstruction);
		document.add(customerSignature);
		document.add(innerWorks);
		document.add(goodsReceived);
		document.add(date);

	}

	private void writeTableHeaderSd(PdfPTable table) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setPaddingBottom(5);
		cell.setBorderWidthBottom(1);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(9);

		cell.setPhrase(new Phrase("Customer Ref" , fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase(" Commodity", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Description ", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase(" Manf.Code", fontSubTitle));
		table.addCell(cell);

		cell.setPhrase(new Phrase(" Quantity", fontSubTitle));
		table.addCell(cell);

	}

	private void writeTableDataSd(PdfPTable table, Document document, Image image, List<ShipmentDeliveryReport> shipmentDeliveryReports) throws ParseException, DocumentException, IOException {

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(8.5f);
		font.setStyle(Font.NORMAL);

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

		int rowCount = 0; // Track the number of rows
		int maxRowsPerPage = 16; // Define the maximum number of rows per page


		for (ShipmentDeliveryReport header : shipmentDeliveryReports) {
//
			PdfPCell cell = new PdfPCell();
			cell.setBorder(0);
			cell.setPaddingBottom(10);
			cell.setBorderWidthBottom(1);

			cell.setPhrase(new Phrase(header.getCustomerRef(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getCommodity(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getDescription() , font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(header.getManfCode(), font));
			table.addCell(cell);

			cell.setPhrase(new Phrase(String.valueOf(header.getQuantity()), font));
			table.addCell(cell);
//
			rowCount++;

			// Check if we've reached the max rows per page
			if (rowCount >= maxRowsPerPage) {
				document.add(table); // Add the current table to the document

				// Add a new page
				document.newPage();

				// Add the logo and header
//				image.setAbsolutePosition(30, 780);
//				document.add(image);
				addPageHeaderSd(document, shipmentDeliveryReports);

				// Reset the table for the next page
				table.deleteBodyRows();

				writeTableHeaderSd(table);
				rowCount = 0;
			}
		}

//		// Add remaining rows if any
//		if (rowCount > 0) {
//			document.add(table);
//		}

	}

	private void addPageHeaderSd(Document document, List<ShipmentDeliveryReport> reportList) throws DocumentException, IOException {

		document.open();
		Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontTitle.setSize(12);

		Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubTitle.setSize(12);

		Font fontSubHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fontSubHeader.setSize(9);

		Paragraph paragraph = new Paragraph("Shipment Delivery Sheet", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
//		paragraph.setSpacingAfter(3.0f);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(9);

		// Convert array to list
//		List<ShipmentDeliveryReport> reportList = Arrays.asList(shipmentDeliveryReports);

		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("d-MMM-yyyy h:mm");
		String date1 = dateFormatter1.format(new Date());

		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("d-MMM-yyyy");
		String date2 = dateFormatter2.format(reportList.get(0).getDeliveryDate());

		Paragraph delivery = new Paragraph("Delivery To: " + reportList.get(0).getDeliveryTo() + " - " + reportList.get(0).getPartnerName(), fontSubHeader);
		delivery.setAlignment(Paragraph.ALIGN_LEFT);
		delivery.setSpacingBefore(12.0f);
		Paragraph order = new Paragraph("Order Type : " + reportList.get(0).getOrderType(), fontSubHeader);
		order.setAlignment(Paragraph.ALIGN_LEFT);

		Paragraph printedDate = new Paragraph("Printed Date: " + date1 , fontSubHeader);
		printedDate.setAlignment(Paragraph.ALIGN_RIGHT);
		Paragraph deliveryDate = new Paragraph("Delivery Date: " + date2 , fontSubHeader);
		deliveryDate.setAlignment(Paragraph.ALIGN_RIGHT);

		// Creating an ImageData object
		String imageFile = propertiesConfig.getLogo();
		Image image = Image.getInstance(imageFile);

		image.scaleToFit(100,1000);

		image.setAbsolutePosition(30,780);
		document.add(paragraph);
		document.add(image);
		document.add(delivery);
		document.add(printedDate);
		document.add(order);
		document.add(deliveryDate);

	}

	private void writeTableDataTotal(PdfPTable table, ShipmentDeliveryReport[] shipmentDeliveryReports) throws ParseException, DocumentException, IOException {

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(8.5f);
		font.setStyle(Font.NORMAL);

		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setPaddingBottom(10);

		// Convert array to list
		List<ShipmentDeliveryReport> reportList = Arrays.stream(shipmentDeliveryReports)
				.filter(r -> r.getQuantity() != null && r.getQuantity() > 0)
				.collect(Collectors.toList());

//		Fetch total value from the first object
		Double totalValue = reportList.get(0).getTotal();

		cell.setPhrase(new Phrase("", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Total : ", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase(String.valueOf(totalValue), font));
		table.addCell(cell);

	}


}

package com.tekclover.wms.api.idmaster.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.pickerdenial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PickupDenialReportService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    SendMailService sendMailService;

    Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

    @Scheduled(cron = "0 0 1 * * ?")
    private void generatePickerDenialReport() throws Exception {

        SearchPickupLine searchPickupLine = getSearchPickupLine();

        // Generate the PDF report for WH_ID 110
        PickerDenialReport pickerDenialReport = transactionService.pickerDenialReport(searchPickupLine);
        log.info("PickupLines_110 ---------------> {}", pickerDenialReport);

        // Generate the PDF report for WH_ID 111
        searchPickupLine.setWarehouseId(Collections.singletonList("111"));
        PickerDenialReport pickerDenialReport111 = transactionService.pickerDenialReport(searchPickupLine);
        log.info("PickupLines_111 ---------------> {}", pickerDenialReport111);

        File pdfFile110 = new File("WMS_Picker_Denial_Report_110.pdf");
        File pdfFile111 = new File("WMS_Picker_Denial_Report_111.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile110)) {
            export(fos, pickerDenialReport); // Adjust `export` to accept OutputStream
        }
        try (FileOutputStream fos = new FileOutputStream(pdfFile111)) {
            export(fos, pickerDenialReport111); // Adjust `export` to accept OutputStream
        }

        String fileName110 = pdfFile110.getName();
        String fileName111 = pdfFile111.getName();
        // Convert the File to MultipartFile
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile110)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    "file",             // Original file name
                    fileName110,              // File name
                    "application/pdf",        // Content type
                    fileInputStream           // File content as InputStream
            );

            // Use the existing storeFile method
            fileStorageService.storeFile(multipartFile);
        } catch (Exception e) {
            log.error("110 save exception..!");
            e.printStackTrace();
        }

        // Convert the File 111 to MultipartFile
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile111)) {
            MultipartFile multipartFile = new MockMultipartFile("file", fileName111, "application/pdf", fileInputStream);
            fileStorageService.storeFile(multipartFile);
        } catch (Exception e) {
            log.error("111 save exception..!");
            e.printStackTrace();
        }

        sendMailService.sendPickerDenialReportMail(fileName110, fileName111);

    }

    /**
     * @param response
     * @param pickerDenialReport
     * @throws Exception
     */
    public void export(OutputStream response, PickerDenialReport pickerDenialReport) throws Exception {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response);

        document.open();

        addPageHeader(document);
        if (pickerDenialReport.getHeaders() != null && !pickerDenialReport.getHeaders().isEmpty()) {
            writeTableData(pickerDenialReport.getHeaders(), document);
        } else {
            noDataFound(document);
        }

        writeReportSummaryTitle(document);
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
            addCellWithoutSideBorders(tableLines, line.getRemarks(), font);

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
        table.setWidths(new float[]{1.0f, 2.0f, 6.0f, 2.5f, 1.5f, 1.5f, 2.5f});
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
    private void writeReportSummaryTitle(Document document) throws Exception {

        document.newPage();
        font.setSize(12);
        addPageHeader(document);

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
    private void addPageHeader(Document document) throws Exception {

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

        // Selection Date
        String formattedStartDate = getFormatDate(getStartDateTime());
        String formattedEndDate = getFormatDate(getEndDateTime());

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
    private void noDataFound(Document document) {
        font.setSize(10);
        Paragraph noDataFound = new Paragraph("No Data Found", font);
        noDataFound.setAlignment(Paragraph.ALIGN_CENTER);
        noDataFound.setSpacingBefore(5);
        document.add(noDataFound);
    }
}
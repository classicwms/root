package com.tekclover.wms.api.idmaster.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.model.pdfreport.MetricsSummary;
import com.tekclover.wms.api.idmaster.model.pdfreport.ShipmentDeliverySummary;
import com.tekclover.wms.api.idmaster.model.pdfreport.ShipmentDeliverySummaryReport;
import com.tekclover.wms.api.idmaster.model.pdfreport.SummaryMetrics;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdfReportService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    SendMailService sendMailService;

//    @Scheduled(cron = "0 0 15 * * ?")
    @Scheduled(cron = "0 */10 * * * ?")
    private void generateShipmentDeliveryReport() throws Exception {

        // Converting StartDateTime and EndDateTime
        Date startDateTime = getStartDateTime();
        Date endDateTime = getEndDateTime();

        log.info("StartDateTime -----> {}", startDateTime);
        log.info("EndDateTime ------> {}", endDateTime);

        String fromDeliveryDate = getFormatDate(startDateTime);
        String toDeliveryDate = getFormatDate(endDateTime);

        List<String> customerCode = new ArrayList<>();
        String warehouseId_110 = "110";
        String warehouseId_111 = "111";

        ShipmentDeliverySummaryReport shipmentDeliverySummaryReport_110 =
                transactionService.getShipmentDeliverySummaryReportPdf(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId_110);

        log.info("ShipmentDeliverySummaryReport 110 : {}", shipmentDeliverySummaryReport_110);

        ShipmentDeliverySummaryReport shipmentDeliverySummaryReport_111 =
                transactionService.getShipmentDeliverySummaryReportPdf(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId_111);

        log.info("ShipmentDeliverySummaryReport 111 : {}", shipmentDeliverySummaryReport_111);

        File pdfFile110 = new File("110_Delivery_Report_" + fromDeliveryDate + ".pdf");
        File pdfFile111 = new File("111_Delivery_Report_" + fromDeliveryDate + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(pdfFile110)) {
            deliveryExport(fos, shipmentDeliverySummaryReport_110, fromDeliveryDate, toDeliveryDate);
        }

        try (FileOutputStream fos = new FileOutputStream(pdfFile111)) {
            deliveryExport(fos, shipmentDeliverySummaryReport_111, fromDeliveryDate, toDeliveryDate);
        }

        String fileName110 = pdfFile110.getName();
        log.info("ShipmentDelivery Report fileName ----> {}", fileName110);

        String fileName111 = pdfFile111.getName();
        log.info("ShipmentDelivery Report fileName -----> {}", fileName111);

        //Convert the File to MultipartFile
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

        //Convert the File to MultipartFile
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile111)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    "file",             // Original file name
                    fileName111,              // File name
                    "application/pdf",        // Content type
                    fileInputStream           // File content as InputStream
            );

            // Use the existing storeFile method
            fileStorageService.storeFile(multipartFile);
        } catch (Exception e) {
            log.error("110 save exception..!");
            e.printStackTrace();
        }

        sendMailService.sendShipmentDeliveryReport(fileName110, fileName111);
    }

    //------------------------------------------------------------Delivery Report-------------------------------------------------------------//

    /**
     * Modified for TV ShipmentReport Pdf in Backend - 07/07/2025
     * Aakash Vinayak
     *
     * @param response
     * @param shipmentDeliverySummaryReport
     * @param fromDeliveryDate
     * @param toDeliveryDate
     * @throws Exception
     */
    public void deliveryExport(OutputStream response , ShipmentDeliverySummaryReport shipmentDeliverySummaryReport, String fromDeliveryDate, String toDeliveryDate) throws Exception {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(11);

        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(105f);
        table.setWidths(new float[] {2.0f, 2.0f, 1.5f, 1.5f, 2.5f, 1.8f, 1.8f, 2.0f, 2.0f, 1.5f, 1.5f});
        table.setSpacingBefore(10);

        // Creating an ImageData object
        String imageFile = propertiesConfig.getLogo();
        Image image = Image.getInstance(imageFile);

        image.scaleToFit(100,1000);

        image.setAbsolutePosition(30,780);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(8);

        String[] rangeDates = DateUtils.pdfReportDate(fromDeliveryDate, toDeliveryDate);

        String formattedStartDate = rangeDates[0];
        String formattedEndDate = rangeDates[1];

        Paragraph paragraph2 = new Paragraph("Selection Date\n" + formattedStartDate + " - "  + formattedEndDate , fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);


        Paragraph paragraph = new Paragraph("Delivery Report", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph.setSpacingAfter(25.0f);

        //----------------hardCode
        Paragraph paragraph1 = new Paragraph("Amghara Warehouse", fontSubTitle);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph1.setSpacingAfter(10f);

        document.add(image);
        document.add(paragraph2);
        document.add(paragraph);
        document.add(paragraph1);

//		writeTableHeaderDelivery(table);
        writeTableDataDr(table,document,shipmentDeliverySummaryReport.getShipmentDeliverySummary(),fromDeliveryDate, toDeliveryDate);
        document.add(table);
        ReportSummaryTitleDeliveryReport(document,shipmentDeliverySummaryReport.getSummaryMetrics(),fromDeliveryDate, toDeliveryDate);
        document.close();
    }

    /**
     * Writing TableData for ReportPdf - 04/07/2025
     * Aakash Vinayak
     *
     * @param table
     * @param document
     * @param shipmentDeliverySummaryReports
     * @param fromDeliveryDate
     * @param toDeliveryDate
     * @throws ParseException
     * @throws DocumentException
     * @throws IOException
     */
    private void writeTableDataDr(PdfPTable table, Document document, List<ShipmentDeliverySummary> shipmentDeliverySummaryReports, String fromDeliveryDate, String toDeliveryDate)
            throws ParseException, DocumentException, IOException {

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, Font.NORMAL);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(11);

        int maxRowsPerPage = 16;

        // Group by BranchCode + BranchDesc
        Map<String, List<ShipmentDeliverySummary>> groupedData = shipmentDeliverySummaryReports.stream()
                .collect(Collectors.groupingBy(item -> item.getBranchCode() + " - " + item.getBranchDesc(), LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<ShipmentDeliverySummary>> entry : groupedData.entrySet()) {
            String branchInfo = entry.getKey();
            List<ShipmentDeliverySummary> groupList = entry.getValue();

            // Title: Branch Code + Description
            Paragraph paragraph3 = new Paragraph(branchInfo, fontSubTitle);
            paragraph3.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(paragraph3);

            // Reset counters for group
            int rowCount = 0;
            long totalSkusOrdered = 0;
            long totalSkusShipped = 0;
            long totalLinesPicked = 0;
            double totalQtyOrdered = 0;
            double totalQtyShipped = 0;
            double totalPercent = 0;
            int recordCount = 0;

            writeTableHeaderDelivery(table); // Add table header once per group

            for (ShipmentDeliverySummary header : groupList) {
                PdfPCell cell = new PdfPCell();
                cell.setBorder(0);
                cell.setPaddingBottom(15f);
                cell.setBorderWidthBottom(1f);

                // Date & Time Formatting
                cell.setPhrase(new Phrase(dateFormatter.format(header.getExpectedDeliveryDate()), font));
                table.addCell(cell);

                String deliveryDateTime = header.getDeliveryDateTime() != null
                        ? dateFormatter.format(header.getDeliveryDateTime()) + "\n" + new SimpleDateFormat("HH:mm").format(header.getDeliveryDateTime())
                        : "";
                cell.setPhrase(new Phrase(deliveryDateTime, font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(header.getBranchCode(), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(header.getOrderType(), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(header.getSo(), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.valueOf(header.getLineOrdered()), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.valueOf(header.getLineShipped()), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.valueOf(header.getPickedQty()), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.valueOf(header.getOrderedQty()), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.valueOf(header.getShippedQty()), font));
                table.addCell(cell);

                cell.setPhrase(new Phrase(String.format("%.2f", header.getPercentageShipped()), font));
                table.addCell(cell);

                // Group Totals
                totalSkusOrdered += header.getLineOrdered() != null ? header.getLineOrdered() : 0;
                totalSkusShipped += header.getLineShipped() != null ? header.getLineShipped() : 0;
                totalLinesPicked += header.getPickedQty() != null ? header.getPickedQty().longValue() : 0;
                totalQtyOrdered += header.getOrderedQty() != null ? header.getOrderedQty() : 0;
                totalQtyShipped += header.getShippedQty() != null ? header.getShippedQty() : 0;
                totalPercent += header.getPercentageShipped() != null ? header.getPercentageShipped() : 0;
                recordCount++;

                rowCount++;

                if (rowCount >= maxRowsPerPage) {
                    document.add(table);
                    document.newPage();
                    addPageHeaderDR(document,fromDeliveryDate, toDeliveryDate);
                    Paragraph repeatTitle = new Paragraph(branchInfo, fontSubTitle);
                    repeatTitle.setAlignment(Paragraph.ALIGN_CENTER);
                    document.add(repeatTitle);
                    table.deleteBodyRows();
                    writeTableHeaderDelivery(table);
                    rowCount = 0;
                }
            }

            // Totals row
            for (int i = 0; i < 4; i++) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("", font));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                emptyCell.setMinimumHeight(20f);
                table.addCell(emptyCell);
            }

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total", font));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalLabelCell.setPaddingTop(6f);
            totalLabelCell.setPaddingBottom(6f);
            totalLabelCell.setMinimumHeight(20f);
            table.addCell(totalLabelCell);

            List<String> totalValues = Arrays.asList(
                    String.valueOf(totalSkusOrdered),
                    String.valueOf(totalSkusShipped),
                    String.valueOf(totalLinesPicked),
                    String.valueOf(totalQtyOrdered),
                    String.valueOf(totalQtyShipped),
                    String.format("%.2f", (recordCount > 0 ? (totalPercent / recordCount) : 0))
            );

            for (String value : totalValues) {
                PdfPCell totalCell = new PdfPCell(new Phrase(value, font));
                totalCell.setBorder(Rectangle.NO_BORDER);
                totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                totalCell.setPaddingTop(6f);
                totalCell.setPaddingBottom(6f);
                totalCell.setMinimumHeight(20f);
                table.addCell(totalCell);
            }

            document.add(table);
            table.deleteBodyRows();
        }
    }

    /**
     * This is for Writing TableHeaders - 04/07/2025
     * Aakash Vinayak
     *
     * @param table
     * @throws DocumentException
     * @throws IOException
     */
    private void writeTableHeaderDelivery(PdfPTable table) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);


        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(7);

        cell.setPhrase(new Phrase("Expected\nDelivery Date", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Delivery Date\nTime", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Branch\nCode", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Order\nType", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# S.O", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# SKUs\nOrdered", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# SKUs\nShipped", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# Lines\nPicked", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Ordered\nQty", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Shipped\nQty", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("%\nShipped", fontSubTitle));
        table.addCell(cell);
    }

    /**
     * This method is for adding PageHeader for Next page if continues
     * Aakash Vinayak - 04/07/2025
     *
     * @param document
     * @param fromDeliveryDate
     * @param toDeliveyDate
     * @throws DocumentException
     * @throws IOException
     */
    private void addPageHeaderDR(Document document, String fromDeliveryDate, String toDeliveyDate) throws DocumentException, IOException {
        // Fonts
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15);
        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA, 8);

        // Logo
        String imageFile = propertiesConfig.getLogo();
        Image image = Image.getInstance(imageFile);
        image.scaleToFit(100, 1000);
        image.setAbsolutePosition(30, 780); // top-left position
        document.add(image);

        String formattedStartDate = fromDeliveryDate;
        String formattedEndDate =  toDeliveyDate;

        Paragraph selectionDate = new Paragraph("Selection Date\n" + formattedStartDate + " - " + formattedEndDate, fontParagraph);
        selectionDate.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(selectionDate);

        Paragraph title = new Paragraph("Delivery Report", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
    }

    /**
     * This Method is for Writing the SummaryTitle
     * Aakash Vinayak - 04/07/2025
     *
     * @param document
     * @param summaryMetricsList
     * @param fromDeliveryDate
     * @param toDeliveryDate
     * @throws Exception
     */
    private void ReportSummaryTitleDeliveryReport(Document document,
                                                  List<SummaryMetrics> summaryMetricsList, String fromDeliveryDate, String toDeliveryDate) throws Exception {

        document.newPage();
        addPageHeaderDR(document,fromDeliveryDate, toDeliveryDate);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Paragraph summaryTitle = new Paragraph("Report Summary", titleFont);
        summaryTitle.setAlignment(Paragraph.ALIGN_LEFT);
        summaryTitle.setSpacingBefore(10);
        document.add(summaryTitle);

        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f);

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{5f, 2.5f, 2f, 2f, 2f, 2f, 2f, 2f, 2f});
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        writeSummaryHeaderDelivery(table); // your existing header row

        // Use the new grouped logic
        DeliveryReportSummaryDataBind(table, summaryMetricsList, bodyFont, boldFont);
        document.add(table);
    }

    //------------------------------------------Summary----------------------------------//

    /**
     * This method is for Writing SummaryHeaders
     * Aakash Vinayak - 04/07/2025
     *
     * @param table2
     * @throws Exception
     */
    private void writeSummaryHeaderDelivery(PdfPTable table2) throws Exception {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(8.5f);
        font.setStyle(Font.BOLD);

        table2.addCell(createBorderedCell("Location", font));
        table2.addCell(createBorderedCell("Type", font));
        table2.addCell(createBorderedCell("#No of \nOrders", font));
        table2.addCell(createBorderedCell("#SKUs \nOrdered", font));
        table2.addCell(createBorderedCell("#SKUs \nShipped", font));
        table2.addCell(createBorderedCell("#Lines \nPicked", font));
        table2.addCell(createBorderedCell("#Order Qty", font));
        table2.addCell(createBorderedCell("Shipped \nQty", font));
        table2.addCell(createBorderedCell("%Shipped", font));
    }

    /**
     * This Method is for Binding the Data in summary table
     * Aakash Vinayak - 04/07/2025
     *
     * @param table
     * @param summaryMetricsList
     * @param bodyFont
     * @param boldFont
     */
    private void DeliveryReportSummaryDataBind(PdfPTable table,
                                               List<SummaryMetrics> summaryMetricsList, Font bodyFont, Font boldFont) {

        // Grand‑totals
        int    grandTotalOrders      = 0;
        int    grandTotalSKUsOrdered = 0;
        int    grandTotalSKUsShipped = 0;
        int    grandTotalLinesPicked = 0;
        double grandTotalOrderQty    = 0.0;
        double grandTotalShippedQty  = 0.0;
        double shippedPercentSum     = 0.0;
        int    rowCount              = 0;

        for (SummaryMetrics sm : summaryMetricsList) {
            MetricsSummary m = sm.getMetricsSummary();

            // Extracted values
            Double orders       = m.getTotalOrder();
            Double skusOrdered  = m.getLineItems();
            Double shippedLines = m.getShippedLines();
            Double linesPicked  = m.getLineItemPicked();
            double orderQty     = m.getOrderedQty();
            double shipQty      = m.getDeliveryQty();
            double pctShp       = m.getPercentageShipped();

            // Build and add row
            List<String> cols = Arrays.asList(
                    sm.getPartnerCode(),
                    sm.getType(),
                    String.valueOf(orders),
                    String.valueOf(skusOrdered),
                    String.valueOf(shippedLines),
                    String.valueOf(linesPicked),
                    String.format("%.2f", orderQty),
                    String.format("%.2f", shipQty),
                    String.format("%.2f", pctShp)
            );

            for (String c : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(c, bodyFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPaddingTop(6f);
                cell.setPaddingBottom(6f);
                cell.setMinimumHeight(20f);
                table.addCell(cell);
            }

            // Accumulate grand totals
            grandTotalOrders      += orders;
            grandTotalSKUsOrdered += skusOrdered;
            grandTotalSKUsShipped += shippedLines;
            grandTotalLinesPicked += linesPicked;
            grandTotalOrderQty    += orderQty;
            grandTotalShippedQty  += shipQty;
            shippedPercentSum     += pctShp;
            rowCount++;
        }

        // Grand‑total row
        PdfPCell totalLabel = new PdfPCell(new Phrase("Total", boldFont));
        totalLabel.setColspan(2);
        totalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(totalLabel);

        table.addCell(new Phrase(String.valueOf(grandTotalOrders),      boldFont));
        table.addCell(new Phrase(String.valueOf(grandTotalSKUsOrdered), boldFont));
        table.addCell(new Phrase(String.valueOf(grandTotalSKUsShipped), boldFont));
        table.addCell(new Phrase(String.valueOf(grandTotalLinesPicked), boldFont));
        table.addCell(new Phrase(String.format("%.2f", grandTotalOrderQty),   boldFont));
        table.addCell(new Phrase(String.format("%.2f", grandTotalShippedQty), boldFont));

        double avgPct = rowCount > 0 ? shippedPercentSum / rowCount : 0.0;
        table.addCell(new Phrase(String.format("%.2f", avgPct), boldFont));
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

    //------------------------------------- Date Convertion Utils ---------------------------------------//

    /**
     * Modified dateTime to yesterday 2.00pm
     * Aakash Vinayak - 24/07/2025
     *
     * @return
     */
    private Date getStartDateTime() throws Exception {
        Calendar calendar = getPreviousDate(); // yesterday
        calendar.set(Calendar.HOUR, 2); // 2 o'clock
        calendar.set(Calendar.AM_PM, Calendar.PM); // PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Modified dateTime to yesterday 1.59pm
     * Aakash Vinayak  - 24/07/2025
     *
     * @return
     */
    private Date getEndDateTime() throws Exception {
        Calendar calendar = getCurrentDate(); // today
        calendar.set(Calendar.HOUR, 1); // 1 o'clock
        calendar.set(Calendar.AM_PM, Calendar.PM); // PM
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
        calendar.add(Calendar.DATE, -1);
        return calendar;
    }

    /**
     * @return
     */
    private Calendar getCurrentDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        return calendar;
    }

    /**
     * Modified for Converting DateTime Sting to MM-dd-yyyy'T'HH:mm:ss
     * pattern - 07/07/2025 , Aakash Vinayak
     *
     * @param dateTime
     * @return
     * @throws Exception
     */
    private String getFormatDate(Date dateTime) throws Exception {
        LocalDateTime localDateTime = dateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss");
        return localDateTime.format(formatter);
    }

    /**
     *
     * @param date
     * @return
     */
    private String getFormat12HrsDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a"); // 12-hour with AM/PM
        return formatter.format(date);
    }



}

package com.tekclover.wms.api.idmaster.service;


import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.model.pdfModels.PickupLine;
import com.tekclover.wms.api.idmaster.model.pickerdenial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.itextpdf.kernel.xmp.PdfConst.Description;

@Slf4j
@Service
public class PickupDenialService {

    @Autowired
    PropertiesConfig propertiesConfig;


    public void export(OutputStream response, PickerDenialReport pickerDenialReport, SearchPickupLine searchPickupLine, Date startOrderDate) throws IOException, DocumentException, ParseException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(12f);

        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(105f);
        table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        table.setSpacingBefore(10);

        PdfPTable table2 = new PdfPTable(8);
        table2.setWidthPercentage(100f);
        table2.setWidths(new float[] {5.5f, 3.0f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f,2.5f});
        table2.setSpacingBefore(10);

        PdfPTable table3 = new PdfPTable(8);
        table3.setWidthPercentage(100f);
        table3.setWidths(new float[] {5.5f, 3.0f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f,2.5f});
        table3.setSpacingBefore(10);


        // Creating an ImageData object
        String imageFile = propertiesConfig.getLogo();
        Image image = Image.getInstance(imageFile);

        image.scaleToFit(100, 1000);
        document.open();
        image.setAbsolutePosition(30, 780);
        document.add(image);
//        document.add(paragraph2);
//        document.add(paragraph3);
//        document.add(paragraph);

//        String partnerCode = pickupLines.get(0).getPartnerCode();

//        addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
//        writeTableHeader(table);
        addPageHeader2(document, searchPickupLine, startOrderDate);
        writeTableData2(table, pickerDenialReport, document, searchPickupLine, startOrderDate, image);
        writeSummaryTableHeader(table2);



//        document.add(table);

//        addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
        writeSummaryHeader(document, image);
//        writeTableData2(table3, table2, pickupLines, document, image,searchPickupLine, startOrderDate);
//        document.add(table2);

        writeSummaryTableData(table3, pickerDenialReport, document);



        document.close();
    }

    private void writeTableHeader(PdfPTable table) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(9);

        cell.setPhrase(new Phrase("Denial Date", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Delivery Date \n Time", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Branch \n Code", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Order \n Type", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# S.O", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# SKUs \n Ordered", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# SKUs \n Shipped", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("# SKU \n Denied", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Ordered \n Qty", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Shipped \n Qty", fontSubTitle));
        table.addCell(cell);

        cell.setPhrase(new Phrase("% \n Shipped", fontSubTitle));
        table.addCell(cell);


        // ✅ Ensure the header row is repeated on every page
//        table.setHeaderRows(1);




    }


    private void writeSummaryHeader(Document document, Image image) throws DocumentException, IOException {

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubtitle.setSize(12);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(10);

        image.scaleToFit(100, 1000);
        document.open();
        image.setAbsolutePosition(30, 780);
        document.add(image);

        // Selection Date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String formattedStartDate = dateFormatter.format(new Date());
        String formattedEndDate = dateFormatter.format(new Date());

        Paragraph dateInfo = new Paragraph("Selection Date: \n" + formattedStartDate + " - " + formattedEndDate, fontParagraph);
        dateInfo.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(dateInfo);


        // Report Title
        Paragraph denialReport = new Paragraph("Denial Report", fontTitle);
        denialReport.setAlignment(Paragraph.ALIGN_CENTER);
        denialReport.setSpacingBefore(20);
        document.add(denialReport);


        // Adding Report Summary Header
        Paragraph reportSummary = new Paragraph("Report Summary", fontSubtitle);
        reportSummary.setAlignment(Paragraph.ALIGN_LEFT);
        reportSummary.setSpacingBefore(10);
        document.add(reportSummary);


    }

//    private void writeTableData(PdfPTable table, PickerDenialReport pickerDenialReport, Document document, Image image,
//                                SearchPickupLine searchPickupLine, Date startOrderDate)
//            throws ParseException, DocumentException, IOException {
//
//        Font font = FontFactory.getFont(FontFactory.HELVETICA);
//        font.setSize(8.5f);
//        font.setStyle(Font.NORMAL);
//
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
//        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
//        int maxRowsPerPage = 22;
//
//        // Group records by partnerCode
//        Map<String, List<PickerDenialHeader>> groupedByPartner = pickerDenialReport.getHeaders().stream()
//                .collect(Collectors.groupingBy(PickerDenialHeader::getPartnerCode, LinkedHashMap::new, Collectors.toList()));
//
//        for (Map.Entry<String, List<PickerDenialHeader>> entry : groupedByPartner.entrySet()) {
//            String partnerCode = entry.getKey();
//            List<PickerDenialHeader> partnerHeaders = entry.getValue();
//
//            int rowCount = 0;
//
//            // Always start with a new page for a new partner
//            image.setAbsolutePosition(30, 780);
//            document.add(image);
//
//            // Add the initial header for the partner
//            addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
//
//            table.deleteBodyRows();  // Clear table before adding a new header
//            writeTableHeader(table); // Re-add table headers
//
//            for (PickerDenialHeader header : partnerHeaders) {
//                for (PickerDenialReportImpl line : header.getLines()) {
//                    if (rowCount >= maxRowsPerPage) {
//                        // If the current page reaches maxRowsPerPage, create a new page but keep the same partner header
//                        document.add(table);
//                        document.newPage();
//                        image.setAbsolutePosition(30, 780);
//                        document.add(image);
//
//                        // Retain partner header on the new page
//                        addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
//                        table.deleteBodyRows();  // Clear table for new page
//                        writeTableHeader(table); // Re-add table headers
//
//                        rowCount = 0; // Reset row count for the new page
//                    }
//
//                    // Adding data to the table
//                    addCellWithoutSideBorders(table, header.getSDenialDate(), font);
//                    addCellWithoutSideBorders(table, header.getSDeliveryDate(), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getPartnerCode()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getOrderType()), font);
//                    addCellWithoutSideBorders(table, header.getPreOutboundNo(), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getSkuOrdered()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getSkuShipped()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getSkuDenied()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getOrderedQty()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getShippedQty()), font);
//                    addCellWithoutSideBorders(table, String.valueOf(header.getPercentageShipped()), font);
//
//                    rowCount++;
//                }
//            }
//
//            // Add this block to handle the last page header properly
//            if (rowCount > 0) {
//                document.add(table);
//
//                image.scaleToFit(100, 1000);
//                document.open();
//                image.setAbsolutePosition(30, 780);
//                document.add(image);
//
//                // Add the page header for the last page
//                addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
//            }
//        }
//    }

//    private void writeTableData(PdfPTable table, PickerDenialReport report, Document document,
//                                Image image, SearchPickupLine searchPickupLine, Date startOrderDate)
//            throws DocumentException, IOException {
//        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);
//
//        // Group by Partner Code
//        Map<String, List<PickerDenialHeader>> groupedByPartner = report.getHeaders()
//                .stream()
//                .collect(Collectors.groupingBy(PickerDenialHeader::getPartnerCode, LinkedHashMap::new, Collectors.toList()));
//
//        for (var entry : groupedByPartner.entrySet()) {
//            String partnerCode = entry.getKey();
//            List<PickerDenialHeader> headers = entry.getValue();
//
//            // Add new page header
//            addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);
////            PdfPTable table2 = new PdfPTable(9);
////            table.setWidthPercentage(105f);
////            table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
//
//            table.deleteBodyRows();
//            writeTableHeader(table); // Ensure table headers are written
//
//            for (PickerDenialHeader header : headers) {
//
//                addCellWithoutSideBorders(table, header.getSDenialDate(), font);   // Denial Date
//                addCellWithoutSideBorders(table, header.getSDeliveryDate(), font); // Delivery Date Time
//                addCellWithoutSideBorders(table, header.getPartnerCode(), font);   // Branch Code
//                addCellWithoutSideBorders(table, header.getOrderType(), font);     // Order Type
//                addCellWithoutSideBorders(table, header.getPreOutboundNo(), font); // # S.O
//                addCellWithoutSideBorders(table, String.valueOf(header.getSkuOrdered()), font);  // # SKUs Ordered
//                addCellWithoutSideBorders(table, String.valueOf(header.getSkuShipped()), font);  // # SKUs Shipped
//                addCellWithoutSideBorders(table, String.valueOf(header.getSkuDenied()), font);   // # SKU Denied
//                addCellWithoutSideBorders(table, String.valueOf(header.getOrderedQty()), font);  // Ordered Qty
//                addCellWithoutSideBorders(table, String.valueOf(header.getShippedQty()), font);  // Shipped Qty
//                addCellWithoutSideBorders(table, String.valueOf(header.getPercentageShipped()), font); //
//
//
////                writeTableHeaderData(table, header, document);
////                writeTableLineData(header, document);
//
//            }
//
//            // Add completed table section to the document
//            document.add(table);
//            document.newPage();
//        }
//    }
private void writeTableData(PdfPTable tableHeader, PickerDenialReport report, Document document,
                            Image image, SearchPickupLine searchPickupLine, Date startOrderDate)
        throws DocumentException, IOException {

    Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

    // Group by Partner Code
    Map<String, List<PickerDenialHeader>> groupedByPartner = report.getHeaders()
            .stream()
            .collect(Collectors.groupingBy(PickerDenialHeader::getPartnerCode, LinkedHashMap::new, Collectors.toList()));

    for (var entry : groupedByPartner.entrySet()) {
        String partnerCode = entry.getKey();
        List<PickerDenialHeader> headers = entry.getValue();

        // Add new page header
        addPageHeader(document, searchPickupLine, startOrderDate, partnerCode);

        // ✅ Create Separate Tables for Headers & Lines
        tableHeader.deleteBodyRows();
        writeTableHeader(tableHeader); // Ensure header table structure

        // ✅ Create Line-Level Table (Adjusted Description Position)
        PdfPTable tableLines = new PdfPTable(8); // Adjusted to 8 columns
        tableLines.setWidthPercentage(100);
        tableLines.setSpacingBefore(5f);

        // ✅ Add table header before adding data
        writeTableHeader(tableHeader);
        document.add(tableHeader);


        for (PickerDenialHeader header : headers) {
            // ✅ **Print Header Data in tableHeader (Without PreOutboundNo)**
            addCellWithoutSideBorders(tableHeader, header.getSDenialDate(), font);   // Denial Date
            addCellWithoutSideBorders(tableHeader, header.getSDeliveryDate(), font); // Delivery Date/Time
            addCellWithoutSideBorders(tableHeader, header.getPartnerCode(), font);   // Branch Code
            addCellWithoutSideBorders(tableHeader, header.getOrderType(), font);     // Order Type
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getSkuOrdered()), font);  // # SKUs Ordered
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getSkuShipped()), font);  // # SKUs Shipped
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getSkuDenied()), font);   // # SKU Denied
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getOrderedQty()), font);  // Ordered Qty
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getShippedQty()), font);  // Shipped Qty
            addCellWithoutSideBorders(tableHeader, String.valueOf(header.getPercentageShipped()), font); // % Shipped

            // ✅ **Print Line-Level Data in tableLines (Moved Description Column)**
            for (PickerDenialReportImpl line : header.getLines()) {
                addCellWithoutSideBorders(tableLines, String.valueOf(line.getLineNumber()), font);
                addCellWithoutSideBorders(tableLines, line.getPickupNumber(), font);
                addCellWithoutSideBorders(tableLines, line.getItemCode(), font);
                addCellWithoutSideBorders(tableLines, line.getDescription(), font);  // **✅ Moved Here**
                addCellWithoutSideBorders(tableLines, line.getPickedStorageBin(), font);
//                addCellWithoutSideBorders(tableLines, line.getPickedPackCode(), font);
                addCellWithoutSideBorders(tableLines, line.getAssignedPickerId(), font);
                addCellWithoutSideBorders(tableLines, String.valueOf(line.getOrderedQty()), font);
                addCellWithoutSideBorders(tableLines, String.valueOf(line.getShippedQty()), font);
            }

            // ✅ Add **Header Table** to Document
            document.add(tableHeader);

            // ✅ Add **Lines Table** Only If It Has Data
            if (!header.getLines().isEmpty()) {
                document.add(tableLines);
            }

            document.newPage(); // Page break after each partner
        }
    }
}

private void writeTableData2(PdfPTable tableHeader, PickerDenialReport report, Document document,
                            SearchPickupLine searchPickupLine, Date startOrderDate, Image imageFile)
        throws DocumentException, IOException {

    Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);
    List<PickerDenialHeader> headers = report.getHeaders();
    String partnerCodeChange = null;
    for (PickerDenialHeader header : headers) {
        String partnerCode = header.getPartnerCode() + " - " + header.getPartnerName();

//        PdfPTable table = new PdfPTable(11);
//        table.setWidthPercentage(105f);
//        table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
//        table.setSpacingBefore(10);

        if(partnerCodeChange == null) {
            partnerCodeChange = header.getPartnerCode();
            addPartnerCodeHeader(document, partnerCode);
            tableHeader = new PdfPTable(11); // Adjusted to 11 columns
            tableHeader.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
            tableHeader.setWidthPercentage(105f);
//            writeTableHeader(tableHeader);
//            document.add(tableHeader);
        } else if(!partnerCodeChange.equalsIgnoreCase(header.getPartnerCode())) {
            partnerCodeChange = header.getPartnerCode();
            addPartnerCodeHeader(document, partnerCode);
            tableHeader = new PdfPTable(11); // Adjusted to 11 columns
            tableHeader.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
            tableHeader.setWidthPercentage(105f);
//            writeTableHeader(tableHeader);
//            document.add(tableHeader);
        }

        PdfPTable tableHeader1 = new PdfPTable(11); // Adjusted to 11 columns
        tableHeader1.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        tableHeader1.setWidthPercentage(105f);

        writeTableHeader(tableHeader1);
        document.add(tableHeader1);

//        PdfPTable table = new PdfPTable(11);
//        table.setWidthPercentage(105f);
//        table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
//        table.setSpacingBefore(10);
//
//        writeTableHeader(table);
//        document.add(table);

        // Add new page header

        // ✅ Create Separate Tables for Headers & Lines
//        tableHeader.deleteBodyRows();
//        writeTableHeader(tableHeader); // Ensure header table structure

//         ✅ Create Line-Level Table (Adjusted Description Position)
        PdfPTable tableLines = new PdfPTable(8); // Adjusted to 8 columns
        tableLines.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        tableLines.setWidthPercentage(105f);
        tableLines.setSpacingBefore(5f);

        PdfPTable tableLines2 = new PdfPTable(8); // Adjusted to 8 columns
        tableLines2.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        tableLines2.setWidthPercentage(105f);
        tableLines2.setSpacingBefore(5f);

        PdfPTable tableHeader2 = new PdfPTable(11); // Adjusted to 8 columns
        tableHeader2.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        tableHeader2.setWidthPercentage(105f);
        tableHeader2.setSpacingBefore(5f);

        // ✅ Add table headers before adding data
//        writeTableHeader(table);
//        table.setHeaderRows(1); // ✅ Ensures table headers repeat on each new page
//        document.add(table);

//        for (PickerDenialHeader header : headers) {
            // ✅ **Print Header Data in tableHeader (Without PreOutboundNo)**
            addCellWithoutSideBorders(tableHeader2, header.getSDenialDate(), font);   // Denial Date
            addCellWithoutSideBorders(tableHeader2, header.getSDeliveryDate(), font); // Delivery Date/Time
            addCellWithoutSideBorders(tableHeader2, header.getPartnerCode(), font);   // Branch Code
            addCellWithoutSideBorders(tableHeader2, header.getOrderType(), font);     // Order Type
            addCellWithoutSideBorders(tableHeader2, header.getRefDocNumber(), font);     // Order Type
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getSkuOrdered()), font);  // # SKUs Ordered
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getSkuShipped()), font);  // # SKUs Shipped
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getSkuDenied()), font);   // # SKU Denied
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getOrderedQty()), font);  // Ordered Qty
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getShippedQty()), font);  // Shipped Qty
            addCellWithoutSideBorders(tableHeader2, String.valueOf(header.getPercentageShipped()), font); // % Shipped

            // ✅ Add **Header Table** to Document
            document.add(tableHeader2);
//            document.add(tableLines2);

            if(header.getLines() != null && !header.getLines().isEmpty()) {
                writeTableLineHeader(tableLines2);
                document.add(tableLines2);
//                tableLines2.setHeaderRows(1);
                // ✅ **Print Line-Level Data in tableLines (Moved Description Column)**
                for (PickerDenialReportImpl line : header.getLines()) {

                    addCellWithoutSideBorders(tableLines, String.valueOf(line.getLineNumber()), font);
                    addCellWithoutSideBorders(tableLines, line.getPickupNumber(), font);
//                    addCellWithoutSideBorders(tableLines, line.getItemCode(), font);
                    addCellWithoutSideBorders(tableLines, line.getDescription(), font);  // **✅ Moved Here**
//                    addCellWithoutSideBorders(tableLines, line.getPickedStorageBin(), font);
//                addCellWithoutSideBorders(tableLines, line.getPickedPackCode(), font);
                    addCellWithoutSideBorders(tableLines, line.getAssignedPickerId(), font);
                    addCellWithoutSideBorders(tableLines, String.valueOf(line.getOrderedQty()), font);
                    addCellWithoutSideBorders(tableLines, String.valueOf(line.getShippedQty()), font);
                    document.add(tableLines);
                }
            }



            // ✅ Add **Lines Table** Only If It Has Data
//            if (!header.getLines().isEmpty()) {
//                document.add(tableLines);
//            }

//            document.newPage(); // Page break after each partner
//        }
    }
}

    private void writeTableLineHeader(PdfPTable table) throws DocumentException, IOException {

//        PdfPTable table = new PdfPTable(8); // Adjusted to 8 columns
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(5f);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(9);

        PdfPCell cell;

        cell = new PdfPCell(new Phrase("LineNo", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("PickupNo", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

//        cell = new PdfPCell(new Phrase("ItemCode", fontSubTitle));
//        cell.setBorder(0);
//        cell.setPaddingBottom(5);
//        cell.setBorderWidthBottom(1);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Description", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

//        cell = new PdfPCell(new Phrase("PickedBin", fontSubTitle));
//        cell.setBorder(0);
//        cell.setPaddingBottom(5);
//        cell.setBorderWidthBottom(1);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(cell);

        cell = new PdfPCell(new Phrase("PickerID", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("OrderedQty", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("PickedQty", fontSubTitle));
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // ✅ Ensure the header row is repeated on every page
//        table.setHeaderRows(1);
    }





    private void writeTableHeaderData(PdfPTable table, PickerDenialHeader header, Document document) {

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

        addCellWithoutSideBorders(table, header.getSDenialDate(), font);   // Denial Date
        addCellWithoutSideBorders(table, header.getSDeliveryDate(), font); // Delivery Date Time
        addCellWithoutSideBorders(table, header.getPartnerCode(), font);   // Branch Code
        addCellWithoutSideBorders(table, header.getOrderType(), font);     // Order Type
        addCellWithoutSideBorders(table, header.getPreOutboundNo(), font); // # S.O
        addCellWithoutSideBorders(table, String.valueOf(header.getSkuOrdered()), font);  // # SKUs Ordered
        addCellWithoutSideBorders(table, String.valueOf(header.getSkuShipped()), font);  // # SKUs Shipped
        addCellWithoutSideBorders(table, String.valueOf(header.getSkuDenied()), font);   // # SKU Denied
        addCellWithoutSideBorders(table, String.valueOf(header.getOrderedQty()), font);  // Ordered Qty
        addCellWithoutSideBorders(table, String.valueOf(header.getShippedQty()), font);  // Shipped Qty
        addCellWithoutSideBorders(table, String.valueOf(header.getPercentageShipped()), font); // % Shipped

        document.add(table);
//        writeTableLineData(header, document);
    }

    private void writeTableLineData(PickerDenialHeader header, Document document) {

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

        PdfPTable table2 = new PdfPTable(8);
        table2.setWidthPercentage(100f);
        table2.setWidths(new float[] {5.5f, 3.0f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f});

        for (PickerDenialReportImpl line : header.getLines()) {
            addCellWithoutSideBorders(table2,line.getWarehouseId(), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getLineNumber()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getPickupNumber()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getItemCode()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getPickedPackCode()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getDescription()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getAssignedPickerId()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getOrderedQty()), font);
            addCellWithoutSideBorders(table2, String.valueOf(line.getShippedQty()), font);
        }

        document.add(table2);
    }

    /**
     * Adds a cell to the table with only a bottom border (removes left and right borders).
     */
    private void addCellWithoutSideBorders(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderWidthLeft(0);  // Remove left border
        cell.setBorderWidthRight(0); // Remove right border
        cell.setBorder(PdfPCell.BOTTOM); // Keep only bottom border
        cell.setPaddingBottom(10);
        table.addCell(cell);
    }

    private void addPageHeader(Document document, SearchPickupLine searchPickupLine, Date startOrderDate, String partnerCode)
            throws DocumentException, IOException {
        // Define Fonts
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(12);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(10);

        // Define Paragraphs
        Paragraph title = new Paragraph("Denial Report", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(5);

        // Dynamic Subtitle with Partner Code
        Paragraph subtitle = new Paragraph(partnerCode, fontSubTitle);
        subtitle.setAlignment(Paragraph.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);

        // Format Selection Date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        // Ensure startOrderDate and endOrderDate are provided
        String formattedStartDate = (startOrderDate != null) ? dateFormatter.format(startOrderDate) : "N/A";
        String formattedEndDate = (searchPickupLine.getToPickConfirmedOn() != null) ? dateFormatter.format(searchPickupLine.getToPickConfirmedOn()) : "N/A";

        // Format as required: "03-02-2025 : 02:00 PM - 04-02-2025 : 01:59 PM"
        String selectionDateFormatted = formattedStartDate + " - " + formattedEndDate;

        Paragraph dateInfo = new Paragraph("Selection Date:\n" + selectionDateFormatted, fontParagraph);
        dateInfo.setAlignment(Paragraph.ALIGN_RIGHT);
        dateInfo.setSpacingBefore(10);

        // Add to Document
        document.add(dateInfo);
        document.add(title);
        document.add(subtitle);

    }

    private void addPageHeader2(Document document, SearchPickupLine searchPickupLine, Date startOrderDate)
            throws DocumentException, IOException {
        // Define Fonts
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(10);

        // Define Paragraphs
        Paragraph title = new Paragraph("Denial Report", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(5);

        // Format Selection Date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        // Ensure startOrderDate and endOrderDate are provided
        String formattedStartDate = (startOrderDate != null) ? dateFormatter.format(startOrderDate) : "N/A";
        String formattedEndDate = (searchPickupLine.getToPickConfirmedOn() != null) ? dateFormatter.format(searchPickupLine.getToPickConfirmedOn()) : "N/A";

        // Format as required: "03-02-2025 : 02:00 PM - 04-02-2025 : 01:59 PM"
        String selectionDateFormatted = formattedStartDate + " - " + formattedEndDate;

        Paragraph dateInfo = new Paragraph("Selection Date:\n" + selectionDateFormatted, fontParagraph);
        dateInfo.setAlignment(Paragraph.ALIGN_RIGHT);
        dateInfo.setSpacingBefore(10);

        // Add to Document
        document.add(dateInfo);
        document.add(title);

    }

    private void addPartnerCodeHeader(Document document, String partnerCode)
            throws DocumentException, IOException {

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(12);

        // Dynamic Subtitle with Partner Code
        Paragraph subtitle = new Paragraph(partnerCode, fontSubTitle);
        subtitle.setAlignment(Paragraph.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);

        // Add to Document
        document.add(subtitle);
    }

    // Method to add header to the second page and all subsequent pages
    private void addReportSummarypageHeader(Document document) throws DocumentException, IOException {
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubtitle.setSize(12);

        // Adding logo
        String imageFile = propertiesConfig.getLogo();
        Image image = Image.getInstance(imageFile);
        image.scaleToFit(100, 1000);
        image.setAbsolutePosition(30, 780);
        document.add(image);

        // Report Title
        Paragraph denialReport = new Paragraph("Denial Report", fontTitle);
        denialReport.setAlignment(Paragraph.ALIGN_CENTER);
        denialReport.setSpacingBefore(20);
        document.add(denialReport);

        // Selection Date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String formattedStartDate = dateFormatter.format(new Date());
        String formattedEndDate = dateFormatter.format(new Date());

        Paragraph dateInfo = new Paragraph("Selection Date: " + formattedStartDate + " - " + formattedEndDate, fontSubtitle);
        dateInfo.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(dateInfo);

        // Adding Report Summary Header
        Paragraph reportSummary = new Paragraph("Report Summary", fontSubtitle);
        reportSummary.setSpacingBefore(10);
        document.add(reportSummary);
    }
    private void writeSummaryTableHeader(PdfPTable table2) throws IOException {

        PdfPCell cell = new PdfPCell();
        cell.setBorder(1);
//		cell.setPaddingBottom(5);
        cell.setBorderWidthTop(1);
        cell.setBorderWidthBottom(1);
        cell.setBorderWidthLeft(1);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(8.5f);
        font.setStyle(Font.BOLD);

        cell.setPhrase(new Phrase("Location", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("Type", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("# SKU Out \nof Stock", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("# SKU \nShort Qty", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("# SKU \nDamage", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("# SKU Aisle \nBlock", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("# SKU Non - \nPack Qty", font));
        table2.addCell(cell);

        cell.setPhrase(new Phrase("Total SKU", font));
        cell.setBorderWidthRight(1);
        table2.addCell(cell);

    }

    private void writeSummaryTableData(PdfPTable table2, PickerDenialReport report, Document document)
            throws DocumentException, IOException {

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

        // Ensure the summary list is not empty
        if (report.getSummaryList().isEmpty()) {
            System.out.println("No summary data found!");
            return;
        }

        System.out.println("Summary Data Found: " + report.getSummaryList().size());

        // Write table header
        writeSummaryTableHeader(table2);

        for (PickerDenialSummary summary : report.getSummaryList()) {
            // Extract summary details
            String partnerCode = summary.getPartnerCode();
            String partnerName = summary.getPartnerName();
            String orderType = summary.getOrderType();
            Long totalSKU = summary.getTotalSKU();

            // Initialize denial count variables
            int outOfStock = 0, shortQty = 0, damage = 0, aisleBlock = 0, nonPackQty = 0;

            // Add data to the table
//            table2.addCell(new PdfPCell(new Phrase(partnerCode +"-"+partnerName, font)));  // Location (Partner Name)
//            table2.addCell(new PdfPCell(new Phrase(orderType, font)));    // Type (Order Type)
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(outOfStock), font)));  // # SKU Out of Stock
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(shortQty), font)));    // # SKU Short Qty
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(damage), font)));      // # SKU Damage
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(aisleBlock), font)));  // # SKU Aisle Block
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(nonPackQty), font)));  // # SKU Non-Pack Qty
//            table2.addCell(new PdfPCell(new Phrase(String.valueOf(totalSKU), font)));    // Total SKU
//
            table2.addCell(createBorderedCell(partnerCode + " - " + partnerName, font));  // Location (Partner Name)
            table2.addCell(createBorderedCell(orderType, font));                          // Type (Order Type)
            table2.addCell(createBorderedCell(String.valueOf(outOfStock), font));         // # SKU Out of Stock
            table2.addCell(createBorderedCell(String.valueOf(shortQty), font));           // # SKU Short Qty
            table2.addCell(createBorderedCell(String.valueOf(damage), font));             // # SKU Damage
            table2.addCell(createBorderedCell(String.valueOf(aisleBlock), font));         // # SKU Aisle Block
            table2.addCell(createBorderedCell(String.valueOf(nonPackQty), font));         // # SKU Non-Pack Qty
            table2.addCell(createBorderedCell(String.valueOf(totalSKU), font));

        }

        // Add summary table to document
        document.add(table2);
    }

    /**
     * Creates a table cell with borders and height adjustments.
     */
    private PdfPCell createBorderedCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(8);  // Adjusts padding for spacing
        cell.setBorder(Rectangle.BOX); // Full border
        cell.setMinimumHeight(25f); // Adjust height to ensure uniformity
        return cell;
    }

}

package com.tekclover.wms.api.idmaster.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.model.outboundheader.PreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.outboundheader.SearchPreOutboundHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class ReportService {

    @Autowired
    PropertiesConfig propertiesConfig;

    //Email PDF Generate
    public void exportEmail(OutputStream response, PreOutboundHeader[] preOutboundHeaders, SearchPreOutboundHeader searchPreOutboundHeader, Date startOrderDate) throws IOException, DocumentException, ParseException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(12);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(105f);
        table.setWidths(new float[] {2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 3.0f});
        table.setSpacingBefore(10);

        Paragraph paragraph = new Paragraph("WMS Daily Order Report", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        paragraph.setSpacingAfter(3.0f);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(8);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy h:mm a");

        String formattedStartDate = startOrderDate != null ? dateFormatter.format(startOrderDate) : "N/A";
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
        writeTableData(table, preOutboundHeaders, document, image, searchPreOutboundHeader, startOrderDate);

        document.add(table);

        document.close();
    }

    private void writeTableHeader(PdfPTable table) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPaddingBottom(5);
        cell.setBorderWidthBottom(1);

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

    private void writeTableData(PdfPTable table, PreOutboundHeader[] preOutboundHeaders, Document document, Image image, SearchPreOutboundHeader searchPreOutboundHeader, Date startOrderDate) throws ParseException, DocumentException, IOException {

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(8.5f);
        font.setStyle(Font.NORMAL);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        int rowCount = 0; // Track the number of rows
        int maxRowsPerPage = 30; // Define the maximum number of rows per page


        for (PreOutboundHeader header : preOutboundHeaders) {

            PdfPCell cell = new PdfPCell();
            cell.setBorder(0);
            cell.setPaddingBottom(10);
            cell.setBorderWidthBottom(1);

            String formattedOrderDate = dateFormatWithTime.format(header.getRefDocDate());
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
                addPageHeader(document, searchPreOutboundHeader, startOrderDate);

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

    private void addPageHeader(Document document, SearchPreOutboundHeader searchPreOutboundHeader, Date startOrderDate) throws DocumentException, IOException {
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(15);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(8);

        Paragraph paragraph = new Paragraph("WMS Daily Order Report", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy h:mm a");

        String formattedStartDate = startOrderDate != null ? dateFormatter.format(startOrderDate) : "N/A";
        String formattedEndDate = searchPreOutboundHeader.getEndOrderDate() != null ? dateFormatter.format(searchPreOutboundHeader.getEndOrderDate()) : "N/A";
        String formattedRunDate = searchPreOutboundHeader.getRunDate() != null ? dateFormatter.format(searchPreOutboundHeader.getRunDate()) : "N/A";

        Paragraph paragraph2 = new Paragraph("Selection Date: " + formattedStartDate + " - "  + formattedEndDate , fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
        Paragraph paragraph3 = new Paragraph("Run Date: " + formattedRunDate, fontParagraph);
        paragraph3.setAlignment(Paragraph.ALIGN_RIGHT);

        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph);
    }


}

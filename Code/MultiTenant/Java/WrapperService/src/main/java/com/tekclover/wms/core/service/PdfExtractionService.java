package com.tekclover.wms.core.service;

import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.pdfextract.InvoiceData;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PdfExtractionService {


    /**
     * Extract text directly from uploaded MultipartFile
     */
    public InvoiceData extractText(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("Uploaded file is empty.");
        }

        // Always use stream for uploads (do NOT convert to File)
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream, MemoryUsageSetting.setupMixed(50 * 1024 * 1024))) {

            if (document.isEncrypted()) {
                throw new IOException("Encrypted PDFs are not supported.");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            // Maintain natural reading order
            stripper.setSortByPosition(true);
            stripper.setAddMoreFormatting(false);

            String extracted = stripper.getText(document);

            return parseInvoice(extracted);
        }
    }

    //=====================================Helper Methods=======================================//

    public InvoiceData parseInvoice(String rawText) {

        String text = normalize(rawText);   // MUST normalize first

        log.info("RAW NORMALIZED TEXT: {}", text);

        InvoiceData data = new InvoiceData();

        // -----------------------------
        // Extract Invoice Number + Date (dynamic, tolerant of label/value row-swap)
        // -----------------------------

        // Invoice No: skip up to 4 pure-alpha words (e.g. "Date") before the real value
        Pattern invNoPattern = Pattern.compile(
                "Invoice\\s*No\\.?\\s*(?:[A-Za-z]{2,20}\\s+){0,4}?([A-Z]{1,8}\\d{2,})"
        );
        Matcher invNoMatcher = invNoPattern.matcher(text);
        if (invNoMatcher.find()) {
            data.setInvoiceNo(invNoMatcher.group(1));
        }

        // Date: skip up to 4 tokens (alpha OR alnum, so it can jump over "SPX0920" too)
        Pattern datePattern = Pattern.compile(
                "Date\\s*(?:[A-Za-z0-9./]{1,20}\\s+){0,4}?(\\d{2}-\\d{2}-\\d{4},?\\s*\\d{2}:\\d{2}\\s*[AP]M)"
        );
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            data.setRequiredDeliveryDate(dateMatcher.group(1));
        }

        // Last-resort fallback: this exact timestamp format is unique enough
        // that the *first* occurrence in the whole doc is almost always the invoice date.
        if (data.getRequiredDeliveryDate() == null) {
            Matcher anyDate = Pattern.compile(
                    "\\d{2}-\\d{2}-\\d{4},?\\s*\\d{2}:\\d{2}\\s*[AP]M"
            ).matcher(text);
            if (anyDate.find()) {
                data.setRequiredDeliveryDate(anyDate.group());
            }
        }

        // Last-resort fallback for invoice no: letters-then-digits pattern is safe
        // globally because item codes (170F142) and GSTINs (33DPZPB...) start with digits, not letters.
        if (data.getInvoiceNo() == null) {
            Matcher anyInvNo = Pattern.compile("\\b([A-Z]{1,8}\\d{2,})\\b").matcher(text);
            if (anyInvNo.find()) {
                data.setInvoiceNo(anyInvNo.group(1));
            }
        }

        if (data.getInvoiceNo() == null || data.getRequiredDeliveryDate() == null) {
            log.warn("Incomplete extraction — invoiceNo={}, date={}, raw len={}",
                    data.getInvoiceNo(), data.getRequiredDeliveryDate(), text.length());
        }

        // -----------------------------
        // Customer Name
        // -----------------------------
        data.setCustomerName(extractCustomerName(text));

        // -----------------------------
        // Delivery Location (Optional)
        // -----------------------------
        data.setDeliveryTo(extractDeliveryLocationV3(text));

        // -----------------------------
        // Extract Items (Robust)
        // -----------------------------
        extractItems(text, data);

        return data;
    }

    /**
     * Extract Items
     *
     * @param text
     * @param data
     */
//    private void extractItems(String text, InvoiceData data) {
//
//        // Find every HSN + Quantity occurrence
//        Pattern hsnQtyPattern = Pattern.compile("(\\d{8})\\s+(\\d+)");
//
//        Matcher matcher = hsnQtyPattern.matcher(text);
//
//        while (matcher.find()) {
//
//            int searchStart = Math.max(0, matcher.start() - 80);
//            String window = text.substring(searchStart, matcher.start());
//
//            // Look backwards for item code (last alphanumeric token before HSN)
    ////            Matcher codeMatcher = Pattern.compile("([-Z0-9]{5,})\\s*$").matcher(window);
//            Matcher codeMatcher = Pattern.compile("([A-Z0-9][A-Z0-9\\-/]{3,})\\s*$").matcher(window);
//
//            if (!codeMatcher.find()) {
//                continue;
//            }
//
//            String itemCode = codeMatcher.group(1);
//            int qty = Integer.parseInt(matcher.group(2));
//
//            // Skip known service rows
//            if (NON_ITEM_KEYWORDS.contains(itemCode)) {
//                continue;
//            }
//
//            data.getItems().add(new InvoiceData.ItemLine(itemCode, qty));
//        }
//    }

    private void extractItems(String text, InvoiceData data) {

//        Pattern hsnQtyPattern = Pattern.compile("(\\d{8})\\s+(\\d+)");
        Pattern hsnQtyPattern = Pattern.compile("\\b(\\d{8})\\b\\s+(\\d+)");
        Matcher matcher = hsnQtyPattern.matcher(text);

        while (matcher.find()) {

            int searchStart = Math.max(0, matcher.start() - 80);
            String window = text.substring(searchStart, matcher.start());

//            Matcher codeMatcher = Pattern.compile("([A-Z0-9][A-Z0-9\\-/]{3,})\\s*$").matcher(window);
            Matcher codeMatcher = Pattern.compile(
                    "([A-Z0-9][A-Z0-9\\-/]{2,})(?:\\s+([A-Z0-9]{1,3}))?\\s*$"
            ).matcher(window);

//            String itemCode = codeMatcher.find() ? codeMatcher.group(1) : null;
            String itemCode = null;
            if (codeMatcher.find()) {
                String primary = codeMatcher.group(1);
                String trailingFragment = codeMatcher.group(2);
                itemCode = (trailingFragment != null) ? primary + trailingFragment : primary;
            }
            int qty = Integer.parseInt(matcher.group(2));

            // Explicit validation: skip if itemCode is missing, blank, or too short to be real
            if (itemCode == null || itemCode.isBlank()) {
                log.warn("Skipping line — no item code found near HSN match at position {}", matcher.start());
                throw new BadRequestException("There is no ItemCode found near HSN match at position ---> " + matcher.start());
//                continue;
            }

            if (NON_ITEM_KEYWORDS.contains(itemCode)) {
                continue;
            }

            data.getItems().add(new InvoiceData.ItemLine(itemCode, qty));
        }
    }

    /**
     * Extract Delivery Location (Optional Field)
     */
    private String extractDeliveryLocation(String text) {

        Matcher matcher = Pattern.compile(
                "Delivery Location\\s+(.*?)(?=\\s+Place of supply|\\s+Bill To|\\s+\\w+:|$)"
        ).matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null; // If not present
    }

    /**
     * Extract Delivery Location (Improved Boundary Logic)
     */
    private String extractDeliveryLocationV2(String text) {

        Pattern pattern = Pattern.compile(
                "Delivery Location\\s+(.*?)\\s+(?=Invoice No|Date|Place of supply|Email:|GSTIN:|State:|Bill To|#)"
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    /**
     * Extract Delivery Location (Robust Block Parsing, skips stray "State:" line)
     */
    private String extractDeliveryLocationV3(String text) {

        Pattern blockPattern = Pattern.compile(
                "Delivery Location.*?Total Number Of Boxes(.*?)Bill To",
                Pattern.DOTALL
        );

        Matcher blockMatcher = blockPattern.matcher(text);
        if (!blockMatcher.find()) {
            return null;
        }

        String block = blockMatcher.group(1).trim();

        // Case A: block starts with a stray "State: <code>-<Name>" line (row-swap artifact).
        // A state value's shape is always "NN-CapWord" plus AT MOST one more "Cap+lowercase" word
        // (e.g. "33-Tamil Nadu"). It is never all-caps, so this won't accidentally
        // swallow a genuine delivery-location value like "KANNAMANGALAM".
        Pattern stateStripPattern = Pattern.compile(
                "^State:\\s*\\d{1,2}-[A-Za-z]+(?:\\s+[A-Z][a-z]+)?\\s+(.*)$",
                Pattern.DOTALL
        );
        Matcher stateMatcher = stateStripPattern.matcher(block);
        if (stateMatcher.find()) {
            String remainder = stateMatcher.group(1).replace("\n", " ").trim();
            return remainder.isEmpty() ? null : remainder;
        }

        // Case B: block starts with letterhead noise (GSTIN/Email/Phone/State-only) and
        // has no real value at all -- means Delivery Location was genuinely left blank
        // on this invoice (e.g. your SPX0949 sample). Don't return garbage in that case.
        if (block.matches("(?s)^(GSTIN|Email|Phone|State)\\s*[:].*")) {
            return null;
        }

        // Case C: no swap detected, block is already clean
        String cleaned = block.replace("\n", " ").trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    /**
     * Extract Customer Name
     *
     * @param text
     * @return
     */
//    private String extractCustomerName(String text) {
//
//        Matcher billToMatcher = Pattern.compile("Bill To\\s+(.*)").matcher(text);
//        if (!billToMatcher.find()) {
//            return null;
//        }
//
//        String afterBillTo = billToMatcher.group(1).trim();
//        String[] tokens = afterBillTo.split("\\s+");
//
//        StringBuilder nameBuilder = new StringBuilder();
//
//        for (String token : tokens) {
//            // Stop as soon as we hit something that isn't a "pure name" token:
//            // contains a digit, a lowercase letter, or is only punctuation.
//            if (token.matches(".*\\d.*") || token.matches(".*[a-z].*")) {
//                break;
//            }
//            if (nameBuilder.length() > 0) {
//                nameBuilder.append(" ");
//            }
//            nameBuilder.append(token);
//        }
//
//        String customer = nameBuilder.toString().trim();
//
//        // Safety net: if nothing captured (edge case), fall back to old stopword cut
//        if (customer.isEmpty()) {
//            customer = afterBillTo;
//        }
//
//        customer = customer.replaceFirst("^(?i)Ship To\\s*", "").trim();
//
//        return customer;
//    }


    /**
     * Extract Customer Name using line structure (not character case).
     * Every vendor puts "Bill To" on its own line, followed immediately by
     * the customer name on the next non-empty line. Some vendors jam the
     * address onto that same line separated by a comma -- in that case the
     * name is just the text before the first comma.
     */
    private String extractCustomerName(String rawText) {

        String[] lines = rawText.replace("\r", "").split("\n");

        int billToIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().matches("(?i)^Bill\\s*To\\s*:?\\s*$")) {
                billToIndex = i;
                break;
            }
        }

        String nameLine;

        if (billToIndex != -1) {
            // Standard case: "Bill To" alone on its own line; name is the next non-empty line
            nameLine = null;
            for (int i = billToIndex + 1; i < lines.length; i++) {
                String candidate = lines[i].trim();
                if (!candidate.isEmpty()) {
                    nameLine = candidate;
                    break;
                }
            }
        } else {
            // Fallback: "Bill To" and the name might be on the same line, e.g. "Bill To NISI TRADERS"
            nameLine = null;
            for (String line : lines) {
                Matcher m = Pattern.compile("(?i)Bill\\s*To\\s*:?\\s*(.+)").matcher(line.trim());
                if (m.find() && !m.group(1).isBlank()) {
                    nameLine = m.group(1).trim();
                    break;
                }
            }
        }

        if (nameLine == null) {
            return null;
        }

        return cleanNameLine(nameLine);
    }

    /**
     * Given the raw name line, trim it down to just the customer name:
     * - If address is jammed on the same line via a comma, cut at the first comma.
     * - As a final safety net, cut at the first token containing a digit
     *   (covers a stray phone number with no comma separator).
     */
    private String cleanNameLine(String line) {

        String candidate = line.trim();

        int commaIdx = candidate.indexOf(',');
        if (commaIdx != -1) {
            candidate = candidate.substring(0, commaIdx).trim();
        }

        String[] tokens = candidate.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (token.matches(".*\\d.*")) {
                break;
            }
            if (sb.length() > 0) sb.append(" ");
            sb.append(token);
        }

        String result = sb.toString().trim();
        return result.isEmpty() ? candidate : result;
    }

    private String normalize(String text) {
        return text.replace("\r", " ")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static final Set<String> NON_ITEM_KEYWORDS = Set.of(
            "FORWARDING",
            "FREIGHT",
            "TRANSPORT",
            "PACKING",
            "HANDLING",
            "DELIVERY",
            "LABOUR",
            "SERVICE",
            "CHARGES"
    );
}

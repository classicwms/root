package com.tekclover.wms.api.enterprise.service;

import com.tekclover.wms.api.enterprise.config.PropertiesConfig;
import com.tekclover.wms.api.enterprise.model.auth.AuthToken;
import com.tekclover.wms.api.enterprise.model.dto.UpdateOutboundHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PDFApacheExtractionService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getTransactionServiceApiUrl() {
        return propertiesConfig.getTransactionServiceUrl();
    }

    /**
     *
     * @param filePath
     * @param loginUserId
     * @return
     * @throws IOException
     */
    public void pdfExtractDetails(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String preOutboundNo, String filePath, String loginUserId) throws Exception {
        filePath = propertiesConfig.getDocStorageBasePath() + filePath;
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setSortByPosition(true);
            UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
            String pdfContent = pdfTextStripper.getText(document);

            // Define regex to extract "Ship To" block
            String billToRegex = "(?i)Bill To\\s*\\n?(Name\\s*:\\s*(.+))?\\n?(Address\\s*:\\s*(.+))?";
            String invoiceNoRegex = "(?i)Invoice No.\\s*:\\s*(\\S+)";
            String transportNameRegex = "(?i)Transport Name\\s*:\\s*(.+)";

            String invoiceNo = extractField(pdfContent, invoiceNoRegex);
            String transportName = extractField(pdfContent, transportNameRegex);

            String[] lines = pdfContent.split("\\r?\\n");
            String keyword = "Ship To";
            String[] shipTo = extractFields(lines, keyword);
            if(shipTo != null && shipTo.length > 1) {
                updateOutboundHeader.setPartyName(shipTo[0]);
                updateOutboundHeader.setPartyLocation(shipTo[1]);
            } else {
                keyword = "Bill To";
                String[] billTo = extractFields(lines, keyword);
                if(billTo != null && billTo.length > 1) {
                    updateOutboundHeader.setPartyName(billTo[0]);
                    updateOutboundHeader.setPartyLocation(billTo[1]);
                }
            }

            updateOutboundHeader.setCompanyCodeId(companyCodeId);
            updateOutboundHeader.setPlantId(plantId);
            updateOutboundHeader.setLanguageId(languageId);
            updateOutboundHeader.setWarehouseId(warehouseId);
            updateOutboundHeader.setPreOutboundNo(preOutboundNo);
            updateOutboundHeader.setLoginUserId(loginUserId);
            updateOutboundHeader.setInvoiceNumber(invoiceNo);
            updateOutboundHeader.setTransportName(transportName);
            log.info("updateOutboundHeader : " + updateOutboundHeader);

            //Update the desired fields in outbound header
            updateOutboundHeader(updateOutboundHeader);

        } catch (Exception e) {
            log.error("Exception while PDF Extraction! ");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param content
     * @param regex
     * @return
     */
    private String[] extractDetails(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String name = matcher.group(2) != null ? matcher.group(2).trim() : null;
            String address = matcher.group(4) != null ? matcher.group(4).trim() : null;

            return new String[]{name, address};
        }
        return null;
    }

    /**
     *
     * @param content
     * @param regex
     * @return
     */
    private String extractField(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     *
     * @param lines
     * @param keyword
     * @return
     */
    private String[] extractFields(String[] lines, String keyword) {
        boolean keywordFound = false;
        boolean nameFound = false;
        boolean addressFound = false;
        boolean stateFound = false;
        String nameRegex = "(?i)Name\\s*:\\s*([\\w\\s&-]+)";
        String stateRegex = "(?i)State\\s*:\\s*(\\w+)";
        String stateCodeRegex = "(?i)State Code\\s*:\\s*(\\d+)";
        String addressRegex = "(?i)Address\\s*:\\s*(.+)?";

        String nameKeyword = "Name";
        String stateKeyword = "State";
        String addressKeyword = "Address";
        List<String> addressValidation = Arrays.asList("Invoice", "E-Way", "Mode", "Delivery", "Supplier", "Buyer", "Order", "Dispatch", "LR", "Transport");

        String name = null;
        String state = null;
        String stateCode = null;
        String address = null;
        String tempAddress = null;
        String[] splitAddress = null;

        for (String line : lines) {
            if (line.contains(keyword)) {
                keywordFound = true;
            }
            if (!nameFound && keywordFound && line.contains(nameKeyword)) {
                name = extractField(line, nameRegex);
                nameFound = true;
            }
            if (!stateFound && keywordFound) {
                if (line.contains(addressKeyword) || addressFound) {
                    if (!addressFound) {
                        addressFound = true;
                        address = extractField(line, addressRegex);
                        splitAddress = address.split("\\s+");
                    } else if(!stateFound && !line.contains(stateKeyword)) {
                        splitAddress = line.split("\\s+");
                    }
                    if(stateFound) {
                        break;
                    }
                    if (splitAddress != null && splitAddress.length > 0) {
                        for (String add : splitAddress) {
                            boolean isPresent = addressValidation.stream().anyMatch(n -> n.equalsIgnoreCase(add));
                            if (!isPresent) {
                                tempAddress = tempAddress != null ? tempAddress + " " + add : add;
                            } else {
                                splitAddress = null;
                                break;
                            }
                        }
                    }
                }
            }
            if (!stateFound && keywordFound && line.contains(stateKeyword)) {
                state = extractField(line, stateRegex);
                stateCode = extractField(line, stateCodeRegex);
                stateFound = true;
            }
        }
        address = tempAddress + ", " + state + ", " + stateCode;
        return new String[]{name, address};
    }

    /**
     *
     * @param updateOutboundHeader
     */
    private void updateOutboundHeader (UpdateOutboundHeader updateOutboundHeader) {
        try {
            AuthToken transactionAuthToken = authTokenService.getTransactionServiceAuthToken();
            String authToken = transactionAuthToken.getAccess_token();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            HttpEntity<?> entity = new HttpEntity<>(updateOutboundHeader, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getTransactionServiceApiUrl() + "outboundheader/v2/consignmentSlip");

            ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            log.info("result : " + result.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
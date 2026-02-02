package com.tekclover.wms.api.inbound.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.model.dto.SAPData;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ODataService {

    @Autowired
    PropertiesConfig propertiesConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    private String ODATA_URL;
    private String USERNAME;
    private String PASSWORD;

    @PostConstruct
    public void init() {
        this.ODATA_URL = propertiesConfig.getOdataUrl();
        this.USERNAME = propertiesConfig.getOdataUserName();
        this.PASSWORD = propertiesConfig.getOdataPassword();
    }

//    private String ODATA_URL = propertiesConfig.getOdataUrl();
//    private String USERNAME = propertiesConfig.getOdataUserName();
//    private String PASSWORD = propertiesConfig.getOdataPassword();
//    private static final String ODATA_URL = "https://walkdevapp1.wihad.in:44300/sap/opu/odata/sap/ZWMS_GR_SRV/ZWMS_GR_HRDSet";
//    private static final String USERNAME = "WMS_API";
//    private static final String PASSWORD = "WMS76#@22^5ds";

//    public String postODataRequest(String exidv, String vbeln, String lgmng, String flag) {
//        // Prepare the request body
//        Map<String, Object> requestBody = new HashMap<>();
//        Map<String, Object> d = new HashMap<>();
//        d.put("EXIDV", exidv);
//        d.put("MESSAGE", "");
//        d.put("VBELN", vbeln);
//        d.put("LGMNG", lgmng);
//        d.put("FLAG", flag);
//        requestBody.put("d", d);
//
//        // Encode credentials for Basic Authentication
//        String auth = USERNAME + ":" + PASSWORD;
//        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//        String authHeader = "Basic " + encodedAuth;
//
//        // Set headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//        headers.add("x-requested-with", "X");
//        headers.add("Authorization", authHeader);
    ////        headers.add("Cookie", "MYSAPSSO2=<your-cookie-value-here>"); // If session-based authentication is needed
//
//       System.out.println("REQUEST -- > "+ requestBody);
//
//        // Create request entity
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Send POST request
//        ResponseEntity<String> response = restTemplate.exchange(ODATA_URL, HttpMethod.POST, requestEntity, String.class);
//
//        System.out.println("RESPONSE -- > "+ response);
//
//
//        if (response.getStatusCode() == HttpStatus.CREATED) {
//            try {
//                // Parse JSON response
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode root = objectMapper.readTree(response.getBody());
//                JsonNode data = root.path("d");
//
//                // Extract values
//                String responseExidv = data.path("EXIDV").asText();
//                String responseMessage = data.path("MESSAGE").asText();
//                String responseVbeln = data.path("VBELN").asText();
//                String responseLgmng = data.path("LGMNG").asText();
//                String responseFlag = data.path("FLAG").asText();
//
//                return "0";
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "Error parsing response: " + e.getMessage();
//            }
//        }
//        else
//            return "1";
//
//    }

//    public String postODataRequest(String exidv, String vbeln, String lgmng, String flag) throws JsonProcessingException {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("VBELN", vbeln);
//
//        Map<String, Object> innerItem = new HashMap<>();
//        innerItem.put("EXIDV", exidv);
//        innerItem.put("MESSAGE", "");
//        innerItem.put("VBELN", vbeln); // or different VBELN if needed
//        innerItem.put("LGMNG", lgmng);
//        innerItem.put("FLAG", flag);
//
//        List<Map<String, Object>> zwmsGrPostSet = new ArrayList<>();
//        zwmsGrPostSet.add(innerItem);
//
//        requestBody.put("ZWMS_GR_POSTSet", zwmsGrPostSet);
//
//        // Encode credentials for Basic Authentication
//        String auth = USERNAME + ":" + PASSWORD; // use exact base64 string from Postman if needed
//        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//        String authHeader = "Basic " + encodedAuth;
//
//        // Set headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//        headers.add("x-requested-with", "X");
//        headers.add("Authorization", authHeader);
//
//        //  Add SAP session cookie (important)
//        headers.add("Cookie", "MYSAPSSO2=AjQxMDMBABhUAEUASwBDAEwATwBWAEUAXwBEAEUAVgACAAY0ADAAMAADABBXAEQARQAgACAAIAAgACAABAAYMgAwADIANQAwADQAMAA3ADAAOAAxADcABQAEAAAACAYAAlgACQACRQD%2fAPwwgfkGCSqGSIb3DQEHAqCB6zCB6AIBATELMAkGBSsOAwIaBQAwCwYJKoZIhvcNAQcBMYHIMIHFAgEBMBowDjEMMAoGA1UEAxMDV1BTAggKICMECAlAATAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMjUwNDA3MDgxNzA4WjAjBgkqhkiG9w0BCQQxFgQUUYDumTkrTiB1CknRYLzOy5obhikwCQYHKoZIzjgEAwQvMC0CFE2mbO1ZxQHQSolTg367T8m3Kz80AhUA7koyi2tSa9sEXAFtnFpD5i%2frpxI%3d; TS01e63acc=012988a6d6fd1f1c324e3c3ee81646766c0544cd429397a17d9ffbf68e1cc7be1629a9c5def76ec8daf86f84ca9fd7aafb16c5fbfe; visid_incap_3199672=DPIzUcR+QzeTs5sHeHPEQAfJ7GcAAAAAQUIPAAAAAACRKL4I/IjpXz5fLNwsScfO; 636903=SMhJas2lORNoD/O/jx8C4pKfVDHpaDnbmryEn2Wl6GB9xQ2RdE0AQ10m3zdk81BA09Ofa3OdaJTLrZbFzwMwZCNHoFWA3Ab6ytH/Ku8n6mkzcmcCXSgJpvlsqzFNuveSN30vffWxYPjfHGqOJdLptf4GbNqAuLnDOwm0YGBARaaxu/3d; SAP_SESSIONID_WDE_400=jooQh987qpf11cNropXXTb0ZavcTkBHwtXrN9oJhntA%3d; TS01dc4fc6=012988a6d68bbef26db7bbd622ff4d9b682f44c30e27cd44774906c90e5e89420cb204b6c299b9a6c6b8df227026c3207216031230; sap-usercontext=sap-client=400"); // Paste full cookie value from working Postman request
//
//        // Log request
//        System.out.println("REQUEST --> " + new ObjectMapper().writeValueAsString(requestBody));
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Use the correct endpoint (ZWMS_GR_HRDSet)
//        ResponseEntity<String> response = restTemplate.exchange(
//                ODATA_URL,
//                HttpMethod.POST,
//                requestEntity,
//                String.class
//        );
//
//        System.out.println("RESPONSE --> " + response);
//
//        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
//            return "0";
//        } else {
//            return "1";
//        }
//    }


    public String postODataRequest(List<StagingLineEntityV2> entities, String vbeln, String lgmng, String flag) throws JsonProcessingException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("VBELN", vbeln);

        List<Map<String, Object>> zwmsGrPostSet = entities.stream().map(entity -> {
            Map<String, Object> item = new HashMap<>();
            item.put("MESSAGE", "");
            item.put("VBELN", vbeln);
            item.put("LGMNG", lgmng);
            item.put("FLAG", flag);
            item.put("EXIDV", entity.getBarcodeId());
            return item;
        }).collect(Collectors.toList());

        requestBody.put("ZWMS_GR_POSTSet", zwmsGrPostSet);

        // Encode credentials for Basic Authentication
        String auth = USERNAME + ":" + PASSWORD; // use exact base64 string from Postman if needed
        System.out.print("USERNAME - " + USERNAME + " PASSWORD - " + PASSWORD);
//        String auth = "";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        System.out.println("AuthHeader --> " + authHeader);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("x-requested-with", "X");
        headers.add("Authorization", authHeader);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("REQUEST --> " + new ObjectMapper().writeValueAsString(requestBody));

        ResponseEntity<String> response = restTemplate.exchange(
                ODATA_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("RESPONSE --> " + response);

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            return "0";
        } else {
            return "1";
        }
    }


    public String postODataRequest1(List<SAPData> entities, String vbeln, String lgmng, String flag) throws JsonProcessingException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("VBELN", vbeln);

        List<Map<String, Object>> zwmsGrPostSet = entities.stream().map(entity -> {
            Map<String, Object> item = new HashMap<>();
            item.put("MESSAGE", "");
            item.put("VBELN", vbeln);
            item.put("LGMNG", lgmng);
            item.put("FLAG", flag);
            item.put("EXIDV", entity.getBarcodeId());
            return item;
        }).collect(Collectors.toList());

        requestBody.put("ZWMS_GR_POSTSet", zwmsGrPostSet);

        // Encode credentials for Basic Authentication
        String auth = USERNAME + ":" + PASSWORD; // use exact base64 string from Postman if needed
//        String auth = "";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        System.out.println("AuthHeader --> " + authHeader);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("x-requested-with", "X");
        headers.add("Authorization", authHeader);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("REQUEST --> " + new ObjectMapper().writeValueAsString(requestBody));

        ResponseEntity<String> response = restTemplate.exchange(
                ODATA_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("RESPONSE --> " + response);

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            return "0";
        } else {
            return "1";
        }
    }
}

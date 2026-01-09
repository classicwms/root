package com.tekclover.wms.core.service;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.dto.*;
import com.tekclover.wms.core.model.masters.*;
import com.tekclover.wms.core.model.threepl.*;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.core.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.core.model.warehouse.inbound.ASN;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.inbound.walkaroo.ReversalV3;
import com.tekclover.wms.core.model.warehouse.outbound.OutboundIntegrationHeaderV2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationSAP;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationV3;
import com.tekclover.wms.core.util.CommonUtils;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service

public class OutboundTransactionService {

        @Autowired
        PropertiesConfig propertiesConfig;

        @Autowired
        JdbcTemplate jdbcTemplate;

    @Autowired
    AuthTokenService authTokenService;

        @Autowired
        private NamedParameterJdbcTemplate jdbcParamTemplate;

        /**
         * @return
         */
        private RestTemplate getRestTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate;
        }


         private String getOutboundTransactionServiceApiUrl() {
            return propertiesConfig.getOutboundTransactionServiceUrl();
    }

        // --------------------------------------------InventoryMovement------------------------------------------------------------------------
        // GET ALL
        public InventoryMovement[] getInventoryMovements(String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InventoryMovement[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, InventoryMovement[].class);
                log.info("result : " + result.getStatusCode());

                List<InventoryMovement> inventoryMovementList = new ArrayList<>();
                for (InventoryMovement inventoryMovement : result.getBody()) {

                    inventoryMovementList.add(addingTimeWithDateInventoryMovement(inventoryMovement));
                }
                return inventoryMovementList.toArray(new InventoryMovement[inventoryMovementList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public InventoryMovement addingTimeWithDateInventoryMovement(InventoryMovement inventoryMovement) throws ParseException {

            if (inventoryMovement.getCreatedOn() != null) {
                inventoryMovement.setCreatedOn(DateUtils.addTimeToDate(inventoryMovement.getCreatedOn(), 3));
            }

            return inventoryMovement;
        }

        // GET
        public InventoryMovement getInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                                      String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("movementType", movementType)
                        .queryParam("submovementType", submovementType)
                        .queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode)
                        .queryParam("batchSerialNumber", batchSerialNumber)
                        .queryParam("movementDocumentNo", movementDocumentNo);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, InventoryMovement.class);
                return addingTimeWithDateInventoryMovement(result.getBody());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findInventoryMovement
        public InventoryMovement[] findInventoryMovement(SearchInventoryMovement searchInventoryMovement, String authToken)
                throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement/findInventoryMovement");
                HttpEntity<?> entity = new HttpEntity<>(searchInventoryMovement, headers);
                ResponseEntity<InventoryMovement[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, InventoryMovement[].class);

                List<InventoryMovement> inventoryMovementList = new ArrayList<>();
                for (InventoryMovement inventoryMovement : result.getBody()) {

                    inventoryMovementList.add(addingTimeWithDateInventoryMovement(inventoryMovement));
                }
                return inventoryMovementList.toArray(new InventoryMovement[inventoryMovementList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public InventoryMovement createInventoryMovement(InventoryMovement newInventoryMovement, String loginUserID,
                                                         String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInventoryMovement, headers);
            ResponseEntity<InventoryMovement> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, InventoryMovement.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public InventoryMovement updateInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                                         String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo,
                                                         InventoryMovement modifiedInventoryMovement, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(modifiedInventoryMovement, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("movementType", movementType)
                        .queryParam("submovementType", submovementType)
                        .queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode)
                        .queryParam("batchSerialNumber", batchSerialNumber)
                        .queryParam("movementDocumentNo", movementDocumentNo).queryParam("loginUserID", loginUserID);

                ResponseEntity<InventoryMovement> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, InventoryMovement.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteInventoryMovement(String warehouseId, Long movementType, Long submovementType,
                                               String packBarcodes, String itemCode, String batchSerialNumber, String movementDocumentNo, String loginUserID,
                                               String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventorymovement/" + movementType)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("movementType", movementType)
                        .queryParam("submovementType", submovementType)
                        .queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode)
                        .queryParam("batchSerialNumber", batchSerialNumber)
                        .queryParam("movementDocumentNo", movementDocumentNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // --------------------------------------------Inventory------------------------------------------------------------------------
        // GET ALL
        public Inventory[] getInventorys(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, Inventory[].class);
                log.info("result : " + result.getStatusCode());

                List<Inventory> inventoryList = new ArrayList<>();
                for (Inventory inventory : result.getBody()) {

                    inventoryList.add(addingTimeWithDateInventory(inventory));
                }
                return inventoryList.toArray(new Inventory[inventoryList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public Inventory addingTimeWithDateInventory(Inventory inventory) throws ParseException {

            if (inventory.getCreatedOn() != null) {
                inventory.setCreatedOn(DateUtils.addTimeToDate(inventory.getCreatedOn(), 3));
            }

            if (inventory.getManufacturerDate() != null) {
                inventory.setManufacturerDate(DateUtils.addTimeToDate(inventory.getManufacturerDate(), 3));
            }

            if (inventory.getExpiryDate() != null) {
                inventory.setExpiryDate(DateUtils.addTimeToDate(inventory.getExpiryDate(), 3));
            }

            return inventory;
        }

        // GET
        public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                      Long stockTypeId, Long specialStockIndicatorId, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                        .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                        .queryParam("stockTypeId", stockTypeId)
                        .queryParam("specialStockIndicatorId", specialStockIndicatorId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Inventory.class);
                log.info("result : " + result.getStatusCode());

                return addingTimeWithDateInventory(result.getBody());

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                      String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/transfer")
                        .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Inventory.class);
                log.info("result : " + result.getStatusCode());

                return addingTimeWithDateInventory(result.getBody());

            } catch (Exception e) {
                throw e;
            }
        }

        // POST - FinderQuery
        public Inventory[] findInventory(SearchInventory searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/findInventory");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, Inventory[].class);
                log.info("result : " + result.getStatusCode());

                List<Inventory> inventoryList = new ArrayList<>();
                for (Inventory inventory : result.getBody()) {

                    inventoryList.add(addingTimeWithDateInventory(inventory));
                }
                return inventoryList.toArray(new Inventory[inventoryList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - FinderQueryNew - SQL Query
        public Inventory[] findInventoryNew(SearchInventory searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/findInventoryNew");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, Inventory[].class);
                return result.getBody();
//			log.info("result : " + result.getStatusCode());

//			List<Inventory> inventoryList = new ArrayList<>();
//			for (Inventory inventory : result.getBody()) {
//
//				inventoryList.add(addingTimeWithDateInventory(inventory));
//			}
//			return inventoryList.toArray(new Inventory[inventoryList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - FinderQuery
        public Inventory[] getQuantityValidatedInventory(SearchInventory searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/get-all-validated-inventory");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, Inventory[].class);
                log.info("result : " + result.getStatusCode());

                List<Inventory> inventoryList = new ArrayList<>();
                for (Inventory inventory : result.getBody()) {

                    inventoryList.add(addingTimeWithDateInventory(inventory));
                }
                return inventoryList.toArray(new Inventory[inventoryList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        public PaginatedResponse<Inventory> findInventory(SearchInventory searchInventory, Integer pageNo, Integer pageSize,
                                                          String sortBy, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/findInventory/pagination")
                        .queryParam("pageNo", pageNo).queryParam("pageSize", pageSize).queryParam("sortBy", sortBy);

                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);

                ParameterizedTypeReference<PaginatedResponse<Inventory>> responseType = new ParameterizedTypeReference<PaginatedResponse<Inventory>>() {
                };
                ResponseEntity<PaginatedResponse<Inventory>> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, responseType);

                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public Inventory createInventory(Inventory newInventory, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInventory, headers);
            ResponseEntity<Inventory> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    Inventory.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public Inventory updateInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                         Long stockTypeId, Long specialStockIndicatorId, Inventory modifiedInventory, String loginUserID,
                                         String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(modifiedInventory, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                        .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                        .queryParam("stockTypeId", stockTypeId)
                        .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<Inventory> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        Inventory.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                       Long stockTypeId, Long specialStockIndicatorId, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/" + stockTypeId)
                        .queryParam("warehouseId", warehouseId).queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                        .queryParam("stockTypeId", stockTypeId)
                        .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // --------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
        // GET ALL
        public InhouseTransferHeader[] getInhouseTransferHeaders(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, InhouseTransferHeader[].class);

                //Start of Comment by V.Senthil on 10-03-2024
//            List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
//            for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {
//
//                inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
//            }
//            return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
                return result.getBody();
                //End of Comment by V.Senthil on 10-03-2024


            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public InhouseTransferHeader addingTimeWithDateInhouseTransferHeader(InhouseTransferHeader inhouseTransferHeader) throws ParseException {

            if (inhouseTransferHeader.getCreatedOn() != null) {
                inhouseTransferHeader.setCreatedOn(DateUtils.addTimeToDate(inhouseTransferHeader.getCreatedOn(), 3));
            }
            if (inhouseTransferHeader.getUpdatedOn() != null) {
                inhouseTransferHeader.setUpdatedOn(DateUtils.addTimeToDate(inhouseTransferHeader.getUpdatedOn(), 3));
            }

            return inhouseTransferHeader;
        }

        // GET
        public InhouseTransferHeader getInhouseTransferHeader(String warehouseId, String transferNumber,
                                                              Long transferTypeId, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader/" + transferNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("transferNumber", transferNumber)
                        .queryParam("transferTypeId", transferTypeId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, InhouseTransferHeader.class);

                return addingTimeWithDateInhouseTransferHeader(result.getBody());

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - Find
        public InhouseTransferHeader[] findInHouseTransferHeader(SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                                 String authToken) throws ParseException {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader/findInHouseTransferHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchInHouseTransferHeader, headers);
            ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InhouseTransferHeader[].class);

            List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
            for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {

                inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
            }
            return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
        }

        // POST - Find - Stream
        public InhouseTransferHeader[] findInHouseTransferHeaderNew(SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                                    String authToken) throws ParseException {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader/findInHouseTransferHeaderNew");
            HttpEntity<?> entity = new HttpEntity<>(searchInHouseTransferHeader, headers);
            ResponseEntity<InhouseTransferHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InhouseTransferHeader[].class);

            List<InhouseTransferHeader> inhouseTransferHeaderList = new ArrayList<>();
            for (InhouseTransferHeader inhouseTransferHeader : result.getBody()) {

                inhouseTransferHeaderList.add(addingTimeWithDateInhouseTransferHeader(inhouseTransferHeader));
            }
            return inhouseTransferHeaderList.toArray(new InhouseTransferHeader[inhouseTransferHeaderList.size()]);
        }

        // POST
        public InhouseTransferHeader createInhouseTransferHeader(InhouseTransferHeader newInhouseTransferHeader,
                                                                 String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferHeader, headers);
            ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InhouseTransferHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - V2
        public InhouseTransferHeader createInhouseTransferHeaderV2(InhouseTransferHeader newInhouseTransferHeader,
                                                                   String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader/v2")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferHeader, headers);
            ResponseEntity<InhouseTransferHeader> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InhouseTransferHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - V2
        public WarehouseApiResponse createInhouseTransferUploadV2(List<InhouseTransferUpload> inhouseTransferUploadList,
                                                                  String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferheader/v2/upload")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(inhouseTransferUploadList, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // --------------------------------------------InhouseTransferHeader------------------------------------------------------------------------
        // GET ALL
        public InhouseTransferLine[] getInhouseTransferLines(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferline");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InhouseTransferLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, InhouseTransferLine[].class);
                log.info("result : " + result.getStatusCode());

                List<InhouseTransferLine> inhouseTransferLineList = new ArrayList<>();
                for (InhouseTransferLine inhouseTransferLine : result.getBody()) {

                    inhouseTransferLineList.add(addingTimeWithDateInhouseTransferLine(inhouseTransferLine));
                }
                return inhouseTransferLineList.toArray(new InhouseTransferLine[inhouseTransferLineList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public InhouseTransferLine addingTimeWithDateInhouseTransferLine(InhouseTransferLine inhouseTransferLine) throws ParseException {

            if (inhouseTransferLine.getCreatedOn() != null) {
                inhouseTransferLine.setCreatedOn(DateUtils.addTimeToDate(inhouseTransferLine.getCreatedOn(), 3));
            }
            if (inhouseTransferLine.getUpdatedOn() != null) {
                inhouseTransferLine.setUpdatedOn(DateUtils.addTimeToDate(inhouseTransferLine.getUpdatedOn(), 3));
            }
            if (inhouseTransferLine.getConfirmedOn() != null) {
                inhouseTransferLine.setConfirmedOn(DateUtils.addTimeToDate(inhouseTransferLine.getConfirmedOn(), 3));
            }
            return inhouseTransferLine;
        }

        // GET
        public InhouseTransferLine getInhouseTransferLine(String warehouseId, String transferNumber, String sourceItemCode,
                                                          String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferline/" + transferNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("transferNumber", transferNumber)
                        .queryParam("sourceItemCode", sourceItemCode);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InhouseTransferLine> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, InhouseTransferLine.class);
                log.info("result : " + result.getStatusCode());

                return addingTimeWithDateInhouseTransferLine(result.getBody());

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - Find
        public InhouseTransferLine[] findInhouseTransferLine(SearchInhouseTransferLine searchInhouseTransferLine,
                                                             String authToken) throws ParseException {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferline/findInhouseTransferLine");
            HttpEntity<?> entity = new HttpEntity<>(searchInhouseTransferLine, headers);
            ResponseEntity<InhouseTransferLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, InhouseTransferLine[].class);

            List<InhouseTransferLine> inhouseTransferLineList = new ArrayList<>();
            for (InhouseTransferLine inhouseTransferLine : result.getBody()) {

                inhouseTransferLineList.add(addingTimeWithDateInhouseTransferLine(inhouseTransferLine));
            }
            return inhouseTransferLineList.toArray(new InhouseTransferLine[inhouseTransferLineList.size()]);
        }

        // POST
        public InhouseTransferLine createInhouseTransferLine(InhouseTransferLine newInhouseTransferLine, String loginUserID,
                                                             String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inhousetransferline")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInhouseTransferLine, headers);
            ResponseEntity<InhouseTransferLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, InhouseTransferLine.class);
            return result.getBody();
        }

        /*
         * -------------PreOutboundHeader----------------------------------------
         */
        // POST - findPreOutboundHeader
        public PreOutboundHeader[] findPreOutboundHeader(SearchPreOutboundHeader searchPreOutboundHeader,
                                                         String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundheader/findPreOutboundHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundHeader, headers);
                ResponseEntity<PreOutboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PreOutboundHeader[].class);
                log.info("result : " + result.getBody());

                List<PreOutboundHeader> obList = new ArrayList<>();
                for (PreOutboundHeader obHeader : result.getBody()) {
                    log.info("Result RefDocDate :" + obHeader.getRefDocDate());
                    if (obHeader.getRefDocDate() != null) {
                        obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
                    }
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PreOutboundHeader[obList.size()]);
            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findPreOutboundHeader - Stream
        public PreOutboundHeader[] findPreOutboundHeaderNew(SearchPreOutboundHeader searchPreOutboundHeader,
                                                            String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundheader/findPreOutboundHeaderNew");
                HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundHeader, headers);
                ResponseEntity<PreOutboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PreOutboundHeader[].class);
                log.info("result : " + result.getBody());

                List<PreOutboundHeader> obList = new ArrayList<>();
                for (PreOutboundHeader obHeader : result.getBody()) {
                    log.info("Result RefDocDate :" + obHeader.getRefDocDate());
                    if (obHeader.getRefDocDate() != null) {
                        obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
                    }
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PreOutboundHeader[obList.size()]);
            } catch (Exception e) {
                throw e;
            }
        }

        // -------------------------PreOutboundLine------------------------------------------------
        public PreOutboundLine[] findPreOutboundLine(SearchPreOutboundLine searchPreOutboundLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundline/findPreOutboundLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundLine, headers);
                ResponseEntity<PreOutboundLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PreOutboundLine[].class);
                log.info("result : " + result.getStatusCode());

                List<PreOutboundLine> obList = new ArrayList<>();
                for (PreOutboundLine obHeader : result.getBody()) {
                    log.info("Result RefDocDate :" + obHeader.getRequiredDeliveryDate());
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PreOutboundLine[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // --------------------------OrderManagementLine----------------------------------------------------

        // POST - findOrderManagementLine
        public OrderManagementLine[] findOrderManagementLine(SearchOrderManagementLine searchOrderManagementLine,
                                                             String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/findOrderManagementLine");
                HttpEntity<?> entity = new HttpEntity<>(searchOrderManagementLine, headers);
                ResponseEntity<OrderManagementLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OrderManagementLine[].class);
                log.info("result : " + result.getStatusCode());

                List<OrderManagementLine> obList = new ArrayList<>();
                for (OrderManagementLine obHeader : result.getBody()) {
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getPickupCreatedOn() != null) {
                        obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
                    }
                    if (obHeader.getPickupupdatedOn() != null) {
                        obHeader.setPickupupdatedOn(DateUtils.addTimeToDate(obHeader.getPickupupdatedOn(), 3));
                    }
                    if (obHeader.getReAllocatedOn() != null) {
                        obHeader.setReAllocatedOn(DateUtils.addTimeToDate(obHeader.getReAllocatedOn(), 3));
                    }
                    if (obHeader.getPickerAssignedOn() != null) {
                        obHeader.setPickerAssignedOn(DateUtils.addTimeToDate(obHeader.getPickerAssignedOn(), 3));
                    }
                    if (obHeader.getPickerReassignedOn() != null) {
                        obHeader.setPickerReassignedOn(DateUtils.addTimeToDate(obHeader.getPickerReassignedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new OrderManagementLine[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findOrderManagementLine - Stream
        public OrderManagementLine[] findOrderManagementLineNew(SearchOrderManagementLine searchOrderManagementLine,
                                                                String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/findOrderManagementLineNew");
                HttpEntity<?> entity = new HttpEntity<>(searchOrderManagementLine, headers);
                ResponseEntity<OrderManagementLine[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OrderManagementLine[].class);
                log.info("result : " + result.getStatusCode());

                List<OrderManagementLine> obList = new ArrayList<>();
                for (OrderManagementLine obHeader : result.getBody()) {
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getPickupCreatedOn() != null) {
                        obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
                    }
                    if (obHeader.getPickupupdatedOn() != null) {
                        obHeader.setPickupupdatedOn(DateUtils.addTimeToDate(obHeader.getPickupupdatedOn(), 3));
                    }
                    if (obHeader.getReAllocatedOn() != null) {
                        obHeader.setReAllocatedOn(DateUtils.addTimeToDate(obHeader.getReAllocatedOn(), 3));
                    }
                    if (obHeader.getPickerAssignedOn() != null) {
                        obHeader.setPickerAssignedOn(DateUtils.addTimeToDate(obHeader.getPickerAssignedOn(), 3));
                    }
                    if (obHeader.getPickerReassignedOn() != null) {
                        obHeader.setPickerReassignedOn(DateUtils.addTimeToDate(obHeader.getPickerReassignedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new OrderManagementLine[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        public String updateRef9ANDRef10(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/updateRefFields");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        String.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLine doUnAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
                                                  String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackBarCode,
                                                  String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/unallocate")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackBarCode", proposedPackBarCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLine doAllocation(String warehouseId, String preOutboundNo, String refDocNumber,
                                                String partnerCode, Long lineNumber, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                HttpClient client = HttpClients.createDefault();

                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/allocate")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLine[] doAssignPicker(List<AssignPicker> assignPicker, String assignedPickerId,
                                                    String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(assignPicker, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/assignPicker")
                        .queryParam("assignedPickerId", assignedPickerId).queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLine[]> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, OrderManagementLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLine updateOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                             String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                                             String loginUserID, @Valid OrderManagementLine updateOrderMangementLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOrderMangementLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/" + refDocNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode).queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode).queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteOrderManagementLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                 String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                                 String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/" + refDocNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode).queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode).queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*--------------------------PickupHeader----------------------------------------------------*/
        // POST - Finder
        public PickupHeader[] findPickupHeader(SearchPickupHeader searchPickupHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/findPickupHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupHeader, headers);
                ResponseEntity<PickupHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickupHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<PickupHeader> obList = new ArrayList<>();
                for (PickupHeader obHeader : result.getBody()) {
                    if (obHeader.getPickupReversedOn() != null) {
                        obHeader.setPickupReversedOn(DateUtils.addTimeToDate(obHeader.getPickupReversedOn(), 3));
                    }
                    if (obHeader.getPickupCreatedOn() != null) {
                        obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
                    }
                    if (obHeader.getPickUpdatedOn() != null) {
                        obHeader.setPickUpdatedOn(DateUtils.addTimeToDate(obHeader.getPickUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PickupHeader[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findPickupHeader - Stream
        public PickupHeader[] findPickupHeaderNew(SearchPickupHeader searchPickupHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/findPickupHeaderNew");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupHeader, headers);
                ResponseEntity<PickupHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickupHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<PickupHeader> obList = new ArrayList<>();
                for (PickupHeader obHeader : result.getBody()) {
                    if (obHeader.getPickupReversedOn() != null) {
                        obHeader.setPickupReversedOn(DateUtils.addTimeToDate(obHeader.getPickupReversedOn(), 3));
                    }
                    if (obHeader.getPickupCreatedOn() != null) {
                        obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
                    }
                    if (obHeader.getPickUpdatedOn() != null) {
                        obHeader.setPickUpdatedOn(DateUtils.addTimeToDate(obHeader.getPickUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PickupHeader[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        //PickHeaderWithStatusId=48
        public PickUpHeaderReport findPickUpHeaderWithStatusId(FindPickUpHeader searchPickupHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/findPickupHeader/v2/status");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupHeader, headers);
                ResponseEntity<PickUpHeaderReport> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickUpHeaderReport.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public PickupHeader updatePickupHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID,
                                               @Valid PickupHeader updatePickupHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/" + pickupNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PickupHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PickupHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PickupHeader[] patchAssignedPickerIdInPickupHeader(String loginUserID,
                                                                  @Valid List<UpdatePickupHeader> updatePickupHeaderList, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupHeaderList, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/update-assigned-picker")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PickupHeader[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PickupHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePickupHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                          String pickupNumber, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                          String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/" + pickupNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode).queryParam("lineNumber", lineNumber)
                        .queryParam("refDocNumber", refDocNumber).queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*--------------------------PickupLine----------------------------------------------------*/
        // GET
        public Inventory[] getAdditionalBins(String warehouseId, String itemCode, Long obOrdertypeId,
                                             String proposedPackBarCode, String proposedStorageBin, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/additionalBins")
                        .queryParam("warehouseId", warehouseId).queryParam("itemCode", itemCode)
                        .queryParam("obOrdertypeId", obOrdertypeId).queryParam("proposedPackBarCode", proposedPackBarCode)
                        .queryParam("proposedStorageBin", proposedStorageBin);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Inventory[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, Inventory[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public PickupLine[] createPickupLine(@Valid List<AddPickupLine> newPickupLine, String loginUserID,
                                             String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPickupLine, headers);
            ResponseEntity<PickupLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    PickupLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - findPickupLine
        public PickupLine[] findPickupLine(SearchPickupLine searchPickupLine, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/findPickupLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupLine, headers);
                ResponseEntity<PickupLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickupLine[].class);

                List<PickupLine> pickupLineList = new ArrayList<>();
                for (PickupLine pickupLine : result.getBody()) {
                    if (pickupLine.getPickupCreatedOn() != null) {
                        pickupLine.setPickupCreatedOn(DateUtils.addTimeToDate(pickupLine.getPickupCreatedOn(), 3));
                    }
                    if (pickupLine.getPickupConfirmedOn() != null) {
                        pickupLine.setPickupConfirmedOn(DateUtils.addTimeToDate(pickupLine.getPickupConfirmedOn(), 3));
                    }
                    if (pickupLine.getPickupUpdatedOn() != null) {
                        pickupLine.setPickupUpdatedOn(DateUtils.addTimeToDate(pickupLine.getPickupUpdatedOn(), 3));
                    }
                    if (pickupLine.getPickupReversedOn() != null) {
                        pickupLine.setPickupReversedOn(DateUtils.addTimeToDate(pickupLine.getPickupReversedOn(), 3));
                    }
                    pickupLineList.add(pickupLine);
                }
                return pickupLineList.toArray(new PickupLine[pickupLineList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PickupLine updatePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                           String pickedPackCode, String actualHeNo, String loginUserID, @Valid PickupLine updatePickupLine,
                                           String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/" + actualHeNo)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("pickupNumber", pickupNumber)
                        .queryParam("itemCode", itemCode).queryParam("pickedStorageBin", pickedStorageBin)
                        .queryParam("pickedPackCode", pickedPackCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<PickupLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PickupLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                        Long lineNumber, String pickupNumber, String itemCode, String actualHeNo, String pickedStorageBin,
                                        String pickedPackCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/" + actualHeNo)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("pickupNumber", pickupNumber)
                        .queryParam("itemCode", itemCode).queryParam("pickedStorageBin", pickedStorageBin)
                        .queryParam("pickedPackCode", pickedPackCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*-----------------------------QualityHeader---------------------------------------------------------*/

        // POST - CREATE QUALITY HEADER
        public QualityHeader createQualityHeader(@Valid QualityHeader newQualityHeader, String loginUserID,
                                                 String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader")
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(newQualityHeader, headers);
                ResponseEntity<QualityHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findQualityHeader
        public QualityHeader[] findQualityHeader(SearchQualityHeader searchQualityHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/findQualityHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchQualityHeader, headers);
                ResponseEntity<QualityHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<QualityHeader> obList = new ArrayList<>();
                for (QualityHeader obHeader : result.getBody()) {
                    if (obHeader.getQualityConfirmedOn() != null) {
                        obHeader.setQualityConfirmedOn(DateUtils.addTimeToDate(obHeader.getQualityConfirmedOn(), 3));
                    }
                    if (obHeader.getQualityCreatedOn() != null) {
                        obHeader.setQualityCreatedOn(DateUtils.addTimeToDate(obHeader.getQualityCreatedOn(), 3));
                    }
                    if (obHeader.getQualityUpdatedOn() != null) {
                        obHeader.setQualityUpdatedOn(DateUtils.addTimeToDate(obHeader.getQualityUpdatedOn(), 3));
                    }
                    if (obHeader.getQualityReversedOn() != null) {
                        obHeader.setQualityReversedOn(DateUtils.addTimeToDate(obHeader.getQualityReversedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new QualityHeader[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findQualityHeader - Stream
        public QualityHeader[] findQualityHeaderNew(SearchQualityHeader searchQualityHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/findQualityHeaderNew");
                HttpEntity<?> entity = new HttpEntity<>(searchQualityHeader, headers);
                ResponseEntity<QualityHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<QualityHeader> obList = new ArrayList<>();
                for (QualityHeader obHeader : result.getBody()) {
                    if (obHeader.getQualityConfirmedOn() != null) {
                        obHeader.setQualityConfirmedOn(DateUtils.addTimeToDate(obHeader.getQualityConfirmedOn(), 3));
                    }
                    if (obHeader.getQualityCreatedOn() != null) {
                        obHeader.setQualityCreatedOn(DateUtils.addTimeToDate(obHeader.getQualityCreatedOn(), 3));
                    }
                    if (obHeader.getQualityUpdatedOn() != null) {
                        obHeader.setQualityUpdatedOn(DateUtils.addTimeToDate(obHeader.getQualityUpdatedOn(), 3));
                    }
                    if (obHeader.getQualityReversedOn() != null) {
                        obHeader.setQualityReversedOn(DateUtils.addTimeToDate(obHeader.getQualityReversedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new QualityHeader[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public QualityHeader updateQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                                 String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID,
                                                 @Valid QualityHeader updateQualityHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateQualityHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/" + qualityInspectionNo)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("pickupNumber", pickupNumber).queryParam("actualHeNo", actualHeNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<QualityHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, QualityHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID,
                                           String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/" + qualityInspectionNo)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("pickupNumber", pickupNumber).queryParam("actualHeNo", actualHeNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*-----------------------------QualityLine------------------------------------------------------------*/
        // POST - findQualityLine
        public QualityLine[] findQualityLine(SearchQualityLine searchQualityLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/findQualityLine");
                HttpEntity<?> entity = new HttpEntity<>(searchQualityLine, headers);
                ResponseEntity<QualityLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityLine[].class);
                log.info("result : " + result.getStatusCode());

                List<QualityLine> obList = new ArrayList<>();
                for (QualityLine obHeader : result.getBody()) {
                    if (obHeader.getQualityReversedOn() != null) {
                        obHeader.setQualityReversedOn(DateUtils.addTimeToDate(obHeader.getQualityReversedOn(), 3));
                    }
                    if (obHeader.getQualityCreatedOn() != null) {
                        obHeader.setQualityCreatedOn(DateUtils.addTimeToDate(obHeader.getQualityCreatedOn(), 3));
                    }
                    if (obHeader.getQualityUpdatedOn() != null) {
                        obHeader.setQualityUpdatedOn(DateUtils.addTimeToDate(obHeader.getQualityUpdatedOn(), 3));
                    }
                    if (obHeader.getQualityConfirmedOn() != null) {
                        obHeader.setQualityConfirmedOn(DateUtils.addTimeToDate(obHeader.getQualityConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new QualityLine[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public QualityLine[] createQualityLine(@Valid List<AddQualityLine> newQualityLine, String loginUserID,
                                               String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline").queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(newQualityLine, headers);
                ResponseEntity<QualityLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public QualityLine updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                             String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID,
                                             @Valid QualityLine updateQualityLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateQualityLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/" + partnerCode)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("qualityInspectionNo", qualityInspectionNo)
                        .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<QualityLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        QualityLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                         Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/" + partnerCode)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber).queryParam("qualityInspectionNo", qualityInspectionNo)
                        .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                throw e;
            }
        }

        /*
         * ----------------------OutboundHeader-----------------------------------------
         * ------------------------
         */
        // POST - findOutboundHeader
        public OutboundHeader[] findOutboundHeader(SearchOutboundHeader requestData, String authToken)
                throws ParseException {
            try {
                SearchOutboundHeaderModel requestDataForService = new SearchOutboundHeaderModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getStartDeliveryConfirmedOn() != null) {
                    if (requestData.getStartDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getStartDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getStartDeliveryConfirmedOn()));
                    }
                }
                Integer flag = 0;
                if (requestData.getEndDeliveryConfirmedOn() != null) {
                    if (requestData.getEndDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getEndDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getEndDeliveryConfirmedOn()));
                        flag = 1;
                    }
                }
                if (requestData.getStartOrderDate() != null) {
                    requestDataForService
                            .setStartOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getStartOrderDate()));
                }
                if (requestData.getEndOrderDate() != null) {
                    requestDataForService.setEndOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getEndOrderDate()));
                }
                if (requestData.getStartRequiredDeliveryDate() != null) {
                    requestDataForService.setStartRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getStartRequiredDeliveryDate()));
                }
                if (requestData.getEndRequiredDeliveryDate() != null) {
                    requestDataForService.setEndRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getEndRequiredDeliveryDate()));
                }
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/findOutboundHeader")
                        .queryParam("flag", flag);
                HttpEntity<?> entity = new HttpEntity<>(requestDataForService, headers);
                ResponseEntity<OutboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundHeader[].class);
//			log.info("result : " + result.getBody());

                List<OutboundHeader> obList = new ArrayList<>();
                for (OutboundHeader obHeader : result.getBody()) {
                    log.info("Result getDeliveryConfirmedOn :" + obHeader.getDeliveryConfirmedOn());
                    if (obHeader.getRefDocDate() != null) {
                        obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
                    }
                    if (obHeader.getRequiredDeliveryDate() != null) {
                        obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
                    }
                    if (obHeader.getDeliveryConfirmedOn() != null) {
                        obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new OutboundHeader[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findOutboundHeadernew
        public OutboundHeader[] findOutboundHeaderNew(SearchOutboundHeader requestData, String authToken)
                throws ParseException {
            try {
                SearchOutboundHeaderModel requestDataForService = new SearchOutboundHeaderModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getStartDeliveryConfirmedOn() != null) {
                    if (requestData.getStartDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getStartDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getStartDeliveryConfirmedOn()));
                    }
                }
                Integer flag = 0;
                if (requestData.getEndDeliveryConfirmedOn() != null) {
                    if (requestData.getEndDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getEndDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getEndDeliveryConfirmedOn()));
                        flag = 1;
                    }
                }
                if (requestData.getStartOrderDate() != null) {
                    requestDataForService
                            .setStartOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getStartOrderDate()));
                }
                if (requestData.getEndOrderDate() != null) {
                    requestDataForService.setEndOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getEndOrderDate()));
                }
                if (requestData.getStartRequiredDeliveryDate() != null) {
                    requestDataForService.setStartRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getStartRequiredDeliveryDate()));
                }
                if (requestData.getEndRequiredDeliveryDate() != null) {
                    requestDataForService.setEndRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getEndRequiredDeliveryDate()));
                }
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/findOutboundHeaderNew")
                        .queryParam("flag", flag);

                HttpEntity<?> entity = new HttpEntity<>(requestData, headers);
                ResponseEntity<OutboundHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundHeader[].class);
                return result.getBody();

//			List<OutboundHeader> obList = new ArrayList<>();
//			for (OutboundHeader obHeader : result.getBody()) {
//				log.info("Result getDeliveryConfirmedOn :" + obHeader.getDeliveryConfirmedOn());
//				if(obHeader.getRefDocDate() != null) {
//					obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
//				}
//				if(obHeader.getRequiredDeliveryDate() != null) {
//					obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
//				}
//				if(obHeader.getDeliveryConfirmedOn() != null) {
//					obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
//				}
//				if(obHeader.getCreatedOn() != null) {
//					obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//				}
//				if(obHeader.getUpdatedOn() != null) {
//					obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//				}
//				obList.add(obHeader);
//			}
//			return obList.toArray(new OutboundHeader[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public OutboundHeader updateOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                                   String partnerCode, @Valid OutboundHeader updateOutboundHeader, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOutboundHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/" + preOutboundNo)
                        .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<OutboundHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OutboundHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                            String partnerCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/" + preOutboundNo)
                        .queryParam("warehouseId", warehouseId).queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*
         * ----------------------OutboundLine-------------------------------------------
         * ---------------
         */
        // GET - /outboundline/delivery/orderedLines
        public Long getCountofOrderedLines(String warehouseId, String preOutboundNo, String refDocNumber,
                                           String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/delivery/orderedLines")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Long> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Long.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // GET - /outboundline/delivery/totalQuantity
        public Long getSumOfOrderedQty(String warehouseId, String preOutboundNo, String refDocNumber, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/delivery/totalQuantity")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Long> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Long.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // GET - /outboundline/delivery/deliveryLines
        public Long getDeliveryLines(String warehouseId, String preOutboundNo, String refDocNumber, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/delivery/deliveryLines")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Long> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Long.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // GET - /outboundline/delivery/confirmation
        public OutboundLine[] deliveryConfirmation(String warehouseId, String preOutboundNo, String refDocNumber,
                                                   String partnerCode, String loginUserID, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/delivery/confirmation")
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, OutboundLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findOutboundLine
        public OutboundLine[] findOutboundLine(SearchOutboundLine searchOutboundLine, String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/findOutboundLine");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<OutboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundLine[].class);
                log.info("result : " + result.getStatusCode());

                List<OutboundLine> obList = new ArrayList<>();
                for (OutboundLine obHeader : result.getBody()) {
                    if (obHeader.getDeliveryConfirmedOn() != null) {
                        obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    if (obHeader.getReversedOn() != null) {
                        obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new OutboundLine[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findOutboundLine
        public OutboundLine[] findOutboundLineNew(SearchOutboundLine searchOutboundLine, String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/findOutboundLine-new");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<OutboundLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundLine[].class);
                log.info("result : " + result.getStatusCode());

                List<OutboundLine> obList = new ArrayList<>();
                for (OutboundLine obHeader : result.getBody()) {
                    if (obHeader.getDeliveryConfirmedOn() != null) {
                        obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getUpdatedOn() != null) {
                        obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
                    }
                    if (obHeader.getReversedOn() != null) {
                        obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new OutboundLine[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - stock-movement-report-findOutboundLine
        public StockMovementReport[] findOutboundLineForStockMovement(SearchOutboundLine searchOutboundLine,
                                                                      String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/stock-movement-report/findOutboundLine");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<StockMovementReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, StockMovementReport[].class);
                log.info("result : " + result.getStatusCode());

                List<StockMovementReport> obList = new ArrayList<>();
                for (StockMovementReport obHeader : result.getBody()) {

                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new StockMovementReport[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - stock-movement-report-findOutboundLine V2
        public StockMovementReport[] findOutboundLineForStockMovementV2(SearchOutboundLineV2 searchOutboundLine,
                                                                        String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/stock-movement-report/v2/findOutboundLine");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<StockMovementReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, StockMovementReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<StockMovementReport> obList = new ArrayList<>();
//            for (StockMovementReport obHeader : result.getBody()) {
//
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new StockMovementReport[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - stock-movement-report-get all
//	public StockMovementReport[] getStockMovementReports(String authToken) throws ParseException {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "ClassicWMS RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			UriComponentsBuilder builder = UriComponentsBuilder
//					.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/");
//			HttpEntity<?> entity = new HttpEntity<>(headers);
//			ResponseEntity<StockMovementReport[]> result = getRestTemplate().exchange(builder.toUriString(),
//					HttpMethod.GET, entity, StockMovementReport[].class);
//			log.info("result : " + result.getStatusCode());
//
//			List<StockMovementReport> obList = new ArrayList<>();
//			for (StockMovementReport obHeader : result.getBody()) {
//
//				if(obHeader.getConfirmedOn() != null) {
//					obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//				}
//				obList.add(obHeader);
//			}
//			return obList.toArray(new StockMovementReport[obList.size()]);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
        public StockMovementReport[] getStockMovementReports(String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/new");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<StockMovementReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, StockMovementReport[].class);
                log.info("result : " + result.getStatusCode());

                List<StockMovementReport> obList = new ArrayList<>();
                for (StockMovementReport obHeader : result.getBody()) {

                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new StockMovementReport[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - Opening Stock Report renamed to Transaction History Report
        public InventoryStockReport[] getTransactionHistoryReport(FindImBasicData1 findImBasicData1, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/transactionHistoryReport");

                HttpEntity<?> entity = new HttpEntity<>(findImBasicData1, headers);
                ResponseEntity<InventoryStockReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, InventoryStockReport[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH ----
        public OutboundLine updateOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                               @Valid OutboundLine updateOutboundLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOutboundLine, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/" + lineNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<OutboundLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        OutboundLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // DELETE
        public boolean deleteOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                          Long lineNumber, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/" + lineNumber)
                        .queryParam("warehouseId", warehouseId).queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber).queryParam("partnerCode", partnerCode)
                        .queryParam("itemCode", itemCode).queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*
         * ---------------------------------OutboundReversal----------------------------
         * -----------------------
         */
        // POST - findOutboundReversal
        public OutboundReversal[] findOutboundReversal(SearchOutboundReversal searchOutboundReversal, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundreversal/findOutboundReversal");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundReversal, headers);
                ResponseEntity<OutboundReversal[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OutboundReversal[].class);
                log.info("result : " + result.getStatusCode());

                List<OutboundReversal> obList = new ArrayList<>();
                for (OutboundReversal obHeader : result.getBody()) {
                    if (obHeader.getReversedOn() != null) {
                        obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
                    }

                    obList.add(obHeader);
                }
                return obList.toArray(new OutboundReversal[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findOutboundReversal - Stream
        public OutboundReversal[] findOutboundReversalNew(SearchOutboundReversal searchOutboundReversal, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundreversal/findOutboundReversalNew");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundReversal, headers);
                ResponseEntity<OutboundReversal[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OutboundReversal[].class);
                log.info("result : " + result.getStatusCode());

                List<OutboundReversal> obList = new ArrayList<>();
                for (OutboundReversal obHeader : result.getBody()) {
                    if (obHeader.getReversedOn() != null) {
                        obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
                    }

                    obList.add(obHeader);
                }
                return obList.toArray(new OutboundReversal[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // GET
        public OutboundReversal[] doReversal(String refDocNumber, String itemCode, String loginUserID, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/reversal/new")
                        .queryParam("refDocNumber", refDocNumber).queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundReversal[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, OutboundReversal[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public DCReversalRequest[] doDCReversal(List<DCReversalRequest> dcReversalRequest, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/reversal/dc");
                HttpEntity<?> entity = new HttpEntity<>(dcReversalRequest, headers);
                ResponseEntity<DCReversalRequest[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, DCReversalRequest[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*----------------------------------REPORTS----------------------------------------------------------*/

        // GET - STOCK REPORT
        public PaginatedResponse<StockReport> getStockReports(List<String> warehouseId, List<String> itemCode,
                                                              String itemText, String stockTypeText, Integer pageNo, Integer pageSize, String sortBy, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/stockReport")
                        .queryParam("warehouseId", warehouseId).queryParam("itemCode", itemCode)
                        .queryParam("itemText", itemText).queryParam("stockTypeText", stockTypeText)
                        .queryParam("pageNo", pageNo).queryParam("pageSize", pageSize).queryParam("sortBy", sortBy);
                HttpEntity<?> entity = new HttpEntity<>(headers);

//			ResponseEntity<StockReport[]> result =
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockReport[].class);

                ParameterizedTypeReference<PaginatedResponse<StockReport>> responseType = new ParameterizedTypeReference<PaginatedResponse<StockReport>>() {
                };
                ResponseEntity<PaginatedResponse<StockReport>> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, responseType);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // Get All Stock Reports
        public StockReport[] getAllStockReports(List<String> languageId, List<String> companyCodeId, List<String> plantId,
                                                List<String> warehouseId, List<String> itemCode, List<String> manufacturerName, String itemText,
                                                String stockTypeText, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/stockReport-all")
                        .queryParam("languageId", languageId).queryParam("companyCodeId", companyCodeId).queryParam("plantId", plantId)
                        .queryParam("warehouseId", warehouseId).queryParam("itemCode", itemCode).queryParam("manufacturerName", manufacturerName)
                        .queryParam("itemText", itemText).queryParam("stockTypeText", stockTypeText);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<StockReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, StockReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // Get All Stock Reports New
        public StockReport[] getAllStockReportsV2(SearchStockReport searchStockReport, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/v2/stockReport-all");
                HttpEntity<?> entity = new HttpEntity<>(searchStockReport, headers);
                ResponseEntity<StockReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, StockReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // Get All Stock Reports New
        public StockReportOutput[] getAllStockReportsV2SP(SearchStockReportInput searchStockReport, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/v2/stockReportSP");
                HttpEntity<?> entity = new HttpEntity<>(searchStockReport, headers);
                ResponseEntity<StockReportOutput[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, StockReportOutput[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - INVENTORY REPORT
        public PaginatedResponse<InventoryReport> getInventoryReport(List<String> warehouseId, List<String> itemCode,
                                                                     String storageBin, String stockTypeText, List<String> stSectionIds, Integer pageNo, Integer pageSize,
                                                                     String sortBy, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/inventoryReport")
                        .queryParam("warehouseId", warehouseId).queryParam("itemCode", itemCode)
                        .queryParam("storageBin", storageBin).queryParam("stockTypeText", stockTypeText)
                        .queryParam("stSectionIds", stSectionIds).queryParam("pageNo", pageNo)
                        .queryParam("pageSize", pageSize).queryParam("sortBy", sortBy);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ParameterizedTypeReference<PaginatedResponse<InventoryReport>> responseType = new ParameterizedTypeReference<PaginatedResponse<InventoryReport>>() {
                };
                ResponseEntity<PaginatedResponse<InventoryReport>> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.GET, entity, responseType);

//			ResponseEntity<InventoryReport[]> result =
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InventoryReport[].class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - StockMovementReport
        public StockMovementReport[] getStockMovementReport(String warehouseId, String itemCode, String fromCreatedOn,
                                                            String toCreatedOn, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/stockMovementReport")
                        .queryParam("warehouseId", warehouseId).queryParam("itemCode", itemCode)
                        .queryParam("fromCreatedOn", fromCreatedOn).queryParam("toCreatedOn", toCreatedOn);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<StockMovementReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, StockMovementReport[].class);

                List<StockMovementReport> stockMovementReportList = new ArrayList<>();
                for (StockMovementReport stockMovementReport : result.getBody()) {
                    if (stockMovementReport.getConfirmedOn() != null) {
                        stockMovementReport
                                .setConfirmedOn(DateUtils.addTimeToDate(stockMovementReport.getConfirmedOn(), 3));
                        stockMovementReportList.add(stockMovementReport);
                    }
                }
                return stockMovementReportList.toArray(new StockMovementReport[stockMovementReportList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - OrderStatusReport
        public OrderStatusReport[] getOrderStatusReport(SearchOrderStatusReport requestData, String authToken)
                throws ParseException {
            try {
                SearchOrderStatusModel requestDataForService = new SearchOrderStatusModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getFromDeliveryDate() != null) {
                    requestDataForService
                            .setFromDeliveryDate(DateUtils.convertStringToYYYYMMDD(requestData.getFromDeliveryDate()));
                }
                if (requestData.getToDeliveryDate() != null) {
                    requestDataForService
                            .setToDeliveryDate(DateUtils.convertStringToYYYYMMDD(requestData.getToDeliveryDate()));
                }
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/orderStatusReport");
                HttpEntity<?> entity = new HttpEntity<>(requestDataForService, headers);
                ResponseEntity<OrderStatusReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OrderStatusReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
//            List<OrderStatusReport> orderStatusReportList = new ArrayList<>();
//            for (OrderStatusReport orderStatusReport : result.getBody()) {
//                if (orderStatusReport.getDeliveryConfirmedOn() != null) {
//                    orderStatusReport.setDeliveryConfirmedOn(
//                            DateUtils.addTimeToDate(orderStatusReport.getDeliveryConfirmedOn(), 3));
//                }
//
//                if (orderStatusReport.getOrderReceivedDate() != null) {
//                    orderStatusReport
//                            .setOrderReceivedDate(DateUtils.addTimeToDate(orderStatusReport.getOrderReceivedDate(), 3));
//                }
//
//                if (orderStatusReport.getExpectedDeliveryDate() != null) {
//                    orderStatusReport.setExpectedDeliveryDate(
//                            DateUtils.addTimeToDate(orderStatusReport.getExpectedDeliveryDate(), 3));
//                }
//                orderStatusReportList.add(orderStatusReport);
//            }
//            log.info("orderStatusReportList--------> : " + orderStatusReportList);
//            return orderStatusReportList.toArray(new OrderStatusReport[orderStatusReportList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - ShipmentDelivery
        public ShipmentDeliveryReport[] getShipmentDeliveryReport(String warehouseId, String fromDeliveryDate,
                                                                  String toDeliveryDate, String storeCode, List<String> soType, String orderNumber, String authToken)
                throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/shipmentDelivery")
                        .queryParam("warehouseId", warehouseId).queryParam("fromDeliveryDate", fromDeliveryDate)
                        .queryParam("toDeliveryDate", toDeliveryDate).queryParam("storeCode", storeCode)
                        .queryParam("orderNumber", orderNumber).queryParam("soType", soType);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ShipmentDeliveryReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ShipmentDeliveryReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
//            List<ShipmentDeliveryReport> shipmentDeliveryReportList = new ArrayList<>();
//            for (ShipmentDeliveryReport shipmentDeliveryReport : result.getBody()) {
//                if (shipmentDeliveryReport.getDeliveryDate() != null) {
//                    shipmentDeliveryReport
//                            .setDeliveryDate(DateUtils.addTimeToDate(shipmentDeliveryReport.getDeliveryDate(), 3));
//                    shipmentDeliveryReportList.add(shipmentDeliveryReport);
//                }
//            }
//
//            return shipmentDeliveryReportList.toArray(new ShipmentDeliveryReport[shipmentDeliveryReportList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - ShipmentDelivery
        public ShipmentDeliveryReport[] getShipmentDeliveryReportV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                    String fromDeliveryDate, String toDeliveryDate, String storeCode, List<String> soType, String orderNumber, String authToken)
                throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/v2/shipmentDelivery")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("fromDeliveryDate", fromDeliveryDate)
                        .queryParam("toDeliveryDate", toDeliveryDate).queryParam("storeCode", storeCode)
                        .queryParam("orderNumber", orderNumber).queryParam("soType", soType);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ShipmentDeliveryReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ShipmentDeliveryReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - ShipmentDelivery
        public ShipmentDeliveryReport[] getShipmentDeliveryReportV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                    String fromDeliveryDate, String toDeliveryDate, String storeCode, List<String> soType,
                                                                    String orderNumber, String preOutboundNo, String authToken)
                throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/v2/shipmentDelivery/new")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("fromDeliveryDate", fromDeliveryDate)
                        .queryParam("toDeliveryDate", toDeliveryDate).queryParam("storeCode", storeCode)
                        .queryParam("orderNumber", orderNumber)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("soType", soType);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ShipmentDeliveryReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ShipmentDeliveryReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - ShipmentDeliverySummary
        public ShipmentDeliverySummaryReport getShipmentDeliverySummaryReport(String fromDeliveryDate, String toDeliveryDate,
                                                                              List<String> customerCode, String warehouseId,
                                                                              String companyCodeId, String plantId, String languageId,
                                                                              String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/shipmentDeliverySummary")
                        .queryParam("fromDeliveryDate", fromDeliveryDate).queryParam("toDeliveryDate", toDeliveryDate)
                        .queryParam("customerCode", customerCode)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ShipmentDeliverySummaryReport> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ShipmentDeliverySummaryReport.class);
                ShipmentDeliverySummaryReport summaryReport = result.getBody();

                for (ShipmentDeliverySummary shipmentDeliverySummary : summaryReport.getShipmentDeliverySummary()) {
                    if (shipmentDeliverySummary.getExpectedDeliveryDate() != null) {
                        shipmentDeliverySummary.setExpectedDeliveryDate(
                                DateUtils.addTimeToDate(shipmentDeliverySummary.getExpectedDeliveryDate(), 3));
                    }

                    if (shipmentDeliverySummary.getDeliveryDateTime() != null) {
                        shipmentDeliverySummary.setDeliveryDateTime(
                                DateUtils.addTimeToDate(shipmentDeliverySummary.getDeliveryDateTime(), 3));
                    }
                }
                return summaryReport;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - ShipmentDispatchSummary
        public ShipmentDispatchSummaryReport getShipmentDispatchSummaryReport(String fromDeliveryDate,
                                                                              String toDeliveryDate, List<String> customerCode, String warehouseId, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/shipmentDispatchSummary")
                        .queryParam("fromDeliveryDate", fromDeliveryDate).queryParam("toDeliveryDate", toDeliveryDate)
                        .queryParam("customerCode", customerCode).queryParam("warehouseId", warehouseId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ShipmentDispatchSummaryReport> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ShipmentDispatchSummaryReport.class);
                ShipmentDispatchSummaryReport summaryReport = result.getBody();
                for (ShipmentDispatch shipmentDispatch : summaryReport.getShipmentDispatch()) {
                    for (ShipmentDispatchList list : shipmentDispatch.getShipmentDispatchList()) {
                        if (list.getOrderReceiptTime() != null) {
                            list.setOrderReceiptTime(DateUtils.addTimeToDate(list.getOrderReceiptTime(), 3));
                        }
                    }
                }
                return summaryReport;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - ReceiptConfimation
        public ReceiptConfimationReport getReceiptConfimationReport(String asnNumber, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/receiptConfirmation")
                        .queryParam("asnNumber", asnNumber);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ReceiptConfimationReport> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, ReceiptConfimationReport.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - MpbileDashboard
        public MobileDashboard getMobileDashboard(String companyCode, String plantId, String languageId, String warehouseId, String loginUserID, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/dashboard/mobile")
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("loginUserID", loginUserID)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<MobileDashboard> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, MobileDashboard.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public MobileDashboard findMobileDashBoard(FindMobileDashBoard findMobileDashBoard, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/dashboard/mobile/find");
                HttpEntity<?> entity = new HttpEntity<>(findMobileDashBoard, headers);
                ResponseEntity<MobileDashboard> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MobileDashboard.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //// GET - MpbileDashboard
        //    public MobileDashboard getMobileDashboard(String warehouseId, String authToken) {
        //        try {
        //            HttpHeaders headers = new HttpHeaders();
        //            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //            headers.add("User-Agent", "ClassicWMS RestTemplate");
        //            headers.add("Authorization", "Bearer " + authToken);
        //            UriComponentsBuilder builder = UriComponentsBuilder
        //                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/dashboard/mobile")
        //                    .queryParam("warehouseId", warehouseId);
        //
        //            HttpEntity<?> entity = new HttpEntity<>(headers);
        //            ResponseEntity<MobileDashboard> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
        //                    entity, MobileDashboard.class);
        //            log.info("result : " + result.getStatusCode());
        //            return result.getBody();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            throw e;
        //        }
        //    }

        // GET - Opening Stock Report
        public InventoryStockReport[] getInventoryStockReport(FindImBasicData1 findImBasicData1, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/inventoryStock");

                HttpEntity<?> entity = new HttpEntity<>(findImBasicData1, headers);
                ResponseEntity<InventoryStockReport[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, InventoryStockReport[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // ---------------------------------PerpetualHeader----------------------------------------------------
        // GET ALL
        public PerpetualHeader[] getPerpetualHeaders(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PerpetualHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PerpetualHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<PerpetualHeader> obList = new ArrayList<>();
                for (PerpetualHeader obHeader : result.getBody()) {

                    obList.add(addingTimeWithDatePerpetualHeader(obHeader));
                }
                return obList.toArray(new PerpetualHeader[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public PerpetualHeader addingTimeWithDatePerpetualHeader(PerpetualHeader obHeader) throws ParseException {

            if (obHeader.getCountedOn() != null) {
                obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
            }
            if (obHeader.getCreatedOn() != null) {
                obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
            }
            if (obHeader.getConfirmedOn() != null) {
                obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
            }

            return obHeader;
        }

        public PerpetualHeader getPerpetualHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                  Long movementTypeId, Long subMovementTypeId, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId).queryParam("subMovementTypeId", subMovementTypeId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PerpetualHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PerpetualHeader.class);
                log.info("result : " + result.getStatusCode());

                return addingTimeWithDatePerpetualHeader(result.getBody());

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualHeader
        public PerpetualHeaderEntity[] findPerpetualHeader(SearchPerpetualHeader searchPerpetualHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/findPerpetualHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);
                ResponseEntity<PerpetualHeaderEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PerpetualHeaderEntity[].class);
                log.info("result : " + result.getStatusCode());

                List<PerpetualHeaderEntity> obList = new ArrayList<>();
                for (PerpetualHeaderEntity obHeader : result.getBody()) {
                    if (obHeader.getCountedOn() != null) {
                        obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PerpetualHeaderEntity[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - CREATE
        public PerpetualHeader createPerpetualHeader(@Valid AddPerpetualHeader newPerpetualHeader, String loginUserID,
                                                     String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader").queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPerpetualHeader, headers);
            ResponseEntity<PerpetualHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PerpetualHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - RUN
        public PerpetualLineEntity[] runPerpetualHeader(@Valid RunPerpetualHeader runPerpetualHeader, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/run");
            HttpEntity<?> entity = new HttpEntity<>(runPerpetualHeader, headers);
            ResponseEntity<PerpetualLineEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PerpetualLineEntity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - RUN - Stream
        public PerpetualLineEntity[] runPerpetualHeaderNew(@Valid RunPerpetualHeader runPerpetualHeader, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/runStream");
            HttpEntity<?> entity = new HttpEntity<>(runPerpetualHeader, headers);
            ResponseEntity<PerpetualLineEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PerpetualLineEntity[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public PerpetualHeader updatePerpetualHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                     Long movementTypeId, Long subMovementTypeId, String loginUserID,
                                                     @Valid UpdatePerpetualHeader updatePerpetualHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId).queryParam("subMovementTypeId", subMovementTypeId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<PerpetualHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PerpetualHeader.class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePerpetualHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                             Long movementTypeId, Long subMovementTypeId, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId).queryParam("subMovementTypeId", subMovementTypeId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualLine
        public PerpetualLine[] findPerpetualLine(SearchPerpetualLine searchPerpetualLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/findPerpetualLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualLine, headers);
                ResponseEntity<PerpetualLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PerpetualLine[].class);
                log.info("result : " + result.getBody());

                List<PerpetualLine> obList = new ArrayList<>();
                for (PerpetualLine obHeader : result.getBody()) {
                    if (obHeader.getCountedOn() != null) {
                        obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PerpetualLine[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualLineStream
        public PerpetualLine[] findPerpetualLineStream(SearchPerpetualLine searchPerpetualLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/findPerpetualLineStream");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualLine, headers);
                ResponseEntity<PerpetualLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PerpetualLine[].class);
                log.info("result : " + result.getBody());

                List<PerpetualLine> obList = new ArrayList<>();
                for (PerpetualLine obHeader : result.getBody()) {
                    if (obHeader.getCountedOn() != null) {
                        obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PerpetualLine[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PerpetualLine[] updateAssingHHTUser(List<AssignHHTUserCC> assignHHTUser, String loginUserID,
                                                   String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/assigingHHTUser")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PerpetualLine[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PerpetualLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PerpetualUpdateResponse updatePerpetualLine(String cycleCountNo,
                                                           List<UpdatePerpetualLine> updatePerpetualLine, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PerpetualUpdateResponse> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, PerpetualUpdateResponse.class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // ---------------------------------PeriodicHeader----------------------------------------------------
        // GET ALL
        public PeriodicHeaderEntity[] getPeriodicHeaders(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PeriodicHeaderEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, PeriodicHeaderEntity[].class);
                log.info("result : " + result.getStatusCode());

                List<PeriodicHeaderEntity> obList = new ArrayList<>();
                for (PeriodicHeaderEntity obHeader : result.getBody()) {

                    obList.add(addingTimeWithDatePeriodicHeaderEntity(obHeader));
                }
                return obList.toArray(new PeriodicHeaderEntity[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public PeriodicHeaderEntity addingTimeWithDatePeriodicHeaderEntity(PeriodicHeaderEntity obHeader) throws ParseException {

            if (obHeader.getConfirmedOn() != null) {
                obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
            }
            if (obHeader.getCreatedOn() != null) {
                obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
            }
            if (obHeader.getCountedOn() != null) {
                obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
            }

            return obHeader;
        }

        // GET
        public PeriodicHeader[] getPeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                  Long movementTypeId, Long subMovementTypeId, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId).queryParam("subMovementTypeId", subMovementTypeId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PeriodicHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PeriodicHeader[].class);
                log.info("result : " + result.getStatusCode());

                List<PeriodicHeader> obList = new ArrayList<>();
                for (PeriodicHeader obHeader : result.getBody()) {
                    if (obHeader.getConfirmedOn() != null) {
                        obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
                    }
                    if (obHeader.getCreatedOn() != null) {
                        obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
                    }
                    if (obHeader.getCountedOn() != null) {
                        obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
                    }
                    obList.add(obHeader);
                }
                return obList.toArray(new PeriodicHeader[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPeriodicHeader
        public PeriodicHeaderEntity[] findPeriodicHeader(SearchPeriodicHeader searchPeriodicHeader, String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/findPeriodicHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicHeader, headers);
                ResponseEntity<PeriodicHeaderEntity[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeaderEntity[].class);

                List<PeriodicHeaderEntity> obList = new ArrayList<>();
                for (PeriodicHeaderEntity obHeader : result.getBody()) {
                    obList.add(addingTimeWithDatePeriodicHeaderEntity(obHeader));
                }
                return obList.toArray(new PeriodicHeaderEntity[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPeriodicHeader -Stream
        public PeriodicHeader[] findPeriodicHeaderStream(SearchPeriodicHeader searchPeriodicHeader, String authToken) throws ParseException {
            try {
                AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/findPeriodicHeaderStream");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicHeader, headers);
                ResponseEntity<PeriodicHeader[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeader[].class);

                Arrays.stream(result.getBody()).forEach(n -> {
                            if (n.getConfirmedOn() != null) {
                                try {
                                    n.setConfirmedOn(DateUtils.addTimeToDate(n.getConfirmedOn(), 3));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (n.getCreatedOn() != null) {
                                try {
                                    n.setCreatedOn(DateUtils.addTimeToDate(n.getCreatedOn(), 3));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (n.getCountedOn() != null) {
                                try {
                                    n.setCountedOn(DateUtils.addTimeToDate(n.getCountedOn(), 3));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                );
//			List<PeriodicHeader> obList = new ArrayList<>();
//			for (PeriodicHeader obHeader : result.getBody()) {
//
//				if(obHeader.getConfirmedOn() != null) {
//					obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//				}
//				if(obHeader.getCreatedOn() != null) {
//					obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//				}
//				if(obHeader.getCountedOn() != null) {
//					obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//				}
//
//				obList.add(obHeader);
//			}
//			return obList.toArray(new PeriodicHeader[obList.size()]);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - CREATE
        public PeriodicHeaderEntity createPeriodicHeader(@Valid AddPeriodicHeader newPeriodicHeader, String loginUserID,
                                                         String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader").queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPeriodicHeader, headers);
            ResponseEntity<PeriodicHeaderEntity> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PeriodicHeaderEntity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // GET ALL
        public Page<?> runPeriodicHeader(String warehouseId, Integer pageNo, Integer pageSize, String sortBy,
                                         String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("ClientGeneral-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/run/pagination")
                        .queryParam("pageNo", pageNo).queryParam("pageSize", pageSize).queryParam("sortBy", sortBy)
                        .queryParam("warehouseId", warehouseId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>> responseType =
                        new ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>>() {
                        };
                ResponseEntity<PaginatedResponse<PeriodicLineEntity>> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicHeader updatePeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                   String loginUserID, UpdatePeriodicHeader updatePeriodicHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<PeriodicHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePeriodicHeader(String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                            String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId).queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //-------------------------------------PeriodicLine--------------------------------------------------------
        // FIND
        public PeriodicLine[] findPeriodicLine(SearchPeriodicLine searchPeriodicLine, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/findPeriodicLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicLine, headers);
                ResponseEntity<PeriodicLine[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicLine[].class);
                List<PeriodicLine> periodicLineList = new ArrayList<>();
                for (PeriodicLine periodicLine : result.getBody()) {
                    periodicLine.setCreatedOn(DateUtils.addTimeToDate(periodicLine.getCreatedOn(), 3));
                    periodicLineList.add(periodicLine);
                }
                return periodicLineList.toArray(new PeriodicLine[periodicLineList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND - Stream
        public PeriodicLine[] findPeriodicLineNew(SearchPeriodicLine searchPeriodicLine, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/findPeriodicLineStream");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicLine, headers);
                ResponseEntity<PeriodicLine[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicLine[].class);
                List<PeriodicLine> periodicLineList = new ArrayList<>();
                for (PeriodicLine periodicLine : result.getBody()) {
                    periodicLine.setCreatedOn(DateUtils.addTimeToDate(periodicLine.getCreatedOn(), 3));
                    periodicLineList.add(periodicLine);
                }
                return periodicLineList.toArray(new PeriodicLine[periodicLineList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicLine[] updatePeriodicLineAssingHHTUser(List<AssignHHTUserCC> assignHHTUser, String loginUserID,
                                                              String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/assigingHHTUser")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PeriodicLine[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicLine[].class);
                log.info("result : " + result);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicUpdateResponse updatePeriodicLine(String cycleCountNo, List<UpdatePeriodicLine> updatePeriodicLine,
                                                         String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PeriodicUpdateResponse> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicUpdateResponse.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /**
         * @param authToken
         */
        public void getInventoryReport(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/inventoryReport/schedule");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        String.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        public InventoryReport[] generateInventoryReport(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/inventoryReport/all");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InventoryReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, InventoryReport[].class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

//	/**
//	 * sendEmail
//	 * @param email
//	 * @param authToken
//	 * @return
//	 */
//	public String sendEmail (MultipartFile file,String authToken) throws Exception {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//			headers.add("User-Agent", "ClassicWMS RestTemplate");
//			headers.add("Authorization", "Bearer " + authToken);
//
//			MultiValueMap<String, Object> body
//					= new LinkedMultiValueMap<>();
//			body.add("file", file);
//
//			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body,headers);
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/sendmail/attachment").queryParam("file", file);
//			ResponseEntity<String> result = getRestTemplate().postForEntity(builder.toUriString(), entity, String.class);
//			log.info("result : " + result.getStatusCode());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

        // -----------------------------------------------------------------------------------------------------------------

        /**
         * @param warehouseId
         * @param authToken
         * @return
         */
        public Dashboard getDashboardCount(String warehouseId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/dashboard/get-count")
                        .queryParam("warehouseId", warehouseId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Dashboard> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        Dashboard.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        public FastSlowMovingDashboard[] getFastSlowMovingDashboard(FastSlowMovingDashboardRequest requestData,
                                                                    String authToken) throws ParseException {
            try {
                FastSlowMovingDashboardRequestModel requestDataForService = new FastSlowMovingDashboardRequestModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getFromDate() != null) {
                    requestDataForService.setFromDate(DateUtils.convertStringToYYYYMMDD(requestData.getFromDate()));
                }
                if (requestData.getToDate() != null) {
                    requestDataForService.setToDate(DateUtils.convertStringToYYYYMMDD(requestData.getToDate()));
                }
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/dashboard/get-fast-slow-moving");

                HttpEntity<?> entity = new HttpEntity<>(requestDataForService, headers);
                ResponseEntity<FastSlowMovingDashboard[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, FastSlowMovingDashboard[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - SO
        public WarehouseApiResponse postSO(@Valid ShipmentOrder shipmenOrder, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/so");
            HttpEntity<?> entity = new HttpEntity<>(shipmenOrder, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }


        // GET - OutboundOrder - OrderByID
        public OutboundOrder getOutboundOrdersById(String orderId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "orders/outbound/orders/" + orderId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundOrder> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, OutboundOrder.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET - OutboundOrder - OrderByDate
        public OutboundOrder[] getOBOrderByDate(String orderStartDate, String orderEndDate, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "orders/outbound/orders/byDate")
                        .queryParam("orderStartDate", orderStartDate).queryParam("orderEndDate", orderEndDate);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundOrder[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, OutboundOrder[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - FAILED
        public OutboundIntegrationLog[] getFailedOutboundOrders(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "orders/outbound/orders/failed");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundIntegrationLog[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, OutboundIntegrationLog[].class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET
        public OutboundOrder[] getOBOrders(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "orders/outbound");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundOrder[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, OutboundOrder[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //---------------Periodic-CSV-Writer----------------------------------------

        /**
         * @param newPeriodicLines
         * @throws IOException
         */
        public void createCSV(List<PeriodicLine> newPeriodicLines) throws IOException {
            try (
                    Writer writer = Files.newBufferedWriter(Paths.get("periodicLine.csv"));
                    CSVWriter csvWriter = new CSVWriter(writer,
                            CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.DEFAULT_LINE_END);
            ) {
                String[] headerRecord = {"languageId", "companyCode", "plantId", "warehouseId", "cycleCountNo", "storageBin",
                        "itemCode", "packBarcodes", "variantCode", "variantSubCode", "batchSerialNumber", "stockTypeId", "specialStockIndicator",
                        "inventoryQuantity", "inventoryUom", "countedQty", "varianceQty", "cycleCounterId", "cycleCounterName", "statusId",
                        "cycleCountAction", "referenceNo", "approvalProcessId", "approvalLevel", "approverCode", "approvalStatus", "remarks",
                        "referenceField1", "referenceField2", "referenceField3", "referenceField4", "referenceField5", "referenceField6",
                        "referenceField7", "referenceField8", "referenceField9", "referenceField10", "deletionIndicator", "createdBy", "createdOn",
                        "confirmedBy", "confirmedOn", "countedBy", "countedOn"};
                csvWriter.writeNext(headerRecord);

                List<String[]> listArr = new ArrayList<>();
                newPeriodicLines.stream().forEach(pl -> {
                    String[] sarr = toArray(pl);
                    listArr.add(sarr);
                });

                csvWriter.writeAll(listArr);
            }
        }

        /**
         * @param periodicLine
         * @return
         */
        private String[] toArray(PeriodicLine periodicLine) {
            String[] strarr = new String[]{
                    periodicLine.getLanguageId(),
                    periodicLine.getCompanyCode(),
                    periodicLine.getPlantId(),
                    periodicLine.getWarehouseId(),
                    periodicLine.getCycleCountNo(),
                    periodicLine.getStorageBin(),
                    periodicLine.getItemCode(),
                    periodicLine.getPackBarcodes(),
                    String.valueOf(periodicLine.getVariantCode()),
                    periodicLine.getVariantSubCode(),
                    periodicLine.getBatchSerialNumber(),
                    String.valueOf(periodicLine.getStockTypeId()),
                    periodicLine.getSpecialStockIndicator(),
                    String.valueOf(periodicLine.getInventoryQuantity()),
                    periodicLine.getInventoryUom(),
                    String.valueOf(periodicLine.getCountedQty()),
                    String.valueOf(periodicLine.getVarianceQty()),
                    periodicLine.getCycleCounterId(),
                    periodicLine.getCycleCounterName(),
                    String.valueOf(periodicLine.getStatusId()),
                    periodicLine.getCycleCountAction(),
                    periodicLine.getReferenceNo(),
                    String.valueOf(periodicLine.getApprovalProcessId()),
                    periodicLine.getApprovalLevel(),
                    periodicLine.getApproverCode(),
                    periodicLine.getApprovalStatus(),
                    periodicLine.getRemarks(),
                    periodicLine.getReferenceField1(),
                    periodicLine.getReferenceField2(),
                    periodicLine.getReferenceField3(),
                    periodicLine.getReferenceField4(),
                    periodicLine.getReferenceField5(),
                    periodicLine.getReferenceField6(),
                    periodicLine.getReferenceField7(),
                    periodicLine.getReferenceField8(),
                    periodicLine.getReferenceField9(),
                    periodicLine.getReferenceField10(),
                    String.valueOf(periodicLine.getDeletionIndicator()),
                    periodicLine.getCreatedBy(),
                    String.valueOf(periodicLine.getCreatedOn()),
                    periodicLine.getConfirmedBy(),
                    String.valueOf(periodicLine.getConfirmedOn()),
                    periodicLine.getCountedBy(),
                    String.valueOf(periodicLine.getCountedOn())
            };
            return strarr;
        }

        //=================================================STREAMING===============================================================

        //	private final Gson gson = new Gson();
        //-------------------------------------------findInventoryMovementByStreaming-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findInventoryMovementByStreaming() {
            Stream<InventoryMovement2> inventoryMovement2 = streamInventoryMovement();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    inventoryMovement2.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }

        /**
         * warehouseId
         * itemCode
         * description
         * packBarcodes
         * storageBin
         * movementType
         * submovementType
         * refDocNumber
         * movementQty
         * inventoryUom
         * createdOn
         *
         * @return
         */
        public Stream<InventoryMovement2> streamInventoryMovement() {
            jdbcTemplate.setFetchSize(1000);
		/*
		 * "MVT_DOC_NO"
		 * ----------------
		 * String warehouseId,
			Long movementType,
			Long submovementType,
			String palletCode,
			String packBarcodes,
			String itemCode,
			String storageBin,
			String description,
			Double movementQty,
			String inventoryUom,
			String refDocNumber,
			Date createdOn
		 */
            Stream<InventoryMovement2> inventoryMovement2 = jdbcTemplate.queryForStream(
                    "Select WH_ID, MVT_TYP_ID, SUB_MVT_TYP_ID, PAL_CODE, PACK_BARCODE, ITM_CODE, "
                            + "ST_BIN, TEXT, MVT_QTY, MVT_UOM, REF_DOC_NO, DATEADD(hour, 3, IM_CTD_ON) AS IM_CTD_ON, MVT_DOC_NO "
                            + "from tblinventorymovement "
                            + "where is_deleted = 0 ",
                    (resultSet, rowNum) -> {
                        return new InventoryMovement2(
                                resultSet.getString("WH_ID"),
                                resultSet.getLong("MVT_TYP_ID"),
                                resultSet.getLong("SUB_MVT_TYP_ID"),
                                resultSet.getString("PAL_CODE"),
                                resultSet.getString("PACK_BARCODE"),
                                resultSet.getString("ITM_CODE"),
                                resultSet.getString("ST_BIN"),
                                resultSet.getString("TEXT"),
                                resultSet.getDouble("MVT_QTY"),
                                resultSet.getString("MVT_UOM"),
                                resultSet.getString("REF_DOC_NO"),
                                resultSet.getDate("IM_CTD_ON"),
                                resultSet.getString("MVT_DOC_NO")
                        );
                    });
//		this.createdOn = DateUtils.addTimeToDate(createdOn, 3);

            return inventoryMovement2;
        }

        //-------------------------------------------findStreamOutboundHeader-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamOutboundHeader() {
            Stream<OutboundHeaderStream> outboundHeaderStream = streamOutboundHeader();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    outboundHeaderStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * Outbound Header
         * refDocNumber
         * partnerCode
         * referenceDocumentType
         * statusId
         * refDocDate
         * requiredDeliveryDate
         * referenceField1
         * referenceField7
         * referenceField8
         * referenceField9
         * referenceField10
         * deliveryConfirmedOn
         *
         * @return
         */
        public Stream<OutboundHeaderStream> streamOutboundHeader() {
            jdbcTemplate.setFetchSize(50);
            /**
             * Outbound Header
             * String refDocNumber
             * String partnerCode
             * String referenceDocumentType
             * Long statusId
             * Date refDocDate
             * Date requiredDeliveryDate
             * String referenceField1
             * String referenceField7
             * String referenceField8
             * String referenceField9
             * String referenceField10
             * Date deliveryConfirmedOn
             */
            Stream<OutboundHeaderStream> outboundHeaderStream = jdbcTemplate.queryForStream(
                    "Select REF_DOC_NO, PARTNER_CODE, REF_DOC_TYP, STATUS_ID, REF_DOC_DATE, REQ_DEL_DATE, REF_FIELD_1, "
                            + "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, DLV_CNF_ON "
                            + "from tbloutboundheader "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new OutboundHeaderStream(
                            resultSet.getString("REF_DOC_NO"),
                            resultSet.getString("PARTNER_CODE"),
                            resultSet.getString("REF_DOC_TYP"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getDate("REF_DOC_DATE"),
                            resultSet.getDate("REQ_DEL_DATE"),
                            resultSet.getString("REF_FIELD_1"),
                            resultSet.getString("REF_FIELD_7"),
                            resultSet.getString("REF_FIELD_8"),
                            resultSet.getString("REF_FIELD_9"),
                            resultSet.getString("REF_FIELD_10"),
                            resultSet.getDate("DLV_CNF_ON")
                    ));
            return outboundHeaderStream;
        }

        //-------------------------------------------findStreamPreOutboundHeader-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamPreOutboundHeader() {
            Stream<PreOutboundHeaderStream> preOutboundHeaderStream = streamPreOutboundHeader();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    preOutboundHeaderStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * PreOutbound Header
         * refDocNumber
         * preOutboundNo
         * partnerCode
         * referenceDocumentType
         * statusId
         * refDocDate
         * requiredDeliveryDate
         * referenceField1
         *
         * @return
         */
        public Stream<PreOutboundHeaderStream> streamPreOutboundHeader() {
            jdbcTemplate.setFetchSize(50);
            /**
             * PreOutbound Header
             * String refDocNumber
             * String preOutboundNo
             * String partnerCode
             * String referenceDocumentType
             * Long statusId
             * Date refDocDate
             * Date requiredDeliveryDate
             * String referenceField1
             */
            Stream<PreOutboundHeaderStream> preOutboundHeaderStream = jdbcTemplate.queryForStream(
                    "Select REF_DOC_NO, PRE_OB_NO, PARTNER_CODE, REF_DOC_TYP, tpo.status_id, ts.STATUS_TEXT, REF_DOC_DATE, "
                            + "REQ_DEL_DATE, tpo.REF_FIELD_1, OB_ORD_TYP_ID "
                            + "from tblpreoutboundheader tpo join tblstatusid ts on ts.status_id = tpo.status_id and ts.lang_id = tpo.lang_id "
                            + "where tpo.IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new PreOutboundHeaderStream(
                            resultSet.getString("REF_DOC_NO"),
                            resultSet.getString("PRE_OB_NO"),
                            resultSet.getString("PARTNER_CODE"),
                            resultSet.getString("REF_DOC_TYP"),
                            resultSet.getString("STATUS_TEXT"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getDate("REF_DOC_DATE"),
                            resultSet.getDate("REQ_DEL_DATE"),
                            resultSet.getString("REF_FIELD_1"),
                            resultSet.getLong("OB_ORD_TYP_ID")
                    ));
            return preOutboundHeaderStream;
        }

        //-------------------------------------------findStreamOrderManagementLine-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamOrderManagementLine() {
            Stream<OrderManagementLineStream> orderManagementLineStream = streamOrderManagementLine();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    orderManagementLineStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * OrderManagementLine
         * String preOutboundNo
         * String refDocNumber
         * String partnerCode
         * Long lineNumber
         * String itemCode
         * String proposedStorageBin
         * String proposedPackBarCode
         * Long outboundOrderTypeId
         * Long statusId
         * String description
         * Double orderQty
         * Double inventoryQty
         * Double allocatedQty
         * Date requiredDeliveryDate
         *
         * @return
         */
        public Stream<OrderManagementLineStream> streamOrderManagementLine() {
            jdbcTemplate.setFetchSize(50);
            /**
             * Order Management Line
             * String preOutboundNo
             * String refDocNumber
             * String partnerCode
             * Long lineNumber
             * String itemCode
             * String proposedStorageBin
             * String proposedPackBarCode
             * Long outboundOrderTypeId
             * Long statusId
             * String description
             * Double orderQty
             * Double inventoryQty
             * Double allocatedQty
             * Date requiredDeliveryDate
             */
            Stream<OrderManagementLineStream> orderManagementLineStream = jdbcTemplate.queryForStream(
                    "Select PRE_OB_NO, REF_DOC_NO, PARTNER_CODE, OB_LINE_NO, ITM_CODE, PROP_ST_BIN, "
                            + "PROP_PACK_BARCODE, OB_ORD_TYP_ID, STATUS_ID, ITEM_TEXT, ORD_QTY, INV_QTY, "
                            + "ALLOC_QTY, REQ_DEL_DATE "
                            + "from tblordermangementline "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new OrderManagementLineStream(
                            resultSet.getString("PRE_OB_NO"),
                            resultSet.getString("REF_DOC_NO"),
                            resultSet.getString("PARTNER_CODE"),
                            resultSet.getLong("OB_LINE_NO"),
                            resultSet.getString("ITM_CODE"),
                            resultSet.getString("PROP_ST_BIN"),
                            resultSet.getString("PROP_PACK_BARCODE"),
                            resultSet.getLong("OB_ORD_TYP_ID"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getString("ITEM_TEXT"),
                            resultSet.getDouble("ORD_QTY"),
                            resultSet.getDouble("INV_QTY"),
                            resultSet.getDouble("ALLOC_QTY"),
                            resultSet.getDate("REQ_DEL_DATE")
                    ));
            return orderManagementLineStream;
        }

        //-------------------------------------------findStreamPickupHeader-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamPickupHeader() {
            Stream<PickupHeaderStream> pickupHeaderStream = streamPickupHeader();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    pickupHeaderStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * Pickup Header Stream
         * String refDocNumber
         * String partnerCode
         * Long lineNumber
         * String itemCode
         * String proposedStorageBin
         * String proposedPackBarCode
         * Long outboundOrderTypeId
         * Double pickToQty
         * Long statusId
         * String assignedPickerId
         * String referenceField1
         * Date pickupCreatedOn
         *
         * @return
         */
        public Stream<PickupHeaderStream> streamPickupHeader() {
            jdbcTemplate.setFetchSize(50);
            /**
             * Pickup Header Stream
             * String refDocNumber
             * String partnerCode
             * Long lineNumber
             * String itemCode
             * String proposedStorageBin
             * String proposedPackBarCode
             * Long outboundOrderTypeId
             * Double pickToQty
             * Long statusId
             * String assignedPickerId
             * String referenceField1
             * Date pickupCreatedOn
             */
            Stream<PickupHeaderStream> pickupHeaderStream = jdbcTemplate.queryForStream(
                    "Select REF_DOC_NO, PARTNER_CODE, OB_LINE_NO, ITM_CODE, PROP_ST_BIN, PROP_PACK_BARCODE, "
                            + "OB_ORD_TYP_ID, PICK_TO_QTY, STATUS_ID, ASS_PICKER_ID, REF_FIELD_1, PICK_CTD_ON "
                            + "from tblpickupheader "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new PickupHeaderStream(
                            resultSet.getString("REF_DOC_NO"),
                            resultSet.getString("PARTNER_CODE"),
                            resultSet.getLong("OB_LINE_NO"),
                            resultSet.getString("ITM_CODE"),
                            resultSet.getString("PROP_ST_BIN"),
                            resultSet.getString("PROP_PACK_BARCODE"),
                            resultSet.getLong("OB_ORD_TYP_ID"),
                            resultSet.getDouble("PICK_TO_QTY"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getString("ASS_PICKER_ID"),
                            resultSet.getString("REF_FIELD_1"),
                            resultSet.getDate("PICK_CTD_ON")
                    ));
            return pickupHeaderStream;
        }

        //-------------------------------------------findStreamQualityHeader-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamQualityHeader() {
            Stream<QualityHeaderStream> qualityHeaderStream = streamQualityHeader();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    qualityHeaderStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * Quality Header Stream
         * String refDocNumber
         * String partnerCode
         * String qualityInspectionNo
         * String actualHeNo
         * Long statusId
         * String qcToQty
         * String manufacturerPartNo
         * String referenceField1
         * String referenceField2
         * String referenceField4
         * String qualityCreatedBy
         * Date qualityCreatedOn
         *
         * @return
         */
        public Stream<QualityHeaderStream> streamQualityHeader() {
            jdbcTemplate.setFetchSize(50);
            /**
             * Quality Header Stream
             * String refDocNumber
             * String partnerCode
             * String qualityInspectionNo
             * String actualHeNo
             * Long statusId
             * String qcToQty
             * String manufacturerPartNo
             * String referenceField1
             * String referenceField2
             * String referenceField4
             * String qualityCreatedBy
             * Date qualityCreatedOn
             */
            Stream<QualityHeaderStream> qualityHeaderStream = jdbcTemplate.queryForStream(
                    "Select REF_DOC_NO, PARTNER_CODE, QC_NO, PICK_HE_NO, STATUS_ID, QC_TO_QTY, "
                            + "MFR_PART, REF_FIELD_1, REF_FIELD_2, REF_FIELD_4, QC_CTD_BY, QC_CTD_ON "
                            + "from tblqualityheader "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new QualityHeaderStream(
                            resultSet.getString("REF_DOC_NO"),
                            resultSet.getString("PARTNER_CODE"),
                            resultSet.getString("QC_NO"),
                            resultSet.getString("PICK_HE_NO"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getString("QC_TO_QTY"),
                            resultSet.getString("MFR_PART"),
                            resultSet.getString("REF_FIELD_1"),
                            resultSet.getString("REF_FIELD_2"),
                            resultSet.getString("REF_FIELD_4"),
                            resultSet.getString("QC_CTD_BY"),
                            resultSet.getDate("QC_CTD_ON")
                    ));
            return qualityHeaderStream;
        }

        //-------------------------------------------findStreamImBasicData1-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamImBasicData1() {
            Stream<ImBasicData1Stream> imBasicData1Stream = streamImBasicData1();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    imBasicData1Stream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }

        /**
         * imBasicData1 Stream
         * String uomId
         * String languageId
         * String companyCodeId
         * String plantId
         * String warehouseId
         * String itemCode
         * String description
         * String model
         * String specifications1
         * String specifications2
         * String eanUpcNo
         * String manufacturerPartNo
         * String hsnCode
         * Long   itemType
         * Long   itemGroup
         * Long   subItemGroup
         * String storageSectionId
         * Double totalStock
         * Double minimumStock
         * Double maximumStock
         * Double reorderLevel
         * Double replenishmentQty
         * Double safetyStock
         * Long   statusId
         * String referenceField1
         * String referenceField2
         * String referenceField3
         * String referenceField4
         * String referenceField5
         * String referenceField6
         * String referenceField7
         * String referenceField8
         * String referenceField9
         * String referenceField10
         * Long   deletionIndicator
         * String createdBy
         * Date   createdOn
         * String updatedBy
         * Date   updatedOn
         *
         * @return
         */
        public Stream<ImBasicData1Stream> streamImBasicData1() {
            jdbcTemplate.setFetchSize(1000);

            Stream<ImBasicData1Stream> imBasicData1Stream = jdbcTemplate.queryForStream(
                    "Select UOM_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, ITM_CODE, "
                            + "TEXT, MODEL, SPEC_01, SPEC_02, EAN_UPC_NO, MFR_PART, HSN_CODE, ITM_TYP_ID, ITM_GRP_ID, SUB_ITM_GRP_ID, ST_SEC_ID, TOT_STK, "
                            + "MIN_STK, MAX_STK, RE_ORD_LVL, REP_QTY, SAFTY_STCK, STATUS_ID, REF_FIELD_1, REF_FIELD_2, REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, "
                            + "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, IS_DELETED, CTD_BY,"
                            + "CTD_ON, UTD_BY, UTD_ON "
                            + "from tblimbasicdata1 "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new ImBasicData1Stream(
                            resultSet.getString("UOM_ID"),
                            resultSet.getString("LANG_ID"),
                            resultSet.getString("C_ID"),
                            resultSet.getString("PLANT_ID"),
                            resultSet.getString("WH_ID"),
                            resultSet.getString("ITM_CODE"),
                            resultSet.getString("TEXT"),
                            resultSet.getString("MODEL"),
                            resultSet.getString("SPEC_01"),
                            resultSet.getString("SPEC_02"),
                            resultSet.getString("EAN_UPC_NO"),
                            resultSet.getString("MFR_PART"),
                            resultSet.getString("HSN_CODE"),
                            resultSet.getLong("ITM_TYP_ID"),
                            resultSet.getLong("ITM_GRP_ID"),
                            resultSet.getLong("SUB_ITM_GRP_ID"),
                            resultSet.getString("ST_SEC_ID"),
                            resultSet.getDouble("TOT_STK"),
                            resultSet.getDouble("MIN_STK"),
                            resultSet.getDouble("MAX_STK"),
                            resultSet.getDouble("RE_ORD_LVL"),
                            resultSet.getDouble("REP_QTY"),
                            resultSet.getDouble("SAFTY_STCK"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getString("REF_FIELD_1"),
                            resultSet.getString("REF_FIELD_2"),
                            resultSet.getString("REF_FIELD_3"),
                            resultSet.getString("REF_FIELD_4"),
                            resultSet.getString("REF_FIELD_5"),
                            resultSet.getString("REF_FIELD_6"),
                            resultSet.getString("REF_FIELD_7"),
                            resultSet.getString("REF_FIELD_8"),
                            resultSet.getString("REF_FIELD_9"),
                            resultSet.getString("REF_FIELD_10"),
                            resultSet.getLong("IS_DELETED"),
                            resultSet.getString("CTD_BY"),
                            resultSet.getDate("CTD_ON"),
                            resultSet.getString("UTD_BY"),
                            resultSet.getDate("UTD_ON")
                    ));
            return imBasicData1Stream;
        }

        //-------------------------------------------findStreamStorageBin-------------------------------------------------------

        /**
         * @return
         */
        public StreamingResponseBody findStreamStorageBin() {
            Stream<StorageBinStream> storageBinStream = streamStorageBin();
            StreamingResponseBody responseBody = httpResponseOutputStream -> {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
                    jsonGenerator.writeStartArray();
                    jsonGenerator.setCodec(new ObjectMapper());
                    storageBinStream.forEach(im -> {
                        try {
                            jsonGenerator.writeObject(im);
                        } catch (IOException exception) {
                            log.error("exception occurred while writing object to stream", exception);
                        }
                    });
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                } catch (Exception e) {
                    log.info("Exception occurred while publishing data", e);
                    e.printStackTrace();
                }
                log.info("finished streaming records");
            };
            return responseBody;
        }
        //	private final Gson gson = new Gson();

        /**
         * storageBinStream
         * String storageBin
         * String warehouseId
         * Long floorId
         * String storageSectionId
         * String spanId
         * Long statusId
         * String createdBy
         * Date createdOn
         *
         * @return
         */
        public Stream<StorageBinStream> streamStorageBin() {
            jdbcTemplate.setFetchSize(50);
            /**
             * storageBinStream
             * String storageBin
             * String warehouseId
             * Long floorId
             * String storageSectionId
             * String spanId
             * Long statusId
             * String createdBy
             * Date createdOn
             */
            Stream<StorageBinStream> storageBinStream = jdbcTemplate.queryForStream(
                    "Select ST_BIN, WH_ID, FL_ID, ST_SEC_ID, SPAN_ID, STATUS_ID, "
                            + "CTD_BY, CTD_ON "
                            + "from tblstoragebin "
                            + "where IS_DELETED = 0 ",
                    (resultSet, rowNum) -> new StorageBinStream(
                            resultSet.getString("ST_BIN"),
                            resultSet.getString("WH_ID"),
                            resultSet.getLong("FL_ID"),
                            resultSet.getString("ST_SEC_ID"),
                            resultSet.getString("SPAN_ID"),
                            resultSet.getLong("STATUS_ID"),
                            resultSet.getString("CTD_BY"),
                            resultSet.getDate("CTD_ON")
                    ));
            return storageBinStream;
        }

        public List<ImBasicData1> getAllImBasicData1(SearchImBasicData1 searchImBasicData1) {

            String sql = "Select UOM_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, ITM_CODE, "
                    + "TEXT, MODEL, SPEC_01, SPEC_02, EAN_UPC_NO, MFR_PART, HSN_CODE, ITM_TYP_ID, ITM_GRP_ID, SUB_ITM_GRP_ID, ST_SEC_ID, TOT_STK, "
                    + "MIN_STK, MAX_STK, RE_ORD_LVL, REP_QTY, SAFTY_STCK, STATUS_ID, REF_FIELD_1, REF_FIELD_2, REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, "
                    + "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, IS_DELETED, CTD_BY,"
                    + "CTD_ON, UTD_BY, UTD_ON "
                    + "from tblimbasicdata1 "
                    + "where IS_DELETED = 0 and wh_id in (:warehouseId) ";

            SqlParameterSource param = new MapSqlParameterSource("warehouseId", searchImBasicData1.getWarehouseId());
            jdbcParamTemplate.setCacheLimit(10000);
            List<ImBasicData1> imBasicData1List = jdbcParamTemplate.query(sql,
                    param,
                    (resultSet, i) -> {
                        return toImBasicData1(resultSet);
                    });

//		List<ImBasicData1> imBasicData1List = jdbcParamTemplate.query(sql,
//												param,
//												BeanPropertyRowMapper.newInstance(ImBasicData1.class));
            return imBasicData1List;
        }

        private ImBasicData1 toImBasicData1(ResultSet resultSet) throws SQLException {
            ImBasicData1 imBasicData1 = new ImBasicData1();
            imBasicData1.setUomId(resultSet.getString("UOM_ID"));
            imBasicData1.setLanguageId(resultSet.getString("LANG_ID"));
            imBasicData1.setCompanyCodeId(resultSet.getString("C_ID"));
            imBasicData1.setPlantId(resultSet.getString("PLANT_ID"));
            imBasicData1.setWarehouseId(resultSet.getString("WH_ID"));
            imBasicData1.setItemCode(resultSet.getString("ITM_CODE"));
            imBasicData1.setDescription(resultSet.getString("TEXT"));
            imBasicData1.setModel(resultSet.getString("MODEL"));
            imBasicData1.setSpecifications1(resultSet.getString("SPEC_01"));
            imBasicData1.setSpecifications2(resultSet.getString("SPEC_02"));
            imBasicData1.setEanUpcNo(resultSet.getString("EAN_UPC_NO"));
            imBasicData1.setManufacturerPartNo(resultSet.getString("MFR_PART"));
            imBasicData1.setHsnCode(resultSet.getString("HSN_CODE"));
            imBasicData1.setItemType(resultSet.getLong("ITM_TYP_ID"));
            imBasicData1.setItemGroup(resultSet.getLong("ITM_GRP_ID"));
            imBasicData1.setSubItemGroup(resultSet.getLong("SUB_ITM_GRP_ID"));
            imBasicData1.setStorageSectionId(resultSet.getString("ST_SEC_ID"));
            imBasicData1.setTotalStock(resultSet.getDouble("TOT_STK"));
            imBasicData1.setMinimumStock(resultSet.getDouble("MIN_STK"));
            imBasicData1.setMaximumStock(resultSet.getDouble("MAX_STK"));
            imBasicData1.setReorderLevel(resultSet.getDouble("RE_ORD_LVL"));
            imBasicData1.setReplenishmentQty(resultSet.getDouble("REP_QTY"));
            imBasicData1.setSafetyStock(resultSet.getDouble("SAFTY_STCK"));
            imBasicData1.setStatusId(resultSet.getLong("STATUS_ID"));
            imBasicData1.setReferenceField1(resultSet.getString("REF_FIELD_1"));
            imBasicData1.setReferenceField2(resultSet.getString("REF_FIELD_2"));
            imBasicData1.setReferenceField3(resultSet.getString("REF_FIELD_3"));
            imBasicData1.setReferenceField4(resultSet.getString("REF_FIELD_4"));
            imBasicData1.setReferenceField5(resultSet.getString("REF_FIELD_5"));
            imBasicData1.setReferenceField6(resultSet.getString("REF_FIELD_6"));
            imBasicData1.setReferenceField7(resultSet.getString("REF_FIELD_7"));
            imBasicData1.setReferenceField8(resultSet.getString("REF_FIELD_8"));
            imBasicData1.setReferenceField9(resultSet.getString("REF_FIELD_9"));
            imBasicData1.setReferenceField10(resultSet.getString("REF_FIELD_10"));
            imBasicData1.setDeletionIndicator(resultSet.getLong("IS_DELETED"));
            imBasicData1.setCreatedBy(resultSet.getString("CTD_BY"));
            imBasicData1.setCreatedOn(resultSet.getDate("CTD_ON"));
            imBasicData1.setUpdatedBy(resultSet.getString("UTD_BY"));
            imBasicData1.setUpdatedOn(resultSet.getDate("UTD_ON"));

            return imBasicData1;
        }

        //====================================================Inventory==========================================================================

        //Add Time to Date plus 3 Hours
        public InventoryV2 addingTimeWithDateInventory(InventoryV2 inventory) throws ParseException {

            if (inventory.getCreatedOn() != null) {
                inventory.setCreatedOn(DateUtils.addTimeToDate(inventory.getCreatedOn(), 3));
            }

            if (inventory.getManufacturerDate() != null) {
                inventory.setManufacturerDate(DateUtils.addTimeToDate(inventory.getManufacturerDate(), 3));
            }

            if (inventory.getExpiryDate() != null) {
                inventory.setExpiryDate(DateUtils.addTimeToDate(inventory.getExpiryDate(), 3));
            }

            return inventory;
        }

        // POST - findInventory/v2
        public InventoryV2[] findInventoryV2(SearchInventoryV2 searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/findInventoryNew/v2");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<InventoryV2[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findInventory/v3 - stock movement report
        public InventoryV2[] findInventoryV3(SearchInventoryV2 searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/findInventory/v3");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<InventoryV2[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public InventoryV2 createInventoryV2(InventoryV2 newInventory, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/v2")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newInventory, headers);
            ResponseEntity<InventoryV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    InventoryV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public InventoryV2 updateInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes,
                                             String itemCode, String manufacturerName, String storageBin, Long stockTypeId,
                                             Long specialStockIndicatorId, InventoryV2 modifiedInventory, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(modifiedInventory, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/v2/" + stockTypeId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("manufacturerName", manufacturerName)
                        .queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                        .queryParam("stockTypeId", stockTypeId)
                        .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<InventoryV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        InventoryV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String manufacturerName, String packBarcodes, String itemCode, String storageBin,
                                         Long stockTypeId, Long specialStockIndicatorId, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/v2/" + stockTypeId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("manufacturerName", manufacturerName)
                        .queryParam("packBarcodes", packBarcodes)
                        .queryParam("itemCode", itemCode).queryParam("storageBin", storageBin)
                        .queryParam("stockTypeId", stockTypeId)
                        .queryParam("specialStockIndicatorId", specialStockIndicatorId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        /*-----------------------------------------Delivery Module-------------------------------------------------------*/
        //--------------------------------------------Delivery Header------------------------------------------------------------------------

        // GET ALL
        public DeliveryHeader[] getAllDeliveryHeader(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<DeliveryHeader[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public DeliveryHeader getDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId,
                                                String languageId, String plantId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);

                ResponseEntity<DeliveryHeader> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public DeliveryHeader createDeliveryHeader(AddDeliveryHeader newDeliveryHeader, String loginUserID, String authToken) {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newDeliveryHeader, headers);
            ResponseEntity<DeliveryHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public DeliveryHeader updateDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId, String languageId,
                                                   String plantId, String loginUserID, UpdateDeliveryHeader updateDeliveryHeader,
                                                   String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "MNRClara's RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);

                HttpEntity<?> entity = new HttpEntity<>(updateDeliveryHeader, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<DeliveryHeader> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, DeliveryHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteDeliveryHeader(String warehouseId, Long deliveryNo, String companyCodeId, String languageId,
                                            String plantId, String loginUserID, String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "MNRClara's RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader/" + deliveryNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public DeliveryHeader[] findDeliveryHeader(SearchDeliveryHeader searchDeliveryHeader, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryheader/findDeliveryHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchDeliveryHeader, headers);
                ResponseEntity<DeliveryHeader[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //--------------------------------------------Delivery Line------------------------------------------------------------------------

        // GET ALL
        public DeliveryLine[] getAllDeliveryLine(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<DeliveryLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public DeliveryLine getDeliveryLine(String warehouseId, Long deliveryNo, String itemCode, Long lineNumber,
                                            String companyCodeId, String languageId, String plantId, String invoiceNumber,
                                            String refDocNumber, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/" + deliveryNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("itemCode", itemCode)
                                .queryParam("lineNumber", lineNumber)
                                .queryParam("invoiceNumber", invoiceNumber)
                                .queryParam("refDocNumber", refDocNumber)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<DeliveryLine> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DeliveryLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST DeliveryLine
        public DeliveryLine[] createDeliveryLine(List<AddDeliveryLine> newDeliveryLine, String loginUserID, String authToken) {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newDeliveryLine, headers);
            ResponseEntity<DeliveryLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLine[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }


        // PATCH
        public DeliveryLine[] updateDeliveryLine(String loginUserID, List<UpdateDeliveryLine> updateDeliveryLine, String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "MNRClara's RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);

                HttpEntity<?> entity = new HttpEntity<>(updateDeliveryLine, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/deliveryNo/")
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<DeliveryLine[]> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, DeliveryLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteDeliveryLine(String warehouseId, Long deliveryNo, String itemCode, Long lineNumber,
                                          String refDocNumber, String invoiceNumber, String companyCodeId, String languageId,
                                          String plantId, String loginUserID, String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "MNRClara's RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/" + deliveryNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId)
                                .queryParam("itemCode", itemCode)
                                .queryParam("lineNumber", lineNumber)
                                .queryParam("invoiceNumber", invoiceNumber)
                                .queryParam("refDocNumber", refDocNumber)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public DeliveryLine[] findDeliveryLine(SearchDeliveryLine searchDeliveryLine, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/findDeliveryLine");
                HttpEntity<?> entity = new HttpEntity<>(searchDeliveryLine, headers);
                ResponseEntity<DeliveryLine[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // GET - DeliveryLineCount
        public DeliveryLineCount getDeliveryLineCount(String companyCodeId, String plantId, String languageId, String warehouseId, String driverId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/count")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("driverId", driverId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<DeliveryLineCount> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, DeliveryLineCount.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public DeliveryLineCount findDeliveryLineCount(FindDeliveryLineCount findDeliveryLineCount, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryline/findDeliveryLineCount");
                HttpEntity<?> entity = new HttpEntity<>(findDeliveryLineCount, headers);
                ResponseEntity<DeliveryLineCount> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryLineCount.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*------------------------------CycleCount-----------------------------------------------*/

        // POST - Perpetual
        public WarehouseApiResponse postPerpetual(@Valid com.tekclover.wms.core.model.warehouse.cyclecount.perpetual.Perpetual perpetual, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/warehouse/stockcount/perpetual");
            HttpEntity<?> entity = new HttpEntity<>(perpetual, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - Periodic
        public WarehouseApiResponse postPeriodic(@Valid com.tekclover.wms.core.model.warehouse.cyclecount.periodic.Periodic periodic, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/warehouse/stockcount/periodic");
            HttpEntity<?> entity = new HttpEntity<>(periodic, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

//===================================================================Outbound V2=======================================================================

        /*
         * -------------PreOutboundHeader----------------------------------------
         */
        // POST - findPreOutboundHeader
        public PreOutboundHeaderV2[] findPreOutboundHeaderV2(SearchPreOutboundHeaderV2 searchPreOutboundHeader,
                                                             String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundheader/v2/findPreOutboundHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundHeader, headers);
                ResponseEntity<PreOutboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PreOutboundHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PreOutboundHeaderV2> obList = new ArrayList<>();
//            for (PreOutboundHeaderV2 obHeader : result.getBody()) {
//                log.info("Result RefDocDate :" + obHeader.getRefDocDate());
//                if (obHeader.getRefDocDate() != null) {
//                    obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
//                }
//                if (obHeader.getRequiredDeliveryDate() != null) {
//                    obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getUpdatedOn() != null) {
//                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PreOutboundHeaderV2[obList.size()]);
            } catch (Exception e) {
                throw e;
            }
        }

        // -------------------------PreOutboundLine------------------------------------------------
        public PreOutboundLineV2[] findPreOutboundLineV2(SearchPreOutboundLineV2 searchPreOutboundLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundline/v2/findPreOutboundLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPreOutboundLine, headers);
                ResponseEntity<PreOutboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PreOutboundLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PreOutboundLineV2> obList = new ArrayList<>();
//            for (PreOutboundLineV2 obHeader : result.getBody()) {
//                log.info("Result RefDocDate :" + obHeader.getRequiredDeliveryDate());
//                if (obHeader.getRequiredDeliveryDate() != null) {
//                    obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getUpdatedOn() != null) {
//                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PreOutboundLineV2[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // --------------------------OrderManagementLine----------------------------------------------------

        // POST - findOrderManagementLine
        public OrderManagementLineV2[] findOrderManagementLineV2(SearchOrderManagementLineV2 searchOrderManagementLine,
                                                                 String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/findOrderManagementLine");
                HttpEntity<?> entity = new HttpEntity<>(searchOrderManagementLine, headers);
                ResponseEntity<OrderManagementLineV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OrderManagementLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<OrderManagementLineV2> obList = new ArrayList<>();
//            for (OrderManagementLineV2 obHeader : result.getBody()) {
//                if (obHeader.getRequiredDeliveryDate() != null) {
//                    obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
//                }
//                if (obHeader.getPickupCreatedOn() != null) {
//                    obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
//                }
//                if (obHeader.getPickupupdatedOn() != null) {
//                    obHeader.setPickupupdatedOn(DateUtils.addTimeToDate(obHeader.getPickupupdatedOn(), 3));
//                }
//                if (obHeader.getReAllocatedOn() != null) {
//                    obHeader.setReAllocatedOn(DateUtils.addTimeToDate(obHeader.getReAllocatedOn(), 3));
//                }
//                if (obHeader.getPickerAssignedOn() != null) {
//                    obHeader.setPickerAssignedOn(DateUtils.addTimeToDate(obHeader.getPickerAssignedOn(), 3));
//                }
//                if (obHeader.getPickerReassignedOn() != null) {
//                    obHeader.setPickerReassignedOn(DateUtils.addTimeToDate(obHeader.getPickerReassignedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new OrderManagementLineV2[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        public String updateRef9ANDRef10V2(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/updateRefFields");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity,
                        String.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        public OrderManagementLineV2[] doUnAllocationV2(List<OrderManagementLineV2> orderManagementLineV2,
                                                        String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(orderManagementLineV2, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/unallocate/patch")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        //PATCH
        public OrderManagementLineV2 doUnAllocationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                      String itemCode, String proposedStorageBin, String proposedPackBarCode,
                                                      String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/unallocate")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackBarCode", proposedPackBarCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        public OrderManagementLineV2 doAllocationV2(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String preOutboundNo, String refDocNumber,
                                                    String partnerCode, Long lineNumber, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                HttpClient client = HttpClients.createDefault();

                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/allocate")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        public boolean rollBackOutboundOrder(String companyCodeId, String plantId, String languageId, String warehouseId,
                                             String refDocNumber, Long outboundOrderTypeId, String authToken) throws Exception {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                HttpClient client = HttpClients.createDefault();

                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/rollBack")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("outboundOrderTypeId", outboundOrderTypeId);
                ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
                log.info("result : " + result.getStatusCode());
                return true;
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLineV2[] doAllocationV2(List<OrderManagementLineV2> orderManagementLineV2, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(orderManagementLineV2, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/allocate/patch")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLineV2[] doAssignPickerV2(List<AssignPickerV2> assignPicker, String assignedPickerId,
                                                        String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(assignPicker, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/assignPicker")
                        .queryParam("assignedPickerId", assignedPickerId).queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2[]> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, OrderManagementLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public OrderManagementLineV2 updateOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                                 String itemCode, String proposedStorageBin, String proposedPackCode,
                                                                 String loginUserID, @Valid OrderManagementLineV2 updateOrderMangementLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOrderMangementLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/" + refDocNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OrderManagementLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OrderManagementLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                   String itemCode, String proposedStorageBin, String proposedPackCode,
                                                   String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "ordermanagementline/v2/" + refDocNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*--------------------------PickupHeader----------------------------------------------------*/
        // POST - Finder
        public PickupHeaderV2[] findPickupHeaderV2(SearchPickupHeaderV2 searchPickupHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/findPickupHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupHeader, headers);
                ResponseEntity<PickupHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickupHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PickupHeaderV2> obList = new ArrayList<>();
//            for (PickupHeaderV2 obHeader : result.getBody()) {
//                if (obHeader.getPickupReversedOn() != null) {
//                    obHeader.setPickupReversedOn(DateUtils.addTimeToDate(obHeader.getPickupReversedOn(), 3));
//                }
//                if (obHeader.getPickupCreatedOn() != null) {
//                    obHeader.setPickupCreatedOn(DateUtils.addTimeToDate(obHeader.getPickupCreatedOn(), 3));
//                }
//                if (obHeader.getPickUpdatedOn() != null) {
//                    obHeader.setPickUpdatedOn(DateUtils.addTimeToDate(obHeader.getPickUpdatedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PickupHeaderV2[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public PickupHeaderV2 updatePickupHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
                                                   Long lineNumber, String itemCode, String loginUserID,
                                                   @Valid PickupHeaderV2 updatePickupHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/" + pickupNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PickupHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PickupHeaderV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
//    public PickupHeaderV2[] patchAssignedPickerIdInPickupHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID,
//                                                                  @Valid List<PickupHeaderV2> updatePickupHeaderList, String authToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
//            headers.add("Authorization", "Bearer " + authToken);
//            HttpEntity<?> entity = new HttpEntity<>(updatePickupHeaderList, headers);
//            HttpClient client = HttpClients.createDefault();
//            RestTemplate restTemplate = getRestTemplate();
//            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
//
//            UriComponentsBuilder builder = UriComponentsBuilder
//                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/update-assigned-picker")
//                    .queryParam("companyCodeId", companyCodeId)
//                    .queryParam("plantId", plantId)
//                    .queryParam("languageId", languageId)
//                    .queryParam("warehouseId", warehouseId)
//                    .queryParam("loginUserID", loginUserID);
//            ResponseEntity<PickupHeaderV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
//                    entity, PickupHeaderV2[].class);
//            log.info("result : " + result.getStatusCode());
//            return result.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

        public PickupHeaderV2[] patchAssignedPickerIdInPickupHeaderV2(@Valid List<PickupHeaderV2> updatePickupHeaderList, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupHeaderList, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/update-assigned-picker");
                ResponseEntity<PickupHeaderV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PickupHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePickupHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
                                            Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode,
                                            String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/" + pickupNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("proposedStorageBin", proposedStorageBin)
                        .queryParam("proposedPackCode", proposedPackCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

    /**
     *
     * @param updatePickupHeaderList
     * @param authToken
     * @return
     */
    public PickupHeaderV2[] updateAssignPickerPrintConfirm(@Valid List<PickupHeaderV2> updatePickupHeaderList, String loginUserID, String authToken) {
        try {
            AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            HttpEntity<?> entity = new HttpEntity<>(updatePickupHeaderList, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupheader/v2/assign-picker")
                    .queryParam("loginUserID", loginUserID);
            ResponseEntity<PickupHeaderV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                    entity, PickupHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

        /*--------------------------PickupLine----------------------------------------------------*/
        // GET
        public InventoryV2[] getAdditionalBinsV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String itemCode, Long obOrdertypeId, String proposedPackBarCode, String manufacturerName,
                                                 String proposedStorageBin, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2/additionalBins")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("itemCode", itemCode)
                        .queryParam("obOrdertypeId", obOrdertypeId)
                        .queryParam("proposedPackBarCode", proposedPackBarCode)
                        .queryParam("manufacturerName", manufacturerName)
                        .queryParam("proposedStorageBin", proposedStorageBin);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InventoryV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, InventoryV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public PickupLineV2[] createPickupLineV2(@Valid List<AddPickupLine> newPickupLine, String loginUserID,
                                                 String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPickupLine, headers);
            ResponseEntity<PickupLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    PickupLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - findPickupLine
        public PickupLineV2[] findPickupLineV2(SearchPickupLineV2 searchPickupLine, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2/findPickupLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPickupLine, headers);
                ResponseEntity<PickupLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickupLineV2[].class);

                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PickupLineV2> pickupLineList = new ArrayList<>();
//            for (PickupLineV2 pickupLine : result.getBody()) {
//                if (pickupLine.getPickupCreatedOn() != null) {
//                    pickupLine.setPickupCreatedOn(DateUtils.addTimeToDate(pickupLine.getPickupCreatedOn(), 3));
//                }
//                if (pickupLine.getPickupConfirmedOn() != null) {
//                    pickupLine.setPickupConfirmedOn(DateUtils.addTimeToDate(pickupLine.getPickupConfirmedOn(), 3));
//                }
//                if (pickupLine.getPickupUpdatedOn() != null) {
//                    pickupLine.setPickupUpdatedOn(DateUtils.addTimeToDate(pickupLine.getPickupUpdatedOn(), 3));
//                }
//                if (pickupLine.getPickupReversedOn() != null) {
//                    pickupLine.setPickupReversedOn(DateUtils.addTimeToDate(pickupLine.getPickupReversedOn(), 3));
//                }
//                pickupLineList.add(pickupLine);
//            }
//            return pickupLineList.toArray(new PickupLineV2[pickupLineList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH - BarcodeId
        public ImPartner updatePickupLineForBarcodeId(UpdateBarcodeInput updateBarcodeInput, String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS's RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);
                HttpEntity<?> entity = new HttpEntity<>(updateBarcodeInput, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2/barcodeId");
                ResponseEntity<ImPartner> result =
                        restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ImPartner.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PickupLineV2 updatePickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                               String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                               String pickedPackCode, String actualHeNo, String loginUserID, @Valid PickupLineV2 updatePickupLine,
                                               String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePickupLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2/" + actualHeNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("pickupNumber", pickupNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("pickedStorageBin", pickedStorageBin)
                        .queryParam("pickedPackCode", pickedPackCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PickupLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PickupLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                          String refDocNumber, String partnerCode, Long lineNumber, String pickupNumber, String itemCode,
                                          String actualHeNo, String pickedStorageBin, String pickedPackCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v2/" + actualHeNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("pickupNumber", pickupNumber)
                        .queryParam("itemCode", itemCode)
                        .queryParam("pickedStorageBin", pickedStorageBin)
                        .queryParam("pickedPackCode", pickedPackCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*-----------------------------QualityHeader---------------------------------------------------------*/

        // POST - CREATE QUALITY HEADER
        public QualityHeaderV2 createQualityHeaderV2(@Valid QualityHeaderV2 newQualityHeader, String loginUserID,
                                                     String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/v2")
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(newQualityHeader, headers);
                ResponseEntity<QualityHeaderV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityHeaderV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findQualityHeader
        public QualityHeaderV2[] findQualityHeaderV2(SearchQualityHeaderV2 searchQualityHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/v2/findQualityHeaderNew");
                HttpEntity<?> entity = new HttpEntity<>(searchQualityHeader, headers);
                ResponseEntity<QualityHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
//
//            List<QualityHeaderV2> obList = new ArrayList<>();
//            for (QualityHeaderV2 obHeader : result.getBody()) {
//                if (obHeader.getQualityConfirmedOn() != null) {
//                    obHeader.setQualityConfirmedOn(DateUtils.addTimeToDate(obHeader.getQualityConfirmedOn(), 3));
//                }
//                if (obHeader.getQualityCreatedOn() != null) {
//                    obHeader.setQualityCreatedOn(DateUtils.addTimeToDate(obHeader.getQualityCreatedOn(), 3));
//                }
//                if (obHeader.getQualityUpdatedOn() != null) {
//                    obHeader.setQualityUpdatedOn(DateUtils.addTimeToDate(obHeader.getQualityUpdatedOn(), 3));
//                }
//                if (obHeader.getQualityReversedOn() != null) {
//                    obHeader.setQualityReversedOn(DateUtils.addTimeToDate(obHeader.getQualityReversedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new QualityHeaderV2[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public QualityHeaderV2 updateQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                                     String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID,
                                                     @Valid QualityHeaderV2 updateQualityHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateQualityHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/v2/" + qualityInspectionNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("pickupNumber", pickupNumber)
                        .queryParam("actualHeNo", actualHeNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<QualityHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, QualityHeaderV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                             String refDocNumber, String partnerCode, String pickupNumber, String qualityInspectionNo,
                                             String actualHeNo, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityheader/v2/" + qualityInspectionNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("pickupNumber", pickupNumber)
                        .queryParam("actualHeNo", actualHeNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*-----------------------------QualityLine------------------------------------------------------------*/
        // POST - findQualityLine
        public QualityLineV2[] findQualityLineV2(SearchQualityLineV2 searchQualityLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/v2/findQualityLine");
                HttpEntity<?> entity = new HttpEntity<>(searchQualityLine, headers);
                ResponseEntity<QualityLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<QualityLineV2> obList = new ArrayList<>();
//            for (QualityLineV2 obHeader : result.getBody()) {
//                if (obHeader.getQualityReversedOn() != null) {
//                    obHeader.setQualityReversedOn(DateUtils.addTimeToDate(obHeader.getQualityReversedOn(), 3));
//                }
//                if (obHeader.getQualityCreatedOn() != null) {
//                    obHeader.setQualityCreatedOn(DateUtils.addTimeToDate(obHeader.getQualityCreatedOn(), 3));
//                }
//                if (obHeader.getQualityUpdatedOn() != null) {
//                    obHeader.setQualityUpdatedOn(DateUtils.addTimeToDate(obHeader.getQualityUpdatedOn(), 3));
//                }
//                if (obHeader.getQualityConfirmedOn() != null) {
//                    obHeader.setQualityConfirmedOn(DateUtils.addTimeToDate(obHeader.getQualityConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new QualityLineV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findPickListHeader
        public PickListHeader[] findPickListHeader(SearchPickListHeader searchPickListHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/invoice/findPickListHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPickListHeader, headers);
                ResponseEntity<PickListHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickListHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public QualityLineV2[] createQualityLineV2(@Valid List<AddQualityLine> newQualityLine, String loginUserID,
                                                   String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/v2").queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(newQualityLine, headers);
                ResponseEntity<QualityLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, QualityLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // PATCH
        public QualityLineV2 updateQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                 String refDocNumber, String partnerCode, Long lineNumber, String qualityInspectionNo,
                                                 String itemCode, String loginUserID, @Valid QualityLineV2 updateQualityLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateQualityLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/v2/" + partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("qualityInspectionNo", qualityInspectionNo)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<QualityLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        QualityLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                           String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                           String qualityInspectionNo, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "qualityline/v2/" + partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("qualityInspectionNo", qualityInspectionNo)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                throw e;
            }
        }

        /*
         * ----------------------OutboundHeader-----------------------------------------
         */
        // POST - findOutboundHeader
        public OutboundHeaderV2[] findOutboundHeaderV2(SearchOutboundHeaderV2 requestData, String authToken)
                throws ParseException {
            try {
                SearchOutboundHeaderModel requestDataForService = new SearchOutboundHeaderModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getStartDeliveryConfirmedOn() != null) {
                    if (requestData.getStartDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getStartDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getStartDeliveryConfirmedOn()));
                    }
                }
//            Integer flag = 0;
                if (requestData.getEndDeliveryConfirmedOn() != null) {
                    if (requestData.getEndDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getEndDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getEndDeliveryConfirmedOn()));
//                    flag = 1;
                    }
                }
                if (requestData.getStartOrderDate() != null) {
                    requestDataForService
                            .setStartOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getStartOrderDate()));
                }
                if (requestData.getEndOrderDate() != null) {
                    requestDataForService.setEndOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getEndOrderDate()));
                }
                if (requestData.getStartRequiredDeliveryDate() != null) {
                    requestDataForService.setStartRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getStartRequiredDeliveryDate()));
                }
                if (requestData.getEndRequiredDeliveryDate() != null) {
                    requestDataForService.setEndRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getEndRequiredDeliveryDate()));
                }
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/v2/findOutboundHeaderStream");
//                    .queryParam("flag", flag);
                HttpEntity<?> entity = new HttpEntity<>(requestDataForService, headers);
                ResponseEntity<OutboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<OutboundHeaderV2> obList = new ArrayList<>();
//            for (OutboundHeaderV2 obHeader : result.getBody()) {
//                log.info("Result getDeliveryConfirmedOn :" + obHeader.getDeliveryConfirmedOn());
//                if (obHeader.getRefDocDate() != null) {
//                    obHeader.setRefDocDate(DateUtils.addTimeToDate(obHeader.getRefDocDate(), 3));
//                }
//                if (obHeader.getRequiredDeliveryDate() != null) {
//                    obHeader.setRequiredDeliveryDate(DateUtils.addTimeToDate(obHeader.getRequiredDeliveryDate(), 3));
//                }
//                if (obHeader.getDeliveryConfirmedOn() != null) {
//                    obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getUpdatedOn() != null) {
//                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new OutboundHeaderV2[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findOutboundHeader - This Method for seperate consignment Tab in Delivery
        public OutboundHeaderV2[] findOutboundHeaderDeliveryV2(SearchOutboundHeaderV2 requestData, String authToken)
                throws ParseException {
            try {
                SearchOutboundHeaderModel requestDataForService = new SearchOutboundHeaderModel();
                BeanUtils.copyProperties(requestData, requestDataForService, CommonUtils.getNullPropertyNames(requestData));
                if (requestData.getStartDeliveryConfirmedOn() != null) {
                    if (requestData.getStartDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getStartDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setStartDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getStartDeliveryConfirmedOn()));
                    }
                }
                if (requestData.getEndDeliveryConfirmedOn() != null) {
                    if (requestData.getEndDeliveryConfirmedOn().length() < 11) {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToYYYYMMDD(requestData.getEndDeliveryConfirmedOn()));
                    } else {
                        requestDataForService.setEndDeliveryConfirmedOn(
                                DateUtils.convertStringToDateWithTime(requestData.getEndDeliveryConfirmedOn()));
                    }
                }
                if (requestData.getStartOrderDate() != null) {
                    requestDataForService
                            .setStartOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getStartOrderDate()));
                }
                if (requestData.getEndOrderDate() != null) {
                    requestDataForService.setEndOrderDate(DateUtils.convertStringToYYYYMMDD(requestData.getEndOrderDate()));
                }
                if (requestData.getStartRequiredDeliveryDate() != null) {
                    requestDataForService.setStartRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getStartRequiredDeliveryDate()));
                }
                if (requestData.getEndRequiredDeliveryDate() != null) {
                    requestDataForService.setEndRequiredDeliveryDate(
                            DateUtils.convertStringToYYYYMMDD(requestData.getEndRequiredDeliveryDate()));
                }
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/v2/findOutboundHeader/delivery");
                HttpEntity<?> entity = new HttpEntity<>(requestDataForService, headers);
                ResponseEntity<OutboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findOutBoundHeader/Rfd
        public OutboundHeaderV2[] findOutboundHeaderRfd(SearchOutboundHeaderV2 searchOutboundHeaderV2, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/v2/findOutboundHeaderRfd");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundHeaderV2, headers);
                ResponseEntity<OutboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<OutboundLine> obList = new ArrayList<>();
//            for (OutboundLine obHeader : result.getBody()) {
//                if (obHeader.getDeliveryConfirmedOn() != null) {
//                    obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getUpdatedOn() != null) {
//                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//                }
//                if (obHeader.getReversedOn() != null) {
//                    obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new OutboundLine[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public OutboundHeaderV2 updateOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String preOutboundNo, String refDocNumber, String partnerCode,
                                                       @Valid OutboundHeaderV2 updateOutboundHeader, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOutboundHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/v2/" + preOutboundNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OutboundHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, OutboundHeaderV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundheader/" + preOutboundNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*
         * ----------------------OutboundLine-------------------------------------------
         */

        // GET - /outboundline/delivery/confirmation
        public OutboundLineV2[] deliveryConfirmationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String preOutboundNo, String refDocNumber,
                                                       String partnerCode, String loginUserID, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/delivery/confirmation")
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, OutboundLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // POST - findOutboundLine
        public OutboundLineV2[] findOutboundLineV2(SearchOutboundLineV2 searchOutboundLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/findOutboundLineNew");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<OutboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<OutboundLineV2> obList = new ArrayList<>();
//            for (OutboundLineV2 obHeader : result.getBody()) {
//                if (obHeader.getDeliveryConfirmedOn() != null) {
//                    obHeader.setDeliveryConfirmedOn(DateUtils.addTimeToDate(obHeader.getDeliveryConfirmedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getUpdatedOn() != null) {
//                    obHeader.setUpdatedOn(DateUtils.addTimeToDate(obHeader.getUpdatedOn(), 3));
//                }
//                if (obHeader.getReversedOn() != null) {
//                    obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new OutboundLineV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findOutboundLineStream for calling in reports
        public OutboundLineV2[] findOutboundLineStreamV2(SearchOutboundLineV2 searchOutboundLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/findOutboundLineStream");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundLine, headers);
                ResponseEntity<OutboundLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, OutboundLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH ----
        public OutboundLineV2 updateOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                   String itemCode, String loginUserID, @Valid OutboundLineV2 updateOutboundLine, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateOutboundLine, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/" + lineNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<OutboundLineV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        OutboundLineV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        // DELETE
        public boolean deleteOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode,
                                            Long lineNumber, String itemCode, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/" + lineNumber)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("preOutboundNo", preOutboundNo)
                        .queryParam("refDocNumber", refDocNumber)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("itemCode", itemCode)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /*
         * ---------------------------------DeliveryConfirmation----------------------------
         */
        // POST - findDeliveryConfirmation
        public DeliveryConfirmation[] findDeliveryConfirmation(SearchDeliveryConfirmation searchDeliveryConfirmation, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "deliveryconfirmation/v3/find");
                HttpEntity<?> entity = new HttpEntity<>(searchDeliveryConfirmation, headers);
                ResponseEntity<DeliveryConfirmation[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DeliveryConfirmation[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                throw e;
            }
        }

        /*
         * ---------------------------------OutboundReversal----------------------------
         */
        // POST - findOutboundReversal
        public OutboundReversalV2[] findOutboundReversalV2(SearchOutboundReversalV2 searchOutboundReversal, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundreversal/v2/findOutboundReversalNew");
                HttpEntity<?> entity = new HttpEntity<>(searchOutboundReversal, headers);
                ResponseEntity<OutboundReversalV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, OutboundReversalV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<OutboundReversalV2> obList = new ArrayList<>();
//            for (OutboundReversalV2 obHeader : result.getBody()) {
//                if (obHeader.getReversedOn() != null) {
//                    obHeader.setReversedOn(DateUtils.addTimeToDate(obHeader.getReversedOn(), 3));
//                }
//
//                obList.add(obHeader);
//            }
//            return obList.toArray(new OutboundReversalV2[obList.size()]);

            } catch (Exception e) {
                throw e;
            }
        }

        // GET
        public OutboundReversalV2[] doReversalV2(String refDocNumber, String itemCode, String manufacturerName, String loginUserID, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundline/v2/reversal/new")
                        .queryParam("refDocNumber", refDocNumber).queryParam("itemCode", itemCode).queryParam("manufacturerName", manufacturerName)
                        .queryParam("loginUserID", loginUserID);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OutboundReversalV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, OutboundReversalV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //=========================================================================================================

        //=============================================== Outbound-Order ==========================================================

        // POST - SO
        public WarehouseApiResponse postSOV2(@Valid ShipmentOrderV2 shipmenOrder, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/so/v2");
            HttpEntity<?> entity = new HttpEntity<>(shipmenOrder, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - SO
        public WarehouseApiResponse postShipmentOrderV2(@Valid List<ShipmentOrderV2> shipmenOrder, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/so/bulk/v2");
            HttpEntity<?> entity = new HttpEntity<>(shipmenOrder, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        //Post SalesOrderV2 - Upload
        public WarehouseApiResponse postSalesOrderV2(@Valid List<SalesOrderV2> salesOrderV2, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/upload/salesorderv2");
            HttpEntity<?> entity = new HttpEntity<>(salesOrderV2, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post SalesOrderV2 - Upload
        public WarehouseApiResponse postSalesOrderV4(@Valid List<SalesOrderV2> salesOrderV2, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/upload/salesorder/V4");
            HttpEntity<?> entity = new HttpEntity<>(salesOrderV2, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //	Post SalesOrderV2 from SAP
        public OutboundHeaderV2[] postSalesOrderV2OrderFullfillment(List<OutboundIntegrationHeaderV2> obIntegrationHeaderV2List, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/order/fullfillment/v3");
            HttpEntity<?> entity = new HttpEntity<>(obIntegrationHeaderV2List, headers);
            ResponseEntity<OutboundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundHeaderV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post SalesOrderV2 - Upload
        public WarehouseApiResponse postDeliveryConfirmationV3 (@Valid DeliveryConfirmationV3 deliveryConfirmationV3, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/upload/deliveryConfirmationV3");
            HttpEntity<?> entity = new HttpEntity<>(deliveryConfirmationV3, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        // Post SalesOrderV2 - Upload
        // Modified for SAP orders - 30/06/2025
        // Aakash vinayak
        public WarehouseApiResponse postDeliveryConfirmationV4(List<DeliveryConfirmationSAP> deliveryConfirmationSAPList, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/SAP/deliveryConfirmationV4");
            HttpEntity<?> entity = new HttpEntity<>(deliveryConfirmationSAPList, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post OutboundReversalV3 - Upload
        public WarehouseApiResponse postOutboundReversalUploadV3 (@Valid ReversalV3 reversalV3, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/upload/outboundReversalV3");
            HttpEntity<?> entity = new HttpEntity<>(reversalV3, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post SalesOrderV2
        public WarehouseApiResponse postSalesOrderV2(@Valid SalesOrderV2 salesOrderV2, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/salesorderv2");
            HttpEntity<?> entity = new HttpEntity<>(salesOrderV2, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post ReturnPOV2
        public WarehouseApiResponse postReturnPoV2(@Valid ReturnPOV2 returnPOV2, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/returnpov2");
            HttpEntity<?> entity = new HttpEntity<>(returnPOV2, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post InterWarehouseTransferOutV2
        public WarehouseApiResponse postInterWhTransferOutV2(@Valid InterWarehouseTransferOutV2 iWhTransferOutV2, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/interwarehousetransferoutv2");
            HttpEntity<?> entity = new HttpEntity<>(iWhTransferOutV2, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //Post SalesInvoice
        public WarehouseApiResponse postSalesInvoice(@Valid SalesInvoice salesInvoice, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/outbound/salesinvoice");
            HttpEntity<?> entity = new HttpEntity<>(salesInvoice, headers);
            ResponseEntity<WarehouseApiResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        }

        //=================================================================V2===================================================

        // ---------------------------------PerpetualHeader----------------------------------------------------
        // GET ALL
        public PerpetualHeaderEntityV2[] getPerpetualHeadersV2(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PerpetualHeaderEntityV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PerpetualHeaderEntityV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PerpetualHeaderEntityV2> obList = new ArrayList<>();
//            for (PerpetualHeaderEntityV2 obHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDatePerpetualHeader(obHeader));
//            }
//            return obList.toArray(new PerpetualHeaderEntityV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public PerpetualHeaderEntityV2 addingTimeWithDatePerpetualHeader(PerpetualHeaderEntityV2 obHeader) throws ParseException {

            if (obHeader.getCountedOn() != null) {
                obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
            }
            if (obHeader.getCreatedOn() != null) {
                obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
            }
            if (obHeader.getConfirmedOn() != null) {
                obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
            }

            return obHeader;
        }

        public PerpetualHeaderEntityV2 getPerpetualHeaderV2(String companyCodeId, String plantId, String languageId,
                                                            String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                            Long movementTypeId, Long subMovementTypeId, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/" + cycleCountNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId)
                        .queryParam("subMovementTypeId", subMovementTypeId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PerpetualHeaderEntityV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PerpetualHeaderEntityV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualHeader
        public PerpetualHeaderEntityV2[] findPerpetualHeaderV2(SearchPerpetualHeaderV2 searchPerpetualHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/findPerpetualHeaderEntity");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);
                ResponseEntity<PerpetualHeaderEntityV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PerpetualHeaderEntityV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PerpetualHeaderEntityV2> obList = new ArrayList<>();
//            for (PerpetualHeaderEntityV2 obHeader : result.getBody()) {
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PerpetualHeaderEntityV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualHeaderEntity
        public PerpetualHeaderV2[] findPerpetualHeaderEntityV2(SearchPerpetualHeaderV2 searchPerpetualHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/findPerpetualHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);
                ResponseEntity<PerpetualHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PerpetualHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PerpetualHeaderV2> obList = new ArrayList<>();
//            for (PerpetualHeaderV2 obHeader : result.getBody()) {
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PerpetualHeaderV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - CREATE
        public PerpetualHeaderEntityV2 createPerpetualHeaderV2(@Valid PerpetualHeaderEntityV2 newPerpetualHeader, String loginUserID,
                                                               String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPerpetualHeader, headers);
            ResponseEntity<PerpetualHeaderEntityV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PerpetualHeaderEntityV2.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - Perpetual Header Line
        public WarehouseApiResponse postPerpetualHeaderV2(@Valid Perpetual newPerpetualHeader, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/stockcount/perpetual");
            HttpEntity<?> entity = new HttpEntity<>(newPerpetualHeader, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - RUN
        public PerpetualLineV2[] runPerpetualHeaderV2(@Valid RunPerpetualHeader runPerpetualHeader, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/run");
            HttpEntity<?> entity = new HttpEntity<>(runPerpetualHeader, headers);
            ResponseEntity<PerpetualLineV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PerpetualLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - RUN - Stream
        public PerpetualLineV2[] runPerpetualHeaderNewV2(@Valid RunPerpetualHeader runPerpetualHeader, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/runStream");
            HttpEntity<?> entity = new HttpEntity<>(runPerpetualHeader, headers);
            ResponseEntity<PerpetualLineV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, PerpetualLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public PerpetualHeaderV2 updatePerpetualHeaderV2(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                                         Long movementTypeId, Long subMovementTypeId, String loginUserID,
                                                         @Valid PerpetualHeaderEntityV2 updatePerpetualHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/" + cycleCountNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId)
                        .queryParam("subMovementTypeId", subMovementTypeId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<PerpetualHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PerpetualHeaderV2.class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePerpetualHeaderV2(String companyCodeId, String plantId, String languageId,
                                               String warehouseId, Long cycleCountTypeId, String cycleCountNo,
                                               Long movementTypeId, Long subMovementTypeId, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualheader/v2/" + cycleCountNo)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("movementTypeId", movementTypeId)
                        .queryParam("subMovementTypeId", subMovementTypeId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualLine
        public PerpetualLineV2[] findPerpetualLineV2(SearchPerpetualLineV2 searchPerpetualLine, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/v2/findPerpetualLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualLine, headers);
                ResponseEntity<PerpetualLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PerpetualLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PerpetualLineV2> obList = new ArrayList<>();
//            for (PerpetualLineV2 obHeader : result.getBody()) {
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PerpetualLineV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PerpetualLineV2[] updateAssingHHTUserV2(List<AssignHHTUserCC> assignHHTUser, String loginUserID,
                                                       String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/v2/assigingHHTUser")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PerpetualLineV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PerpetualLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PerpetualUpdateResponseV2 updatePerpetualLineV2(String cycleCountNo,
                                                               List<PerpetualLineV2> updatePerpetualLine, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/v2/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PerpetualUpdateResponseV2> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, PerpetualUpdateResponseV2.class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public WarehouseApiResponse updatePerpetualLineConfirmV2(String cycleCountNo, List<PerpetualLineV2> updatePerpetualLine,
                                                                 String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/v2/confirm/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<WarehouseApiResponse> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, WarehouseApiResponse.class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PerpetualLineV2[] updatePerpetualZeroStkLine(List<PerpetualZeroStockLine> updatePerpetualLine,
                                                            String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePerpetualLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "perpetualline/v2/createPerpetualFromZeroStk")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PerpetualLineV2[]> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PerpetualLineV2[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicLineV2[] updatePeriodicZeroStkLine(List<PeriodicZeroStockLine> updatePeriodicLine,
                                                          String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/v2/createPeriodicFromZeroStk")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PeriodicLineV2[]> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PeriodicLineV2[].class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // ---------------------------------PeriodicHeader----------------------------------------------------
        // GET ALL
        public PeriodicHeaderEntity[] getPeriodicHeadersV2(String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PeriodicHeaderEntity[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.GET, entity, PeriodicHeaderEntity[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PeriodicHeaderEntity> obList = new ArrayList<>();
//            for (PeriodicHeaderEntity obHeader : result.getBody()) {
//
//                obList.add(addingTimeWithDatePeriodicHeaderEntityV2(obHeader));
//            }
//            return obList.toArray(new PeriodicHeaderEntity[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //Add Time to Date plus 3 Hours
        public PeriodicHeaderEntity addingTimeWithDatePeriodicHeaderEntityV2(PeriodicHeaderEntity obHeader) throws ParseException {

            if (obHeader.getConfirmedOn() != null) {
                obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
            }
            if (obHeader.getCreatedOn() != null) {
                obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
            }
            if (obHeader.getCountedOn() != null) {
                obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
            }

            return obHeader;
        }

        // GET
        public PeriodicHeaderEntityV2 getPeriodicHeaderV2(String warehouseId, String companyCode, String plantId, String languageId,
                                                          Long cycleCountTypeId, String cycleCountNo, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/" + cycleCountNo)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PeriodicHeaderEntityV2> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PeriodicHeaderEntityV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PeriodicHeaderEntityV2> obList = new ArrayList<>();
//            for (PeriodicHeaderEntityV2 obHeader : result.getBody()) {
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PeriodicHeaderEntityV2[obList.size()]);


            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPeriodicHeader
        public PeriodicHeaderEntity[] findPeriodicHeaderEntity(SearchPeriodicHeader searchPeriodicHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/findPeriodicHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicHeader, headers);
                ResponseEntity<PeriodicHeaderEntity[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeaderEntity[].class);

                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PeriodicHeaderEntity> obList = new ArrayList<>();
//            for (PeriodicHeaderEntity obHeader : result.getBody()) {
//                obList.add(addingTimeWithDatePeriodicHeaderEntity(obHeader));
//            }
//            return obList.toArray(new PeriodicHeaderEntity[obList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPerpetualHeader
        public PeriodicHeaderEntityV2[] findPeriodicHeaderV2(SearchPeriodicHeader searchPerpetualHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/findPeriodicHeaderEntity");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);
                ResponseEntity<PeriodicHeaderEntityV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PeriodicHeaderEntityV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            List<PeriodicHeaderEntityV2> obList = new ArrayList<>();
//            for (PeriodicHeaderEntityV2 obHeader : result.getBody()) {
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PeriodicHeaderEntityV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPeriodicHeaderEntity
        public PeriodicHeaderEntityV2[] findPeriodicHeaderEntityV2(SearchPeriodicHeader searchPerpetualHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/findPeriodicHeaderEntity");
                HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeader, headers);
                ResponseEntity<PeriodicHeaderEntityV2[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, PeriodicHeaderEntityV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
//            List<PeriodicHeaderEntityV2> obList = new ArrayList<>();
//            for (PeriodicHeaderEntityV2 obHeader : result.getBody()) {
//                if (obHeader.getCountedOn() != null) {
//                    obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//                }
//                if (obHeader.getCreatedOn() != null) {
//                    obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//                }
//                if (obHeader.getConfirmedOn() != null) {
//                    obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//                }
//                obList.add(obHeader);
//            }
//            return obList.toArray(new PeriodicHeaderEntityV2[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findPeriodicHeader -Stream
        public PeriodicHeaderV2[] findPeriodicHeaderStreamV2(SearchPeriodicHeader searchPeriodicHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/findPeriodicHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicHeader, headers);
                ResponseEntity<PeriodicHeaderV2[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeaderV2[].class);

                log.info("result : " + result.getStatusCode());
                return result.getBody();

//            Arrays.stream(result.getBody()).forEach(n -> {
//                        if (n.getConfirmedOn() != null) {
//                            try {
//                                n.setConfirmedOn(DateUtils.addTimeToDate(n.getConfirmedOn(), 3));
//                            } catch (ParseException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                        if (n.getCreatedOn() != null) {
//                            try {
//                                n.setCreatedOn(DateUtils.addTimeToDate(n.getCreatedOn(), 3));
//                            } catch (ParseException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                        if (n.getCountedOn() != null) {
//                            try {
//                                n.setCountedOn(DateUtils.addTimeToDate(n.getCountedOn(), 3));
//                            } catch (ParseException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    }
//            );
//			List<PeriodicHeader> obList = new ArrayList<>();
//			for (PeriodicHeader obHeader : result.getBody()) {
//
//				if(obHeader.getConfirmedOn() != null) {
//					obHeader.setConfirmedOn(DateUtils.addTimeToDate(obHeader.getConfirmedOn(), 3));
//				}
//				if(obHeader.getCreatedOn() != null) {
//					obHeader.setCreatedOn(DateUtils.addTimeToDate(obHeader.getCreatedOn(), 3));
//				}
//				if(obHeader.getCountedOn() != null) {
//					obHeader.setCountedOn(DateUtils.addTimeToDate(obHeader.getCountedOn(), 3));
//				}
//
//				obList.add(obHeader);
//			}
//			return obList.toArray(new PeriodicHeader[obList.size()]);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - CREATE
        public PeriodicHeaderEntity createPeriodicHeaderV2(@Valid AddPeriodicHeader newPeriodicHeader, String loginUserID,
                                                           String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader").queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPeriodicHeader, headers);
            ResponseEntity<PeriodicHeaderEntity> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, PeriodicHeaderEntity.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - CREATE V2
        public WarehouseApiResponse postPeriodicHeaderV2(@Valid Periodic newPeriodic, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/stockcount/periodic");
            HttpEntity<?> entity = new HttpEntity<>(newPeriodic, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                    entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // GET ALL
        public Page<?> runPeriodicHeaderV2(String warehouseId, Integer pageNo, Integer pageSize, String sortBy,
                                           String authToken) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("ClientGeneral-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + authToken);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/run/pagination")
                        .queryParam("pageNo", pageNo).queryParam("pageSize", pageSize).queryParam("sortBy", sortBy)
                        .queryParam("warehouseId", warehouseId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>> responseType =
                        new ParameterizedTypeReference<PaginatedResponse<PeriodicLineEntity>>() {
                        };
                ResponseEntity<PaginatedResponse<PeriodicLineEntity>> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, responseType);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicHeaderV2 updatePeriodicHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                       Long cycleCountTypeId, String cycleCountNo,
                                                       String loginUserID, PeriodicHeaderEntityV2 updatePeriodicHeader, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicHeader, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/" + cycleCountNo)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<PeriodicHeaderV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicHeaderV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deletePeriodicHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                              Long cycleCountTypeId, String cycleCountNo,
                                              String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicheader/v2/" + cycleCountNo)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("cycleCountTypeId", cycleCountTypeId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //-------------------------------------PeriodicLine--------------------------------------------------------
        // FIND
        public PeriodicLineV2[] findPeriodicLineV2(SearchPeriodicLineV2 searchPeriodicLine, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/v2/findPeriodicLine");
                HttpEntity<?> entity = new HttpEntity<>(searchPeriodicLine, headers);
                ResponseEntity<PeriodicLineV2[]> result = getRestTemplate()
                        .exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicLineV2[].class);

                log.info("result : " + result.getStatusCode());
                return result.getBody();
//            List<PeriodicLineV2> periodicLineList = new ArrayList<>();
//            for (PeriodicLineV2 periodicLine : result.getBody()) {
//                periodicLine.setCreatedOn(DateUtils.addTimeToDate(periodicLine.getCreatedOn(), 3));
//                periodicLineList.add(periodicLine);
//            }
//            return periodicLineList.toArray(new PeriodicLineV2[periodicLineList.size()]);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicLineV2[] updatePeriodicLineAssingHHTUserV2(List<AssignHHTUserCC> assignHHTUser, String loginUserID,
                                                                  String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(assignHHTUser, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/v2/assigingHHTUser")
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PeriodicLineV2[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicLineV2[].class);
                log.info("result : " + result);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // PATCH
        public PeriodicUpdateResponseV2 updatePeriodicLineV2(String cycleCountNo, List<PeriodicLineV2> updatePeriodicLine,
                                                             String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicLine, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/v2/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PeriodicUpdateResponseV2> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, PeriodicUpdateResponseV2.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // PATCH
        public WarehouseApiResponse updatePeriodicLineConfirmV2(String cycleCountNo, List<PeriodicLineV2> updatePeriodicLineV2,
                                                                String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updatePeriodicLineV2, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "periodicline/v2/confirm/" + cycleCountNo)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<WarehouseApiResponse> result = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.PATCH, entity, WarehouseApiResponse.class);
                log.info("result : " + result.getBody());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // ---------------------------------StockAdjustment----------------------------------------------------

        public StockAdjustment[] getStockAdjustment(String companyCode, String plantId, String languageId,
                                                    String warehouseId, Long stockAdjustmentKey, String itemCode,
                                                    String manufacturerName, String storageBin, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "stockadjustment/" + stockAdjustmentKey)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("itemCode", itemCode)
                        .queryParam("manufacturerName", manufacturerName)
                        .queryParam("storageBin", storageBin);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<StockAdjustment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, StockAdjustment[].class);
                log.info("result : " + result.getStatusCode());

                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // FIND ALL - findStockAdjustment
        public StockAdjustment[] findStockAdjustment(SearchStockAdjustment searchInventory, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "stockadjustment/findStockAdjustment");
                HttpEntity<?> entity = new HttpEntity<>(searchInventory, headers);
                ResponseEntity<StockAdjustment[]> result = getRestTemplate().exchange(builder.toUriString(),
                        HttpMethod.POST, entity, StockAdjustment[].class);
                log.info("result : " + result.getStatusCode());

                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        // PATCH
        public StockAdjustment[] updateStockAdjustment(String companyCode, String plantId, String languageId,
                                                       String warehouseId, Long stockAdjustmentKey, String itemCode,
                                                       String manufacturerName, String loginUserId,
                                                       @Valid List<StockAdjustment> updateStockAdjustment, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updateStockAdjustment, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "stockadjustment/" + stockAdjustmentKey)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("itemCode", itemCode)
                        .queryParam("manufacturerName", manufacturerName)
//                    .queryParam("storageBin", storageBin)
                        .queryParam("loginUserId", loginUserId);

                ResponseEntity<StockAdjustment[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH,
                        entity, StockAdjustment[].class);
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteStockAdjustment(String companyCode, String plantId, String languageId,
                                             String warehouseId, Long stockAdjustmentKey, String itemCode,
                                             String manufacturerName, String storageBin, String loginUserId, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "stockadjustment/" + stockAdjustmentKey)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("itemCode", itemCode)
                        .queryParam("manufacturerName", manufacturerName)
                        .queryParam("storageBin", storageBin)
                        .queryParam("loginUserId", loginUserId);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //==========================================Get All Exception Log Details==========================================
        public ExceptionLog[] getAllExceptionLogs(String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "exceptionlog/all");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ExceptionLog[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ExceptionLog[].class);
                log.info("result: " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //FindOutboundOrder
        public OutboundOrderV2[] findOutboundOrderV2(FindOutboundOrderV2 findOutboundOrderV2, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/orders/findOutboundOrderV2");
                HttpEntity<?> entity = new HttpEntity<>(findOutboundOrderV2, headers);
                ResponseEntity<OutboundOrderV2[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        public OutboundOrderLineV2[] findOutboundOrderLineV2(FindOutboundOrderLineV2 findOutboundOrderLineV2, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/orders/findOutboundOrderLineV2");
                HttpEntity<?> entity = new HttpEntity<>(findOutboundOrderLineV2, headers);
                ResponseEntity<OutboundOrderLineV2[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderLineV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - findSupplierInvoiceHeader
        public SupplierInvoiceHeader[] findSupplierInvoiceHeader(SearchSupplierInvoiceHeader searchSupplierInvoiceHeader, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/invoice/findSupplierInvoiceHeader");
                HttpEntity<?> entity = new HttpEntity<>(searchSupplierInvoiceHeader, headers);
                ResponseEntity<SupplierInvoiceHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, SupplierInvoiceHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        /**
         *
         * @param outboundOrderCancelInput
         * @param authToken
         * @return
         * @throws ParseException
         */
        public PreOutboundHeaderV2[] orderCancellation(List<OutboundOrderCancelInput> outboundOrderCancelInput, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "preoutboundheader/v2/orderCancellation");

                HttpEntity<?> entity = new HttpEntity<>(outboundOrderCancelInput, headers);
                ResponseEntity<PreOutboundHeaderV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PreOutboundHeaderV2[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - Upload - StockAdjustment V2
        public WarehouseApiResponse createStockAdjustmentUploadV2(List<StockAdjustment> stockAdjustmentList, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/stockAdjustment/upload");
            HttpEntity<?> entity = new HttpEntity<>(stockAdjustmentList, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - StockAdjustment V2
        public WarehouseApiResponse createStockAdjustmentV2(StockAdjustment stockAdjustment, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/stockAdjustment");
            HttpEntity<?> entity = new HttpEntity<>(stockAdjustment, headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(),
                    HttpMethod.POST, entity, WarehouseApiResponse.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // POST - pickingProductivityReport
        public PickingProductivityReport[] pickingProductivityReport(SearchPickingProductivityReport searchPickingProductivityReport, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/reports/pickingProductivityReport");
                HttpEntity<?> entity = new HttpEntity<>(searchPickingProductivityReport, headers);
                ResponseEntity<PickingProductivityReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, PickingProductivityReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - binningProductivityReport
        public BinningProductivityReport[] binningProductivityReport(SearchBinningProductivityReport searchBinningProductivityReport, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/reports/binningProductivityReport");
                HttpEntity<?> entity = new HttpEntity<>(searchBinningProductivityReport, headers);
                ResponseEntity<BinningProductivityReport[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, BinningProductivityReport[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        // ThreePL

        //--------------------------------------------ProformaInvoiceHeader------------------------------------------------------------------------
        // GET ALL
        public ProformaInvoiceHeader[] getProformaInvoiceHeader(String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ProformaInvoiceHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProformaInvoiceHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public ProformaInvoiceHeader getProformaInvoiceHeader(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, Long proformaBillNo, String partnerCode, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader/" + proformaBillNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ProformaInvoiceHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProformaInvoiceHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public ProformaInvoiceHeader createProformainvoiceheader(AddProformaInvoiceHeader addProformaInvoiceHeader, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(addProformaInvoiceHeader, headers);
            ResponseEntity<ProformaInvoiceHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProformaInvoiceHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public ProformaInvoiceHeader updateProformaInvoiceHeader(String companyCodeId, String plantId, String languageId,
                                                                 String warehouseId, Long proformaBillNo, String partnerCode, String loginUserID,
                                                                 UpdateProformaInvoiceHeader updateProformaInvoiceHeader, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updateProformaInvoiceHeader, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader/" + proformaBillNo)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<ProformaInvoiceHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ProformaInvoiceHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteProformaInvoiceHeader(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, Long proformaBillNo, String partnerCode,
                                                   String loginUserID, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader/" + proformaBillNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public ProformaInvoiceHeader[] findProformaInvoiceHeader(FindProformaInvoiceHeader findProformaInvoiceHeader, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceheader/find");
                HttpEntity<?> entity = new HttpEntity<>(findProformaInvoiceHeader, headers);
                ResponseEntity<ProformaInvoiceHeader[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProformaInvoiceHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //--------------------------------------------ProformaInvoiceLine------------------------------------------------------------------------
        // GET ALL
        public ProformaInvoiceLine[] getProformaInvoiceLine(String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ProformaInvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProformaInvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public ProformaInvoiceLine[] getProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                            Long proformaBillNo, String partnerCode, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline/" + proformaBillNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<ProformaInvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProformaInvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public ProformaInvoiceLine[] createProformaInvoiceLine(List<AddProformaInvoiceLine> addProformaInvoiceHeader, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(addProformaInvoiceHeader, headers);
            ResponseEntity<ProformaInvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProformaInvoiceLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public ProformaInvoiceLine updateProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             Long proformaBillNo, String partnerCode, Long lineNumber, String loginUserID,
                                                             UpdateProformaInvoiceLine updateProformaInvoiceLine, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updateProformaInvoiceLine, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline/" + proformaBillNo)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("lineNumber", lineNumber)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<ProformaInvoiceLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ProformaInvoiceLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteProformaInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 Long proformaBillNo, String partnerCode, Long lineNumber, String loginUserID, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline/" + proformaBillNo)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("lineNumber", lineNumber)
                                .queryParam("plantId", plantId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public ProformaInvoiceLine[] findProformaInvoiceLine(FindProformaInvoiceLine findProformaInvoiceHeader, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "proformainvoiceline/find");
                HttpEntity<?> entity = new HttpEntity<>(findProformaInvoiceHeader, headers);
                ResponseEntity<ProformaInvoiceLine[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProformaInvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        //--------------------------------------------InvoiceHeader------------------------------------------------------------------------
        // GET ALL
        public InvoiceHeader[] getAllInvoiceHeader(String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InvoiceHeader[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public InvoiceHeader getInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId, Long invoiceNumber,
                                              String partnerCode, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader/" + invoiceNumber)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InvoiceHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public InvoiceHeader createInvoiceHeader(AddInvoiceHeader addInvoiceHeader, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(addInvoiceHeader, headers);
            ResponseEntity<InvoiceHeader> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceHeader.class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public InvoiceHeader updateInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String invoiceNumber, String partnerCode, String loginUserID,
                                                 UpdateInvoiceHeader updateInvoiceHeader, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updateInvoiceHeader, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader/" + invoiceNumber)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<InvoiceHeader> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InvoiceHeader.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteInvoiceHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                           String invoiceNumber, String partnerCode, String loginUserID, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader/" + invoiceNumber)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public InvoiceHeader[] findInvoiceHeader(FindInvoiceHeader findInvoiceHeader, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceheader/find");
                HttpEntity<?> entity = new HttpEntity<>(findInvoiceHeader, headers);
                ResponseEntity<InvoiceHeader[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceHeader[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }


        //--------------------------------------------InvoiceLine------------------------------------------------------------------------
        // GET ALL
        public InvoiceLine[] getAllInvoiceLine(String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline");
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET
        public InvoiceLine[] getInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId, Long invoiceNumber,
                                            String partnerCode, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline/" + invoiceNumber)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("invoiceNumber", invoiceNumber)
                                .queryParam("plantId", plantId);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<InvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public InvoiceLine[] createInvoiceLine(List<AddInvoiceLine> addInvoiceLine, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(addInvoiceLine, headers);
            ResponseEntity<InvoiceLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }

        // PATCH
        public InvoiceLine updateInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                             Long invoiceNumber, String partnerCode, Long lineNumber, String loginUserID,
                                             UpdateInvoiceLine updateInvoiceLine, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(updateInvoiceLine, headers);

                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline/" + lineNumber)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("partnerCode", partnerCode)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("languageId", languageId)
                        .queryParam("plantId", plantId)
                        .queryParam("invoiceNumber", invoiceNumber)
                        .queryParam("loginUserID", loginUserID);

                ResponseEntity<InvoiceLine> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InvoiceLine.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE
        public boolean deleteInvoiceLine(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         Long invoiceNumber, String partnerCode, Long lineNumber, String loginUserID, String authToken) {
            try {
                  AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline/" + lineNumber)
                                .queryParam("warehouseId", warehouseId)
                                .queryParam("companyCodeId", companyCodeId)
                                .queryParam("languageId", languageId)
                                .queryParam("plantId", plantId)
                                .queryParam("partnerCode", partnerCode)
                                .queryParam("invoiceNumber", invoiceNumber)
                                .queryParam("loginUserID", loginUserID);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public InvoiceLine[] findInvoiceLine(FindInvoiceLine findInvoiceLine, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "invoiceline/find");
                HttpEntity<?> entity = new HttpEntity<>(findInvoiceLine, headers);
                ResponseEntity<InvoiceLine[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InvoiceLine[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //==========================================Walkaroor=================================================================
        // process
        public boolean processPickup(String companyCode, String plantId, String languageId, String warehouseId,
                                     String pickupNumber, String loginUserId, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "pickupline/v3/" + pickupNumber)
                        .queryParam("companyCode", companyCode)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("loginUserId", loginUserId);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        //SEARCH
        public StorageBinDashBoardImpl[] getStorageBinDashBoard(StorageBinDashBoardInput storageBinDashBoardInput, String authToken) throws Exception {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "reports/storageBinDashboard");
                HttpEntity<?> entity = new HttpEntity<>(storageBinDashBoardInput, headers);
                ResponseEntity<StorageBinDashBoardImpl[]> result =
                        getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinDashBoardImpl[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - putAwayReport
        public CharData[] putAwayReport(FindReport findReport, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/reports/putaway/report/v2");
                HttpEntity<?> entity = new HttpEntity<>(findReport, headers);
                ResponseEntity<CharData[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, CharData[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - pickingReport
        public CharData[] pickingReport(FindReport findReport, String authToken) throws ParseException {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "/reports/picking/report/v2");
                HttpEntity<?> entity = new HttpEntity<>(findReport, headers);
                ResponseEntity<CharData[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST,
                        entity, CharData[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }



        /*--------------------------PalletIdAssignment----------------------------------------------------*/

        // GET
        public PalletIdAssignment getPalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, Long pId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/" + pId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("authToken", authToken);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PalletIdAssignment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PalletIdAssignment.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // GET V3
        public PalletIdAssignment getPalletIdAssignmentV3(String companyCodeId, String plantId, String languageId, String warehouseId, String palletId, String authToken) {
            try {
                   AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/v3/" + palletId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("authToken", authToken);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<PalletIdAssignment> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
                        entity, PalletIdAssignment.class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST
        public PalletIdAssignment[] createPalletIdAssignment(String companyCodeId, String plantId, String languageId,
                                                             String warehouseId, List<PalletIdAssignment> newPalletIdAssignment,
                                                             String loginUserID, String authToken) throws Exception {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/create")
                    .queryParam("companyCodeId", companyCodeId)
                    .queryParam("plantId", plantId)
                    .queryParam("languageId", languageId)
                    .queryParam("warehouseId", warehouseId)
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(newPalletIdAssignment, headers);
            ResponseEntity<PalletIdAssignment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
                    PalletIdAssignment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        }


        // patch PalletIdAssignment
        public PalletIdAssignment[] updatePalletIdAssignment(String companyCodeId, String plantId, String languageId,
                                                             String warehouseId, List<PalletIdAssignment> updatePalletIdAssignment,
                                                             String loginUserID, String authToken) throws Exception {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePalletIdAssignment, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/update" )
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PalletIdAssignment[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PalletIdAssignment[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // patch PalletIdStatus
        public PalletIdAssignment[] updatePalletIdStatus(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, List<PalletIdAssignment> updatePalletIdStatus,
                                                         String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(updatePalletIdStatus, headers);
                HttpClient client = HttpClients.createDefault();
                RestTemplate restTemplate = getRestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/update/PalletIdStatus" )
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("loginUserID", loginUserID);
                ResponseEntity<PalletIdAssignment[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity,
                        PalletIdAssignment[].class);
                log.info("result : " + result.getStatusCode());
                return result.getBody();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // DELETE PalletIdAssignment
        public boolean deletePalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, Long pId, String loginUserID, String authToken) {
            try {
                    AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/delete/" + pId)
                        .queryParam("companyCodeId", companyCodeId)
                        .queryParam("plantId", plantId)
                        .queryParam("languageId", languageId)
                        .queryParam("warehouseId", warehouseId)
                        .queryParam("loginUserID", loginUserID)
                        .queryParam("authToken", authToken);
                ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                        String.class);
                log.info("result : " + result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // POST - Finder PalletIdAssignment -Stream
        public PalletIdAssignment[] findPalletIdAssignment(FindPalletIdAssignment findPalletIdAssignment, String authToken) throws Exception {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "palletIdAssignment/findPalletIdAssignment");
            HttpEntity<?> entity = new HttpEntity<>(findPalletIdAssignment, headers);
            ResponseEntity<PalletIdAssignment[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PalletIdAssignment[].class);
            return result.getBody();
        }


        public WarehouseApiResponse postData(String input, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "warehouse/send")
                    .queryParam("input", input);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            return result.getBody();
        }

        public WarehouseApiResponse createInventoryv3(List<ExcessConfirmation> excessConfirmations, String loginUserID, String authToken) {
               AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("User-Agent", "ClassicWMS RestTemplate");
                headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(getOutboundTransactionServiceApiUrl() + "inventory/update/excessConfirmation")
                    .queryParam("loginUserID", loginUserID);
            HttpEntity<?> entity = new HttpEntity<>(excessConfirmations,headers);
            ResponseEntity<WarehouseApiResponse> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseApiResponse.class);
            return result.getBody();
        }

    /**
     *
     * @param outboundOrderReversalList
     * @param authToken
     * @return
     */
    public OutboundOrderReversal[] outboundOrderReversals(List<OutboundOrderReversal> outboundOrderReversalList , String authToken) {
        try {
            AuthToken oAuth = authTokenService.getOutboundTransactionServiceAuthToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "ClassicWMS-Almailem RestTemplate");
            headers.add("Authorization", "Bearer " + oAuth.getAccess_token());

            HttpEntity<?> entity = new HttpEntity<>(outboundOrderReversalList, headers);
            HttpClient client = HttpClients.createDefault();
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getOutboundTransactionServiceApiUrl() + "outboundreversal/order/reversal");
            ResponseEntity<OutboundOrderReversal[]> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    entity, OutboundOrderReversal[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}



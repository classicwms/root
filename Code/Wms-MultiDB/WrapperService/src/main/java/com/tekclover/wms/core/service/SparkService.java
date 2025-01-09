package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.spark.*;
import com.tekclover.wms.core.model.transaction.SearchInboundHeaderV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
@Slf4j
public class SparkService {

    @Autowired
    PropertiesConfig propertiesConfig;


    private String getSparkServiceUrl() {
        return propertiesConfig.getSparkServiceUrl();
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }




    /*==============================================MASTER==========================================================*/

    /**
     * @param searchImBasicData1
     * @return
     */
    public ImBasicData1[] findImBasicData1(SearchImBasicData1 searchImBasicData1) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "imbasicdata1");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchStorageBin
     * @return
     */
    public StorageBin[] findStorageBin(SearchStorageBin searchStorageBin) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "storagebin");
            HttpEntity<?> entity = new HttpEntity<>(searchStorageBin, headers);
            ResponseEntity<StorageBin[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBin[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*==========================================INBOUND======================================================*/

    /**
     * @param findContainerReceiptV2
     * @return
     */
    public ContainerReceiptV2[] findContainerReceiptV2(FindContainerReceiptV2 findContainerReceiptV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "containerReceipt");
            HttpEntity<?> entity = new HttpEntity<>(findContainerReceiptV2, headers);
            ResponseEntity<ContainerReceiptV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ContainerReceiptV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param findPreInboundHeaderV2
     * @return
     */
    public PreInboundHeaderV2[] findPreinboundHeaderV2(FindPreInboundHeaderV2 findPreInboundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "preinboundHeader");
            HttpEntity<?> entity = new HttpEntity<>(findPreInboundHeaderV2, headers);
            ResponseEntity<PreInboundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreInboundHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchInboundHeaderV2
     * @return
     */
    public StagingHeaderV2[] findStagingHeaderV2(FindStagingHeaderV2 searchInboundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "stagingheader");
            HttpEntity<?> entity = new HttpEntity<>(searchInboundHeaderV2, headers);
            ResponseEntity<StagingHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchGrHeaderV2
     * @return
     */
    public GrHeaderV2[] findGrHeaderV2(SearchGrHeaderV2 searchGrHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "grheader");
            HttpEntity<?> entity = new HttpEntity<>(searchGrHeaderV2, headers);
            ResponseEntity<GrHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchPutAwayHeaderV2
     * @return
     */
    public PutAwayHeaderV2[] findPutawayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "putawayheader");
            HttpEntity<?> entity = new HttpEntity<>(searchPutAwayHeaderV2, headers);
            ResponseEntity<PutAwayHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchInboundHeaderV2
     * @return
     */
    public InboundHeaderV2[] findInboundHeaderV2(SearchInboundHeaderV2 searchInboundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inboundHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchInboundHeaderV2, headers);
            ResponseEntity<InboundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*====================================OUTBOUND REVERSAL============================================================*/

    // PreOutboundHeaderV2
    public PreOutboundHeaderV2[] findPreOutboundHeaderV2(FindPreOutboundHeaderV2 findPreOutboundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "preoutboundheader");
            HttpEntity<?> entity = new HttpEntity<>(findPreOutboundHeaderV2, headers);
            ResponseEntity<PreOutboundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreOutboundHeaderV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // OrderManagementLineV2
    public OrderManagementLineV2[] findOrderManagementLineV2(FindOrderManagementLineV2 findOrderManagementLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "ordermanagementline");
            HttpEntity<?> entity = new HttpEntity<>(findOrderManagementLineV2, headers);
            ResponseEntity<OrderManagementLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OrderManagementLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // PickupHeaderV2
    public PickupHeaderV2[] findPickupHeaderV2(FindPickupHeaderV2 findPickupHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "pickupheader");
            HttpEntity<?> entity = new HttpEntity<>(findPickupHeaderV2, headers);
            ResponseEntity<PickupHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickupHeaderV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // QualityHeaderV2
    public QualityHeaderV2[] findQualityHeaderV2(FindQualityHeaderV2 findQualityHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "qualityheader");
            HttpEntity<?> entity = new HttpEntity<>(findQualityHeaderV2, headers);
            ResponseEntity<QualityHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityHeaderV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // OutBoundHeaderV2
    public OutBoundHeaderV2[] findOutboundHeaderV2(FindOutBoundHeaderV2 findOutBoundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "outboundheader");
            HttpEntity<?> entity = new HttpEntity<>(findOutBoundHeaderV2, headers);
            ResponseEntity<OutBoundHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutBoundHeaderV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param findOutBoundReversalV2
     * @return
     */
    public OutBoundReversalV2[] findOutBoundReversal(FindOutBoundReversalV2 findOutBoundReversalV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "outboundreversal");
            HttpEntity<?> entity = new HttpEntity<>(findOutBoundReversalV2, headers);
            ResponseEntity<OutBoundReversalV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutBoundReversalV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchPickupLineV2
     * @return
     */
    public PickupLine[] findPickUpLine(SearchPickupLineV3 searchPickupLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "pickupline");
            HttpEntity<?> entity = new HttpEntity<>(searchPickupLineV2, headers);
            ResponseEntity<PickupLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickupLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param searchInventoryMovementV2
     * @return
     */
    public InventoryMovementV2[] findInventoryMovementV2(SearchInventoryMovementV2 searchInventoryMovementV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inventorymovement");
            HttpEntity<?> entity = new HttpEntity<>(searchInventoryMovementV2, headers);
            ResponseEntity<InventoryMovementV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryMovementV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // Find InventoryV2
    public InventoryV2[] findInventoryV2(FindInventoryV2 findInventoryV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inventory");
            HttpEntity<?> entity = new HttpEntity<>(findInventoryV2, headers);
            ResponseEntity<InventoryV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find InventoryV2CoreNew
    public InventoryV3[] findInventoryV2CoreNew(FindInventoryV2 findInventoryV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inventory/new");
            HttpEntity<?> entity = new HttpEntity<>(findInventoryV2, headers);
            ResponseEntity<InventoryV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PutAwayLineV2
    public PutAwayLineV2[] findPutAwayLineV2(FindPutAwayLineV2 findInventoryV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "putawayline");
            HttpEntity<?> entity = new HttpEntity<>(findInventoryV2, headers);
            ResponseEntity<PutAwayLineV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PutAwayLineV2
    public PutAwayLineCore[] findPutAwayLineCore(FindPutAwayLineV2 findInventoryV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "putawayline/new");
            HttpEntity<?> entity = new HttpEntity<>(findInventoryV2, headers);
            ResponseEntity<PutAwayLineCore[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLineCore[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PeriodicHeaderV2
    public PeriodicHeaderV2[] findPeriodicHeader(FindPeriodicHeaderV2 findPeriodicHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "periodicheader");
            HttpEntity<?> entity = new HttpEntity<>(findPeriodicHeaderV2, headers);
            ResponseEntity<PeriodicHeaderV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeaderV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find InhouseTransferLine
    public InhouseTransferLine[] findInhouseTransferLine(SearchInhouseTransferLine searchInhouseTransferLine) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inhousetransferline");
            HttpEntity<?> entity = new HttpEntity<>(searchInhouseTransferLine, headers);
            ResponseEntity<InhouseTransferLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PerpetualHeader
    public PerpetualHeader[] findPerpetualHeader(SearchPerpetualHeaderV2 searchPerpetualHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "perpetualheader");
            HttpEntity<?> entity = new HttpEntity<>(searchPerpetualHeaderV2, headers);
            ResponseEntity<PerpetualHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find InboundLine
    public InboundLineV2[] findInboundLine(FindInboundLineV2 findInboundLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inboundline");
            HttpEntity<?> entity = new HttpEntity<>(findInboundLineV2, headers);
            ResponseEntity<InboundLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Get All InhouseTransferHeaders
    public InhouseTransferHeader[] getAllInhouseTransferHeaders() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inhousetransferheader");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<InhouseTransferHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InhouseTransferHeader[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Get StockReport
    public StockReport[] getStockReport(FindStockReport findStockReport) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "stockreport");
            HttpEntity<?> entity = new HttpEntity<>(findStockReport, headers);
            ResponseEntity<StockReport[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockReport[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find OrderStatusReport
    public OrderStatusReport[] findOrderStatusReport(SearchOrderStatusReport searchOrderStatusReport) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inboundline");
            HttpEntity<?> entity = new HttpEntity<>(searchOrderStatusReport, headers);
            ResponseEntity<OrderStatusReport[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OrderStatusReport[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    // Find PeriodicLine
    public PeriodicLineV2[] findPeriodicLine(FindPeriodicLineV2 findPeriodicLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "periodicline");
            HttpEntity<?> entity = new HttpEntity<>(findPeriodicLineV2, headers);
            ResponseEntity<PeriodicLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PerpetualLine
    public PerpetualLineV2[] findPerpetualLine(SearchPerpetualLineV2 searchPerpetualLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "perpetualline");
            HttpEntity<?> entity = new HttpEntity<>(searchPerpetualLineV2, headers);
            ResponseEntity<PerpetualLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // Find PeriodicLine
    public PreOutBoundLineV2[] findPreOutBoundLine(FindPreOutBoundLineV2 searchPreOutBoundLine) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "preoutboundline");
            HttpEntity<?> entity = new HttpEntity<>(searchPreOutBoundLine, headers);
            ResponseEntity<PreOutBoundLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PreOutBoundLineV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Find StagingLine
    public StagingLineV2[] findStagingLineV2(FindStagingLineV2 findStagingLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "stagingLine");
            HttpEntity<?> entity = new HttpEntity<>(findStagingLineV2, headers);
            ResponseEntity<StagingLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StagingLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //Find GrLine
    public GrLineV2[] findGrLineV2(FindGrLineV2 findGrLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "grline");
            HttpEntity<?> entity = new HttpEntity<>(findGrLineV2, headers);
            ResponseEntity<GrLineV2[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLineV2[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find QualityLine
    public QualityLine[] findQualityLine(FindQualityLine FindQualityLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "qualityline");
            HttpEntity<?> entity = new HttpEntity<>(FindQualityLineV2, headers);
            ResponseEntity<QualityLine[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, QualityLine[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find ImBasicData1
    public ImBasicData1V3[] findImbasicData1V3(SearchImBasicData1 searchImBasicData1) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "/imbasicdata1v3");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1V3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1V3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find ImBasicData1
    public ImBasicData1V3[] findImbasicData1V5(SearchImBasicData1 searchImBasicData1) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "/imbasicdata1v3");
            HttpEntity<?> entity = new HttpEntity<>(searchImBasicData1, headers);
            ResponseEntity<ImBasicData1V3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ImBasicData1V3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find BusinessPartner
    public BusinessPartnerV2[] findBusinessPartner(FindBusinessPartner findBusinessPartner) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "/businessPartner");
            HttpEntity<?> entity = new HttpEntity<>(findBusinessPartner, headers);
            ResponseEntity<BusinessPartnerV2[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BusinessPartnerV2[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find InboundLine Join InboundHeader
    public InboundLineV3[] findInboundLineJoin(FindInboundLineV2 findInboundLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "/inboundLineV3");
            HttpEntity<?> entity = new HttpEntity<>(findInboundLineV2, headers);
            ResponseEntity<InboundLineV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundLineV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Find OutboundHeader
     * @param findOutBoundHeader
     * @return
     */
    public Outbound[] findOutbounds(FindOutBoundHeader findOutBoundHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "/outboundHeaderTest");
            HttpEntity<?> entity = new HttpEntity<>(findOutBoundHeader, headers);
            ResponseEntity<Outbound[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Outbound[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FindInventory
     *
     * @param findInventoryV3
     * @return
     */
    public InventoryV3[] findInventoryV3(FindInventoryV3 findInventoryV3) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inventory/new");
            HttpEntity<?> entity = new HttpEntity<>(findInventoryV3, headers);
            ResponseEntity<InventoryV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InventoryV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
}
    }

    /**
     * FindPutAwayLine
     *
     * @param findPutAwayLineV2
     * @return
     */
    public PutAwayLineSpark[] findPutAwayLineSpark(FindPutAwayLineV2 findPutAwayLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
//        headers.add("Authorization", "Bearer " + authToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "putawayline/new");
            HttpEntity<?> entity = new HttpEntity<>(findPutAwayLineV2, headers);
            ResponseEntity<PutAwayLineSpark[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PutAwayLineSpark[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FindInboundLine
     *
     * @param findInboundLineV2
     * @return
     */
    public InboundLineV4[] findInboundLinev4(FindInboundLineV2 findInboundLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "inboundLine/new");
            HttpEntity<?> entity = new HttpEntity<>(findInboundLineV2, headers);
            ResponseEntity<InboundLineV4[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundLineV4[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * PickUpLine
     *
     * @param searchPickupLineV3
     * @return
     */
    public PickUpLineV3[] findPickUpLineV3(SearchPickupLineV3 searchPickupLineV3) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "pickuplinev3/new");
            HttpEntity<?> entity = new HttpEntity<>(searchPickupLineV3, headers);
            ResponseEntity<PickUpLineV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickUpLineV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FindOutboundLineV2
     *
     * @param findOutboundLineV2
     * @return
     */
    public OutboundLineV3[] findOutboundLineV3(FindOutboundLineV2 findOutboundLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "outboundv3/new");
            HttpEntity<?> entity = new HttpEntity<>(findOutboundLineV2, headers);
            ResponseEntity<OutboundLineV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundLineV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FindOutboundLineV2
     *
     * @param findGrLineV2
     * @return
     */
    public GrLineV3[] findGrLineV3(FindGrLineV2 findGrLineV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "grline/v3");
            HttpEntity<?> entity = new HttpEntity<>(findGrLineV2, headers);
            ResponseEntity<GrLineV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, GrLineV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Outbound Order Summary Report
    public OutboundHeaderV3[] findOutboundOrderSummaryReport(FindOutBoundHeaderV2 findOutBoundHeaderV2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", " RestTemplate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getSparkServiceUrl() + "obHeader/obOrderSummaryReport");
            HttpEntity<?> entity = new HttpEntity<>(findOutBoundHeaderV2, headers);
            ResponseEntity<OutboundHeaderV3[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundHeaderV3[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
package com.tekclover.wms.core.service;


import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.model.Connector.*;
import com.tekclover.wms.core.model.transaction.IntegrationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
@Slf4j
public class ConnectorService {


    @Autowired
    PropertiesConfig propertiesConfig;


    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getConnectorServiceApiUrl() {
        return propertiesConfig.getConnectorServiceUrl();
    }

    /*=================================================Integration Log=========================================================*/

    //Integration Log
    public IntegrationLog[] getAllIntegrationLog(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "integrationlog");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<IntegrationLog[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, IntegrationLog[].class);
            log.info("result: " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //================================================Find_APIs'=======================================================
    //Find SupplierInvoiceHeader
    public SupplierInvoiceHeader[] findSupplierInvoiceHeader(SearchSupplierInvoiceHeader searchSupplierInvoiceHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/supplierinvoice/findSupplierInvoiceHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchSupplierInvoiceHeader, headers);
            ResponseEntity<SupplierInvoiceHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SupplierInvoiceHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public StockReceiptHeader[] findStockReceiptHeader(SearchStockReceiptHeader searchStockReceiptHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/stockreceipt/findStockReceiptHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchStockReceiptHeader, headers);
            ResponseEntity<StockReceiptHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockReceiptHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public TransferInHeader[] findB2BTransferInHeader(SearchTransferInHeader searchTransferInHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/b2btransferin/findB2BTransferInHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchTransferInHeader, headers);
            ResponseEntity<TransferInHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferInHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public TransferInHeader[] findInterWareHouseTransferInHeader(SearchTransferInHeader searchTransferInHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/interwarehousetransferinv2/findInterWareHouseTransferInHeader");
            HttpEntity<?> entity = new HttpEntity<>(searchTransferInHeader, headers);
            ResponseEntity<TransferInHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferInHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find SalesReturnHeader
    public SalesReturnHeader[] findSalesReturnHeader(FindSalesReturnHeader findSalesReturnHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/salesReturn/findsalesreturnheader");
            HttpEntity<?> entity = new HttpEntity<>(findSalesReturnHeader, headers);
            ResponseEntity<SalesReturnHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SalesReturnHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    // Find StockAdjustment
    public StockAdjustment[] findStockAdjustment(FindStockAdjustment findStockAdjustment, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/stockAdjustment/findStockAdjustment");
            HttpEntity<?> entity = new HttpEntity<>(findStockAdjustment, headers);
            ResponseEntity<StockAdjustment[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockAdjustment[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PickListHeader
    public PickListHeader[] findPickListHeader(FindPickListHeader findPickListHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/salesorderv2/findPickListHeader");
            HttpEntity<?> entity = new HttpEntity<>(findPickListHeader, headers);
            ResponseEntity<PickListHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickListHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PurchaseReturnHeader
    public PurchaseReturnHeader[] findPurchaseReturnHeader(FindPurchaseReturnHeader findPurchaseReturnHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/returnpov2/findPurchaseReturnHeader");
            HttpEntity<?> entity = new HttpEntity<>(findPurchaseReturnHeader, headers);
            ResponseEntity<PurchaseReturnHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PurchaseReturnHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find SalesInvoice
    public SalesInvoice[] findSalesInvoice(FindSalesInvoice findSalesInvoice, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/salesinvoice/findSalesInvoice");
            HttpEntity<?> entity = new HttpEntity<>(findSalesInvoice, headers);
            ResponseEntity<SalesInvoice[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SalesInvoice[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find InterWarehouseTransferOut
    public TransferOutHeader[] findInterWarehouseTransferOut(FindTransferOutHeader findTransferOutHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/interwarehousetransferoutv2/findInterWarehouseTransferOut");
            HttpEntity<?> entity = new HttpEntity<>(findTransferOutHeader, headers);
            ResponseEntity<TransferOutHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferOutHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }



    // Find ShipmentOrder
    public TransferOutHeader[] findShipmentOrder(FindTransferOutHeader findTransferOutHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/shipmentorderv2/findShipmentOrder");
            HttpEntity<?> entity = new HttpEntity<>(findTransferOutHeader, headers);
            ResponseEntity<TransferOutHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferOutHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find ItemMaster
    public ItemMaster[] findItemMaster(FindItemMaster findItemMaster, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/itemmaster/findItemMaster");
            HttpEntity<?> entity = new HttpEntity<>(findItemMaster, headers);
            ResponseEntity<ItemMaster[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemMaster[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find SalesReturnHeader
    public CustomerMaster[] findCustomerMaster(FindCustomerMaster findCustomerMaster, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/customermaster/findCustomerMaster");
            HttpEntity<?> entity = new HttpEntity<>(findCustomerMaster, headers);
            ResponseEntity<CustomerMaster[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CustomerMaster[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PerpetualHeader
    public PerpetualHeader[] findPerpetualHeader(FindPerpetualHeader findPerpetualHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/perpetual/findPerpetualHeader");
            HttpEntity<?> entity = new HttpEntity<>(findPerpetualHeader, headers);
            ResponseEntity<PerpetualHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Find PeriodicHeader
    public PeriodicHeader[] findPeriodicHeader(FindPeriodicHeader findPeriodicHeader, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/periodic/findPeriodicHeader");
            HttpEntity<?> entity = new HttpEntity<>(findPeriodicHeader, headers);
            ResponseEntity<PeriodicHeader[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicHeader[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //==================================LINE===========================================================================================================================================


    // Find InterWarehouseTransferOutLine
    public TransferOutLine[] findInterWarehouseTransferOutLine(FindTransferOutLine findTransferOutLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/interwarehousetransferoutv2/findInterWarehouseTransferOutLine");
            HttpEntity<?> entity = new HttpEntity<>(findTransferOutLine, headers);
            ResponseEntity<TransferOutLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferOutLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


//Find ShipmentOrderLine
    public TransferOutLine[] findShipmentOrderLine(FindTransferOutLine findTransferOutLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/shipmentorderv2/findShipmentOrderLine");
            HttpEntity<?> entity = new HttpEntity<>(findTransferOutLine, headers);
            ResponseEntity<TransferOutLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferOutLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public TransferInLine[] findInterWareHouseTransferInLine(SearchTransferInLine searchTransferInLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/interwarehousetransferinv2/findInterWareHouseTransferInLine");
            HttpEntity<?> entity = new HttpEntity<>(searchTransferInLine, headers);
            ResponseEntity<TransferInLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferInLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public TransferInLine[] findB2BTransferInLine(SearchTransferInLine searchTransferInLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/b2btransferin/findB2BTransferInLine");
            HttpEntity<?> entity = new HttpEntity<>(searchTransferInLine, headers);
            ResponseEntity<TransferInLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferInLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public SupplierInvoiceLine[] findSupplierInvoiceLine(SearchSupplierInvoiceLine searchSupplierInvoiceLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/supplierinvoice/findSupplierInvoiceLine");
            HttpEntity<?> entity = new HttpEntity<>(searchSupplierInvoiceLine, headers);
            ResponseEntity<SupplierInvoiceLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SupplierInvoiceLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public StockReceiptLine[] findStockReceiptLine(SearchStockReceiptLine searchStockReceiptLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/stockreceipt/findStockReceiptLine");
            HttpEntity<?> entity = new HttpEntity<>(searchStockReceiptLine, headers);
            ResponseEntity<StockReceiptLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockReceiptLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public SalesReturnLine[] findSalesReturnLine(FindSalesReturnLine findSalesReturnLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/salesReturn/findsalesreturnline");
            HttpEntity<?> entity = new HttpEntity<>(findSalesReturnLine, headers);
            ResponseEntity<SalesReturnLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SalesReturnLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PurchaseReturnLine[] findPurchaseReturnLine(FindPurchaseReturnLine findPurchaseReturnLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/returnpov2/findPurchaseReturnLine");
            HttpEntity<?> entity = new HttpEntity<>(findPurchaseReturnLine, headers);
            ResponseEntity<PurchaseReturnLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PurchaseReturnLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PickListLine[] findPickListLine(FindPickListLine findPickListLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/salesorderv2/findPickListLine");
            HttpEntity<?> entity = new HttpEntity<>(findPickListLine, headers);
            ResponseEntity<PickListLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PickListLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PeriodicLine[] findPeriodicLine(FindPeriodicLine findPeriodicLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/periodic/findPeriodicLine");
            HttpEntity<?> entity = new HttpEntity<>(findPeriodicLine, headers);
            ResponseEntity<PeriodicLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PeriodicLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PerpetualLine[] findPerpetualLine(FindPerpetualLine findPerpetualLine, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("User-Agent", "RestTemplate");
            headers.add("Authorization", "Bearer " + authToken);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getConnectorServiceApiUrl() + "/perpetual/findPerpetualLine");
            HttpEntity<?> entity = new HttpEntity<>(findPerpetualLine, headers);
            ResponseEntity<PerpetualLine[]> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PerpetualLine[].class);
            log.info("result : " + result.getStatusCode());
            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }





}

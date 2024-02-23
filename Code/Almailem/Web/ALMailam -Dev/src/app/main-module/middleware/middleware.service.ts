import { HttpClient } from '@angular/common/http'; 
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MiddlewareService {

  constructor(private http: HttpClient) { }


  salesInvoiceSearch(obj: any) {
    return this.http.post<any>('/wms-connector-service/findSalesInvoice', obj); 
  } 
  salesInvoiceheader(obj: any) {
    return this.http.post<any>('/wms-connector-service/findSalesReturnHeader', obj); 
  } 
    shippingheader(obj: any) {
    return this.http.post<any>('/wms-connector-service/findShipmentOrder', obj); 
  } 
  stockAdjustment(obj: any) {
    return this.http.post<any>('/wms-connector-service/findStockAdjustment', obj); 
  } 
  supplierinvoice(obj: any) {
    return this.http.post<any>('/wms-connector-service/findSupplierInvoiceHeader', obj); 
  }  
   b2btransfer(obj: any) {
    return this.http.post<any>('/wms-connector-service/findB2BTransferInHeader', obj); 
  } 
  b2btransferline(obj: any) {
    return this.http.post<any>('/wms-connector-service/findB2BTransferInLine', obj); 
  } 
  periodicHeader(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPeriodicHeader', obj); 
  } 
  perpertualHeader(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPerpetualHeader', obj); 
  } 
  periodiclines(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPeriodicLine', obj); 
  }
  perpertuallines(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPerpetualLine', obj); 
  }
  customerMaster(obj: any) {
    return this.http.post<any>('/wms-connector-service/findCustomerMaster', obj); 
  } 
  ItemMaster(obj: any) {
    return this.http.post<any>('/wms-connector-service/findItemMaster', obj); 
  } 
  interwarehousein(obj: any) {
    return this.http.post<any>('/wms-connector-service/findInterWareHouseTransferInHeader', obj); 
  } 
  interwarehouseout(obj: any) {
    return this.http.post<any>('/wms-connector-service/findInterWarehouseTransferOut', obj); 
  }
  interwarehouseinlines(obj: any) {
    return this.http.post<any>('/wms-connector-service/findInterWareHouseTransferInLine', obj); 
  } 
  interwarehouseoutlines(obj: any) {
    return this.http.post<any>('/wms-connector-service/findInterWarehouseTransferOutLine', obj); 
  } 
  picklist(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPickListHeader', obj); 
  } 
  purchasereturn(obj: any) {
    return this.http.post<any>('/wms-connector-service/findPurchaseReturnHeader', obj); 
  } 
  stockreceipt(obj: any) {
    return this.http.post<any>('/wms-connector-service/findStockReceiptHeader', obj); 
  } 
}

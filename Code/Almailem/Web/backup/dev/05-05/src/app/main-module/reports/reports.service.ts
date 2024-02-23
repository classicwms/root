import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  constructor(private apiService: ApiService, private configService: ConfigService, private http: HttpClient) { }

  getStockReportDetails(itemCode: string[], itemText: string, stockTypeText: string, warehouseId: string[], pageNumber, pageSize) {
    return this.apiService.get(this.configService.stockReport_url + '?itemCode=' + itemCode + '&itemText=' + itemText + '&stockTypeText=' + stockTypeText + '&warehouseId=' + warehouseId + '&pageNo=' + pageNumber + '&pageSize=' + pageSize);
  }

  getInventoryReportDetails(itemCode: string[], stSectionIds: string[], stockTypeText: string, storageBin: string, warehouseId: string[], pageNumber, pageSize) {
    return this.apiService.get(this.configService.inventoryReport_url + '?itemCode=' + itemCode + '&stSectionIds=' + stSectionIds + '&stockTypeText=' + stockTypeText + '&storageBin=' + storageBin + '&warehouseId=' + warehouseId + '&pageNo=' + pageNumber + '&pageSize=' + pageSize);
  }

  getStockMovementDetails(fromCreatedOn: string, itemCode: string, toCreatedOn: string, warehouseId: string) {
    return this.apiService.get(this.configService.stockMovement_url + '?fromCreatedOn=' + fromCreatedOn + '&itemCode=' + itemCode + '&toCreatedOn=' + toCreatedOn + '&warehouseId=' + warehouseId);
  }

  getStockMovementDetailsNew(obj: any) {
    return this.http.post<any>(this.configService.newStockMovement_url, obj);
    // return this.http.post<any>('/wms-transaction-service/inventory/findInventory', obj);
  }

  getOrderStatusDetails(customerCode: string[], fromDeliveryDate: string, orderType: string[], statusId: number[], toDeliveryDate: string, warehouseId: string) {
    return this.apiService.get(this.configService.orderStatus_url + '?customerCode=' + customerCode + '&fromDeliveryDate=' + fromDeliveryDate + '&orderType=' + orderType + '&statusId=' + statusId + '&toDeliveryDate=' + toDeliveryDate + '&warehouseId=' + warehouseId);
  }

  getOrderStatusDetailsNew(obj: any) {
    return this.apiService.post(this.configService.orderStatus_url, obj);
  }

  //getShipmentDetails(warehouseId: string, fromDeliveryDate: string, orderNumber: string, soType: string[], storeCode: string, toDeliveryDate: string, reportFormat: string) {
  getShipmentDetails(warehouseId: string, orderNumber: string) {

    //return this.apiService.get(this.configService.orderStatus_url + '?fromDeliveryDate=' + fromDeliveryDate + '&orderNumber=' + orderNumber + '&reportFormat=' + reportFormat + '&soType=' + soType + '&storeCode=' + storeCode + '&toDeliveryDate=' + toDeliveryDate + '&warehouseId=' + warehouseId);
    //return this.apiService.get(this.configService.shipmentDelivery_url + '?orderNumber=' + orderNumber + '&warehouseId=' + warehouseId);
    return this.http.get(
      this.configService.shipmentDelivery_url + '?orderNumber=' + orderNumber + '&warehouseId=' + warehouseId, { responseType: 'text' }
    );
  }

  getItemCodeDetails() {
    return this.apiService.get(this.configService.itemCode_url);
  }

  getItemCodeDropDown(searchKey) {
    return this.apiService.get(this.configService.itemCode_like_url + `?likeSearchByItemCodeNDesc=${searchKey}`);
  }
  getStorageDropDown(searchKey) {
    return this.apiService.get(this.configService.storageBin_like_url + `?likeSearchByStorageBinNDesc=${searchKey}`);
  }

  getShipmentDispatchSummary(data: any) {
    return this.apiService.get(`/wms-transaction-service/shipmentDispatchSummary?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}`);
  }

  getShipmentDeliverySummary(data: any) {
    return this.apiService.get(`/wms-transaction-service/reports/shipmentDeliverySummary?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}`);
  }
  getShipmentDeliverySummarynew(data: any) {
    return this.apiService.get(`/wms-transaction-service/reports/shipmentDeliverySummary1?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}`);
  }

  getInventoryFile(): Promise<File> {
    return this.http
      .get<any>('/report/inventory/download', {
        responseType: 'blob' as 'json',
      })
      .toPromise();
  }

  GetallInventory(): Promise<File> {
    return this.http
      .get<any>('/report/inventory/online', {
        responseType: 'blob' as 'json',
      })
      .toPromise();
  }

  findInventory(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventory/findInventory', obj);
  }

  GetInventory() {
    return this.http.get<any>('/batch-fetch/jobInventoryQuery');
  }



  GetallDataStockReport(itemCode: string[], itemText: string, stockTypeText: string, warehouseId: string[]) {
    return this.apiService.get(this.configService.newstockReportAllData_url + '?itemCode=' + itemCode + '&itemText=' + itemText + '&stockTypeText=' + stockTypeText + '&warehouseId=' + warehouseId);
  }


  getInventoryMovement(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventorymovement/findInventoryMovement', obj);
  }

  deleteInventoryMovement(obj: any,) {
    return this.http.delete<any>('/wms-transaction-service/inventorymovement/' + obj.movementType +'?batchSerialNumber=' + 1 +  '&itemCode=' + obj.itemCode + '&movementDocumentNo=' + obj.movementDocumentNo + '&packBarcodes=' + obj.packBarcodes +  '&submovementType=' + obj.submovementType + '&warehouseId=' + obj.warehouseId);
  }

  getTransferReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inhousetransferline/findInhouseTransferLine', obj);
  }

  getNewInventoryMovement() {
    return this.http.get<any>('/batch-fetch/jobInventoryMovementQuery');
  }
  getNewInventoryMovement1() {
    return this.http.get<any>('/wms-transaction-service/streaming/inventoryMovement');
  }

  getBinnerReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/putawayline/findPutAwayLine', obj);
  }
  getInboundStatusReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inboundheader/findInboundHeader', obj);
  }


  
  sendMail() {
    return this.http.get<any>('/wms-idmaster-service/email/sendmail');
  }


  public uploadfile(file: File, location) {
    let formParams = new FormData();
    formParams.append('file', file)
    return this.http.post('/wms-idmaster-service/doc-storage/upload?' + `location=${location}`, formParams)
  }

}

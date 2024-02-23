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
  }
  getStockMovementDetailsNew2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/outboundline/v2/findOutboundLine', obj);
  }
  getStockMovementDetailsNewV2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/outboundline/stock-movement-report/v2/findOutboundLine', obj);
  }

  getstocktype(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/stocktypeid/findStockTypeId', obj);
    // return this.http.post<any>('/wms-transaction-service/inventory/findInventory', obj);
  }

  getordermanagement(obj: any) {
    return this.http.post<any>('/mnr-spark-service/ordermanagementline', obj);
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
  getStorageDropDown2(searchKey,companyCodeId:any,plantId:any,warehouseId:any,languageId:any) {
    return this.apiService.get(this.configService.storageBin_like_url2 + `?likeSearchByStorageBinNDesc=${searchKey}`+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&languageId='+languageId);
  }
  getItemCodeDropDown2(searchKey,companyCodeId:any,plantId:any,warehouseId:any,languageId:any) {
    return this.apiService.get(this.configService.itemCode_like_url2 + `?likeSearchByItemCodeNDesc=${searchKey}`+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&languageId='+languageId);
  }

  getShipmentDispatchSummary(data: any) {
    return this.apiService.get(`/wms-transaction-service/shipmentDispatchSummary?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}`);
  }

  getShipmentDeliverySummary(data: any) {
    return this.apiService.get(`/wms-transaction-service/reports/shipmentDeliverySummary?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}`);
  }
  getShipmentDeliverySummarynew(data: any) {
    return this.apiService.get(`/wms-transaction-service/reports/shipmentDeliverySummary1?fromDeliveryDate=${data.fromDeliveryDate}&toDeliveryDate=${data.toDeliveryDate}&warehouseId=${data.warehouseId}&companyCodeId=${data.companyCodeId}&plantId=${data.plantId}&languageId=${data.languageId}`);
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

findInventory2(obj:any){
  return this.http.post<any>('/wms-transaction-service/inventory/findInventory/v2', obj);
}
findInventoryspark(obj:any){
  return this.http.post<any>('/mnr-spark-service/inventory/v2', obj);
}
  GetInventory() {
    return this.http.get<any>('/batch-fetch/jobInventoryQuery');
  }



  GetallDataStockReport(itemCode: string[], itemText: string, stockTypeText: string, warehouseId: string[],companyCodeId:string[],plantId:string[],languageId:string[]) {
    return this.apiService.get(this.configService.newstockReportAllData_url + '?itemCode=' + itemCode + '&itemText=' + itemText + '&stockTypeText=' + stockTypeText + '&warehouseId=' + warehouseId +'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId);
  }
  GetallDataStockReport1(itemCode: string[], itemText: string, stockTypeText: string, warehouseId: string[],companyCodeId:string[],plantId:string[],languageId:string[]) {
    return this.apiService.get(this.configService.newstockReportAllData_url + '?itemCode=' + itemCode + '&itemText=' + itemText + '&stockTypeText=' + stockTypeText + '&warehouseId=' + warehouseId +'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId);
  }
  getallDataStockReportv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/reports/v2/stockReport-all', obj);
  }

  getInventoryMovement(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventorymovement/findInventoryMovement', obj);
  }
  getInventoryMovementV2(obj: any) {
    return this.http.post<any>('/mnr-spark-service/inventorymovement', obj);
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
  getNewInventoryMovement1(obj:any) {
    return this.http.get<any>('/wms-transaction-service/streaming/inventoryMovement');
  }
  getTransactionHistoryReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/reports/transactionHistoryReport', obj);
  }
  getBinnerReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/putawayline/findPutAwayLine', obj);
  }
  getBinnerReportv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/putawayline/findPutAwayLine/v2', obj);
  } 
  getBinnerReportspark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/putawayline/v2', obj);
  } 
   getperpetuallinev2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/v2/findPerpetualLine', obj);
  }
  getperodiclinev2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/periodicline/v2/findPeriodicLine', obj);
  }
  getperodiclineSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/periodicline', obj);
  }
  getperpertuallineSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/perpetualline', obj);
  }

  getInboundStatusReport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inboundheader/findInboundHeader', obj);
  }
  getInboundStatusReportv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inboundheader/findInboundHeader/v2', obj);
  }
  getstatus(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/statusid/findStatusId', obj);
  }
  getInboundStatusReportSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/inboundHeader', obj);
  }
  getoutboundline(obj: any) {
    return this.http.post<any>('/wms-transaction-service/outboundline/v2/findOutboundLine', obj);
  }
  getHhtreport(obj: any) {
    return this.http.post<any>('/wms-transaction-service/pickupheader/findPickupHeader/v2/status', obj);
  }
  getperiodicheader(obj: any) {
    return this.http.post<any>('/wms-transaction-service/periodicheader/v2/findPeriodicHeader', obj);
  }
  getperiodicheaderv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/periodicheader/v2/findPeriodicHeaderNew', obj);
  }
  getgrlinev2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/grline/findGrLine/v2', obj);
  }
  getperpetualheader(obj: any) {
    return this.http.post<any>('/wms-transaction-service/perpetualheader/v2/findPerpetualHeader', obj);
  }
  getperpetualheaderv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/perpetualheader/v2/findPerpetualHeaderNew', obj);
  }
  sendMail() {
    return this.http.get<any>('/wms-idmaster-service/email/sendmail');
  }


  public uploadfile(file: File, location) {
    let formParams = new FormData();
    formParams.append('file', file)
    return this.http.post('/wms-idmaster-service/doc-storage/upload?' + `location=${location}`, formParams)
  }
 

  barCodeScan() {
    return this.http.get<any>('/wms-idmaster-service/interimbarcode');
  }

  findBarCodeScan(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/interimbarcode/findInterimBarcode' , obj);
  }
  getpickupLine(obj: any) {
    return this.http.post<any>('/wms-transaction-service/pickupline/v2/findPickupLine', obj);
  }
  pickupSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/pickUpLine', obj);
  }
}

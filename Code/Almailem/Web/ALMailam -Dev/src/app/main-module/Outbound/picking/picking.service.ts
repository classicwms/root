import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PickingService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'pickupheader';
  url = this.apiName + this.methodName;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findPickupHeader', obj);
  }
  searchv2(obj: any) {
    return this.http.post<any>(this.url + '/v2/findPickupHeader', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/pickupheader', obj);
  }
  getpickingline(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/findPickupLine', obj);
  }
  getpickinglinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/v2/findPickupLine', obj);
  }
  pickuplineSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/pickUpLine', obj);
  }
  getadditionalBins(itemCode: any, obOrdertypeId: any, warehouseId: any, proposedPackBarCode: any, proposedStorageBin: any) {
    return this.http.get<any>(this.apiName + 'pickupline/additionalBins?itemCode=' + itemCode + '&obOrdertypeId=' + obOrdertypeId + '&proposedStorageBin=' + proposedStorageBin + '&proposedPackBarCode=' + proposedPackBarCode + '&warehouseId=' + warehouseId);
  }
  getadditionalBinsv2(itemCode: any, obOrdertypeId: any, warehouseId: any, proposedPackBarCode: any, proposedStorageBin: any,companyCodeId:any,plantId:any,languageId:any,manufacturerName:any) {
    return this.http.get<any>(this.apiName + 'pickupline/v2/additionalBins?itemCode=' + itemCode + '&obOrdertypeId=' + obOrdertypeId + '&proposedStorageBin=' + proposedStorageBin + '&proposedPackBarCode=' + proposedPackBarCode + '&warehouseId=' + warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId+'&manufacturerName='+manufacturerName);
  }
  confirm(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline', obj);
  }
  confirmv2(obj: any) {
    return this.http.post<any>(this.apiName + 'v2/pickupline', obj);
  }
  GetInventoryv2(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventory/findInventory/v2', obj);
  }
  searchcancellation(obj: any) {
    return this.http.post<any>('/wms-transaction-service/pickListCancellation/v2/findPickListHeader', obj);
  }
  GetInventorySpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/inventory/v2', obj);
  }
  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  
  updatePicker(obj: any, pickupNumber: any, itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.patch<any>(this.apiName + 'pickupheader/' + pickupNumber + '?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, obj );
  }
  updatePickerv2(obj: any, pickupNumber: any, itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any,companyCodeId:any,plantId:any,languageId:any,) {
    return this.http.patch<any>(this.apiName + 'pickupheader/v2/' + pickupNumber + '?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId, obj );
  }

  multipleUpdatePicker(obj: any) {
    return this.http.patch<any>(this.apiName + 'pickupheader/update-assigned-picker', obj);
  }
  multipleUpdatePickerv2(obj: any) {
    return this.http.patch<any>(this.apiName + 'pickupheader/v2/update-assigned-picker', obj);
  }


  deletePickupHeader(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/pickupheader/'  + obj.pickupNumber + '?itemCode=' + obj.itemCode + '&lineNumber=' + obj.lineNumber + '&partnerCode=' + obj.partnerCode + '&preOutboundNo=' + obj.preOutboundNo + '&proposedPackCode=' + obj.proposedPackCode + '&proposedStorageBin=' + obj.proposedStorageBin + '&refDocNumber=' + obj.refDocNumber + '&warehouseId=' + obj.warehouseId);
  }

  deletePickupHeaderv2(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/pickupheader/v2/'  + obj.pickupNumber + '?itemCode=' + obj.itemCode + '&lineNumber=' + obj.lineNumber + '&partnerCode=' + obj.partnerCode + '&preOutboundNo=' + obj.preOutboundNo + '&proposedPackCode=' + obj.proposedPackCode + '&proposedStorageBin=' + obj.proposedStorageBin + '&refDocNumber=' + obj.refDocNumber + '&warehouseId=' + obj.warehouseId+'&companyCodeId='+obj.companyCodeId+'&plantId='+obj.plantId+'&languageId='+obj.languageId);
  }


}

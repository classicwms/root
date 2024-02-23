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
  getpickingline(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/findPickupLine', obj);
  }
  getadditionalBins(itemCode: any, obOrdertypeId: any, warehouseId: any, proposedPackBarCode: any, proposedStorageBin: any) {
    return this.http.get<any>(this.apiName + 'pickupline/additionalBins?itemCode=' + itemCode + '&obOrdertypeId=' + obOrdertypeId + '&proposedStorageBin=' + proposedStorageBin + '&proposedPackBarCode=' + proposedPackBarCode + '&warehouseId=' + warehouseId);
  }
  confirm(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline', obj);
  }
  GetInventory(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventory/findInventory', obj);
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  
  updatePicker(obj: any, pickupNumber: any, itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.patch<any>(this.apiName + 'pickupheader/' + pickupNumber + '?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, obj );
  }

  multipleUpdatePicker(obj: any) {
    return this.http.patch<any>(this.apiName + 'pickupheader/update-assigned-picker', obj);
  }


  deletePickupHeader(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/pickupheader/'  + obj.pickupNumber + '?itemCode=' + obj.itemCode + '&lineNumber=' + obj.lineNumber + '&partnerCode=' + obj.partnerCode + '&preOutboundNo=' + obj.preOutboundNo + '&proposedPackCode=' + obj.proposedPackCode + '&proposedStorageBin=' + obj.proposedStorageBin + '&refDocNumber=' + obj.refDocNumber + '&warehouseId=' + obj.warehouseId);
  }


}

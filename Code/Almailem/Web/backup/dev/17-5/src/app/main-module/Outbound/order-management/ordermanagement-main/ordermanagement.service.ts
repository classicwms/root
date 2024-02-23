import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class OrdermanagementService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'ordermanagementline';
  url = this.apiName + this.methodName;

  searchLine(obj: any) {
    return this.http.post<any>(this.url + '/findOrderManagementLine', obj);
  }

  AssignPicker(obj: any, assignedPickerId: any) {
    return this.http.patch<any>(this.url + '/assignPicker?assignedPickerId=' + assignedPickerId, obj);
  }
  Unallocate(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any,warehouseId: any, proposedPackBarCode: any,  proposedStorageBin: any) {
    return this.http.patch<any>(this.url + '/unallocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId  + '&proposedPackBarCode=' + proposedPackBarCode + '&proposedStorageBin=' + proposedStorageBin, {});
  }

  Allocate(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.patch<any>(this.url + '/allocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }
}

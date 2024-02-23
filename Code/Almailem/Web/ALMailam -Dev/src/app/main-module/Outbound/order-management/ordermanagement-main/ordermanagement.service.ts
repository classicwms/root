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
  searchLinev2(obj: any) {
    return this.http.post<any>(this.url + '/v2/findOrderManagementLine', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/ordermanagementline', obj);
  }
  AssignPicker(obj: any, assignedPickerId: any) {
    return this.http.patch<any>(this.url + '/assignPicker?assignedPickerId=' + assignedPickerId, obj);
  }
  AssignPickerv2(obj: any, assignedPickerId: any) {
    return this.http.patch<any>(this.url + '/v2/assignPicker?assignedPickerId=' + assignedPickerId, obj);
  }
  Unallocate(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any,warehouseId: any, proposedPackBarCode: any,  proposedStorageBin: any) {
    return this.http.patch<any>(this.url + '/unallocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId  + '&proposedPackBarCode=' + proposedPackBarCode + '&proposedStorageBin=' + proposedStorageBin, {});
  }
  UnallocateV2(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any,warehouseId: any, proposedPackBarCode: any,  proposedStorageBin: any,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.patch<any>(this.url + '/v2/unallocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId  + '&proposedPackBarCode=' + proposedPackBarCode + '&proposedStorageBin=' + proposedStorageBin+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId, {});
  }


  Allocate(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.patch<any>(this.url + '/allocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }
  AllocateV2(itemCode: any, lineNumber: any, partnerCode: any, preOutboundNo: any, refDocNumber: any, warehouseId: any,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.patch<any>(this.url + '/v2/allocate?itemCode=' + itemCode + '&lineNumber=' + lineNumber + '&partnerCode=' + partnerCode + '&preOutboundNo=' + preOutboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId, {});
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  allocateBatch(obj: any) {
    return this.http.patch<any>('/wms-transaction-service/ordermanagementline/v2/allocate/patch', obj);
  }
  unallocateBatch(obj: any) {
    return this.http.patch<any>('/wms-transaction-service/ordermanagementline/v2/unallocate/patch', obj);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class QualityService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'qualityheader';
  url = this.apiName + this.methodName;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findQualityHeader', obj);
  }

  getpickingline(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/findPickupLine', obj);
  }
  getqualityline(obj: any) {
    return this.http.post<any>(this.apiName + 'qualityline/findQualityLine', obj);
  }
  confirm(obj: any) {
    return this.http.post<any>(this.apiName + 'qualityline', obj);
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  deleteQualitypHeader(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/qualityheader/'  + obj.qualityInspectionNo + '?actualHeNo=' + obj.actualHeNo + '&partnerCode=' + obj.partnerCode + '&pickupNumber=' + obj.pickupNumber + '&preOutboundNo=' + obj.preOutboundNo + '&refDocNumber=' + obj.refDocNumber +  '&warehouseId=' + obj.warehouseId);
  }
}

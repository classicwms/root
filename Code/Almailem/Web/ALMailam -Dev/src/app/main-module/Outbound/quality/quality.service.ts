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
  searchv2(obj: any) {
    return this.http.post<any>(this.url + '/v2/findQualityHeader', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/qualityHeader', obj);
  }

  getpickingline(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/findPickupLine', obj);
  }
  getpickinglinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'pickupline/v2/findPickupLine', obj);
  }
  getpickinglineSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/pickUpLine', obj);
  }
  getqualityline(obj: any) {
    return this.http.post<any>(this.apiName + 'qualityline/findQualityLine', obj);
  }
  getqualitylinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'qualityline/v2/findQualityLine', obj);
  }
  confirm(obj: any) {
    return this.http.post<any>(this.apiName + 'qualityline', obj);
  }
  confirmv2(obj: any) {
    return this.http.post<any>(this.apiName + 'v2/qualityline', obj);
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  deleteQualitypHeader(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/qualityheader/'  + obj.qualityInspectionNo + '?actualHeNo=' + obj.actualHeNo + '&partnerCode=' + obj.partnerCode + '&pickupNumber=' + obj.pickupNumber + '&preOutboundNo=' + obj.preOutboundNo + '&refDocNumber=' + obj.refDocNumber +  '&warehouseId=' + obj.warehouseId);
  }
  deleteQualitypHeaderv2(obj: any) {
    return this.http.delete<any>('/wms-transaction-service/qualityheader/v2/'  + obj.qualityInspectionNo + '?actualHeNo=' + obj.actualHeNo + '&partnerCode=' + obj.partnerCode + '&pickupNumber=' + obj.pickupNumber + '&preOutboundNo=' + obj.preOutboundNo + '&refDocNumber=' + obj.refDocNumber +  '&warehouseId=' + obj.warehouseId+'&companyCodeId='+obj.companyCodeId+'&plantId='+obj.plantId+'&languageId='+obj.languageId);
  }
}

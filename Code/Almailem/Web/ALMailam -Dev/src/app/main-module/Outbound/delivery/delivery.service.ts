import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-transaction-service/';
  methodName = 'outboundheader';
  url = this.apiName + this.methodName;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findOutboundHeader', obj);
  }
  searchv2(obj: any) {
    return this.http.post<any>(this.url + '/v2/findOutboundHeader', obj);
  }
  searchRfd(obj: any) {
    return this.http.post<any>('/wms-transaction-service/outboundheader/v2/findOutboundHeader/rfd', obj);
  }
  searchspark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/outboundheader', obj);
  }

  searchLine(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine', obj);
  }
  searchLinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/v2/findOutboundLine', obj);
  }
  searchLinenew(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine-new', obj);
  }
  confirm(data: any,) {
    return this.http.get<any>(this.apiName + 'outboundline/delivery/confirmation?partnerCode=' + data.partnerCode + '&preOutboundNo=' + data.preOutboundNo + '&refDocNumber=' + data.refDocNumber + '&warehouseId=' + data.warehouseId);
  }
  confirmv2(data: any,) {
    return this.http.get<any>(this.apiName + 'outboundline/v2/delivery/confirmation?partnerCode=' + data.partnerCode + '&preOutboundNo=' + data.preOutboundNo + '&refDocNumber=' + data.refDocNumber + '&warehouseId=' + data.warehouseId+'&companyCodeId='+data.companyCodeId+'&plantId='+data.plantId+'&languageId='+data.languageId);
  }

  getOtBoundDeliveryFormPdf(orderNumber, warehouseId) {
    return this.http.get<any>(this.apiName + 'reports/v2/shipmentDelivery' + `?orderNumber=${orderNumber}&warehouseId=${warehouseId}&companyCodeId=${this.auth.companyId}&languageId=${this.auth.languageId}
    &plantId=${this.auth.plantId}`);
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  searchBusinessPartner(obj: any) {
    return this.http.post<any>('/wms-masters-service/businesspartner/findBusinessPartner', obj);
  }
  Updateheader(obj: any, code: any, languageId: any,companyCodeId:any,plantId:any,warehouseId:any,refDocNumber:any,partnerCode:any) {
    return this.http.patch<any>('/wms-transaction-service/outboundheader/v2/' + code + '?languageId=' + languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&refDocNumber='+refDocNumber+'&partnerCode='+partnerCode, obj);
  }
 
}

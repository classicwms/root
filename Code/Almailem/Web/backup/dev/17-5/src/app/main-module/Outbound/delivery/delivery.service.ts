import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'outboundheader';
  url = this.apiName + this.methodName;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findOutboundHeader', obj);
  }

  searchLine(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine', obj);
  }
  searchLinenew(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine-new', obj);
  }
  confirm(data: any,) {
    return this.http.get<any>(this.apiName + 'outboundline/delivery/confirmation?partnerCode=' + data.partnerCode + '&preOutboundNo=' + data.preOutboundNo + '&refDocNumber=' + data.refDocNumber + '&warehouseId=' + data.warehouseId);
  }

  getOtBoundDeliveryFormPdf(orderNumber, warehouseId) {
    return this.http.get<any>(this.apiName + 'reports/shipmentDelivery' + `?orderNumber=${orderNumber}&warehouseId=${warehouseId}`);
  }

  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  searchBusinessPartner(obj: any) {
    return this.http.post<any>('/wms-masters-service/businesspartner/findBusinessPartner', obj);
  }
}

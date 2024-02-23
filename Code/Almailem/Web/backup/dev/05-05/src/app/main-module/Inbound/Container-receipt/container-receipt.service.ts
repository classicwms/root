import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ContainerReceiptService {



  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'containerreceipt';
  url = this.apiName + this.methodName;
  Getall() {
    return this.http.get<any>(this.url);
  }
  Get(containerReceiptNo: any,) {
    return this.http.get<any>(this.url + '/' + containerReceiptNo);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url, obj);
  }
  Update(obj: any, containerReceiptNo: any) {
    return this.http.patch<any>(this.url + '/' + containerReceiptNo, obj);
  }
  Delete(containerReceiptNo: any) {
    return this.http.delete<any>(this.url + '/' + containerReceiptNo);
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findContainerReceipt', obj);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
}

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
  Get(containerReceiptNo: any, company, plant, language, warehoueId) {
    return this.http.get<any>(this.url + '/' + containerReceiptNo + '/v2' + '?companyCode=' + company + '&plantId=' + plant + '&languageId=' + language + '&warehouseId=' + warehoueId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url + '/v2', obj);
  }
  Update(obj: any, containerReceiptNo: any, company, plant, language, warehoueId) {
    return this.http.patch<any>(this.url + '/' + containerReceiptNo + '/v2'+ '?companyCode=' + company + '&plantId=' + plant + '&languageId=' + language + '&warehouseId=' + warehoueId, obj);
  }
  Delete(containerReceiptNo: any) {
    return this.http.delete<any>(this.url + containerReceiptNo + '/v2');
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findContainerReceipt/v2', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/containerReceipt', obj);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
}

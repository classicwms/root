import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ReversalOutboundService {


  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'outboundreversal';
  url = this.apiName + this.methodName;

  search(obj: any) {
    return this.http.post<any>(this.url + '/findOutboundReversal', obj);
  }
  searchv2(obj: any) {
    return this.http.post<any>(this.url + '/v2/findOutboundReversal', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/outboundReversal', obj);
  }

  searchsoLine(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine-new', obj);
  }
  searchsoLinev2(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/v2/findOutboundLine', obj);
  }
  reversal(itemCode: any, refDocNumber: any) {
    return this.http.get<any>(this.url + '/reversal/new?itemCode=' + itemCode + '&refDocNumber=' + refDocNumber);
  }
  reversalv2(itemCode: any, refDocNumber: any, manufacturerName: any) {
    return this.http.get<any>(this.url + '/v2/reversal/new?itemCode=' + itemCode + '&refDocNumber=' + refDocNumber + '&manufacturerName=' + manufacturerName);
  }
}

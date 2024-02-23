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


  searchsoLine(obj: any) {
    return this.http.post<any>(this.apiName + 'outboundline/findOutboundLine-new', obj);
  }
  reversal(itemCode: any, refDocNumber: any) {
    return this.http.get<any>(this.url + '/reversal/new?itemCode=' + itemCode + '&refDocNumber=' + refDocNumber);
  }
}

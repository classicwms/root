import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class OutboundsucessordersService {

  constructor(private http: HttpClient, private auth: AuthService) { }
  Getall() {
    return this.http.get<any>('/wms-transaction-service/orders/outbound/byDate');
  }
  Get(orderStartDate:any,orderEndDate:any) {
    return this.http.get<any>('/wms-transaction-service/orders/outbound/byDate' + '?orderStartDate=' + orderStartDate +'&orderEndDate='+orderEndDate );
  }
 
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class OutboundfailedordersService {

  constructor(private http: HttpClient, private auth: AuthService) { }
  Getall() {
    return this.http.get<any>('/wms-transaction-service/orders/outbound/failed');
  }
 
}

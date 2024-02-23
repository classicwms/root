import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WmsIdmasterService {

  constructor(private http: HttpClient) { }
  wmsApiName = '/wms-idmaster-service/';
  getallcity() {
    return this.http.get<any>(this.wmsApiName + `city`);
  }
  getallcompany() {
    return this.http.get<any>(this.wmsApiName + `company`);
  }
  getallcountry() {
    return this.http.get<any>(this.wmsApiName + `country`);
  }
  getallcurrency() {
    return this.http.get<any>(this.wmsApiName + `currency`);
  }
  getallstate() {
    return this.http.get<any>(this.wmsApiName + `state`);
  }

  getallvetical() {
    return this.http.get<any>(this.wmsApiName + `vetical`);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PickerService {


  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-idmaster-service/';
  methodName = 'hhtuser';
  url = this.apiName + this.methodName;
  Getall() {
    return this.http.get<any>(this.url + '/userId');
  }


  GetWarehousehht_login() {
    return this.http.get<any>(this.url + '/' + this.auth.warehouseId + '/hhtUser');
  }
  search1(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/hhtuser/findHhtUser', obj);
  }
  GetWarehousehht_loginv2(obj:any) {
    return this.http.get<any>(this.url + '/findHhtUser',obj);
  }


  GetStoreCode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }
}

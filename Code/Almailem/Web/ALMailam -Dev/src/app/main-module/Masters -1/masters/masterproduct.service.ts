import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MasterproductService {

  constructor(private http: HttpClient,) { }

  wmsApiName = '/wms-masters-service/';
  getallitemcode() {
    return this.http.get<any>(this.wmsApiName + `imbasicdata1`);
  }
  getitemcode(id: any) {
    return this.http.get<any>(this.wmsApiName + `imbasicdata1/` + id);
  }
  patchitemcode(id: any, data: any) {
    return this.http.patch<any>(this.wmsApiName + `imbasicdata1?basicdata1=` + id, data);
  }

 
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
export interface dropdownvalue {
  code: string,
  description: string
}
@Injectable({
  providedIn: 'root'
})


export class SetupService {

  constructor(private http: HttpClient,) { }

  wmsApiName = '/wms-enterprise-service/';
  getallcompany() {
    return this.http.get<any>(this.wmsApiName + `company`);
  }
  getcompany(id: any) {
    return this.http.get<any>(this.wmsApiName + `company/` + id);
  }
  patchcompany(id: any, data: any) {
    return this.http.patch<any>(this.wmsApiName + `company?companyCode=` + id, data);
  }
}

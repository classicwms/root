import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CompanySetupService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `company`);
  }
  Get(code: string,languageId:any) {
    return this.http.get<any>(this.apiName + `company/` + code+'?languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `company`, obj);
  }
  Update(obj: any, code: any,languageId:any) {
    return this.http.patch<any>(this.apiName + `company/` + code+'?languageId='+languageId, obj);
  }
  Delete(company: any,languageId:any) {
    return this.http.delete<any>(this.apiName + `company?` + '&companyId=' + company+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'company/findCompany', obj);
  }
}



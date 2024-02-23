import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/companyid');
  }
  Get(code: string,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/companyid/' + code+'?languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/companyid', obj);
  }
  Update(obj: any, code: any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/companyid/' + code+'?languageId='+languageId, obj);
  }
  Delete(companyCodeId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/companyid/' + companyCodeId+'?languageId='+languageId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/companyid/findcompanyid', obj);
  }
}





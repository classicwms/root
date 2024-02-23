import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PartnerService {
  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `impartner`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `impartner/`+ code);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `impartner`, obj);
  }
  Update(obj: any, code: any) {
    return this.http.patch<any>(this.apiName + `impartner/` + code, obj);
  }
  Delete(businessPartnerCode: any) {
    return this.http.delete<any>(this.apiName + `impartner/`+businessPartnerCode);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  Getpartnercode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class AltuomService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imalternateuom`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `imalternateuom/` + code);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imalternateuom`, obj);
  }
  Update(obj: any, code: any) {
    return this.http.patch<any>(this.apiName + `imalternateuom/` + code, obj);
  }
  Delete(code: any) {
    return this.http.delete<any>(this.apiName + `imalternateuom/` + code);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
}
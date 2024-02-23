import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class VariantService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `variant`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `variant/`+ code);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `variant`, obj);
  }
  Update(obj: any, code: any) {
    return this.http.patch<any>(this.apiName + `variant/` + code, obj);
  }
  Delete(variantCode: any) {
    return this.http.delete<any>(this.apiName + `variant/`+variantCode);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'variant/findVariant', obj);
  }
}


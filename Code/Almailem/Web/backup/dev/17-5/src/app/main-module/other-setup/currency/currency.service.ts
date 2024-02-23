import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/currency');
  }
  Get(code: string,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/currency/' + code +'?languageId='+languageId  );
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/currency/', obj);
  }
  Update(obj: any, code: any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/currency/' + code+'?languageId='+languageId , obj);
  }
  Delete(currencyId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/currency/' + currencyId+'?languageId='+languageId );
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/currency/findCurrency', obj);
}
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CountryService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/country');
  }
  Get(code: string,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/country/' + code +'?languageId='+languageId  );
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/country', obj);
  }
  Update(obj: any, code: any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/country/' + code+'?languageId='+languageId , obj);
  }
  Delete(countryId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/country/' + countryId +'?languageId='+languageId );
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/country/findcountry', obj);
}
}


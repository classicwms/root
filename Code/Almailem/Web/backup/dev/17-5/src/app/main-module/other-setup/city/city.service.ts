import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/city');
  }
  Get(cityId:any,countryId:any,languageId:any,stateId:any,zipCode:any) {
    return this.http.get<any>('/wms-idmaster-service/city/'+ cityId+'?countryId='+countryId+'&languageId='+languageId+'&stateId='+stateId+'&zipCode='+zipCode );
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/city/', obj);
  }
  Update(obj: any, code: any,countryId:any,languageId:any,stateId:any,zipCode:any) {
    return this.http.patch<any>('/wms-idmaster-service/city/' + code+'?countryId='+countryId+'&languageId='+languageId+'&stateId='+stateId+'&zipCode='+zipCode , obj);
  }
  Delete(cityId: any,countryId:any,languageId:any,stateId:any,zipCode:any) {
    return this.http.delete<any>('/wms-idmaster-service/city/' + cityId+'?countryId='+countryId+'&languageId='+languageId+'&stateId='+stateId+'&zipCode='+zipCode);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/city/findcity', obj);
}
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StateService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/state');
  }
  Get(code: string,countryId:any,languageId:any ) {
    return this.http.get<any>('/wms-idmaster-service/state/' + code +'?countryId='+ countryId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/state', obj);
  }
  Update(obj: any, code: any,countryId:any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/state/' + code+'?countryId='+ countryId+'&languageId='+languageId, obj);
  }
  Delete(stateId: any,countryId:any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/state/' + stateId+'?countryId='+ countryId+'&languageId='+languageId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/state/findState', obj);
}
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class VerticalService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/vertical');
  }
  Get(code: string,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/vetical/' + code +'?languageId='+languageId );
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/vertical', obj);
  }
  Update(obj: any, code: any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/vertical/' + code+'?languageId='+languageId , obj);
  }
  Delete(verticalId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/vertical/' + verticalId + '?languageId='+languageId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/vertical/findVertical', obj);
  }
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StatusidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/statusid');
  }
  Get(code: string, languageId: string) {
    return this.http.get<any>('/wms-idmaster-service/statusid/' + code + '?languageId=' + languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/statusid/', obj);
  }
  Update(obj: any, code: any, languageId: any) {
    return this.http.patch<any>('/wms-idmaster-service/statusid/' + code + '?languageId=' + languageId, obj);
  }
  Delete(statusId: any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/statusid/' + statusId +'?languageId='+languageId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/statusid/findStatusId', obj);
  }
}




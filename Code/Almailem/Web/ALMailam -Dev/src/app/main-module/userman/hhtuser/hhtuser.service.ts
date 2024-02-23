import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class HhtuserService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/hhtuser');
  }
  Get(code: string,warehouseId:any,plantId:any,companyCodeId:any,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/hhtuser/'+ code+'?warehouseId='+warehouseId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/hhtuser', obj);
  }
  Update(obj: any, code: any,warehouseId:any,plantId:any,companyCodeId:any,languageId:any,levelId:any) {
    return this.http.patch<any>('/wms-idmaster-service/hhtuser/' + code+'?warehouseId='+warehouseId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&levelId='+levelId , obj);
  }
  Delete(userId: any,warehouseId:any,plantId:any,companyCodeId:any,languageId:any,levelId:any) {
    return this.http.delete<any>('/wms-idmaster-service/hhtuser/' + userId+ '?warehouseId='+warehouseId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&levelId='+levelId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/hhtuser/findHhtUser', obj);
  }
}


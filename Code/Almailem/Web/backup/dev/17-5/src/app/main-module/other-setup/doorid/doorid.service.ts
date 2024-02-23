import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DooridService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/doorid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,doorDescription:any) {
    return this.http.get<any>('/wms-idmaster-service/doorid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&doorDescription='+doorDescription);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/doorid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,doorDescription:any) {
    return this.http.patch<any>('/wms-idmaster-service/doorid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&doorDescription='+doorDescription, obj);
  }
  Delete(doorId: any,warehouseId:any,languageId:any,companyCodeId:any,plantId:any,doorDescription:any) {
    return this.http.delete<any>('/wms-idmaster-service/doorid/' + doorId+'?warehouseId='+warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&doorDescription='+doorDescription);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/doorid/findDoorId', obj);
}
}



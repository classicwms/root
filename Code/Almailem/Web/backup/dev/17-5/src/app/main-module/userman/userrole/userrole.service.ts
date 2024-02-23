import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserroleService {


  constructor(private http: HttpClient) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/roleaccess');
  }
  Get(code: string, warehouseId: string, companyCodeId: string, plantId: string,  languageId: string,) {
    return this.http.get<any>('/wms-idmaster-service/roleaccess/' + code + '?warehouseId=' + warehouseId + '&companyCodeId=' + companyCodeId + '&plantId=' + plantId + '&languageId=' + languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/roleaccess', obj);
  }
  Update(obj: any, code: any,warehouseId: string) {
    return this.http.patch<any>('/wms-idmaster-service/roleaccess/' + code +'?warehouseId=' + warehouseId, obj);
  }
  Delete(userRoleId: any, warehouseId: string,menuId:any,subMenuId:any) {
    return this.http.delete<any>('/wms-idmaster-service/roleaccess/' + userRoleId+'?warehouseId=' + warehouseId +'&menuId=' +menuId +'&subMenuId=' +subMenuId);
}
}



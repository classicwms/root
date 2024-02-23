import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class UserprofileService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/usermanagement');
  }
  Get(code: string, warehouseId: any,companyCode:any,languageId:any,plantId:any,userRoleId:any) {
    return this.http.get<any>('/wms-idmaster-service/usermanagement/' + code + '?warehouseId=' + warehouseId+'&companyCode='+companyCode+'&languageId='+languageId+'&plantId='+plantId+'&userRoleId='+userRoleId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/usermanagement', obj);
  }
  Update(obj: any, code: any, warehouseId: any,companyCode:any,languageId:any,plantId:any,userRoleId:any) {
    return this.http.patch<any>('/wms-idmaster-service/usermanagement/' + code +'?warehouseId=' + warehouseId+'&companyCode='+companyCode+'&languageId='+languageId+'&plantId='+plantId+'&userRoleId='+userRoleId, obj);
  }
  Delete(userId: any,warehouseId: any,companyCode:any,languageId:any,plantId:any,userRoleId:any) {
    return this.http.delete<any>('/wms-idmaster-service/usermanagement/' + userId + '?warehouseId=' + warehouseId+'&companyCode='+companyCode+'&languageId='+languageId+'&plantId='+plantId+'&userRoleId='+userRoleId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/usermanagement/findUserManagement', obj);
  }

  findProfile(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/usermanagement/findUserManagement', obj);
  }
}

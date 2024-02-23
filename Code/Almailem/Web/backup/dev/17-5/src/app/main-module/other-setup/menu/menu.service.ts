import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/menuid');
  }
  Get(code :any,warehouseId:any,authorizationObjectId:any,companyCodeId:any,languageId:any,menuId:any,plantId:any,subMenuId:any) {
    return this.http.get<any>('/wms-idmaster-service/menuid/'+ code  +'?warehouseId='+warehouseId+'&authorizationObjectId='+authorizationObjectId+'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&menuId='+menuId+'&plantId='+plantId+'&subMenuId='+subMenuId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/menuid', obj);
  }
  Update(obj: any, code: any,warehouseId:any,authorizationObjectId:any,companyCodeId:any,languageId:any,menuId:any,plantId:any,subMenuId:any) {
    return this.http.patch<any>('/wms-idmaster-service/menuid/' + code +'?warehouseId='+warehouseId+'&authorizationObjectId='+authorizationObjectId+'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&menuId='+menuId+'&plantId='+plantId+'&subMenuId='+subMenuId, obj);
  }
  Delete(menuId: any,warehouseId:any,authorizationObjectId:any,companyCodeId:any,languageId:any,plantId:any,subMenuId:any) {
    return this.http.delete<any>('/wms-idmaster-service/menuid/' + menuId +'?warehouseId='+warehouseId+'&authorizationObjectId='+authorizationObjectId+'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&subMenuId='+subMenuId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/menuid/findMenuId', obj);
  }
}



import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class WorkcenterService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `workcenter`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,workCenterType:any) {
    return this.http.get<any>(this.apiName + `workcenter/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&workCenterType='+workCenterType);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `workcenter`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,workCenterType:any) {
    return this.http.patch<any>(this.apiName + `workcenter/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&workCenterType='+workCenterType, obj);
  }
  Delete(code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,workCenterType:any) {
    return this.http.delete<any>(this.apiName + `workcenter/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&workCenterType='+workCenterType);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'workCenter/findWorkCenter', obj);
  }
}



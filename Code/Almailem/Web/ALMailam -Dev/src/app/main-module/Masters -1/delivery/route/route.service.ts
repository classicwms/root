import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'route';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `route`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>(this.apiName + `route/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `route`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>(this.apiName + `route/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(routeId: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>(this.apiName + `route/` +  routeId +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId );
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findRoute', obj);
  }

 
}




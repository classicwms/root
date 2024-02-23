import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DriverService {

 
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'driver';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `driver`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>(this.apiName + `driver/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `driver`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>(this.apiName + `driver/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(driverId: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>(this.apiName + `driver/` +  driverId +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId );
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findDriver', obj);
  }

 
}



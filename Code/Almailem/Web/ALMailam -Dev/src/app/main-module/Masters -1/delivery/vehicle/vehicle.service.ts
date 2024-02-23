import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'vehicle';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `vehicle`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>(this.apiName + `vehicle/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `vehicle`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>(this.apiName + `vehicle/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(vehicleNumber: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>(this.apiName + `vehicle/` +  vehicleNumber +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId );
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findVehicle', obj);
  }

 
}



import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DriverassignService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'drivervehicleassignment';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `drivervehicleassignment`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,vehicleNumber:any,routeId:any) {
    return this.http.get<any>(this.apiName + `drivervehicleassignment/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&vehicleNumber='+vehicleNumber+'&routeId='+routeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `drivervehicleassignment`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,vehicleNumber:any,routeId:any) {
    return this.http.patch<any>(this.apiName + `drivervehicleassignment/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&vehicleNumber='+vehicleNumber+'&routeId='+routeId, obj);
  }
  Delete(driverId: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,vehicleNumber:any,routeId:any) {
    return this.http.delete<any>(this.apiName + `drivervehicleassignment/` +  driverId +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&vehicleNumber='+vehicleNumber+'&routeId='+routeId );
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findDriverVehicleAssignment', obj);
  }

 
}



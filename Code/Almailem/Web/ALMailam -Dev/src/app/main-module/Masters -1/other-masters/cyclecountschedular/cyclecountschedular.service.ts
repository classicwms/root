import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class CyclecountschedularService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `cyclecountscheduler`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,levelId:any,schedulerNumber:any) {
    return this.http.get<any>(this.apiName + `cyclecountscheduler/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&levelId='+levelId+'&schedulerNumber='+schedulerNumber);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `cyclecountscheduler`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,levelId:any,schedulerNumber:any) {
    return this.http.patch<any>(this.apiName + `cyclecountscheduler/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&levelId='+levelId+'&schedulerNumber='+schedulerNumber, obj);
  }
  Delete(code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,levelId:any,schedulerNumber:any) {
    return this.http.delete<any>(this.apiName + `cyclecountscheduler/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&levelId='+levelId+'&schedulerNumber='+schedulerNumber);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'cyclecountscheduler/findCycleCountScheduler', obj);
  }
}



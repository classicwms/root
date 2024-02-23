import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StratergyService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `strategies`);
  }
  Get(code: string,obj:any) {
    return this.http.get<any>(this.apiName + `strategies/get`+ '?warehouseId='+ obj.warehouseId +'&companyId='+ obj.companyId +'&languageId='+ obj.languageId
    +'&plantId='+ obj.plantId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `strategies`, obj);
  }
  Update(code: any,obj:any, obj1: any) {
    return this.http.patch<any>(this.apiName + `strategies/update`+'?warehouseId='+ obj.warehouseId +'&companyId='+ obj.companyId +'&languageId='+ obj.languageId
    +'&plantId='+ obj.plantId, obj1);
  }
  Delete(obj: any,warehouseId:any) {
    return this.http.delete<any>(this.apiName + `strategies/delete`+'?warehouseId='+warehouseId  +'&companyId='+ obj.companyId +'&languageId='+ obj.languageId
    +'&plantId='+ obj.plantId );
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'strategies/findStrategies', obj);
  }
}


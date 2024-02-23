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
  Get(code: string,warehouseId:any) {
    return this.http.get<any>(this.apiName + `strategies/`+ code+'?warehouseId='+warehouseId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `strategies`, obj);
  }
  Update(obj: any,code:any,warehouseId:any) {
    return this.http.patch<any>(this.apiName + `strategies/`+code+'?warehouseId='+ warehouseId , obj);
  }
  Delete(strategyTypeId: any,warehouseId:any) {
    return this.http.delete<any>(this.apiName + `strategies/`+strategyTypeId+'?warehouseId='+warehouseId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'strategies/findStrategies', obj);
  }
}


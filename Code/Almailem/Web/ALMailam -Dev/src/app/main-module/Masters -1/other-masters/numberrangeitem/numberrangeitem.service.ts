import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class NumberrangeitemService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `numberrangeitem`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceNo:any) {
    return this.http.get<any>(this.apiName + `numberrangeitem/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceNo='+sequenceNo);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `numberrangeitem`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceNo:any) {
    return this.http.patch<any>(this.apiName + `numberrangeitem/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceNo='+sequenceNo, obj);
  }
  Delete(code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceNo:any) {
    return this.http.delete<any>(this.apiName + `numberrangeitem/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceNo='+sequenceNo);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'numberrangeitem/find', obj);
  }
}


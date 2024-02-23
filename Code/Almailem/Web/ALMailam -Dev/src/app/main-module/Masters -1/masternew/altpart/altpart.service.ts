import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class AltpartService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'storagebin';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `imalternatepart`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any) {
    return this.http.get<any>(this.apiName + `imalternatepart/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imalternatepart`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>(this.apiName + `imalternatepart/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(code:any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>(this.apiName + `imalternatepart/` +  code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-masters-service/imalternatepart/findImAlternatePart', obj);
  }

 
}



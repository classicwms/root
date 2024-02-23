import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class BinlocationService {


  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'storagebin';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `storagebin`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>(this.apiName + `storagebin/v2/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `storagebin/v2/`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>(this.apiName + `storagebin/v2/` + code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(storageBin: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>(this.apiName + `storagebin/v2/` +  storageBin +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-masters-service/storagebin/findStorageBin', obj);
  }
  search1(obj: any) {
    return this.http.post<any>('/wms-masters-service/storagebin/findStorageBinNew', obj);
  }
  searchSpark(obj: any) {
    return this.http.post<any>('/mnr-spark-service/storageBin', obj);
  }

 
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class NumberrangestoragebinService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `numberrangestoragebin`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,aisleNumber:any,floorId:any,rowId:any) {
    return this.http.get<any>(this.apiName + `numberrangestoragebin/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&aisleNumber='+aisleNumber+'&floorId='+floorId+'&rowId='+rowId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `numberrangestoragebin`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,aisleNumber:any,floorId:any,rowId:any) {
    return this.http.patch<any>(this.apiName + `numberrangestoragebin/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&aisleNumber='+aisleNumber+'&floorId='+floorId+'&rowId='+rowId, obj);
  }
  Delete(code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,aisleNumber:any,floorId:any,rowId:any) {
    return this.http.delete<any>(this.apiName + `numberrangestoragebin/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&aisleNumber='+aisleNumber+'&floorId='+floorId+'&rowId='+rowId);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'numberrangestoragebin/findNumberRangeStorageBin', obj);
  }
}



import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class BatchserialService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `batchserial`);
  }
  Get(code: string,languageId:any,plantId:any,warehouseId:any,companyId:any,levelId:any, maintenance: any) {
    return this.http.get<any>(this.apiName + `batchserial/`+ code + '?languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&companyId='+companyId+'&levelId='+levelId +'&maintenance='+maintenance);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `batchserial`, obj);
  }
  Update(obj: any, code: any,warehouseId:any, languageId:any,plantId:any,companyId:any,levelId:any, maintenance: any) {
    return this.http.patch<any>(this.apiName + `batchserial/` + code +'?languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&companyId='+companyId+'&levelId='+levelId +'&maintenance='+maintenance, obj);
  }
  Delete(obj: any) {
    return this.http.delete<any>(this.apiName + `batchserial/`+obj.storageMethod +'?languageId='+obj.languageId+'&plantId='+ obj.plantId+'&warehouseId='+ obj.warehouseId+'&companyId='+ obj.companyId +'&levelId='+ obj.levelId +'&maintenance='+ obj.maintenance +'&storageMethod='+ obj.storageMethod);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'batchserial/findBatchSerial', obj);
  }
}

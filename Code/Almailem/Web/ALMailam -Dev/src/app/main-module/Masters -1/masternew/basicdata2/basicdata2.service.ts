import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class Basicdata2Service {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imbasicdata2`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any) {
    return this.http.get<any>(this.apiName + `imbasicdata2/` +code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imbasicdata2`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any) {
    return this.http.patch<any>(this.apiName + `imbasicdata2/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode, obj);
  }
  Delete(warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any) {
    return this.http.delete<any>(this.apiName + `imbasicdata2/` +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'imbasicdata2/findImBasicData2', obj);
  }

}
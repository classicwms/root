import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Item } from 'angular2-multiselect-dropdown';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StratergiesService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imstrategies`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceIndicator:any,itemCode:any,strategyTypeId:any) {
    return this.http.get<any>(this.apiName + `imstrategies/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceIndicator='+sequenceIndicator+'&itemCode='+itemCode+'&strategyTypeId='+strategyTypeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imstrategies`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceIndicator:any,itemCode:any) {
    return this.http.patch<any>(this.apiName + `imstrategies/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceIndicator='+sequenceIndicator+'&itemCode='+itemCode, obj);
  }
  Delete(warehouseId:any,languageId:any,plantId:any,companyCodeId:any,sequenceIndicator:any,itemCode:any,strategyTypeId:any) {
    return this.http.delete<any>(this.apiName + `imstrategies/` +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&sequenceIndicator='+sequenceIndicator+'&itemCode='+itemCode+'&strategyTypeId='+strategyTypeId);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'imstrategies/findImStrategies', obj);
  }
}


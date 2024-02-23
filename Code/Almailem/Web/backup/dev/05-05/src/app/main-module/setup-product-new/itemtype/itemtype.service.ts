import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/Auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ItemtypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `itemtype`);
  }
  Get(code: string,warehouseId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.get<any>(this.apiName + `itemtype/`+ code+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `itemtype`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.patch<any>(this.apiName + `itemtype/` + code+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId, obj);
  }
  Delete(itemTypeId: any,warehouseId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.delete<any>(this.apiName + `itemtype/`+itemTypeId+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'itemtype/findItemType', obj);
  }
}

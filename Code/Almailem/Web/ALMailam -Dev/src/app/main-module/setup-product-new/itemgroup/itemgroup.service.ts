import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ItemgroupService {
  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `itemgroup`);
  }
  Get(warehouseId:any,itemTypeId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.get<any>(this.apiName + `itemgroup/`+ itemTypeId+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `itemgroup`, obj);
  }
  Update(obj: any,warehouseId:any,itemTypeId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.patch<any>(this.apiName + `itemgroup/` + itemTypeId+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId, obj);
  }
  Delete(itemGroupId: any,warehouseId:any,subItemGroupId:any,itemTypeId:any,companyId:any,languageId:any,plantId:any) {
    return this.http.delete<any>(this.apiName + `itemgroup/`+itemTypeId+'?warehouseId='+warehouseId+'&companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'itemgroup/findItemGroup', obj);
  }
}
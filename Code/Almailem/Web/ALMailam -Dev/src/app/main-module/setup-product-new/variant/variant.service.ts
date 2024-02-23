import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class VariantService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `variant`);
  }
  Get(code: string,languageId:any,plantId:any,warehouseId:any, companyId:any,levelId:any,variantSubId:any) {
    return this.http.get<any>(this.apiName + `variant/`+ code+'?companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&levelId='+levelId+'&variantSubId='+variantSubId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `variant`, obj);
  }
  Update(obj: any, code: any,warehouseId:any, languageId:any,plantId:any, companyId:any, levelId:any, subId:any) {
    return this.http.patch<any>(this.apiName + `variant/` + code+'?companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&levelId='+levelId+'&variantSubId='+subId , obj);
  }
  Delete(variantCode: any,companyId:any,languageId:any,plantId:any,warehouseId:any,levelId:any, subId) {
    return this.http.delete<any>(this.apiName + `variant`+'?companyId='+companyId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId+'&levelId='+levelId+'&variantSubId='+subId + '&variantId='+variantCode);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'variant/findVariant', obj);
  }
}


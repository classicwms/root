import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class AltuomService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imalternateuom`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any,alternateUom:any) {
    return this.http.get<any>(this.apiName + `imalternateuom/` + code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode+'&alternateUom='+alternateUom );
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `imalternateuom`, obj);
  }
  Update(obj: any, code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any,alternateUom:any) {
    return this.http.patch<any>(this.apiName + `imalternateuom/` + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode+'&alternateUom='+alternateUom, obj);
  }
  Delete(code: any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any,itemCode:any,alternateUom:any) {
    return this.http.delete<any>(this.apiName + `imalternateuom/` + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemCode='+itemCode+'&alternateUom='+alternateUom);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'imalternateuom/findImAlternateUom', obj);
  }
}
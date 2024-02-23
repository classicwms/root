import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `warehouse`);
  }
  Get(code: string,modeOfImplementation: any,warehouseTypeId: any,companyId:any,plantId:any,languageId:any) {
    return this.http.get<any>(this.apiName + `warehouse/` + code + '?modeOfImplementation=' + modeOfImplementation + '&warehouseTypeId=' + warehouseTypeId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `warehouse`, obj);
  }
  Update(obj: any, code: any,modeOfImplementation: any,warehouseTypeId: any,companyId:any,plantId:any,languageId:any) {
    return this.http.patch<any>(this.apiName + `warehouse/` + code+'?modeOfImplementation=' + modeOfImplementation + '&warehouseTypeId=' + warehouseTypeId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId, obj);
  }
  Delete(warehouseId: any,modeOfImplementation: any,warehouseTypeId: any,companyId:any,plantId:any,languageId:any,) {
    return this.http.delete<any>(this.apiName + `warehouse/` + warehouseId+'?modeOfImplementation=' + modeOfImplementation + '&warehouseTypeId=' + warehouseTypeId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'warehouse/findWarehouse', obj);
  }
}

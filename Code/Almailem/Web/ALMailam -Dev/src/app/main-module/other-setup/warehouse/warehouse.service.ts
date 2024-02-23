import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  Get(code: string,plantId:any,companyCodeId:any,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/warehouseid/' + code+'?plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/warehouseid', obj);
  }
  Update(obj: any, code: any,plantId:any,companyCodeId:any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/warehouseid/' + code+'?plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId, obj);
  }
  Delete(warehouseId: any,plantId:any,companyCodeId:any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/warehouseid/' + warehouseId+ '?plantId='+plantId+'&companyCodeId='+companyCodeId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/warehouseid/findWarehouseId', obj);
  }
}


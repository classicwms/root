import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class FloorService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `floor`);
  }
  Get(code:string,warehouseId: any,companyId:any,plantId:any,languageId:any) {
    return this.http.get<any>(this.apiName + `floor/` + code+ '?warehouseId=' + warehouseId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `floor`, obj);
  }
  Update(obj: any, code: any,warehouseId: any,companyId:any,plantId:any,languageId:any) {
    return this.http.patch<any>(this.apiName + `floor/` + code + '?warehouseId=' + warehouseId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId, obj);
  }
  Delete(floor: any, warehouseId: any,companyId:any,plantId:any,languageId:any) {
    return this.http.delete<any>(this.apiName + `floor/` + floor + '?warehouseId=' + warehouseId+'&companyId='+companyId+'&plantId='+plantId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'floor/findFloor', obj);
  }
}

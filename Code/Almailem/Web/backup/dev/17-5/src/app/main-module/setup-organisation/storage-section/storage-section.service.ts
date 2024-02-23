import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StorageSectionService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `storagesection`);
  }
  Get(code: string, warehouseId: any, floorId: any,plantId:any,companyId:any,languageId:any) {
    return this.http.get<any>(this.apiName + `storagesection/` + code + '?warehouseId=' + warehouseId + '&floorId=' + floorId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `storagesection`, obj);
  }
  Update(obj: any, code: any,warehouseId: any, floorId: any,plantId:any,companyId:any,languageId:any) {
    return this.http.patch<any>(this.apiName + `storagesection/` + code + '?warehouseId=' + warehouseId + '&floorId=' + floorId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId, obj);
  }
  Delete(storagesectionId: any, warehouseId: any, floorId: any,plantId:any,companyId:any,languageId:any) {
    return this.http.delete<any>(this.apiName + `storagesection/` + storagesectionId + '?warehouseId=' + warehouseId + '&floorId=' + floorId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'storagesection/findStorageSection', obj);
  }
}

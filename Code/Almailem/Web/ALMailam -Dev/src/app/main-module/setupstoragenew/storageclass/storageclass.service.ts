import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StorageclassService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `storageclass`);
  }
  Get(code: string,warehouseId:any,plantId:any,companyId:any,languageId:any) {
    return this.http.get<any>(this.apiName + `storageclass/`+ code+'?warehouseId='+warehouseId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `storageclass`, obj);
  }
  Update(obj: any,code:any,warehouseId:any,plantId:any,companyId:any,languageId:any) {
    return this.http.patch<any>(this.apiName + `storageclass/`+code+'?warehouseId='+ warehouseId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId , obj);
  }
  Delete(storageClassId: any,warehouseId:any,plantId:any,companyId:any,languageId:any) {
    return this.http.delete<any>(this.apiName + `storageclass/`+storageClassId+'?warehouseId='+warehouseId+'&plantId='+plantId+'&companyId='+companyId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'storageclass/findStorageClass', obj);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StoragebintypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `storagebintype`);
  }
  Get(code: string,warehouseId:any,storageTypeId:any,plantId:any,languageId:any,companyId:any,storageClassId:any,) {
    return this.http.get<any>(this.apiName + `storagebintype/`+ code+'?warehouseId='+warehouseId+'&storageTypeId='+storageTypeId+'&plantId='+plantId+'&languageId='+languageId+'&companyId='+companyId+'&storageClassId='+storageClassId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `storagebintype`, obj);
  }
  Update(obj: any,code:any,warehouseId:any,storageTypeId:any,plantId:any,languageId:any,companyId:any,storageClassId:any) {
    return this.http.patch<any>(this.apiName + `storagebintype/`+code+'?warehouseId='+warehouseId+'&storageTypeId='+storageTypeId+'&plantId='+plantId+'&languageId='+languageId+'&companyId='+companyId+'&storageClassId='+storageClassId , obj);
  }
  Delete(storageBinTypeId: any,warehouseId:any,storageTypeId:any,plantId:any,languageId:any,companyId:any,storageClassId:any) {
    return this.http.delete<any>(this.apiName + `storagebintype/`+storageBinTypeId+'?warehouseId='+warehouseId+'&storageTypeId='+storageTypeId+'&plantId='+plantId+'&languageId='+languageId+'&companyId='+companyId+'&storageClassId='+storageClassId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'storagebintype/findStorageBinType', obj);
  }
}


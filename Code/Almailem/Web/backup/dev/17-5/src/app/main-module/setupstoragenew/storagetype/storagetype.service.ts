import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StoragetypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-enterprise-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `storagetype`);
  }
  Get(code: string,warehouseId:any,storageClassId:any,languageId:any,plantId:any,companyId:any) {
    return this.http.get<any>(this.apiName + `storagetype/`+ code+'?warehouseId='+warehouseId+'&storageClassId='+storageClassId+'&languageId='+languageId+'&plantId='+plantId+'&companyId='+companyId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `storagetype`, obj);
  }
  Update(obj: any,code:any,warehouseId:any,storageClassId:any,languageId:any,plantId:any,companyId:any) {
    return this.http.patch<any>(this.apiName + `storagetype/`+code+'?warehouseId='+ warehouseId+'&storageClassId='+storageClassId+'&languageId='+languageId+'&plantId='+plantId+'&companyId='+companyId , obj);
  }
  Delete(storageTypeId: any,warehouseId:any,storageClassId:any,languageId:any,plantId:any,companyId:any) {
    return this.http.delete<any>(this.apiName + `storagetype/`+storageTypeId+'?warehouseId='+warehouseId+'&storageClassId='+storageClassId+'&languageId='+languageId+'&plantId='+plantId+'&companyId='+companyId);
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'storagetype/findStorageType', obj);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StoragetypeService {

 
  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/storagetypeid');
  }
  Get(code: string,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,storageClassId:any) {
    return this.http.get<any>('/wms-idmaster-service/storagetypeid/' + code + '?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&storageClassId='+storageClassId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagetypeid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,storageClassId:any) {
    return this.http.patch<any>('/wms-idmaster-service/storagetypeid/' + code +'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&storageClassId='+storageClassId, obj);
  }
  Delete(storageTypeId: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,storageClassId:any) {
    return this.http.delete<any>('/wms-idmaster-service/storagetypeid/' + storageTypeId+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&storageClassId='+storageClassId );
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagetypeid/findStorageTypeId', obj);
  }
}






import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StoragebintypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/storagebintypeid');
  }
  Get(code:string,storageTypeId:any,storageClassId:any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/storagebintypeid/' + code+'?storageTypeId='+storageTypeId+'&storageClassId='+storageClassId+'&warehouseId='+warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagebintypeid', obj);
  }
  Update(obj: any, code: any,storageTypeId:any,storageClassId:any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.patch<any>('/wms-idmaster-service/storagebintypeid/' + code+'?storageTypeId='+storageTypeId+'&storageClassId='+storageClassId+'&warehouseId='+warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId, obj);
  }
  Delete(storageBinTypeId: any,storageTypeId:any,storageClassId:any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.delete<any>('/wms-idmaster-service/storagebintypeid/' + storageBinTypeId +'?storageTypeId='+storageTypeId+'&storageClassId='+storageClassId+'&warehouseId='+warehouseId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&languageId='+languageId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagebintypeid/findstoragebintypeid', obj);
  }
}


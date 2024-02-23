import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StorageSectionService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/storagesectionid');
  }
  Get(code: string,companyCodeId:any,warehouseId:any,storageSection:any,languageId:any,floorId:any,plantId:any) {
    return this.http.get<any>('/wms-idmaster-service/storagesectionid/' + code+'?companyCodeId='+ companyCodeId+'&warehouseId='+ warehouseId +'&storageSection='+ storageSection+'&languageId='+languageId +'&floorId='+floorId+'&plantId='+plantId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagesectionid', obj);
  }
  Update(obj: any, code: any,companyCodeId:any,warehouseId:any,storageSection:any,languageId:any,floorId:any,plantId:any) {
    return this.http.patch<any>('/wms-idmaster-service/storagesectionid/' + code+'?companyCodeId='+ companyCodeId+'&warehouseId='+ warehouseId +'&storageSection='+ storageSection+'&languageId='+languageId +'&floorId='+floorId+'&plantId='+plantId, obj);
  }
  Delete(storageSectionId: any,companyCodeId:any,warehouseId:any,storageSection:any,languageId:any,floorId:any,plantId:any) {
    return this.http.delete<any>('/wms-idmaster-service/storagesectionid/' + storageSectionId +'?companyCodeId='+ companyCodeId+'&warehouseId='+ warehouseId +'&storageSection='+ storageSection+'&languageId='+languageId +'&floorId='+floorId+'&plantId='+plantId);
  }
  search(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/storagesectionid/findstoragesectionid', obj);
  }
}

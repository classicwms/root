import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class RowService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/rowid');
  }
  Get(statusID: string,  warehouseId: string,companyCodeId:any,plantId:any,languageId:any,floorId:any,storageSectionId:any) {
    return this.http.get<any>('/wms-idmaster-service/rowid/' + statusID + '?warehouseId=' + warehouseId +'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId='+languageId+'&floorId='+floorId+'&storageSectionId='+storageSectionId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/rowid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,floorId:any,storageSectionId:any) {
    return this.http.patch<any>('/wms-idmaster-service/rowid/' + code+'?warehouseId=' + warehouseId +'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId='+languageId+'&floorId='+floorId+'&storageSectionId='+storageSectionId, obj);
  }
  Delete(rowId: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,floorId:any,storageSectionId:any) {
    return this.http.delete<any>('/wms-idmaster-service/rowid/' + rowId+'?warehouseId=' + warehouseId +'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId='+languageId+'&floorId='+floorId+'&storageSectionId='+storageSectionId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/rowid/findRowId', obj);
}
}

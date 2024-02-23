import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ShelfidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/shelfid');
  }
  Get(statusID: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,spanId:any,floorId:any) {
    return this.http.get<any>('/wms-idmaster-service/shelfid/' + statusID +'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId +'&spanId='+spanId+'&floorId='+floorId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/shelfid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,spanId:any,floorId:any,) {
    return this.http.patch<any>('/wms-idmaster-service/shelfid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId +'&spanId='+spanId+'&floorId='+floorId, obj);
  }
  Delete(shelfId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,spanId:any,floorId:any) {
    return this.http.delete<any>('/wms-idmaster-service/shelfid/' + shelfId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId +'&spanId='+spanId+'&floorId='+floorId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/shelfid/findShelfId', obj);
}

}





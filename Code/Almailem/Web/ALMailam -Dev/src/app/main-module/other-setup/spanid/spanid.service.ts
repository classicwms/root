import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class SpanidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/spanid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,floorId:any,) {
    return this.http.get<any>('/wms-idmaster-service/spanid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId+'&floorId='+floorId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/spanid', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,floorId:any,) {
    return this.http.patch<any>('/wms-idmaster-service/spanid/' + code+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId+'&floorId='+floorId, obj);
  }
  Delete(spanId: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,aisleId:any,rowId:any,storageSectionId:any,floorId:any,) {
    return this.http.delete<any>('/wms-idmaster-service/spanid/' + spanId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+ companyCodeId +'&plantId='+plantId+'&aisleId='+aisleId+'&rowId='+rowId+'&storageSectionId='+storageSectionId+'&floorId='+floorId);
} search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/spanid/findSpanId', obj);
}

}




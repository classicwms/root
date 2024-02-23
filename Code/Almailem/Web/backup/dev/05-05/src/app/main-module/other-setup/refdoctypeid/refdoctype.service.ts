import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class RefdoctypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/refdoctypeid');
  }
  Get(statusID: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.get<any>('/wms-idmaster-service/refdoctypeid/' + statusID + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/refdoctypeid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.patch<any>('/wms-idmaster-service/refdoctypeid/' + code+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId, obj);
  }
  Delete(referenceDocumentTypeId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.delete<any>('/wms-idmaster-service/refdoctypeid/' + referenceDocumentTypeId+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/refdoctypeid/findRefDocTypeId', obj);
}
}




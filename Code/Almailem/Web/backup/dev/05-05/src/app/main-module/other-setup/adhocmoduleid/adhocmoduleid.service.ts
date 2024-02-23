import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class AdhocmoduleidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/adhocmoduleid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.get<any>('/wms-idmaster-service/adhocmoduleid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/adhocmoduleid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.patch<any>('/wms-idmaster-service/adhocmoduleid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId, obj);
  }
  Delete(adhocModuleId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.delete<any>('/wms-idmaster-service/adhocmoduleid/' + adhocModuleId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/adhocmoduleid/findAdhocModuleId', obj);
}
}



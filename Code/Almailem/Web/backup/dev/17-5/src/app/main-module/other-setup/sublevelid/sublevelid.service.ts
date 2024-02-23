import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class SublevelidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/sublevelid');
  }
  Get(code: string, warehouseId: string,companyCodeId:any,plantId:any,languageId:any,levelId:any) {
    return this.http.get<any>('/wms-idmaster-service/sublevelid/' + code +'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&levelId='+levelId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/sublevelid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,levelId:any) {
    return this.http.patch<any>('/wms-idmaster-service/sublevelid/' + code+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&levelId='+levelId, obj);
  }
  Delete(subLevelId: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,levelId:any) {
    return this.http.delete<any>('/wms-idmaster-service/sublevelid/' + subLevelId+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&levelId='+levelId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/sublevelid/findSubLevelId', obj);
}
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class WorkcenterService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/workcenterid');
  }
  Get(code: string,warehouseId: string,companyCodeId:any,plantId:any,languageId:any) {
    return this.http.get<any>('/wms-idmaster-service/workcenterid/' + code + '?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/workcenterid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,) {
    return this.http.patch<any>('/wms-idmaster-service/workcenterid/' + code+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId, obj);
  }
  Delete(workCenterId: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,) {
    return this.http.delete<any>('//wms-idmaster-service/workcenterid' + workCenterId+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/workcenterid/findWorkCenterId', obj);
}
}



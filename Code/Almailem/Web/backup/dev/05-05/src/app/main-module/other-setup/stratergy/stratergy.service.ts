import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StratergyService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/strategyid');
  }
  Get(statusID: string,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,strategyNo:any,strategyTypeId:any) {
    return this.http.get<any>('/wms-idmaster-service/strategyid/' + statusID + '?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&strategyNo='+strategyNo+'&strategyTypeId='+strategyTypeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/strategyid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,strategyNo:any,strategyTypeId:any) {
    return this.http.patch<any>('/wms-idmaster-service/strategyid/' + code+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&strategyNo='+strategyNo+'&strategyTypeId='+strategyTypeId, obj);
  }
  Delete(strategyNo: any,warehouseId: string,companyCodeId:any,plantId:any,languageId:any,strategyTypeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/strategyid/' + strategyNo+'?warehouseId=' + warehouseId+'&companyCodeId=' +companyCodeId +'&plantId=' +plantId+'&languageId=' +languageId+'&strategyNo='+strategyNo+'&strategyTypeId='+strategyTypeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/strategyid/findStrategyId', obj);
}
}


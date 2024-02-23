import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ProcessService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/processsequenceid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,processId:any) {
    return this.http.get<any>('/wms-idmaster-service/processsequenceid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&processId='+processId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/processsequenceid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,processId:any) {
    return this.http.patch<any>('/wms-idmaster-service/processsequenceid/' + code+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&processId='+processId, obj);
  }
  Delete(processId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,) {
    return this.http.delete<any>('/wms-idmaster-service/processsequenceid/' + processId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/processsequenceid/findProcessSequenceId', obj);
}
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/Auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ServiceidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/servicetypeid');
  }
  Get(code: string, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.get<any>('/wms-idmaster-service/servicetypeid/' + code +  '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/servicetypeid', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.patch<any>('/wms-idmaster-service/servicetypeid/'+ code +  '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId, obj);
  }
  Delete(serviceTypeId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,moduleId:any) {
    return this.http.delete<any>('/wms-idmaster-service/servicetypeid/' + serviceTypeId +'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&moduleId='+moduleId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/servicetypeid/findServiceTypeId', obj);
}
}





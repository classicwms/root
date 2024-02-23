import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class HandlingunitService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/handlingunitid');
  }
  Get(code: string,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,handlingUnitNumber:any,) {
    return this.http.get<any>('/wms-idmaster-service/handlingunitid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&handlingUnitNumber='+handlingUnitNumber);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/handlingunitid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,handlingUnitNumber:any,) {
    return this.http.patch<any>('/wms-idmaster-service/handlingunitid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&handlingUnitNumber='+handlingUnitNumber, obj);
  }
  Delete(handlingUnitId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,handlingUnitNumber:any,) {
    return this.http.delete<any>('/wms-idmaster-service/handlingunitid/' + handlingUnitId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&handlingUnitNumber='+handlingUnitNumber);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/handlingunitid/findHandlingUnitId', obj);
}
}



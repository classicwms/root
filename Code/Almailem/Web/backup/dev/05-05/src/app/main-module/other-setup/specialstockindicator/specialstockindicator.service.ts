import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class SpecialstockindicatorService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/specialstockindicatorid');
  }
  Get(code: string,  warehouseId: string,languageId:any,companyCodeId:any,plantId:any,stockTypeId:any) {
    return this.http.get<any>('/wms-idmaster-service/specialstockindicatorid/' + code +'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&stockTypeId='+stockTypeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/specialstockindicatorid', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,stockTypeId:any) {
    return this.http.patch<any>('/wms-idmaster-service/specialstockindicatorid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&stockTypeId='+stockTypeId, obj);
  }
  Delete(specialStockIndicatorId: any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any,stockTypeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/specialstockindicatorid/' + specialStockIndicatorId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId +'&stockTypeId='+stockTypeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/specialstockindicatorid/findSpecialStockIndicatorId', obj);
}
}





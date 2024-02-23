import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class NumberrangeService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/numberrange');
  }
  Get(code: string,  warehouseId: string,languageId:any,plantId:any,companyCodeId:any,fiscalYear:any) {
    return this.http.get<any>('/wms-idmaster-service/numberrange/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&fiscalYear='+fiscalYear);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/numberrange', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,plantId:any,companyCodeId:any,fiscalYear:any) {
    return this.http.patch<any>('/wms-idmaster-service/numberrange/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&fiscalYear='+fiscalYear , obj);
  }
  Delete(numberRangeCode: any ,warehouseId: string,languageId:any,plantId:any,companyCodeId:any,fiscalYear:any) {
    return this.http.delete<any>('/wms-idmaster-service/numberrange/' + numberRangeCode+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&fiscalYear='+fiscalYear);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/numberrange/findNumberRange', obj);
}
}


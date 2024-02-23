import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class VariantidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/variantid');
  }
  Get(code: string,variantSubCode:any,variantType:any, warehouseId: string,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.get<any>('/wms-idmaster-service/variantid/' + code +'?variantSubCode='+variantSubCode+'&variantType='+variantType+'&warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId );
  
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/variantid', obj);
  }
  Update(obj: any, code: any,variantSubCode:any,variantType:any,warehouseId:any,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.patch<any>('/wms-idmaster-service/variantid/' + code+'?variantSubCode='+variantSubCode+'&variantType='+variantType+'&warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId, obj);
  }
  Delete(variantCode: any,variantSubCode:any,variantType:any,warehouseId:any,languageId:any,companyCodeId:any,plantId:any) {
    return this.http.delete<any>('/wms-idmaster-service/variantid/' + variantCode+'?variantSubCode='+variantSubCode +'&variantType='+variantType+'&warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/variantid/findvariantid', obj);
}
}





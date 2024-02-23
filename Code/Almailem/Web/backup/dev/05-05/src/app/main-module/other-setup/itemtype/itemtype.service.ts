import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ItemtypeService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/itemtypeid');
  }
  Get(code: string,  warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>('/wms-idmaster-service/itemtypeid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/itemtypeid', obj);
  }
  Update(obj: any, code: any, warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>('/wms-idmaster-service/itemtypeid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId , obj);
  }
  Delete(itemTypeId: any ,warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/itemtypeid/' + itemTypeId+ '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/itemtypeid/findItemTypeId', obj);
}
}


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class ItemgroupService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/itemgroupid');
  }
  Get(code: string, warehouseId: string,languageId:any,plantId:any,companyCodeId:any,itemTypeId:any) {
    return this.http.get<any>('/wms-idmaster-service/itemgroupid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemTypeId='+itemTypeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/itemgroupid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any,itemTypeId:any) {
    return this.http.patch<any>('/wms-idmaster-service/itemgroupid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemTypeId='+itemTypeId , obj);
  }
  Delete(itemGroupId: any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any,itemTypeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/itemgroupid/' + itemGroupId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId+'&itemTypeId='+itemTypeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/itemgroupid/findItemGroupId', obj);
}
}


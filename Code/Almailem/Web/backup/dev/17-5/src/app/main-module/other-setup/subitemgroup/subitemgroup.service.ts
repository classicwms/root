import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class SubitemgroupService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/subitemgroupid');
  }
  Get(code: string,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,itemGroupId:any,itemTypeId:any,subItemGroup:any) {
    return this.http.get<any>('/wms-idmaster-service/subitemgroupid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&itemGroupId='+itemGroupId+'&itemTypeId='+itemTypeId+'&subItemGroup='+subItemGroup);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/subitemgroupid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,itemGroupId:any,itemTypeId:any,subItemGroup:any) {
    return this.http.patch<any>('/wms-idmaster-service/subitemgroupid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&itemGroupId='+itemGroupId+'&itemTypeId='+itemTypeId+'&subItemGroup='+subItemGroup, obj);
  }
  Delete(subitemgroupId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,itemGroupId:any,itemTypeId:any,subItemGroup:any,) {
    return this.http.delete<any>('/wms-idmaster-service/subitemgroupid/' + subitemgroupId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&itemGroupId='+itemGroupId+'&itemTypeId='+itemTypeId+'&subItemGroup='+subItemGroup);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/subitemgroupid/findSubItemGroupId', obj);
}
}


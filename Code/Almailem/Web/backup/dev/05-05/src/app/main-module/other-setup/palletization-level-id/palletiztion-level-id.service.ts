import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PalletiztionLevelIdService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/palletizationlevelid');
  }
  Get(statusID: string,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,palletizationLevel:any) {
    return this.http.get<any>('/wms-idmaster-service/palletizationlevelid/' + statusID + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&palletizationLevel='+palletizationLevel);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/palletizationlevelid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,palletizationLevel:any) {
    return this.http.patch<any>('/wms-idmaster-service/palletizationlevelid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&palletizationLevel='+palletizationLevel, obj);
  }
  Delete(palletizationLevelId: any,warehouseId:any,palletizationLevel:any) {
    return this.http.delete<any>('/wms-idmaster-service/palletizationlevelid/' + palletizationLevelId+'?warehouseId='+warehouseId+'&palletizationLevel='+palletizationLevel);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/palletizationlevelid/findPalletizationLevelId', obj);
}
}




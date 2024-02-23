import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class DockidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/dockid');
  }
  Get(code: string,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,dockDescription:any) {
    return this.http.get<any>('/wms-idmaster-service/dockid/' + code + '?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&dockDescription='+dockDescription);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/dockid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,dockDescription:any) {
    return this.http.patch<any>('/wms-idmaster-service/dockid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&dockDescription='+dockDescription, obj);
  }
  Delete(dockId: any,warehouseId: string,languageId:any,companyCodeId:any,plantId:any,dockDescription:any) {
    return this.http.delete<any>('/wms-idmaster-service/dockid/' + dockId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&companyCodeId='+companyCodeId+'&plantId='+plantId+'&dockDescription='+dockDescription);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/dockid/findDockId', obj);
}
}



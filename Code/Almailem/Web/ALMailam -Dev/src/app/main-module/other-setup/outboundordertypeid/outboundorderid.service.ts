import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class OutboundorderidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/outboundordertypeid');
  }
  Get(code: string,  warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>('/wms-idmaster-service/outboundordertypeid/' + code +'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/outboundordertypeid', obj);
  }
  Update(obj: any, code: any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.patch<any>('/wms-idmaster-service/outboundordertypeid/' + code+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId, obj);
  }
  Delete(outboundOrderTypeId: any,warehouseId: string,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.delete<any>('/wms-idmaster-service/outboundordertypeid/' + outboundOrderTypeId+'?warehouseId=' + warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/outboundordertypeid/findOutboundOrderTypeId', obj);
}
}




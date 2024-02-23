import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PartnerService {
  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `impartner`);
  }
  Get(code: string,warehouseId:any,languageId:any,plantId:any,companyCodeId:any) {
    return this.http.get<any>(this.apiName + `impartner/`+ code+'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.apiName + `impartner`, obj);
  }
  Update(obj: any,code:any,warehouseId:any,languageId:any,plantId:any,companyCodeId:any, manufacturerName: any) {
    return this.http.patch<any>(this.apiName + `impartner/`+code +'?warehouseId='+warehouseId+'&languageId='+languageId+'&plantId='+plantId+'&companyCodeId='+companyCodeId + '&manufacturerName=' + manufacturerName, obj);
  }
  Delete(code:any,warehouseId:any,plantId:any,languageId:any,companyCodeId:any, manufacturerName: any,partnerItemBarcode:any) {
    return this.http.delete<any>(this.apiName + `impartner/`+ code +'?warehouseId='+warehouseId+'&plantId='+plantId+ '&languageId='+languageId+'&companyCodeId='+companyCodeId + '&manufacturerName=' + manufacturerName+'&partnerItemBarcode='+partnerItemBarcode,);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  Getpartnercode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }
  search(obj: any) {
    return this.http.post<any>(this.apiName + 'impartner/findImPartner', obj);
  }
}

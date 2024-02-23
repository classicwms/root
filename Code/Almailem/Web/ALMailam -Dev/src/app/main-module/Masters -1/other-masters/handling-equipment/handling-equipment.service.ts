import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

export interface HandlingquipmentElement{
  acquistionDate: Date;
 acquistionValue: number;
  category: string;
  companyCodeId: string;
  countryOfOrigin: string;
  createdby: string;
  createdon: Date;
  currencyId: number;
  deletionIndicator: number;
  handlingEquipmentId: number;
  handlingUnit: string;
 heBarcode: string;
  languageId: string;
 manufacturerPartNo: string;
 modelNo: string;
 plantId: string;
  referenceField1: string;
  referenceField10: string;
  referenceField2: string;
  referenceField3: string;
  referenceField4: string;
  referenceField5: string;
  referenceField6: string;
  referenceField7: string;
  referenceField8: string;
  referenceField9: string;
  statusId: number;
  updatedby: string;
  updatedon: Date;
  warehouseId: string;
  
}

@Injectable({
  providedIn: 'root'
})
export class HandlingEquipmentService {

 
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'handlingequipment';
  url = this.apiName + this.methodName;
  Getall() {
    return this.http.get<any>(this.apiName + `handlingequipment`);
  }
  Get(code: string,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.get<any>(this.apiName + `handlingequipment/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId);
  }
  Create(obj: HandlingquipmentElement) {
    return this.http.post<any>(this.apiName + `handlingequipment`, obj);
  }
  Update( obj:any,code:any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.patch<any>(this.apiName + `handlingequipment/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId, obj);
  }
  Delete(handlingEquipmentId: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.delete<any>(this.apiName + `handlingequipment/` + '?handlingEquipmentId=' + handlingEquipmentId +'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId );
  }
  Gethandlingunit() {
    return this.http.get<any>('/wms-masters-service/handlingunit');
  }
 
  search(obj: any) {
    return this.http.post<any>(this.url + '/findHandlingEquipment', obj);
  }

}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

export interface HandlingUnitElement{
  companyCodeId: string;
  createdby: string;
  createdon: Date;
  deletionIndicator: number;
  handlingUnit: string;
  height: number;
  heightUom: string;
  languageId: string;
  length: number;
  lengthUom: string;
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
  volume: number;
  volumeUom: string;
  warehouseId: string;
  weight: number;
  weightUom: string;
  width: number;
  widthUom: string;
}
@Injectable({
  providedIn: 'root'
})
export class HandlingUnitService {

  constructor(private http: HttpClient, private auth: AuthService) { }


  apiName = '/wms-masters-service/';
  methodName = 'handlingunit';
  url = this.apiName + this.methodName;
  Getall() {
    return this.http.get<any>(this.apiName + `handlingunit`);
  }
  Get(code: string,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.get<any>(this.apiName + `handlingunit/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId);
  }
  Create(obj: HandlingUnitElement) {
    return this.http.post<any>(this.apiName + `handlingunit`, obj);
  }
  Update(obj: HandlingUnitElement, code: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.patch<any>(this.apiName + `handlingunit/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId, obj);
  }
  Delete(handlingUnit: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.delete<any>(this.apiName + `handlingunit/` + '?handlingUnit=' + handlingUnit +'&companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId );
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findHandlingUnit', obj);
  }
 
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';


export interface PackingmaterialElement {
  businessPartnerCode: string;
  companyCodeId: string;
  createdby: string;
  createdon: Date;
  deletionIndicator: number;
  description: string;
  dimensionUom: string;
  height: number;
  languageId: string;
  length: number;
  packingMaterialNo: string;
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
  totalStock: number;
  updatedby: string;
  updatedon: Date;
  volume: number;
  volumeUom: string;
  warehouseId: string;
  width: number;
}



@Injectable({
  providedIn: 'root'
})
export class PackingMaterialService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'packingmaterial';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `packingmaterial`);
  }
  Get(code: string,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.get<any>(this.apiName + `packingmaterial/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId);
  }
  Create(obj: PackingmaterialElement) {
    return this.http.post<any>(this.apiName + `packingmaterial`, obj);
  }
  Update(obj: PackingmaterialElement, code: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.patch<any>(this.apiName + `packingmaterial/` + code+'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId, obj);
  }
  Delete(code: any,companyCodeId:any,languageId:any,plantId:any,warehouseId:any) {
    return this.http.delete<any>(this.apiName + `packingmaterial/` + code +'?companyCodeId='+companyCodeId+'&languageId='+languageId+'&plantId='+plantId+'&warehouseId='+warehouseId );
  }

  Getpartnercode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }

  search(obj: any) {
    return this.http.post<any>(this.url + '/findPackingMaterial', obj);
  }


}

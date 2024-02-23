import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';


export interface Altuomelement {
  alternateUom: string;
  alternateUomBarcode: string;
  companyCodeId: string;
  createdby: string;
  createdon: Date;
  deletionIndicator: number;
  fromUnit: number;
  itemCode: string;
  languageId: string;
  plantId: string;
  qpc20Ft: number;
  qpc40Ft: number;
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
  slNo: number;
  statusId: number;
  toUnit: number;
  uomId: string;
  updatedby: string;
  updatedon: Date;
  warehouseId: string;
}


@Injectable({
  providedIn: 'root'
})
export class AltuomService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imalternateuom`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `imalternateuom/` + code);
  }
  Create(obj: Altuomelement) {
    return this.http.post<any>(this.apiName + `imalternateuom`, obj);
  }
  Update(obj: Altuomelement, code: any) {
    return this.http.patch<any>(this.apiName + `imalternateuom/` + code, obj);
  }
  Delete(code: any) {
    return this.http.delete<any>(this.apiName + `imalternateuom/` + code);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
}
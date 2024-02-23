import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';
import { AuthService } from 'src/app/core/core';


export interface basicdataelement {
  companyCodeId: string;
  createdby: string;
  createdon: Date;
  deletionIndicator: number;
  description: string;
  eanUpcNo: string;
  hsnCode: string;
  itemCode: string;
  itemGroup: number;
  itemType: number;
  languageId: string;
  manufacturerPartNo: string;
  maximumStock: number;
  minimumStock: number;
  model: string;
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
  reorderLevel: number;
  replenishmentQty: number;
  safetyStock: number;
  specifications1: string;
  specifications2: string;
  statusId: number;
  storageSectionId: number;
  subItemGroup: number;
  totalStock: number;
  uomId: string;
  updatedby: string;
  updatedon: Date;
  warehouseId: string;
}

@Injectable({
  providedIn: 'root'
})
export class Basicdata1Service {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `imbasicdata1`);
  }
  Get(code: string, warehouse: string) {
    return this.http.get<any>(this.apiName + `imbasicdata1/` + code + `?warehouseId=` + warehouse);
  }
  Create(obj: basicdataelement) {
    return this.http.post<any>(this.apiName + `imbasicdata1`, obj);
  }
  Update(obj: basicdataelement, code: any) {
    console.log(code);
    return this.http.patch<any>(this.apiName + `imbasicdata1/` + code + `?warehouseId=` + obj.warehouseId, obj);
  }
  Delete(code: any) {
    return this.http.delete<any>(this.apiName + `imbasicdata1/` + code);
  }

  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }

  Getitemgroup() {
    return this.http.get<any>('/wms-idmaster-service/itemgroupid');
  }
  Getitemtype() {
    return this.http.get<any>('/wms-idmaster-service/itemtypeid');
  }
  Getstorage() {
    return this.http.get<any>('/wms-idmaster-service/storagesectionid');
  }
}
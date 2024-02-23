import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';



export interface SelectionElement {
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
export class MasterproductselectionService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'imbasicdata1';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `imbasicdata1`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `imbasicdata1/` + code);
  }
  Create(obj: SelectionElement) {
    return this.http.post<any>(this.apiName + `imbasicdata1`, obj);
  }
  Update(obj: SelectionElement, code: any) {
    return this.http.patch<any>(this.apiName + `imbasicdata1/` + code, obj);
  }
  Delete(itemCode: any) {
    return this.http.delete<any>(this.apiName + `imbasicdata1/` + '?itemCode=' + itemCode);
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
  Getsubitem() {
    return this.http.get<any>('/wms-idmaster-service/subitemgroupid');
  }

  search(obj: any) {
    return this.http.post<any>(this.url + `/findImBasicData1`, obj);
  }

  getAllByPagination(pageNo, pageSize, obj) {
    return this.http.post<any>(this.apiName + `imbasicdata1/findImBasicData1/pagination?pageNo=${pageNo}&pageSize=${pageSize}`, obj);
  }
}

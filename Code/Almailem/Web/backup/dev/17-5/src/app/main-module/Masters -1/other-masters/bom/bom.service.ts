import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';


export interface BOMElement {
  bomNumber: number;
  companyCode: string;
  createdOn: Date;
  createdby: string;
  deletionIndicator: number;
  languageId: string;
  parentItemCode: string;
  childItemCode: string;
  childItemQuantity: string;
  parentItemQuantity: number;
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
  updatedOn: Date;
  updatedby: string;
  warehouseId: string;
}


@Injectable({
  providedIn: 'root'
})
export class BOMService {


  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'bomheader';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `bomheader`);
  }
  Get(parentItemCode: any, warehouseId: any) {
    return this.http.get<any>(this.apiName + `bomheader/` + parentItemCode + '?warehouseId=' + warehouseId);
  }
  Create(obj: BOMElement) {
    return this.http.post<any>(this.apiName + `bomheader`, obj);
  }
  Update(obj: BOMElement, parentItemCode: any, warehouseId: any) {
    return this.http.patch<any>(this.apiName + `bomheader/` + '?parentItemCode=' + parentItemCode + '&warehouseId=' + warehouseId, obj);
  }
  Delete(parentItemCode: any, warehouseId: any) {
    return this.http.delete<any>(this.apiName + `bomheader/` + parentItemCode + '?warehouseId=' + warehouseId);
  }

  Getitemcode(obj: any = { warehouseId: [this.auth.warehouseId] }) {
    return this.http.post<any>('/wms-masters-service/imbasicdata1/findImBasicData1', obj);
  }


  search(obj: any) {
    return this.http.post<any>(this.url + '/findBomHeader', obj);
  }
}

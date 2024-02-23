import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';


export interface PartnerElement {
  brandName: string;
  businessParnterType: string;
  businessPartnerCode: string;
  companyCodeId: string;
  createdby: string;
  createdon: Date;
  deletionIndicator: number;
  itemCode: string;
  languageId: string;
  mfrBarcode: string;
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
  stock: number;
  stockUom: string;
  updatedby: string;
  updatedon: Date;
  vendorItemBarcode: string;
  vendorItemNo: string;
  warehouseId: string;
}

@Injectable({
  providedIn: 'root'
})
export class PartnerService {
  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  Getall() {
    return this.http.get<any>(this.apiName + `impartner`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `impartner/` + code);
  }
  Create(obj: PartnerElement) {
    return this.http.post<any>(this.apiName + `impartner`, obj);
  }
  Update(obj: PartnerElement, code: any) {
    return this.http.patch<any>(this.apiName + `impartner/` + code, obj);
  }
  Delete(code: any) {
    return this.http.delete<any>(this.apiName + `impartner/` + code);
  }
  GetWh() {
    return this.http.get<any>('/wms-idmaster-service/warehouseid');
  }
  Getpartnercode() {
    return this.http.get<any>('/wms-masters-service/businesspartner');
  }
}

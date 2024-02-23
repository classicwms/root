import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

export interface StorageselectionElement{
  aisleNumber: string;
  binBarcode: string;
  binClassId: number;
  binSectionId: number;
  blockReason: string;
  companyCodeId: string;
  createdBy: string;
  createdOn: Date;
  deletionIndicator: number;
  description: string;
  floorId: number;
  languageId: string;
  pickingBlock: number;
  plantId: string;
  putawayBlock: number;
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
  rowId: string;
  shelfId: string;
  spanId: string;
  statusId: number;
  storageBin: string;
  storageSectionId: string;
  storageTypeId: number;
  updatedBy: string;
  updatedOn: Date;
  warehouseId: string;
}

@Injectable({
  providedIn: 'root'
})
export class MasterstorageselectionService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-masters-service/';
  methodName = 'storagebin';
  url = this.apiName + this.methodName;

  Getall() {
    return this.http.get<any>(this.apiName + `storagebin`);
  }
  Get(code: string) {
    return this.http.get<any>(this.apiName + `storagebin/` + code);
  }
  Create(obj: StorageselectionElement) {
    return this.http.post<any>(this.apiName + `storagebin`, obj);
  }
  Update(obj: StorageselectionElement, code: any) {
    return this.http.patch<any>(this.apiName + `storagebin/` + code, obj);
  }
  Delete(storageTypeId: any) {
    return this.http.delete<any>(this.apiName + `storagebin/` + '?storageTypeId=' + storageTypeId  );
  }


  search(obj: any) {
    return this.http.post<any>(this.url + '/findStorageBin', obj);
  }

 
}

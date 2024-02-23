import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface stockElement {

  allocatedQuantity: number;
        batchSerialNumber: string;
        binClassId: number;
        caseCode: string;
        companyCodeId: string;
        createdBy: string;
        createdOn: Date;
        deletionIndicator: number;
        description: string;
        expiryDate: Date;
        inventoryQuantity: number;
        inventoryUom: string;
        itemCode: string;
        languageId: string;
        manufacturerDate: Date;
        packBarcodes: string;
        palletCode: string;
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
        referenceOrderNo: string;
        specialStockIndicatorId: number;
        total: number;
        stockTypeId: number;
        stockTypeId1: number;
        storageBin: string;
        storageMethod: string;
        variantCode: number;
        variantSubCode: string;
        warehouseId: string;
}

@Injectable({
  providedIn: 'root'
})
export class StocksampleService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  method = 'inventory'
  url = this.apiName + this.method;
  Getall() {
    return this.http.get<any>(this.url);
  }
  search(obj: any) {
    return this.http.post<any>(this.url + '/findInventory', obj);
  }
  searchByPagination(obj: any,pageNumber,pageSize) {
    return this.http.post<any>(this.url + `/findInventory/pagination?pageNo=${pageNumber}&pageSize=${pageSize}`, obj);
  }
}
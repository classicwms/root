import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class InhouseTransferService {


  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-transaction-service/';
  methodName = 'inhousetransferheader';
  url = this.apiName + this.methodName;
  GetAll() {
    return this.http.get<any>(this.url);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url, obj);
  }
  confirm(warehouseId: any, preInboundNo: any, refDocNumber: any) {
    return this.http.patch<any>(this.url + '/confirmIndividual?preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId, {});
  }
  Get(warehouseId: any, preInboundNo: any, refDocNumber: any) {
    return this.http.get<any>(this.url + '/' + refDocNumber + '?preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId, {});
  }

  GetAllSKU(obj: any = { warehouseId: [this.auth.warehouseId] }) {
    return this.http.post<any>('/wms-masters-service/imbasicdata1/findImBasicData1', obj);
  }


  GetAllBin(obj: any = { warehouseId: [this.auth.warehouseId] }) {
    return this.http.post<any>('/wms-masters-service/storagebin/findStorageBin', obj);
  }

  GetInventory(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventory/findInventory', obj);
  }


  inventoryUpdate(stockTypeId: any, itemCode: any, packBarcodes: any, specialStockIndicatorId: any, storageBin: any, warehouseId: any, obj: any) {
    return this.http.patch<any>('/wms-transaction-service/inventory/' + stockTypeId + '?itemCode=' + itemCode + '&packBarcodes=' + packBarcodes + '&specialStockIndicatorId=' + specialStockIndicatorId + '&storageBin=' + storageBin + '&warehouseId=' + warehouseId, obj);
  }

  createInventoryMovement(obj: any) {
    return this.http.post<any>('/wms-transaction-service/inventorymovement', obj);
  }


}

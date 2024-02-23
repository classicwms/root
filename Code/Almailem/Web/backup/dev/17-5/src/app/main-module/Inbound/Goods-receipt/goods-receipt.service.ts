import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GoodsReceiptService {

  constructor(private http: HttpClient) { }

  apiName = '/wms-transaction-service/';
  methodName = 'stagingheader';
  url = this.apiName + this.methodName;
  urlline = this.apiName + 'stagingline';

  Get(stagingNo: any, companyCodeId: any, languageId: any, plantId: any, preInboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.get<any>(this.url + '/' + stagingNo + '?companyCodeId=' + companyCodeId + '&languageId=' + languageId + '&plantId=' + plantId + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url, obj);
  }
  Update(obj: any, containerReceiptNo: any, preInboundNo: any, refDocNumber: any) {
    return this.http.patch<any>(this.url + '/' + containerReceiptNo + '?preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber, obj);
  }

  search(obj: any) {
    return this.http.post<any>(this.url + '/findStagingHeader', obj);
  }
  searchLine(obj: any) {
    return this.http.post<any>(this.urlline + '/findStagingLine', obj);
  }

  GetBarcode(numberOfCases: any, warehouseId: any) {
    return this.http.get<any>(this.url + '/' + numberOfCases + '/barcode?warehouseId=' + warehouseId);
  }
  CreateLine(obj: any) {
    return this.http.post<any>(this.urlline, obj);
  }
  deleteLine(lineNo: any, caseCode: any, itemCode: any, preInboundNo: any, stagingNo: any) {
    return this.http.delete<any>(this.urlline + '/' + lineNo + '/cases?stagingNo=' + stagingNo + '&caseCode=' + caseCode + '&itemCode=' + itemCode + '&preInboundNo=' + preInboundNo);
  }
  confrimLine(obj: any, lineNo: any, caseCode: any, itemCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any) {
    return this.http.get<any>(this.urlline + '/' + lineNo + '/caseConfirmation?caseCode=' + caseCode + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&itemCode=' + itemCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId);
  }
  updateLine(obj: any, lineNo: any, caseCode: any, itemCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any) {
    return this.http.patch<any>(this.urlline + '/' + lineNo + '?caseCode=' + caseCode + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&itemCode=' + itemCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId, obj);
  }
  caseConfirmation(obj: any, caseCode: any) {
    return this.http.patch<any>(this.urlline + '/caseConfirmation?caseCode=' + caseCode, obj);
  }
  assignHHTUser(obj: any, assignedUserId: any) {
    return this.http.patch<any>(this.urlline + '/assignHHTUser?assignedUserId=' + assignedUserId, obj);
  }

}

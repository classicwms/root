import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class GoodsReceiptService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  apiName = '/wms-transaction-service/';
  methodName = 'stagingheader';
  url = this.apiName + this.methodName;
  urlline = this.apiName + 'stagingline';

  Get(stagingNo: any, companyCodeId: any, languageId: any, plantId: any, preInboundNo: any, refDocNumber: any, warehouseId: any) {
    return this.http.get<any>(this.url + '/' + stagingNo + '/v2' + '?companyCodeId=' + companyCodeId + '&languageId=' + languageId + '&plantId=' + plantId + '&preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber + '&warehouseId=' + warehouseId);
  }
  Create(obj: any) {
    return this.http.post<any>(this.url + '/v2', obj);
  }
  Update(obj: any, containerReceiptNo: any, preInboundNo: any, refDocNumber: any) {
    return this.http.patch<any>(this.url + '/' + containerReceiptNo  + '/v2' + '?preInboundNo=' + preInboundNo + '&refDocNumber=' + refDocNumber, obj);
  }

  search(obj: any) {
    return this.http.post<any>(this.url + '/findStagingHeader/v2', obj);
  }
  searchGrheader(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/grHeader', obj);
  }
  searchLine(obj: any) {
    return this.http.post<any>(this.urlline + '/findStagingLine/v2', obj);
  }
  searchLineSpark(obj: any) {
    return this.http.post<any>( '/mnr-spark-service/stagingline', obj);
  }

  GetBarcode(numberOfCases: any, warehouseId: any) {
    return this.http.get<any>(this.url + '/' + numberOfCases + '/barcode?warehouseId=' + warehouseId);
  }
  CreateLine(obj: any) {
    return this.http.post<any>(this.urlline + '/v2', obj);
  }
  deleteLine(lineNo: any, caseCode: any, itemCode: any, preInboundNo: any, stagingNo: any) {
    return this.http.delete<any>(this.urlline + '/' + lineNo + '/v2' + '/cases?stagingNo=' + stagingNo + '&caseCode=' + caseCode + '&itemCode=' + itemCode + '&preInboundNo=' + preInboundNo);
  }
  confrimLine(obj: any, lineNo: any, caseCode: any, itemCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any) {
    return this.http.get<any>(this.urlline + '/' + lineNo + '/caseConfirmation?caseCode=' + caseCode + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&itemCode=' + itemCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId);
  }
  updateLine(obj: any, lineNo: any, caseCode: any, itemCode: any, palletCode: any, preInboundNo: any, refDocNumber: any, stagingNo: any, warehouseId: any) {
    return this.http.patch<any>(this.urlline + '/' + lineNo + '?caseCode=' + caseCode + '&refDocNumber=' + refDocNumber + '&stagingNo=' + stagingNo + '&itemCode=' + itemCode + '&palletCode=' + palletCode + '&preInboundNo=' + preInboundNo + '&warehouseId=' + warehouseId, obj);
  }
  updateLine1(obj: any) {
    return this.http.patch<any>(this.urlline + '/' + obj.lineNo  + '/v2'  + '?caseCode=' + obj.caseCode + '&refDocNumber=' + obj.refDocNumber + '&stagingNo=' + obj.stagingNo + '&itemCode=' + obj.itemCode + '&palletCode=' + obj.palletCode + '&preInboundNo=' + obj.preInboundNo + '&warehouseId=' + obj.warehouseId  + '&companyCode=' + this.auth.companyId + '&languageId=' + this.auth.languageId + '&plantId=' + this.auth.plantId, obj);
  }
  caseConfirmation(obj: any, caseCode: any) {
    return this.http.patch<any>(this.urlline + '/caseConfirmation/v2?caseCode=' + caseCode, obj);
  }
  assignHHTUser(obj: any, assignedUserId: any) {
    return this.http.patch<any>(this.urlline + '/assignHHTUser/v2?assignedUserId=' + assignedUserId, obj);
  }

}

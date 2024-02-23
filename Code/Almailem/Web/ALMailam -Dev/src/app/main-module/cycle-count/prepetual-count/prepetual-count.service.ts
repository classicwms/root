import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PrepetualCountService {

  constructor(private apiService: ApiService, private configService: ConfigService, private auth: AuthService) { }

  runPrepetual(data: any) {
    return this.apiService.post(this.configService.prepetualRun_url, data);
  }
  runPrepetualNew(data: any) {
    return this.apiService.post(this.configService.prepetualRun_url_new, data); //20-06-23 streaming
  }

  getPerpetualCountList(obj: any = { warehouseId: [this.auth.warehouseId] }) {
    return this.apiService.post(this.configService.prepetualCountList_url, obj);
  }
  getPerpetualCountList1(obj: any ) {
    return this.apiService.post('/wms-transaction-service/perpetualheader/v2/findPerpetualHeaderNew', obj);
  }
  getPerpetualCountListSpark(obj: any ) {
    return this.apiService.post('/mnr-spark-service/perpetualheader', obj);
  }
  Save(data: any) {
    return this.apiService.post(this.configService.prepetualCountSave, data);
  }
  get(data: any) {
    return this.apiService.get(this.configService.prepetualCountSave + '/v2/' + data.cycleCountNo + '?cycleCountTypeId=' + data.cycleCountTypeId + '&movementTypeId=' + data.movementTypeId + '&subMovementTypeId=' + data.subMovementTypeId + '&warehouseId=' + this.auth.warehouseId + '&companyCodeId='+ this.auth.companyId +'&plantId='+ this.auth.plantId +'&languageId='+ this.auth.languageId);
  }
  SaveHHT(data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualCountSaveHHT, data);
  }

  prepetualCountConfirm(cycleCountNo: any, data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualCountConfirm + '/' + cycleCountNo + '?cycleCountTypeId=' + data.cycleCountTypeId + '&movementTypeId=' + data.movementTypeId + '&subMovementTypeId=' + data.subMovementTypeId + '&warehouseId=' + this.auth.warehouseId + '&companyCode='+ this.auth.companyId +'&plantId='+ this.auth.plantId +'&languageId='+ this.auth.languageId, data);
  }

  updatePerpetualHeader(cycleCountNo: any, cycleCountTypeId: any, warehouseId: any,movementTypeId: any, subMovementTypeId: any,  data: any) {
    return this.apiService.patchWithBody('/wms-transaction-service/perpetualheader/v2/' + cycleCountNo + '?cycleCountTypeId=' + cycleCountTypeId + '&warehouseId=' + warehouseId  + '&movementTypeId=' + movementTypeId + '&subMovementTypeId=' + subMovementTypeId  + '&companyCodeId='+ this.auth.companyId +'&plantId='+ this.auth.plantId +'&languageId='+ this.auth.languageId, data);
  }

  //
  varienAnalysisConfirm(cycleCountNo: any, data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualLinePatch + '/v2/' + cycleCountNo, data);
  }


  
  pushToAMS(cycleCountNo: any, data) {
    return this.apiService.patchWithBody(`/wms-transaction-service/perpetualline/v2/confirm/${cycleCountNo}`, data);
  }

  findPerpetualLine(data: any) {
    return this.apiService.post('/wms-transaction-service/v2/findPerpetualLine', data);
  }
  findPerpetualSpark(data: any) {
    return this.apiService.post('/mnr-spark-service/perpetualline', data);
  }


  noStockPerpetual(obj: any ) {
    return this.apiService.post('/wms-transaction-service/perpetualline/v2/createPerpetualFromZeroStk', obj);
  }
}

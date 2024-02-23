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

  getPerpetualCountList(obj: any = { warehouseId: [this.auth.warehouseId] }) {
    return this.apiService.post(this.configService.prepetualCountList_url, obj);
  }
  Save(data: any) {
    return this.apiService.post(this.configService.prepetualCountSave, data);
  }
  get(data: any) {
    return this.apiService.get(this.configService.prepetualCountSave + '/' + data.cycleCountNo + '?cycleCountTypeId=' + data.cycleCountTypeId + '&movementTypeId=' + data.movementTypeId + '&subMovementTypeId=' + data.subMovementTypeId + '&warehouseId=' + data.warehouseId);
  }
  SaveHHT(data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualCountSaveHHT, data);
  }

  prepetualCountConfirm(cycleCountNo: any, data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualCountConfirm + '/' + cycleCountNo + '?cycleCountTypeId=' + data.cycleCountTypeId + '&movementTypeId=' + data.movementTypeId + '&subMovementTypeId=' + data.subMovementTypeId + '&warehouseId=' + data.warehouseId, data);
  }

  updatePerpetualHeader(cycleCountNo: any, cycleCountTypeId: any, warehouseId: any,movementTypeId: any, subMovementTypeId: any,  data: any) {
    return this.apiService.patchWithBody('/wms-transaction-service/perpetualheader/' + cycleCountNo + '?cycleCountTypeId=' + cycleCountTypeId + '&warehouseId=' + warehouseId  + '&movementTypeId=' + movementTypeId + '&subMovementTypeId=' + subMovementTypeId, data);
  }

  //
  varienAnalysisConfirm(cycleCountNo: any, data: any) {
    return this.apiService.patchWithBody(this.configService.prepetualLinePatch + '/' + cycleCountNo, data);
  }
}

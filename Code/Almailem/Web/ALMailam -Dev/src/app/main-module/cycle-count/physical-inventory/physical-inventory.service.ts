import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class PhysicalInventoryService {

  constructor(private apiService: ApiService, private configService: ConfigService, private auth: AuthService) { }

  runPeriodicHeader(warehouseId: any, stSecIds: any) {
    return this.apiService.post(this.configService.periodicHeaderRun + `?warehouseId=${warehouseId}&stSecIds=${stSecIds}`);
  }

  runPeriodicHeaderNew(warehouseId: any, sortBy: any, pageNo: any, pageSize: any) {
    return this.apiService.post(this.configService.periodicHeaderRun + `?warehouseId=${warehouseId}&sortBy=${sortBy}&pageNo=${pageNo}&pageSize=${pageSize}`);
  }
  createPeriodicHeader(data: any) {
    return this.apiService.post(this.configService.periodicHeaderCreate, data);
  }

  findPeriodicDataList(data: any) {
    return this.apiService.post(this.configService.findPeriodicHeaderList, data);
  }
  findPeriodicDataListNew(data: any) {
    return this.apiService.post('/wms-transaction-service/periodicheader/v2/findPeriodicHeader', data);      //20-06-23 streaming
  }
  findPeriodicDataListNew1(data: any) {
    return this.apiService.post('/wms-transaction-service/periodicheader/v2/findPeriodicHeaderNew', data);      //20-06-23 streaming
  }
  findPeriodicDataListNewSpark(data: any) {
    return this.apiService.post('/mnr-spark-service/periodicheader',data);      //20-06-23 streaming
  }

  confirmHHTUser(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicCountHHTUserPatch, data);
  }

  updatePeriodicHeader(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicHeaderUpdate + '/v2' + `/${data.cycleCountNo}?cycleCountTypeId=${data.cycleCountTypeId}&warehouseId=${data.warehouseId}` + '&companyCode='+ this.auth.companyId +'&plantId='+ this.auth.plantId +'&languageId='+ this.auth.languageId, data);
  }

  updatePeriodicLine(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicLineUpdate + '/v2'  + `/${data[0].cycleCountNo}`, data);
  }

  findPeriodicDLine(data: any) {
    return this.apiService.post('/wms-transaction-service/periodicline/v2/findPeriodicLine', data);
  }
  findPeriodicDLineSprk(data: any) {
    return this.apiService.post('/mnr-spark-service/periodicline', data);
  }

  pushToAMSPeriodic(cycleCountNo: any, data) {
    return this.apiService.patchWithBody(`/wms-transaction-service/periodicline/v2/confirm/${cycleCountNo}`, data);
  }

}

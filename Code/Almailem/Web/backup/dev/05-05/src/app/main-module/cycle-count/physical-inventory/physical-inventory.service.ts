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

  confirmHHTUser(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicCountHHTUserPatch, data);
  }

  updatePeriodicHeader(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicHeaderUpdate + `/${data.cycleCountNo}?cycleCountTypeId=${data.cycleCountTypeId}&warehouseId=${data.warehouseId}`, data);
  }

  updatePeriodicLine(data: any) {
    return this.apiService.patchWithBody(this.configService.periodicLineUpdate + `/${data[0].cycleCountNo}`, data);
  }

}

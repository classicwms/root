import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class SetupStorageService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveStorageSetupDetails(configUrl: any, data: any): Observable<any> {
    return this.apiService.post(configUrl == 'storageClass' ? this.configService.storageClass_url : configUrl == 'storageType' ? this.configService.storageType_url : configUrl == 'storageBinType' ? this.configService.storageBinType_url : configUrl == 'strategy' ? this.configService.strategy_url : '', data);
  }

  updateStorageSetupDetails(configUrl: any, data: any): Observable<any> {
    return this.apiService.patchWithBody(configUrl == 'storageClass' ? this.configService.storageClass_url + '/' + data.storageClassId : configUrl == 'storageType' ? this.configService.storageType_url + '/' + data.storageTypeId : configUrl == 'storageBinType' ? this.configService.storageBinType_url + '/' + data.storageBinTypeId : configUrl == 'strategy' ? this.configService.strategy_url + '/' + data.strategyNo : '', data);
  }

  getStorageSetupDetails(configUrl: any, param: any) {
    return this.apiService.get(configUrl == 'storageClass' ? this.configService.storageClass_url + '/' + param : configUrl == 'storageType' ? this.configService.storageType_url + '/' + param : configUrl == 'storageBinType' ? this.configService.storageBinType_url + '/' + param : configUrl == 'strategy' ? this.configService.strategy_url + '/' + param : '');
  }

  getStorageSetupList(configUrl: any) {
    return this.apiService.get(configUrl == 'storageClass' ? this.configService.storageClass_url : configUrl == 'storageType' ? this.configService.storageType_url : configUrl == 'strategy' ? this.configService.strategy_url : '');
  }

  getSetupMasters() {
    return this.apiService.get(this.configService.storageBinType_url);
  }
}

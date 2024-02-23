import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class SetupProductService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveProductSetupDetails(configUrl: any, data: any): Observable<any> {
    return this.apiService.post(configUrl == 'itemGroup' ? this.configService.itemGroup_url : configUrl == 'itemType' ? this.configService.itemType_url : configUrl == 'batchSerial' ? this.configService.batchSerial_url : configUrl == 'barCode' ? this.configService.barCode_url : configUrl == 'variant' ? this.configService.variant_url : '', data);
  }

  updateProductSetupDetails(configUrl: any, data: any): Observable<any> {
    return this.apiService.patchWithBody(configUrl == 'itemGroup' ? this.configService.itemGroup_url + '/' + data.itemGroupId : configUrl == 'itemType' ? this.configService.itemType_url + '/' + data.itemTypeId : configUrl == 'batchSerial' ? this.configService.batchSerial_url : configUrl == 'barCode' ? this.configService.barCode_url : configUrl == 'variant' ? this.configService.variant_url : '', data);
  }

  getProductSetupDetails(configUrl: any, param: any) {
    return this.apiService.get(configUrl == 'itemGroup' ? this.configService.itemGroup_url + '/' + param : configUrl == 'itemType' ? this.configService.itemType_url + '/' + param : configUrl == 'batchSerial' ? this.configService.batchSerial_url + '/' + param : configUrl == 'barCode' ? this.configService.barCode_url + '/' + param : configUrl == 'variant' ? this.configService.variant_url + '/' + param : '');
  }

  getSetupMasters() {
    return this.apiService.get(this.configService.itemGroup_url);
  }
}

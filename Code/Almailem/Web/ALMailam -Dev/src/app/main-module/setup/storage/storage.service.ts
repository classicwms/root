import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveStorageDetails(data: any): Observable<any> {
    return this.apiService.post(this.configService.storage_url, data);
  }

  getStorageDetails(storageId: any) {
    return this.apiService.get(this.configService.storage_url + '/' + storageId);
  }

  updateStorageDetails(data: any): Observable<any> {
    return this.apiService.patchWithBody(this.configService.storage_url + '/' + data.storageSectionId, data);
  }
}

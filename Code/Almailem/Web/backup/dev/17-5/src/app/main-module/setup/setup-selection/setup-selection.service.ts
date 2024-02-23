import { Injectable } from '@angular/core';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class SetupSelectionService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  getSetupMasters() {
    return this.apiService.get(this.configService.setupMaster_url);
  }
}
